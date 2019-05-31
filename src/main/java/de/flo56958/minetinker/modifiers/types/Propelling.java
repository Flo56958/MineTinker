package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.data.ModifierFailCause;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.events.ModifierFailEvent;
import de.flo56958.minetinker.Main;
import de.flo56958.minetinker.modifiers.Craftable;
import de.flo56958.minetinker.modifiers.Enchantable;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utilities.ChatWriter;
import de.flo56958.minetinker.utilities.ConfigurationManager;
import de.flo56958.minetinker.utilities.Modifiers_Config;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Propelling extends Modifier implements Craftable, Enchantable, Listener {

    private int durabilityLoss;
    private double speedPerLevel;

    private boolean sound;
    private boolean particles;

    private static Propelling instance;

    public static Propelling instance() {
        synchronized (Propelling.class) {
            if (instance == null) instance = new Propelling();
        }
        return instance;
    }

    private Propelling() {
        super(ModifierType.PROPELLING,
                new ArrayList<>(Arrays.asList(ToolType.ELYTRA, ToolType.TRIDENT)),
                Main.getPlugin());
        Bukkit.getPluginManager().registerEvents(this, Main.getPlugin());
    }

    @Override
    public List<Enchantment> getAppliedEnchantments() {
        List<Enchantment> enchantments = new ArrayList<>();
        enchantments.add(Enchantment.RIPTIDE);

        return enchantments;
    }

    @Override
    public void reload() {
        FileConfiguration config = getConfig();
        config.options().copyDefaults(true);

        String key = "Propelling";
        config.addDefault(key + ".allowed", true);
        config.addDefault(key + ".name", key);
        config.addDefault(key + ".name_modifier", "Enchanted Fireworkstar");
        config.addDefault(key + ".modifier_item", "FIREWORK_STAR"); //Needs to be a viable Material-Type
        config.addDefault(key + ".description", "Propel yourself through the air.");
        config.addDefault(key + ".description_modifier", "%WHITE%Modifier-Item for the Propelling-Modifier");
        config.addDefault(key + ".Color", "%GOLD%");
        config.addDefault(key + ".MaxLevel", 3);
        config.addDefault(key + ".EnchantCost", 10);
        config.addDefault(key + ".Elytra.DurabilityLoss", 10);
        config.addDefault(key + ".Elytra.SpeedPerLevel", 0.05);
        config.addDefault(key + ".Elytra.Sound", true);
        config.addDefault(key + ".Elytra.Particles", true);
        config.addDefault(key + ".Recipe.Enabled", false);

        ConfigurationManager.saveConfig(config);

        String name = config.getString(key + ".name");
        String modName = config.getString(key + ".name_modifier");
        String description = config.getString(key + ".description");
        String color = config.getString(key + ".Color");
        int maxLevel = config.getInt(key + ".MaxLevel");
        String modItem = config.getString(key + ".modifier_item");
        String modDescription = config.getString(key + ".description_modifier");

        if (name == null || modName == null || description == null || color == null || modItem == null || modDescription == null) return;

        init(name, "[" + modName + "] " + description, ChatWriter.getColor(color), maxLevel,
                modManager.createModifierItem(Material.getMaterial(modItem), ChatWriter.getColor(color) + modName,
                        ChatWriter.addColors(modDescription), this));

        durabilityLoss = config.getInt(key + ".Elytra.DurabilityLoss");
        speedPerLevel = config.getDouble(key + ".Elytra.SpeedPerLevel");

        sound = config.getBoolean(key + ".Elytra.Sound");
        particles = config.getBoolean(key + ".Elytra.Particles");
    }

    @Override
    public void enchantItem(Player p, ItemStack item) {
        if (!p.hasPermission("minetinker.modifiers.propelling.craft")) return;
        _createModifierItem(getConfig(), p, this, "Propelling");
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (modManager.hasMod(tool, modManager.getAdmin(ModifierType.INFINITY))) {
            pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
            return null;
        }

        if (Modifier.checkAndAdd(p, tool, this, "propelling", isCommand) == null) {
            return null;
        }

        ItemMeta meta = tool.getItemMeta();

        if (meta != null) {
            if (ToolType.TRIDENT.getMaterials().contains(tool.getType())) {
                meta.addEnchant(Enchantment.RIPTIDE, modManager.getModLevel(tool, this), true);
            } //Elytra does not get an enchantment

            if (Main.getPlugin().getConfig().getBoolean("HideEnchants")) {
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            } else {
                meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            tool.setItemMeta(meta);
        }

        return tool;
    }

    @Override
    public void removeMod(ItemStack tool) {
        ItemMeta meta = tool.getItemMeta();

        if (meta != null) {
            meta.removeEnchant(Enchantment.RIPTIDE);
            tool.setItemMeta(meta);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onElytraSneak(PlayerToggleSneakEvent e) {
        Player p = e.getPlayer();

        if (e.isSneaking()) return;
        if (!p.isGliding()) return;
        if (!p.hasPermission("minetinker.modifiers.propelling.use")) return;

        ItemStack elytra = p.getInventory().getChestplate();

        if (elytra == null) return;
        if (!(modManager.isArmorViable(elytra) && ToolType.ELYTRA.getMaterials().contains(elytra.getType()))) return;
        if (!modManager.hasMod(elytra, this)) return;

        int maxDamage = elytra.getType().getMaxDurability();
        ItemMeta meta = elytra.getItemMeta();

        if (meta instanceof Damageable) {
            Damageable dam = (Damageable) meta;

            if (maxDamage <= dam.getDamage() + durabilityLoss + 1) {
                p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.5F, 0.5F);
                return;
            }

            dam.setDamage(dam.getDamage() + durabilityLoss);
            elytra.setItemMeta(meta);
        }

        int level = modManager.getModLevel(elytra, this);
        Location loc = p.getLocation();
        Vector dir = loc.getDirection().normalize();

        p.setVelocity(p.getVelocity().add(dir.multiply(1 + speedPerLevel * level)));

        if (sound && loc.getWorld() != null) loc.getWorld().spawnParticle(Particle.CLOUD, loc, 30, 0.5F, 0.5F, 0.5F, 0.0F);
        if (particles) p.playSound(loc, Sound.ENTITY_ENDER_DRAGON_FLAP, 0.5F, 0.5F);
    }

    private static FileConfiguration getConfig() {
        return ConfigurationManager.getConfig(Modifiers_Config.Propelling);
    }

    @Override
    public boolean isAllowed() {
        return getConfig().getBoolean("Propelling.allowed");
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Propelling", "Modifier_Propelling");
    }
}

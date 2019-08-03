package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ModifierFailCause;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.ModifierFailEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Enchantable;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Propelling extends Modifier implements Enchantable, Listener {

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
        super("Propelling", "Propelling.yml",
                new ArrayList<>(Arrays.asList(ToolType.ELYTRA, ToolType.TRIDENT)),
                Main.getPlugin());
        Bukkit.getPluginManager().registerEvents(this, Main.getPlugin());
    }

    @Override
    public List<Enchantment> getAppliedEnchantments() {
        return Collections.singletonList(Enchantment.RIPTIDE);
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
        ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

        init(config.getString(key + ".name"),
                "[" + config.getString(key + ".name_modifier") + "] \u200B" + config.getString(key + ".description"),
                ChatWriter.getColor(config.getString(key + ".Color")),
                config.getInt(key + ".MaxLevel"),
                modManager.createModifierItem(Material.getMaterial(config.getString(key + ".modifier_item")), ChatWriter.getColor(config.getString(key + ".Color")) + config.getString(key + ".name_modifier"), ChatWriter.addColors(config.getString(key + ".description_modifier")), this));

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
    public boolean applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (modManager.hasMod(tool, Infinity.instance())) {
            pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
            return false;
        }

        if (!Modifier.checkAndAdd(p, tool, this, "propelling", isCommand)) return false;

        ItemMeta meta = tool.getItemMeta();

        if (meta != null) {
            if (ToolType.TRIDENT.contains(tool.getType())) {
                meta.addEnchant(Enchantment.RIPTIDE, modManager.getModLevel(tool, this), true);
            } //Elytra does not get an enchantment

            if (Main.getPlugin().getConfig().getBoolean("HideEnchants")) {
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            } else {
                meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            tool.setItemMeta(meta);
        }

        return true;
    }

    @EventHandler(ignoreCancelled = true)
    public void onElytraSneak(PlayerToggleSneakEvent e) {
        Player p = e.getPlayer();

        if (e.isSneaking()) return;
        if (!p.isGliding()) return;
        if (!p.hasPermission("minetinker.modifiers.propelling.use")) return;

        ItemStack elytra = p.getInventory().getChestplate();

        if (!(modManager.isArmorViable(elytra) && ToolType.ELYTRA.contains(elytra.getType()))) return;
        if (!modManager.hasMod(elytra, this)) return;

        int maxDamage = elytra.getType().getMaxDurability();
        ItemMeta meta = elytra.getItemMeta();

        if (meta instanceof Damageable && !meta.isUnbreakable()) {
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

    @Override
    public boolean isAllowed() {
        return getConfig().getBoolean("Propelling.allowed");
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Propelling", "Modifier_Propelling");
    }
}

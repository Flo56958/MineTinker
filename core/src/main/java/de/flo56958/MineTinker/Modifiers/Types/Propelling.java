package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ModifierFailCause;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.ModifierFailEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Enchantable;
import de.flo56958.MineTinker.Modifiers.Modifier;
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
        List<Enchantment> enchantments = new ArrayList<>();
        enchantments.add(Enchantment.RIPTIDE);

        return enchantments;
    }

    @Override
    public void reload() {
        FileConfiguration config = getConfig();
        config.options().copyDefaults(true);

        config.addDefault("Allowed", true);
        config.addDefault("Name", "Propelling");
        config.addDefault("ModifierItemName", "Enchanted Fireworkstar");
        config.addDefault("Description", "Propel yourself through the air.");
        config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Propelling-Modifier");
        config.addDefault("Color", "%GOLD%");
        config.addDefault("MaxLevel", 3);
        config.addDefault("EnchantCost", 10);
        config.addDefault("Elytra.DurabilityLoss", 10);
        config.addDefault("Elytra.SpeedPerLevel", 0.05);
        config.addDefault("Elytra.Sound", true);
        config.addDefault("Elytra.Particles", true);
        config.addDefault("Recipe.Enabled", false);
        config.addDefault("OverrideLanguagesystem", false);

        ConfigurationManager.saveConfig(config);
        ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

        init(Material.FIREWORK_STAR, true);

        durabilityLoss = config.getInt("Elytra.DurabilityLoss", 10);
        speedPerLevel = config.getDouble("Elytra.SpeedPerLevel", 0.05);

        sound = config.getBoolean("Elytra.Sound", true);
        particles = config.getBoolean("Elytra.Particles", true);
    }

    @Override
    public void enchantItem(Player p, ItemStack item) {
        if (!p.hasPermission("minetinker.modifiers.propelling.craft")) return;
        _createModifierItem(getConfig(), p, this);
    }

    @Override
    public boolean applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (modManager.hasMod(tool, Infinity.instance())) {
            pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
            return false;
        }

        if (Modifier.checkAndAdd(p, tool, this, "propelling", isCommand)) return false;

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
}

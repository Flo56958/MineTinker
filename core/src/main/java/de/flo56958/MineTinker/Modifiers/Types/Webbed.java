package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.MTEntityDamageByEntityEvent;
import de.flo56958.MineTinker.Events.MTProjectileHitEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.*;

public class Webbed extends Modifier implements Listener {

    private int duration;
    private double durationMultiplier;
    private int effectAmplifier;

    private static Webbed instance;

    public static Webbed instance() {
        synchronized (Webbed.class) {
            if (instance == null) {
                instance = new Webbed();
            }
        }

        return instance;
    }

    @Override
    public String getKey() {
        return "Webbed";
    }

    @Override
    public List<ToolType> getAllowedTools() {
        return Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.CROSSBOW, ToolType.SWORD, ToolType.TRIDENT, ToolType.FISHINGROD,
                ToolType.HELMET, ToolType.CHESTPLATE, ToolType.LEGGINGS, ToolType.BOOTS, ToolType.ELYTRA);
    }

    private Webbed() {
        super(Main.getPlugin());

        Bukkit.getPluginManager().registerEvents(this, Main.getPlugin());
    }

    @Override
    public void reload() {
        FileConfiguration config = getConfig();
    	config.options().copyDefaults(true);

    	config.addDefault("Allowed", true);
    	config.addDefault("Name", "Webbed");
    	config.addDefault("ModifierItemName", "Compressed Cobweb");
        config.addDefault("Description", "Slowes down enemies!");
        config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Webbed-Modifier");
        config.addDefault("Color", "%WHITE%");
        config.addDefault("MaxLevel", 3);
    	config.addDefault("Duration", 60); //ticks (20 ticks ~ 1 sec)
    	config.addDefault("DurationMultiplier", 1.2);//Duration * (Multiplier^Level)
    	config.addDefault("EffectAmplifier", 2); //per Level (Level 1 = 0, Level 2 = 2, Level 3 = 4, ...)
        config.addDefault("OverrideLanguagesystem", false);

        config.addDefault("EnchantCost", 10);
        config.addDefault("Enchantable", false);

        config.addDefault("Recipe.Enabled", true);
    	config.addDefault("Recipe.Top", "WWW");
    	config.addDefault("Recipe.Middle", "WWW");
    	config.addDefault("Recipe.Bottom", "WWW");

        Map<String, String> recipeMaterials = new HashMap<>();
        recipeMaterials.put("W", "COBWEB");

        config.addDefault("Recipe.Materials", recipeMaterials);

    	ConfigurationManager.saveConfig(config);
        ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());
        
        init(Material.COBWEB, true);
        
        this.duration = config.getInt("Duration", 60);
        this.durationMultiplier = config.getDouble("DurationMultiplier", 1.2);
        this.effectAmplifier = config.getInt("EffectAmplifier", 2);
    }

    @EventHandler(ignoreCancelled = true)
    public void effect(MTEntityDamageByEntityEvent event) {
        if (!this.isAllowed()) {
            return;
        }

        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }

        effect(event.getPlayer(), event.getTool(), event.getEntity());
    }

    @EventHandler
    public void effect(MTProjectileHitEvent event) {
        if (!this.isAllowed()) {
            // Maybe change this to cancellable and ignoreCancelled?
            return;
        }

        if (!(event.getEvent().getHitEntity() instanceof LivingEntity)) {
            return;
        }

        if (!ToolType.FISHINGROD.contains(event.getTool().getType())) {
            return;
        }

        effect(event.getPlayer(), event.getTool(), event.getEvent().getHitEntity());
    }

    private void effect(Player p, ItemStack tool, Entity ent) {
        if (!p.hasPermission("minetinker.modifiers.webbed.use")) {
            return;
        }

        if (ent.isDead()) {
            return;
        }

        if (!modManager.hasMod(tool, this)) {
            return;
        }

        int level = modManager.getModLevel(tool, this);

        int duration = (int) (this.duration * Math.pow(this.durationMultiplier, (level - 1)));
        int amplifier = this.effectAmplifier * (level - 1) / 2;

        ((LivingEntity) ent).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration, amplifier, false, false));

        ChatWriter.log(false, p.getDisplayName() + " triggered Webbed on " + ItemGenerator.getDisplayName(tool) + ChatColor.GRAY + " (" + tool.getType().toString() + ")!");
    }
}

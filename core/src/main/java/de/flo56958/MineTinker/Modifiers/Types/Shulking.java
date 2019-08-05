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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Shulking extends Modifier implements Listener {

    private int duration;
    private int effectAmplifier;

    private static Shulking instance;

    public static Shulking instance() {
        synchronized (Shulking.class) {
            if (instance == null) {
                instance = new Shulking();
            }
        }

        return instance;
    }

    @Override
    public String getKey() {
        return "Shulking";
    }

    @Override
    public List<ToolType> getAllowedTools() {
        return Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.CROSSBOW, ToolType.SWORD, ToolType.TRIDENT, ToolType.FISHINGROD,
                ToolType.HELMET, ToolType.CHESTPLATE, ToolType.LEGGINGS, ToolType.BOOTS, ToolType.ELYTRA);
    }

    private Shulking() {
        super(Main.getPlugin());

        Bukkit.getPluginManager().registerEvents(this, Main.getPlugin());
    }

    @Override
    public void reload() {
    	FileConfiguration config = getConfig();
    	config.options().copyDefaults(true);

        String key = getKey();

        config.addDefault(key + ".allowed", true);
    	config.addDefault(key + ".name", key); //wingardium leviosa
    	config.addDefault(key + ".name_modifier", "Enhanced Shulkershell");
        config.addDefault(key + ".modifier_item", "SHULKER_SHELL"); //Needs to be a viable Material-Type
        config.addDefault(key + ".description", "Makes enemies levitate!");
        config.addDefault(key + ".description_modifier", "%WHITE%Modifier-Item for the " + key + "-Modifier");
        config.addDefault(key + ".Color", "%LIGHT_PURPLE%");
        config.addDefault(key + ".EnchantCost", 10);
        config.addDefault(key + ".MaxLevel", 10);
    	config.addDefault(key + ".Duration", 20); //ticks (20 ticks ~ 1 sec)
    	config.addDefault(key + ".EffectAmplifier", 2); //per Level (Level 1 = 0, Level 2 = 2, Level 3 = 4, ...)

        config.addDefault(key + ".Recipe.Enabled", true);
    	config.addDefault(key + ".Recipe.Top", "S");
    	config.addDefault(key + ".Recipe.Middle", "C");
    	config.addDefault(key + ".Recipe.Bottom", "S");

        Map<String, String> recipeMaterials = new HashMap<>();
        recipeMaterials.put("S", "SHULKER_SHELL");
        recipeMaterials.put("C", "CHORUS_FRUIT");

        config.addDefault(key + ".Recipe.Materials", recipeMaterials);
    	
    	ConfigurationManager.saveConfig(config);
        ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());
        
        init("[" + config.getString(key + ".name_modifier") + "] \u200B" + config.getString(key + ".description"),
                ChatWriter.getColor(config.getString(key + ".Color")),
                config.getInt(key + ".MaxLevel"),
                modManager.createModifierItem(Material.getMaterial(config.getString(key + ".modifier_item")), ChatWriter.getColor(config.getString(key + ".Color")) + config.getString(key + ".name_modifier"), ChatWriter.addColors(config.getString(key + ".description_modifier")), this));
    
        this.duration = config.getInt("Shulking.Duration");
        this.effectAmplifier = config.getInt("Shulking.EffectAmplifier");
    }

    @Override
    public boolean applyMod(Player p, ItemStack tool, boolean isCommand) {
        return Modifier.checkAndAdd(p, tool, this, "shulking", isCommand);
    }

    @EventHandler(ignoreCancelled = true)
    public void effect(MTEntityDamageByEntityEvent event) {
        if (!this.isAllowed()) {
            return;
        }

        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }

        Player p = event.getPlayer();
        ItemStack tool = event.getTool();

        effect(p, tool, event.getEntity());
    }

    @EventHandler(ignoreCancelled = true)
    public void effect(MTProjectileHitEvent event) {
        if (!this.isAllowed()) {
            return;
        }

        if (!(event.getEvent().getHitEntity() instanceof LivingEntity)) {
            return;
        }

        Player p = event.getPlayer();
        ItemStack tool = event.getTool();

        if (!ToolType.FISHINGROD.contains(tool.getType())) {
            return;
        }

        effect(p, tool, event.getEvent().getHitEntity());
    }

    private void effect(Player p, ItemStack tool, Entity ent) {
        if (!p.hasPermission("minetinker.modifiers.shulking.use")) {
            return;
        }

        if (!modManager.hasMod(tool, this)) {
            return;
        }

        int level = modManager.getModLevel(tool, this);
        int amplifier = this.effectAmplifier * (level - 1);

        ((LivingEntity) ent).addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, this.duration, amplifier, false, false));

        ChatWriter.log(false, p.getDisplayName() + " triggered Shulking on " + ItemGenerator.getDisplayName(tool) + ChatColor.GRAY + " (" + tool.getType().toString() + ")!");

    }
}

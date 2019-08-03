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
import org.bukkit.enchantments.Enchantment;
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

public class Webbed extends Modifier implements Listener {

    private int duration;
    private double durationMultiplier;
    private int effectAmplifier;

    private static Webbed instance;

    public static Webbed instance() {
        synchronized (Webbed.class) {
            if (instance == null) instance = new Webbed();
        }
        return instance;
    }

    private Webbed() {
        super("Webbed", "Webbed.yml",
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.CROSSBOW, ToolType.SWORD, ToolType.TRIDENT, ToolType.FISHINGROD,
                                                ToolType.HELMET, ToolType.CHESTPLATE, ToolType.LEGGINGS, ToolType.BOOTS, ToolType.ELYTRA)),
                Main.getPlugin());
        Bukkit.getPluginManager().registerEvents(this, Main.getPlugin());
    }

    @Override
    public List<Enchantment> getAppliedEnchantments() {
        return new ArrayList<>();
    }

    @Override
    public void reload() {
        FileConfiguration config = getConfig();
    	config.options().copyDefaults(true);
    	
    	String key = "Webbed";
    	config.addDefault(key + ".allowed", true);
    	config.addDefault(key + ".name", key);
    	config.addDefault(key + ".name_modifier", "Compressed Cobweb");
        config.addDefault(key + ".modifier_item", "COBWEB"); //Needs to be a viable Material-Type
        config.addDefault(key + ".description", "Slowes down enemies!");
        config.addDefault(key + ".description_modifier", "%WHITE%Modifier-Item for the Webbed-Modifier");
        config.addDefault(key + ".Color", "%WHITE%");
        config.addDefault(key + ".MaxLevel", 3);
    	config.addDefault(key + ".Duration", 60); //ticks (20 ticks ~ 1 sec)
    	config.addDefault(key + ".Sound", true);
    	config.addDefault(key + ".DurationMultiplier", 1.2);//Duration * (Multiplier^Level)
    	config.addDefault(key + ".EffectAmplifier", 2); //per Level (Level 1 = 0, Level 2 = 2, Level 3 = 4, ...)

        config.addDefault(key + ".Recipe.Enabled", true);
    	config.addDefault(key + ".Recipe.Top", "WWW");
    	config.addDefault(key + ".Recipe.Middle", "WWW");
    	config.addDefault(key + ".Recipe.Bottom", "WWW");

        Map<String, String> recipeMaterials = new HashMap<>();
        recipeMaterials.put("W", "COBWEB");

        config.addDefault(key + ".Recipe.Materials", recipeMaterials);

    	ConfigurationManager.saveConfig(config);
        ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());
        
        init(getConfig().getString(key + ".name"),
                "[" + getConfig().getString(key + ".name_modifier") + "] \u200B" + getConfig().getString(key + ".description"),
                ChatWriter.getColor(config.getString(key + ".Color")),
                getConfig().getInt(key + ".MaxLevel"),
                modManager.createModifierItem(Material.getMaterial(config.getString(key + ".modifier_item")), ChatWriter.getColor(config.getString(key + ".Color")) + config.getString(key + ".name_modifier"), ChatWriter.addColors(config.getString(key + ".description_modifier")), this));
        
        this.duration = config.getInt(key + ".Duration");
        this.durationMultiplier = config.getDouble(key + ".DurationMultiplier");
        this.effectAmplifier = config.getInt(key + ".EffectAmplifier");
    }

    @Override
    public boolean applyMod(Player p, ItemStack tool, boolean isCommand) {
        return Modifier.checkAndAdd(p, tool, this, "webbed", isCommand);
    }

    @EventHandler
    public void effect(MTEntityDamageByEntityEvent event) {
        if (event.isCancelled() || !this.isAllowed()) return;
        if (!(event.getEntity() instanceof LivingEntity)) return;

        effect(event.getPlayer(), event.getTool(), event.getEntity());
    }

    @EventHandler
    public void effect(MTProjectileHitEvent event) {
        if (!this.isAllowed()) return;
        if (!(event.getEvent().getHitEntity() instanceof LivingEntity)) return;
        if (!ToolType.FISHINGROD.contains(event.getTool().getType())) return;

        effect(event.getPlayer(), event.getTool(), event.getEvent().getHitEntity());
    }

    private void effect(Player p, ItemStack tool, Entity ent) {
        if (!p.hasPermission("minetinker.modifiers.webbed.use")) return;
        if (ent.isDead()) return;
        if (!modManager.hasMod(tool, this)) return;

        int level = modManager.getModLevel(tool, this);

        int duration = (int) (this.duration * Math.pow(this.durationMultiplier, (level - 1)));
        int amplifier = this.effectAmplifier * (level - 1) / 2;

        ((LivingEntity) ent).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration, amplifier, false, false));

        ChatWriter.log(false, p.getDisplayName() + " triggered Webbed on " + ItemGenerator.getDisplayName(tool) + ChatColor.GRAY + " (" + tool.getType().toString() + ")!");
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Webbed", "Modifier_Webbed");
    }

    @Override
    public boolean isAllowed() {
    	return getConfig().getBoolean("Webbed.allowed");
    }
}

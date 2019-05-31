package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.events.MTEntityDamageByEntityEvent;
import de.flo56958.minetinker.events.MTProjectileHitEvent;
import de.flo56958.minetinker.Main;
import de.flo56958.minetinker.modifiers.Craftable;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utilities.ChatWriter;
import de.flo56958.minetinker.utilities.ConfigurationManager;
import de.flo56958.minetinker.utilities.ItemGenerator;
import de.flo56958.minetinker.utilities.Modifiers_Config;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Webbed extends Modifier implements Craftable, Listener {

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
        super(ModifierType.WEBBED,
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.SWORD, ToolType.TRIDENT, ToolType.FISHINGROD,
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
    	config.addDefault(key + ".Recipe.Materials.W", "COBWEB");
    	
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

        this.duration = config.getInt(key + ".Duration");
        this.durationMultiplier = config.getDouble(key + ".DurationMultiplier");
        this.effectAmplifier = config.getInt(key + ".EffectAmplifier");
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        return Modifier.checkAndAdd(p, tool, this, "webbed", isCommand);
    }

    @Override
    public void removeMod(ItemStack tool) { }

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
        if (!ToolType.FISHINGROD.getMaterials().contains(event.getTool().getType())) return;

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
    
    private static FileConfiguration getConfig() {
    	return ConfigurationManager.getConfig(Modifiers_Config.Webbed);
    }

    @Override
    public boolean isAllowed() {
    	return getConfig().getBoolean("Webbed.allowed");
    }
}
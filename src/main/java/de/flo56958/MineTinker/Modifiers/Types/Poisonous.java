package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.MTEntityDamageByEntityEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Craftable;
import de.flo56958.MineTinker.Modifiers.Enchantable;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import de.flo56958.MineTinker.Utilities.Modifiers_Config;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;

public class Poisonous extends Modifier implements Enchantable, Craftable, Listener {
	
    private int duration;
    private double durationMultiplier;
    private int effectAmplifier;

    public Poisonous() {
        super(ModifierType.POISONOUS,
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.SWORD,
                                                ToolType.HELMET, ToolType.CHESTPLATE, ToolType.LEGGINGS, ToolType.BOOTS, ToolType.ELYTRA)),
                Main.getPlugin());
        Bukkit.getPluginManager().registerEvents(this, Main.getPlugin());
    }

    @Override
    public void reload() {
    	FileConfiguration config = getConfig();
    	config.options().copyDefaults(true);
    	
    	String key = "Poisonous";
    	config.addDefault(key + ".allowed", true);
    	config.addDefault(key + ".name", key);
    	config.addDefault(key + ".name_modifier", "Enhanced Rotten Flesh");
    	config.addDefault(key + ".description", "Poisons enemies!");
        config.addDefault(key + ".description_modifier", "%WHITE%Modifier-Item for the Poisonous-Modifier");
        config.addDefault(key + ".Color", "%DARK_GREEN%");
        config.addDefault(key + ".MaxLevel", 5);
    	config.addDefault(key + ".EnchantCost", 10);
    	config.addDefault(key + ".Duration", 120); //ticks INTEGER (20 ticks ~ 1 sec)
    	config.addDefault(key + ".DurationMultiplier", 1.1); //Duration * (Multiplier^Level) DOUBLE
    	config.addDefault(key + ".EffectAmplifier", 2); //per Level (Level 1 = 0, Level 2 = 2, Level 3 = 4, ...) INTEGER
    	config.addDefault(key + ".Recipe.Enabled", false);
    	
    	ConfigurationManager.saveConfig(config);
        
        init(config.getString(key + ".name"),
                "[" + config.getString(key + ".name_modifier") + "] " + config.getString(key + ".description"),
                ChatWriter.getColor(config.getString(key + ".Color")),
                config.getInt(key + ".MaxLevel"),
                modManager.createModifierItem(Material.ROTTEN_FLESH, ChatWriter.getColor(config.getString(key + ".Color")) + config.getString(key + ".name_modifier"), ChatWriter.addColors(config.getString(key + ".description_modifier")), this));
        
        this.duration = config.getInt(key + ".Duration");
        this.durationMultiplier = config.getDouble(key + ".DurationMultiplier");
        this.effectAmplifier = config.getInt(key + ".EffectAmplifier");
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        return checkAndAdd(p, tool, this, "poisonous", isCommand);
    }

    @Override
    public void removeMod(ItemStack tool) { }

    @EventHandler
    public void effect(MTEntityDamageByEntityEvent event) {
        if (event.isCancelled() || !this.isAllowed()) { return; }
        if (event.getEvent().getEntity() instanceof LivingEntity) {
            effect(event.getPlayer(), event.getTool(), (LivingEntity) event.getEvent().getEntity());
        }
    }

    private void effect(Player p, ItemStack tool, LivingEntity e) {
        if (!p.hasPermission("minetinker.modifiers.poisonous.use")) { return; }
        if (!modManager.hasMod(tool, this)) { return; }

        int level = modManager.getModLevel(tool, this);

        int duration = (int) (this.duration * Math.pow(this.durationMultiplier, (level - 1)));
        int amplifier = this.effectAmplifier * (level - 1);
        e.addPotionEffect(new PotionEffect(PotionEffectType.POISON, duration, amplifier, false, false));
        ChatWriter.log(false, p.getDisplayName() + " triggered Poisonous on " + ItemGenerator.getDisplayName(tool) + ChatColor.GRAY + " (" + tool.getType().toString() + ")!");
    }

    @Override
    public void enchantItem(Player p, ItemStack item) {
        if (!p.hasPermission("minetinker.modifiers.poisonous.craft")) { return; }
        _createModifierItem(getConfig(), p, this, "Poisonous");
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Poisonous", "Modifier_Poisonous");
    }
    
    private static FileConfiguration getConfig() {
    	return ConfigurationManager.getConfig(Modifiers_Config.Poisonous);
    }

    @Override
    public boolean isAllowed() {
    	return getConfig().getBoolean("Poisonous.allowed");
    }
}

package de.flo56958.MineTinker.Modifiers.Types;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Modifiers.Craftable;
import de.flo56958.MineTinker.Modifiers.Enchantable;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import de.flo56958.MineTinker.Utilities.modifiers_Config;

public class Poisonous extends Modifier implements Enchantable, Craftable {
	
    private int duration;
    private double durationMultiplier;
    private int effectAmplifier;

    public Poisonous() {
        super(ModifierType.POISONOUS,
                ChatColor.DARK_GREEN,
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.SWORD,
                                                ToolType.HELMET, ToolType.CHESTPLATE, ToolType.LEGGINGS, ToolType.BOOTS, ToolType.ELYTRA)),
                Main.getPlugin());
    }
    
    public void reload() {
    	FileConfiguration config = getConfig();
    	config.options().copyDefaults(true);
    	
    	String key = "Poisonous";
    	config.addDefault(key + ".allowed", true);
    	config.addDefault(key + ".name", key);
    	config.addDefault(key + ".name_modifier", "Enhanced Rotten Flesh");
    	config.addDefault(key + ".description", "Poisons enemies!");
    	config.addDefault(key + ".MaxLevel", 5);
    	config.addDefault(key + ".EnchantCost", 10);
    	config.addDefault(key + ".Duration", 120); //#ticks INTEGER (20 ticks ~ 1 sec)
    	config.addDefault(key + ".DurationMultiplier", 1.1); //#Duration * (Multiplier^Level) DOUBLE
    	config.addDefault(key + ".EffectAmplifier", 2); //#per Level (Level 1 = 0, Level 2 = 2, Level 3 = 4, ...) INTEGER
    	config.addDefault(key + ".Recipe.Enabled", false);
        
        init(config.getString("Poisonous.name"),
                "[" + config.getString("Poisonous.name_modifier") + "] " + config.getString("Poisonous.description"),
                config.getInt("Poisonous.MaxLevel"),
                ItemGenerator.itemEnchanter(Material.ROTTEN_FLESH, ChatColor.DARK_GREEN + config.getString("Poisonous.name_modifier"), 1, Enchantment.DURABILITY, 1));
        
        this.duration = config.getInt("Poisonous.Duration");
        this.durationMultiplier = config.getDouble("Poisonous.DurationMultiplier");
        this.effectAmplifier = config.getInt("Poisonous.EffectAmplifier");
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        return checkAndAdd(p, tool, this, "poisonous", isCommand);
    }

    public void effect(Player p, ItemStack tool, Entity e) {
        if (!p.hasPermission("minetinker.modifiers.poisonous.use")) { return; }
        if (!modManager.hasMod(tool, this)) { return; }
        if (!(e instanceof LivingEntity)) { return; }

        int level = modManager.getModLevel(tool, this);
        LivingEntity ent = (LivingEntity) e;
        int duration = (int) (this.duration * Math.pow(this.durationMultiplier, (level - 1)));
        int amplifier = this.effectAmplifier * (level - 1);
        ent.addPotionEffect(new PotionEffect(PotionEffectType.POISON, duration, amplifier, false, false));
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
    	return Main.getConfigurations().getConfig(modifiers_Config.Poisonous);
    }
    
    public boolean isAllowed() {
    	return getConfig().isBoolean("Poisonous.allowed");
    }
}

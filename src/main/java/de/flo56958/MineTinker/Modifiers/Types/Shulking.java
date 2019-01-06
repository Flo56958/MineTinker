package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Craftable;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import de.flo56958.MineTinker.Utilities.Modifiers_Config;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;

public class Shulking extends Modifier implements Craftable {

    private int duration;
    private int effectAmplifier;

    public Shulking() {
        super(ModifierType.SHULKING,
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.SWORD,
                                                ToolType.HELMET, ToolType.CHESTPLATE, ToolType.LEGGINGS, ToolType.BOOTS, ToolType.ELYTRA)),
                Main.getPlugin());
    }
    
    public void reload() {
    	FileConfiguration config = getConfig();
    	config.options().copyDefaults(true);
    	
    	String key = "Shulking";
    	config.addDefault(key + ".allowed", true);
    	config.addDefault(key + ".name", key); //wingardium leviosa
    	config.addDefault(key + ".name_modifier", "Enhanced Shulkershell");
    	config.addDefault(key + ".description", "Makes enemies levitate!");
        config.addDefault(key + ".description_modifier", "%WHITE%Modifier-Item for the Shulking-Modifier");
        config.addDefault(key + ".Color", "%LIGHT_PURPLE%");
        config.addDefault(key + ".MaxLevel", 10);
    	config.addDefault(key + ".Duration", 20); //ticks (20 ticks ~ 1 sec)
    	config.addDefault(key + ".EffectAmplifier", 2); //per Level (Level 1 = 0, Level 2 = 2, Level 3 = 4, ...)
    	config.addDefault(key + ".Recipe.Enabled", true);
    	config.addDefault(key + ".Recipe.Top", "S");
    	config.addDefault(key + ".Recipe.Middle", "C");
    	config.addDefault(key + ".Recipe.Bottom", "S");
    	config.addDefault(key + ".Recipe.Materials.S", "SHULKER_SHELL");
    	config.addDefault(key + ".Recipe.Materials.C", "CHORUS_FRUIT");
    	
    	ConfigurationManager.saveConfig(config);
        
        init(config.getString("Shulking.name"),
                "[" + config.getString("Shulking.name_modifier") + "] " + config.getString("Shulking.description"),
                ChatWriter.getColor(config.getString(key + ".Color")),
                config.getInt("Shulking.MaxLevel"),
                modManager.createModifierItem(Material.SHULKER_SHELL, ChatWriter.getColor(config.getString(key + ".Color")) + config.getString(key + ".name_modifier"), ChatWriter.addColors(config.getString(key + ".description_modifier")), this));
    
        this.duration = config.getInt("Shulking.Duration");
        this.effectAmplifier = config.getInt("Shulking.EffectAmplifier");
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        return Modifier.checkAndAdd(p, tool, this, "shulking", isCommand);
    }

    @Override
    public void removeMod(ItemStack tool) { }

    public void effect(Player p, ItemStack tool, Entity e) {
        if (!p.hasPermission("minetinker.modifiers.shulking.use")) { return; }
        if (!modManager.hasMod(tool, this)) { return; }
        if (!(e instanceof LivingEntity)) { return; }

        int level = modManager.getModLevel(tool, this);
        int amplifier = this.effectAmplifier * (level - 1);

        LivingEntity ent = (LivingEntity) e;
        ent.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, this.duration, amplifier, false, false));
        ChatWriter.log(false, p.getDisplayName() + " triggered Shulking on " + ItemGenerator.getDisplayName(tool) + ChatColor.GRAY + " (" + tool.getType().toString() + ")!");
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Shulking", "Modifier_Shulking");
    }
    
    private static FileConfiguration getConfig() {
    	return ConfigurationManager.getConfig(Modifiers_Config.Shulking);
    }
    
    public boolean isAllowed() {
    	return getConfig().getBoolean("Shulking.allowed");
    }
}

package de.flo56958.MineTinker.Modifiers.Types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Modifiers.Craftable;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import de.flo56958.MineTinker.Utilities.Modifiers_Config;

public class Experienced extends Modifier implements Craftable {

    private int percentagePerLevel;
    private int amount;

    public Experienced() {
        super(ModifierType.EXPERIENCED,
                ChatColor.GREEN,
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.HOE, ToolType.PICKAXE, ToolType.SHOVEL, ToolType.SWORD,
                                                ToolType.HELMET, ToolType.CHESTPLATE, ToolType.LEGGINGS, ToolType.BOOTS, ToolType.ELYTRA)),
                Main.getPlugin());
    }
    
    public void reload() {
    	FileConfiguration config = getConfig();
    	config.options().copyDefaults(true);
    	
    	String key = "Experienced";
    	config.addDefault(key + ".allowed", true);
    	config.addDefault(key + ".name", key);
    	config.addDefault(key + ".description", "Tool has the chance to drop XP while using it!");
    	config.addDefault(key + ".MaxLevel", 10);
    	config.addDefault(key + ".PercentagePerLevel", "2"); //#= 20% at Level 10 -> every 5th hit / block will trigger Experienced
    	config.addDefault(key + ".Amount", 1); //#How much XP should be dropped when triggered
    	config.addDefault(key + ".Recipe.Enabled", false);
    	
        init(config.getString("Experienced.name"),
                "[Bottle o' Experience] " + config.getString("Experienced.description"),
                config.getInt("Experienced.MaxLevel"),
                new ItemStack(Material.EXPERIENCE_BOTTLE, 1));
        
        this.percentagePerLevel = config.getInt("Experienced.PercentagePerLevel");
        this.amount = config.getInt("Experienced.Amount");
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        return Modifier.checkAndAdd(p, tool, this, "experienced", isCommand);
    }

    public void effect(Player p, ItemStack tool) {
        if (!p.hasPermission("minetinker.modifiers.experienced.use")) { return; }
        if (!modManager.hasMod(tool, this)) { return; }

        int level = modManager.getModLevel(tool, this);

        Random rand = new Random();
        int n = rand.nextInt(100);
        if (n <= this.percentagePerLevel * level) {
            ExperienceOrb orb = p.getWorld().spawn(p.getLocation(), ExperienceOrb.class);
            orb.setExperience(this.amount);
            ChatWriter.log(false, p.getDisplayName() + " triggered Experienced on " + ItemGenerator.getDisplayName(tool) + ChatColor.WHITE + " (" + tool.getType().toString() + ")!");
        }
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Experienced", "Modifier_Experienced");
    }
    
    private static FileConfiguration getConfig() {
    	return ConfigurationManager.getConfig(Modifiers_Config.Experienced);
    }
    
    public boolean isAllowed() {
    	return getConfig().isBoolean("Experienced.allowed");
    }
}

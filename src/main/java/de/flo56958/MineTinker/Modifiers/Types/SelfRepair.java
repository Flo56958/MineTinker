package de.flo56958.MineTinker.Modifiers.Types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Modifiers.Craftable;
import de.flo56958.MineTinker.Modifiers.Enchantable;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import de.flo56958.MineTinker.Utilities.Modifiers_Config;

public class SelfRepair extends Modifier implements Enchantable, Craftable {

    private int percentagePerLevel;
    private int healthRepair;

    public SelfRepair() {
        super(ModifierType.SELF_REPAIR,
                ChatColor.GREEN,
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.HOE, ToolType.PICKAXE, ToolType.SHOVEL, ToolType.SWORD,
                                                ToolType.HELMET, ToolType.CHESTPLATE, ToolType.LEGGINGS, ToolType.BOOTS, ToolType.ELYTRA)),
                Main.getPlugin());
    }
    
    public void reload() {
    	FileConfiguration config = getConfig();
    	config.options().copyDefaults(true);
    	
    	String key = "Self-Repair";
    	config.addDefault(key + ".allowed", true);
    	config.addDefault(key + ".name", key);
    	config.addDefault(key + ".name_modifier", "Enchanted mossy Cobblestone");
    	config.addDefault(key + ".description", "Chance to repair the tool / armor while using it!");
    	config.addDefault(key + ".MaxLevel", 10);
    	config.addDefault(key + ".EnchantCost", 10);
    	config.addDefault(key + ".PercentagePerLevel", 10); //#100% at Level 10 (not necessary for unbreakable tool in most cases)
    	config.addDefault(key + ".HealthRepair", 2); //#How much durability should be repaired per trigger
    	config.addDefault(key + ".Recipe.Enabled", false);
    	
    	ConfigurationManager.saveConfig(config);
        
        init(config.getString("Self-Repair.name"),
                "[" + config.getString("Self-Repair.name_modifier") + "] " + config.getString("Self-Repair.description"),
                config.getInt("Self-Repair.MaxLevel"),
                ItemGenerator.itemEnchanter(Material.MOSSY_COBBLESTONE, ChatColor.GREEN + config.getString("Self-Repair.name_modifier"), 1, Enchantment.MENDING, 1));
        
        this.percentagePerLevel = config.getInt("Self-Repair.PercentagePerLevel");
        this.healthRepair = config.getInt("Self-Repair.HealthRepair");
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        return Modifier.checkAndAdd(p, tool, this, "selfrepair", isCommand);
    }

    @SuppressWarnings("deprecation")
	public void effect(Player p, ItemStack tool) {
        if (!p.hasPermission("minetinker.modifiers.selfrepair.use")) { return; }
        if (!modManager.hasMod(tool, this)) { return; }

        int level = modManager.getModLevel(tool, this);

        Random rand = new Random();
        int n = rand.nextInt(100);
        if (n <= this.percentagePerLevel * level) {
            short dura = (short) (tool.getDurability() - this.healthRepair);

            if (dura < 0) {
                dura = 0;
            }

            tool.setDurability(dura);
            ChatWriter.log(false, p.getDisplayName() + " triggered Self-Repair on " + ItemGenerator.getDisplayName(tool) + ChatColor.GRAY + " (" + tool.getType().toString() + ")!");
        }
    }

    @Override
    public void enchantItem(Player p, ItemStack item) {
        if (!p.hasPermission("minetinker.modifiers.selfrepair.craft")) { return; }
        _createModifierItem(getConfig(), p, this, "Self-Repair");
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Self-Repair", "Modifier_SelfRepair");
    }
    
    private static FileConfiguration getConfig() {
    	return ConfigurationManager.getConfig(Modifiers_Config.Self_Repair);
    }
    
    public boolean isAllowed() {
    	return getConfig().getBoolean("Self-Repair.allowed");
    }
}

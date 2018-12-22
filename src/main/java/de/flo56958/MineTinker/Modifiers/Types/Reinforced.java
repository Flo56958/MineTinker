package de.flo56958.MineTinker.Modifiers.Types;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Modifiers.Craftable;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import de.flo56958.MineTinker.Utilities.Modifiers_Config;

public class Reinforced extends Modifier implements Craftable {

    public Reinforced() {
        super(ModifierType.REINFORCED,
                ChatColor.DARK_GRAY,
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.HOE, ToolType.PICKAXE, ToolType.SHOVEL, ToolType.SWORD,
                                                ToolType.HELMET, ToolType.CHESTPLATE, ToolType.LEGGINGS, ToolType.BOOTS, ToolType.ELYTRA)),
                Main.getPlugin());
    }
    
    public void reload() {
    	FileConfiguration config = getConfig();
    	config.options().copyDefaults(true);
    	
    	String key = "Reinforced";
    	config.addDefault(key + ".allowed", true);
    	config.addDefault(key + ".name", key);
    	config.addDefault(key + ".name_modifier", "Compressed Obsidian");
    	config.addDefault(key + ".description", "Chance to not use durability when using the tool/armor!");
    	config.addDefault(key + ".MaxLevel", 3);
    	config.addDefault(key + ".Recipe.Enabled", true);
    	config.addDefault(key + ".Recipe.Top", "OOO");
    	config.addDefault(key + ".Recipe.Middle", "OOO");
    	config.addDefault(key + ".Recipe.Bottom", "OOO");
    	config.addDefault(key + ".Recipe.Materials.O", "OBSIDIAN");
        
    	ConfigurationManager.saveConfig(config);
    	
        init(config.getString("Reinforced.name"),
                "[" + config.getString("Reinforced.name_modifier") + "] " + config.getString("Reinforced.description"),
                config.getInt("Reinforced.MaxLevel"),
                ItemGenerator.itemEnchanter(Material.OBSIDIAN, ChatColor.DARK_GRAY + config.getString("Reinforced.name_modifier"), 1, Enchantment.DURABILITY, 1));
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (Modifier.checkAndAdd(p, tool, this, "reinforced", isCommand) == null) {
            return null;
        }

        ItemMeta meta = tool.getItemMeta();

        meta.addEnchant(Enchantment.DURABILITY, modManager.getModLevel(tool, this), true);
        if (Main.getPlugin().getConfig().getBoolean("HideEnchants")) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        } else {
            meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        tool.setItemMeta(meta);

        return tool;
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Reinforced", "Modifier_Reinforced");
    }
    
    private static FileConfiguration getConfig() {
    	return ConfigurationManager.getConfig(Modifiers_Config.Reinforced);
    }
    
    public boolean isAllowed() {
    	return getConfig().isBoolean("Reinforced.allowed");
    }
}

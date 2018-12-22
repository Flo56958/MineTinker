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
import de.flo56958.MineTinker.Modifiers.Enchantable;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import de.flo56958.MineTinker.Utilities.Modifiers_Config;

public class Fiery extends Modifier implements Enchantable, Craftable {

    public Fiery() {
        super(ModifierType.FIERY,
                ChatColor.YELLOW,
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.SWORD)),
                Main.getPlugin());
    }
    
    public void reload() {
    	FileConfiguration config = getConfig();
    	config.options().copyDefaults(true);
    	
    	String key = "Fiery";
    	config.addDefault(key + ".allowed", true);
    	config.addDefault(key + ".name", key);
    	config.addDefault(key + ".name_modifier", "Enchanted Blaze-Rod");
    	config.addDefault(key + ".description", "Inflames enemies!");
    	config.addDefault(key + ".MaxLevel", 2);
    	config.addDefault(key + ".EnchantCost", 10);
    	config.addDefault(key + ".Recipe.Enabled", false);
    	
    	ConfigurationManager.saveConfig(config);
    	
        init(config.getString("Fiery.name"),
                "[" + config.getString("Fiery.name_modifier") + "] " + config.getString("Fiery.description"),
                config.getInt("Fiery.MaxLevel"),
                ItemGenerator.itemEnchanter(Material.BLAZE_ROD, ChatColor.YELLOW + config.getString("Fiery.name_modifier"), 1, Enchantment.FIRE_ASPECT, 1));
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (Modifier.checkAndAdd(p, tool, this, "fiery", isCommand) == null) {
            return null;
        }

        ItemMeta meta = tool.getItemMeta();

        if (ToolType.BOW.getMaterials().contains(tool.getType())) {
            meta.addEnchant(Enchantment.ARROW_FIRE, modManager.getModLevel(tool, this), true);
        } else if (ToolType.SWORD.getMaterials().contains(tool.getType())) {
            meta.addEnchant(Enchantment.FIRE_ASPECT, modManager.getModLevel(tool, this), true);
        }
        if (Main.getPlugin().getConfig().getBoolean("HideEnchants")) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        } else {
            meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        tool.setItemMeta(meta);

        return tool;
    }

    @Override
    public void enchantItem(Player p, ItemStack item) {
        if (!p.hasPermission("minetinker.modifiers.fiery.craft")) { return; }
        _createModifierItem(getConfig(), p, this, "Fiery");
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Fiery", "Modifier_Fiery");
    }
    
    private static FileConfiguration getConfig() {
    	return ConfigurationManager.getConfig(Modifiers_Config.Fiery);
    }
    
    public boolean isAllowed() {
    	return getConfig().isBoolean("Fiery.allowed");
    }
}

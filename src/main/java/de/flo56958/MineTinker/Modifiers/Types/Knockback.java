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

public class Knockback extends Modifier implements Enchantable, Craftable {

    public Knockback() {
        super(ModifierType.KNOCKBACK,
                ChatColor.GRAY,
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.SWORD)),
                Main.getPlugin());
    }
    
    public void reload() {
    	FileConfiguration config = getConfig();
     	config.options().copyDefaults(true);
    	
     	String key = "Knockback";
     	config.addDefault(key + ".allowed", true);
     	config.addDefault(key + ".name", key);
     	config.addDefault(key + ".name_modifier", "Enhanced TNT");
     	config.addDefault(key + ".description", "Knockbacks Enemies further!");
     	config.addDefault(key + ".MaxLevel", 5);
     	config.addDefault(key + ".EnchantCost", 10);
     	config.addDefault(key + ".Recipe.Enabled", false);
        
     	init(config.getString("Knockback.name"),
                 "[" + config.getString("Knockback.name_modifier") + "] " + config.getString("Knockback.description"),
                 config.getInt("Knockback.MaxLevel"),
                 ItemGenerator.itemEnchanter(Material.TNT, ChatColor.GRAY + config.getString("Knockback.name_modifier"), 1, Enchantment.KNOCKBACK, 1));
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (Modifier.checkAndAdd(p, tool, this, "knockback", isCommand) == null) {
            return null;
        }

        ItemMeta meta = tool.getItemMeta();

        if (ToolType.AXE.getMaterials().contains(tool.getType())) {
            meta.addEnchant(Enchantment.KNOCKBACK, modManager.getModLevel(tool, this), true);
        } else if (ToolType.BOW.getMaterials().contains(tool.getType())) {
            meta.addEnchant(Enchantment.ARROW_KNOCKBACK, modManager.getModLevel(tool, this), true);
        } else if (ToolType.SWORD.getMaterials().contains(tool.getType())) {
            meta.addEnchant(Enchantment.KNOCKBACK, modManager.getModLevel(tool, this), true);
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
        if (!p.hasPermission("minetinker.modifiers.knockback.craft")) { return; }
        _createModifierItem(getConfig(), p, this, "Knockback");
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Knockback", "Modifier_Knockback");
    }
    
    private static FileConfiguration getConfig() {
    	return ConfigurationManager.getConfig(Modifiers_Config.Knockback);
    }
    
    public boolean isAllowed() {
    	return getConfig().isBoolean("Knockback.allowed");
    }
}

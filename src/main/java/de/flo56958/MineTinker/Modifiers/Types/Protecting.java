package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Craftable;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import de.flo56958.MineTinker.Utilities.modifiers_Config;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;

public class Protecting extends Modifier implements Craftable {

    public Protecting() {
        super(ModifierType.PROTECTING,
                ChatColor.GRAY,
                new ArrayList<>(Arrays.asList(ToolType.HELMET, ToolType.CHESTPLATE, ToolType.LEGGINGS, ToolType.BOOTS)),
                Main.getPlugin());
    }
    
    public void reload() {
    	 FileConfiguration config = getConfig();
         
         init(config.getString("Protecting.name"),
                 "[" + config.getString("Protecting.name_modifier") + "] " + config.getString("Protecting.description"),
                 config.getInt("Protecting.MaxLevel"),
                 ItemGenerator.itemEnchanter(Material.OBSIDIAN, ChatColor.GRAY + config.getString("Protecting.name_modifier"), 1, Enchantment.PROTECTION_ENVIRONMENTAL, 1));
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (Modifier.checkAndAdd(p, tool, this, "protecting", isCommand) == null) {
            return null;
        }

        ItemMeta meta = tool.getItemMeta();

        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, modManager.getModLevel(tool, this), true);
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
        _registerCraftingRecipe(getConfig(), this, "Protecting", "Modifier_Protecting");
    }
    
    private static FileConfiguration getConfig() {
    	return Main.getConfigurations().getConfig(modifiers_Config.Protecting);
    }
}

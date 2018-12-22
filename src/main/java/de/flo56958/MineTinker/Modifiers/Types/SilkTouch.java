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
import de.flo56958.MineTinker.Data.ModifierFailCause;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.ModifierFailEvent;
import de.flo56958.MineTinker.Modifiers.Craftable;
import de.flo56958.MineTinker.Modifiers.Enchantable;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import de.flo56958.MineTinker.Utilities.modifiers_Config;

public class SilkTouch extends Modifier implements Enchantable, Craftable {

    public SilkTouch() {
        super(ModifierType.SILK_TOUCH,
                ChatColor.WHITE,
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.HOE, ToolType.PICKAXE, ToolType.SHOVEL)),
                Main.getPlugin());
    }
    
    public void reload() {
    	FileConfiguration config = getConfig();
        
        init(config.getString("Silk-Touch.name"),
                "[" + config.getString("Silk-Touch.name_modifier") + "] " + config.getString("Silk-Touch.description"),
                1,
                ItemGenerator.itemEnchanter(Material.COBWEB, ChatColor.WHITE + config.getString("Silk-Touch.name_modifier"), 1, Enchantment.SILK_TOUCH, 1));
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {

        if (modManager.get(ModifierType.AUTO_SMELT) != null) {
            if (modManager.hasMod(tool, modManager.get(ModifierType.AUTO_SMELT))) {
                pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
                return null;
            }
        }
        if (modManager.get(ModifierType.LUCK) != null) {
            if (modManager.hasMod(tool, modManager.get(ModifierType.LUCK))) {
                pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
                return null;
            }
        }
        if (Modifier.checkAndAdd(p, tool, this, "silktouch", isCommand) == null) {
            return null;
        }

        ItemMeta meta = tool.getItemMeta();

        meta.addEnchant(Enchantment.SILK_TOUCH, modManager.getModLevel(tool, this), true);
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
        if (!p.hasPermission("minetinker.modifiers.silktouch.craft")) { return; }
        _createModifierItem(getConfig(), p, this, "Silk-Touch");
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Silk-Touch", "Modifier_SilkTouch");
    }
    
    private static FileConfiguration getConfig() {
    	return Main.getConfigurations().getConfig(modifiers_Config.Silk_Touch);
    }
    
    public boolean isAllowed() {
    	return getConfig().isBoolean("Silk-Touch.allowed");
    }
}

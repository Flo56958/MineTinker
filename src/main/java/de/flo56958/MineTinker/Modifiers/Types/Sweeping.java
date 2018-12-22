package de.flo56958.MineTinker.Modifiers.Types;

import java.util.ArrayList;
import java.util.Collections;

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
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import de.flo56958.MineTinker.Utilities.modifiers_Config;

public class Sweeping extends Modifier implements Enchantable, Craftable {
    public Sweeping() {
        super(ModifierType.SWEEPING,
                ChatColor.RED,
                new ArrayList<>(Collections.singletonList(ToolType.SWORD)),
                Main.getPlugin());
    }
    
    public void reload() {
    	FileConfiguration config = getConfig();
        
        init(config.getString("Sweeping.name"),
                "[" + config.getString("Sweeping.name_modifier") + "] " + config.getString("Sweeping.description"),
                config.getInt("Sweeping.MaxLevel"),
                ItemGenerator.itemEnchanter(Material.IRON_INGOT, ChatColor.RED + config.getString("Sweeping.name_modifier"), 1, Enchantment.SWEEPING_EDGE, 1));
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (Modifier.checkAndAdd(p, tool, this, "sweeping", isCommand) == null) {
            return null;
        }

        ItemMeta meta = tool.getItemMeta();

        meta.addEnchant(Enchantment.SWEEPING_EDGE, modManager.getModLevel(tool, this), true);
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
        if (!p.hasPermission("minetinker.modifiers.sweeping.craft")) { return; }
        _createModifierItem(getConfig(), p, this, "Sweeping");
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Sweeping", "Modifier_Sweeping");
    }
    
    private static FileConfiguration getConfig() {
    	return Main.getConfigurations().getConfig(modifiers_Config.Sweeping);
    }
    
    public boolean isAllowed() {
    	return getConfig().isBoolean("Sweeping.allowed");
    }
}

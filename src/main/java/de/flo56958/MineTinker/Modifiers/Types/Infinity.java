package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ModifierFailCause;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.ModifierFailEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Craftable;
import de.flo56958.MineTinker.Modifiers.Enchantable;
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
import java.util.Collections;

public class Infinity extends Modifier implements Enchantable, Craftable {

    private boolean compatibleWithEnder;

    public Infinity() {
        super(ModifierType.INFINITY,
                ChatColor.WHITE,
                new ArrayList<>(Collections.singletonList(ToolType.BOW)),
                Main.getPlugin());
    }
    
    public void reload() {
    	FileConfiguration config = getConfig();
    	config.options().copyDefaults(true);
    	
    	String key = "Infinity";
    	config.addDefault(key + ".allowed", true);
    	config.addDefault(key + ".name", key);
    	config.addDefault(key + ".name_modifier", "Enchanted Arrow");
    	config.addDefault(key + ".description", "You only need one Arrow to shoot a bow!");
    	config.addDefault(key + ".EnchantCost", 10);
    	config.addDefault(key + ".Recipe.Enabled", false);
    	//#Check Ender.yml for Compatibility-option for Ender and Infinity
    	
        init(config.getString("Infinity.name"),
                "[" + config.getString("Infinity.name_modifier") + "] " + config.getString("Infinity.description"),
                1,
                ItemGenerator.itemEnchanter(Material.ARROW, ChatColor.WHITE + config.getString("Infinity.name_modifier"), 1, Enchantment.ARROW_INFINITE, 1));
        
        this.compatibleWithEnder = Main.getConfigurations().getConfig("Ender.yml").getBoolean("Ender.CompatibleWithInfinity");
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (!this.compatibleWithEnder) {
            if (modManager.get(ModifierType.ENDER) != null) {
                if (modManager.hasMod(tool, modManager.get(ModifierType.ENDER))) {
                    pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
                    return null;
                }
            }
        }
        if (Modifier.checkAndAdd(p, tool, this, "infinity", isCommand) == null) {
            return null;
        }

        ItemMeta meta = tool.getItemMeta();

        meta.addEnchant(Enchantment.ARROW_INFINITE, modManager.getModLevel(tool, this), true);
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
        if (!p.hasPermission("minetinker.modifiers.infinity.craft")) { return; }
        _createModifierItem(getConfig(), p, this, "Infinity");
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Infinity", "Modifier_Infinity");
    }
    
    private static FileConfiguration getConfig() {
    	return Main.getConfigurations().getConfig(modifiers_Config.Infinity);
    }
    
    public boolean isAllowed() {
    	return getConfig().isBoolean("Infinity.allowed");
    }
}

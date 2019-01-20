package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Craftable;
import de.flo56958.MineTinker.Modifiers.Enchantable;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import de.flo56958.MineTinker.Utilities.Modifiers_Config;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;

public class Knockback extends Modifier implements Enchantable, Craftable {

    public Knockback() {
        super(ModifierType.KNOCKBACK,
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.SWORD)),
                Main.getPlugin());
    }

    @Override
    public void reload() {
    	FileConfiguration config = getConfig();
     	config.options().copyDefaults(true);
    	
     	String key = "Knockback";
     	config.addDefault(key + ".allowed", true);
     	config.addDefault(key + ".name", key);
     	config.addDefault(key + ".name_modifier", "Enhanced TNT");
     	config.addDefault(key + ".description", "Knockbacks Enemies further!");
        config.addDefault(key + ".description_modifier", "%WHITE%Modifier-Item for the Knockback-Modifier");
        config.addDefault(key + ".Color", "%GRAY%");
        config.addDefault(key + ".MaxLevel", 5);
     	config.addDefault(key + ".EnchantCost", 10);
     	config.addDefault(key + ".Recipe.Enabled", false);
        
     	ConfigurationManager.saveConfig(config);
     	
     	init(config.getString(key + ".name"),
                "[" + config.getString(key + ".name_modifier") + "] " + config.getString(key + ".description"),
                ChatWriter.getColor(config.getString(key + ".Color")),
                config.getInt(key + ".MaxLevel"),
                modManager.createModifierItem(Material.TNT, ChatWriter.getColor(config.getString(key + ".Color")) + config.getString(key + ".name_modifier"), ChatWriter.addColors(config.getString(key + ".description_modifier")), this));
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
    public void removeMod(ItemStack tool) {
        ItemMeta meta = tool.getItemMeta();
        meta.removeEnchant(Enchantment.KNOCKBACK);
        meta.removeEnchant(Enchantment.ARROW_KNOCKBACK);
        tool.setItemMeta(meta);
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

    @Override
    public boolean isAllowed() {
    	return getConfig().getBoolean("Knockback.allowed");
    }
}

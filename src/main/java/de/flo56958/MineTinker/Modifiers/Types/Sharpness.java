package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Craftable;
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

public class Sharpness extends Modifier implements Craftable {

    public Sharpness() {
        super(ModifierType.SHARPNESS,
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.SWORD, ToolType.TRIDENT)),
                Main.getPlugin());
    }

    @Override
    public void reload() {
    	FileConfiguration config = getConfig();
    	config.options().copyDefaults(true);
    	
    	String key = "Sharpness";
    	config.addDefault(key + ".allowed", true);
    	config.addDefault(key + ".name", key);
    	config.addDefault(key + ".name_modifier", "Compressed Quartzblock");
    	config.addDefault(key + ".description", "Weapon does additional damage!");
        config.addDefault(key + ".description_modifier", "%WHITE%Modifier-Item for the Sharpness-Modifier");
        config.addDefault(key + ".Color", "%WHITE%");
        config.addDefault(key + ".MaxLevel", 5);
    	config.addDefault(key + ".Recipe.Enabled", true);
    	config.addDefault(key + ".Recipe.Top", "QQQ");
    	config.addDefault(key + ".Recipe.Middle", "QQQ");
    	config.addDefault(key + ".Recipe.Bottom", "QQQ");
    	config.addDefault(key + ".Recipe.Materials.Q", "QUARTZ_BLOCK");
        
    	ConfigurationManager.saveConfig(config);
    	
        init(config.getString(key + ".name"),
                "[" + config.getString(key + ".name_modifier") + "] " + config.getString(key + ".description"),
                ChatWriter.getColor(config.getString(key + ".Color")),
                config.getInt(key + ".MaxLevel"),
                modManager.createModifierItem(Material.QUARTZ_BLOCK, ChatWriter.getColor(config.getString(key + ".Color")) + config.getString(key + ".name_modifier"), ChatWriter.addColors(config.getString(key + ".description_modifier")), this));
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (Modifier.checkAndAdd(p, tool, this, "sharpness", isCommand) == null) {
            return null;
        }

        ItemMeta meta = tool.getItemMeta();

        if (ToolType.AXE.getMaterials().contains(tool.getType())) {
            meta.addEnchant(Enchantment.DAMAGE_ALL, modManager.getModLevel(tool, this), true);
        } else if (ToolType.BOW.getMaterials().contains(tool.getType())) {
            meta.addEnchant(Enchantment.ARROW_DAMAGE, modManager.getModLevel(tool, this), true);
        } else if (ToolType.SWORD.getMaterials().contains(tool.getType())) {
            meta.addEnchant(Enchantment.DAMAGE_ALL, modManager.getModLevel(tool, this), true);
        } else if (ToolType.TRIDENT.getMaterials().contains(tool.getType())) {
            meta.addEnchant(Enchantment.DAMAGE_ALL, modManager.getModLevel(tool, this), true);
            meta.addEnchant(Enchantment.IMPALING, modManager.getModLevel(tool, this), true);
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
        meta.removeEnchant(Enchantment.DAMAGE_ALL);
        meta.removeEnchant(Enchantment.ARROW_DAMAGE);
        meta.removeEnchant(Enchantment.IMPALING);
        tool.setItemMeta(meta);
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Sharpness", "Modifier_Sharpness");
    }
    
    private static FileConfiguration getConfig() {
    	return ConfigurationManager.getConfig(Modifiers_Config.Sharpness);
    }

    @Override
    public boolean isAllowed() {
    	return getConfig().getBoolean("Sharpness.allowed");
    }
}

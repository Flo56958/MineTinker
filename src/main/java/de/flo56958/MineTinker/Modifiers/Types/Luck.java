package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ModifierFailCause;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.ModifierFailEvent;
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

public class Luck extends Modifier implements Craftable {

    public Luck() {
        super(ModifierType.LUCK,
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.HOE, ToolType.PICKAXE, ToolType.SHOVEL, ToolType.SWORD)),
                Main.getPlugin());
    }
    
    public void reload() {
    	FileConfiguration config = getConfig();
    	config.options().copyDefaults(true);
    	
    	String key = "Luck";
    	config.addDefault(key + ".allowed", true);
    	config.addDefault(key + ".name", key);
    	config.addDefault(key + ".name_modifier", "Compressed Lapis-Block");
    	config.addDefault(key + ".description", "Get more loot from enemies and blocks!");
        config.addDefault(key + ".description_modifier", "%WHITE%Modifier-Item for the Luck-Modifier");
        config.addDefault(key + ".Color", "%BLUE%");
        config.addDefault(key + ".MaxLevel", 5);
    	config.addDefault(key + ".Recipe.Enabled", true);
    	config.addDefault(key + ".Recipe.Top", "LLL");
    	config.addDefault(key + ".Recipe.Middle", "LLL");
    	config.addDefault(key + ".Recipe.Bottom", "LLL");
    	config.addDefault(key + ".Recipe.Materials.L", "LAPIS_BLOCK");
    	
    	ConfigurationManager.saveConfig(config);
    	
        init(config.getString("Luck.name"),
                "[" + config.getString("Luck.name_modifier") + "] " + config.getString("Luck.description"),
                ChatWriter.getColor(config.getString(key + ".Color")),
                config.getInt("Luck.MaxLevel"),
                modManager.createModifierItem(Material.LAPIS_BLOCK, ChatWriter.getColor(config.getString(key + ".Color")) + config.getString(key + ".name_modifier"), ChatWriter.addColors(config.getString(key + ".description_modifier")), this));
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (modManager.get(ModifierType.SILK_TOUCH) != null) {
            if (modManager.hasMod(tool, modManager.get(ModifierType.SILK_TOUCH))) {
                pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
                return null;
            }
        }
        if (Modifier.checkAndAdd(p, tool, this, "luck", isCommand) == null) {
            return null;
        }

        ItemMeta meta = tool.getItemMeta();

        if (ToolType.AXE.getMaterials().contains(tool.getType())) {
            meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, modManager.getModLevel(tool, this), true);
            meta.addEnchant(Enchantment.LOOT_BONUS_MOBS, modManager.getModLevel(tool, this), true);
        } else if (ToolType.BOW.getMaterials().contains(tool.getType())) {
            meta.addEnchant(Enchantment.LOOT_BONUS_MOBS, modManager.getModLevel(tool, this), true);
        } else if (ToolType.HOE.getMaterials().contains(tool.getType())) {
            meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, modManager.getModLevel(tool, this), true);
        } else if (ToolType.PICKAXE.getMaterials().contains(tool.getType())) {
            meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, modManager.getModLevel(tool, this), true);
        } else if (ToolType.SHOVEL.getMaterials().contains(tool.getType())) {
            meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, modManager.getModLevel(tool, this), true);
        } else if (ToolType.SWORD.getMaterials().contains(tool.getType())) {
            meta.addEnchant(Enchantment.LOOT_BONUS_MOBS, modManager.getModLevel(tool, this), true);
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
        meta.removeEnchant(Enchantment.LOOT_BONUS_BLOCKS);
        meta.removeEnchant(Enchantment.LOOT_BONUS_MOBS);
        tool.setItemMeta(meta);
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Luck", "Modifier_Luck");
    }
    
    private static FileConfiguration getConfig() {
    	return ConfigurationManager.getConfig(Modifiers_Config.Luck);
    }
    
    public boolean isAllowed() {
    	return getConfig().getBoolean("Luck.allowed");
    }
}

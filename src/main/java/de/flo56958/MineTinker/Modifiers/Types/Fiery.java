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

public class Fiery extends Modifier implements Enchantable, Craftable {

    //TODO: Add Particle effect
    private static Fiery instance;

    public static Fiery instance() {
        if (instance == null) instance = new Fiery();
        return instance;
    }

    private Fiery() {
        super(ModifierType.FIERY,
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.SWORD)),
                Main.getPlugin());
    }

    @Override
    public void reload() {
    	FileConfiguration config = getConfig();
    	config.options().copyDefaults(true);
    	
    	String key = "Fiery";
    	config.addDefault(key + ".allowed", true);
    	config.addDefault(key + ".name", key);
    	config.addDefault(key + ".name_modifier", "Enchanted Blaze-Rod");
        config.addDefault(key + ".modifier_item", "BLAZE_ROD"); //Needs to be a viable Material-Type
        config.addDefault(key + ".description", "Inflames enemies!");
        config.addDefault(key + ".description_modifier", "%WHITE%Modifier-Item for the Fiery-Modifier");
        config.addDefault(key + ".Color", "%YELLOW%");
        config.addDefault(key + ".MaxLevel", 2);
    	config.addDefault(key + ".EnchantCost", 10);
    	config.addDefault(key + ".Recipe.Enabled", false);
    	
    	ConfigurationManager.saveConfig(config);
    	
        init(config.getString(key + ".name"),
                "[" + config.getString(key + ".name_modifier") + "] " + config.getString(key + ".description"),
                ChatWriter.getColor(config.getString(key + ".Color")),
                config.getInt(key + ".MaxLevel"),
                modManager.createModifierItem(Material.getMaterial(config.getString(key + ".modifier_item")), ChatWriter.getColor(config.getString(key + ".Color")) + config.getString(key + ".name_modifier"), ChatWriter.addColors(config.getString(key + ".description_modifier")), this));
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (Modifier.checkAndAdd(p, tool, this, "fiery", isCommand) == null) {
            return null;
        }

        ItemMeta meta = tool.getItemMeta();

        if (meta != null) {
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
        }

        return tool;
    }

    @Override
    public void removeMod(ItemStack tool) {
        ItemMeta meta = tool.getItemMeta();

        if (meta != null) {
            meta.removeEnchant(Enchantment.FIRE_ASPECT);
            meta.removeEnchant(Enchantment.ARROW_FIRE);
            tool.setItemMeta(meta);
        }
    }

    @Override
    public void enchantItem(Player p, ItemStack item) {
        if (!p.hasPermission("minetinker.modifiers.fiery.craft")) return;
        _createModifierItem(getConfig(), p, this, "Fiery");
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Fiery", "Modifier_Fiery");
    }
    
    private static FileConfiguration getConfig() {
    	return ConfigurationManager.getConfig(Modifiers_Config.Fiery);
    }

    @Override
    public boolean isAllowed() {
    	return getConfig().getBoolean("Fiery.allowed");
    }
}

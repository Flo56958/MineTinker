package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.Main;
import de.flo56958.minetinker.modifiers.Craftable;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utilities.ChatWriter;
import de.flo56958.minetinker.utilities.ConfigurationManager;
import de.flo56958.minetinker.utilities.Modifiers_Config;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Haste extends Modifier implements Craftable {

    private static Haste instance;

    public static Haste instance() {
        synchronized (Haste.class) {
            if (instance == null) instance = new Haste();
        }
        return instance;
    }

    private Haste() {
        super(ModifierType.HASTE,
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.PICKAXE, ToolType.SHOVEL, ToolType.SHEARS, ToolType.FISHINGROD)),
                Main.getPlugin());
    }

    @Override
    public List<Enchantment> getAppliedEnchantments() {
        List<Enchantment> enchantments = new ArrayList<>();
        enchantments.add(Enchantment.LURE);
        enchantments.add(Enchantment.DIG_SPEED);

        return enchantments;
    }

    @Override
    public void reload() {
    	FileConfiguration config = getConfig();
    	config.options().copyDefaults(true);
    	
    	String key = "Haste";
    	config.addDefault(key + ".allowed", true);
    	config.addDefault(key + ".name", key);
    	config.addDefault(key + ".name_modifier", "Compressed Redstoneblock");
        config.addDefault(key + ".modifier_item", "REDSTONE_BLOCK"); //Needs to be a viable Material-Type
        config.addDefault(key + ".description", "Tool can destroy blocks faster!");
        config.addDefault(key + ".description_modifier", "%WHITE%Modifier-Item for the Haste-Modifier");
        config.addDefault(key + ".Color", "%DARK_RED%");
        config.addDefault(key + ".MaxLevel", 5);
    	config.addDefault(key + ".Recipe.Enabled", true);
    	config.addDefault(key + ".Recipe.Top", "RRR");
    	config.addDefault(key + ".Recipe.Middle", "RRR");
    	config.addDefault(key + ".Recipe.Bottom", "RRR");
    	config.addDefault(key + ".Recipe.Materials.R", "REDSTONE_BLOCK");
        
    	ConfigurationManager.saveConfig(config);

        String name = config.getString(key + ".name");
        String modName = config.getString(key + ".name_modifier");
        String description = config.getString(key + ".description");
        String color = config.getString(key + ".Color");
        int maxLevel = config.getInt(key + ".MaxLevel");
        String modItem = config.getString(key + ".modifier_item");
        String modDescription = config.getString(key + ".description_modifier");

        if (name == null || modName == null || description == null || color == null || modItem == null || modDescription == null) return;

        init(name, "[" + modName + "] " + description, ChatWriter.getColor(color), maxLevel,
                modManager.createModifierItem(Material.getMaterial(modItem), ChatWriter.getColor(color) + modName,
                        ChatWriter.addColors(modDescription), this));
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (Modifier.checkAndAdd(p, tool, this, "haste", isCommand) == null) {
            return null;
        }

        ItemMeta meta = tool.getItemMeta();

        if (meta != null) {
            if (ToolType.FISHINGROD.getMaterials().contains(tool.getType())) {
                meta.addEnchant(Enchantment.LURE, modManager.getModLevel(tool, this), true);
            } else {
                meta.addEnchant(Enchantment.DIG_SPEED, modManager.getModLevel(tool, this), true);
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
            meta.removeEnchant(Enchantment.DIG_SPEED);
            meta.removeEnchant(Enchantment.LURE);
            tool.setItemMeta(meta);
        }
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Haste", "Modifier_Haste");
    }
    
    private static FileConfiguration getConfig() {
    	return ConfigurationManager.getConfig(Modifiers_Config.Haste);
    }

    @Override
    public boolean isAllowed() {
    	return getConfig().getBoolean("Haste.allowed");
    }
}

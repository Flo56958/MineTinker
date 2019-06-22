package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import de.flo56958.MineTinker.Utilities.nms.NBTUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Haste extends Modifier {

    private static Haste instance;

    public static Haste instance() {
        synchronized (Haste.class) {
            if (instance == null) instance = new Haste();
        }
        return instance;
    }

    private Haste() {
        super(ModifierType.HASTE,
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.CROSSBOW, ToolType.PICKAXE, ToolType.SHOVEL, ToolType.SHEARS, ToolType.FISHINGROD)),
                Main.getPlugin());
    }

    @Override
    public List<Enchantment> getAppliedEnchantments() {
        List<Enchantment> enchantments = new ArrayList<>();
        enchantments.add(Enchantment.LURE);
        enchantments.add(Enchantment.DIG_SPEED);
        if(NBTUtils.isOneFourteenCompatible()) enchantments.add(Enchantment.QUICK_CHARGE);

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

        Map<String, String> recipeMaterials = new HashMap<>();
        recipeMaterials.put("R", "REDSTONE_BLOCK");

        config.addDefault(key + ".Recipe.Materials", recipeMaterials);

    	ConfigurationManager.saveConfig(config);
    	
        init(config.getString(key + ".name"),
                "[" + config.getString(key + ".name_modifier") + "] \u200B" + config.getString(key + ".description"),
                ChatWriter.getColor(config.getString(key + ".Color")),
                config.getInt(key + ".MaxLevel"),
                modManager.createModifierItem(Material.getMaterial(config.getString(key + ".modifier_item")), ChatWriter.getColor(config.getString(key + ".Color")) + config.getString(key + ".name_modifier"), ChatWriter.addColors(config.getString(key + ".description_modifier")), this));
    }

    @Override
    public boolean applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (Modifier.checkAndAdd(p, tool, this, "haste", isCommand)) return false;

        ItemMeta meta = tool.getItemMeta();

        if (meta != null) {
            if (ToolType.FISHINGROD.getMaterials().contains(tool.getType())) {
                meta.addEnchant(Enchantment.LURE, modManager.getModLevel(tool, this), true);
            } else if (ToolType.CROSSBOW.getMaterials().contains(tool.getType())) {
                if (NBTUtils.isOneFourteenCompatible()) meta.addEnchant(Enchantment.QUICK_CHARGE, modManager.getModLevel(tool, this), true);
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

        return true;
    }

    @Override
    public void removeMod(ItemStack tool) {
        ItemMeta meta = tool.getItemMeta();

        if (meta == null) return;

        meta.removeEnchant(Enchantment.DIG_SPEED);
        meta.removeEnchant(Enchantment.LURE);
        if (NBTUtils.isOneFourteenCompatible()) meta.removeEnchant(Enchantment.QUICK_CHARGE);
        tool.setItemMeta(meta);
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Haste", "Modifier_Haste");
    }
    
    private static FileConfiguration getConfig() {
        return ConfigurationManager.getConfig(ModifierType.HASTE.getFileName());
    }

    @Override
    public boolean isAllowed() {
    	return getConfig().getBoolean("Haste.allowed");
    }
}

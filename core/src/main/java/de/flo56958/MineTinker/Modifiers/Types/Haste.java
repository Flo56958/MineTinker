package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import de.flo56958.MineTinker.Utilities.nms.NBTUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.*;

public class Haste extends Modifier {

    private static Haste instance;

    public static Haste instance() {
        synchronized (Haste.class) {
            if (instance == null) instance = new Haste();
        }
        return instance;
    }

    private Haste() {
        super("Haste", "Haste.yml",
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

    	config.addDefault("Allowed", true);
    	config.addDefault("Name", "Haste");
    	config.addDefault("ModifierItemName", "Compressed Redstoneblock");
        config.addDefault("Description", "Tool can destroy blocks faster!");
        config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Haste-Modifier");
        config.addDefault("Color", "%DARK_RED%");
        config.addDefault("MaxLevel", 5);

    	config.addDefault("Recipe.Enabled", true);
    	config.addDefault("Recipe.Top", "RRR");
    	config.addDefault("Recipe.Middle", "RRR");
    	config.addDefault("Recipe.Bottom", "RRR");

        Map<String, String> recipeMaterials = new HashMap<>();
        recipeMaterials.put("R", "REDSTONE_BLOCK");

        config.addDefault("Recipe.Materials", recipeMaterials);

    	ConfigurationManager.saveConfig(config);
        ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

        init(Material.REDSTONE_BLOCK, true);
    }

    @Override
    public boolean applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (!Modifier.checkAndAdd(p, tool, this, "haste", isCommand)) return false;

        ItemMeta meta = tool.getItemMeta();

        if (meta != null) {
            if (ToolType.FISHINGROD.contains(tool.getType())) {
                meta.addEnchant(Enchantment.LURE, modManager.getModLevel(tool, this), true);
            } else if (ToolType.CROSSBOW.contains(tool.getType())) {
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
}

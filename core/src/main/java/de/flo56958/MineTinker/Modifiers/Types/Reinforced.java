package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Reinforced extends Modifier {

    private static Reinforced instance;
    private boolean applyUnbreakableOnMaxLevel;
    private boolean hideUnbreakableFlag;

    public static Reinforced instance() {
        synchronized (Reinforced.class) {
            if (instance == null) {
                instance = new Reinforced();
            }
        }

        return instance;
    }

    @Override
    public String getKey() {
        return "Reinforced";
    }

    @Override
    public List<ToolType> getAllowedTools() {
        return Arrays.asList(ToolType.values());
    }

    private Reinforced() {
        super(Main.getPlugin());
    }

    @Override
    public List<Enchantment> getAppliedEnchantments() {
        return Collections.singletonList(Enchantment.DURABILITY);

    }

    @Override
    public void reload() {
    	FileConfiguration config = getConfig();
    	config.options().copyDefaults(true);

        String key = getKey();

        config.addDefault(key + ".allowed", true);
    	config.addDefault(key + ".name", key);
    	config.addDefault(key + ".name_modifier", "Compressed Obsidian");
        config.addDefault(key + ".modifier_item", "OBSIDIAN"); //Needs to be a viable Material-Type
        config.addDefault(key + ".description", "Chance to not use durability when using the tool/armor!");
        config.addDefault(key + ".description_modifier", "%WHITE%Modifier-Item for the " + key + "-Modifier");
        config.addDefault(key + ".Color", "%DARK_GRAY%");
        config.addDefault(key + ".EnchantCost", 10);
        config.addDefault(key + ".MaxLevel", 3);
        config.addDefault(key + ".ApplyUnbreakableOnMaxLevel", false);
        config.addDefault(key + ".HideUnbreakableFlag", true);

    	config.addDefault(key + ".Recipe.Enabled", true);
    	config.addDefault(key + ".Recipe.Top", "OOO");
    	config.addDefault(key + ".Recipe.Middle", "OOO");
    	config.addDefault(key + ".Recipe.Bottom", "OOO");

        Map<String, String> recipeMaterials = new HashMap<>();
        recipeMaterials.put("O", "OBSIDIAN");

        config.addDefault(key + ".Recipe.Materials", recipeMaterials);

    	ConfigurationManager.saveConfig(config);
        ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

    	this.applyUnbreakableOnMaxLevel = config.getBoolean(key + ".ApplyUnbreakableOnMaxLevel");
    	this.hideUnbreakableFlag = config.getBoolean(key + ".HideUnbreakableFlag");
    	
        init("[" + config.getString(key + ".name_modifier") + "] \u200B" + config.getString(key + ".description"),
                ChatWriter.getColor(config.getString(key + ".Color")),
                config.getInt(key + ".MaxLevel"),
                modManager.createModifierItem(Material.getMaterial(config.getString(key + ".modifier_item")), ChatWriter.getColor(config.getString(key + ".Color")) + config.getString(key + ".name_modifier"), ChatWriter.addColors(config.getString(key + ".description_modifier")), this));
    }

    @Override
    public boolean applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (!Modifier.checkAndAdd(p, tool, this, "reinforced", isCommand)) {
            return false;
        }

        ItemMeta meta = tool.getItemMeta();

        if (meta != null) {
            meta.addEnchant(Enchantment.DURABILITY, modManager.getModLevel(tool, this), true);

            if (Main.getPlugin().getConfig().getBoolean("HideEnchants")) {
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            } else {
                meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            if (modManager.getModLevel(tool, this) == this.getMaxLvl() && this.applyUnbreakableOnMaxLevel) {
                meta.setUnbreakable(true);
                if(hideUnbreakableFlag) {
                    meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                }
            }

            tool.setItemMeta(meta);
        }

        return true;
    }

    @Override
    public void removeMod(ItemStack tool) {
        ItemMeta meta = tool.getItemMeta();

        if (meta != null) {
            meta.removeEnchant(Enchantment.DURABILITY);
            if (this.applyUnbreakableOnMaxLevel) {
                meta.setUnbreakable(false);
                meta.removeItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            }

            tool.setItemMeta(meta);
        }
    }
}

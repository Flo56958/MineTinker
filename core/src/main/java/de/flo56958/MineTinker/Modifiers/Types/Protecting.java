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

public class Protecting extends Modifier {

    private static Protecting instance;

    public static Protecting instance() {
        synchronized (Protecting.class) {
            if (instance == null) instance = new Protecting();
        }
        return instance;
    }

    private Protecting() {
        super("Protecting", "Protecting.yml",
                new ArrayList<>(Arrays.asList(ToolType.HELMET, ToolType.CHESTPLATE, ToolType.LEGGINGS, ToolType.BOOTS, ToolType.ELYTRA)),
                Main.getPlugin());
    }

    @Override
    public List<Enchantment> getAppliedEnchantments() {
        return Collections.singletonList(Enchantment.PROTECTION_ENVIRONMENTAL);

    }

    @Override
    public void reload() {
    	FileConfiguration config = getConfig();
     	config.options().copyDefaults(true);
    	
     	String key = "Protecting";
     	config.addDefault(key + ".allowed", true);
     	config.addDefault(key + ".name", key);
     	config.addDefault(key + ".name_modifier", "Enriched Obsidian");
        config.addDefault(key + ".modifier_item", "OBSIDIAN"); //Needs to be a viable Material-Type
        config.addDefault(key + ".description", "Your armor protects you better against all damage!");
        config.addDefault(key + ".description_modifier", "%WHITE%Modifier-Item for the Protecting-Modifier");
        config.addDefault(key + ".Color", "%GRAY%");
        config.addDefault(key + ".EnchantCost", 10);
        config.addDefault(key + ".MaxLevel", 5);

     	config.addDefault(key + ".Recipe.Enabled", true);
     	config.addDefault(key + ".Recipe.Top", "DID");
     	config.addDefault(key + ".Recipe.Middle", "IOI");
     	config.addDefault(key + ".Recipe.Bottom", "DID");

        Map<String, String> recipeMaterials = new HashMap<>();
        recipeMaterials.put("D", "DIAMOND");
        recipeMaterials.put("I", "IRON_INGOT");
        recipeMaterials.put("O", "OBSIDIAN");

        config.addDefault(key + ".Recipe.Materials", recipeMaterials);
         
     	ConfigurationManager.saveConfig(config);
        ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());
     	
        init(config.getString(key + ".name"),
                 "[" + config.getString(key + ".name_modifier") + "] \u200B" + config.getString(key + ".description"),
                ChatWriter.getColor(config.getString(key + ".Color")),
                config.getInt(key + ".MaxLevel"),
                modManager.createModifierItem(Material.getMaterial(config.getString(key + ".modifier_item")), ChatWriter.getColor(config.getString(key + ".Color")) + config.getString(key + ".name_modifier"), ChatWriter.addColors(config.getString(key + ".description_modifier")), this));
    }

    @Override
    public boolean applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (!Modifier.checkAndAdd(p, tool, this, "protecting", isCommand)) {
            return false;
        }

        ItemMeta meta = tool.getItemMeta();

        if (meta != null) {
            meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, modManager.getModLevel(tool, this), true);

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
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Protecting", "Modifier_Protecting");
    }

    @Override
    public boolean isAllowed() {
    	return getConfig().getBoolean("Protecting.allowed");
    }
}

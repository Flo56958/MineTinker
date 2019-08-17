package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.*;

public class Protecting extends Modifier {

    private static Protecting instance;

    public static Protecting instance() {
        synchronized (Protecting.class) {
            if (instance == null) {
                instance = new Protecting();
            }
        }

        return instance;
    }

    @Override
    public String getKey() {
        return "Protecting";
    }

    @Override
    public List<ToolType> getAllowedTools() {
        return Arrays.asList(ToolType.HELMET, ToolType.CHESTPLATE, ToolType.LEGGINGS, ToolType.BOOTS, ToolType.ELYTRA);
    }

    private Protecting() {
        super(Main.getPlugin());
    }

    @Override
    public List<Enchantment> getAppliedEnchantments() {
        return Collections.singletonList(Enchantment.PROTECTION_ENVIRONMENTAL);

    }

    @Override
    public void reload() {
    	FileConfiguration config = getConfig();
     	config.options().copyDefaults(true);
    	
     	config.addDefault("Allowed", true);
     	config.addDefault("Name", "Protecting");
     	config.addDefault("ModifierItemName", "Enriched Obsidian");
        config.addDefault("Description", "Your armor protects you better against all damage!");
        config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Protecting-Modifier");
        config.addDefault("Color", "%GRAY%");
        config.addDefault("MaxLevel", 5);
        config.addDefault("OverrideLanguagesystem", false);

     	config.addDefault("Recipe.Enabled", true);
     	config.addDefault("Recipe.Top", "DID");
     	config.addDefault("Recipe.Middle", "IOI");
     	config.addDefault("Recipe.Bottom", "DID");

        Map<String, String> recipeMaterials = new HashMap<>();
        recipeMaterials.put("D", "DIAMOND");
        recipeMaterials.put("I", "IRON_INGOT");
        recipeMaterials.put("O", "OBSIDIAN");

        config.addDefault("Recipe.Materials", recipeMaterials);
         
     	ConfigurationManager.saveConfig(config);
        ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());
     	
        init(Material.OBSIDIAN, true);
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
}

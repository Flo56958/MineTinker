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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Knockback extends Modifier {

    private static Knockback instance;

    public static Knockback instance() {
        synchronized (Knockback.class) {
            if (instance == null) {
                instance = new Knockback();
            }
        }

        return instance;
    }

    @Override
    public String getKey() {
        return "Knockback";
    }

    @Override
    public List<ToolType> getAllowedTools() {
        return Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.SWORD, ToolType.TRIDENT);
    }

    private Knockback() {
        super(Main.getPlugin());
    }

    @Override
    public List<Enchantment> getAppliedEnchantments() {
        return Arrays.asList(Enchantment.KNOCKBACK, Enchantment.ARROW_KNOCKBACK);
    }

    @Override
    public void reload() {
    	FileConfiguration config = getConfig();
     	config.options().copyDefaults(true);

     	config.addDefault("Allowed", true);
     	config.addDefault("Name", "Knockback");
     	config.addDefault("ModifierItemName", "Enchanted TNT");
        config.addDefault("Description", "Knock back Enemies further!");
        config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Knockback-Modifier");
        config.addDefault("Color", "%GRAY%");
        config.addDefault("MaxLevel", 5);
     	config.addDefault("EnchantCost", 10);
     	config.addDefault("Recipe.Enabled", false);
        config.addDefault("OverrideLanguagesystem", false);

        ConfigurationManager.saveConfig(config);
        ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

     	init(Material.TNT, true);
    }

    @Override
    public boolean applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (!Modifier.checkAndAdd(p, tool, this, "knockback", isCommand)) {
            return false;
        }

        ItemMeta meta = tool.getItemMeta();

        if (meta != null) {
            if (ToolType.AXE.contains(tool.getType())) {
                meta.addEnchant(Enchantment.KNOCKBACK, modManager.getModLevel(tool, this), true);
            } else if (ToolType.BOW.contains(tool.getType()) || ToolType.CROSSBOW.contains(tool.getType())) {
                meta.addEnchant(Enchantment.ARROW_KNOCKBACK, modManager.getModLevel(tool, this), true);
            } else if (ToolType.SWORD.contains(tool.getType())) {
                meta.addEnchant(Enchantment.KNOCKBACK, modManager.getModLevel(tool, this), true);
            }

            tool.setItemMeta(meta);
        }

        return true;
    }
}

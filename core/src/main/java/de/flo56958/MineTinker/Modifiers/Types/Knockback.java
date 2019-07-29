package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Enchantable;
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

public class Knockback extends Modifier implements Enchantable {

    private static Knockback instance;

    public static Knockback instance() {
        synchronized (Knockback.class) {
            if (instance == null) instance = new Knockback();
        }
        return instance;
    }

    private Knockback() {
        super("Knockback", "Knockback.yml",
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.SWORD, ToolType.TRIDENT)),
                Main.getPlugin());
    }

    @Override
    public List<Enchantment> getAppliedEnchantments() {
        List<Enchantment> enchantments = new ArrayList<>();
        enchantments.add(Enchantment.KNOCKBACK);
        enchantments.add(Enchantment.ARROW_KNOCKBACK);

        return enchantments;
    }

    @Override
    public void reload() {
    	FileConfiguration config = getConfig();
     	config.options().copyDefaults(true);

     	config.addDefault("Allowed", true);
     	config.addDefault("Name", "Knockback");
     	config.addDefault("ModifierItemName", "Enhanced TNT");
        config.addDefault("Description", "Knockbacks Enemies further!");
        config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Knockback-Modifier");
        config.addDefault("Color", "%GRAY%");
        config.addDefault("MaxLevel", 5);
     	config.addDefault("EnchantCost", 10);
     	config.addDefault("Recipe.Enabled", false);
        
     	ConfigurationManager.saveConfig(config);
        ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

     	init(Material.TNT, true);
    }

    @Override
    public boolean applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (!Modifier.checkAndAdd(p, tool, this, "knockback", isCommand)) return false;

        ItemMeta meta = tool.getItemMeta();

        if (meta != null) {
            if (ToolType.AXE.contains(tool.getType())) {
                meta.addEnchant(Enchantment.KNOCKBACK, modManager.getModLevel(tool, this), true);
            } else if (ToolType.BOW.contains(tool.getType()) || ToolType.CROSSBOW.contains(tool.getType())) {
                meta.addEnchant(Enchantment.ARROW_KNOCKBACK, modManager.getModLevel(tool, this), true);
            } else if (ToolType.SWORD.contains(tool.getType())) {
                meta.addEnchant(Enchantment.KNOCKBACK, modManager.getModLevel(tool, this), true);
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

        if (meta != null) {
            meta.removeEnchant(Enchantment.KNOCKBACK);
            meta.removeEnchant(Enchantment.ARROW_KNOCKBACK);
            tool.setItemMeta(meta);
        }
    }

    @Override
    public void enchantItem(Player p, ItemStack item) {
        if (!p.hasPermission("minetinker.modifiers.knockback.craft")) return;
        _createModifierItem(getConfig(), p, this);
    }
}

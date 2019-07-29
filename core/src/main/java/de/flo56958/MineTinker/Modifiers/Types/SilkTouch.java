package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ModifierFailCause;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.ModifierFailEvent;
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

public class SilkTouch extends Modifier implements Enchantable {

    private static SilkTouch instance;

    public static SilkTouch instance() {
        synchronized (SilkTouch.class) {
            if (instance == null) instance = new SilkTouch();
        }
        return instance;
    }

    private SilkTouch() {
        super("Silk-Touch", "Silk-Touch.yml",
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.HOE, ToolType.PICKAXE, ToolType.SHOVEL, ToolType.SHEARS)),
                Main.getPlugin());
    }

    @Override
    public List<Enchantment> getAppliedEnchantments() {
        List<Enchantment> enchantments = new ArrayList<>();
        enchantments.add(Enchantment.SILK_TOUCH);

        return enchantments;
    }

    @Override
    public void reload() {
    	FileConfiguration config = getConfig();
    	config.options().copyDefaults(true);
    	
    	config.addDefault("Allowed", true);
    	config.addDefault("Name", "Silk-Touch");
    	config.addDefault("ModifierItemName", "Enhanced Cobweb");
        config.addDefault("Description", "Applies Silk-Touch!");
        config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Silk-Touch-Modifier");
        config.addDefault("Color", "%WHITE%");
        config.addDefault("MaxLevel", 1); //IF 2: Epic Spawners work with MT-SilkTouch
    	config.addDefault("EnchantCost", 10);
    	config.addDefault("Recipe.Enabled", false);
    	
    	ConfigurationManager.saveConfig(config);
        ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());
    	
        init(Material.COBWEB, true);
    }

    @Override
    public boolean applyMod(Player p, ItemStack tool, boolean isCommand) {

        if (modManager.hasMod(tool, AutoSmelt.instance())) {
            pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
            return false;
        }

        if (modManager.hasMod(tool, Luck.instance())) {
            pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
            return false;
        }

        if (!Modifier.checkAndAdd(p, tool, this, "silktouch", isCommand)) return false;

        ItemMeta meta = tool.getItemMeta();

        if (meta != null) {
            meta.addEnchant(Enchantment.SILK_TOUCH, modManager.getModLevel(tool, this), true);
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
            meta.removeEnchant(Enchantment.SILK_TOUCH);
            tool.setItemMeta(meta);
        }
    }

    @Override
    public void enchantItem(Player p, ItemStack item) {
        if (!p.hasPermission("minetinker.modifiers.silktouch.craft")) return;
        _createModifierItem(getConfig(), p, this);
    }
}

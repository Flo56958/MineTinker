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

public class Infinity extends Modifier implements Enchantable {

    private boolean compatibleWithEnder;

    private static Infinity instance;

    public static Infinity instance() {
        synchronized (Infinity.class) {
            if (instance == null) instance = new Infinity();
        }
        return instance;
    }

    //Infinity does not work on crossbows
    private Infinity() {
        super("Infinity", "Infinity.yml",
                new ArrayList<>(Arrays.asList(ToolType.BOW, ToolType.TRIDENT)),
                Main.getPlugin());
    }

    @Override
    public List<Enchantment> getAppliedEnchantments() {
        List<Enchantment> enchantments = new ArrayList<>();
        enchantments.add(Enchantment.ARROW_INFINITE);
        enchantments.add(Enchantment.LOYALTY);

        return enchantments;
    }

    @Override
    public void reload() {
    	FileConfiguration config = getConfig();
    	config.options().copyDefaults(true);
    	
    	config.addDefault("Allowed", true);
    	config.addDefault("Name", "Infinity");
    	config.addDefault("ModifierItemName", "Enchanted Arrow");
        config.addDefault("Description", "You only need one Arrow to shoot a bow and the Trident comes back!");
        config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Infinity-Modifier");
        config.addDefault("MaxLevel", 3); //higher values than 1 have no effect on Infinity
        config.addDefault("Color", "%WHITE%");
        config.addDefault("EnchantCost", 10);
    	config.addDefault("Recipe.Enabled", false);
    	//Check Ender.yml for Compatibility-option for Ender and Infinity
    	
    	ConfigurationManager.saveConfig(config);
        ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());
    	
        init(Material.ARROW, true);
        
        this.compatibleWithEnder = ConfigurationManager.getConfig(Ender.instance()).getBoolean("CompatibleWithInfinity");
    }

    @Override
    public boolean applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (!this.compatibleWithEnder) {
            if (modManager.hasMod(tool, Ender.instance())) {
                pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
                return false;
            }
        }

        if (modManager.hasMod(tool, Propelling.instance())) {
            pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
            return false;
        }

        if (!Modifier.checkAndAdd(p, tool, this, "infinity", isCommand)) return false;

        ItemMeta meta = tool.getItemMeta();

        if (meta != null) {
            if (ToolType.BOW.contains(tool.getType())) {
                meta.addEnchant(Enchantment.ARROW_INFINITE, modManager.getModLevel(tool, this), true);
            } else if (ToolType.TRIDENT.contains(tool.getType())) {
                meta.addEnchant(Enchantment.LOYALTY, modManager.getModLevel(tool, this), true);
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
            meta.removeEnchant(Enchantment.ARROW_INFINITE);
            meta.removeEnchant(Enchantment.LOYALTY);
            tool.setItemMeta(meta);
        }
    }

    @Override
    public void enchantItem(Player p, ItemStack item) {
        if (!p.hasPermission("minetinker.modifiers.infinity.craft")) return;
        _createModifierItem(getConfig(), p, this);
    }
}

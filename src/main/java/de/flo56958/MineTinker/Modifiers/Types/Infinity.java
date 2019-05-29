package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ModifierFailCause;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.ModifierFailEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Craftable;
import de.flo56958.MineTinker.Modifiers.Enchantable;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import de.flo56958.MineTinker.Utilities.Modifiers_Config;
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

public class Infinity extends Modifier implements Enchantable, Craftable {

    private boolean compatibleWithEnder;

    private static Infinity instance;

    public static Infinity instance() {
        if (instance == null) instance = new Infinity();
        return instance;
    }

    private Infinity() {
        super(ModifierType.INFINITY,
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
    	
    	String key = "Infinity";
    	config.addDefault(key + ".allowed", true);
    	config.addDefault(key + ".name", key);
    	config.addDefault(key + ".name_modifier", "Enchanted Arrow");
        config.addDefault(key + ".modifier_item", "ARROW"); //Needs to be a viable Material-Type
        config.addDefault(key + ".description", "You only need one Arrow to shoot a bowand the Trident comes back!");
        config.addDefault(key + ".description_modifier", "%WHITE%Modifier-Item for the Infinity-Modifier");
        config.addDefault(key + ".Color", "%WHITE%");
        config.addDefault(key + ".EnchantCost", 10);
    	config.addDefault(key + ".Recipe.Enabled", false);
    	//Check Ender.yml for Compatibility-option for Ender and Infinity
    	
    	ConfigurationManager.saveConfig(config);
    	
        init(config.getString(key + ".name"),
                "[" + config.getString(key + ".name_modifier") + "] " + config.getString(key + ".description"),
                ChatWriter.getColor(config.getString(key + ".Color")),
                1,
                modManager.createModifierItem(Material.getMaterial(config.getString(key + ".modifier_item")), ChatWriter.getColor(config.getString(key + ".Color")) + config.getString(key + ".name_modifier"), ChatWriter.addColors(config.getString(key + ".description_modifier")), this));
        
        this.compatibleWithEnder = ConfigurationManager.getConfig("Ender.yml").getBoolean("Ender.CompatibleWithInfinity");
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (!this.compatibleWithEnder) {
            if (modManager.hasMod(tool, modManager.getAdmin(ModifierType.ENDER))) {
                pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
                return null;
            }
        }

        if (modManager.hasMod(tool, modManager.getAdmin(ModifierType.PROPELLING))) {
            pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
            return null;
        }

        if (Modifier.checkAndAdd(p, tool, this, "infinity", isCommand) == null) {
            return null;
        }

        ItemMeta meta = tool.getItemMeta();

        if (meta != null) {
            if (ToolType.BOW.getMaterials().contains(tool.getType())) {
                meta.addEnchant(Enchantment.ARROW_INFINITE, modManager.getModLevel(tool, this), true);
            } else if (ToolType.TRIDENT.getMaterials().contains(tool.getType())) {
                meta.addEnchant(Enchantment.LOYALTY, modManager.getModLevel(tool, this), true);
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
            meta.removeEnchant(Enchantment.ARROW_INFINITE);
            meta.removeEnchant(Enchantment.LOYALTY);
            tool.setItemMeta(meta);
        }
    }

    @Override
    public void enchantItem(Player p, ItemStack item) {
        if (!p.hasPermission("minetinker.modifiers.infinity.craft")) return;
        _createModifierItem(getConfig(), p, this, "Infinity");
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Infinity", "Modifier_Infinity");
    }
    
    private static FileConfiguration getConfig() {
    	return ConfigurationManager.getConfig(Modifiers_Config.Infinity);
    }

    @Override
    public boolean isAllowed() {
    	return getConfig().getBoolean("Infinity.allowed");
    }
}

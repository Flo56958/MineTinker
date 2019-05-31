package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.data.ModifierFailCause;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.events.ModifierFailEvent;
import de.flo56958.minetinker.Main;
import de.flo56958.minetinker.modifiers.Craftable;
import de.flo56958.minetinker.modifiers.Enchantable;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utilities.ChatWriter;
import de.flo56958.minetinker.utilities.ConfigurationManager;
import de.flo56958.minetinker.utilities.Modifiers_Config;
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

public class SilkTouch extends Modifier implements Enchantable, Craftable {

    private static SilkTouch instance;

    public static SilkTouch instance() {
        synchronized (SilkTouch.class) {
            if (instance == null) instance = new SilkTouch();
        }
        return instance;
    }

    private SilkTouch() {
        super(ModifierType.SILK_TOUCH,
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
    	
    	String key = "Silk-Touch";
    	config.addDefault(key + ".allowed", true);
    	config.addDefault(key + ".name", key);
    	config.addDefault(key + ".name_modifier", "Enhanced Cobweb");
        config.addDefault(key + ".modifier_item", "COBWEB"); //Needs to be a viable Material-Type
        config.addDefault(key + ".description", "Applies Silk-Touch!");
        config.addDefault(key + ".description_modifier", "%WHITE%Modifier-Item for the Silk-Touch-Modifier");
        config.addDefault(key + ".Color", "%WHITE%");
        config.addDefault(key + ".MaxLevel", 1); //IF 2 Epic Spawners work with MT-SilkTouch
    	config.addDefault(key + ".EnchantCost", 10);
    	config.addDefault(key + ".Recipe.Enabled", false);
    	
    	ConfigurationManager.saveConfig(config);

        String name = config.getString(key + ".name");
        String modName = config.getString(key + ".name_modifier");
        String description = config.getString(key + ".description");
        String color = config.getString(key + ".Color");
        int maxLevel = config.getInt(key + ".MaxLevel");
        String modItem = config.getString(key + ".modifier_item");
        String modDescription = config.getString(key + ".description_modifier");

        if (name == null || modName == null || description == null || color == null || modItem == null || modDescription == null) return;

        init(name, "[" + modName + "] " + description, ChatWriter.getColor(color), maxLevel,
                modManager.createModifierItem(Material.getMaterial(modItem), ChatWriter.getColor(color) + modName,
                        ChatWriter.addColors(modDescription), this));

    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {

        if (modManager.get(ModifierType.AUTO_SMELT) != null) {
            if (modManager.hasMod(tool, modManager.get(ModifierType.AUTO_SMELT))) {
                pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
                return null;
            }
        }

        if (modManager.get(ModifierType.LUCK) != null) {
            if (modManager.hasMod(tool, modManager.get(ModifierType.LUCK))) {
                pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
                return null;
            }
        }

        if (Modifier.checkAndAdd(p, tool, this, "silktouch", isCommand) == null) {
            return null;
        }

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

        return tool;
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
        _createModifierItem(getConfig(), p, this, "Silk-Touch");
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Silk-Touch", "Modifier_SilkTouch");
    }
    
    private static FileConfiguration getConfig() {
    	return ConfigurationManager.getConfig(Modifiers_Config.Silk_Touch);
    }

    @Override
    public boolean isAllowed() {
    	return getConfig().getBoolean("Silk-Touch.allowed");
    }
}

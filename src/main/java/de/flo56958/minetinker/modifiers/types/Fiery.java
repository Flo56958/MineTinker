package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.data.ToolType;
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

public class Fiery extends Modifier implements Enchantable, Craftable {

    //TODO: Add Particle effect
    private static Fiery instance;

    public static Fiery instance() {
        synchronized (Fiery.class) {
            if (instance == null) instance = new Fiery();
        }
        return instance;
    }

    private Fiery() {
        super(ModifierType.FIERY,
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.SWORD)),
                Main.getPlugin());
    }

    @Override
    public List<Enchantment> getAppliedEnchantments() {
        List<Enchantment> enchantments = new ArrayList<>();
        enchantments.add(Enchantment.ARROW_FIRE);
        enchantments.add(Enchantment.FIRE_ASPECT);

        return enchantments;
    }

    @Override
    public void reload() {
    	FileConfiguration config = getConfig();
    	config.options().copyDefaults(true);
    	
    	String key = "Fiery";
    	config.addDefault(key + ".allowed", true);
    	config.addDefault(key + ".name", key);
    	config.addDefault(key + ".name_modifier", "Enchanted Blaze-Rod");
        config.addDefault(key + ".modifier_item", "BLAZE_ROD"); //Needs to be a viable Material-Type
        config.addDefault(key + ".description", "Inflames enemies!");
        config.addDefault(key + ".description_modifier", "%WHITE%Modifier-Item for the Fiery-Modifier");
        config.addDefault(key + ".Color", "%YELLOW%");
        config.addDefault(key + ".MaxLevel", 2);
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
        if (Modifier.checkAndAdd(p, tool, this, "fiery", isCommand) == null) {
            return null;
        }

        ItemMeta meta = tool.getItemMeta();

        if (meta != null) {
            if (ToolType.BOW.getMaterials().contains(tool.getType())) {
                meta.addEnchant(Enchantment.ARROW_FIRE, modManager.getModLevel(tool, this), true);
            } else if (ToolType.SWORD.getMaterials().contains(tool.getType())) {
                meta.addEnchant(Enchantment.FIRE_ASPECT, modManager.getModLevel(tool, this), true);
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
            meta.removeEnchant(Enchantment.FIRE_ASPECT);
            meta.removeEnchant(Enchantment.ARROW_FIRE);
            tool.setItemMeta(meta);
        }
    }

    @Override
    public void enchantItem(Player p, ItemStack item) {
        if (!p.hasPermission("minetinker.modifiers.fiery.craft")) return;
        _createModifierItem(getConfig(), p, this, "Fiery");
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Fiery", "Modifier_Fiery");
    }
    
    private static FileConfiguration getConfig() {
    	return ConfigurationManager.getConfig(Modifiers_Config.Fiery);
    }

    @Override
    public boolean isAllowed() {
    	return getConfig().getBoolean("Fiery.allowed");
    }
}

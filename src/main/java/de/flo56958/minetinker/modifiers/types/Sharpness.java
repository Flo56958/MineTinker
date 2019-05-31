package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.Main;
import de.flo56958.minetinker.modifiers.Craftable;
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

public class Sharpness extends Modifier implements Craftable {

    private static Sharpness instance;

    public static Sharpness instance() {
        synchronized (Sharpness.class) {
            if (instance == null) instance = new Sharpness();
        }
        return instance;
    }

    private Sharpness() {
        super(ModifierType.SHARPNESS,
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.SWORD, ToolType.TRIDENT)),
                Main.getPlugin());
    }

    @Override
    public List<Enchantment> getAppliedEnchantments() {
        List<Enchantment> enchantments = new ArrayList<>();
        enchantments.add(Enchantment.DAMAGE_ALL);
        enchantments.add(Enchantment.ARROW_DAMAGE);
        enchantments.add(Enchantment.IMPALING);

        return enchantments;
    }

    @Override
    public void reload() {
    	FileConfiguration config = getConfig();
    	config.options().copyDefaults(true);
    	
    	String key = "Sharpness";
    	config.addDefault(key + ".allowed", true);
    	config.addDefault(key + ".name", key);
    	config.addDefault(key + ".name_modifier", "Compressed Quartzblock");
        config.addDefault(key + ".modifier_item", "QUARTZ_BLOCK"); //Needs to be a viable Material-Type
        config.addDefault(key + ".description", "Weapon does additional damage!");
        config.addDefault(key + ".description_modifier", "%WHITE%Modifier-Item for the Sharpness-Modifier");
        config.addDefault(key + ".Color", "%WHITE%");
        config.addDefault(key + ".MaxLevel", 5);
    	config.addDefault(key + ".Recipe.Enabled", true);
    	config.addDefault(key + ".Recipe.Top", "QQQ");
    	config.addDefault(key + ".Recipe.Middle", "QQQ");
    	config.addDefault(key + ".Recipe.Bottom", "QQQ");
    	config.addDefault(key + ".Recipe.Materials.Q", "QUARTZ_BLOCK");
        
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
        if (Modifier.checkAndAdd(p, tool, this, "sharpness", isCommand) == null) {
            return null;
        }

        ItemMeta meta = tool.getItemMeta();

        if (meta != null) {
            if (ToolType.AXE.getMaterials().contains(tool.getType())) {
                meta.addEnchant(Enchantment.DAMAGE_ALL, modManager.getModLevel(tool, this), true);
            } else if (ToolType.BOW.getMaterials().contains(tool.getType())) {
                meta.addEnchant(Enchantment.ARROW_DAMAGE, modManager.getModLevel(tool, this), true);
            } else if (ToolType.SWORD.getMaterials().contains(tool.getType())) {
                meta.addEnchant(Enchantment.DAMAGE_ALL, modManager.getModLevel(tool, this), true);
            } else if (ToolType.TRIDENT.getMaterials().contains(tool.getType())) {
                meta.addEnchant(Enchantment.DAMAGE_ALL, modManager.getModLevel(tool, this), true);
                meta.addEnchant(Enchantment.IMPALING, modManager.getModLevel(tool, this), true);
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
            meta.removeEnchant(Enchantment.DAMAGE_ALL);
            meta.removeEnchant(Enchantment.ARROW_DAMAGE);
            meta.removeEnchant(Enchantment.IMPALING);

            tool.setItemMeta(meta);
        }
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Sharpness", "Modifier_Sharpness");
    }
    
    private static FileConfiguration getConfig() {
    	return ConfigurationManager.getConfig(Modifiers_Config.Sharpness);
    }

    @Override
    public boolean isAllowed() {
    	return getConfig().getBoolean("Sharpness.allowed");
    }
}
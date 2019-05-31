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
import java.util.Collections;
import java.util.List;

public class LightWeight extends Modifier implements Enchantable, Craftable {

    private static LightWeight instance;

    public static LightWeight instance() {
        synchronized (LightWeight.class) {
            if (instance == null) instance = new LightWeight();
        }
        return instance;
    }

    private LightWeight() {
        super(ModifierType.LIGHT_WEIGHT,
                new ArrayList<>(Collections.singletonList(ToolType.BOOTS)),
                Main.getPlugin());
    }

    @Override
    public List<Enchantment> getAppliedEnchantments() {
        List<Enchantment> enchantments = new ArrayList<>();
        enchantments.add(Enchantment.PROTECTION_FALL);

        return enchantments;
    }

    @Override
    public void reload() {
    	FileConfiguration config = getConfig();
    	config.options().copyDefaults(true);
    	
    	String key = "Light-Weight";
    	config.addDefault(key + ".allowed", true);
    	config.addDefault(key + ".name", key);
    	config.addDefault(key + ".name_modifier", "Enhanced Feather");
        config.addDefault(key + ".modifier_item", "FEATHER"); //Needs to be a viable Material-Type
        config.addDefault(key + ".description", "You fall like a feather - sort of...");
        config.addDefault(key + ".description_modifier", "%WHITE%Modifier-Item for the Light-Weight-Modifier");
        config.addDefault(key + ".Color", "%GRAY%");
        config.addDefault(key + ".MaxLevel", 3);
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
        if (Modifier.checkAndAdd(p, tool, this, "lightweight", isCommand) == null) {
            return null;
        }

        ItemMeta meta = tool.getItemMeta();

        if (meta != null) {
            meta.addEnchant(Enchantment.PROTECTION_FALL, modManager.getModLevel(tool, this), true);
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
            meta.removeEnchant(Enchantment.PROTECTION_FALL);
            tool.setItemMeta(meta);
        }
    }

    @Override
    public void enchantItem(Player p, ItemStack item) {
        if (!p.hasPermission("minetinker.modifiers.lightweight.craft")) return;
        _createModifierItem(getConfig(), p, this, "Light-Weight");
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Light-Weight", "Modifier_LightWeight");
    }
    
    private static FileConfiguration getConfig() {
    	return ConfigurationManager.getConfig(Modifiers_Config.Light_Weight);
    }

    @Override
    public boolean isAllowed() {
    	return getConfig().getBoolean("Light-Weight.allowed");
    }
}

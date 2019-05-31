package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Craftable;
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

public class Protecting extends Modifier implements Craftable {

    private static Protecting instance;

    public static Protecting instance() {
        synchronized (Protecting.class) {
            if (instance == null) instance = new Protecting();
        }
        return instance;
    }

    private Protecting() {
        super(ModifierType.PROTECTING,
                new ArrayList<>(Arrays.asList(ToolType.HELMET, ToolType.CHESTPLATE, ToolType.LEGGINGS, ToolType.BOOTS, ToolType.ELYTRA)),
                Main.getPlugin());
    }

    @Override
    public List<Enchantment> getAppliedEnchantments() {
        List<Enchantment> enchantments = new ArrayList<>();
        enchantments.add(Enchantment.PROTECTION_ENVIRONMENTAL);

        return enchantments;
    }

    @Override
    public void reload() {
    	FileConfiguration config = getConfig();
     	config.options().copyDefaults(true);
    	
     	String key = "Protecting";
     	config.addDefault(key + ".allowed", true);
     	config.addDefault(key + ".name", key);
     	config.addDefault(key + ".name_modifier", "Enriched Obsidian");
        config.addDefault(key + ".modifier_item", "OBSIDIAN"); //Needs to be a viable Material-Type
        config.addDefault(key + ".description", "Your armor protects you better against all damage!");
        config.addDefault(key + ".description_modifier", "%WHITE%Modifier-Item for the Protecting-Modifier");
        config.addDefault(key + ".Color", "%GRAY%");
        config.addDefault(key + ".MaxLevel", 5);
     	config.addDefault(key + ".Recipe.Enabled", true);
     	config.addDefault(key + ".Recipe.Top", "DID");
     	config.addDefault(key + ".Recipe.Middle", "IOI");
     	config.addDefault(key + ".Recipe.Bottom", "DID");
     	config.addDefault(key + ".Recipe.Materials.D", "DIAMOND");
     	config.addDefault(key + ".Recipe.Materials.I", "IRON_INGOT");
     	config.addDefault(key + ".Recipe.Materials.O", "OBSIDIAN");
         
     	ConfigurationManager.saveConfig(config);
     	
        init(config.getString(key + ".name"),
                 "[" + config.getString(key + ".name_modifier") + "] " + config.getString(key + ".description"),
                ChatWriter.getColor(config.getString(key + ".Color")),
                config.getInt(key + ".MaxLevel"),
                modManager.createModifierItem(Material.getMaterial(config.getString(key + ".modifier_item")), ChatWriter.getColor(config.getString(key + ".Color")) + config.getString(key + ".name_modifier"), ChatWriter.addColors(config.getString(key + ".description_modifier")), this));
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (Modifier.checkAndAdd(p, tool, this, "protecting", isCommand) == null) {
            return null;
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

        return tool;
    }

    @Override
    public void removeMod(ItemStack tool) {
        ItemMeta meta = tool.getItemMeta();

        if (meta != null) {
            meta.removeEnchant(Enchantment.PROTECTION_ENVIRONMENTAL);
            tool.setItemMeta(meta);
        }
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Protecting", "Modifier_Protecting");
    }
    
    private static FileConfiguration getConfig() {
    	return ConfigurationManager.getConfig(Modifiers_Config.Protecting);
    }

    @Override
    public boolean isAllowed() {
    	return getConfig().getBoolean("Protecting.allowed");
    }
}

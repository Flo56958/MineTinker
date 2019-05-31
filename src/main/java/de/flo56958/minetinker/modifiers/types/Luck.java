package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.data.ModifierFailCause;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.events.ModifierFailEvent;
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

public class Luck extends Modifier implements Craftable {

    private static Luck instance;

    public static Luck instance() {
        synchronized (Luck.class) {
            if (instance == null) instance = new Luck();
        }
        return instance;
    }

    private Luck() {
        super(ModifierType.LUCK,
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.HOE, ToolType.PICKAXE, ToolType.SHEARS,
                        ToolType.FISHINGROD, ToolType.SHOVEL, ToolType.SWORD, ToolType.TRIDENT)),
                Main.getPlugin());
    }

    @Override
    public List<Enchantment> getAppliedEnchantments() {
        List<Enchantment> enchantments = new ArrayList<>();
        enchantments.add(Enchantment.LOOT_BONUS_BLOCKS);
        enchantments.add(Enchantment.LOOT_BONUS_MOBS);
        enchantments.add(Enchantment.LUCK);

        return enchantments;
    }

    @Override
    public void reload() {
    	FileConfiguration config = getConfig();
    	config.options().copyDefaults(true);
    	
    	String key = "Luck";
    	config.addDefault(key + ".allowed", true);
    	config.addDefault(key + ".name", key);
    	config.addDefault(key + ".name_modifier", "Compressed Lapis-Block");
        config.addDefault(key + ".modifier_item", "LAPIS_BLOCK"); //Needs to be a viable Material-Type
        config.addDefault(key + ".description", "Get more loot from enemies and blocks!");
        config.addDefault(key + ".description_modifier", "%WHITE%Modifier-Item for the Luck-Modifier");
        config.addDefault(key + ".Color", "%BLUE%");
        config.addDefault(key + ".MaxLevel", 5);
    	config.addDefault(key + ".Recipe.Enabled", true);
    	config.addDefault(key + ".Recipe.Top", "LLL");
    	config.addDefault(key + ".Recipe.Middle", "LLL");
    	config.addDefault(key + ".Recipe.Bottom", "LLL");
    	config.addDefault(key + ".Recipe.Materials.L", "LAPIS_BLOCK");
    	
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
        if (modManager.get(ModifierType.SILK_TOUCH) != null) {
            if (modManager.hasMod(tool, modManager.get(ModifierType.SILK_TOUCH))) {
                pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
                return null;
            }
        }
        if (Modifier.checkAndAdd(p, tool, this, "luck", isCommand) == null) {
            return null;
        }

        ItemMeta meta = tool.getItemMeta();

        if (meta != null) {
            if (ToolType.AXE.getMaterials().contains(tool.getType())) {
                meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, modManager.getModLevel(tool, this), true);
                meta.addEnchant(Enchantment.LOOT_BONUS_MOBS, modManager.getModLevel(tool, this), true);
            } else if (ToolType.BOW.getMaterials().contains(tool.getType())) {
                meta.addEnchant(Enchantment.LOOT_BONUS_MOBS, modManager.getModLevel(tool, this), true);
            } else if (ToolType.HOE.getMaterials().contains(tool.getType())) {
                meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, modManager.getModLevel(tool, this), true);
            } else if (ToolType.PICKAXE.getMaterials().contains(tool.getType())) {
                meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, modManager.getModLevel(tool, this), true);
            } else if (ToolType.SHOVEL.getMaterials().contains(tool.getType())) {
                meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, modManager.getModLevel(tool, this), true);
            } else if (ToolType.SWORD.getMaterials().contains(tool.getType())) {
                meta.addEnchant(Enchantment.LOOT_BONUS_MOBS, modManager.getModLevel(tool, this), true);
            } else if (ToolType.SHEARS.getMaterials().contains(tool.getType())) {
                meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, modManager.getModLevel(tool, this), true);
            } else if (ToolType.FISHINGROD.getMaterials().contains(tool.getType())) {
                meta.addEnchant(Enchantment.LUCK, modManager.getModLevel(tool, this), true);
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
            meta.removeEnchant(Enchantment.LOOT_BONUS_BLOCKS);
            meta.removeEnchant(Enchantment.LOOT_BONUS_MOBS);
            meta.removeEnchant(Enchantment.LUCK);
            tool.setItemMeta(meta);
        }
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Luck", "Modifier_Luck");
    }
    
    private static FileConfiguration getConfig() {
    	return ConfigurationManager.getConfig(Modifiers_Config.Luck);
    }

    @Override
    public boolean isAllowed() {
    	return getConfig().getBoolean("Luck.allowed");
    }
}

package de.flo56958.MineTinker.Utilities;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.ToolUpgradeEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.ModManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.List;

public class ItemGenerator {

    private static final PluginManager pluginManager = Bukkit.getPluginManager();
    private static final FileConfiguration config = Main.getPlugin().getConfig();
    private static final ModManager modManager = ModManager.instance();

    public static String getDisplayName (ItemStack tool) {
        String name = tool.getItemMeta().getDisplayName();
        if (tool.getItemMeta().getDisplayName() == null || tool.getItemMeta().getDisplayName().equals("")) {
            name = tool.getType().toString();
        }
        return name;
    }

    public static ItemStack changeLore(ItemStack tool, List<String> lore) {
        ItemMeta meta = tool.getItemMeta();
        meta.setLore(lore);
        tool.setItemMeta(meta);
        return tool;
    }

    public static ItemStack changeItem(ItemStack tool, ItemMeta meta, List<String> lore) {
        meta.setLore(lore);
        tool.setItemMeta(meta);
        return tool;
    }

    public static ItemStack buildersWandCreator(Material m, String name, int amount) { //TODO: Modify to implement Modifiers
        ItemStack wand = new ItemStack(m, amount);
        ItemMeta meta = wand.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();
        meta.setDisplayName(name);
        lore.add(modManager.IDENTIFIER_BUILDERSWAND);
        meta.setLore(lore);
        wand.setItemMeta(meta);
        return wand;
    }

    public static ItemStack itemEnchanter(Material m, String name, int amount, Enchantment ench, int level) {
        ItemStack item = new ItemStack(m, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        item.addUnsafeEnchantment(ench, level);
        return item;
    }

    public static ItemStack itemUpgrader(ItemStack tool, ItemStack upgrade, Player p) {
        ItemMeta meta = tool.getItemMeta();
        String[] name = tool.getType().toString().split("_");
        if (name[0].toLowerCase().equals("wooden") && (
                upgrade.getType().equals(Material.ACACIA_PLANKS) ||
                upgrade.getType().equals(Material.BIRCH_PLANKS) ||
                upgrade.getType().equals(Material.DARK_OAK_PLANKS) ||
                upgrade.getType().equals(Material.JUNGLE_PLANKS) ||
                upgrade.getType().equals(Material.OAK_PLANKS) ||
                upgrade.getType().equals(Material.SPRUCE_PLANKS))) {
            pluginManager.callEvent(new ToolUpgradeEvent(p, tool, false));
            return null;
        } else if (name[0].toLowerCase().equals("stone") && upgrade.getType().equals(Material.COBBLESTONE)) {
            pluginManager.callEvent(new ToolUpgradeEvent(p, tool, false));
            return null;
        } else if (name[0].toLowerCase().equals("iron") && upgrade.getType().equals(Material.IRON_INGOT)) {
            pluginManager.callEvent(new ToolUpgradeEvent(p, tool, false));
            return null;
        } else if (name[0].toLowerCase().equals("gold") && upgrade.getType().equals(Material.GOLD_INGOT)) {
            pluginManager.callEvent(new ToolUpgradeEvent(p, tool, false));
            return null;
        } else if (name[0].toLowerCase().equals("diamond") && upgrade.getType().equals(Material.DIAMOND)) {
            pluginManager.callEvent(new ToolUpgradeEvent(p, tool, false));
            return null;
        } else if (name[0].toLowerCase().equals("leather") && upgrade.getType().equals(Material.LEATHER)) {
            pluginManager.callEvent(new ToolUpgradeEvent(p, tool, false));
            return null;
        } else if (name[0].toLowerCase().equals("turtle") && upgrade.getType().equals(Material.SCUTE)) {
            pluginManager.callEvent(new ToolUpgradeEvent(p, tool, false));
            return null;
        } else if (name[0].toLowerCase().equals("chainmail") && upgrade.getType().equals(Material.IRON_BARS)) {
            pluginManager.callEvent(new ToolUpgradeEvent(p, tool, false));
            return null;
        }
        if (ToolType.SWORD.getMaterials().contains(tool.getType())) {
            if ((upgrade.getType().equals(Material.ACACIA_PLANKS) ||
                    upgrade.getType().equals(Material.BIRCH_PLANKS) ||
                    upgrade.getType().equals(Material.DARK_OAK_PLANKS) ||
                    upgrade.getType().equals(Material.JUNGLE_PLANKS) ||
                    upgrade.getType().equals(Material.OAK_PLANKS) ||
                    upgrade.getType().equals(Material.SPRUCE_PLANKS))) {
                tool.setType(Material.WOODEN_SWORD);
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, true));
            } else if (upgrade.getType().equals(Material.COBBLESTONE)) {
                tool.setType(Material.STONE_SWORD);
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, true));
            } else if (upgrade.getType().equals(Material.IRON_INGOT)) {
                tool.setType(Material.IRON_SWORD);
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, true));
            } else if (upgrade.getType().equals(Material.GOLD_INGOT)) {
                tool.setType(Material.GOLDEN_SWORD);
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, true));
            } else if (upgrade.getType().equals(Material.DIAMOND)) {
                tool.setType(Material.DIAMOND_SWORD);
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, true));
            } else {
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, false));
                return null;
            }
        } else if (ToolType.PICKAXE.getMaterials().contains(tool.getType())) {
            if ((upgrade.getType().equals(Material.ACACIA_PLANKS) ||
                    upgrade.getType().equals(Material.BIRCH_PLANKS) ||
                    upgrade.getType().equals(Material.DARK_OAK_PLANKS) ||
                    upgrade.getType().equals(Material.JUNGLE_PLANKS) ||
                    upgrade.getType().equals(Material.OAK_PLANKS) ||
                    upgrade.getType().equals(Material.SPRUCE_PLANKS))) {
                tool.setType(Material.WOODEN_PICKAXE);
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, true));
            } else if (upgrade.getType().equals(Material.COBBLESTONE)) {
                tool.setType(Material.STONE_PICKAXE);
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, true));
            } else if (upgrade.getType().equals(Material.IRON_INGOT)) {
                tool.setType(Material.IRON_PICKAXE);
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, true));
            } else if (upgrade.getType().equals(Material.GOLD_INGOT)) {
                tool.setType(Material.GOLDEN_PICKAXE);
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, true));
            } else if (upgrade.getType().equals(Material.DIAMOND)) {
                tool.setType(Material.DIAMOND_PICKAXE);
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, true));
            } else {
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, false));
                return null;
            }
        } else if (ToolType.AXE.getMaterials().contains(tool.getType())) {
            if ((upgrade.getType().equals(Material.ACACIA_PLANKS) ||
                    upgrade.getType().equals(Material.BIRCH_PLANKS) ||
                    upgrade.getType().equals(Material.DARK_OAK_PLANKS) ||
                    upgrade.getType().equals(Material.JUNGLE_PLANKS) ||
                    upgrade.getType().equals(Material.OAK_PLANKS) ||
                    upgrade.getType().equals(Material.SPRUCE_PLANKS))) {
                tool.setType(Material.WOODEN_AXE);
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, true));
            } else if (upgrade.getType().equals(Material.COBBLESTONE)) {
                tool.setType(Material.STONE_AXE);
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, true));
            } else if (upgrade.getType().equals(Material.IRON_INGOT)) {
                tool.setType(Material.IRON_AXE);
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, true));
            } else if (upgrade.getType().equals(Material.GOLD_INGOT)) {
                tool.setType(Material.GOLDEN_AXE);
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, true));
            } else if (upgrade.getType().equals(Material.DIAMOND)) {
                tool.setType(Material.DIAMOND_AXE);
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, true));
            } else {
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, false));
                return null;
            }
        } else if (ToolType.SHOVEL.getMaterials().contains(tool.getType())) {
            if ((upgrade.getType().equals(Material.ACACIA_PLANKS) ||
                    upgrade.getType().equals(Material.BIRCH_PLANKS) ||
                    upgrade.getType().equals(Material.DARK_OAK_PLANKS) ||
                    upgrade.getType().equals(Material.JUNGLE_PLANKS) ||
                    upgrade.getType().equals(Material.OAK_PLANKS) ||
                    upgrade.getType().equals(Material.SPRUCE_PLANKS))) {
                tool.setType(Material.WOODEN_SHOVEL);
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, true));
            } else if (upgrade.getType().equals(Material.COBBLESTONE)) {
                tool.setType(Material.STONE_SHOVEL);
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, true));
            } else if (upgrade.getType().equals(Material.IRON_INGOT)) {
                tool.setType(Material.IRON_SHOVEL);
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, true));
            } else if (upgrade.getType().equals(Material.GOLD_INGOT)) {
                tool.setType(Material.GOLDEN_SHOVEL);
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, true));
            } else if (upgrade.getType().equals(Material.DIAMOND)) {
                tool.setType(Material.DIAMOND_SHOVEL);
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, true));
            } else {
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, false));
                return null;
            }
        } else if (ToolType.HOE.getMaterials().contains(tool.getType())) {
            if ((upgrade.getType().equals(Material.ACACIA_PLANKS) ||
                    upgrade.getType().equals(Material.BIRCH_PLANKS) ||
                    upgrade.getType().equals(Material.DARK_OAK_PLANKS) ||
                    upgrade.getType().equals(Material.JUNGLE_PLANKS) ||
                    upgrade.getType().equals(Material.OAK_PLANKS) ||
                    upgrade.getType().equals(Material.SPRUCE_PLANKS))) {
                tool.setType(Material.WOODEN_HOE);
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, true));
            } else if (upgrade.getType().equals(Material.COBBLESTONE)) {
                tool.setType(Material.STONE_HOE);
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, true));
            } else if (upgrade.getType().equals(Material.IRON_INGOT)) {
                tool.setType(Material.IRON_HOE);
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, true));
            } else if (upgrade.getType().equals(Material.GOLD_INGOT)) {
                tool.setType(Material.GOLDEN_HOE);
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, true));
            } else if (upgrade.getType().equals(Material.DIAMOND)) {
                tool.setType(Material.DIAMOND_HOE);
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, true));
            } else {
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, false));
                return null;
            }
        } else if (ToolType.HELMET.getMaterials().contains(tool.getType())) {
            if (upgrade.getType().equals(Material.LEATHER)) {
                tool.setType(Material.LEATHER_HELMET);
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, true));
            } else if (upgrade.getType().equals(Material.IRON_INGOT)) {
                tool.setType(Material.IRON_HELMET);
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, true));
            } else if (upgrade.getType().equals(Material.GOLD_INGOT)) {
                tool.setType(Material.GOLDEN_HELMET);
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, true));
            } else if (upgrade.getType().equals(Material.DIAMOND)) {
                tool.setType(Material.DIAMOND_HELMET);
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, true));
            } else if (upgrade.getType().equals(Material.SCUTE)) {
                tool.setType(Material.TURTLE_HELMET);
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, true));
            } else if (upgrade.getType().equals(Material.IRON_BARS)) {
                tool.setType(Material.CHAINMAIL_HELMET);
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, true));
            } else {
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, false));
                return null;
            }
        } else if (ToolType.CHESTPLATE.getMaterials().contains(tool.getType())) {
            if (upgrade.getType().equals(Material.LEATHER)) {
                tool.setType(Material.LEATHER_CHESTPLATE);
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, true));
            } else if (upgrade.getType().equals(Material.IRON_INGOT)) {
                tool.setType(Material.IRON_CHESTPLATE);
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, true));
            } else if (upgrade.getType().equals(Material.GOLD_INGOT)) {
                tool.setType(Material.GOLDEN_CHESTPLATE);
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, true));
            } else if (upgrade.getType().equals(Material.DIAMOND)) {
                tool.setType(Material.DIAMOND_CHESTPLATE);
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, true));
            } else if (upgrade.getType().equals(Material.IRON_BARS)) {
                tool.setType(Material.CHAINMAIL_CHESTPLATE);
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, true));
            } else {
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, false));
                return null;
            }
        } else if (ToolType.LEGGINGS.getMaterials().contains(tool.getType())) {
            if (upgrade.getType().equals(Material.LEATHER)) {
                tool.setType(Material.LEATHER_LEGGINGS);
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, true));
            } else if (upgrade.getType().equals(Material.IRON_INGOT)) {
                tool.setType(Material.IRON_LEGGINGS);
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, true));
            } else if (upgrade.getType().equals(Material.GOLD_INGOT)) {
                tool.setType(Material.GOLDEN_LEGGINGS);
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, true));
            } else if (upgrade.getType().equals(Material.DIAMOND)) {
                tool.setType(Material.DIAMOND_LEGGINGS);
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, true));
            } else if (upgrade.getType().equals(Material.IRON_BARS)) {
                tool.setType(Material.CHAINMAIL_LEGGINGS);
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, true));
            } else {
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, false));
                return null;
            }
        } else if (ToolType.BOOTS.getMaterials().contains(tool.getType())) {
            if (upgrade.getType().equals(Material.LEATHER)) {
                tool.setType(Material.LEATHER_BOOTS);
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, true));
            } else if (upgrade.getType().equals(Material.IRON_INGOT)) {
                tool.setType(Material.IRON_BOOTS);
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, true));
            } else if (upgrade.getType().equals(Material.GOLD_INGOT)) {
                tool.setType(Material.GOLDEN_BOOTS);
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, true));
            } else if (upgrade.getType().equals(Material.DIAMOND)) {
                tool.setType(Material.DIAMOND_BOOTS);
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, true));
            } else if (upgrade.getType().equals(Material.IRON_BARS)) {
                tool.setType(Material.CHAINMAIL_BOOTS);
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, true));
            } else {
                pluginManager.callEvent(new ToolUpgradeEvent(p, tool, false));
                return null;
            }
        }
        tool.setDurability((short) 0);
        tool.setItemMeta(meta);
        return tool;
    }

    public static List<String> createLore() {
        ArrayList<String> lore = new ArrayList<>();
        //lore.add(modManager.IDENTIFIER_TOOL);
        lore.add(modManager.LEVELLINE + "1");
        lore.add(modManager.EXPLINE + "0 / " + modManager.getNextLevelReq(1));
        lore.add(modManager.FREEMODIFIERSLOTS + config.getInt("StartingModifierSlots"));
        lore.add(modManager.MODIFIERSTART);
        return lore;
    }

    public static List<String> createLore(int level) {
        ArrayList<String> lore = new ArrayList<>();
        //lore.add(modManager.IDENTIFIER_TOOL);
        lore.add(modManager.LEVELLINE + level);
        lore.add(modManager.EXPLINE + modManager.getNextLevelReq(level - 1) + " / " + modManager.getNextLevelReq(level));
        lore.add(modManager.FREEMODIFIERSLOTS + (config.getInt("StartingModifierSlots") + (config.getInt("AddModifierSlotsPerLevel") * (level - 1))));
        lore.add(modManager.MODIFIERSTART);
        return lore;
    }
}
package de.flo56958.MineTinker.Utilities;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.ToolUpgradeEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;

public class ItemGenerator {

    private static final PluginManager pluginManager = Bukkit.getPluginManager();

    public static String getDisplayName (ItemStack tool) {
        String name ;

        if (tool.getItemMeta() == null || tool.getItemMeta().getDisplayName().equals("")) {
            name = tool.getType().toString();
        } else {
            name = tool.getItemMeta().getDisplayName();
        }

        return name;
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
            switch (upgrade.getType()) {
                case ACACIA_PLANKS:
                case BIRCH_PLANKS:
                case DARK_OAK_PLANKS:
                case JUNGLE_PLANKS:
                case OAK_PLANKS:
                case SPRUCE_PLANKS:
                    tool.setType(Material.WOODEN_SWORD);
                    break;
                case COBBLESTONE:
                    tool.setType(Material.STONE_SWORD);
                    break;
                case IRON_INGOT:
                    tool.setType(Material.IRON_SWORD);
                    break;
                case GOLD_INGOT:
                    tool.setType(Material.GOLDEN_SWORD);
                    break;
                case DIAMOND:
                    tool.setType(Material.DIAMOND_SWORD);
                    break;
                default:
                    pluginManager.callEvent(new ToolUpgradeEvent(p, tool, false));
                    return null;
            }
        } else if (ToolType.PICKAXE.getMaterials().contains(tool.getType())) {
            switch (upgrade.getType()) {
                case ACACIA_PLANKS:
                case BIRCH_PLANKS:
                case DARK_OAK_PLANKS:
                case JUNGLE_PLANKS:
                case OAK_PLANKS:
                case SPRUCE_PLANKS:
                    tool.setType(Material.WOODEN_PICKAXE);
                    break;
                case COBBLESTONE:
                    tool.setType(Material.STONE_PICKAXE);
                    break;
                case IRON_INGOT:
                    tool.setType(Material.IRON_PICKAXE);
                    break;
                case GOLD_INGOT:
                    tool.setType(Material.GOLDEN_PICKAXE);
                    break;
                case DIAMOND:
                    tool.setType(Material.DIAMOND_PICKAXE);
                    break;
                default:
                    pluginManager.callEvent(new ToolUpgradeEvent(p, tool, false));
                    return null;
            }
        } else if (ToolType.AXE.getMaterials().contains(tool.getType())) {
            switch (upgrade.getType()) {
                case ACACIA_PLANKS:
                case BIRCH_PLANKS:
                case DARK_OAK_PLANKS:
                case JUNGLE_PLANKS:
                case OAK_PLANKS:
                case SPRUCE_PLANKS:
                    tool.setType(Material.WOODEN_AXE);
                    break;
                case COBBLESTONE:
                    tool.setType(Material.STONE_AXE);
                    break;
                case IRON_INGOT:
                    tool.setType(Material.IRON_AXE);
                    break;
                case GOLD_INGOT:
                    tool.setType(Material.GOLDEN_AXE);
                    break;
                case DIAMOND:
                    tool.setType(Material.DIAMOND_AXE);
                    break;
                default:
                    pluginManager.callEvent(new ToolUpgradeEvent(p, tool, false));
                    return null;
            }
        } else if (ToolType.SHOVEL.getMaterials().contains(tool.getType())) {
            switch (upgrade.getType()) {
                case ACACIA_PLANKS:
                case BIRCH_PLANKS:
                case DARK_OAK_PLANKS:
                case JUNGLE_PLANKS:
                case OAK_PLANKS:
                case SPRUCE_PLANKS:
                    tool.setType(Material.WOODEN_SHOVEL);
                    break;
                case COBBLESTONE:
                    tool.setType(Material.STONE_SHOVEL);
                    break;
                case IRON_INGOT:
                    tool.setType(Material.IRON_SHOVEL);
                    break;
                case GOLD_INGOT:
                    tool.setType(Material.GOLDEN_SHOVEL);
                    break;
                case DIAMOND:
                    tool.setType(Material.DIAMOND_SHOVEL);
                    break;
                default:
                    pluginManager.callEvent(new ToolUpgradeEvent(p, tool, false));
                    return null;
            }
        } else if (ToolType.HOE.getMaterials().contains(tool.getType())) {
            switch (upgrade.getType()) {
                case ACACIA_PLANKS:
                case BIRCH_PLANKS:
                case DARK_OAK_PLANKS:
                case JUNGLE_PLANKS:
                case OAK_PLANKS:
                case SPRUCE_PLANKS:
                    tool.setType(Material.WOODEN_HOE);
                    break;
                case COBBLESTONE:
                    tool.setType(Material.STONE_HOE);
                    break;
                case IRON_INGOT:
                    tool.setType(Material.IRON_HOE);
                    break;
                case GOLD_INGOT:
                    tool.setType(Material.GOLDEN_HOE);
                    break;
                case DIAMOND:
                    tool.setType(Material.DIAMOND_HOE);
                    break;
                default:
                    pluginManager.callEvent(new ToolUpgradeEvent(p, tool, false));
                    return null;
            }
        } else if (ToolType.HELMET.getMaterials().contains(tool.getType())) {
            switch (upgrade.getType()) {
                case LEATHER:
                    tool.setType(Material.LEATHER_HELMET);
                    break;
                case IRON_INGOT:
                    tool.setType(Material.IRON_HELMET);
                    break;
                case GOLD_INGOT:
                    tool.setType(Material.GOLDEN_HELMET);
                    break;
                case DIAMOND:
                    tool.setType(Material.DIAMOND_HELMET);
                    break;
                case SCUTE:
                    tool.setType(Material.TURTLE_HELMET);
                    break;
                case IRON_BARS:
                    tool.setType(Material.CHAINMAIL_HELMET);
                    break;
                default:
                    pluginManager.callEvent(new ToolUpgradeEvent(p, tool, false));
                    return null;
            }
        } else if (ToolType.CHESTPLATE.getMaterials().contains(tool.getType())) {
            switch (upgrade.getType()) {
                case LEATHER:
                    tool.setType(Material.LEATHER_CHESTPLATE);
                    break;
                case IRON_INGOT:
                    tool.setType(Material.IRON_CHESTPLATE);
                    break;
                case GOLD_INGOT:
                    tool.setType(Material.GOLDEN_CHESTPLATE);
                    break;
                case DIAMOND:
                    tool.setType(Material.DIAMOND_CHESTPLATE);
                    break;
                case IRON_BARS:
                    tool.setType(Material.CHAINMAIL_CHESTPLATE);
                    break;
                default:
                    pluginManager.callEvent(new ToolUpgradeEvent(p, tool, false));
                    return null;
            }
        } else if (ToolType.LEGGINGS.getMaterials().contains(tool.getType())) {
            switch (upgrade.getType()) {
                case LEATHER:
                    tool.setType(Material.LEATHER_LEGGINGS);
                    break;
                case IRON_INGOT:
                    tool.setType(Material.IRON_LEGGINGS);
                    break;
                case GOLD_INGOT:
                    tool.setType(Material.GOLDEN_LEGGINGS);
                    break;
                case DIAMOND:
                    tool.setType(Material.DIAMOND_LEGGINGS);
                    break;
                case IRON_BARS:
                    tool.setType(Material.CHAINMAIL_LEGGINGS);
                    break;
                default:
                    pluginManager.callEvent(new ToolUpgradeEvent(p, tool, false));
                    return null;
            }
        } else if (ToolType.BOOTS.getMaterials().contains(tool.getType())) {
            switch (upgrade.getType()) {
                case LEATHER:
                    tool.setType(Material.LEATHER_BOOTS);
                    break;
                case IRON_INGOT:
                    tool.setType(Material.IRON_BOOTS);
                    break;
                case GOLD_INGOT:
                    tool.setType(Material.GOLDEN_BOOTS);
                    break;
                case DIAMOND:
                    tool.setType(Material.DIAMOND_BOOTS);
                    break;
                case IRON_BARS:
                    tool.setType(Material.CHAINMAIL_BOOTS);
                    break;
                default:
                    pluginManager.callEvent(new ToolUpgradeEvent(p, tool, false));
                    return null;
            }
        }

        if (meta instanceof Damageable) {
            Damageable dam = (Damageable) meta;
            dam.setDamage(0);
        }

        tool.setItemMeta(meta);
        return tool;
    }
}
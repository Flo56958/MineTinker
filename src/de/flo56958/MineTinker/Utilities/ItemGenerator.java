package de.flo56958.MineTinker.Utilities;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Data.Strings;
import de.flo56958.MineTinker.Main;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class ItemGenerator {

    public static String getDisplayName (ItemStack tool) {
        String name = tool.getItemMeta().getDisplayName();
        if (tool.getItemMeta().getDisplayName() == null || tool.getItemMeta().getDisplayName().equals("")) {
            name = tool.getType().toString();
        }
        return name;
    }

    public static ItemStack changeItem(ItemStack tool, ArrayList<String> lore) {
        ItemMeta meta = tool.getItemMeta();
        meta.setLore(lore);
        tool.setItemMeta(meta);
        return tool;
    }

    public static ItemStack buildersWandCreator(Material m, String name, int amount) {
        ItemStack wand = new ItemStack(m, amount);
        ItemMeta meta = wand.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();
        meta.setDisplayName(name);
        lore.add(Strings.IDENTIFIER_BUILDERSWAND);
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

    public static ItemStack ToolModifier(ItemStack tool, String modifier, Player p, boolean event) {
        ItemMeta meta = tool.getItemMeta();
        ArrayList<String> lore = (ArrayList<String>) meta.getLore();
        String slots[] = lore.get(3).split(" ");
        int slotsRemaining = Integer.parseInt(slots[3]);
        if (slotsRemaining != 0 || modifier.equals(Main.getPlugin().getConfig().getString("Modifiers.Extra-Modifier.name").toLowerCase()) || event) {
            if (modifier.equals(Main.getPlugin().getConfig().getString("Modifiers.Auto-Smelt.name").toLowerCase())) {
                return ModifierApply.AutoSmelt(p, tool, slotsRemaining, event);
            } else if (modifier.equals(Main.getPlugin().getConfig().getString("Modifiers.Beheading.name").toLowerCase())) {
                return ModifierApply.Beheading(p, tool, slotsRemaining, event);
            } else if (modifier.equals(Main.getPlugin().getConfig().getString("Modifiers.Ender.name").toLowerCase())) {
                return ModifierApply.Ender(p, tool, slotsRemaining, event);
            } else if (modifier.equals(Main.getPlugin().getConfig().getString("Modifiers.Extra-Modifier.name").toLowerCase())) {
                return ModifierApply.ExtraModifier(p, tool, slotsRemaining, event);
            } else if (modifier.equals(Main.getPlugin().getConfig().getString("Modifiers.Fiery.name").toLowerCase())) {
                return ModifierApply.Fiery(p, tool, slotsRemaining, event);
            } else if (modifier.equals(Main.getPlugin().getConfig().getString("Modifiers.Glowing.name").toLowerCase())) {
                return ModifierApply.Glowing(p, tool, slotsRemaining, event);
            } else if (modifier.equals(Main.getPlugin().getConfig().getString("Modifiers.Haste.name").toLowerCase())) {
                return ModifierApply.Haste(p, tool, slotsRemaining, event);
            } else if (modifier.equals(Main.getPlugin().getConfig().getString("Modifiers.Infinity.name").toLowerCase())) {
                return ModifierApply.Infinity(p, tool, slotsRemaining, event);
            } else if (modifier.equals(Main.getPlugin().getConfig().getString("Modifiers.Luck.name").toLowerCase())) {
                return ModifierApply.Luck(p, tool, slotsRemaining, event);
            } else if (modifier.equals(Main.getPlugin().getConfig().getString("Modifiers.Poisonous.name").toLowerCase())) {
                return ModifierApply.Poisonous(p, tool, slotsRemaining, event);
            } else if (modifier.equals(Main.getPlugin().getConfig().getString("Modifiers.Power.name").toLowerCase())) {
                return ModifierApply.Power(p, tool, slotsRemaining, event);
            } else if (modifier.equals(Main.getPlugin().getConfig().getString("Modifiers.Reinforced.name").toLowerCase())) {
                return ModifierApply.Reinforced(p, tool, slotsRemaining, event);
            } else if (modifier.equals(Main.getPlugin().getConfig().getString("Modifiers.Self-Repair.name").toLowerCase())) {
                return ModifierApply.SelfRepair(p, tool, slotsRemaining, event);
            } else if (modifier.equals(Main.getPlugin().getConfig().getString("Modifiers.Sharpness.name").toLowerCase())) {
                return ModifierApply.Sharpness(p, tool, slotsRemaining, event);
            } else if (modifier.equals(Main.getPlugin().getConfig().getString("Modifiers.Shulking.name").toLowerCase())) {
                return ModifierApply.Shulking(p, tool, slotsRemaining, event);
            } else if (modifier.equals(Main.getPlugin().getConfig().getString("Modifiers.Silk-Touch.name").toLowerCase())) {
                return ModifierApply.SilkTouch(p, tool, slotsRemaining, event);
            } else if (modifier.equals(Main.getPlugin().getConfig().getString("Modifiers.XP.name").toLowerCase())) {
                return ModifierApply.XP(p, tool, slotsRemaining, event);
            } else {
                return null;
            }
        } else {
            Events.Mod_NoSlots(p, tool, modifier);
            return null;
        }
    }

    public static ItemStack itemUpgrader(ItemStack tool, ItemStack upgrade, Player p) {
        ItemMeta meta = tool.getItemMeta();
        String[] name = tool.getType().toString().split("_");
        //<editor-fold desc="SAME SEARCH">
        if (name[0].toLowerCase().equals("wooden") && (
                upgrade.getType().equals(Material.ACACIA_PLANKS) ||
                upgrade.getType().equals(Material.BIRCH_PLANKS) ||
                upgrade.getType().equals(Material.DARK_OAK_PLANKS) ||
                upgrade.getType().equals(Material.JUNGLE_PLANKS) ||
                upgrade.getType().equals(Material.OAK_PLANKS) ||
                upgrade.getType().equals(Material.SPRUCE_PLANKS))) {
            Events.Upgrade_Fail(p, tool, "WOOD");
            return null;
        } else if (name[0].toLowerCase().equals("stone") && upgrade.getType().equals(Material.COBBLESTONE)) {
            Events.Upgrade_Fail(p, tool, "STONE");
            return null;
        } else if (name[0].toLowerCase().equals("iron") && upgrade.getType().equals(Material.IRON_INGOT)) {
            Events.Upgrade_Fail(p, tool, "IRON");
            return null;
        } else if (name[0].toLowerCase().equals("gold") && upgrade.getType().equals(Material.GOLD_INGOT)) {
            Events.Upgrade_Fail(p, tool, "GOLD");
            return null;
        } else if (name[0].toLowerCase().equals("diamond") && upgrade.getType().equals(Material.DIAMOND)) {
            Events.Upgrade_Fail(p, tool, "DIAMOND");
            return null;
        }
        //</editor-fold>
        if (Lists.SWORDS.contains(tool.getType().toString())) {
            //<editor-fold desc="SWORDS">
            if ((upgrade.getType().equals(Material.ACACIA_PLANKS) ||
                    upgrade.getType().equals(Material.BIRCH_PLANKS) ||
                    upgrade.getType().equals(Material.DARK_OAK_PLANKS) ||
                    upgrade.getType().equals(Material.JUNGLE_PLANKS) ||
                    upgrade.getType().equals(Material.OAK_PLANKS) ||
                    upgrade.getType().equals(Material.SPRUCE_PLANKS)) && Lists.SWORDS.contains("WOODEN_SWORD")) {
                tool.setType(Material.WOODEN_SWORD);
                Events.Upgrade_Success(p, tool, "WOOD");
            } else if (upgrade.getType().equals(Material.COBBLESTONE) && Lists.SWORDS.contains("STONE_SWORD")) {
                tool.setType(Material.STONE_SWORD);
                Events.Upgrade_Success(p, tool, "STONE");
            } else if (upgrade.getType().equals(Material.IRON_INGOT) && Lists.SWORDS.contains("IRON_SWORD")) {
                tool.setType(Material.IRON_SWORD);
                Events.Upgrade_Success(p, tool, "IRON");
            } else if (upgrade.getType().equals(Material.GOLD_INGOT) && Lists.SWORDS.contains("GOLDEN_SWORD")) {
                tool.setType(Material.GOLDEN_SWORD);
                Events.Upgrade_Success(p, tool, "GOLD");
            } else if (upgrade.getType().equals(Material.DIAMOND) && Lists.SWORDS.contains("DIAMOND_SWORD")) {
                tool.setType(Material.DIAMOND_SWORD);
                Events.Upgrade_Success(p, tool, "DIAMOND");
            } else {
                Events.Upgrade_Prohibited(p, tool);
                return null;
            }
            //</editor-fold>
        } else if (Lists.PICKAXES.contains(tool.getType().toString())) {
            //<editor-fold desc="PICKAXES">
            if ((upgrade.getType().equals(Material.ACACIA_PLANKS) ||
                    upgrade.getType().equals(Material.BIRCH_PLANKS) ||
                    upgrade.getType().equals(Material.DARK_OAK_PLANKS) ||
                    upgrade.getType().equals(Material.JUNGLE_PLANKS) ||
                    upgrade.getType().equals(Material.OAK_PLANKS) ||
                    upgrade.getType().equals(Material.SPRUCE_PLANKS)) && Lists.PICKAXES.contains("WOODEN_PICKAXE")) {
                tool.setType(Material.WOODEN_PICKAXE);
                Events.Upgrade_Success(p, tool, "WOOD");
            } else if (upgrade.getType().equals(Material.COBBLESTONE) && Lists.PICKAXES.contains("STONE_PICKAXE")) {
                tool.setType(Material.STONE_PICKAXE);
                Events.Upgrade_Success(p, tool, "STONE");
            } else if (upgrade.getType().equals(Material.IRON_INGOT) && Lists.PICKAXES.contains("IRON_PICKAXE")) {
                tool.setType(Material.IRON_PICKAXE);
                Events.Upgrade_Success(p, tool, "IRON");
            } else if (upgrade.getType().equals(Material.GOLD_INGOT) && Lists.PICKAXES.contains("GOLDEN_PICKAXE")) {
                tool.setType(Material.GOLDEN_PICKAXE);
                Events.Upgrade_Success(p, tool, "GOLD");
            } else if (upgrade.getType().equals(Material.DIAMOND) && Lists.PICKAXES.contains("DIAMOND_PICKAXE")) {
                tool.setType(Material.DIAMOND_PICKAXE);
                Events.Upgrade_Success(p, tool, "DIAMOND");
            } else {
                Events.Upgrade_Prohibited(p, tool);
                return null;
            }
            //</editor-fold>
        } else if (Lists.AXES.contains(tool.getType().toString())) {
            //<editor-fold desc="AXES">
            if ((upgrade.getType().equals(Material.ACACIA_PLANKS) ||
                    upgrade.getType().equals(Material.BIRCH_PLANKS) ||
                    upgrade.getType().equals(Material.DARK_OAK_PLANKS) ||
                    upgrade.getType().equals(Material.JUNGLE_PLANKS) ||
                    upgrade.getType().equals(Material.OAK_PLANKS) ||
                    upgrade.getType().equals(Material.SPRUCE_PLANKS)) && Lists.AXES.contains("WOODEN_AXE")) {
                tool.setType(Material.WOODEN_AXE);
                Events.Upgrade_Success(p, tool, "WOOD");
            } else if (upgrade.getType().equals(Material.COBBLESTONE) && Lists.AXES.contains("STONE_AXE")) {
                tool.setType(Material.STONE_AXE);
                Events.Upgrade_Success(p, tool, "STONE");
            } else if (upgrade.getType().equals(Material.IRON_INGOT) && Lists.AXES.contains("IRON_AXE")) {
                tool.setType(Material.IRON_AXE);
                Events.Upgrade_Success(p, tool, "IRON");
            } else if (upgrade.getType().equals(Material.GOLD_INGOT) && Lists.AXES.contains("GOLDEN_AXE")) {
                tool.setType(Material.GOLDEN_AXE);
                Events.Upgrade_Success(p, tool, "GOLD");
            } else if (upgrade.getType().equals(Material.DIAMOND) && Lists.AXES.contains("DIAMOND_AXE")) {
                tool.setType(Material.DIAMOND_AXE);
                Events.Upgrade_Success(p, tool, "DIAMOND");
            } else {
                Events.Upgrade_Prohibited(p, tool);
                return null;
            }
            //</editor-fold>
        } else if (Lists.SHOVELS.contains(tool.getType().toString())) {
            //<editor-fold desc="SHOVELS">
            if ((upgrade.getType().equals(Material.ACACIA_PLANKS) ||
                    upgrade.getType().equals(Material.BIRCH_PLANKS) ||
                    upgrade.getType().equals(Material.DARK_OAK_PLANKS) ||
                    upgrade.getType().equals(Material.JUNGLE_PLANKS) ||
                    upgrade.getType().equals(Material.OAK_PLANKS) ||
                    upgrade.getType().equals(Material.SPRUCE_PLANKS)) && Lists.SHOVELS.contains("WOODEN_SHOVEL")) {
                tool.setType(Material.WOODEN_SHOVEL);
                Events.Upgrade_Success(p, tool, "WOOD");
            } else if (upgrade.getType().equals(Material.COBBLESTONE) && Lists.SHOVELS.contains("STONE_SHOVEL")) {
                tool.setType(Material.STONE_SHOVEL);
                Events.Upgrade_Success(p, tool, "STONE");
            } else if (upgrade.getType().equals(Material.IRON_INGOT) && Lists.SHOVELS.contains("IRON_SHOVEL")) {
                tool.setType(Material.IRON_SHOVEL);
                Events.Upgrade_Success(p, tool, "IRON");
            } else if (upgrade.getType().equals(Material.GOLD_INGOT) && Lists.SHOVELS.contains("GOLDEN_SHOVEL")) {
                tool.setType(Material.GOLDEN_SHOVEL);
                Events.Upgrade_Success(p, tool, "GOLD");
            } else if (upgrade.getType().equals(Material.DIAMOND) && Lists.SHOVELS.contains("DIAMOND_SHOVEL")) {
                tool.setType(Material.DIAMOND_SHOVEL);
                Events.Upgrade_Success(p, tool, "DIAMOND");
            } else {
                Events.Upgrade_Prohibited(p, tool);
                return null;
            }
            //</editor-fold>
        } else if (Lists.HOES.contains(tool.getType().toString())) {
            //<editor-fold desc="HOES">
            if ((upgrade.getType().equals(Material.ACACIA_PLANKS) ||
                    upgrade.getType().equals(Material.BIRCH_PLANKS) ||
                    upgrade.getType().equals(Material.DARK_OAK_PLANKS) ||
                    upgrade.getType().equals(Material.JUNGLE_PLANKS) ||
                    upgrade.getType().equals(Material.OAK_PLANKS) ||
                    upgrade.getType().equals(Material.SPRUCE_PLANKS)) && Lists.HOES.contains("WOODEN_HOE")) {
                tool.setType(Material.WOODEN_HOE);
                Events.Upgrade_Success(p, tool, "WOOD");
            } else if (upgrade.getType().equals(Material.COBBLESTONE) && Lists.HOES.contains("STONE_HOE")) {
                tool.setType(Material.STONE_HOE);
                Events.Upgrade_Success(p, tool, "STONE");
            } else if (upgrade.getType().equals(Material.IRON_INGOT) && Lists.HOES.contains("IRON_HOE")) {
                tool.setType(Material.IRON_HOE);
                Events.Upgrade_Success(p, tool, "IRON");
            } else if (upgrade.getType().equals(Material.GOLD_INGOT) && Lists.HOES.contains("GOLDEN_HOE")) {
                tool.setType(Material.GOLDEN_HOE);
                Events.Upgrade_Success(p, tool, "GOLD");
            } else if (upgrade.getType().equals(Material.DIAMOND) && Lists.HOES.contains("DIAMOND_HOE")) {
                tool.setType(Material.DIAMOND_HOE);
                Events.Upgrade_Success(p, tool, "DIAMOND");
            } else {
                Events.Upgrade_Prohibited(p, tool);
                return null;
            }
            //</editor-fold>
        }
        tool.setDurability((short) 0);
        tool.setItemMeta(meta);
        return tool;
    }
}
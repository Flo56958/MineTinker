package de.flo56958.MineTinker.Utilities;

import de.flo56958.MineTinker.Data.Strings;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.ModManager;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemGenerator {

    private static final FileConfiguration config = Main.getPlugin().getConfig();
    private static final ModManager modManager = Main.getModManager();

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
        if (ToolType.SWORD.getMaterials().contains(tool.getType())) {
            if ((upgrade.getType().equals(Material.ACACIA_PLANKS) ||
                    upgrade.getType().equals(Material.BIRCH_PLANKS) ||
                    upgrade.getType().equals(Material.DARK_OAK_PLANKS) ||
                    upgrade.getType().equals(Material.JUNGLE_PLANKS) ||
                    upgrade.getType().equals(Material.OAK_PLANKS) ||
                    upgrade.getType().equals(Material.SPRUCE_PLANKS))) {
                tool.setType(Material.WOODEN_SWORD);
                Events.Upgrade_Success(p, tool, "WOOD");
            } else if (upgrade.getType().equals(Material.COBBLESTONE)) {
                tool.setType(Material.STONE_SWORD);
                Events.Upgrade_Success(p, tool, "STONE");
            } else if (upgrade.getType().equals(Material.IRON_INGOT)) {
                tool.setType(Material.IRON_SWORD);
                Events.Upgrade_Success(p, tool, "IRON");
            } else if (upgrade.getType().equals(Material.GOLD_INGOT)) {
                tool.setType(Material.GOLDEN_SWORD);
                Events.Upgrade_Success(p, tool, "GOLD");
            } else if (upgrade.getType().equals(Material.DIAMOND)) {
                tool.setType(Material.DIAMOND_SWORD);
                Events.Upgrade_Success(p, tool, "DIAMOND");
            } else {
                Events.Upgrade_Prohibited(p, tool);
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
                Events.Upgrade_Success(p, tool, "WOOD");
            } else if (upgrade.getType().equals(Material.COBBLESTONE)) {
                tool.setType(Material.STONE_PICKAXE);
                Events.Upgrade_Success(p, tool, "STONE");
            } else if (upgrade.getType().equals(Material.IRON_INGOT)) {
                tool.setType(Material.IRON_PICKAXE);
                Events.Upgrade_Success(p, tool, "IRON");
            } else if (upgrade.getType().equals(Material.GOLD_INGOT)) {
                tool.setType(Material.GOLDEN_PICKAXE);
                Events.Upgrade_Success(p, tool, "GOLD");
            } else if (upgrade.getType().equals(Material.DIAMOND)) {
                tool.setType(Material.DIAMOND_PICKAXE);
                Events.Upgrade_Success(p, tool, "DIAMOND");
            } else {
                Events.Upgrade_Prohibited(p, tool);
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
                Events.Upgrade_Success(p, tool, "WOOD");
            } else if (upgrade.getType().equals(Material.COBBLESTONE)) {
                tool.setType(Material.STONE_AXE);
                Events.Upgrade_Success(p, tool, "STONE");
            } else if (upgrade.getType().equals(Material.IRON_INGOT)) {
                tool.setType(Material.IRON_AXE);
                Events.Upgrade_Success(p, tool, "IRON");
            } else if (upgrade.getType().equals(Material.GOLD_INGOT)) {
                tool.setType(Material.GOLDEN_AXE);
                Events.Upgrade_Success(p, tool, "GOLD");
            } else if (upgrade.getType().equals(Material.DIAMOND)) {
                tool.setType(Material.DIAMOND_AXE);
                Events.Upgrade_Success(p, tool, "DIAMOND");
            } else {
                Events.Upgrade_Prohibited(p, tool);
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
                Events.Upgrade_Success(p, tool, "WOOD");
            } else if (upgrade.getType().equals(Material.COBBLESTONE)) {
                tool.setType(Material.STONE_SHOVEL);
                Events.Upgrade_Success(p, tool, "STONE");
            } else if (upgrade.getType().equals(Material.IRON_INGOT)) {
                tool.setType(Material.IRON_SHOVEL);
                Events.Upgrade_Success(p, tool, "IRON");
            } else if (upgrade.getType().equals(Material.GOLD_INGOT)) {
                tool.setType(Material.GOLDEN_SHOVEL);
                Events.Upgrade_Success(p, tool, "GOLD");
            } else if (upgrade.getType().equals(Material.DIAMOND)) {
                tool.setType(Material.DIAMOND_SHOVEL);
                Events.Upgrade_Success(p, tool, "DIAMOND");
            } else {
                Events.Upgrade_Prohibited(p, tool);
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
                Events.Upgrade_Success(p, tool, "WOOD");
            } else if (upgrade.getType().equals(Material.COBBLESTONE)) {
                tool.setType(Material.STONE_HOE);
                Events.Upgrade_Success(p, tool, "STONE");
            } else if (upgrade.getType().equals(Material.IRON_INGOT)) {
                tool.setType(Material.IRON_HOE);
                Events.Upgrade_Success(p, tool, "IRON");
            } else if (upgrade.getType().equals(Material.GOLD_INGOT)) {
                tool.setType(Material.GOLDEN_HOE);
                Events.Upgrade_Success(p, tool, "GOLD");
            } else if (upgrade.getType().equals(Material.DIAMOND)) {
                tool.setType(Material.DIAMOND_HOE);
                Events.Upgrade_Success(p, tool, "DIAMOND");
            } else {
                Events.Upgrade_Prohibited(p, tool);
                return null;
            }
        }
        tool.setDurability((short) 0);
        tool.setItemMeta(meta);
        return tool;
    }

    public static void createModifierItem(Player p, String modifier, ItemStack modItem) {
        if (p.getGameMode().equals(GameMode.CREATIVE)) {
            p.getLocation().getWorld().dropItemNaturally(p.getLocation(), modItem);
            if (config.getBoolean("Sound.OnEnchanting")) {
                p.playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 0.5F);
            }
            ChatWriter.log(false, p.getDisplayName() + " created a " + modifier + "-Modifiers in Creative!");
        } else if (p.getLevel() >= config.getInt("Modifiers." + modifier + ".EnchantCost")) {
            int amount = p.getInventory().getItemInMainHand().getAmount();
            int newLevel = p.getLevel() - config.getInt("Modifiers." + modifier + ".EnchantCost");
            p.setLevel(newLevel);
            p.getInventory().getItemInMainHand().setAmount(amount - 1);
            if (p.getInventory().addItem(modItem).size() != 0) { //adds items to (full) inventory
                p.getWorld().dropItem(p.getLocation(), modItem);
            } // no else as it gets added in if
            if (config.getBoolean("Sound.OnEnchanting")) {
                p.playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 0.5F);
            }
            ChatWriter.log(false, p.getDisplayName() + " created a " + modifier + "-Modifiers!");
        } else {
            ChatWriter.sendActionBar(p, ChatColor.RED + "" + config.getInt("Modifiers." + modifier + ".EnchantCost") + " levels required!");
            ChatWriter.log(false, p.getDisplayName() + " tried to create a " + modifier + "-Modifiers but had not enough levels!");
        }

    }

    public static List<String> createLore() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add(Strings.IDENTIFIER);
        lore.add(Strings.LEVELLINE + "1");
        lore.add(Strings.EXPLINE + "0 / " + modManager.getNextLevelReq(1));
        lore.add(Strings.FREEMODIFIERSLOTS + config.getInt("StartingModifierSlots"));
        lore.add(Strings.MODIFIERSTART);
        return lore;
    }

    public static List<String> createLore(int level) {
        ArrayList<String> lore = new ArrayList<>();
        lore.add(Strings.IDENTIFIER);
        lore.add(Strings.LEVELLINE + level);
        lore.add(Strings.EXPLINE + modManager.getNextLevelReq(level - 1) + " / " + modManager.getNextLevelReq(level));
        lore.add(Strings.FREEMODIFIERSLOTS + (config.getInt("StartingModifierSlots") + (config.getInt("AddModifierSlotsPerLevel") * (level - 1))));
        lore.add(Strings.MODIFIERSTART);
        return lore;
    }
}
package de.flo56958.MineTinker.Utilities;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Modifier;
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
        } else if (name[0].toLowerCase().equals("leather") && upgrade.getType().equals(Material.LEATHER)) {
            Events.Upgrade_Fail(p, tool, "LEATHER");
            return null;
        } else if (name[0].toLowerCase().equals("turtle") && upgrade.getType().equals(Material.SCUTE)) {
            Events.Upgrade_Fail(p, tool, "SCUTE");
            return null;
        } else if (name[0].toLowerCase().equals("chainmail") && upgrade.getType().equals(Material.IRON_BARS)) {
            Events.Upgrade_Fail(p, tool, "CHAIN");
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
        } else if (ToolType.HELMET.getMaterials().contains(tool.getType())) {
            if (upgrade.getType().equals(Material.LEATHER)) {
                tool.setType(Material.LEATHER_HELMET);
                Events.Upgrade_Success(p, tool, "LEATHER");
            } else if (upgrade.getType().equals(Material.IRON_INGOT)) {
                tool.setType(Material.IRON_HELMET);
                Events.Upgrade_Success(p, tool, "IRON");
            } else if (upgrade.getType().equals(Material.GOLD_INGOT)) {
                tool.setType(Material.GOLDEN_HELMET);
                Events.Upgrade_Success(p, tool, "GOLD");
            } else if (upgrade.getType().equals(Material.DIAMOND)) {
                tool.setType(Material.DIAMOND_HELMET);
                Events.Upgrade_Success(p, tool, "DIAMOND");
            } else if (upgrade.getType().equals(Material.SCUTE)) {
                tool.setType(Material.TURTLE_HELMET);
                Events.Upgrade_Success(p, tool, "DIAMOND");
            } else if (upgrade.getType().equals(Material.IRON_BARS)) {
                tool.setType(Material.CHAINMAIL_HELMET);
                Events.Upgrade_Success(p, tool, "DIAMOND");
            } else {
                Events.Upgrade_Prohibited(p, tool);
                return null;
            }
        } else if (ToolType.CHESTPLATE.getMaterials().contains(tool.getType())) {
            if (upgrade.getType().equals(Material.LEATHER)) {
                tool.setType(Material.LEATHER_CHESTPLATE);
                Events.Upgrade_Success(p, tool, "LEATHER");
            } else if (upgrade.getType().equals(Material.IRON_INGOT)) {
                tool.setType(Material.IRON_CHESTPLATE);
                Events.Upgrade_Success(p, tool, "IRON");
            } else if (upgrade.getType().equals(Material.GOLD_INGOT)) {
                tool.setType(Material.GOLDEN_CHESTPLATE);
                Events.Upgrade_Success(p, tool, "GOLD");
            } else if (upgrade.getType().equals(Material.DIAMOND)) {
                tool.setType(Material.DIAMOND_CHESTPLATE);
                Events.Upgrade_Success(p, tool, "DIAMOND");
            } else if (upgrade.getType().equals(Material.IRON_BARS)) {
                tool.setType(Material.CHAINMAIL_CHESTPLATE);
                Events.Upgrade_Success(p, tool, "DIAMOND");
            } else {
                Events.Upgrade_Prohibited(p, tool);
                return null;
            }
        } else if (ToolType.LEGGINGS.getMaterials().contains(tool.getType())) {
            if (upgrade.getType().equals(Material.LEATHER)) {
                tool.setType(Material.LEATHER_LEGGINGS);
                Events.Upgrade_Success(p, tool, "LEATHER");
            } else if (upgrade.getType().equals(Material.IRON_INGOT)) {
                tool.setType(Material.IRON_LEGGINGS);
                Events.Upgrade_Success(p, tool, "IRON");
            } else if (upgrade.getType().equals(Material.GOLD_INGOT)) {
                tool.setType(Material.GOLDEN_LEGGINGS);
                Events.Upgrade_Success(p, tool, "GOLD");
            } else if (upgrade.getType().equals(Material.DIAMOND)) {
                tool.setType(Material.DIAMOND_LEGGINGS);
                Events.Upgrade_Success(p, tool, "DIAMOND");
            } else if (upgrade.getType().equals(Material.IRON_BARS)) {
                tool.setType(Material.CHAINMAIL_LEGGINGS);
                Events.Upgrade_Success(p, tool, "DIAMOND");
            } else {
                Events.Upgrade_Prohibited(p, tool);
                return null;
            }
        } else if (ToolType.BOOTS.getMaterials().contains(tool.getType())) {
            if (upgrade.getType().equals(Material.LEATHER)) {
                tool.setType(Material.LEATHER_BOOTS);
                Events.Upgrade_Success(p, tool, "LEATHER");
            } else if (upgrade.getType().equals(Material.IRON_INGOT)) {
                tool.setType(Material.IRON_BOOTS);
                Events.Upgrade_Success(p, tool, "IRON");
            } else if (upgrade.getType().equals(Material.GOLD_INGOT)) {
                tool.setType(Material.GOLDEN_BOOTS);
                Events.Upgrade_Success(p, tool, "GOLD");
            } else if (upgrade.getType().equals(Material.DIAMOND)) {
                tool.setType(Material.DIAMOND_BOOTS);
                Events.Upgrade_Success(p, tool, "DIAMOND");
            } else if (upgrade.getType().equals(Material.IRON_BARS)) {
                tool.setType(Material.CHAINMAIL_BOOTS);
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

    public static void createModifierItem(Player p, Modifier mod, String modifier) {
        if (p.getGameMode().equals(GameMode.CREATIVE)) {
            p.getLocation().getWorld().dropItemNaturally(p.getLocation(), mod.getModItem());
            if (config.getBoolean("Sound.OnEnchanting")) {
                p.playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 0.5F);
            }
            ChatWriter.log(false, p.getDisplayName() + " created a " + mod.getName() + "-Modifiers in Creative!");
        } else if (p.getLevel() >= config.getInt("Modifiers." + modifier + ".EnchantCost")) {
            int amount = p.getInventory().getItemInMainHand().getAmount();
            int newLevel = p.getLevel() - config.getInt("Modifiers." + modifier + ".EnchantCost");
            p.setLevel(newLevel);
            p.getInventory().getItemInMainHand().setAmount(amount - 1);
            if (p.getInventory().addItem(mod.getModItem()).size() != 0) { //adds items to (full) inventory
                p.getWorld().dropItem(p.getLocation(), mod.getModItem());
            } // no else as it gets added in if
            if (config.getBoolean("Sound.OnEnchanting")) {
                p.playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 0.5F);
            }
            ChatWriter.log(false, p.getDisplayName() + " created a " + mod.getName() + "-Modifiers!");
        } else {
            ChatWriter.sendActionBar(p, ChatColor.RED + "" + config.getInt("Modifiers." + modifier + ".EnchantCost") + " levels required!");
            ChatWriter.log(false, p.getDisplayName() + " tried to create a " + mod.getName() + "-Modifiers but had not enough levels!");
        }

    }

    public static List<String> createLore() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add(modManager.IDENTIFIER_TOOL);
        lore.add(modManager.LEVELLINE + "1");
        lore.add(modManager.EXPLINE + "0 / " + modManager.getNextLevelReq(1));
        lore.add(modManager.FREEMODIFIERSLOTS + config.getInt("StartingModifierSlots"));
        lore.add(modManager.MODIFIERSTART);
        return lore;
    }

    public static List<String> createLore(int level) {
        ArrayList<String> lore = new ArrayList<>();
        lore.add(modManager.IDENTIFIER_TOOL);
        lore.add(modManager.LEVELLINE + level);
        lore.add(modManager.EXPLINE + modManager.getNextLevelReq(level - 1) + " / " + modManager.getNextLevelReq(level));
        lore.add(modManager.FREEMODIFIERSLOTS + (config.getInt("StartingModifierSlots") + (config.getInt("AddModifierSlotsPerLevel") * (level - 1))));
        lore.add(modManager.MODIFIERSTART);
        return lore;
    }
}
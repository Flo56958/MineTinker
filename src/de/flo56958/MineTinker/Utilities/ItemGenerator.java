package de.flo56958.MineTinker.Utilities;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Data.Strings;
import de.flo56958.MineTinker.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class ItemGenerator {

    public static ItemStack changeItem(ItemStack tool, ArrayList<String> lore) {
        ItemMeta meta = tool.getItemMeta();
        meta.setLore(lore);
        tool.setItemMeta(meta);
        return tool;
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

    public static ItemStack ToolModifier(ItemStack tool, String modifier, Player p) {
        ItemMeta meta = tool.getItemMeta();
        ArrayList<String> lore = (ArrayList<String>) meta.getLore();
        String slots[] = lore.get(3).split(" ");
        int slotsRemaining = Integer.parseInt(slots[3]);
        if (slotsRemaining != 0 || modifier.equals("Extra-Modifier")) {
            if (modifier.equals("Self-Repair")) {
                //<editor-fold desc="SELF-REPAIR">
                int index = 0;
                boolean hasSelfRepair = false;
                searchloop:
                for (int i = 1; i <= Main.getPlugin().getConfig().getInt("Modifiers.Self-Repair.MaxLevel"); i++) {
                    if (lore.contains(Strings.SELFREPAIR + i)) {
                        index = i;
                        hasSelfRepair = true;
                        break searchloop;
                    }
                }
                int loreIndex = 0;
                int level = 1 + index;
                if (level > Main.getPlugin().getConfig().getInt("Modifiers.Self-Repair.MaxLevel")) {
                    Events.Mod_MaxLevel(p, tool, ChatColor.GREEN + "Self-Repair");
                    return null;
                }
                if (hasSelfRepair) {
                    loreIndex = lore.indexOf(Strings.SELFREPAIR + index);
                }
                if (loreIndex != 0) {
                    lore.set(loreIndex, Strings.SELFREPAIR + level);
                } else {
                    lore.add(Strings.SELFREPAIR + level);
                }
                Events.Mod_AddMod(p, tool, ChatColor.GREEN + "Self-Repair " + level, slotsRemaining - 1);
                lore.set(3, Strings.FREEMODIFIERSLOTS + (slotsRemaining - 1));
                if (level == Main.getPlugin().getConfig().getInt("Modifiers.Self-Repair.MaxLevel")) {
                    meta.addEnchant(Enchantment.MENDING, 1, true);
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                }
                //</editor-fold>
            } else if (modifier.equals("Extra-Modifier")) {
                //<editor-fold desc="EXTRA-MODIFIER">
                int extra = Main.getPlugin().getConfig().getInt("Modifiers.Extra-Modifier.ExtraModifierGain");
                lore.set(3, Strings.FREEMODIFIERSLOTS + (slotsRemaining + extra));
                Events.Mod_AddMod(p, tool, ChatColor.WHITE + "" + extra + " extra Modifier-Slot", slotsRemaining + extra);
                //</editor-fold>
            } else if (modifier.equals("Haste")) {
                //<editor-fold desc="HASTE">
                if (Lists.AXES.contains(tool.getType().toString()) || Lists.SHOVELS.contains(tool.getType().toString()) || Lists.PICKAXES.contains(tool.getType().toString())) {
                    int index = 0;
                    boolean hasHaste = false;
                    searchloop:
                    for (int i = 1; i <= 5; i++) {
                        if (lore.contains(Strings.HASTE + i)) {
                            index = i;
                            hasHaste = true;
                            break searchloop;
                        }
                    }
                    int loreIndex = 0;
                    int level = 1 + index;
                    if (level > 5) {
                        Events.Mod_MaxLevel(p, tool, ChatColor.DARK_RED + "Haste");
                        return null;
                    }
                    if (hasHaste) {
                        loreIndex = lore.indexOf(Strings.HASTE + index);
                    }
                    if (loreIndex != 0) {
                        lore.set(loreIndex, Strings.HASTE + level);
                    } else {
                        lore.add(Strings.HASTE + level);
                    }
                    meta.addEnchant(Enchantment.DIG_SPEED, level, true);
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    Events.Mod_AddMod(p, tool, ChatColor.DARK_RED + "Haste " + level, slotsRemaining - 1);
                    lore.set(3, Strings.FREEMODIFIERSLOTS + (slotsRemaining - 1));
                } else {
                    return null;
                }
                //</editor-fold>
            } else if (modifier.equals("Reinforced")) {
                //<editor-fold desc="REINFORCED">
                int index = 0;
                boolean hasReinforced = false;
                searchloop:
                for (int i = 1; i <= 3; i++) {
                    if (lore.contains(Strings.REINFORCED + i)) {
                        index = i;
                        hasReinforced = true;
                        break searchloop;
                    }
                }
                int loreIndex = 0;
                int level = 1 + index;
                if (level > 3) {
                    Events.Mod_MaxLevel(p, tool, ChatColor.BLACK + "Reinforced");
                    return null;
                }
                if (hasReinforced) {
                    loreIndex = lore.indexOf(Strings.REINFORCED + index);
                }
                if (loreIndex != 0) {
                    lore.set(loreIndex, Strings.REINFORCED + level);
                } else {
                    lore.add(Strings.REINFORCED + level);
                }
                meta.addEnchant(Enchantment.DURABILITY, level, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                Events.Mod_AddMod(p, tool, ChatColor.BLACK + "Reinforced " + level, slotsRemaining - 1);
                lore.set(3, Strings.FREEMODIFIERSLOTS + (slotsRemaining - 1));
                //</editor-fold>
            } else if (modifier.equals("Sharpness")) {
                //<editor-fold desc="SHARPNESS">
                if (Lists.SWORDS.contains(tool.getType().toString())) {
                    int index = 0;
                    boolean hasSharpness = false;
                    searchloop:
                    for (int i = 1; i <= 5; i++) {
                        if (lore.contains(Strings.SHARPNESS + i)) {
                            index = i;
                            hasSharpness = true;
                            break searchloop;
                        }
                    }
                    int loreIndex = 0;
                    int level = 1 + index;
                    if (level > 5) {
                        Events.Mod_MaxLevel(p, tool, ChatColor.WHITE + "Sharpness");
                        return null;
                    }
                    if (hasSharpness) {
                        loreIndex = lore.indexOf(Strings.SHARPNESS + index);
                    }
                    if (loreIndex != 0) {
                        lore.set(loreIndex, Strings.SHARPNESS + level);
                    } else {
                        lore.add(Strings.SHARPNESS + level);
                    }
                    meta.addEnchant(Enchantment.DAMAGE_ALL, level, true);
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    Events.Mod_AddMod(p, tool, ChatColor.WHITE + "Sharpness " + level, slotsRemaining - 1);
                    lore.set(3, Strings.FREEMODIFIERSLOTS + (slotsRemaining - 1));
                } else {
                    return null;
                }
                //</editor-fold>
            }
        } else {
            Events.Mod_NoSlots(p, tool, modifier);
            return null;
        }
        meta.setLore(lore);
        tool.setItemMeta(meta);
        return tool;
    }

    public static ItemStack itemUpgrader(ItemStack tool, ItemStack upgrade, Player p) {
        ItemMeta meta = tool.getItemMeta();
        String[] name = tool.getType().toString().split("_");
        //<editor-fold desc="SAME SEARCH">
        if (name[0].toLowerCase().equals("wood") && upgrade.getType().equals(Material.WOOD)) {
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
            if (upgrade.getType().equals(Material.WOOD) && Lists.SWORDS.contains("WOOD_SWORD")) {
                tool.setType(Material.WOOD_SWORD);
                Events.Upgrade_Success(p, tool, "WOOD");
            } else if (upgrade.getType().equals(Material.COBBLESTONE) && Lists.SWORDS.contains("STONE_SWORD")) {
                tool.setType(Material.STONE_SWORD);
                Events.Upgrade_Success(p, tool, "STONE");
            } else if (upgrade.getType().equals(Material.IRON_INGOT) && Lists.SWORDS.contains("IRON_SWORD")) {
                tool.setType(Material.IRON_SWORD);
                Events.Upgrade_Success(p, tool, "IRON");
            } else if (upgrade.getType().equals(Material.GOLD_INGOT) && Lists.SWORDS.contains("GOLD_SWORD")) {
                tool.setType(Material.GOLD_SWORD);
                Events.Upgrade_Success(p, tool, "GOLD");
            } else if (upgrade.getType().equals(Material.COBBLESTONE) && Lists.SWORDS.contains("DIAMOND_SWORD")) {
                tool.setType(Material.DIAMOND_SWORD);
                Events.Upgrade_Success(p, tool, "DIAMOND");
            } else {
                Events.Upgrade_Prohibited(p, tool);
                return null;
            }
            //</editor-fold>
        } else if (Lists.PICKAXES.contains(tool.getType().toString())) {
            //<editor-fold desc="PICKAXES">
            if (upgrade.getType().equals(Material.WOOD) && Lists.PICKAXES.contains("WOOD_PICKAXE")) {
                tool.setType(Material.WOOD_PICKAXE);
                Events.Upgrade_Success(p, tool, "WOOD");
            } else if (upgrade.getType().equals(Material.COBBLESTONE) && Lists.PICKAXES.contains("STONE_PICKAXE")) {
                tool.setType(Material.STONE_PICKAXE);
                Events.Upgrade_Success(p, tool, "STONE");
            } else if (upgrade.getType().equals(Material.IRON_INGOT) && Lists.PICKAXES.contains("IRON_PICKAXE")) {
                tool.setType(Material.IRON_PICKAXE);
                Events.Upgrade_Success(p, tool, "IRON");
            } else if (upgrade.getType().equals(Material.GOLD_INGOT) && Lists.PICKAXES.contains("GOLD_PICKAXE")) {
                tool.setType(Material.GOLD_PICKAXE);
                Events.Upgrade_Success(p, tool, "GOLD");
            } else if (upgrade.getType().equals(Material.COBBLESTONE) && Lists.PICKAXES.contains("DIAMOND_PICKAXE")) {
                tool.setType(Material.DIAMOND_PICKAXE);
                Events.Upgrade_Success(p, tool, "DIAMOND");
            } else {
                Events.Upgrade_Prohibited(p, tool);
                return null;
            }
            //</editor-fold>
        } else if (Lists.AXES.contains(tool.getType().toString())) {
            //<editor-fold desc="AXES">
            if (upgrade.getType().equals(Material.WOOD) && Lists.AXES.contains("WOOD_AXE")) {
                tool.setType(Material.WOOD_AXE);
                Events.Upgrade_Success(p, tool, "WOOD");
            } else if (upgrade.getType().equals(Material.COBBLESTONE) && Lists.SWORDS.contains("STONE_AXE")) {
                tool.setType(Material.STONE_AXE);
                Events.Upgrade_Success(p, tool, "STONE");
            } else if (upgrade.getType().equals(Material.IRON_INGOT) && Lists.SWORDS.contains("IRON_AXE")) {
                tool.setType(Material.IRON_AXE);
                Events.Upgrade_Success(p, tool, "IRON");
            } else if (upgrade.getType().equals(Material.GOLD_INGOT) && Lists.SWORDS.contains("GOLD_AXE")) {
                tool.setType(Material.GOLD_AXE);
                Events.Upgrade_Success(p, tool, "GOLD");
            } else if (upgrade.getType().equals(Material.COBBLESTONE) && Lists.SWORDS.contains("DIAMOND_AXE")) {
                tool.setType(Material.DIAMOND_AXE);
                Events.Upgrade_Success(p, tool, "DIAMOND");
            } else {
                Events.Upgrade_Prohibited(p, tool);
                return null;
            }
            //</editor-fold>
        } else if (Lists.SHOVELS.contains(tool.getType().toString())) {
            //<editor-fold desc="SHOVELS">
            if (upgrade.getType().equals(Material.WOOD) && Lists.SHOVELS.contains("WOOD_SPADE")) {
                tool.setType(Material.WOOD_SPADE);
                Events.Upgrade_Success(p, tool, "WOOD");
            } else if (upgrade.getType().equals(Material.COBBLESTONE) && Lists.SHOVELS.contains("STONE_SPADE")) {
                tool.setType(Material.STONE_SPADE);
                Events.Upgrade_Success(p, tool, "STONE");
            } else if (upgrade.getType().equals(Material.IRON_INGOT) && Lists.SHOVELS.contains("IRON_SPADE")) {
                tool.setType(Material.IRON_SPADE);
                Events.Upgrade_Success(p, tool, "IRON");
            } else if (upgrade.getType().equals(Material.GOLD_INGOT) && Lists.SHOVELS.contains("GOLD_SPADE")) {
                tool.setType(Material.GOLD_SPADE);
                Events.Upgrade_Success(p, tool, "GOLD");
            } else if (upgrade.getType().equals(Material.COBBLESTONE) && Lists.SHOVELS.contains("DIAMOND_SPADE")) {
                tool.setType(Material.DIAMOND_SPADE);
                Events.Upgrade_Success(p, tool, "DIAMOND");
            } else {
                Events.Upgrade_Prohibited(p, tool);
                return null;
            }
            //</editor-fold>
        }
        tool.setItemMeta(meta);
        return tool;
    }
}
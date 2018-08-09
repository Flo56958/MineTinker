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
        if (slotsRemaining != 0 || modifier.equals("Extra-Modifier") || event) {
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
                    if (!event) {
                        Events.Mod_MaxLevel(p, tool, ChatColor.GREEN + Main.getPlugin().getConfig().getString("Modifiers.Self-Repair.name"));
                    }
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
                Events.Mod_AddMod(p, tool, ChatColor.GREEN + Main.getPlugin().getConfig().getString("Modifiers.Self-Repair.name") + " " + level, slotsRemaining - 1);
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
                        if (!event) {
                            Events.Mod_MaxLevel(p, tool, ChatColor.DARK_RED + Main.getPlugin().getConfig().getString("Modifiers.Haste.name"));
                        }
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
                    Events.Mod_AddMod(p, tool, ChatColor.DARK_RED + Main.getPlugin().getConfig().getString("Modifiers.Haste.name") + " " + level, slotsRemaining - 1);
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
                    if (!event) {
                        Events.Mod_MaxLevel(p, tool, ChatColor.GRAY + Main.getPlugin().getConfig().getString("Modifiers.Reinforced.name"));
                    }
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
                Events.Mod_AddMod(p, tool, ChatColor.GRAY + Main.getPlugin().getConfig().getString("Modifiers.Reinforced.name")+ " " + level, slotsRemaining - 1);
                lore.set(3, Strings.FREEMODIFIERSLOTS + (slotsRemaining - 1));
                //</editor-fold>
            } else if (modifier.equals("Sharpness")) {
                //<editor-fold desc="SHARPNESS">
                if (Lists.SWORDS.contains(tool.getType().toString()) || Lists.BOWS.contains(tool.getType().toString())) {
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
                        if (!event) {
                            Events.Mod_MaxLevel(p, tool, ChatColor.WHITE + Main.getPlugin().getConfig().getString("Modifiers.Sharpness.name"));
                        }
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
                    if (Lists.BOWS.contains(tool.getType().toString())) {
                        meta.addEnchant(Enchantment.ARROW_DAMAGE, level, true);
                    } else if (Lists.SWORDS.contains(tool.getType().toString())) {
                        meta.addEnchant(Enchantment.DAMAGE_ALL, level, true);
                    }
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    Events.Mod_AddMod(p, tool, ChatColor.WHITE + Main.getPlugin().getConfig().getString("Modifiers.Sharpness.name") + " " + level, slotsRemaining - 1);
                    lore.set(3, Strings.FREEMODIFIERSLOTS + (slotsRemaining - 1));
                } else {
                    return null;
                }
                //</editor-fold>
            } else if (modifier.equals("XP")) {
                //<editor-fold desc="XP">
                if (Lists.BOWS.contains(tool.getType().toString())) {
                    return null;
                }
                int index = 0;
                boolean hasXP = false;
                searchloop:
                for (int i = 1; i <= Main.getPlugin().getConfig().getInt("Modifiers.XP.MaxLevel"); i++) {
                    if (lore.contains(Strings.XP + i)) {
                        index = i;
                        hasXP = true;
                        break searchloop;
                    }
                }
                int loreIndex = 0;
                int level = 1 + index;
                if (level > Main.getPlugin().getConfig().getInt("Modifiers.XP.MaxLevel")) {
                    if (!event) {
                        Events.Mod_MaxLevel(p, tool, ChatColor.GREEN + Main.getPlugin().getConfig().getString("Modifiers.XP.name"));
                    }
                    return null;
                }
                if (hasXP) {
                    loreIndex = lore.indexOf(Strings.XP + index);
                }
                if (loreIndex != 0) {
                    lore.set(loreIndex, Strings.XP + level);
                } else {
                    lore.add(Strings.XP + level);
                }
                Events.Mod_AddMod(p, tool, ChatColor.GREEN + Main.getPlugin().getConfig().getString("Modifiers.XP.name") + " " + level, slotsRemaining - 1);
                lore.set(3, Strings.FREEMODIFIERSLOTS + (slotsRemaining - 1));
                //</editor-fold>
            } else if (modifier.equals("Auto-Smelt")) {
                //<editor-fold desc="AUTO-SMELT">
                if (lore.contains(Strings.SILKTOUCH)) {
                    if (!event) {
                        Events.ModAndSilk(p, Main.getPlugin().getConfig().getString("Modifiers.Auto-Smelt.name"));
                    }
                }
                if (Lists.PICKAXES.contains(tool.getType().toString()) ||
                    Lists.SHOVELS.contains(tool.getType().toString()) ||
                    Lists.AXES.contains(tool.getType().toString())) {
                    int index = 0;
                    boolean hasAutoSmelt = false;
                    searchloop:
                    for (int i = 1; i <= Main.getPlugin().getConfig().getInt("Modifiers.Auto-Smelt.MaxLevel"); i++) {
                        if (lore.contains(Strings.AUTOSMELT + i)) {
                            index = i;
                            hasAutoSmelt = true;
                            break searchloop;
                        }
                    }
                    int loreIndex = 0;
                    int level = 1 + index;
                    if (level > Main.getPlugin().getConfig().getInt("Modifiers.Auto-Smelt.MaxLevel")) {
                        if (!event) {
                            Events.Mod_MaxLevel(p, tool, ChatColor.YELLOW + Main.getPlugin().getConfig().getString("Modifiers.Auto-Smelt.name"));
                        }
                        return null;
                    }
                    if (hasAutoSmelt) {
                        loreIndex = lore.indexOf(Strings.AUTOSMELT + index);
                    }
                    if (loreIndex != 0) {
                        lore.set(loreIndex, Strings.AUTOSMELT + level);
                    } else {
                        lore.add(Strings.AUTOSMELT + level);
                    }
                    Events.Mod_AddMod(p, tool, ChatColor.YELLOW + Main.getPlugin().getConfig().getString("Modifiers.Auto-Smelt.name") + " " + level, slotsRemaining - 1);
                    lore.set(3, Strings.FREEMODIFIERSLOTS + (slotsRemaining - 1));
                } else {
                    return null;
                }
                //</editor-fold>
            } else if (modifier.equals("Power")) {
                //<editor-fold desc="POWER">
                if (Lists.PICKAXES.contains(tool.getType().toString()) ||
                        Lists.SHOVELS.contains(tool.getType().toString()) ||
                        Lists.AXES.contains(tool.getType().toString())) {
                    int index = 0;
                    boolean hasPower = false;
                    searchloop:
                    for (int i = 1; i <= 3; i++) {
                        if (lore.contains(Strings.POWER + i)) {
                            index = i;
                            hasPower = true;
                            break searchloop;
                        }
                    }
                    int loreIndex = 0;
                    int level = 1 + index;
                    if (level > 3) {
                        if (!event) {
                            Events.Mod_MaxLevel(p, tool, ChatColor.GREEN + Main.getPlugin().getConfig().getString("Modifiers.Power.name"));
                        }
                        return null;
                    }
                    if (hasPower) {
                        loreIndex = lore.indexOf(Strings.POWER + index);
                    }
                    if (loreIndex != 0) {
                        lore.set(loreIndex, Strings.POWER + level);
                    } else {
                        lore.add(Strings.POWER + level);
                    }
                    Events.Mod_AddMod(p, tool, ChatColor.GREEN + Main.getPlugin().getConfig().getString("Modifiers.Power.name") + " " + level, slotsRemaining - 1);
                    lore.set(3, Strings.FREEMODIFIERSLOTS + (slotsRemaining - 1));
                } else {
                    return null;
                }
                //</editor-fold>
            } else if (modifier.equals("Luck")) {
                //<editor-fold desc="LUCK">
                if (Lists.BOWS.contains(tool.getType().toString())) {
                    return null;
                }
                if (lore.contains(Strings.SILKTOUCH)) {
                    if (!event) {
                        Events.ModAndSilk(p, Main.getPlugin().getConfig().getString("Modifiers.Luck.name"));
                    }
                    return null;
                }
                int index = 0;
                boolean hasLuck = false;
                searchloop:
                for (int i = 1; i <= 3; i++) {
                    if (lore.contains(Strings.LUCK + i)) {
                        index = i;
                        hasLuck = true;
                        break searchloop;
                    }
                }
                int loreIndex = 0;
                int level = 1 + index;
                if (level > 3) {
                    if (!event) {
                        Events.Mod_MaxLevel(p, tool, ChatColor.BLUE + Main.getPlugin().getConfig().getString("Modifiers.Luck.name"));
                    }
                    return null;
                }
                if (hasLuck) {
                    loreIndex = lore.indexOf(Strings.LUCK + index);
                }
                if (loreIndex != 0) {
                    lore.set(loreIndex, Strings.LUCK + level);
                } else {
                    lore.add(Strings.LUCK + level);
                }
                if (Lists.SWORDS.contains(tool.getType().toString())) {
                    meta.addEnchant(Enchantment.LOOT_BONUS_MOBS, level, true);
                } else if (Lists.AXES.contains(tool.getType().toString()) || Lists.PICKAXES.contains(tool.getType().toString()) || Lists.SHOVELS.contains(tool.getType().toString()) || Lists.HOES.contains(tool.getType().toString())) {
                    meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, level, true);
                } else { return null; }
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                Events.Mod_AddMod(p, tool, ChatColor.BLUE + Main.getPlugin().getConfig().getString("Modifiers.Luck.name") + " " + level, slotsRemaining - 1);
                lore.set(3, Strings.FREEMODIFIERSLOTS + (slotsRemaining - 1));
                //</editor-fold>
            } else if (modifier.equals("Silk-Touch")) {
                //<editor-fold desc="SILK-TOUCH">
                if (lore.contains(Strings.SILKTOUCH)) {
                    if (!event) {
                        Events.Mod_MaxLevel(p, tool, ChatColor.WHITE + Main.getPlugin().getConfig().getString("Modifiers.Silk-Touch.name"));
                    }
                    return null;
                }
                for (int i = 1; i <= 3; i++) {
                    if (lore.contains(Strings.LUCK + i)) {
                        if (!event) {
                            Events.ModAndSilk(p, Main.getPlugin().getConfig().getString("Modifiers.Luck.name"));
                        }
                        return null;
                    }
                }
                for (int i = 1; i <= Main.getPlugin().getConfig().getInt("Modifiers.Auto-Smelt.MaxLevel"); i++) {
                    if (lore.contains(Strings.AUTOSMELT + i)) {
                        if (!event) {
                            Events.ModAndSilk(p, Main.getPlugin().getConfig().getString("Modifiers.Auto-Smelt.name"));
                        }
                        return null;
                    }
                }
                if (Lists.AXES.contains(tool.getType().toString()) || Lists.PICKAXES.contains(tool.getType().toString()) || Lists.SHOVELS.contains(tool.getType().toString())) {
                    meta.addEnchant(Enchantment.SILK_TOUCH, 1, true);
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    Events.Mod_AddMod(p, tool, ChatColor.WHITE + Main.getPlugin().getConfig().getString("Modifiers.Silk-Touch.name"), slotsRemaining - 1);
                    lore.set(3, Strings.FREEMODIFIERSLOTS + (slotsRemaining - 1));
                    lore.add(Strings.SILKTOUCH);
                } else {
                    return null;
                }
                //</editor-fold>
            } else if (modifier.equals("Fiery")) {
                //<editor-fold desc="FIERY">
                if (Lists.SWORDS.contains(tool.getType().toString())) {
                    int index = 0;
                    boolean hasFiery = false;
                    searchloop:
                    for (int i = 1; i <= 2; i++) {
                        if (lore.contains(Strings.FIERY + i)) {
                            index = i;
                            hasFiery = true;
                            break searchloop;
                        }
                    }
                    int loreIndex = 0;
                    int level = 1 + index;
                    if (level > 2) {
                        if (!event) {
                            Events.Mod_MaxLevel(p, tool, ChatColor.YELLOW + Main.getPlugin().getConfig().getString("Modifiers.Fiery.name"));
                        }
                        return null;
                    }
                    if (hasFiery) {
                        loreIndex = lore.indexOf(Strings.FIERY + index);
                    }
                    if (loreIndex != 0) {
                        lore.set(loreIndex, Strings.FIERY + level);
                    } else {
                        lore.add(Strings.FIERY + level);
                    }
                    meta.addEnchant(Enchantment.FIRE_ASPECT, level, true);
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    Events.Mod_AddMod(p, tool, ChatColor.YELLOW + Main.getPlugin().getConfig().getString("Modifiers.Fiery.name") + " " + level, slotsRemaining - 1);
                    lore.set(3, Strings.FREEMODIFIERSLOTS + (slotsRemaining - 1));
                } else if (Lists.BOWS.contains(tool.getType().toString())){
                    if (lore.contains(Strings.FIERY + 1)) {
                        if (!event) {
                            Events.Mod_MaxLevel(p, tool, ChatColor.YELLOW + Main.getPlugin().getConfig().getString("Modifiers.Fiery.name"));
                        }
                        return null;
                    }
                    lore.add(Strings.FIERY + 1);
                    meta.addEnchant(Enchantment.ARROW_FIRE, 1, true);
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    Events.Mod_AddMod(p, tool, ChatColor.YELLOW + Main.getPlugin().getConfig().getString("Modifiers.Fiery.name"), slotsRemaining - 1);
                    lore.set(3, Strings.FREEMODIFIERSLOTS + (slotsRemaining - 1));
                } else {
                    return null;
                }
                //</editor-fold>
            } else if (modifier.equals("Beheading")) {
                //<editor-fold desc="BEHEADING">
                if (Lists.SWORDS.contains(tool.getType().toString())) {
                    int index = 0;
                    boolean hasBeheading = false;
                    searchloop:
                    for (int i = 1; i <= Main.getPlugin().getConfig().getInt("Modifiers.Beheading.MaxLevel"); i++) {
                        if (lore.contains(Strings.BEHEADING + i)) {
                            index = i;
                            hasBeheading = true;
                            break searchloop;
                        }
                    }
                    int loreIndex = 0;
                    int level = 1 + index;
                    if (level > Main.getPlugin().getConfig().getInt("Modifiers.Beheading.MaxLevel")) {
                        if (!event) {
                            Events.Mod_MaxLevel(p, tool, ChatColor.DARK_GRAY + Main.getPlugin().getConfig().getString("Modifiers.Beheading.name"));
                        }
                        return null;
                    }
                    if (hasBeheading) {
                        loreIndex = lore.indexOf(Strings.BEHEADING + index);
                    }
                    if (loreIndex != 0) {
                        lore.set(loreIndex, Strings.BEHEADING + level);
                    } else {
                        lore.add(Strings.BEHEADING + level);
                    }
                    Events.Mod_AddMod(p, tool, ChatColor.DARK_GRAY + Main.getPlugin().getConfig().getString("Modifiers.Beheading.name") + " " + level, slotsRemaining - 1);
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
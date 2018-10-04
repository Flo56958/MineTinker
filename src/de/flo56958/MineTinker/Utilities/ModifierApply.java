package de.flo56958.MineTinker.Utilities;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Data.Strings;
import de.flo56958.MineTinker.Main;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

class ModifierApply {

    static ItemStack AutoSmelt(Player p, ItemStack tool, int slotsRemaining, boolean event) {
        if (!p.hasPermission("minetinker.modifiers.autosmelt.apply")) { return null; }
        ItemMeta meta = tool.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore.contains(Strings.SILKTOUCH)) {
            if (!event) {
                Events.IncompatibleMods(p, Main.getPlugin().getConfig().getString("Modifiers.Auto-Smelt.name"),  Main.getPlugin().getConfig().getString("Modifiers.Silk-Touch.name"));
            }
            return null;
        }
        if (Lists.PICKAXES.contains(tool.getType().toString()) ||
                Lists.SHOVELS.contains(tool.getType().toString()) ||
                Lists.AXES.contains(tool.getType().toString())) {
            int index = 0;
            boolean hasAutoSmelt = false;
            for (int i = 1; i <= Main.getPlugin().getConfig().getInt("Modifiers.Auto-Smelt.MaxLevel"); i++) {
                if (lore.contains(Strings.AUTOSMELT + i)) {
                    index = i;
                    hasAutoSmelt = true;
                    break;
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
            Events.Mod_AddMod(p, tool, ChatColor.YELLOW + Main.getPlugin().getConfig().getString("Modifiers.Auto-Smelt.name") + " " + level, slotsRemaining - 1, event);
            if (!event) {
                lore.set(3, Strings.FREEMODIFIERSLOTS + (slotsRemaining - 1));
            }
        } else {
            return null;
        }
        meta.setLore(lore);
        tool.setItemMeta(meta);
        return tool;
    }

    static ItemStack Beheading(Player p, ItemStack tool, int slotsRemaining, boolean event) {
        if (!p.hasPermission("minetinker.modifiers.beheading.apply")) { return null; }
        ItemMeta meta = tool.getItemMeta();
        List<String> lore = meta.getLore();
        if (Lists.SWORDS.contains(tool.getType().toString()) || Lists.BOWS.contains(tool.getType().toString())) {
            int index = 0;
            boolean hasBeheading = false;
            for (int i = 1; i <= Main.getPlugin().getConfig().getInt("Modifiers.Beheading.MaxLevel"); i++) {
                if (lore.contains(Strings.BEHEADING + i)) {
                    index = i;
                    hasBeheading = true;
                    break;
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
            Events.Mod_AddMod(p, tool, ChatColor.DARK_GRAY + Main.getPlugin().getConfig().getString("Modifiers.Beheading.name") + " " + level, slotsRemaining - 1, event);
            if (!event) {
                lore.set(3, Strings.FREEMODIFIERSLOTS + (slotsRemaining - 1));
            }
        } else {
            return null;
        }
        meta.setLore(lore);
        tool.setItemMeta(meta);
        return tool;
    }

    static ItemStack Ender(Player p, ItemStack tool, int slotsRemaining, boolean event) {
        if (!p.hasPermission("minetinker.modifiers.ender.apply")) { return null; }
        ItemMeta meta = tool.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore.contains(Strings.ENDER)) {
            if (!event) {
                Events.Mod_MaxLevel(p, tool, ChatColor.DARK_GREEN + Main.getPlugin().getConfig().getString("Modifiers.Ender.name"));
            }
            return null;
        }
        if (Lists.BOWS.contains(tool.getType().toString())) {
            Events.Mod_AddMod(p, tool, ChatColor.DARK_GREEN + Main.getPlugin().getConfig().getString("Modifiers.Ender.name"), slotsRemaining - 1, event);
            if (!event) {
                lore.set(3, Strings.FREEMODIFIERSLOTS + (slotsRemaining - 1));
            }
            lore.add(Strings.ENDER);
        } else {
            return null;
        }
        meta.setLore(lore);
        tool.setItemMeta(meta);
        return tool;
    }

    static ItemStack ExtraModifier(Player p, ItemStack tool, int slotsRemaining, boolean event) {
        if (!p.hasPermission("minetinker.modifiers.extramodifier.apply")) { return null; }
        ItemMeta meta = tool.getItemMeta();
        List<String> lore = meta.getLore();

        int extra = Main.getPlugin().getConfig().getInt("Modifiers.Extra-Modifier.ExtraModifierGain");
        lore.set(3, Strings.FREEMODIFIERSLOTS + (slotsRemaining + extra));
        Events.Mod_AddMod(p, tool, ChatColor.WHITE + "" + extra + " extra Modifier-Slot", slotsRemaining + extra, event);

        meta.setLore(lore);
        tool.setItemMeta(meta);
        return tool;
    }

    static ItemStack Fiery(Player p, ItemStack tool, int slotsRemaining, boolean event) {
        if (!p.hasPermission("minetinker.modifiers.fiery.apply")) { return null; }
        ItemMeta meta = tool.getItemMeta();
        List<String> lore = meta.getLore();

        if (Lists.SWORDS.contains(tool.getType().toString()) || Lists.BOWS.contains(tool.getType().toString())) {
            int index = 0;
            boolean hasFiery = false;
            for (int i = 1; i <= Main.getPlugin().getConfig().getInt("Modifiers.Fiery.MaxLevel"); i++) {
                if (lore.contains(Strings.FIERY + i)) {
                    index = i;
                    hasFiery = true;
                    break;
                }
            }
            int loreIndex = 0;
            int level = 1 + index;
            if (level > Main.getPlugin().getConfig().getInt("Modifiers.Fiery.MaxLevel")) {
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
            if (Lists.BOWS.contains(tool.getType().toString())) {
                meta.addEnchant(Enchantment.ARROW_FIRE, level, true);
            } else if (Lists.SWORDS.contains(tool.getType().toString())) {
                meta.addEnchant(Enchantment.FIRE_ASPECT, level, true);
            }
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            Events.Mod_AddMod(p, tool, ChatColor.YELLOW + Main.getPlugin().getConfig().getString("Modifiers.Fiery.name") + " " + level, slotsRemaining - 1, event);
            if (!event) {
                lore.set(3, Strings.FREEMODIFIERSLOTS + (slotsRemaining - 1));
            }
        } else {
            return null;
        }

        meta.setLore(lore);
        tool.setItemMeta(meta);
        return tool;
    }

    static ItemStack Glowing(Player p, ItemStack tool, int slotsRemaining, boolean event) {
        if (!p.hasPermission("minetinker.modifiers.glowing.apply")) { return null; }
        ItemMeta meta = tool.getItemMeta();
        List<String> lore = meta.getLore();

        if (lore.contains(Strings.GLOWING)) {
            if (!event) {
                Events.Mod_MaxLevel(p, tool, ChatColor.YELLOW + Main.getPlugin().getConfig().getString("Modifiers.Glowing.name"));
            }
            return null;
        }
        if (Lists.BOWS.contains(tool.getType().toString()) || Lists.SWORDS.contains(tool.getType().toString())) {
            Events.Mod_AddMod(p, tool, ChatColor.YELLOW + Main.getPlugin().getConfig().getString("Modifiers.Glowing.name"), slotsRemaining - 1, event);
            if (!event) {
                lore.set(3, Strings.FREEMODIFIERSLOTS + (slotsRemaining - 1));
            }
            lore.add(Strings.GLOWING);
        } else {
            return null;
        }

        meta.setLore(lore);
        tool.setItemMeta(meta);
        return tool;
    }

    static ItemStack Haste(Player p, ItemStack tool, int slotsRemaining, boolean event) {
        if (!p.hasPermission("minetinker.modifiers.haste.apply")) { return null; }
        ItemMeta meta = tool.getItemMeta();
        List<String> lore = meta.getLore();

        if (Lists.AXES.contains(tool.getType().toString()) || Lists.SHOVELS.contains(tool.getType().toString()) || Lists.PICKAXES.contains(tool.getType().toString())) {
            int index = 0;
            boolean hasHaste = false;
            for (int i = 1; i <= Main.getPlugin().getConfig().getInt("Modifiers.Haste.MaxLevel"); i++) {
                if (lore.contains(Strings.HASTE + i)) {
                    index = i;
                    hasHaste = true;
                    break;
                }
            }
            int loreIndex = 0;
            int level = 1 + index;
            if (level > Main.getPlugin().getConfig().getInt("Modifiers.Haste.MaxLevel")) {
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
            Events.Mod_AddMod(p, tool, ChatColor.DARK_RED + Main.getPlugin().getConfig().getString("Modifiers.Haste.name") + " " + level, slotsRemaining - 1, event);
            if (!event) {
                lore.set(3, Strings.FREEMODIFIERSLOTS + (slotsRemaining - 1));
            }
        } else {
            return null;
        }

        meta.setLore(lore);
        tool.setItemMeta(meta);
        return tool;
    }

    static ItemStack Knockback(Player p, ItemStack tool, int slotsRemaining, boolean event) {
        if (!p.hasPermission("minetinker.modifiers.knockback.apply")) { return null; }
        ItemMeta meta = tool.getItemMeta();
        List<String> lore = meta.getLore();

        if (Lists.SWORDS.contains(tool.getType().toString()) || Lists.BOWS.contains(tool.getType().toString())) {
            int index = 0;
            boolean hasKnockback = false;
            for (int i = 1; i <= Main.getPlugin().getConfig().getInt("Modifiers.Knockback.MaxLevel"); i++) {
                if (lore.contains(Strings.KNOCKBACK + i)) {
                    index = i;
                    hasKnockback = true;
                    break;
                }
            }
            int loreIndex = 0;
            int level = 1 + index;
            if (level > Main.getPlugin().getConfig().getInt("Modifiers.Knockback.MaxLevel")) {
                if (!event) {
                    Events.Mod_MaxLevel(p, tool, ChatColor.GRAY + Main.getPlugin().getConfig().getString("Modifiers.Knockback.name"));
                }
                return null;
            }
            if (hasKnockback) {
                loreIndex = lore.indexOf(Strings.KNOCKBACK + index);
            }
            if (loreIndex != 0) {
                lore.set(loreIndex, Strings.KNOCKBACK + level);
            } else {
                lore.add(Strings.KNOCKBACK + level);
            }
            if (Lists.BOWS.contains(tool.getType().toString())) {
                meta.addEnchant(Enchantment.ARROW_KNOCKBACK, level, true);
            } else if (Lists.SWORDS.contains(tool.getType().toString())) {
                meta.addEnchant(Enchantment.KNOCKBACK, level, true);
            }
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            Events.Mod_AddMod(p, tool, ChatColor.GRAY + Main.getPlugin().getConfig().getString("Modifiers.Knockback.name") + " " + level, slotsRemaining - 1, event);
            if (!event) {
                lore.set(3, Strings.FREEMODIFIERSLOTS + (slotsRemaining - 1));
            }
        } else {
            return null;
        }

        meta.setLore(lore);
        tool.setItemMeta(meta);
        return tool;
    }

    static ItemStack Infinity(Player p, ItemStack tool, int slotsRemaining, boolean event) {
        if (!p.hasPermission("minetinker.modifiers.infinity.apply")) { return null; }
        ItemMeta meta = tool.getItemMeta();
        List<String> lore = meta.getLore();

        if (lore.contains(Strings.INFINITY)) {
            if (!event) {
                Events.Mod_MaxLevel(p, tool, ChatColor.WHITE + Main.getPlugin().getConfig().getString("Modifiers.Infinity.name"));
            }
            return null;
        }
        if (Lists.BOWS.contains(tool.getType().toString())) {
            meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            Events.Mod_AddMod(p, tool, ChatColor.WHITE + Main.getPlugin().getConfig().getString("Modifiers.Infinity.name"), slotsRemaining - 1, event);
            if (!event) {
                lore.set(3, Strings.FREEMODIFIERSLOTS + (slotsRemaining - 1));
            }
            lore.add(Strings.INFINITY);
        } else {
            return null;
        }

        meta.setLore(lore);
        tool.setItemMeta(meta);
        return tool;
    }

    static ItemStack Luck(Player p, ItemStack tool, int slotsRemaining, boolean event) {
        if (!p.hasPermission("minetinker.modifiers.luck.apply")) { return null; }
        ItemMeta meta = tool.getItemMeta();
        List<String> lore = meta.getLore();

        if (lore.contains(Strings.SILKTOUCH)) {
            if (!event) {
                Events.IncompatibleMods(p, Main.getPlugin().getConfig().getString("Modifiers.Luck.name"),  Main.getPlugin().getConfig().getString("Modifiers.Silk-Touch.name"));
            }
            return null;
        }
        int index = 0;
        boolean hasLuck = false;
        for (int i = 1; i <= Main.getPlugin().getConfig().getInt("Modifiers.Luck.MaxLevel"); i++) {
            if (lore.contains(Strings.LUCK + i)) {
                index = i;
                hasLuck = true;
                break;
            }
        }
        int loreIndex = 0;
        int level = 1 + index;
        if (level > Main.getPlugin().getConfig().getInt("Modifiers.Luck.MaxLevel")) {
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
        if (Lists.SWORDS.contains(tool.getType().toString()) || Lists.BOWS.contains(tool.getType().toString())) {
            meta.addEnchant(Enchantment.LOOT_BONUS_MOBS, level, true);
        } else if (Lists.AXES.contains(tool.getType().toString()) || Lists.PICKAXES.contains(tool.getType().toString()) || Lists.SHOVELS.contains(tool.getType().toString()) || Lists.HOES.contains(tool.getType().toString())) {
            meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, level, true);
        } else {
            return null;
        }
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        Events.Mod_AddMod(p, tool, ChatColor.BLUE + Main.getPlugin().getConfig().getString("Modifiers.Luck.name") + " " + level, slotsRemaining - 1, event);
        if (!event) {
            lore.set(3, Strings.FREEMODIFIERSLOTS + (slotsRemaining - 1));
        }

        meta.setLore(lore);
        tool.setItemMeta(meta);
        return tool;
    }

    static ItemStack Poisonous(Player p, ItemStack tool, int slotsRemaining, boolean event) {
        if (!p.hasPermission("minetinker.modifiers.poisonous.apply")) { return null; }
        ItemMeta meta = tool.getItemMeta();
        List<String> lore = meta.getLore();

        if (Lists.BOWS.contains(tool.getType().toString()) || Lists.SWORDS.contains(tool.getType().toString()) || Lists.AXES.contains(tool.getType().toString())) {
            int index = 0;
            boolean hasPoisonous = false;
            for (int i = 1; i <= Main.getPlugin().getConfig().getInt("Modifiers.Poisonous.MaxLevel"); i++) {
                if (lore.contains(Strings.POISONOUS + i)) {
                    index = i;
                    hasPoisonous = true;
                    break;
                }
            }

            int loreIndex = 0;
            int level = 1 + index;
            if (level > Main.getPlugin().getConfig().getInt("Modifiers.Poisonous.MaxLevel")) {
                if (!event) {
                    Events.Mod_MaxLevel(p, tool, ChatColor.DARK_GREEN + Main.getPlugin().getConfig().getString("Modifiers.Poisonous.name"));
                }
                return null;
            }
            if (hasPoisonous) {
                loreIndex = lore.indexOf(Strings.POISONOUS + index);
            }
            if (loreIndex != 0) {
                lore.set(loreIndex, Strings.POISONOUS + level);
            } else {
                lore.add(Strings.POISONOUS + level);
            }

            Events.Mod_AddMod(p, tool, ChatColor.DARK_GREEN + Main.getPlugin().getConfig().getString("Modifiers.Poisonous.name") + " " + level, slotsRemaining - 1, event);
            if (!event) {
                lore.set(3, Strings.FREEMODIFIERSLOTS + (slotsRemaining - 1));
            }
        } else {
            return null;
        }

        meta.setLore(lore);
        tool.setItemMeta(meta);
        return tool;
    }

    static ItemStack Power(Player p, ItemStack tool, int slotsRemaining, boolean event) {
        if (!p.hasPermission("minetinker.modifiers.power.apply")) { return null; }
        ItemMeta meta = tool.getItemMeta();
        List<String> lore = meta.getLore();

        if (Lists.PICKAXES.contains(tool.getType().toString()) ||
                Lists.SHOVELS.contains(tool.getType().toString()) ||
                Lists.AXES.contains(tool.getType().toString()) ||
                Lists.HOES.contains(tool.getType().toString())) {
            int index = 0;
            boolean hasPower = false;
            for (int i = 1; i <= Main.getPlugin().getConfig().getInt("Modifiers.Power.MaxLevel"); i++) {
                if (lore.contains(Strings.POWER + i)) {
                    index = i;
                    hasPower = true;
                    break;
                }
            }
            int loreIndex = 0;
            int level = 1 + index;
            if (level > Main.getPlugin().getConfig().getInt("Modifiers.Power.MaxLevel")) {
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
            Events.Mod_AddMod(p, tool, ChatColor.GREEN + Main.getPlugin().getConfig().getString("Modifiers.Power.name") + " " + level, slotsRemaining - 1, event);
            if (!event) {
                lore.set(3, Strings.FREEMODIFIERSLOTS + (slotsRemaining - 1));
            }
        } else {
            return null;
        }

        meta.setLore(lore);
        tool.setItemMeta(meta);
        return tool;
    }

    static ItemStack Reinforced(Player p, ItemStack tool, int slotsRemaining, boolean event) {
        if (!p.hasPermission("minetinker.modifiers.reinforced.apply")) { return null; }
        ItemMeta meta = tool.getItemMeta();
        List<String> lore = meta.getLore();

        int index = 0;
        boolean hasReinforced = false;
        for (int i = 1; i <= Main.getPlugin().getConfig().getInt("Modifiers.Reinforced.MaxLevel"); i++) {
            if (lore.contains(Strings.REINFORCED + i)) {
                index = i;
                hasReinforced = true;
                break;
            }
        }
        int loreIndex = 0;
        int level = 1 + index;
        if (level > Main.getPlugin().getConfig().getInt("Modifiers.Reinforced.MaxLevel")) {
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
        Events.Mod_AddMod(p, tool, ChatColor.GRAY + Main.getPlugin().getConfig().getString("Modifiers.Reinforced.name") + " " + level, slotsRemaining - 1, event);
        if (!event) {
            lore.set(3, Strings.FREEMODIFIERSLOTS + (slotsRemaining - 1));
        }

        meta.setLore(lore);
        tool.setItemMeta(meta);
        return tool;
    }

    static ItemStack SelfRepair(Player p, ItemStack tool, int slotsRemaining, boolean event) {
        if (!p.hasPermission("minetinker.modifiers.selfrepair.apply")) { return null; }
        ItemMeta meta = tool.getItemMeta();
        List<String> lore = meta.getLore();

        int index = 0;
        boolean hasSelfRepair = false;
        for (int i = 1; i <= Main.getPlugin().getConfig().getInt("Modifiers.Self-Repair.MaxLevel"); i++) {
            if (lore.contains(Strings.SELFREPAIR + i)) {
                index = i;
                hasSelfRepair = true;
                break;
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
        Events.Mod_AddMod(p, tool, ChatColor.GREEN + Main.getPlugin().getConfig().getString("Modifiers.Self-Repair.name") + " " + level, slotsRemaining - 1, event);
        if (!event) {
            lore.set(3, Strings.FREEMODIFIERSLOTS + (slotsRemaining - 1));
        }

        meta.setLore(lore);
        tool.setItemMeta(meta);
        return tool;
    }

    static ItemStack Sharpness(Player p, ItemStack tool, int slotsRemaining, boolean event) {
        if (!p.hasPermission("minetinker.modifiers.sharpness.apply")) { return null; }
        ItemMeta meta = tool.getItemMeta();
        List<String> lore = meta.getLore();

        if (Lists.SWORDS.contains(tool.getType().toString()) || Lists.BOWS.contains(tool.getType().toString())) {
            int index = 0;
            boolean hasSharpness = false;
            for (int i = 1; i <= Main.getPlugin().getConfig().getInt("Modifiers.Sharpness.MaxLevel"); i++) {
                if (lore.contains(Strings.SHARPNESS + i)) {
                    index = i;
                    hasSharpness = true;
                    break;
                }
            }
            int loreIndex = 0;
            int level = 1 + index;
            if (level > Main.getPlugin().getConfig().getInt("Modifiers.Sharpness.MaxLevel")) {
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
            Events.Mod_AddMod(p, tool, ChatColor.WHITE + Main.getPlugin().getConfig().getString("Modifiers.Sharpness.name") + " " + level, slotsRemaining - 1, event);
            if (!event) {
                lore.set(3, Strings.FREEMODIFIERSLOTS + (slotsRemaining - 1));
            }
        } else {
            return null;
        }

        meta.setLore(lore);
        tool.setItemMeta(meta);
        return tool;
    }

    static ItemStack Shulking(Player p, ItemStack tool, int slotsRemaining, boolean event) {
        if (!p.hasPermission("minetinker.modifiers.shulking.apply")) { return null; }
        ItemMeta meta = tool.getItemMeta();
        List<String> lore = meta.getLore();

        if (Lists.SWORDS.contains(tool.getType().toString()) || Lists.BOWS.contains(tool.getType().toString())) {
            int index = 0;
            boolean hasShulking = false;
            for (int i = 1; i <= Main.getPlugin().getConfig().getInt("Modifiers.Shulking.MaxLevel"); i++) {
                if (lore.contains(Strings.SHULKING + i)) {
                    index = i;
                    hasShulking = true;
                    break;
                }
            }
            int loreIndex = 0;
            int level = 1 + index;
            if (level > Main.getPlugin().getConfig().getInt("Modifiers.Shulking.MaxLevel")) {
                if (!event) {
                    Events.Mod_MaxLevel(p, tool, ChatColor.LIGHT_PURPLE + Main.getPlugin().getConfig().getString("Modifiers.Shulking.name"));
                }
                return null;
            }
            if (hasShulking) {
                loreIndex = lore.indexOf(Strings.SHULKING + index);
            }
            if (loreIndex != 0) {
                lore.set(loreIndex, Strings.SHULKING + level);
            } else {
                lore.add(Strings.SHULKING + level);
            }
            Events.Mod_AddMod(p, tool, ChatColor.LIGHT_PURPLE + Main.getPlugin().getConfig().getString("Modifiers.Shulking.name") + " " + level, slotsRemaining - 1, event);
            if (!event) {
                lore.set(3, Strings.FREEMODIFIERSLOTS + (slotsRemaining - 1));
            }
        } else {
            return null;
        }

        meta.setLore(lore);
        tool.setItemMeta(meta);
        return tool;
    }

    static ItemStack SilkTouch(Player p, ItemStack tool, int slotsRemaining, boolean event) {
        if (!p.hasPermission("minetinker.modifiers.silktouch.apply")) { return null; }
        ItemMeta meta = tool.getItemMeta();
        List<String> lore = meta.getLore();

        if (lore.contains(Strings.SILKTOUCH)) {
            if (!event) {
                Events.Mod_MaxLevel(p, tool, ChatColor.WHITE + Main.getPlugin().getConfig().getString("Modifiers.Silk-Touch.name"));
            }
            return null;
        }
        for (int i = 1; i <= Main.getPlugin().getConfig().getInt("Modifiers.Luck.MaxLevel"); i++) {
            if (lore.contains(Strings.LUCK + i)) {
                if (!event) {
                    Events.IncompatibleMods(p, Main.getPlugin().getConfig().getString("Modifiers.Luck.name"), Main.getPlugin().getConfig().getString("Modifiers.Silk-Touch.name"));
                }
                return null;
            }
        }
        for (int i = 1; i <= Main.getPlugin().getConfig().getInt("Modifiers.Auto-Smelt.MaxLevel"); i++) {
            if (lore.contains(Strings.AUTOSMELT + i)) {
                if (!event) {
                    Events.IncompatibleMods(p, Main.getPlugin().getConfig().getString("Modifiers.Auto-Smelt.name"), Main.getPlugin().getConfig().getString("Modifiers.Silk-Touch.name"));
                }
                return null;
            }
        }
        if (Lists.AXES.contains(tool.getType().toString()) || Lists.PICKAXES.contains(tool.getType().toString()) || Lists.SHOVELS.contains(tool.getType().toString())) {
            meta.addEnchant(Enchantment.SILK_TOUCH, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            Events.Mod_AddMod(p, tool, ChatColor.WHITE + Main.getPlugin().getConfig().getString("Modifiers.Silk-Touch.name"), slotsRemaining - 1, event);
            if (!event) {
                lore.set(3, Strings.FREEMODIFIERSLOTS + (slotsRemaining - 1));
            }
            lore.add(Strings.SILKTOUCH);
        } else {
            return null;
        }

        meta.setLore(lore);
        tool.setItemMeta(meta);
        return tool;
    }

    static ItemStack Sweeping(Player p, ItemStack tool, int slotsRemaining, boolean event) {
        if (!p.hasPermission("minetinker.modifiers.sweeping.apply")) { return null; }
        ItemMeta meta = tool.getItemMeta();
        List<String> lore = meta.getLore();

        if (Lists.SWORDS.contains(tool.getType().toString())) {
            int index = 0;
            boolean hasSweeping = false;
            for (int i = 1; i <= Main.getPlugin().getConfig().getInt("Modifiers.Sweeping.MaxLevel"); i++) {
                if (lore.contains(Strings.SWEEPING + i)) {
                    index = i;
                    hasSweeping = true;
                    break;
                }
            }
            int loreIndex = 0;
            int level = 1 + index;
            if (level > Main.getPlugin().getConfig().getInt("Modifiers.Sweeping.MaxLevel")) {
                if (!event) {
                    Events.Mod_MaxLevel(p, tool, ChatColor.RED + Main.getPlugin().getConfig().getString("Modifiers.Sweeping.name"));
                }
                return null;
            }
            if (hasSweeping) {
                loreIndex = lore.indexOf(Strings.SWEEPING + index);
            }
            if (loreIndex != 0) {
                lore.set(loreIndex, Strings.SWEEPING + level);
            } else {
                lore.add(Strings.SWEEPING + level);
            }
            meta.addEnchant(Enchantment.SWEEPING_EDGE, level, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            Events.Mod_AddMod(p, tool, ChatColor.RED + Main.getPlugin().getConfig().getString("Modifiers.Sweeping.name") + " " + level, slotsRemaining - 1, event);
            if (!event) {
                lore.set(3, Strings.FREEMODIFIERSLOTS + (slotsRemaining - 1));
            }
        } else {
            return null;
        }

        meta.setLore(lore);
        tool.setItemMeta(meta);
        return tool;
    }

    static ItemStack Timber(Player p, ItemStack tool, int slotsRemaining, boolean event) {
        if (!p.hasPermission("minetinker.modifiers.timber.apply")) { return null; }
        ItemMeta meta = tool.getItemMeta();
        List<String> lore = meta.getLore();

        if (lore.contains(Strings.TIMBER)) {
            if (!event) {
                Events.Mod_MaxLevel(p, tool, Strings.TIMBER);
            }
            return null;
        }
        for (int i = 1; i <= Main.getPlugin().getConfig().getInt("Modifiers.Power.MaxLevel"); i++) {
            if (lore.contains(Strings.POWER + i)) {
                if (!event) {
                    Events.IncompatibleMods(p, Main.getPlugin().getConfig().getString("Modifiers.Power.name"), Main.getPlugin().getConfig().getString("Modifiers.Timber.name"));
                }
                return null;
            }
        }
        if (Lists.AXES.contains(tool.getType().toString())) {
            Events.Mod_AddMod(p, tool, ChatColor.WHITE + Main.getPlugin().getConfig().getString("Modifiers.Timber.name"), slotsRemaining - 1, event);
            if (!event) {
                lore.set(3, Strings.FREEMODIFIERSLOTS + (slotsRemaining - 1));
            }
            lore.add(Strings.TIMBER);
        } else {
            return null;
        }

        meta.setLore(lore);
        tool.setItemMeta(meta);
        return tool;
    }

    static ItemStack XP(Player p, ItemStack tool, int slotsRemaining, boolean event) {
        if (!p.hasPermission("minetinker.modifiers.xp.apply")) { return null; }
        ItemMeta meta = tool.getItemMeta();
        List<String> lore = meta.getLore();

        int index = 0;
        boolean hasXP = false;
        for (int i = 1; i <= Main.getPlugin().getConfig().getInt("Modifiers.XP.MaxLevel"); i++) {
            if (lore.contains(Strings.XP + i)) {
                index = i;
                hasXP = true;
                break;
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
        Events.Mod_AddMod(p, tool, ChatColor.GREEN + Main.getPlugin().getConfig().getString("Modifiers.XP.name") + " " + level, slotsRemaining - 1, event);
        if (!event) {
            lore.set(3, Strings.FREEMODIFIERSLOTS + (slotsRemaining - 1));
        }

        meta.setLore(lore);
        tool.setItemMeta(meta);
        return tool;
    }

}
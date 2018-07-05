package de.flo56958.MineTinker.Utilities;

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
                //<editor-fold desc="">
                int index = 0;
                boolean hasSelfRepair = false;
                searchloop:
                for (int i = 1; i <= Main.getPlugin().getConfig().getInt("Modifiers.Auto-Repair.MaxLevel"); i++) {
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
                int extra = Main.getPlugin().getConfig().getInt("Modifiers.Extra-Modifier.ExtraModifierGain");
                lore.set(3, Strings.FREEMODIFIERSLOTS + (slotsRemaining + extra));
                Events.Mod_AddMod(p, tool, ChatColor.WHITE + "" + extra + " extra Modifier-Slot", slotsRemaining + extra);
            } else if (modifier.equals("Reinforced")) {
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
            }
        } else {
            Events.Mod_NoSlots(p, tool, modifier);
            return null;
        }
        meta.setLore(lore);
        tool.setItemMeta(meta);
        return tool;
    }
}

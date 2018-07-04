package de.flo56958.MineTinker.Utilities;

import de.flo56958.MineTinker.Data.Strings;
import de.flo56958.MineTinker.Main;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
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
        item.setItemMeta(meta);
        item.addUnsafeEnchantment(ench, level);
        return item;
    }

    public static ItemStack ToolModifier(ItemStack tool, String modifier) {
        ItemMeta meta = tool.getItemMeta();
        ArrayList<String> lore = (ArrayList<String>) meta.getLore();
        String slots[] = lore.get(3).split(" ");
        int slotsRemaining = Integer.parseInt(slots[3]);
        if (slotsRemaining != 0 || modifier.equals("Extra-Modifier")) {
            if (modifier.equals("Auto-Repair")) {
                //<editor-fold desc="">
                int index = 0;
                boolean hasAutoRepair = false;
                searchloop:
                for (int i = 0; i <= Main.getPlugin().getConfig().getInt("Modifiers.Auto-Repair.MaxLevel"); i++) {
                    if (lore.contains(Strings.AUTOREPAIR + i)) {
                        index = i;
                        hasAutoRepair = true;
                        break searchloop;
                    }
                }
                int loreIndex = 0;
                int level = 1 + index;
                if (level > Main.getPlugin().getConfig().getInt("Modifiers.Auto-Repair.MaxLevel")) {
                    return tool;
                }
                if (hasAutoRepair) {
                    loreIndex = lore.indexOf(Strings.AUTOREPAIR + index);
                }
                if (loreIndex != 0) {
                    lore.set(loreIndex, Strings.AUTOREPAIR + level);
                } else {
                    lore.add(Strings.AUTOREPAIR + level);
                }
                lore.set(3, Strings.FREEMODIFIERSLOTS + (slotsRemaining - 1));
                if (level == Main.getPlugin().getConfig().getInt("Modifiers.Auto-Repair.MaxLevel")) {
                    meta.addEnchant(Enchantment.MENDING, 1, true);
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                }
                //</editor-fold>
            } else if (modifier.equals("Extra-Modifier")) {
                lore.set(3, Strings.FREEMODIFIERSLOTS + (slotsRemaining + Main.getPlugin().getConfig().getInt("Modifiers.Extra-Modifier.ExtraModifierGain")));
            }
        } else {
            return tool;
        }
        meta.setLore(lore);
        tool.setItemMeta(meta);
        return tool;
    }
}

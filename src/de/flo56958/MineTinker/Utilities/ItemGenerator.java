package de.flo56958.MineTinker.Utilities;

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
}

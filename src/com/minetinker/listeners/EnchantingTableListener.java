package com.minetinker.listeners;

import com.minetinker.data.Lists;
import com.minetinker.data.Strings;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemStack;

public class EnchantingTableListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.isCancelled()) { return; }
        if (!Lists.WORLDS.contains(e.getWhoClicked().getWorld().getName())) { return; }

        if (!(e.getInventory() instanceof EnchantingInventory)) { return; }
        if (e.getSlot() != 0) { return; }

        ItemStack tool = e.getWhoClicked().getItemOnCursor();
        if (!tool.hasItemMeta()) { return; }
        if (!tool.getItemMeta().hasLore()) { return; }

        if (!(e.getWhoClicked().getItemOnCursor().getItemMeta().getLore().contains(Strings.IDENTIFIER)
                || e.getWhoClicked().getItemOnCursor().getItemMeta().getLore().contains(Strings.IDENTIFIER_BUILDERSWAND))) { return; }

        e.setCancelled(true);
    }
}


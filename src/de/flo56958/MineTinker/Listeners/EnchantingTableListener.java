package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Data.Strings;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.EnchantingInventory;

public class EnchantingTableListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInventoryClick(InventoryClickEvent e) {
        if (!e.isCancelled()) {
            if (!Lists.WORLDS.contains(e.getWhoClicked().getWorld().getName())) { return; }
            if (e.getInventory() instanceof EnchantingInventory) {
                if (e.getSlot() == 0) {
                    if (e.getWhoClicked().getItemOnCursor().hasItemMeta()) {
                        if (e.getWhoClicked().getItemOnCursor().getItemMeta().getLore().contains(Strings.IDENTIFIER) || e.getWhoClicked().getItemOnCursor().getItemMeta().getLore().contains(Strings.IDENTIFIER_BUILDERSWAND)) {
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }
    }
}

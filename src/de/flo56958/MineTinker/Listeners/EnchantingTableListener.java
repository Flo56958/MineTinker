package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Data.Strings;
import de.flo56958.MineTinker.Utilities.PlayerInfo;
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
        if (PlayerInfo.isToolViable(tool)) {
            if (!(e.getWhoClicked().getItemOnCursor().getItemMeta().getLore().contains(Strings.IDENTIFIER)
                    || e.getWhoClicked().getItemOnCursor().getItemMeta().getLore().contains(Strings.IDENTIFIER_BUILDERSWAND))) { return; }

            e.setCancelled(true);
        }
    }
}


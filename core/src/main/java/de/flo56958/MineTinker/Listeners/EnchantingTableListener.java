package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Modifiers.ModManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;

public class EnchantingTableListener implements Listener {

    private static final ModManager modManager = ModManager.instance();

    @EventHandler(ignoreCancelled = true)
    public void onEnchant(EnchantItemEvent e) {
        if (Lists.WORLDS.contains(e.getEnchanter().getWorld().getName())) {
            return;
        }

        ItemStack tool = e.getItem();

        if (modManager.isToolViable(tool) || modManager.isWandViable(tool) || modManager.isArmorViable(tool)) {
            e.setCancelled(true);
        }
    }
}


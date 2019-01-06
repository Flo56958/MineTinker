package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Modifier;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ItemListener implements Listener {

    private final ModManager modManager = ModManager.instance();

    @EventHandler
    public void onDespawn(ItemDespawnEvent e) {
        if (e.isCancelled()) { return; }

        Item item = e.getEntity();
        ItemStack is = item.getItemStack();
        if (!(modManager.isArmorViable(is) || modManager.isToolViable(is) || modManager.isWandViable(is))) { return; }

        if (Main.getPlugin().getConfig().getBoolean("ItemBehaviour.SetPersistent")) {
            e.setCancelled(true);
            item.setTicksLived(1);
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        if (e.isCancelled()) { return; }

        Item item = e.getItemDrop();
        ItemStack is = item.getItemStack();

        boolean isMineTinker = false;

        if (Main.getPlugin().getConfig().getBoolean("ItemBehaviour.ForModItems")) {
            ItemStack modifierTester = is.clone();
            modifierTester.setAmount(1);

            for (Modifier m : modManager.getAllowedMods()) {
                if (m.getModItem().equals(modifierTester)) {
                    isMineTinker = true;
                    break;
                }
            }
        }
        if (modManager.isArmorViable(is) || modManager.isToolViable(is) || modManager.isWandViable(is)) { isMineTinker = true; }

        if (!isMineTinker) { return; }

        if (Main.getPlugin().getConfig().getBoolean("ItemBehaviour.ShowName")) {
            item.setCustomName(is.getItemMeta().getDisplayName());
            item.setCustomNameVisible(true);
        }
        if (Main.getPlugin().getConfig().getBoolean("ItemBehaviour.SetGlowing")) {
            item.setGlowing(true);
        }
        if (Main.getPlugin().getConfig().getBoolean("ItemBehaviour.SetInvulnerable")) {
            item.setInvulnerable(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (e.getKeepInventory()) { return; }

        Player p = e.getEntity();
        Inventory inv = p.getInventory();

        for (ItemStack is : inv.getContents()) {
            if (is == null) { continue; }

            boolean isMineTinker = false;
            if (Main.getPlugin().getConfig().getBoolean("ItemBehaviour.ForModItems")) {
                ItemStack modifierTester = is.clone();
                modifierTester.setAmount(1);

                for (Modifier m : modManager.getAllowedMods()) {
                    if (m.getModItem().equals(modifierTester)) {
                        isMineTinker = true;
                        break;
                    }
                }
            }
            if (modManager.isArmorViable(is) || modManager.isToolViable(is) || modManager.isWandViable(is)) { isMineTinker = true; }

            if (!isMineTinker) { continue; }

            Bukkit.getPluginManager().callEvent(new PlayerDropItemEvent(p, p.getWorld().dropItem(p.getLocation(), is))); //To trigger item behaviour

            is.setAmount(0);
        }
    }
}

package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Modifiers.Types.Soulbound;
import de.flo56958.MineTinker.Utilities.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemListener implements Listener {

    private final ModManager modManager = ModManager.instance();

    @EventHandler(ignoreCancelled = true)
    public void onDespawn(ItemDespawnEvent event) {
        Item item = event.getEntity();
        ItemStack is = item.getItemStack();

        if (!(modManager.isArmorViable(is) || modManager.isToolViable(is) || modManager.isWandViable(is))) {
            return; //TODO: Consider Modifier-Items
        }

        if (Main.getPlugin().getConfig().getBoolean("ItemBehaviour.SetPersistent")) {
            event.setCancelled(true);
            item.setTicksLived(1);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemDrop(PlayerDropItemEvent event) {
        if (Main.getPlugin().getConfig().getBoolean("ItemBehaviour.DisableDroppingBehaviour")) {
            return;
        }

        Item item = event.getItemDrop();
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
        if (modManager.isArmorViable(is) || modManager.isToolViable(is) || modManager.isWandViable(is)) {
            isMineTinker = true;
        }

        if (!isMineTinker) {
            return;
        }

        if (Main.getPlugin().getConfig().getBoolean("ItemBehaviour.ShowName") && is.getItemMeta() != null) {
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
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getKeepInventory()) {
            return;
        }

        Player player = event.getEntity();
        Inventory inventory = player.getInventory();

        for (ItemStack itemStack : inventory.getContents()) {
            if (itemStack == null) {
                continue; // More consistent nullability in NotNull fields
            }

            boolean isMineTinker = false;

            if (Main.getPlugin().getConfig().getBoolean("ItemBehaviour.ForModItems")) { //Modifieritems
                ItemStack modifierTester = itemStack.clone();
                modifierTester.setAmount(1);

                for (Modifier modifier : modManager.getAllowedMods()) {
                    if (modifier.getModItem().equals(modifierTester)) {
                        isMineTinker = true;
                        break;
                    }
                }
            }

            if (modManager.isArmorViable(itemStack) || modManager.isToolViable(itemStack) || modManager.isWandViable(itemStack)) {
                isMineTinker = true;
            }

            if (!isMineTinker) {
                continue;
            }

            if (Soulbound.instance().effect(player, itemStack)) {
                itemStack.setAmount(0);
                continue;
            } //workaround as inv.remove(is) does not work insteads duplicates item

            if (!Main.getPlugin().getConfig().getBoolean("ItemBehaviour.DisableDroppingBehaviour")) {
                PlayerDropItemEvent dropItemEvent = new PlayerDropItemEvent(player, player.getWorld().dropItem(player.getLocation(), itemStack));
                Bukkit.getPluginManager().callEvent(dropItemEvent); //To trigger item behaviour
                itemStack.setAmount(0);
            }
        }
    }

    //TODO: Crossbow gets destroyed non the less
    @EventHandler
    public void onItemBreak(PlayerItemBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getBrokenItem();

        if (Lists.WORLDS.contains(player.getWorld().getName())) {
            return;
        }

        if (!modManager.isToolViable(item)) {
            return;
        }

        if (!Main.getPlugin().getConfig().getBoolean("ItemBehaviour.StopBreakEvent")) {
            return;
        }

        if (Main.getPlugin().getConfig().getBoolean("ItemBehaviour.AlertPlayerOnBreak")) {
            p.sendMessage(LanguageManager.getString("Alert.OnItemBreak", p));
        }

        ItemMeta meta = item.getItemMeta();

        if (meta instanceof Damageable) {
            ((Damageable) meta).setDamage(item.getType().getMaxDurability() - 1);
            item.setItemMeta(meta);
        }

        player.getInventory().addItem(item);
    }
}

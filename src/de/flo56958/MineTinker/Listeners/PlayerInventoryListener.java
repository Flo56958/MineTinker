package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Data.Strings;
import de.flo56958.MineTinker.Main;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PlayerInventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!e.isCancelled()) {
            if (e.getClickedInventory() instanceof PlayerInventory) {
                if (e.getClickedInventory().getItem(e.getSlot()) != null) {
                    if (e.getClickedInventory().getItem(e.getSlot()).hasItemMeta()) {
                        if (!(e.getClickedInventory().getItem(e.getSlot()).getItemMeta().getLore() == null)) {
                            if (e.getClickedInventory().getItem(e.getSlot()).getItemMeta().getLore().contains(Strings.IDENTIFIER)) {
                                if (e.getWhoClicked().getItemOnCursor() != null) {
                                    ItemStack tool = e.getClickedInventory().getItem(e.getSlot());
                                    ItemStack repair = e.getWhoClicked().getItemOnCursor();
                                    String[] name = tool.getType().toString().split("_");
                                    boolean eligible = false;
                                    //<editor-fold desc="SAME SEARCH">
                                    if (name[0].toLowerCase().equals("wood") && repair.getType().equals(Material.WOOD)) {
                                        eligible = true;
                                    } else if (name[0].toLowerCase().equals("stone") && repair.getType().equals(Material.COBBLESTONE)) {
                                        eligible = true;
                                    } else if (name[0].toLowerCase().equals("iron") && repair.getType().equals(Material.IRON_INGOT)) {
                                        eligible = true;
                                    } else if (name[0].toLowerCase().equals("gold") && repair.getType().equals(Material.GOLD_INGOT)) {
                                        eligible = true;
                                    } else if (name[0].toLowerCase().equals("diamond") && repair.getType().equals(Material.DIAMOND)) {
                                        eligible = true;
                                    }
                                    if (eligible) {
                                        double dura = tool.getDurability();
                                        double maxDura = tool.getType().getMaxDurability();
                                        int amount = e.getWhoClicked().getItemOnCursor().getAmount();
                                        double percent = Main.getPlugin().getConfig().getDouble("DurabilityPercentageRepair");
                                        while (amount > 0 && dura > 0) {
                                            dura = dura - (maxDura * percent);
                                            amount--;
                                        }
                                        if (dura < 0) {
                                            dura = 0;
                                        }
                                        tool.setDurability((short) dura);
                                        e.getWhoClicked().getItemOnCursor().setAmount(amount);
                                        e.setCancelled(true);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

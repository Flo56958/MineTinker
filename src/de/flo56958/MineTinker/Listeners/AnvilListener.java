package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Data.Modifiers;
import de.flo56958.MineTinker.Data.Strings;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class AnvilListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInventoryClick(InventoryClickEvent e) {
        if (!e.isCancelled()) {
            if (e.getWhoClicked() instanceof HumanEntity) {
                Inventory i = e.getClickedInventory();
                if (i instanceof AnvilInventory) {
                    int s = e.getRawSlot();
                    if (s == 1) {       //Mod-Slot
                        if (i.getItem(0) != null) {
                            if (i.getItem(0).getItemMeta().getLore().contains(Strings.IDENTIFIER)) {
                                if (e.getWhoClicked().getItemOnCursor().getAmount() == 1) {
                                    boolean isModifier = false;
                                    ItemStack newTool = i.getItem(0);
                                    ItemStack modifier = e.getWhoClicked().getItemOnCursor();
                                    if (Main.getPlugin().getConfig().getBoolean("Modifiers.Self-Repair.allowed")) {
                                        if (modifier.equals(Modifiers.SELFREPAIR_MODIFIER)) {
                                            newTool = ItemGenerator.ToolModifier(i.getItem(0), "Self-Repair", (Player) e.getWhoClicked());
                                            isModifier = true;
                                        }
                                    }
                                    if (Main.getPlugin().getConfig().getBoolean("Modifiers.Extra-Modifier.allowed")) {
                                        if (modifier.getType().equals(Material.NETHER_STAR)) {
                                            newTool = ItemGenerator.ToolModifier(i.getItem(0), "Extra-Modifier", (Player) e.getWhoClicked());
                                            isModifier = true;
                                        }
                                    }
                                    if (Main.getPlugin().getConfig().getBoolean("Modifiers.Reinforced.allowed")) {
                                        if (modifier.equals(Modifiers.REINFORCED_MODIFIER)) {
                                            newTool = ItemGenerator.ToolModifier(i.getItem(0), "Reinforced", (Player) e.getWhoClicked());
                                            isModifier = true;
                                        }
                                    }
                                    if (isModifier) {
                                        if (newTool == null) { return; }
                                        if (newTool != i.getItem(0)) {
                                            int newAmount = e.getWhoClicked().getItemOnCursor().getAmount() - 1;
                                            e.getWhoClicked().getItemOnCursor().setAmount(newAmount);
                                            i.setItem(2, newTool);
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
}

package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Data.Modifiers;
import de.flo56958.MineTinker.Data.Strings;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import org.bukkit.Material;
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
            Inventory i = e.getClickedInventory();
            if (i instanceof AnvilInventory) {
                int s = e.getRawSlot();
                if (s == 1) {       //Mod-Slot
                    if (i.getItem(0) != null) {
                        if (i.getItem(0).getItemMeta().getLore().contains(Strings.IDENTIFIER)) {
                            ItemStack newTool = null;
                            if (e.getWhoClicked().getItemOnCursor().getAmount() == 1) {
                                //<editor-fold desc="MODIFIERS">
                                boolean isModifier = false;
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
                                if (Main.getPlugin().getConfig().getBoolean("Modifiers.Haste.allowed")) {
                                    if (modifier.equals(Modifiers.HASTE_MODIFIER)) {
                                        newTool = ItemGenerator.ToolModifier(i.getItem(0), "Haste", (Player) e.getWhoClicked());
                                        isModifier = true;
                                    }
                                }
                                if (Main.getPlugin().getConfig().getBoolean("Modifiers.Reinforced.allowed")) {
                                    if (modifier.equals(Modifiers.REINFORCED_MODIFIER)) {
                                        newTool = ItemGenerator.ToolModifier(i.getItem(0), "Reinforced", (Player) e.getWhoClicked());
                                        isModifier = true;
                                    }
                                }
                                if (Main.getPlugin().getConfig().getBoolean("Modifiers.Sharpness.allowed")) {
                                    if (modifier.equals(Modifiers.SHARPNESS_MODIFIER)) {
                                        newTool = ItemGenerator.ToolModifier(i.getItem(0), "Sharpness", (Player) e.getWhoClicked());
                                        isModifier = true;
                                    }
                                }
                                if (Main.getPlugin().getConfig().getBoolean("Modifiers.XP.allowed")) {
                                    if (modifier.getType().equals(Material.EXP_BOTTLE)) {
                                        newTool = ItemGenerator.ToolModifier(i.getItem(0), "XP", (Player) e.getWhoClicked());
                                        isModifier = true;
                                    }
                                }
                                if (Main.getPlugin().getConfig().getBoolean("Modifiers.Luck.allowed")) {
                                    if (modifier.equals(Modifiers.LUCK_MODIFIER)) {
                                        newTool = ItemGenerator.ToolModifier(i.getItem(0), "Luck", (Player) e.getWhoClicked());
                                        isModifier = true;
                                    }
                                }
                                if (Main.getPlugin().getConfig().getBoolean("Modifiers.Silk-Touch.allowed")) {
                                    if (modifier.equals(Modifiers.SILKTOUCH_MODIFIER)) {
                                        newTool = ItemGenerator.ToolModifier(i.getItem(0), "Silk-Touch", (Player) e.getWhoClicked());
                                        isModifier = true;
                                    }
                                }
                                if (Main.getPlugin().getConfig().getBoolean("Modifiers.Fiery.allowed")) {
                                    if (modifier.equals(Modifiers.FIERY_MODIFIER)) {
                                        newTool = ItemGenerator.ToolModifier(i.getItem(0), "Fiery", (Player) e.getWhoClicked());
                                        isModifier = true;
                                    }
                                }
                                if (Main.getPlugin().getConfig().getBoolean("Modifiers.Auto-Smelt.allowed")) {
                                    if (modifier.equals(Modifiers.AUTOSMELT_MODIFIER)) {
                                        newTool = ItemGenerator.ToolModifier(i.getItem(0), "Auto-Smelt", (Player) e.getWhoClicked());
                                        isModifier = true;
                                    }
                                }
                                if (isModifier) {
                                    if (newTool == null) { return; }
                                    int newAmount = e.getWhoClicked().getItemOnCursor().getAmount() - 1;
                                    e.getWhoClicked().getItemOnCursor().setAmount(newAmount);
                                    i.setItem(2, newTool);
                                    e.setCancelled(true);
                                    return;
                                }
                                //</editor-fold>
                            }
                            if (Main.getPlugin().getConfig().getBoolean("Upgradeable")) {
                                //<editor-fold desc="UPGRADE">
                                if (e.getWhoClicked().getItemOnCursor().getAmount() == 1) { //Shovel
                                    if (Lists.SHOVELS.contains(i.getItem(0).getType().toString())) {
                                        newTool = ItemGenerator.itemUpgrader(i.getItem(0), e.getWhoClicked().getItemOnCursor(), (Player) e.getWhoClicked());
                                    }
                                } else if (e.getWhoClicked().getItemOnCursor().getAmount() == 2) { //Sword / Hoe
                                    if (Lists.SWORDS.contains(i.getItem(0).getType().toString()) || Lists.HOES.contains(i.getItem(0).getType().toString())) {
                                        newTool = ItemGenerator.itemUpgrader(i.getItem(0), e.getWhoClicked().getItemOnCursor(), (Player) e.getWhoClicked());
                                    }
                                } else if (e.getWhoClicked().getItemOnCursor().getAmount() == 3) { //Axe / Pickaxe
                                    if (Lists.AXES.contains(i.getItem(0).getType().toString()) || Lists.PICKAXES.contains(i.getItem(0).getType().toString())) {
                                        newTool = ItemGenerator.itemUpgrader(i.getItem(0), e.getWhoClicked().getItemOnCursor(), (Player) e.getWhoClicked());
                                    }
                                }
                                if (newTool != null) {
                                    e.getWhoClicked().getItemOnCursor().setAmount(0);
                                    e.setCancelled(true);
                                } else { return; }
                                //</editor-fold>
                            }
                        }
                    }
                }
            }
        }
    }
}

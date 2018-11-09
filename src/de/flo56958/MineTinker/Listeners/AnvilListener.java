package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Modifiers.Types.ModifierType;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import de.flo56958.MineTinker.Utilities.PlayerInfo;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class AnvilListener implements Listener {

    private static final FileConfiguration config = Main.getPlugin().getConfig();
    private static final ModManager modManager = Main.getModManager();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInventoryClick(InventoryClickEvent e) { //TODO: Add Slot 1 to Slot 0   |or|   add Slot 0 as a viable option
        if (e.isCancelled()) { return; }
        if (!Lists.WORLDS.contains(e.getWhoClicked().getWorld().getName())) { return; }

        Inventory i = e.getClickedInventory();
        if (!(i instanceof AnvilInventory)) { return; } //TODO: Add the feature to modify a tool directly in the inventory

        if (!(e.getWhoClicked() instanceof Player)) { return; }

        Player p = (Player) e.getWhoClicked();
        int s = e.getRawSlot();
        if (s != 1) { return; }      //Mod-Slot

        ItemStack tool = i.getItem(0);
        if (!PlayerInfo.isToolViable(tool)) { return; }

        ItemStack newTool = null;

        if (p.getItemOnCursor().getAmount() == 1) { //TODO: Change code so you can apply more than one modifier at a time
            boolean isModifier = true;
            ItemStack modifier = p.getItemOnCursor();

            if (modManager.get(ModifierType.EXTRA_MODIFIER) != null && modifier.equals(modManager.get(ModifierType.EXTRA_MODIFIER).getModItem())) {
                newTool = modManager.get(ModifierType.EXTRA_MODIFIER).applyMod(p, tool, false);
            } else if (modManager.getFreeSlots(tool) > 0) {
                boolean success = false;
                for (Modifier m : modManager.getAllMods()) {
                    if (m.getModItem().equals(modifier)) {
                        newTool = m.applyMod(p, tool, false);
                    }
                }
            } else {
                isModifier = false;
            }
            if (isModifier) {
                if (newTool == null) {
                    return;
                }
                int newAmount = e.getWhoClicked().getItemOnCursor().getAmount() - 1;
                e.getWhoClicked().getItemOnCursor().setAmount(newAmount);
                e.setCancelled(true);
                return;
            }
        }

        if (config.getBoolean("Upgradeable") && e.getWhoClicked().hasPermission("minetinker.tool.upgrade")) {
            switch (e.getWhoClicked().getItemOnCursor().getAmount()) {
                case 1:
                    if (ToolType.SHOVEL.getMaterials().contains(tool.getType())) {
                        newTool = ItemGenerator.itemUpgrader(tool, e.getWhoClicked().getItemOnCursor(), (Player) e.getWhoClicked());
                    }
                    break;
                case 2:
                    if (ToolType.SWORD.getMaterials().contains(tool.getType()) || ToolType.HOE.getMaterials().contains(tool.getType())) {
                        newTool = ItemGenerator.itemUpgrader(tool, e.getWhoClicked().getItemOnCursor(), (Player) e.getWhoClicked());
                    }
                    break;
                case 3:
                    if (ToolType.AXE.getMaterials().contains(tool.getType()) || ToolType.PICKAXE.getMaterials().contains(tool.getType())) {
                        newTool = ItemGenerator.itemUpgrader(tool, e.getWhoClicked().getItemOnCursor(), (Player) e.getWhoClicked());
                    }
                    break;
            }
            if (newTool != null) {
                e.getWhoClicked().getItemOnCursor().setAmount(0);
                e.setCancelled(true);
            }
        }
    }
}
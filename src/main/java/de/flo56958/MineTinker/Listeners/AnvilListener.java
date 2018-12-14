package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
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
    private static final ModManager modManager = ModManager.instance();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInventoryClick(InventoryClickEvent e) { //TODO: Add Slot 1 to Slot 0   |or|   add Slot 0 as a viable option
        if (e.isCancelled()) { return; }
        if (Lists.WORLDS.contains(e.getWhoClicked().getWorld().getName())) { return; }

        Inventory i = e.getClickedInventory();
        if (!(i instanceof AnvilInventory)) { return; } //TODO: Add the feature to modify a tool directly in the inventory

        if (!(e.getWhoClicked() instanceof Player)) { return; }

        Player p = (Player) e.getWhoClicked();
        int s = e.getRawSlot();
        if (s != 1) { return; }      //Mod-Slot

        ItemStack tool = i.getItem(0);

        if (!(modManager.isToolViable(tool) || modManager.isArmorViable(tool))) { return; }

        ItemStack newTool = null;

        ItemStack modifier = p.getItemOnCursor();
        ItemStack modifierTester = modifier.clone();
        modifierTester.setAmount(1);

        Modifier mod = null;

        for (Modifier m : modManager.getAllMods()) {
            if (m.getModItem().equals(modifierTester)) {
                mod = m;
            }
        }

        if (mod != null) {
            for (; modifier.getAmount() > 0; modifier.setAmount(modifier.getAmount() - 1)) {
                newTool = mod.applyMod(p, tool, false);

                if (newTool == null) { break; }
                tool = newTool;
            }
            e.setCancelled(true);
        } else {
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
                    case 4:
                        if (ToolType.BOOTS.getMaterials().contains(tool.getType())) {
                            newTool = ItemGenerator.itemUpgrader(tool, e.getWhoClicked().getItemOnCursor(), (Player) e.getWhoClicked());
                        }
                        break;
                    case 5:
                        if (ToolType.HELMET.getMaterials().contains(tool.getType())) {
                            newTool = ItemGenerator.itemUpgrader(tool, e.getWhoClicked().getItemOnCursor(), (Player) e.getWhoClicked());
                        }
                        break;
                    case 7:
                        if (ToolType.LEGGINGS.getMaterials().contains(tool.getType())) {
                            newTool = ItemGenerator.itemUpgrader(tool, e.getWhoClicked().getItemOnCursor(), (Player) e.getWhoClicked());
                        }
                        break;
                    case 8:
                        if (ToolType.CHESTPLATE.getMaterials().contains(tool.getType())) {
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
}
package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.ModifierApplyEvent;
import de.flo56958.MineTinker.Events.ToolUpgradeEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import de.flo56958.MineTinker.Utilities.nms.NBTUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class AnvilListener implements Listener {

    private static final ModManager modManager = ModManager.instance();

    //<--- code from 27.07.2019 --->
    //reverted due to bugs in new implementation; newer implementation is commented out below
    //changed method applyMod() to addMod() due to modifier rework
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent e) {
        HumanEntity he = e.getWhoClicked();

        if (!(he instanceof Player && e.getClickedInventory() instanceof AnvilInventory)) {
            return;
        }

        AnvilInventory inv = (AnvilInventory) e.getClickedInventory();
        Player player = (Player) he;

        ItemStack tool = inv.getItem(0);
        ItemStack modifier = inv.getItem(1);
        ItemStack newTool = inv.getItem(2);

        if (tool == null || modifier == null || newTool == null) {
            return;
        }

        if (e.getSlot() != 2) {
            return;
        }

        if (Lists.WORLDS.contains(player.getWorld().getName())) {
            return;
        }

        if (!(modManager.isToolViable(tool) || modManager.isArmorViable(tool))) {
            return;
        }

        //boolean deleteAllItems = false;
        if (e.getCursor() != null && !e.getCursor().getType().equals(Material.AIR)) {
            return;
        }

        if (!modManager.isModifierItem(modifier)) { //upgrade
            if (tool.getType().equals(newTool.getType())) return; //Not an upgrade

            // ------ upgrade
            if (e.isShiftClick()) {
                if (player.getInventory().addItem(newTool).size() != 0) { //adds items to (full) inventory and then case if inventory is full
                    e.setCancelled(true); //cancels the event if the player has a full inventory
                    return;
                } // no else as it gets added in if-clause

                inv.clear();

                return;
            }

            Bukkit.getPluginManager().callEvent(new ToolUpgradeEvent(player, newTool, true));

            player.setItemOnCursor(newTool);
            inv.clear();
        } else { //is modifier
            Modifier mod = modManager.getModifierFromItem(modifier);

            if (mod == null) {
                return;
            }

            modifier.setAmount(modifier.getAmount() - 1);
            Bukkit.getPluginManager().callEvent(new ModifierApplyEvent(player, tool, mod, modManager.getFreeSlots(newTool), false));

            if (e.isShiftClick()) {
                if (player.getInventory().addItem(newTool).size() != 0) { //adds items to (full) inventory and then case if inventory is full
                    e.setCancelled(true); //cancels the event if the player has a full inventory
                    return;
                } // no else as it gets added in if-clause

                inv.clear();
                inv.setItem(1, modifier);

                return;
            }

            player.setItemOnCursor(newTool);

            inv.clear();
            inv.setItem(1, modifier);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onAnvilPrepare(PrepareAnvilEvent e) {
        AnvilInventory i = e.getInventory();
        ItemStack tool = i.getItem(0);
        ItemStack modifier = i.getItem(1);

        if (tool == null || modifier == null) {
            return;
        }

        //-----
        Player player = null;

        List<HumanEntity> listHumans = e.getViewers();

        for (HumanEntity he : listHumans) {
            if (he instanceof Player) {
                player = (Player) he;
                break;
            }
        }

        if (player == null) {
            return;
        }

        //-----
        System.out.println("1.");

        if (Lists.WORLDS.contains(player.getWorld().getName())) {
            return;
        }

        System.out.println("1.5");

        if (!(modManager.isToolViable(tool) || modManager.isArmorViable(tool))) {
            return;
        }
        System.out.println("2.");

        if (modifier.getType().equals(Material.ENCHANTED_BOOK)) { //So no Tools can be enchanted via books, if enchanting is disabled
            if (Main.getPlugin().getConfig().getBoolean("AllowEnchanting")) {
                // If enchanting is allowed, don't do anything
                return;
            } else {
                // Otherwise, set the resulting item to AIR to negate the enchant
                e.setResult(new ItemStack(Material.AIR, 0)); //sets ghostitem by client
                return;
            }
        }

        Modifier mod = modManager.getModifierFromItem(modifier);

        ItemStack newTool = null;
        System.out.println("3.");

        if (mod != null) {
            newTool = tool.clone();
            System.out.println("4.");
            if (!modManager.addMod(player, newTool, mod, false, false)) {
                return;
            }
            System.out.println("5.");
        } else {
            if (Main.getPlugin().getConfig().getBoolean("Upgradeable") && player.hasPermission("minetinker.tool.upgrade")) {
                ItemStack item = i.getItem(1);

                if (item != null) {
                    switch (item.getAmount()) {
                        case 1:
                            if (ToolType.SHOVEL.contains(tool.getType())) {
                                newTool = ItemGenerator.itemUpgrader(tool.clone(), i.getItem(1), player);
                            }
                            break;
                        case 2:
                            if (ToolType.SWORD.contains(tool.getType()) || ToolType.HOE.contains(tool.getType())) {
                                newTool = ItemGenerator.itemUpgrader(tool.clone(), i.getItem(1), player);
                            }
                            break;
                        case 3:
                            if (ToolType.AXE.contains(tool.getType()) || ToolType.PICKAXE.contains(tool.getType())) {
                                newTool = ItemGenerator.itemUpgrader(tool.clone(), i.getItem(1), player);
                            }
                            break;
                        case 4:
                            if (ToolType.BOOTS.contains(tool.getType())) {
                                newTool = ItemGenerator.itemUpgrader(tool.clone(), i.getItem(1), player);
                            }
                            break;
                        case 5:
                            if (ToolType.HELMET.contains(tool.getType())) {
                                newTool = ItemGenerator.itemUpgrader(tool.clone(), i.getItem(1), player);
                            }
                            break;
                        case 7:
                            if (ToolType.LEGGINGS.contains(tool.getType())) {
                                newTool = ItemGenerator.itemUpgrader(tool.clone(), i.getItem(1), player);
                            }
                            break;
                        case 8:
                            if (ToolType.CHESTPLATE.contains(tool.getType())) {
                                newTool = ItemGenerator.itemUpgrader(tool.clone(), i.getItem(1), player);
                            }
                            break;
                    }
                }
            }
        }
        System.out.println("6.");

        if (newTool != null) {
            System.out.println("7.");
            e.setResult(newTool);
            i.setRepairCost(0);
        }
    }

    /* <--- newer buggy code; needs fixing or rework --->
    // Code is from
	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryClick(InventoryClickEvent event) {
		HumanEntity entity = event.getWhoClicked();
		
		if (!(entity instanceof Player && event.getClickedInventory() instanceof AnvilInventory)) {
		    return;
        }

        AnvilInventory inv = (AnvilInventory) event.getClickedInventory();
        Player player = (Player) entity;

        ItemStack tool = inv.getItem(0);
        ItemStack modifier = inv.getItem(1);
        ItemStack newTool = inv.getItem(2);

        if (tool == null || modifier == null || newTool == null) {
            return;
        }

        if (event.getSlot() != 2) {
            return;
        }

        if (Lists.WORLDS.contains(player.getWorld().getName())) {
            return;
        }

        if (!(modManager.isToolViable(tool) || modManager.isArmorViable(tool))) {
            return;
        }

        //boolean deleteAllItems = false;
        if (event.getCursor() != null && !(event.getCursor().getType() == Material.AIR)) {
            return;
        }

        if (!modManager.isModifierItem(modifier)) { //upgrade
            if (tool.getType().equals(newTool.getType())) {
                return; //Not an upgrade
            }

            //TODO: Items should not be stackable but could be with certain plugins
            // ------ upgrade
//            if (e.isShiftClick()) {
//                if (player.getInventory().addItem(newTool).size() != 0) { //adds items to (full) inventory and then case if inventory is full
//                    e.setCancelled(true); //cancels the event if the player has a full inventory
//                    return;
//                } // no else as it gets added in if-clause
//
//                inv.clear();
//
//                return;
//            }

            //TODO: TEST NEW UPGRADE IMPLEMENTATION

            int usedAmountItem = -1;

            if (ToolType.SHOVEL.contains(tool.getType()))
                usedAmountItem = 1;
            else if (ToolType.SWORD.contains(tool.getType()) || ToolType.HOE.contains(tool.getType()))
                usedAmountItem = 2;
            else if (ToolType.AXE.contains(tool.getType()) || ToolType.PICKAXE.contains(tool.getType()))
                usedAmountItem = 3;
            else if (ToolType.BOOTS.contains(tool.getType()))
                usedAmountItem = 4;
            else if (ToolType.HELMET.contains(tool.getType()))
                usedAmountItem = 5;
            else if (ToolType.LEGGINGS.contains(tool.getType()))
                usedAmountItem = 7;
            else if (ToolType.CHESTPLATE.contains(tool.getType()))
                usedAmountItem = 8;

            if(usedAmountItem >= 0) {
                player.setItemOnCursor(newTool);
                inv.clear();

                if(modifier.getAmount() > usedAmountItem) {
                    modifier.setAmount(modifier.getAmount() - usedAmountItem);
                    inv.setItem(1, modifier);
                }
            } else {
                Bukkit.getPluginManager().callEvent(new ToolUpgradeEvent(player, newTool, true));

                player.setItemOnCursor(newTool);
                inv.clear();
            }

        } else { //is modifier
            Modifier mod = modManager.getModifierFromItem(modifier);

            if (mod == null) {
                return;
            }

            modifier.setAmount(modifier.getAmount() - 1);
            Bukkit.getPluginManager().callEvent(new ModifierApplyEvent(player, tool, mod, modManager.getFreeSlots(newTool), false));

            if (event.isShiftClick()) {
                if (player.getInventory().addItem(newTool).size() != 0) { //adds items to (full) inventory and then case if inventory is full
                    event.setCancelled(true); //cancels the event if the player has a full inventory
                    return;
                } // no else as it gets added in if-clause

                inv.clear();
                inv.setItem(1, modifier);

                return;
            }

            player.setItemOnCursor(newTool);

            inv.clear();
            inv.setItem(1, modifier);
        }
	}
    
    @EventHandler
    public void onAnvilPrepare(PrepareAnvilEvent event) {
        AnvilInventory inventory = event.getInventory();
        ItemStack tool = inventory.getItem(0);
        ItemStack modifier = inventory.getItem(1);

        if (tool == null || modifier == null) {
            return;
        }

        Player player = null;

        for (HumanEntity entity : event.getViewers()) {
            if (entity instanceof Player) {
                player = (Player) entity;
                break;
            }
        }

        if (player == null) {
            return;
        }

        if (Lists.WORLDS.contains(player.getWorld().getName())) {
            return;
        }

        if (!(modManager.isToolViable(tool) || modManager.isArmorViable(tool))) {
            return;
        }

        if (modifier.getType() == Material.ENCHANTED_BOOK) { //So no Tools can be enchanted via books, if enchanting is disabled
            if (config.getBoolean("AllowEnchanting")) {
                // If enchanting is allowed, don't do anything
                return;
            } else {
                // Otherwise, set the resulting item to AIR to negate the enchant
                event.setResult(new ItemStack(Material.AIR, 0)); //sets ghostitem by client
                return;
            }
        }

        Modifier mod = modManager.getModifierFromItem(modifier);

        ItemStack newTool;

        if (mod != null) {
            newTool = tool.clone();
            if (!modManager.addMod(player, newTool, mod, false)) {
                return;
            }

            event.setResult(newTool);
            inventory.setRepairCost(0);
        } else {
            if (config.getBoolean("Upgradeable") && player.hasPermission("minetinker.tool.upgrade")) {
                ItemStack item = inventory.getItem(1);

                if (item != null) {
                    switch (item.getAmount()) {
                        case 64:
                        case 63:
                        case 62:
                        case 61:
                        case 60:
                        case 59:
                        case 58:
                        case 57:
                        case 56:
                        case 55:
                        case 54:
                        case 53:
                        case 52:
                        case 51:
                        case 50:
                        case 49:
                        case 48:
                        case 47:
                        case 46:
                        case 45:
                        case 44:
                        case 43:
                        case 42:
                        case 41:
                        case 40:
                        case 39:
                        case 38:
                        case 37:
                        case 36:
                        case 35:
                        case 34:
                        case 33:
                        case 32:
                        case 31:
                        case 30:
                        case 29:
                        case 28:
                        case 27:
                        case 26:
                        case 25:
                        case 24:
                        case 23:
                        case 22:
                        case 21:
                        case 20:
                        case 19:
                        case 18:
                        case 17:
                        case 16:
                        case 15:
                        case 14:
                        case 13:
                        case 12:
                        case 11:
                        case 10:
                        case 9:
                        case 8:
                            if (ToolType.CHESTPLATE.contains(tool.getType())) {
                                newTool = ItemGenerator.itemUpgrader(tool.clone(), inventory.getItem(1), player);
                                event.setResult(newTool);
                                inventory.setRepairCost(0);
                            }
                        case 7:
                            if (ToolType.LEGGINGS.contains(tool.getType())) {
                                newTool = ItemGenerator.itemUpgrader(tool.clone(), inventory.getItem(1), player);
                                event.setResult(newTool);
                                inventory.setRepairCost(0);
                            }
                        case 6:
                        case 5:
                            if (ToolType.HELMET.contains(tool.getType())) {
                                newTool = ItemGenerator.itemUpgrader(tool.clone(), inventory.getItem(1), player);
                                event.setResult(newTool);
                                inventory.setRepairCost(0);
                            }
                        case 4:
                            if (ToolType.BOOTS.contains(tool.getType())) {
                                newTool = ItemGenerator.itemUpgrader(tool.clone(), inventory.getItem(1), player);
                                event.setResult(newTool);
                                inventory.setRepairCost(0);
                            }
                        case 3:
                            if (ToolType.AXE.contains(tool.getType()) || ToolType.PICKAXE.contains(tool.getType())) {
                                newTool = ItemGenerator.itemUpgrader(tool.clone(), inventory.getItem(1), player);
                                event.setResult(newTool);
                                inventory.setRepairCost(0);
                            }
                        case 2:
                            if (ToolType.SWORD.contains(tool.getType()) || ToolType.HOE.contains(tool.getType())) {
                                newTool = ItemGenerator.itemUpgrader(tool.clone(), inventory.getItem(1), player);
                                event.setResult(newTool);
                                inventory.setRepairCost(0);
                            }
                        case 1:
                            if (ToolType.SHOVEL.contains(tool.getType())) {
                                newTool = ItemGenerator.itemUpgrader(tool.clone(), inventory.getItem(1), player);
                                event.setResult(newTool);
                                inventory.setRepairCost(0);
                            }
                    }
                }
            }
        }
    }
    */

    @EventHandler(ignoreCancelled = true)
    public void onGrind(InventoryClickEvent event) {
	    if (!NBTUtils.isOneFourteenCompatible()) {
	        return;
        }

	    if (!(event.getInventory() instanceof GrindstoneInventory)) {
	        return;
        }

	    if (event.getSlot() != 9) {
	        return;
        }

	    ItemStack results = event.getCurrentItem();

	    if (modManager.isToolViable(results) || modManager.isArmorViable(results)) {
	        event.setCancelled(true);
        }
    }
}

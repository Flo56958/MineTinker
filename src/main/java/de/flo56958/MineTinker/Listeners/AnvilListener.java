package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.ModifierApplyEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class AnvilListener implements Listener {

    private static final FileConfiguration config = Main.getPlugin().getConfig();
    private static final ModManager modManager = ModManager.instance();
    
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryClick(InventoryClickEvent e) {
		HumanEntity he = e.getWhoClicked();
		
		if(!(he instanceof Player && e.getClickedInventory() instanceof AnvilInventory)) { return; }
        AnvilInventory inv = (AnvilInventory) e.getClickedInventory();
        Player player = (Player) he;

        ItemStack tool = inv.getItem(0);
        ItemStack modifier = inv.getItem(1);
        ItemStack newTool = inv.getItem(2);

        if (tool == null || modifier == null) { return; }

        if (e.getSlot() != 2) { return; }
        if (Lists.WORLDS.contains(player.getWorld().getName())) { return; }
        if (!(modManager.isToolViable(tool) || modManager.isArmorViable(tool))) { return; }

        boolean deleteAllItems = false;
        if (e.getCursor() != null && !e.getCursor().getType().equals(Material.AIR)) { return; }
        if (!modManager.isModifierItem(modifier)) { //something else
            if (newTool != null && tool.getType().equals(newTool.getType())) { return; } //Not an upgrade
            deleteAllItems = true;
        } else { //Modifier-apply
            Modifier mod = modManager.getModifierFromItem(modifier);

            if (mod == null && tool.getType().equals(newTool.getType())) { return; } //Vanilla anvil use
            if (mod != null) {
                if (modifier.getAmount() > 1) {
                    modifier.setAmount(modifier.getAmount() - 1);
                    inv.setItem(1, modifier);
                } else { inv.setItem(1, null); }
                Bukkit.getPluginManager().callEvent(new ModifierApplyEvent(player, tool, mod, modManager.getFreeSlots(newTool), false));
            } else {
                inv.setItem(1, null); //when item upgrading
            }
        }

        if (e.isShiftClick()) {
            if (player.getInventory().addItem(newTool).size() != 0) { //adds items to (full) inventory and then case if inventory is full
                e.setCancelled(true); //cancels the event if the player has a full inventory
                return;
            } // no else as it gets added in if-clause
        } else {
            e.setCursor(newTool);
        }

        inv.setItem(0, null);

        if(deleteAllItems) {
            inv.setItem(1, null);
        }
	}
    
    @EventHandler
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

        if (Lists.WORLDS.contains(player.getWorld().getName())) {
            return;
        }
        if (!(modManager.isToolViable(tool) || modManager.isArmorViable(tool))) {
            return;
        }

        if (modifier.getType().equals(Material.ENCHANTED_BOOK) && !config.getBoolean("AllowEnchanting")) { //So no Tools can be enchanted via books, if enchanting is disabled
            e.setResult(new ItemStack(Material.AIR, 0)); //sets ghostitem by client
            return;
        }

        Modifier mod = modManager.getModifierFromItem(modifier);

        ItemStack newTool = null;

        if (mod != null) {
            newTool = mod.applyMod(player, tool.clone(), false);
        } else {
            if (config.getBoolean("Upgradeable") && player.hasPermission("minetinker.tool.upgrade")) {
                switch (i.getItem(1).getAmount()) {
                    case 1:
                        if (ToolType.SHOVEL.getMaterials().contains(tool.getType())) {
                            newTool = ItemGenerator.itemUpgrader(tool.clone(), i.getItem(1), player);
                        }
                        break;
                    case 2:
                        if (ToolType.SWORD.getMaterials().contains(tool.getType()) || ToolType.HOE.getMaterials().contains(tool.getType())) {
                            newTool = ItemGenerator.itemUpgrader(tool.clone(), i.getItem(1), player);
                        }
                        break;
                    case 3:
                        if (ToolType.AXE.getMaterials().contains(tool.getType()) || ToolType.PICKAXE.getMaterials().contains(tool.getType())) {
                            newTool = ItemGenerator.itemUpgrader(tool.clone(), i.getItem(1), player);
                        }
                        break;
                    case 4:
                        if (ToolType.BOOTS.getMaterials().contains(tool.getType())) {
                            newTool = ItemGenerator.itemUpgrader(tool.clone(), i.getItem(1), player);
                        }
                        break;
                    case 5:
                        if (ToolType.HELMET.getMaterials().contains(tool.getType())) {
                            newTool = ItemGenerator.itemUpgrader(tool.clone(), i.getItem(1), player);
                        }
                        break;
                    case 7:
                        if (ToolType.LEGGINGS.getMaterials().contains(tool.getType())) {
                            newTool = ItemGenerator.itemUpgrader(tool.clone(), i.getItem(1), player);
                        }
                        break;
                    case 8:
                        if (ToolType.CHESTPLATE.getMaterials().contains(tool.getType())) {
                            newTool = ItemGenerator.itemUpgrader(tool.clone(), i.getItem(1), player);
                        }
                        break;
                }
            }
        }
        
        if(newTool != null) {
        	e.setResult(newTool);
        	i.setRepairCost(0);
        }
    }
}

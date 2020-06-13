package de.flo56958.minetinker.listeners;

import de.flo56958.minetinker.data.Lists;
import de.flo56958.minetinker.data.ModifierFailCause;
import de.flo56958.minetinker.events.ModifierApplyEvent;
import de.flo56958.minetinker.events.ModifierFailEvent;
import de.flo56958.minetinker.events.ToolUpgradeEvent;
import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.modifiers.ModManager;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.datatypes.Pair;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Random;

public class AnvilListener implements Listener {

	private static final ModManager modManager = ModManager.instance();

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event) {
		HumanEntity he = event.getWhoClicked();

		if (!(he instanceof Player && event.getClickedInventory() instanceof AnvilInventory)) {
			return;
		}

		AnvilInventory inv = (AnvilInventory) event.getClickedInventory();
		Player player = (Player) he;

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
		if (event.getCursor() != null && !event.getCursor().getType().equals(Material.AIR)) {
			return;
		}

		if (!modManager.isModifierItem(modifier)) { //upgrade
			if (tool.getType().equals(newTool.getType())) return; //Not an upgrade

			if (new Random().nextInt(100) < MineTinker.getPlugin().getConfig().getInt("ChanceToFailToolUpgrade")) {
				newTool = tool;
				Bukkit.getPluginManager().callEvent(new ToolUpgradeEvent(player, newTool, false));
			} else {
				Bukkit.getPluginManager().callEvent(new ToolUpgradeEvent(player, newTool, true));
			}

			// ------ upgrade
			if (event.isShiftClick()) {
				if (player.getInventory().addItem(newTool).size() != 0) { //adds items to (full) inventory and then case if inventory is full
					event.setCancelled(true); //cancels the event if the player has a full inventory
					return;
				} // no else as it gets added in if-clause

				inv.clear();
				return;
			}

			player.setItemOnCursor(newTool);
			inv.clear();
		} else { //is modifier
			Modifier mod = modManager.getModifierFromItem(modifier);

			if (mod == null) {
				return;
			}

			modifier.setAmount(modifier.getAmount() - 1);

			if (new Random().nextInt(100) < MineTinker.getPlugin().getConfig().getInt("ChanceToFailModifierApply")) {
				newTool = tool;
				Bukkit.getPluginManager().callEvent(new ModifierFailEvent(player, tool, mod, ModifierFailCause.PLAYER_FAILURE, false));
			} else {
				Bukkit.getPluginManager().callEvent(new ModifierApplyEvent(player, tool, mod, modManager.getFreeSlots(newTool), false));
			}

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

	@EventHandler(ignoreCancelled = true)
	public void onAnvilPrepare(PrepareAnvilEvent event) {
		AnvilInventory inventory = event.getInventory();
		ItemStack tool = inventory.getItem(0);
		ItemStack modifier = inventory.getItem(1);

		if (tool == null || modifier == null) {
			return;
		}

		//-----
		Player player = null;

		List<HumanEntity> listHumans = event.getViewers();

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

		if (modifier.getType().equals(Material.ENCHANTED_BOOK)) { //So no Tools can be enchanted via books, if enchanting is disabled
			if (MineTinker.getPlugin().getConfig().getBoolean("AllowEnchanting")) {
				// If enchanting is allowed, don't do anything
				return;
			} else {
				// Otherwise, set the resulting item to AIR to negate the enchant
				event.setResult(new ItemStack(Material.AIR, 0)); //sets ghostitem by client
				return;
			}
		}

		Modifier mod = modManager.getModifierFromItem(modifier);

		ItemStack newTool = null;

		if (mod != null) {
			newTool = tool.clone();
			if (!modManager.addMod(player, newTool, mod, false, false, false)) {
				return;
			}
		} else {
			if (MineTinker.getPlugin().getConfig().getBoolean("Upgradeable")
					&& player.hasPermission("minetinker.tool.upgrade")) {
				ItemStack item = inventory.getItem(1);

				if (item != null) {
					Pair<Material, Integer> materialIntegerPair = ModManager.itemUpgrader(tool.getType(), item.getType());
					if (materialIntegerPair != null && materialIntegerPair.x != null) {
						if (item.getAmount() == materialIntegerPair.y) {
							newTool = tool.clone();
							newTool.setType(materialIntegerPair.x);
							modManager.addArmorAttributes(newTool);
							ItemMeta meta = newTool.getItemMeta();
							if(meta instanceof Damageable) {
								((Damageable) meta).setDamage(0);
							}
							newTool.setItemMeta(meta);
						}
					} else {
						Bukkit.getPluginManager().callEvent(new ToolUpgradeEvent(player, tool, false));
					}
				}
			}
		}

		if (newTool != null) {
			event.setResult(newTool);
			inventory.setRepairCost(0);
		}
	}
}

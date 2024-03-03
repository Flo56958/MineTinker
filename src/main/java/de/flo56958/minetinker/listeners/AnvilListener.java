package de.flo56958.minetinker.listeners;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.api.events.ModifierApplyEvent;
import de.flo56958.minetinker.api.events.ModifierFailEvent;
import de.flo56958.minetinker.api.events.ToolUpgradeEvent;
import de.flo56958.minetinker.data.Lists;
import de.flo56958.minetinker.data.ModifierFailCause;
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
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmithingInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class AnvilListener implements Listener {

	private static final ModManager modManager = ModManager.instance();

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onInventoryClick(@NotNull final InventoryClickEvent event) {
		final HumanEntity he = event.getWhoClicked();

		if (!(he instanceof final Player player &&
				((!MineTinker.getPlugin().getConfig().getBoolean("UseSmithingTable", false) && event.getClickedInventory() instanceof AnvilInventory)
						|| (MineTinker.getPlugin().getConfig().getBoolean("UseSmithingTable", false) && event.getClickedInventory() instanceof SmithingInventory)))) {
			return;
		}

		final Inventory inv = event.getInventory();
		final ItemStack item1 = inv.getItem(0);
		final ItemStack item2 = inv.getItem(1);
		ItemStack newTool = inv.getItem(2);

		if (item1 == null || item2 == null || newTool == null) {
			return;
		}

		if (event.getSlot() != 2) {
			return;
		}

		if (Lists.WORLDS.contains(player.getWorld().getName())) {
			return;
		}

		if (!(modManager.isToolViable(item1) || modManager.isArmorViable(item1))) {
			return;
		}

		//boolean deleteAllItems = false;
		if (event.getCursor() != null && !event.getCursor().getType().equals(Material.AIR)) {
			return;
		}

		if (!modManager.isModifierItem(item2)) { //Upgrade or combining
			if (!(item1.getType().equals(newTool.getType())
					&& item1.getType().equals(item2.getType())
					&& (modManager.isToolViable(item2) || modManager.isArmorViable(item2)))) { //Not Combining
				if (item1.getType() == newTool.getType()) { // Vanilla repair
					return;
				}
				if (new Random().nextInt(100) < MineTinker.getPlugin().getConfig().getInt("ChanceToFailToolUpgrade")) {
					newTool = item1;
					Bukkit.getPluginManager().callEvent(new ToolUpgradeEvent(player, newTool, false));
				} else {
					Bukkit.getPluginManager().callEvent(new ToolUpgradeEvent(player, newTool, true));
				}
				final Pair<Material, Integer> materialIntegerPair = ModManager.itemUpgrader(item1.getType(), item2.getType());
				if (materialIntegerPair != null && materialIntegerPair.x() != null) {
					if (newTool.getType() == materialIntegerPair.x() && materialIntegerPair.y() != null) {
						item2.setAmount(item2.getAmount() - materialIntegerPair.y());
					}
				}
			} else {
				inv.clear(1);
			}

			inv.clear(0);
			inv.clear(2);

			if (event.isShiftClick()) {
				if (!player.getInventory().addItem(newTool).isEmpty()) { //adds items to (full) inventory and then case if inventory is full
					event.setCancelled(true); //cancels the event if the player has a full inventory
				} // no else as it gets added in if-clause
			} else {
				player.setItemOnCursor(newTool);
			}
		} else { //is modifier
			final Modifier mod = modManager.getModifierFromItem(item2);

			if (mod == null) {
				return;
			}

			item2.setAmount(item2.getAmount() - 1);

			if (new Random().nextInt(100) < MineTinker.getPlugin().getConfig().getInt("ChanceToFailModifierApply")) {
				newTool = item1;
				Bukkit.getPluginManager().callEvent(new ModifierFailEvent(player, item1, mod, ModifierFailCause.PLAYER_FAILURE, false));
			} else {
				Bukkit.getPluginManager().callEvent(new ModifierApplyEvent(player, item1, mod, modManager.getFreeSlots(newTool), false));
			}

			if (event.isShiftClick()) {
				if (!player.getInventory().addItem(newTool).isEmpty()) { //adds items to (full) inventory and then case if inventory is full
					event.setCancelled(true); //cancels the event if the player has a full inventory
					return;
				} // no else as it gets added in if-clause

				inv.clear();
				inv.setItem(1, item2);

				return;
			}

			player.setItemOnCursor(newTool);

			inv.clear();
			inv.setItem(1, item2);
		}
	}

	public static ItemStack onPrepare(@NotNull Inventory inventory, List<HumanEntity> listHumans) {
		final ItemStack item1 = inventory.getItem(0);
		final ItemStack item2 = inventory.getItem(1);

		if (item1 == null || item2 == null) {
			return null;
		}

		//-----
		Player player = null;

		for (HumanEntity he : listHumans) {
			if (he instanceof Player) {
				player = (Player) he;
				break;
			}
		}

		if (player == null) {
			return null;
		}

		//-----
		if (Lists.WORLDS.contains(player.getWorld().getName())) {
			return null;
		}

		if (!(modManager.isToolViable(item1) || modManager.isArmorViable(item1))) {
			return null;
		}

		if (item2.getType().equals(Material.ENCHANTED_BOOK)) { //So no Tools can be enchanted via books, if enchanting is disabled
			if (!MineTinker.getPlugin().getConfig().getBoolean("AllowEnchanting")) {
				//Set the resulting item to AIR to negate the enchant
				return new ItemStack(Material.AIR, 0); //sets ghostitem by client
			}
			return null;
		}

		final Modifier mod = modManager.getModifierFromItem(item2);

		ItemStack newTool = null;

		if (mod != null) {
			if (!MineTinker.getPlugin().getConfig().getBoolean("UseSmithingTable", false) && inventory instanceof AnvilInventory
					|| MineTinker.getPlugin().getConfig().getBoolean("UseSmithingTable", false) && inventory instanceof SmithingInventory) {
				newTool = item1.clone();
				if (!modManager.addMod(player, newTool, mod, false, false, true, true)) {
					return null;
				}
			}
		} else if (item1.getType().equals(item2.getType()) && inventory instanceof AnvilInventory) { //Whether we're combining the tools
			if ((modManager.isToolViable(item2) || modManager.isArmorViable(item2))
					&& MineTinker.getPlugin().getConfig().getBoolean("Combinable")
					&& player.hasPermission("minetinker.tool.combine")) {
				newTool = item1.clone();

				final List<Modifier> newToolMods = modManager.getToolMods(newTool);
				final List<Modifier> item2Mods = modManager.getToolMods(item2);

				final List<Modifier> sharedModifiers = item2Mods.stream().filter(newToolMods::contains).toList();
				final List<Modifier> uniqueModifiers = item2Mods.stream().filter(Predicate.not(newToolMods::contains)).toList();

				for (Modifier shared : sharedModifiers) {
					final int newToolModLevel = modManager.getModLevel(newTool, shared);
					final int item2ModLevel = modManager.getModLevel(item2, shared);

					if (item2ModLevel > newToolModLevel) { //If the 2nd tool's modifier has a higher level, clamp the level to the 2nd tool's level.
						for (int i = 0; i < item2ModLevel - newToolModLevel; i++) {
							modManager.addMod(player, newTool, shared, false, false, true, false);
						}
					} else if (item2ModLevel == newToolModLevel) {
						modManager.addMod(player, newTool, shared, false, false, true, false);
					}
				}

				for (Modifier unique : uniqueModifiers) {
					final int modLevel = modManager.getModLevel(item2, unique);
					for (int i = 0; i < modLevel; i++) {
						modManager.addMod(player, newTool, unique, false, false, true, false);
					}
				}

				modManager.addExp(player, newTool, modManager.getExp(item2), false);
			}
		} else {
			if (MineTinker.getPlugin().getConfig().getBoolean("Upgradeable")
					&& player.hasPermission("minetinker.tool.upgrade")) {
				if (!MineTinker.getPlugin().getConfig().getBoolean("UseSmithingTable", false) && inventory instanceof AnvilInventory
						|| MineTinker.getPlugin().getConfig().getBoolean("UseSmithingTable", false) && inventory instanceof SmithingInventory) {
					final ItemStack item = inventory.getItem(1);

					if (item != null) {
						final Pair<Material, Integer> materialIntegerPair = ModManager.itemUpgrader(item1.getType(), item.getType());
						if (materialIntegerPair != null && materialIntegerPair.x() != null) {
							if (materialIntegerPair.y() != null && item.getAmount() >= materialIntegerPair.y()) {
								newTool = item1.clone();
								newTool.setType(materialIntegerPair.x());
								modManager.addArmorAttributes(newTool); //The Attributes need to be reapplied
								final ItemMeta meta = newTool.getItemMeta();
								if (meta instanceof Damageable) {
									((Damageable) meta).setDamage(0);
								}
								newTool.setItemMeta(meta);
							}
						} // Do not trigger a unsuccessful upgrade event as it could still be a vanilla repair
					}
				}
			}
		}
		return newTool;
	}

	@EventHandler(ignoreCancelled = true)
	public void onAnvilPrepare(@NotNull final PrepareAnvilEvent event) {
		ItemStack newTool = onPrepare(event.getInventory(), event.getViewers());
		if (newTool != null) {
			event.setResult(newTool);
			event.getInventory().setRepairCost(0);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onSmithPrepare(PrepareSmithingEvent event) {
		if (ModManager.instance().isArmorViable(event.getResult()))
			//The attributes need to be reapplied
			ModManager.instance().addArmorAttributes(event.getResult());

		if (!MineTinker.getPlugin().getConfig().getBoolean("UseSmithingTable", false)) return;

		final ItemStack newTool = onPrepare(event.getInventory(), event.getViewers());
		if (newTool == null) return;

		event.setResult(newTool);
	}
}

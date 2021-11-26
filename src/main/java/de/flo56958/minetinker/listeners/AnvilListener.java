package de.flo56958.minetinker.listeners;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.data.Lists;
import de.flo56958.minetinker.data.ModifierFailCause;
import de.flo56958.minetinker.events.ModifierApplyEvent;
import de.flo56958.minetinker.events.ModifierFailEvent;
import de.flo56958.minetinker.events.ToolUpgradeEvent;
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
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class AnvilListener implements Listener {

	private static final ModManager modManager = ModManager.instance();

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onInventoryClick(@NotNull final InventoryClickEvent event) {
		final HumanEntity he = event.getWhoClicked();

		if (!(he instanceof final Player player && event.getClickedInventory() instanceof final AnvilInventory inv)) {
			return;
		}

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
				if (new Random().nextInt(100) < MineTinker.getPlugin().getConfig().getInt("ChanceToFailToolUpgrade")) {
					newTool = item1;
					Bukkit.getPluginManager().callEvent(new ToolUpgradeEvent(player, newTool, false));
				} else {
					Bukkit.getPluginManager().callEvent(new ToolUpgradeEvent(player, newTool, true));
				}
			}

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
				if (player.getInventory().addItem(newTool).size() != 0) { //adds items to (full) inventory and then case if inventory is full
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

	@EventHandler(ignoreCancelled = true)
	public void onAnvilPrepare(@NotNull final PrepareAnvilEvent event) {
		final AnvilInventory inventory = event.getInventory();
		final ItemStack item1 = inventory.getItem(0);
		final ItemStack item2 = inventory.getItem(1);

		if (item1 == null || item2 == null) {
			return;
		}

		//-----
		Player player = null;

		final List<HumanEntity> listHumans = event.getViewers();

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

		if (!(modManager.isToolViable(item1) || modManager.isArmorViable(item1))) {
			return;
		}

		if (item2.getType().equals(Material.ENCHANTED_BOOK)) { //So no Tools can be enchanted via books, if enchanting is disabled
			if (!MineTinker.getPlugin().getConfig().getBoolean("AllowEnchanting")) {
				//Set the resulting item to AIR to negate the enchant
				event.setResult(new ItemStack(Material.AIR, 0)); //sets ghostitem by client
			}
			return;
		}

		final Modifier mod = modManager.getModifierFromItem(item2);

		ItemStack newTool = null;

		if (mod != null) {
			newTool = item1.clone();
			if (!modManager.addMod(player, newTool, mod, false, false, false, true)) {
				return;
			}
		} else if (item1.getType().equals(item2.getType())) { //Whether we're combining the tools
			if ((modManager.isToolViable(item2) || modManager.isArmorViable(item2))
					&& MineTinker.getPlugin().getConfig().getBoolean("Combinable")
					&& player.hasPermission("minetinker.tool.combine")) {
				newTool = item1.clone();

				for (Modifier tool2Mod : modManager.getToolMods(item2)) {
					int modLevel = modManager.getModLevel(item2, tool2Mod);
					for (int i = 0; i < modLevel; i++) {
						modManager.addMod(player, newTool, tool2Mod, false, false, true, false);
					}
				}

				modManager.addExp(player, newTool, modManager.getExp(item2), false);
			}
		} else {
			if (MineTinker.getPlugin().getConfig().getBoolean("Upgradeable")
					&& player.hasPermission("minetinker.tool.upgrade")) {
				final ItemStack item = inventory.getItem(1);

				if (item != null) {
					final Pair<Material, Integer> materialIntegerPair = ModManager.itemUpgrader(item1.getType(), item.getType());
					if (materialIntegerPair != null && materialIntegerPair.x() != null) {
						if (materialIntegerPair.y() != null && item.getAmount() == materialIntegerPair.y()) {
							newTool = item1.clone();
							newTool.setType(materialIntegerPair.x());
							modManager.addArmorAttributes(newTool); //The Attributes need to be reapplied
							final ItemMeta meta = newTool.getItemMeta();
							if (meta instanceof Damageable) {
								((Damageable) meta).setDamage(0);
							}
							newTool.setItemMeta(meta);
						}
					} else {
						Bukkit.getPluginManager().callEvent(new ToolUpgradeEvent(player, item1, false));
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

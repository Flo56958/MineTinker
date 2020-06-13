package de.flo56958.minetinker.listeners;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.data.Lists;
import de.flo56958.minetinker.modifiers.ModManager;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class ItemListener implements Listener {

	private final ModManager modManager = ModManager.instance();

	@EventHandler(ignoreCancelled = true)
	public void onDespawn(ItemDespawnEvent event) {
		Item item = event.getEntity();
		ItemStack is = item.getItemStack();

		if (!((modManager.isArmorViable(is) || modManager.isToolViable(is) || modManager.isWandViable(is))
				|| (MineTinker.getPlugin().getConfig().getBoolean("ItemBehaviour.ForModItems")
				&& modManager.isModifierItem(is)))) {
			return;
		}

		if (MineTinker.getPlugin().getConfig().getBoolean("ItemBehaviour.SetPersistent")) {
			event.setCancelled(true);
			item.setTicksLived(1);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onItemDrop(PlayerDropItemEvent event) {
		if (MineTinker.getPlugin().getConfig().getBoolean("ItemBehaviour.DisableDroppingBehaviour")) {
			return;
		}

		Item item = event.getItemDrop();
		ItemStack is = item.getItemStack();

		boolean isMineTinker = false;

		if (MineTinker.getPlugin().getConfig().getBoolean("ItemBehaviour.ForModItems")) {
			isMineTinker = modManager.isModifierItem(is);
		}
		if (modManager.isArmorViable(is) || modManager.isToolViable(is) || modManager.isWandViable(is)) {
			isMineTinker = true;
		}

		if (!isMineTinker) {
			return;
		}

		if (MineTinker.getPlugin().getConfig().getBoolean("ItemBehaviour.ShowName") && is.getItemMeta() != null) {
			item.setCustomName(is.getItemMeta().getDisplayName());
			item.setCustomNameVisible(true);
		}

		if (MineTinker.getPlugin().getConfig().getBoolean("ItemBehaviour.SetGlowing")) {
			item.setGlowing(true);
		}

		if (MineTinker.getPlugin().getConfig().getBoolean("ItemBehaviour.SetInvulnerable")) {
			item.setInvulnerable(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (event.getKeepInventory()) {
			return;
		}

		Player player = event.getEntity();
		Inventory inventory = player.getInventory();

		if (!MineTinker.getPlugin().getConfig().getBoolean("ItemBehaviour.ApplyOnPlayerDeath", true)) { //For DeadSouls and other Grave-Plugins
			// TODO: Try to find better handling of this Event or with these Plugins
			return;
		}

		for (ItemStack itemStack : inventory.getContents()) {
			if (itemStack == null) {
				continue; // More consistent nullability in NotNull fields
			}

			boolean isMineTinker = false;

			if (MineTinker.getPlugin().getConfig().getBoolean("ItemBehaviour.ForModItems")) { //Modifieritems
				ItemStack modifierTester = itemStack.clone();
				modifierTester.setAmount(1);

				for (Modifier modifier : modManager.getAllowedMods()) {
					if (modifier.getModItem().equals(modifierTester)) {
						isMineTinker = true;
						break;
					}
				}
			}

			if (modManager.isArmorViable(itemStack) || modManager.isToolViable(itemStack) || modManager.isWandViable(itemStack)) {
				isMineTinker = true;
			}

			if (!isMineTinker) {
				continue;
			}

			if (!MineTinker.getPlugin().getConfig().getBoolean("ItemBehaviour.DisableDroppingBehaviour")) {
				PlayerDropItemEvent dropItemEvent = new PlayerDropItemEvent(player, player.getWorld().dropItem(player.getLocation(), itemStack));
				Bukkit.getPluginManager().callEvent(dropItemEvent); //To trigger item behaviour
				itemStack.setAmount(0);
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onItemBreak(PlayerItemBreakEvent event) {
		Player player = event.getPlayer();
		ItemStack item = event.getBrokenItem();

		if (!MineTinker.getPlugin().getConfig().getBoolean("UnbreakableTools", true)) {
			return;
		}

		if (Lists.WORLDS.contains(player.getWorld().getName())) {
			return;
		}

		if (!modManager.isToolViable(item) && !modManager.isArmorViable(item)) {
			return;
		}

		if (!MineTinker.getPlugin().getConfig().getBoolean("ItemBehaviour.StopBreakEvent", true)) {
			return;
		}

		if (MineTinker.getPlugin().getConfig().getBoolean("ItemBehaviour.AlertPlayerOnBreak", true)) {
			player.sendMessage(LanguageManager.getString("Alert.OnItemBreak", player));
		}

		ItemMeta meta = item.getItemMeta();

		if (meta instanceof Damageable) {
			((Damageable) meta).setDamage(item.getType().getMaxDurability() - 1);
		}

		if (meta instanceof CrossbowMeta) { //TODO: Crossbow will still shoot arrow
			((CrossbowMeta) meta).setChargedProjectiles(new ArrayList<>());
		}

		item.setItemMeta(meta);

		if (player.getInventory().addItem(item).size() != 0) { //adds items to (full) inventory
			if (!MineTinker.getPlugin().getConfig().getBoolean("ItemBehaviour.DisableDroppingBehaviour")) {
				PlayerDropItemEvent dropItemEvent = new PlayerDropItemEvent(player, player.getWorld().dropItem(player.getLocation(), item));
				Bukkit.getPluginManager().callEvent(dropItemEvent); //To trigger item behaviour
			} else {
				player.getWorld().dropItem(player.getLocation(), item);
			}
		} // no else as it gets added in if-clause
	}

	@EventHandler
	public void onItemUse(PlayerItemDamageEvent event) {
		ItemStack item = event.getItem();

		if (modManager.isToolViable(item) && modManager.isArmorViable(item) && modManager.isWandViable(item)) {
			return;
		}

		if (!MineTinker.getPlugin().getConfig().getBoolean("ItemBehaviour.ConvertItemsOnUse", true)) {
			return;
		}

		modManager.convertItemStack(event.getItem(), event.getPlayer());
	}
}

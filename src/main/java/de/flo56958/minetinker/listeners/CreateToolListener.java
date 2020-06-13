package de.flo56958.minetinker.listeners;

import de.flo56958.minetinker.data.Lists;
import de.flo56958.minetinker.modifiers.ModManager;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class CreateToolListener implements Listener {

	private static final ModManager modManager = ModManager.instance();

	@EventHandler
	public void PrepareCraft(PrepareItemCraftEvent event) {
		if (event.getRecipe() == null) {
			return;
		}

		//checking for dye process
		for (ItemStack item : event.getInventory().getMatrix()) {
			if (item == null) continue;
			if (item.getType() == Material.AIR) continue;
			if (!Lists.getLetherArmor().contains(item.getType())) continue; //not leather armor
			if (item.getType() != event.getInventory().getResult().getType()) break; //Not a dye process

			ItemMeta gridMeta = item.getItemMeta();
			ItemMeta resultMeta = event.getInventory().getResult().getItemMeta();
			if (gridMeta instanceof LeatherArmorMeta && resultMeta instanceof LeatherArmorMeta) {
				if (!((LeatherArmorMeta) gridMeta).getColor().equals(((LeatherArmorMeta) resultMeta).getColor()))
					return; //dye process - abort converting
			}
			break;
		}

		Player player = null;

		for (HumanEntity humans : event.getViewers()) {
			if (humans instanceof Player) {
				player = (Player) humans;
			}
		}

		if (player == null) {
			return;
		}

		if (!player.hasPermission("minetinker.tool.create")) {
			return;
		}

		if (Lists.WORLDS.contains(player.getWorld().getName())) {
			return;
		}

		ItemStack currentItem = event.getInventory().getResult();

		if (currentItem == null) {
			return;
		}

		int totalItems = 0;
		ItemStack lastItem = null;

		for (ItemStack item : event.getInventory().getMatrix()) {
			// Keep this null check, it says it's NotNull but bukkit is lying :(
			if (item != null && item.getType() != Material.AIR) {
				totalItems += 1;
				lastItem = item;
			}
		}

		if (lastItem == null) {
			return;
		}

		ItemMeta m = currentItem.getItemMeta();

		if (m != null) {
			if (modManager.isWandViable(currentItem)) {
				return;
			}
		}

		if (totalItems == 1 && lastItem.getType() == currentItem.getType()) {
			currentItem.setAmount(1);
		}

		modManager.convertItemStack(event.getInventory().getResult(), player);
	}
}

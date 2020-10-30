package de.flo56958.minetinker.listeners;

import de.flo56958.minetinker.data.Lists;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.modifiers.ModManager;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ConvertToolListener implements Listener {

	private static final ModManager modManager = ModManager.instance();

	@EventHandler
	public void onCraft(@NotNull final PrepareItemCraftEvent event) {
		final CraftingInventory inv = event.getInventory();

		boolean canConvert = false;
		World world = null;

		HumanEntity humanEntity = null;

		for (HumanEntity human : inv.getViewers()) {
			if (human.hasPermission("minetinker.tool.create")) {
				canConvert = true;
				world = human.getWorld();
				humanEntity = human;
				break;
			}
		}

		if (!canConvert) {
			return;
		}

		if (Lists.WORLDS.contains(world.getName())) {
			return;
		}

		int recipeItems = 0;
		ItemStack lastItem = null;

		for (ItemStack item : inv.getMatrix()) {
			if (item != null && item.getType() != Material.AIR) {
				recipeItems += 1;
				lastItem = item;
			}
		}

		if (recipeItems == 1) {
			if (modManager.isArmorViable(lastItem) || modManager.isToolViable(lastItem) || modManager.isWandViable(lastItem)) {
				inv.setResult(new ItemStack(Material.AIR, 1));
				return;
			}

			if (ToolType.isMaterialCompatible(lastItem.getType())) {
				inv.setResult(lastItem);
				modManager.convertItemStack(event.getInventory().getResult(), humanEntity);
				inv.getResult().setAmount(1);
			}
		}
	}
}

package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Modifier;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class EnchantingListener implements Listener {

	private ModManager modManager = ModManager.instance();

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onTableEnchant(EnchantItemEvent event) {
		if (!ToolType.ALL.contains(event.getItem().getType())) { //Something different (like a book)
			return;
		}
		if (!(modManager.isToolViable(event.getItem()) || modManager.isWandViable(event.getItem()) || modManager.isArmorViable(event.getItem()))) { //not a MineTinker Tool
			return;
		}

		boolean free = !Main.getPlugin().getConfig().getBoolean("EnchantingCostsSlots", true);

		Map<Enchantment, Integer> enchants = event.getEnchantsToAdd();

		for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
			Modifier modifier = modManager.getModifierFromEnchantment(entry.getKey());

			// The modifier may be disabled
			if (modifier != null && modifier.isAllowed()) {
				for (int i = 0; i < entry.getValue(); i++) {
					boolean success = modManager.addMod(event.getEnchanter(), event.getItem(), modifier, free, false, true);

					if (success) {
						int newLevel = enchants.get(entry.getKey()) - 1;

						// If the target level is 0 then just remove from the map instead of setting to 0
						// Not quite sure what happens if it tries to set an enchant with a level of 0
						// It may remove it? Which would be adverse.
						if (newLevel == 0) {
							enchants.remove(entry.getKey());
						} else {
							enchants.put(entry.getKey(), enchants.get(entry.getKey()) - 1);
						}
					}
				}
			}
		}

		// The enchants should be added when calling applyMod
		event.getEnchantsToAdd().clear();
	}

	@EventHandler(ignoreCancelled = true)
	public void onEnchant(EnchantItemEvent event) {
		if (Lists.WORLDS.contains(event.getEnchanter().getWorld().getName())) {
			return;
		}

		if (Main.getPlugin().getConfig().getBoolean("AllowEnchanting")) {
			return;
		}

		ItemStack tool = event.getItem();

		if (modManager.isToolViable(tool) || modManager.isWandViable(tool) || modManager.isArmorViable(tool)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onAnvilPrepare(InventoryClickEvent event) {
		HumanEntity entity = event.getWhoClicked();

		if (!(entity instanceof Player && event.getClickedInventory() instanceof AnvilInventory)) {
			return;
		}

		AnvilInventory inv = (AnvilInventory) event.getClickedInventory();
		Player player = (Player) entity;

		ItemStack tool = inv.getItem(0);
		ItemStack book = inv.getItem(1);
		ItemStack newTool = inv.getItem(2);

		if (tool == null || book == null || newTool == null) {
			return;
		}

		if (book.getType() != Material.ENCHANTED_BOOK) {
			return;
		}

		boolean free = !Main.getPlugin().getConfig().getBoolean("EnchantingCostsSlots", true);

		for (Map.Entry<Enchantment, Integer> entry : newTool.getEnchantments().entrySet()) {
			int oldEnchantLevel = tool.getEnchantmentLevel(entry.getKey());

			if (oldEnchantLevel < entry.getValue()) {
				int difference = entry.getValue() - oldEnchantLevel;
				Modifier modifier = ModManager.instance().getModifierFromEnchantment(entry.getKey());

				newTool.removeEnchantment(entry.getKey());
				if (modifier != null) {
					for (int i = 0; i < difference; i++) {
						modManager.addMod(player, newTool, modifier, free, false, true);
					}
				}
			}
		}

		// TODO: Refund enchantment levels lost due to removeEnchantment and addMod
	}
}

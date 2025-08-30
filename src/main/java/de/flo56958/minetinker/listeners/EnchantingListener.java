package de.flo56958.minetinker.listeners;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.data.Lists;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.modifiers.ModManager;
import de.flo56958.minetinker.modifiers.Modifier;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.view.AnvilView;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Map;

public class EnchantingListener implements Listener {

	private final ModManager modManager = ModManager.instance();

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onTableEnchant(@NotNull final EnchantItemEvent event) {
		if (!MineTinker.getPlugin().getConfig().getBoolean("ConvertEnchantmentsOnEnchant", true)) return;
		if (!ToolType.ALL.contains(event.getItem().getType())) return; //Something different (like a book)
		if (!modManager.isToolViable(event.getItem()) && !modManager.isArmorViable(event.getItem())) //not a MineTinker Tool
			return;

		final boolean free = !MineTinker.getPlugin().getConfig().getBoolean("EnchantingCostsSlots", true);

		final Map<Enchantment, Integer> enchants = event.getEnchantsToAdd();
		final HashSet<Enchantment> toremove = new HashSet<>();

		for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
			final Modifier modifier = modManager.getModifierFromEnchantment(entry.getKey());

			// The modifier may be disabled
			if (modifier == null || !modifier.isAllowed()) continue;

			for (int i = 0; i < entry.getValue(); i++) {
				if (!modManager.addMod(event.getEnchanter(), event.getItem(), modifier,
						false, false, true, free)) {
					if (MineTinker.getPlugin().getConfig().getBoolean("RefundLostEnchantmentsAsItems", true)) {
						for (; i < entry.getValue(); i++) { //Drop lost enchantments due to some error in addMod
							if (!event.getEnchanter().getInventory().addItem(modifier.getModItem()).isEmpty()) { //adds items to (full) inventory
								event.getEnchanter().getWorld().dropItem(event.getEnchanter().getLocation(), modifier.getModItem());
							} // no else as it gets added in if-clause
						}
					}
					break;
				}
			}
			toremove.add(entry.getKey());
		}

		toremove.forEach(v -> enchants.put(v, 0));
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBookEnchant(@NotNull final InventoryClickEvent event) {
		if (!MineTinker.getPlugin().getConfig().getBoolean("ConvertEnchantmentsOnEnchant", true)) return;
		final HumanEntity entity = event.getWhoClicked();

		if (!(entity instanceof final Player player))
			return;

		if (!(event.getClickedInventory() instanceof final AnvilInventory inv && event.getView() instanceof AnvilView view))
			return;


		final ItemStack tool = inv.getItem(0);
		final ItemStack book = inv.getItem(1);
		final ItemStack newTool = inv.getItem(2);

		if (!modManager.isToolViable(tool) && !modManager.isArmorViable(tool)) //not a MineTinker Tool
			return;

		if (book == null || newTool == null) return;
		if (book.getType() != Material.ENCHANTED_BOOK) return;
		if (event.getResult() != Event.Result.ALLOW) return;
		if (player.getLevel() < view.getRepairCost()) return; // Player does not have enough levels

		final boolean free = !MineTinker.getPlugin().getConfig().getBoolean("EnchantingCostsSlots", true);

		for (final Map.Entry<Enchantment, Integer> entry : newTool.getEnchantments().entrySet()) {
			final int oldEnchantLevel = tool.getEnchantmentLevel(entry.getKey());
			if (oldEnchantLevel >= entry.getValue()) continue;

			final int difference = entry.getValue() - oldEnchantLevel;
			final Modifier modifier = ModManager.instance().getModifierFromEnchantment(entry.getKey());
			if (modifier == null || !modifier.isAllowed()) continue;

			for (int i = 0; i < difference; i++) {
				//Adding necessary slots
				if (free)
					modManager.setFreeSlots(newTool, modManager.getFreeSlots(newTool) + modifier.getSlotCost());
				if (!modManager.addMod(player, newTool, modifier,
						false, false, true, true)) {
					//Remove slots as they were not needed
					if (free)
						modManager.setFreeSlots(newTool, modManager.getFreeSlots(newTool) - modifier.getSlotCost());
					if (MineTinker.getPlugin().getConfig().getBoolean("RefundLostEnchantmentsAsItems", true)
							&& event.getSlotType() == InventoryType.SlotType.RESULT) {
						for (; i < difference; i++) { //Drop lost enchantments due to some error in addMod
							if (!player.getInventory().addItem(modifier.getModItem()).isEmpty()) { //adds items to (full) inventory
								player.getWorld().dropItem(player.getLocation(), modifier.getModItem());
							} // no else as it gets added in if-clause
						}
					}
					break;
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
	public void onEnchant(@NotNull final PrepareItemEnchantEvent event) {
		if (Lists.WORLDS.contains(event.getEnchanter().getWorld().getName())) return;

		final ItemStack tool = event.getItem();
		if (!modManager.isToolViable(tool) && !modManager.isArmorViable(tool)) return;

		if (!MineTinker.getPlugin().getConfig().getBoolean("AllowEnchanting"))
			event.setCancelled(true);

		if (MineTinker.getPlugin().getConfig().getBoolean("EnchantingCostsSlots", true) && modManager.getFreeSlots(tool) == 0)
			event.setCancelled(true);
	}
}

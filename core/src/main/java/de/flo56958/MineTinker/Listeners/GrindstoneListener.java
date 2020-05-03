package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class GrindstoneListener implements Listener {

	private final ModManager modManager = ModManager.instance();

	private final HashMap<Player, GrindstoneSave> save = new HashMap<>();

	private static class GrindstoneSave {
		final ArrayList<ItemStack> itemStacks = new ArrayList<>();
		int slots;
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onGrind(InventoryClickEvent event) {
		FileConfiguration config = Main.getPlugin().getConfig();

		if (!(event.getInventory() instanceof GrindstoneInventory)) {
			return;
		}

		if (!(event.getWhoClicked() instanceof Player)) {
			return;
		}

		Player player = (Player) event.getWhoClicked();

		if (config.getBoolean("Grindstone.Enabled")) {
			if (event.getClick() != ClickType.LEFT && event.getClick() != ClickType.RIGHT) {
				event.setCancelled(true);
				return;
			}
			if (event.getSlotType() == InventoryType.SlotType.CRAFTING) { //on Prepare
				//Both slots should be null as the one item is on Cursor
				ItemStack slot1 =  event.getClickedInventory().getItem(0);
				ItemStack slot2 =  event.getClickedInventory().getItem(1);
				ItemStack cursorItem = event.getCursor();
				if (slot1 != null || slot2 != null || cursorItem == null) {
					//Illegal state
					if (modManager.isToolViable(cursorItem) || modManager.isArmorViable(cursorItem)) {
						event.setResult(Event.Result.DENY);
						Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> event.getClickedInventory().setItem(2, null), 1);
					}
					return;
				}
				//check for MT item
				if (!modManager.isToolViable(cursorItem) && !modManager.isArmorViable(cursorItem)) {
					return;
				}

				ItemStack result = cursorItem.clone();
				GrindstoneSave gs = new GrindstoneSave();
				Random rand = new Random();
				int amount = 0;
				boolean hadMods = false;
				for (Modifier mod : ModManager.instance().getAllMods()) {
					int level = ModManager.instance().getModLevel(result, mod);
					amount += level * mod.getSlotCost();
					for(int i = 0; i < level; i++) {
						hadMods = true;
						//Test for getting slot back
						if (rand.nextInt(100) < config.getInt("Grindstone.ChanceToGetSlotsBack")) {
							gs.slots += mod.getSlotCost();
						}
						//Test for getting modifier item back
						if (rand.nextInt(100) < config.getInt("Grindstone.ChanceToGetModifierItemBack")) {
							gs.itemStacks.add(mod.getModItem());
						}
					}
					ModManager.instance().removeMod(result, mod);
				}

				if (!hadMods) {
					Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> event.getClickedInventory().setItem(2, null), 1);
					return;
				}

				if (config.getInt("Grindstone.ChanceToGetSlotsBack") > 0) {
					ItemMeta meta = result.getItemMeta();
					List<String> lore = meta.getLore();
					lore.add(0, ChatColor.WHITE +
							LanguageManager.getString("GrindStone.PossibleSlotsAfterGrind")
							.replaceAll("%amount", String.valueOf(amount + modManager.getFreeSlots(result))));
					meta.setLore(lore);
					result.setItemMeta(meta);
				}

				event.setResult(Event.Result.ALLOW);
				//so the item gets not overwritten by vanilla
				Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> event.getClickedInventory().setItem(2, result), 1);
				save.put(player, gs);
			} else if (event.getSlotType() == InventoryType.SlotType.RESULT) { //on Gridstone use
				ItemStack result = event.getCurrentItem();
				if (!modManager.isArmorViable(result) && !modManager.isToolViable(result)) {
					return;
				}

				GrindstoneSave gs = save.remove(player);
				if (gs == null) {
					event.setCancelled(true);
					ChatWriter.sendActionBar(player,
							LanguageManager.getString("Alert.InternalError", player));
					return;
				}

				if (config.getInt("Grindstone.ChanceToGetSlotsBack") > 0) {
					ItemMeta meta = result.getItemMeta();
					List<String> lore = meta.getLore();
					lore.remove(0);
					meta.setLore(lore);
					result.setItemMeta(meta);
				}
				modManager.setFreeSlots(result, modManager.getFreeSlots(result) + gs.slots);

				for (ItemStack stack : gs.itemStacks) {
					if (player.getInventory().addItem(stack).size() != 0) { //adds items to (full) inventory
						player.getWorld().dropItem(player.getLocation(), stack);
					} // no else as it gets added in if-clause
				}
			}
		} else {
			// Avoid handling the clicks inside of the inventory.
			if (event.getSlotType() != InventoryType.SlotType.RESULT && event.getSlotType() != InventoryType.SlotType.CRAFTING) {
				return;
			}
			// Works fine even if the getItem method returns null.
			ItemStack slot1 =  event.getClickedInventory().getItem(0);
			ItemStack slot2 =  event.getClickedInventory().getItem(1);

			if (!(modManager.isToolViable(slot1) || modManager.isArmorViable(slot1) || modManager.isToolViable(slot2) || modManager.isArmorViable(slot2))) {
				return;
			}

			if (event.getSlotType() != InventoryType.SlotType.RESULT) {
				return;
			}

			event.setResult(Event.Result.DENY);
			event.setCancelled(true);

			ChatWriter.sendActionBar(player, LanguageManager.getString("Alert.OnItemGrind", player));
			if (config.getBoolean("Sound.OnFail")) {
				player.playSound(player.getLocation(), Sound.BLOCK_CHEST_LOCKED, 1.0F, 2F);
			}
		}
	}
}
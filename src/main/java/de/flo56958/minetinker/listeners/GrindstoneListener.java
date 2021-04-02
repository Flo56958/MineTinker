package de.flo56958.minetinker.listeners;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.modifiers.ModManager;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class GrindstoneListener implements Listener {

	private final ModManager modManager = ModManager.instance();

	private final HashMap<Player, GrindstoneSave> save = new HashMap<>();

	private static final class GrindstoneSave {
		final ArrayList<ItemStack> itemStacks = new ArrayList<>();

		ItemStack tool;
		int slots;
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onGrind(@NotNull final InventoryClickEvent event) {
		final FileConfiguration config = MineTinker.getPlugin().getConfig();

		if (!(event.getInventory() instanceof GrindstoneInventory)) {
			return;
		}

		if (!(event.getWhoClicked() instanceof Player)) {
			return;
		}

		final Player player = (Player) event.getWhoClicked();

		if (config.getBoolean("Grindstone.Enabled")) {
			if (event.getClick() != ClickType.LEFT && event.getClick() != ClickType.RIGHT) {
				event.setCancelled(true);
				return;
			}
			if (event.getSlotType() == InventoryType.SlotType.CRAFTING) { //on Prepare
				//Both slots should be null as the one item is on Cursor
				final ItemStack slot1 =  event.getClickedInventory().getItem(0);
				final ItemStack slot2 =  event.getClickedInventory().getItem(1);
				final ItemStack cursorItem = event.getCursor();
				if (slot1 != null || slot2 != null || cursorItem == null) {
					//Illegal state
					if (modManager.isToolViable(cursorItem) || modManager.isArmorViable(cursorItem)) {
						event.setResult(Event.Result.DENY);
						Bukkit.getScheduler().runTaskLater(MineTinker.getPlugin(),
								() -> event.getClickedInventory().setItem(2, null), 1);
					}
					return;
				}
				//check for MT item
				if (!modManager.isToolViable(cursorItem) && !modManager.isArmorViable(cursorItem)) {
					return;
				}

				final ItemStack result = cursorItem.clone();
				final GrindstoneSave gs = new GrindstoneSave();
				final Random rand = new Random();
				int amount = 0;
				boolean hadMods = false;
				gs.tool = result;
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

				//Remove remaining non-modifier enchants
				final ItemMeta resultMeta = result.getItemMeta();
				if (resultMeta != null) {
					final Map<Enchantment, Integer> enchants = resultMeta.getEnchants();
					if (!enchants.isEmpty()) {
						hadMods = true;
						for (Enchantment enchant : enchants.keySet()) {
							resultMeta.removeEnchant(enchant);
						}
					}
					result.setItemMeta(resultMeta);
				}

				if (!hadMods) {
					Bukkit.getScheduler().runTaskLater(MineTinker.getPlugin(),
							() -> event.getClickedInventory().setItem(2, null), 2);
					return;
				}

				if (config.getInt("Grindstone.ChanceToGetSlotsBack") > 0) {
					final ItemMeta meta = result.getItemMeta();
					final List<String> lore = meta.getLore();
					if (lore != null) {
						lore.add(0, ChatColor.WHITE +
								LanguageManager.getString("GrindStone.PossibleSlotsAfterGrind")
										.replaceAll("%amount",
												String.valueOf(amount + modManager.getFreeSlots(result))));
						meta.setLore(lore);
						result.setItemMeta(meta);
					}
				}

				event.setResult(Event.Result.ALLOW);

				//So the wanted result item does not get overwritten by vanilla
				//This is a workaround as you can not set the output in the event directly
				//It has its problems and causes bugged states and result items but those states can be
				//identified by checking the known "normal" item saved in GrindstoneSave when the item gets clicked
				//by the player.
				//This has mayor drawback e.g. it is highly incompatible with other grindstone plugins
				//FIXME: Find a better workaround to set the output of the Grindstone
				Bukkit.getScheduler().runTaskLater(MineTinker.getPlugin(),
						() -> event.getClickedInventory().setItem( /*Resultslot*/ 2, result), 1);
				save.put(player, gs);
			} else if (event.getSlotType() == InventoryType.SlotType.RESULT) { //on Gridstone use
				final ItemStack result = event.getCurrentItem();
				if (!modManager.isArmorViable(result) && !modManager.isToolViable(result)) {
					return;
				}

				final GrindstoneSave gs = save.remove(player);
				//Check for bugged state
				if (gs == null || !gs.tool.equals(result)) {
					event.setCancelled(true);
					ChatWriter.sendActionBar(player,
							LanguageManager.getString("Alert.InternalError", player));
					if (config.getBoolean("Sound.OnFail")) {
						player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 2F);
					}
					return;
				}

				if (config.getInt("Grindstone.ChanceToGetSlotsBack") > 0) {
					final ItemMeta meta = result.getItemMeta();
					final List<String> lore = meta.getLore();
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
			if (event.getSlotType() != InventoryType.SlotType.RESULT
					&& event.getSlotType() != InventoryType.SlotType.CRAFTING) {
				return;
			}
			// Works fine even if the getItem method returns null.
			final Inventory inv = event.getClickedInventory();
			if (inv != null) {
				final ItemStack slot1 = inv.getItem(0);
				final ItemStack slot2 = inv.getItem(1);

				if (!(modManager.isToolViable(slot1) || modManager.isArmorViable(slot1)
						|| modManager.isToolViable(slot2) || modManager.isArmorViable(slot2))) {
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
}
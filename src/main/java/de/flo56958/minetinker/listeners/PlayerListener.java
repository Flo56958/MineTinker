package de.flo56958.minetinker.listeners;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.data.Lists;
import de.flo56958.minetinker.modifiers.ModManager;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.modifiers.types.Drilling;
import de.flo56958.minetinker.modifiers.types.Power;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.LanguageManager;
import de.flo56958.minetinker.utils.Updater;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class PlayerListener implements Listener {

	private static final ModManager modManager = ModManager.instance();

	@EventHandler(ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event) {
		if (Lists.WORLDS.contains(event.getWhoClicked().getWorld().getName())) {
			return;

		}
		if (event.getSlot() < 0) {
			return;
		}

		if (!(event.getClickedInventory() instanceof PlayerInventory
				|| event.getClickedInventory() instanceof DoubleChestInventory)) {
			return;
		}

		ItemStack tool = event.getClickedInventory().getItem(event.getSlot());

		if (tool == null) {
			return;
		}

		if (!(modManager.isToolViable(tool) || modManager.isWandViable(tool) || modManager.isArmorViable(tool))) {
			return;
		}

		if (!(MineTinker.getPlugin().getConfig().getBoolean("Repairable")
				&& event.getWhoClicked().hasPermission("minetinker.tool.repair"))) {
			return;
		}

		ItemStack repair = event.getWhoClicked().getItemOnCursor();
		if (repair == null) return;

		ItemMeta repairMeta = repair.getItemMeta();
		if(repairMeta != null) {
			if (repairMeta.hasDisplayName() || repairMeta.hasLore()) return;
		}

		String[] name = tool.getType().toString().split("_");

		boolean eligible = false;

		String beginning = name[0].toLowerCase();

		switch (beginning) {
			case "shield":
			case "wooden":
				if (Lists.getWoodPlanks().contains(repair.getType())) {
					eligible = true;
				}
				break;
			case "stone":
				if (repair.getType() == Material.COBBLESTONE || repair.getType() == Material.STONE) {
					eligible = true;
				}
				break;
			case "shears":
			case "iron":
				if (repair.getType() == Material.IRON_INGOT) {
					eligible = true;
				}
				break;
			case "golden":
				if (repair.getType() == Material.GOLD_INGOT) {
					eligible = true;
				}
				break;
			case "diamond":
				if (repair.getType() == Material.DIAMOND) {
					eligible = true;
				}
				break;
			case "bow":
			case "crossbow":
			case "fishing":
				if (repair.getType() == Material.STICK || repair.getType() == Material.STRING) {
					eligible = true;
				}
				break;
			case "leather":
				if (repair.getType() == Material.LEATHER) {
					eligible = true;
				}
				break;
			case "chainmail":
				if (repair.getType() == Material.IRON_BARS) {
					eligible = true;
				}
				break;
			case "elytra":
				if (repair.getType() == Material.PHANTOM_MEMBRANE) {
					eligible = true;
				}
				break;
			case "trident":
				if (repair.getType() == Material.PRISMARINE_SHARD) {
					eligible = true;
				}
				break;
			case "turtle":
				if (repair.getType() == Material.SCUTE) {
					eligible = true;
				}
				break;
			case "netherite":
				if (repair.getType() == Material.NETHERITE_INGOT) {
					eligible = true;
				}
		}

		if (eligible) {
			Damageable meta = (Damageable) tool.getItemMeta();

			if (meta == null) {
				return;
			}

			//TODO: change fix amount
			int dura = meta.getDamage();
			short maxDura = tool.getType().getMaxDurability();
			int amount = event.getWhoClicked().getItemOnCursor().getAmount();
			float percent = (float) MineTinker.getPlugin().getConfig().getDouble("DurabilityPercentageRepair");

			while (amount > 0 && dura > 0) {
				dura = Math.round(dura - (maxDura * percent));
				amount--;
			}

			if (dura < 0) {
				dura = 0;
			}

			meta.setDamage(dura);
			tool.setItemMeta((ItemMeta) meta);

			event.getWhoClicked().getItemOnCursor().setAmount(amount);
			event.setCancelled(true);
		}
	}


	/**
	 * Adds the Player to the HashMaps BLOCKFACE and HASPOWER
	 *
	 * @param event PlayerJoinEvent
	 */
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Lists.BLOCKFACE.put(player, null);
		Power.HAS_POWER.computeIfAbsent(player, p -> new AtomicBoolean(false));
		Drilling.HAS_DRILLING.computeIfAbsent(player, p -> new AtomicBoolean(false));

		if (MineTinker.getPlugin().getConfig().getBoolean("CheckForUpdates")) {
			if (player.hasPermission("minetinker.update.notify")) {
				if (Updater.hasUpdate()) {
					ChatWriter.sendMessage(player, ChatColor.GOLD,
							LanguageManager.getString("Updater.UpdateAvailable", player));
					ChatWriter.sendMessage(player, ChatColor.WHITE,
							LanguageManager.getString("Updater.YourVersion", player)
							.replace("%ver", MineTinker.getPlugin().getDescription().getVersion()));
					ChatWriter.sendMessage(player, ChatColor.WHITE,
							LanguageManager.getString("Updater.OnlineVersion", player)
							.replace("%ver", Updater.getOnlineVersion()));
				}
			}
		}
		if (player.isOp() || player.hasPermission("minetinker.commands.editconfigbroadcast")) {
			if (LanguageManager.isUsingFallback()) {
				ChatWriter.sendMessage(player, ChatColor.RED,
						"MineTinker is using the fallback language en_US as "
								+ MineTinker.getPlugin().getConfig().getString("Language")
								+ " is not currently supported. If you want MineTinker to support this language you "
								+ "can help translating on Transifex!");
			} else {
				if (!LanguageManager.isComplete()
						&& MineTinker.getPlugin().getConfig().getBoolean("LanguageManagerNotifyOP", true)) {
					Long langCompleteness = LanguageManager.getCompleteness();
					ChatWriter.sendMessage(player, ChatColor.RED, "The translation you are using is only "
							+ langCompleteness / 100 + "." + langCompleteness % 100
							+ "% complete. The missing strings will be loaded from the Language 'en_US'!");
				}
			}
		}
	}

	/**
	 * Removes the Player form the HashMaps BLOCKFACE and HASPOWER
	 *
	 * @param event PlayerQuitEvent
	 */
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Lists.BLOCKFACE.remove(event.getPlayer());
		Power.HAS_POWER.remove(event.getPlayer());
		Drilling.HAS_DRILLING.remove(event.getPlayer());
	}

	/**
	 * Updates the HashMap BLOCKFACE with the clicked face of the Block
	 *
	 * @param event PlayerInteractEvent
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void onInteract(PlayerInteractEvent event) {
		if (Lists.WORLDS.contains(event.getPlayer().getWorld().getName())) {
			return;
		}

		if (!event.getBlockFace().equals(BlockFace.SELF)) {
			Lists.BLOCKFACE.replace(event.getPlayer(), event.getBlockFace());
		}

		if (!modManager.allowBookToModifier()) {
			return;
		}

		if (event.getClickedBlock() == null || event.getClickedBlock().getType() != Material.BOOKSHELF) {
			return;
		}

		if (event.getItem() == null || event.getItem().getType() != Material.ENCHANTED_BOOK) {
			return;
		}

		if (event.getItem().getItemMeta() == null || !(event.getItem().getItemMeta() instanceof EnchantmentStorageMeta)) {
			return;
		}

		EnchantmentStorageMeta meta = (EnchantmentStorageMeta) event.getItem().getItemMeta();

		for (Map.Entry<Enchantment, Integer> entry : meta.getStoredEnchants().entrySet()) {
			Modifier modifier = modManager.getModifierFromEnchantment(entry.getKey());

			if (modifier == null) {
				continue;
			}

			ItemStack modDrop = modifier.getModItem();
			modDrop.setAmount(entry.getValue());

			event.getClickedBlock().getWorld().dropItem(event.getClickedBlock().getLocation(), modDrop);

			meta.removeStoredEnchant(entry.getKey());
		}

		if (meta.getStoredEnchants().isEmpty()) {
			event.getPlayer().getInventory().removeItem(event.getItem());
		} else {
			event.getItem().setItemMeta(meta);
		}
	}
}

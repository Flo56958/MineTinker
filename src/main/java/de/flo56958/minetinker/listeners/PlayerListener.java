package de.flo56958.minetinker.listeners;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.data.Lists;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.modifiers.ModManager;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.modifiers.types.Power;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.LanguageManager;
import de.flo56958.minetinker.utils.Updater;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
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
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class PlayerListener implements Listener {

	private static final ModManager modManager = ModManager.instance();

	@EventHandler(ignoreCancelled = true)
	public void onInventoryClick(@NotNull final InventoryClickEvent event) {
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

		final ItemStack tool = event.getCurrentItem();

		if (tool == null) {
			return;
		}

		if (!(modManager.isToolViable(tool) || modManager.isWandViable(tool) || modManager.isArmorViable(tool))) {
			return;
		}

		if (!(event.getWhoClicked() instanceof Player)) {
			return;
		}

		//There is a duplication bug in creative, the event does not get executed correctly somehow
		//TODO: Remove if paper/spigot/minecraft bug is resolved
		//The feature is therefore disabled for creative, should be very low priority to fix as if you are in creative
		//you should be able to execute a few commands as well
		if (event.getWhoClicked().getGameMode() == GameMode.CREATIVE || event.getWhoClicked().getGameMode() == GameMode.SPECTATOR) {
			return;
		}

		final ItemStack repair = event.getCursor();
		if (repair == null) return;

		//Check if the player wants to modify the tool
		if (modManager.isModifierItem(repair)) {
			if (!MineTinker.getPlugin().getConfig().getBoolean("ModifiableInInventory"))
				return;

			Modifier mod = modManager.getModifierFromItem(repair);
			if (mod != null) { //shouldn't be necessary
				while(repair.getAmount() > 0) {
					if (modManager.addMod((Player) event.getWhoClicked(), tool, mod,
							false, false, false)) {
						//Mod was successful
						//Decrement item count
						repair.setAmount(repair.getAmount() - 1);
					} else {
						//Mod was unsuccessful
						break;
					}
				}

				event.setCancelled(true);
				return;
			}
		}

		//Check if the player can repair
		if (!(MineTinker.getPlugin().getConfig().getBoolean("Repairable")
				&& event.getWhoClicked().hasPermission("minetinker.tool.repair"))) {
			return;
		}

		final ItemMeta repairMeta = repair.getItemMeta();
		if (repairMeta != null) {
			if (repairMeta.hasDisplayName() || repairMeta.hasLore()) return;
		}

		boolean eligible = false;

		final String beginning = tool.getType().toString().split("_")[0].toLowerCase();

		//check if correct material is used
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
			case "netherite":
				if (repair.getType() == Material.NETHERITE_INGOT) {
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
		}

		if (eligible) {
			final Damageable meta = (Damageable) tool.getItemMeta();

			if (meta == null) {
				return;
			}

			int dura = meta.getDamage();
			final short maxDura = tool.getType().getMaxDurability();
			int amount = event.getWhoClicked().getItemOnCursor().getAmount();

			//Calculate the maximum required Materials to restore to full
			int requiredMaterial;
			switch (ToolType.get(tool.getType())) {
				case AXE:
				case PICKAXE:
				case FISHINGROD:
				case CROSSBOW:
				case BOW:
					requiredMaterial = 3;
					break;
				case BOOTS:
					requiredMaterial = 4;
					break;
				case CHESTPLATE:
				case ELYTRA:
					requiredMaterial = 8;
					break;
				case HELMET:
					requiredMaterial = 5;
					break;
				case HOE:
				case TRIDENT:
				case SWORD:
				case SHEARS:
				case OTHER:
					requiredMaterial = 2;
					break;
				case LEGGINGS:
					requiredMaterial = 7;
					break;
				case SHIELD:
					requiredMaterial = 6;
					break;
				case SHOVEL:
					requiredMaterial = 1;
					break;
				default:
					return;
			}

			final float percent = 1.0f / requiredMaterial;

			while (amount > 0 && dura > 0) {
				dura = Math.max(Math.round(dura - (maxDura * percent)), 0);
				amount--;
			}

			meta.setDamage(dura);
			tool.setItemMeta((ItemMeta) meta);

			repair.setAmount(amount);
			event.setCancelled(true);
		}
	}


	/**
	 * Adds the Player to the HashMaps BLOCKFACE and HASPOWER
	 *
	 * @param event PlayerJoinEvent
	 */
	@EventHandler
	public void onJoin(@NotNull final PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		Lists.BLOCKFACE.put(player, BlockFace.SELF);
		Power.HAS_POWER.computeIfAbsent(player, p -> new AtomicBoolean(false));

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
					final Long langCompleteness = LanguageManager.getCompleteness();
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
	public void onQuit(@NotNull final PlayerQuitEvent event) {
		Lists.BLOCKFACE.remove(event.getPlayer());
		Power.HAS_POWER.remove(event.getPlayer());
	}

	/**
	 * Updates the HashMap BLOCKFACE with the clicked face of the Block
	 *
	 * @param event PlayerInteractEvent
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void onInteract(@NotNull final PlayerInteractEvent event) {
		if (Lists.WORLDS.contains(event.getPlayer().getWorld().getName())) {
			return;
		}

		if (!event.getBlockFace().equals(BlockFace.SELF)) {
			if (!Power.HAS_POWER.getOrDefault(event.getPlayer(), new AtomicBoolean(false)).get())
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

		if (event.getItem().getItemMeta() == null || !(event.getItem().getItemMeta() instanceof final EnchantmentStorageMeta meta)) {
			return;
		}

		for (Map.Entry<Enchantment, Integer> entry : meta.getStoredEnchants().entrySet()) {
			final Modifier modifier = modManager.getModifierFromEnchantment(entry.getKey());

			if (modifier == null) {
				continue;
			}

			final ItemStack modDrop = modifier.getModItem();
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

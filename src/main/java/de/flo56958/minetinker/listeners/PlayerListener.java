package de.flo56958.minetinker.listeners;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.data.Lists;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.modifiers.ModManager;
import de.flo56958.minetinker.modifiers.Modifier;
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
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class PlayerListener implements Listener {

	private static final ModManager modManager = ModManager.instance();

	@EventHandler(ignoreCancelled = true)
	public void onConsume(@NotNull final PlayerItemConsumeEvent event) {
		event.setCancelled(modManager.isModifierItem(event.getItem()));
	}

	@EventHandler(ignoreCancelled = true)
	public void onInventoryClick(@NotNull final InventoryClickEvent event) {
		if (Lists.WORLDS.contains(event.getWhoClicked().getWorld().getName())) return;

		if (event.getSlot() < 0) return;

		if (!(event.getClickedInventory() instanceof PlayerInventory
				|| event.getClickedInventory() instanceof DoubleChestInventory))
			return;

		final ItemStack tool = event.getCurrentItem();
		if (tool == null) return;

		if (!(modManager.isToolViable(tool) || modManager.isWandViable(tool) || modManager.isArmorViable(tool))) return;
		if (!(event.getWhoClicked() instanceof Player)) return;

		//There is a duplication bug in creative, the event does not get executed correctly somehow
		//TODO: Remove if paper/spigot/minecraft bug is resolved
		//The feature is therefore disabled for creative, should be very low priority to fix as if you are in creative
		//you should be able to execute a few commands as well
		if (event.getWhoClicked().getGameMode() == GameMode.CREATIVE || event.getWhoClicked().getGameMode() == GameMode.SPECTATOR) return;

		final ItemStack repair = event.getCursor();
		if (repair == null) return;

		//Check if the player wants to modify the tool
		if (modManager.isModifierItem(repair)) {
			if (!MineTinker.getPlugin().getConfig().getBoolean("ModifiableInInventory"))
				return;

			final Modifier mod = modManager.getModifierFromItem(repair);
			if (mod != null) { //shouldn't be necessary
				while(repair.getAmount() > 0) {
					if (modManager.addMod((Player) event.getWhoClicked(), tool, mod,
							false, false, false, true)) {
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
				&& event.getWhoClicked().hasPermission("minetinker.tool.repair")))
			return;

		final ItemMeta repairMeta = repair.getItemMeta();
		if (repairMeta != null)
			if (repairMeta.hasDisplayName() || repairMeta.hasLore()) return;

		boolean eligible = false;

		final String beginning = tool.getType().toString().split("_")[0].toLowerCase();

		//check if correct material is used
		switch (beginning) {
			case "shield", "wooden" -> {
				if (Lists.getWoodPlanks().contains(repair.getType())) {
					eligible = true;
				}
			}
			case "stone" -> {
				if (repair.getType() == Material.COBBLESTONE || repair.getType() == Material.STONE) {
					eligible = true;
				}
			}
			case "shears", "iron" -> {
				if (repair.getType() == Material.IRON_INGOT) {
					eligible = true;
				}
			}
			case "golden" -> {
				if (repair.getType() == Material.GOLD_INGOT) {
					eligible = true;
				}
			}
			case "diamond" -> {
				if (repair.getType() == Material.DIAMOND) {
					eligible = true;
				}
			}
			case "netherite" -> {
				if (repair.getType() == Material.NETHERITE_INGOT) {
					eligible = true;
				}
			}
			case "bow", "crossbow", "fishing" -> {
				if (repair.getType() == Material.STICK || repair.getType() == Material.STRING) {
					eligible = true;
				}
			}
			case "leather" -> {
				if (repair.getType() == Material.LEATHER) {
					eligible = true;
				}
			}
			case "chainmail" -> {
				if (repair.getType() == Material.IRON_BARS) {
					eligible = true;
				}
			}
			case "elytra" -> {
				if (repair.getType() == Material.PHANTOM_MEMBRANE) {
					eligible = true;
				}
			}
			case "trident" -> {
				if (repair.getType() == Material.PRISMARINE_SHARD) {
					eligible = true;
				}
			}
			case "turtle" -> {
				if (repair.getType() == Material.SCUTE) {
					eligible = true;
				}
			}
		}

        if (!eligible) return;

        final Damageable meta = (Damageable) tool.getItemMeta();

        if (meta == null) return;

        int dura = meta.getDamage();
        final short maxDura = tool.getType().getMaxDurability();
        int amount = event.getWhoClicked().getItemOnCursor().getAmount();

        //Calculate the maximum required Materials to restore to full
        int requiredMaterial;
        switch (ToolType.get(tool.getType())) {
            case AXE, PICKAXE, FISHINGROD, CROSSBOW, BOW -> requiredMaterial = 3;
            case BOOTS -> requiredMaterial = 4;
            case CHESTPLATE, ELYTRA -> requiredMaterial = 8;
            case HELMET -> requiredMaterial = 5;
            case HOE, TRIDENT, SWORD, SHEARS, OTHER -> requiredMaterial = 2;
            case LEGGINGS -> requiredMaterial = 7;
            case SHIELD -> requiredMaterial = 6;
            case SHOVEL -> requiredMaterial = 1;
            default -> {
                return;
            }
        }

        final float percent = 1.0f / requiredMaterial;

        while (amount > 0 && dura > 0) {
            dura = Math.max(Math.round(dura - (maxDura * percent)), 0);
            amount--;
        }

        meta.setDamage(dura);
        tool.setItemMeta(meta);

        repair.setAmount(amount);
        event.setCancelled(true);
    }

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onLootGenerate(@NotNull LootGenerateEvent event) {
		if (!MineTinker.getPlugin().getConfig().getBoolean("ConvertLoot.ChestLoot", true))
			return;

		event.getLoot().forEach(stack -> modManager.convertLoot(stack, null));
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
	}

	/**
	 * Updates the HashMap BLOCKFACE with the clicked face of the Block
	 *
	 * @param event PlayerInteractEvent
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void onInteract(@NotNull final PlayerInteractEvent event) {
		if (Lists.WORLDS.contains(event.getPlayer().getWorld().getName())) return;

		final Player player = event.getPlayer();

		if (event.getBlockFace() != BlockFace.SELF)
			Lists.BLOCKFACE.replace(event.getPlayer(), event.getBlockFace());

		if (!modManager.allowBookToModifier()) return;

		if (event.getClickedBlock() == null || event.getClickedBlock().getType() != Material.BOOKSHELF) return;

		if (event.getItem() == null || event.getItem().getType() != Material.ENCHANTED_BOOK) return;
		if (event.getItem().getItemMeta() == null
				|| !(event.getItem().getItemMeta() instanceof final EnchantmentStorageMeta meta)) return;

		for (final Map.Entry<Enchantment, Integer> entry : meta.getStoredEnchants().entrySet()) {
			final Modifier modifier = modManager.getModifierFromEnchantment(entry.getKey());
			if (modifier == null) continue;

			final ItemStack modDrop = modifier.getModItem();
			modDrop.setAmount(entry.getValue());

			if (!player.getInventory().addItem(modDrop).isEmpty()) { //adds items to (full) inventory
				player.getWorld().dropItem(player.getLocation(), modDrop);
			} // no else as it gets added in if-clause

			meta.removeStoredEnchant(entry.getKey());
		}

        if (!meta.getStoredEnchants().isEmpty()) {
			event.getItem().setItemMeta(meta);
			return;
		}

        // This seems not to work when the item is in the offhand, and can lead to bugs when nbt data is involved
        //event.getPlayer().getInventory().removeItem(event.getItem());

        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            if (event.getPlayer().getInventory().getItemInMainHand().equals(event.getItem()))
                event.getPlayer().getInventory().setItemInMainHand(null);
            else
                event.getPlayer().getInventory().setItemInOffHand(null);
        }
    }
}

package de.flo56958.minetinker.utils.data;

import de.flo56958.minetinker.MineTinker;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class DataHandler {

	/**
	 * Checks if the ItemStack has a Tag with the given key and the given dataType.
	 *
	 * @param item     The ItemStack to check
	 * @param key      The key of the Tag
	 * @param dataType The dataType of the Tag
	 * @param <T>      The type of the Tag
	 * @param <Z>      The type of the value of the Tag
	 * @return true if the ItemStack has a Tag with the given key and the given dataType, false otherwise
	 */
	public static <T, Z> boolean hasTag(@NotNull final ItemStack item, @NotNull final String key, final PersistentDataType<T, Z> dataType) {
		return hasTag(item, key, dataType, false);
	}

	/**
	 * Checks if the ItemStack has a Tag with the given key and the given dataType.
	 *
	 * @param item         The ItemStack to check
	 * @param key          The key of the Tag
	 * @param dataType     The dataType of the Tag
	 * @param useMinecraft If the key should be the Minecraft-Namespace or the Plugin-Namespace
	 * @param <T>          The type of the Tag
	 * @param <Z>          The type of the value of the Tag
	 * @return true if the ItemStack has a Tag with the given key and the given dataType, false otherwise
	 */
	public static <T, Z> boolean hasTag(@NotNull final ItemStack item, @NotNull final String key, final PersistentDataType<T, Z> dataType, final boolean useMinecraft) {
		final ItemMeta meta = item.getItemMeta();
		if (meta == null) return false;
		final PersistentDataContainer container = meta.getPersistentDataContainer();

		return container.has((useMinecraft ? NamespacedKey.minecraft(key) : new NamespacedKey(MineTinker.getPlugin(), key)), dataType);
	}

	/**
	 * Sets a Tag with the given key and the given value and dataType.
	 *
	 * @param item     The ItemStack to set the Tag on
	 * @param key      The key of the Tag
	 * @param value    The value of the Tag
	 * @param dataType The dataType of the Tag
	 * @param <T>      The type of the Tag
	 * @param <Z>      The type of the value of the Tag
	 */
	public static <T, Z> void setTag(@NotNull final ItemStack item, @NotNull final String key, final Z value, final PersistentDataType<T, Z> dataType) {
		setTag(item, key, value, dataType, false);
	}

	/**
	 * Sets a Tag with the given key and the given value and dataType.
	 *
	 * @param item         The ItemStack to set the Tag on
	 * @param key          The key of the Tag
	 * @param value        The value of the Tag
	 * @param dataType     The dataType of the Tag
	 * @param useMinecraft If the key should be the Minecraft-Namespace or the Plugin-Namespace
	 * @param <T>          The type of the Tag
	 * @param <Z>          The type of the value of the Tag
	 */
	public static <T, Z> void setTag(@NotNull final ItemStack item, @NotNull final String key, final Z value,
	                                 final PersistentDataType<T, Z> dataType, final boolean useMinecraft) {
		final ItemMeta meta = item.getItemMeta();
		if (meta == null) return;

		final PersistentDataContainer container = meta.getPersistentDataContainer();
		container.set((useMinecraft ? NamespacedKey.minecraft(key) : new NamespacedKey(MineTinker.getPlugin(), key)), dataType, value);
		item.setItemMeta(meta);
	}

	/**
	 * Gets a Tag with the given key and the given dataType.
	 *
	 * @param item         The ItemStack to get the Tag from
	 * @param key          The key of the Tag
	 * @param dataType     The dataType of the Tag
	 * @param defaultValue The value to return if the ItemStack does not have a Tag with the given key and the given dataType
	 * @param <T>          The type of the Tag
	 * @param <Z>          The type of the value of the Tag
	 * @return The value of the Tag or defaultValue if the ItemStack does not have a Tag with the given key and the given dataType
	 */
	public static <T, Z> @NotNull Z getTagOrDefault(@NotNull final ItemStack item, @NotNull final String key,
	                                                final PersistentDataType<T, Z> dataType, @NotNull final Z defaultValue) {
		final Z value = getTag(item, key, dataType, false);
		return value != null ? value : defaultValue;
	}

	/**
	 * Gets a Tag with the given key and the given dataType.
	 *
	 * @param item     The ItemStack to get the Tag from
	 * @param key      The key of the Tag
	 * @param dataType The dataType of the Tag
	 * @param <T>      The type of the Tag
	 * @param <Z>      The type of the value of the Tag
	 * @return The value of the Tag or null if the ItemStack does not have a Tag with the given key and the given dataType
	 */
	public static <T, Z> @Nullable Z getTag(@NotNull final ItemStack item, @NotNull final String key, final PersistentDataType<T, Z> dataType) {
		return getTag(item, key, dataType, false);
	}

	/**
	 * Gets a Tag with the given key and the given dataType.
	 *
	 * @param item         The ItemStack to get the Tag from
	 * @param key          The key of the Tag
	 * @param dataType     The dataType of the Tag
	 * @param useMinecraft If the key should be the Minecraft-Namespace or the Plugin-Namespace
	 * @param <T>          The type of the Tag
	 * @param <Z>          The type of the value of the Tag
	 * @return The value of the Tag or null if the ItemStack does not have a Tag with the given key and the given dataType
	 */
	public static <T, Z> @Nullable Z getTag(@NotNull final ItemStack item, @NotNull final String key, final PersistentDataType<T, Z> dataType, final boolean useMinecraft) {
		final ItemMeta meta = item.getItemMeta();
		if (meta == null) return null;

		final PersistentDataContainer container = meta.getPersistentDataContainer();
		return container.get((useMinecraft ? NamespacedKey.minecraft(key) : new NamespacedKey(MineTinker.getPlugin(), key)), dataType);
	}

	/**
	 * Removes a Tag with the given key.
	 *
	 * @param item The ItemStack to remove the Tag from
	 * @param key  The key of the Tag
	 */
	public static void removeTag(@NotNull final ItemStack item, @NotNull final String key) {
		removeTag(item, key, false);
	}

	/**
	 * Removes a Tag with the given key.
	 *
	 * @param item         The ItemStack to remove the Tag from
	 * @param key          The key of the Tag
	 * @param useMinecraft If the key should be the Minecraft-Namespace or the Plugin-Namespace
	 */
	public static void removeTag(@NotNull final ItemStack item, @NotNull final String key, final boolean useMinecraft) {
		ItemMeta meta = item.getItemMeta();
		if (meta == null) return;
		PersistentDataContainer container = meta.getPersistentDataContainer();

		container.remove((useMinecraft ? NamespacedKey.minecraft(key) : new NamespacedKey(MineTinker.getPlugin(), key)));
		item.setItemMeta(meta);
	}

	/**
	 * Triggers the item damage event for the given player and itemStack and sets the damage.
	 * @param player    The player that should trigger the item damage event
	 * @param itemStack The itemStack that should be damaged
	 * @param damage    The amount of damage that should be dealt
	 * @return          true if the event was successful, false otherwise
	 */
	public static boolean triggerItemDamage(@NotNull final Player player, @NotNull final ItemStack itemStack, final int damage) {
		ItemMeta meta = itemStack.getItemMeta();
		if (meta == null) return true;
		if (meta.isUnbreakable()) return true;

		// No damage if the Player is in Creative
		if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) return true;

		// Consider Unbreaking Enchant
		final int lvl = meta.getEnchantLevel(Enchantment.UNBREAKING);
		// Check if the item should be damaged
		if (new Random().nextInt(100) >= (100 / (lvl + 1))) return true;

		//Call damage event
		final PlayerItemDamageEvent damageEvent = new PlayerItemDamageEvent(player, itemStack, damage);
		Bukkit.getPluginManager().callEvent(damageEvent);
		if (damageEvent.isCancelled()) return false;

		meta = itemStack.getItemMeta();
		if (meta instanceof Damageable damageable) {
			if (damageable.getDamage() + damageEvent.getDamage() >= itemStack.getType().getMaxDurability())
				return false;

			damageable.setDamage(damageable.getDamage() + damageEvent.getDamage());
			itemStack.setItemMeta(meta);
		}
		return true;
	}

	public static boolean playerPlaceBlock(@NotNull final Player player, @NotNull final ItemStack itemInHand,
	                                       @NotNull final Block toPlace, @NotNull final Block placedAgainst,
	                                       @NotNull final BlockState blockState, @Nullable final BlockData blockData) {
		// Triggers a pseudoevent to find out if the Player can build here
		final BlockPlaceEvent placeEvent =
				new BlockPlaceEvent(toPlace, blockState, placedAgainst, itemInHand, player, true, EquipmentSlot.HAND);
		Bukkit.getPluginManager().callEvent(placeEvent);

		// Check the pseudoevent
		if (!placeEvent.canBuild() || placeEvent.isCancelled())
			return false;

		toPlace.setType(itemInHand.getType(), true); //incl. physics update
		if (blockData != null) toPlace.setBlockData(blockData, true); //incl. physics update
		toPlace.getState().update();

		// Play sound
		final Sound placeSound = toPlace.getBlockData().getSoundGroup().getPlaceSound();
		if (!placeSound.equals(lastSound.getOrDefault(player, null))) {
			toPlace.getWorld().playSound(toPlace.getLocation(), placeSound, 1.0f, 1.0f);
			lastSound.put(player, placeSound);
		}
		return true;
	}

	// Decrease the amount of duplicate Sounds
	private static final HashMap<Player, Sound> lastSound = new HashMap<>();

	public static void removeLastSound(@NotNull final Player player) {
		lastSound.remove(player);
	}

	/**
	 * Let the player break a block with the given itemStack through the plugin.
	 *
	 * @param player    The player that should break the block
	 * @param block     The block that should be broken
	 * @param itemStack The itemStack that is used to break the block
	 * @return true if the player successfully broke the block, false otherwise
	 * @throws IllegalArgumentException on wrong input
	 */
	public static boolean playerBreakBlock(@NotNull final Player player, @NotNull final Block block, @NotNull final ItemStack itemStack)
			throws IllegalArgumentException {
		// This skips all interactions and synergies with MT and other Plugins but is way less performance heavy
		if (MineTinker.getPlugin().getConfig().getBoolean("LowSpecMode")) {
			// Container handling is done by .breakNaturally()

			if (!block.breakNaturally(itemStack)) return false;

			// Spawn Experience Orb, if Player is not in Survival
			int exp = calculateExp(block.getType());
			if (exp > 0 && player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR) {
				final ExperienceOrb orb = (ExperienceOrb) player.getWorld().spawnEntity(block.getLocation(), EntityType.EXPERIENCE_ORB);
				orb.setExperience(exp);
			}

			// Calculate Damage for itemStack
			// No Damage for Creative Players
			if (player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR) {
				final ItemMeta meta = itemStack.getItemMeta();
				if (meta == null) return true;
				if (!meta.isUnbreakable()) {
					if (meta instanceof Damageable damageable) {
						// Consider Unbreaking enchant
						int lvl = meta.getEnchantLevel(Enchantment.UNBREAKING);
						int r = new Random().nextInt(100);
						if (!(r > 100 / (lvl + 1))) {
							damageable.setDamage(damageable.getDamage() + 1);
							itemStack.setItemMeta(meta);
						}
					}
				}
			}
			return true;
		}

		// Trigger BlockBreakEvent
		// For interactions with MT itself and other Plugins
		final BlockBreakEvent breakEvent = new BlockBreakEvent(block, player);
		// No itemdrops for creative players, but can be changed by other plugins in the event
		breakEvent.setDropItems(player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR);
		ItemMeta meta = itemStack.getItemMeta();
		if (meta != null && !meta.hasEnchant(Enchantment.SILK_TOUCH))
			breakEvent.setExpToDrop(calculateExp(block.getType()));
		Bukkit.getPluginManager().callEvent(breakEvent);

		// Check if Event got cancelled and if not destroy the block and check if the player can successfully break the blocks (incl. drops)
		// Block#breakNaturally(ItemStack itemStack) can not be used as it drops Items itself (without Event, and we don't want that)
		if (breakEvent.isCancelled()) return false;

		// Get all drops to drop
		final Collection<ItemStack> items = block.getDrops(itemStack, player);

		// If the Block is a Container it needs to drop the items inside as well
		if (block.getState() instanceof Container container && !(block.getState() instanceof ShulkerBox)) {
			// Check for chests as chest inventories can be spread out over 2 blocks (Chest::getBlockInventory())
			final ItemStack[] contents = container instanceof Chest c
					? c.getBlockInventory().getContents() : container.getInventory().getContents();
			Collections.addAll(items, contents);
			items.removeIf(Objects::isNull);
		}

		// Play sound before breaking the block as AIR has the wrong sound
		final Sound breakSound = block.getBlockData().getSoundGroup().getBreakSound();
		if (!breakSound.equals(lastSound.getOrDefault(player, null))) {
			block.getWorld().playSound(block.getLocation(), breakSound, 1.0f, 1.0f);
			lastSound.put(player, breakSound);
		}
		// Set Block to Material.AIR (effectively breaks the Block)
		block.setType(Material.AIR);

		// Check if items need to be dropped
		if (breakEvent.isDropItems()) {
			final List<Item> itemEntities = items.stream()
					.map(entry -> player.getWorld().dropItemNaturally(block.getLocation(), entry)) //World#spawnEntity() does not work for Items
					.collect(Collectors.toList());

			// Trigger BlockDropItemEvent (internally also used for Directing)
			final BlockDropItemEvent event = new BlockDropItemEvent(block, block.getState(), player, new ArrayList<>(itemEntities));
			Bukkit.getPluginManager().callEvent(event);

			// Check if Event got cancelled
			if (!event.isCancelled()) {
				// Remove all drops that should be dropped
				itemEntities.removeAll(event.getItems());
			}
			itemEntities.forEach(Item::remove); // Delete all items that should not be dropped
			// if the event spawns additional items, they will still exist in the world
		}

		// Check if Exp needs to be dropped, Player should be in survival
		if (breakEvent.getExpToDrop() > 0 && player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR) {
			//Spawn Experience Orb
			final ExperienceOrb orb = (ExperienceOrb) player.getWorld().spawnEntity(block.getLocation(), EntityType.EXPERIENCE_ORB);
			orb.setExperience(breakEvent.getExpToDrop());
		}

		triggerItemDamage(player, itemStack, 1); // Unbreaking and Creative is considered in the method

		return true;
	}

	/**
	 * Calculates the amount of experience that should be dropped when breaking a block.
	 *
	 * @param type The type of the block that should be broken.
	 * @return The amount of experience that should be dropped when breaking a block.
	 */
	private static int calculateExp(final Material type) {
		//TODO: Find better method then hardcoded values
		return switch (type) {
			//1
			case SCULK -> 1;
			//5
			case SCULK_CATALYST, SCULK_SENSOR, SCULK_SHRIEKER -> 5;
			//0-2
			case COAL_ORE -> new Random().nextInt(3);
			//0-1
			case NETHER_GOLD_ORE -> new Random().nextInt(2);
			//3-7
			case DIAMOND_ORE, EMERALD_ORE -> new Random().nextInt(5) + 3;
			//2-5
			case NETHER_QUARTZ_ORE, LAPIS_ORE -> new Random().nextInt(4) + 2;
			//1-5
			case REDSTONE_ORE -> new Random().nextInt(4) + 1;
			//15-43
			case SPAWNER -> new Random().nextInt(29) + 15;
			default -> 0;
		};
	}
}

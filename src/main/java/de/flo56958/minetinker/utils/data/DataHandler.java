package de.flo56958.minetinker.utils.data;

import de.flo56958.minetinker.MineTinker;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.block.ShulkerBox;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class DataHandler {

	/**
	 * Checks if the ItemStack has a Tag with the given key and the given dataType.
	 * @param item The ItemStack to check
	 * @param key The key of the Tag
	 * @param dataType The dataType of the Tag
	 * @return true if the ItemStack has a Tag with the given key and the given dataType, false otherwise
	 * @param <T> The type of the Tag
	 * @param <Z> The type of the value of the Tag
	 */
	public static <T, Z> boolean hasTag(@NotNull ItemStack item, @NotNull String key, PersistentDataType<T, Z> dataType) {
		return hasTag(item, key, dataType, false);
	}

	/**
     * Checks if the ItemStack has a Tag with the given key and the given dataType.
     * @param item The ItemStack to check
     * @param key The key of the Tag
     * @param dataType The dataType of the Tag
     * @param useMinecraft If the key should be the Minecraft-Namespace or the Plugin-Namespace
     * @return true if the ItemStack has a Tag with the given key and the given dataType, false otherwise
     * @param <T> The type of the Tag
     * @param <Z> The type of the value of the Tag
     */
	public static <T, Z> boolean hasTag(@NotNull ItemStack item, @NotNull String key, PersistentDataType<T, Z> dataType, boolean useMinecraft) {
		final ItemMeta meta = item.getItemMeta();
		if (meta == null) return false;
		final PersistentDataContainer container = meta.getPersistentDataContainer();

		return container.has((useMinecraft ? NamespacedKey.minecraft(key) : new NamespacedKey(MineTinker.getPlugin(), key)), dataType);
	}

	/**
	 * Sets a Tag with the given key and the given value and dataType.
	 * @param item The ItemStack to set the Tag on
	 * @param key The key of the Tag
	 * @param value The value of the Tag
	 * @param dataType The dataType of the Tag
	 * @param <T> The type of the Tag
	 * @param <Z> The type of the value of the Tag
	 */
	public static <T, Z> void setTag(@NotNull ItemStack item, @NotNull String key, Z value, PersistentDataType<T, Z> dataType) {
		setTag(item, key, value, dataType, false);
	}

	/**
	 * Sets a Tag with the given key and the given value and dataType.
	 * @param item The ItemStack to set the Tag on
	 * @param key The key of the Tag
	 * @param value The value of the Tag
	 * @param dataType The dataType of the Tag
	 * @param useMinecraft If the key should be the Minecraft-Namespace or the Plugin-Namespace
	 * @param <T> The type of the Tag
	 * @param <Z> The type of the value of the Tag
	 */
	public static <T, Z> void setTag(@NotNull ItemStack item, @NotNull String key, Z value, PersistentDataType<T, Z> dataType, boolean useMinecraft) {
		final ItemMeta meta = item.getItemMeta();
		if (meta == null) return;

		final PersistentDataContainer container = meta.getPersistentDataContainer();
		container.set((useMinecraft ? NamespacedKey.minecraft(key) : new NamespacedKey(MineTinker.getPlugin(), key)), dataType, value);
		item.setItemMeta(meta);
	}

	/**
	 * Gets a Tag with the given key and the given dataType.
	 * @param item The ItemStack to get the Tag from
	 * @param key The key of the Tag
	 * @param dataType The dataType of the Tag
	 * @param defaultValue The value to return if the ItemStack does not have a Tag with the given key and the given dataType
	 * @return The value of the Tag or defaultValue if the ItemStack does not have a Tag with the given key and the given dataType
	 * @param <T> The type of the Tag
	 * @param <Z> The type of the value of the Tag
	 */
	public static <T, Z> @NotNull Z getTagOrDefault(@NotNull ItemStack item, @NotNull String key, PersistentDataType<T, Z> dataType, @NotNull Z defaultValue) {
		final Z value = getTag(item, key, dataType, false);
		return value != null ? value : defaultValue;
	}

	/**
	 * Gets a Tag with the given key and the given dataType.
	 * @param item The ItemStack to get the Tag from
	 * @param key The key of the Tag
	 * @param dataType The dataType of the Tag
	 * @return The value of the Tag or null if the ItemStack does not have a Tag with the given key and the given dataType
	 * @param <T> The type of the Tag
	 * @param <Z> The type of the value of the Tag
	 */
	public static <T, Z> @Nullable Z getTag(@NotNull ItemStack item, @NotNull String key, PersistentDataType<T, Z> dataType) {
		return getTag(item, key, dataType, false);
	}

	/**
	 * Gets a Tag with the given key and the given dataType.
	 * @param item The ItemStack to get the Tag from
	 * @param key The key of the Tag
	 * @param dataType The dataType of the Tag
	 * @param useMinecraft If the key should be the Minecraft-Namespace or the Plugin-Namespace
	 * @return The value of the Tag or null if the ItemStack does not have a Tag with the given key and the given dataType
	 * @param <T> The type of the Tag
	 * @param <Z> The type of the value of the Tag
	 */
	public static <T, Z> @Nullable Z getTag(@NotNull ItemStack item, @NotNull String key, PersistentDataType<T, Z> dataType, boolean useMinecraft) {
		final ItemMeta meta = item.getItemMeta();
		if (meta == null) return null;

		final PersistentDataContainer container = meta.getPersistentDataContainer();
		return container.get((useMinecraft ? NamespacedKey.minecraft(key) : new NamespacedKey(MineTinker.getPlugin(), key)), dataType);
	}

	/**
	 * Removes a Tag with the given key.
	 * @param item The ItemStack to remove the Tag from
	 * @param key The key of the Tag
	 */
	public static void removeTag(@NotNull ItemStack item, @NotNull String key) {
		removeTag(item, key, false);
	}

	/**
	 * Removes a Tag with the given key.
	 * @param item The ItemStack to remove the Tag from
	 * @param key The key of the Tag
	 * @param useMinecraft If the key should be the Minecraft-Namespace or the Plugin-Namespace
	 */
	public static void removeTag(@NotNull ItemStack item, @NotNull String key, boolean useMinecraft) {
		ItemMeta meta = item.getItemMeta();
		if (meta == null) return;
		PersistentDataContainer container = meta.getPersistentDataContainer();
		
		container.remove((useMinecraft ? NamespacedKey.minecraft(key) : new NamespacedKey(MineTinker.getPlugin(), key)));
		item.setItemMeta(meta);
	}

	public static boolean triggerItemDamage(@NotNull Player player, @NotNull ItemStack itemStack, int damage) {
		ItemMeta meta = itemStack.getItemMeta();
		if (meta == null) return true;
        if (meta.isUnbreakable()) return true;

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

	/**
	 * Let the player break a block with the given itemStack through the plugin.
	 * @param player The player that should break the block
	 * @param block The block that should be broken
	 * @param itemStack The itemStack that is used to break the block
	 * @return true if the player successfully broke the block, false otherwise
	 * @throws IllegalArgumentException
	 */
	public static boolean playerBreakBlock(@NotNull Player player, @NotNull Block block, @NotNull ItemStack itemStack)
			throws IllegalArgumentException {
		//
		//This skips all interactions and synergies with MT and other Plugins but is way less performance heavy
		//
		if (MineTinker.getPlugin().getConfig().getBoolean("LowSpecMode")) {
			//Container handling is done by .breakNaturally()

			if (!block.breakNaturally(itemStack)) return false;

			//Spawn Experience Orb, if Player is not in Survival
			int exp = calculateExp(block.getType());
			if (exp > 0 && player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR) {
				ExperienceOrb orb = (ExperienceOrb) player.getWorld().spawnEntity(block.getLocation(), EntityType.EXPERIENCE_ORB);
				orb.setExperience(exp);
			}

			//Calculate Damage for itemStack
			//No Damage for Creative Players
			if (player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR) {
				ItemMeta meta = itemStack.getItemMeta();
				if (!meta.isUnbreakable()) {
					if (meta instanceof Damageable) {
						//Consider Unbreaking enchant
						int lvl = meta.getEnchantLevel(Enchantment.DURABILITY);
						int r = new Random().nextInt(100);
						if (!(r > 100 / (lvl + 1))) {
							((Damageable) meta).setDamage(((Damageable) meta).getDamage() + 1);
							itemStack.setItemMeta(meta);
						}
					}
				}
			}
			return true;
		}
		
		//
		//Trigger BlockBreakEvent
		//For interactions with MT itself and other Plugins
		//
		BlockBreakEvent breakEvent = new BlockBreakEvent(block, player);
		//No itemdrops for creative players, but can be changed by other plugins in the event
		breakEvent.setDropItems(player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR);
		ItemMeta meta = itemStack.getItemMeta();
		if (meta != null && !meta.hasEnchant(Enchantment.SILK_TOUCH)) breakEvent.setExpToDrop(calculateExp(block.getType()));
		Bukkit.getPluginManager().callEvent(breakEvent);
		
		//Check if Event got cancelled and if not destroy the block and check if the player can successfully break the blocks (incl. drops)
		//Block#breakNaturally(ItemStack itemStack) can not be used as it drops Items itself (without Event, and we don't want that)
		if (!breakEvent.isCancelled()) {
			//Get all drops to drop
			Collection<ItemStack> items = block.getDrops(itemStack);
			
			//If the Block is a Container it needs to drop the items inside as well
			if (block.getState() instanceof Container container && !(block.getState() instanceof ShulkerBox)) {
				// Check for chests as chest inventories can be spread out over 2 blocks (Chest::getBlockInventory())
				for (ItemStack stack :
						container instanceof Chest c
							? c.getBlockInventory().getContents() : container.getInventory().getContents()) {
					if (stack != null) items.add(stack); //Null items can not be dropped
				}
			}
			
			//Set Block to Material.AIR (effectively breaks the Block)
			block.setType(Material.AIR);
			//TODO: Play Sound? - Not needed
			
			//Check if items need to be dropped
			if (breakEvent.isDropItems()) {
				List<Item> itemEntities = items.stream()
				.map(entry -> player.getWorld().dropItemNaturally(block.getLocation(), entry)) //World#spawnEntity() does not work for Items
				.collect(Collectors.toList());
				
				//Trigger BlockDropItemEvent (internally also used for Directing)
				BlockDropItemEvent event = new BlockDropItemEvent(block, block.getState(), player, new ArrayList<>(itemEntities));
				Bukkit.getPluginManager().callEvent(event);
				
				//check if Event got cancelled
				if (!event.isCancelled()) {
					//Remove all drops that should be dropped
					itemEntities.removeIf(element -> event.getItems().contains(element));
				}
				itemEntities.forEach(Item::remove);
			}
			
			//Check if Exp needs to be dropped, Player should be in survival
			if (breakEvent.getExpToDrop() > 0 && player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR) {
				//Spawn Experience Orb
				ExperienceOrb orb = (ExperienceOrb) player.getWorld().spawnEntity(block.getLocation(), EntityType.EXPERIENCE_ORB);
				orb.setExperience(breakEvent.getExpToDrop());
			}
			
			//Calculate Damage for itemStack
			meta = itemStack.getItemMeta();
			//No Damage for creative players
			if (player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR) {
				if (!meta.isUnbreakable()) {
					if (meta instanceof Damageable) {
						//Consider Unbreaking Enchant
						int lvl = meta.getEnchantLevel(Enchantment.DURABILITY);
						int damage = 1;
						int r = new Random().nextInt(100);
						if (r > 100 / (lvl + 1)) {
							damage = 0;
						}

						triggerItemDamage(player, itemStack, damage);
					}
				}
			}
			return true;
		}
		
		return false;
	}

	/**
	 * Calculates the amount of experience that should be dropped when breaking a block.
	 * @param type The type of the block that should be broken.
	 * @return The amount of experience that should be dropped when breaking a block.
	 */
	private static int calculateExp(Material type) {
		//TODO: Find better method then hardcoded values
		if (MineTinker.is19compatible) {
			int amount = switch (type) {
				//1
				case SCULK -> 1;
				//5
				case SCULK_CATALYST, SCULK_SENSOR, SCULK_SHRIEKER -> 5;
				default -> 0;
			};
			if (amount != 0) return amount;
		}

		return switch (type) {
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

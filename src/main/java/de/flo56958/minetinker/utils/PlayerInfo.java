package de.flo56958.minetinker.utils;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.data.Lists;
import de.flo56958.minetinker.modifiers.ModManager;
import de.flo56958.minetinker.utils.data.DataHandler;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerInfo implements Listener {

	static final ModManager modManager = ModManager.instance();

	private static final ConcurrentHashMap<UUID, Long> combatTagTracker = new ConcurrentHashMap<>();
	private static final ConcurrentHashMap<UUID, Long> combatTagTimeTracker = new ConcurrentHashMap<>();
	private static final ConcurrentHashMap<UUID, ItemStack> fishingRodTracker = new ConcurrentHashMap<>();

	public static boolean isCombatTagged(@NotNull final Player player) {
		final long time = combatTagTracker.getOrDefault(player.getUniqueId(), -1L);
		if (time == -1L) return false;

		return System.currentTimeMillis() - time
				< MineTinker.getPlugin().getConfig().getInt("CombatTagDuration", 5) * 1000L;
	}

	/**
	 * @param player The player to get the combat time of
	 * @return The time the player is currently combat tagged in milliseconds
	 */
	public static long getCombatTagTime(@NotNull final Player player) {
		if (!isCombatTagged(player)) return 0L;

		final long time = System.currentTimeMillis();
		return time - combatTagTimeTracker.getOrDefault(player.getUniqueId(), time);
	}

	@EventHandler(ignoreCancelled = true)
	private void onFish(@NotNull final PlayerFishEvent event) {
		final Player player = event.getPlayer();
		if (Lists.WORLDS.contains(player.getWorld().getName())) return;

		switch (event.getState()) {
			case FISHING -> {
				if (event.getHand() == null) return;
				final ItemStack rod = player.getInventory().getItem(event.getHand());
				if (!modManager.isToolViable(rod)) return;
				fishingRodTracker.put(player.getUniqueId(), rod);
			}
			case CAUGHT_FISH -> {
				// event.getHand() is null in State.CAUGHT_FISH
				// looking for fishing rod
				ItemStack rod = fishingRodTracker.remove(player.getUniqueId());
				if (event.getHand() == null) {
					// rod needs to be updated if the slot changed because of a hand swap
					for (final ItemStack item : new ItemStack[]{player.getInventory().getItemInMainHand(),
							player.getInventory().getItemInOffHand()}) {
						if (rod.equals(item)) {
							rod = item;
							break;
						}
					}
				} else {
					rod = player.getInventory().getItem(event.getHand());
				}
				if (!modManager.isToolViable(rod)) return;

				modManager.addExp(player, rod, event.getExpToDrop(), true);
			}
			default -> {}
		}
	}

	private void setCombatTag(@Nullable final Entity entity) {
		if (!(entity instanceof Player player)) return;

		final long time = System.currentTimeMillis();

		// set first damage time (begin of combat)
		if (!isCombatTagged(player))
			combatTagTimeTracker.put(player.getUniqueId(), time);

		combatTagTracker.put(player.getUniqueId(), time);
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	private void onCombat(EntityDamageEvent event) {
		setCombatTag(event.getEntity());
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	private void onCombat(EntityDamageByEntityEvent event) {
		setCombatTag(event.getEntity());
		setCombatTag(event.getDamager());
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onDeath(PlayerDeathEvent event) {
		combatTagTracker.remove(event.getEntity().getUniqueId());
		combatTagTimeTracker.remove(event.getEntity().getUniqueId());

		fishingRodTracker.remove(event.getEntity().getUniqueId());
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onDisconnect(PlayerQuitEvent event) {
		combatTagTracker.remove(event.getPlayer().getUniqueId());
		combatTagTimeTracker.remove(event.getPlayer().getUniqueId());

		fishingRodTracker.remove(event.getPlayer().getUniqueId());

		DataHandler.removeLastSound(event.getPlayer());
	}

	public static int getEmptyInventorySlots(final Player player) {
		int counter = 0;
		for (final ItemStack stack : player.getInventory().getStorageContents()) {
			if (stack == null || stack.getType().isAir())
				counter++;
		}
		return counter;
	}

	/**
	 * @param player The player to get the facing direction of as a single character
	 * @return The facing direction of the player in Degrees
	 */
	public static Direction getFacingDirection(final Player player) {
		double rot = (player.getLocation().getYaw() - 90) % 360;

		if (rot < 0) {
			rot += 360.0;
		}

		return getDirection(rot);
	}

	// Calculate total experience up to a level
	private static int getExpAtLevel(int level) {
		if (level <= 16) {
			return (int) (Math.pow(level, 2) + 6 * level);
		} else if (level <= 31) {
			return (int) (2.5 * Math.pow(level, 2) - 40.5 * level + 360.0);
		} else {
			return (int) (4.5 * Math.pow(level, 2) - 162.5 * level + 2220.0);
		}
	}

	// Calculate amount of EXP needed to level up
	private static int getExpToLevelUp(int level) {
		if (level <= 15) {
			return 2 * level + 7;
		} else if (level <= 30) {
			return 5 * level - 38;
		} else {
			return 9 * level - 158;
		}
	}

	/**
	 * @param player The player
	 * @return the players current total exp amount
	 */
	public static int getPlayerExp(@NotNull Player player) {
		int exp = 0;
		int level = player.getLevel();

		// Get the amount of XP in past levels
		exp += getExpAtLevel(level);

		// Get amount of XP towards next level
		exp += Math.round(getExpToLevelUp(level) * player.getExp());

		return exp;
	}

	/**
	 * @param rot The rotation in degrees
	 * @return The compass facing direction
	 */
	private @Nullable
	static Direction getDirection(final double rot) {
		return switch ((int) (rot / 45)) {
			case 0, 7 -> Direction.WEST;
			case 1, 2 -> Direction.NORTH;
			case 3, 4 -> Direction.EAST;
			case 5, 6 -> Direction.SOUTH;
			default -> null;
		};
	}

	public enum Direction {
		NORTH,
		WEST,
		SOUTH,
		EAST
	}
}

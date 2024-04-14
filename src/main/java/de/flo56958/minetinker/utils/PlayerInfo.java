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
		switch (event.getState()) {
			case FISHING:
				if (event.getHand() == null) return;
				fishingRodTracker.put(event.getPlayer().getUniqueId(),
						event.getPlayer().getInventory().getItem(event.getHand()));
				break;
			case CAUGHT_FISH:
				final Player player = event.getPlayer();
				if (Lists.WORLDS.contains(player.getWorld().getName())) return;

				// event.getHand() is null in State.CAUGHT_FISH
				// looking for fishing rod
				ItemStack rod = fishingRodTracker.get(player.getUniqueId());
				if (!modManager.isToolViable(rod)) return;
				// rod needs to be updated if the slot changed because of a hand swap
				for (final ItemStack item : new ItemStack[]{player.getInventory().getItemInMainHand(),
						player.getInventory().getItemInOffHand()}) {
					if (rod.equals(item)) {
						rod = item;
						break;
					}
				}

				modManager.addExp(player, rod, event.getExpToDrop(), true);
			case CAUGHT_ENTITY:
			case IN_GROUND:
			case FAILED_ATTEMPT:
			case REEL_IN:
			default:
				fishingRodTracker.remove(event.getPlayer().getUniqueId());
				break;
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

	// Calculate amount of EXP needed to level up
	private static int getExpToLevelUp(final int level) {
		return switch (level) {
			case 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 ->
					2 * level + 7;
			case 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30 ->
					5 * level - 38;
			default ->
					9 * level - 158;
		};
	}

	// Calculate total experience up to a level
	private static int getExpAtLevel(final int level) {
		return switch (level) {
			case 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 ->
					(int) (Math.pow(level, 2) + 6 * level);
			case 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31 ->
					(int) (2.5 * Math.pow(level, 2) - 40.5 * level + 360.0);
			default ->
					(int) (4.5 * Math.pow(level, 2) - 162.5 * level + 2220.0);
		};
	}

	/**
	 * @param player The player
	 * @return the players current total exp amount
	 */
	public static int getPlayerExp(@NotNull final Player player) {
		int exp = 0;
		final int level = player.getLevel();

		// Get the amount of XP in past levels
		exp += getExpAtLevel(level);

		// Get amount of XP towards next level
		exp += Math.round(getExpToLevelUp(level) * player.getExp());

		return exp;
	}

	public enum Direction {
		NORTH,
		WEST,
		SOUTH,
		EAST
	}
}

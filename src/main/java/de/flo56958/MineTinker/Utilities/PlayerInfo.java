package de.flo56958.MineTinker.Utilities;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;

public class PlayerInfo implements Listener {

	private static final ConcurrentHashMap<String, Long> combatTagTracker = new ConcurrentHashMap<>();

	public static boolean isCombatTagged(Player player) {
		Long time = combatTagTracker.getOrDefault(player.getUniqueId().toString(), -1L);
		if (time == -1L) return false;

		return System.currentTimeMillis() - time < 5 * 1000;
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	private void onCombat(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			combatTagTracker.put(event.getEntity().getUniqueId().toString(), System.currentTimeMillis());
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	private void onCombat(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player) {
			combatTagTracker.put(event.getEntity().getUniqueId().toString(), System.currentTimeMillis());
		}

		if (event.getDamager() instanceof Player) {
			combatTagTracker.put(event.getDamager().getUniqueId().toString(), System.currentTimeMillis());
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onDeath(PlayerDeathEvent event) {
		combatTagTracker.remove(event.getEntity().getUniqueId().toString());
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onDisconnect(PlayerQuitEvent event) {
		combatTagTracker.remove(event.getPlayer().getUniqueId().toString());
	}


	/**
	 * @param player The player to get the facing direction of as a single character
	 * @return The facing direction of the player in Degrees
	 */
	public static Direction getFacingDirection(Player player) {
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
	private @Nullable static Direction getDirection(double rot) {
		if (0 <= rot && rot < 45) {
			return Direction.WEST;
		} else if (45 <= rot && rot < 135) {
			return Direction.NORTH;
		} else if (135 <= rot && rot < 225) {
			return Direction.EAST;
		} else if (225 <= rot && rot < 315) {
			return Direction.SOUTH;
		} else if (315 <= rot && rot < 360) {
			return Direction.WEST;
		} else {
			return null;
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

	public enum Direction {
		NORTH,
		WEST,
		SOUTH,
		EAST
	}
}

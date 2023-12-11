package de.flo56958.minetinker.utils;

import de.flo56958.minetinker.data.Lists;
import de.flo56958.minetinker.modifiers.ModManager;
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

import java.util.concurrent.ConcurrentHashMap;

public class PlayerInfo implements Listener {

	static final ModManager modManager = ModManager.instance();

	private static final ConcurrentHashMap<String, Long> combatTagTracker = new ConcurrentHashMap<>();
	private static final ConcurrentHashMap<String, ItemStack> fishingRodTracker = new ConcurrentHashMap<>();

	public static boolean isCombatTagged(Player player) {
		Long time = combatTagTracker.getOrDefault(player.getUniqueId().toString(), -1L);
		if (time == -1L) return false;

		return System.currentTimeMillis() - time < 5 * 1000L;
	}

	@EventHandler(ignoreCancelled = true)
	private void onFish(@NotNull final PlayerFishEvent event) {
		switch (event.getState()) {
			case FISHING:
				if (event.getHand() == null) return;
				fishingRodTracker.put(event.getPlayer().getUniqueId().toString(),
						event.getPlayer().getInventory().getItem(event.getHand()));
				break;
			case CAUGHT_FISH:
				final Player player = event.getPlayer();
				if (Lists.WORLDS.contains(player.getWorld().getName())) return;

				// event.getHand() is null in State.CAUGHT_FISH
				// looking for fishing rod
				ItemStack rod = fishingRodTracker.get(player.getUniqueId().toString());
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
				fishingRodTracker.remove(event.getPlayer().getUniqueId().toString());
			default:
				break;
        }
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	private void onCombat(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player player) {
			combatTagTracker.put(player.getUniqueId().toString(), System.currentTimeMillis());
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	private void onCombat(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player player) {
			combatTagTracker.put(player.getUniqueId().toString(), System.currentTimeMillis());
		}

		if (event.getDamager() instanceof Player player) {
			combatTagTracker.put(player.getUniqueId().toString(), System.currentTimeMillis());
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onDeath(PlayerDeathEvent event) {
		combatTagTracker.remove(event.getEntity().getUniqueId().toString());
		fishingRodTracker.remove(event.getEntity().getUniqueId().toString());
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onDisconnect(PlayerQuitEvent event) {
		combatTagTracker.remove(event.getPlayer().getUniqueId().toString());
		fishingRodTracker.remove(event.getPlayer().getUniqueId().toString());
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
        return switch ((int) (rot / 45)) {
            case 0, 7 -> Direction.WEST;
            case 1, 2 -> Direction.NORTH;
            case 3, 4 -> Direction.EAST;
            case 5, 6 -> Direction.SOUTH;
            default -> null;
        };
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

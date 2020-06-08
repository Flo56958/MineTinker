package de.flo56958.MineTinker.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * The Event is only a extra trigger for the MineTinker-Modifiers (mostly used for Armor)
 * it's only purpose is it to activate the Listeners if a EntityDamageEvent matches
 * the criteria (right Armor, ...)
 */
public class MTEntityDamageEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();

	private final Player player;
	private final ItemStack tool;
	private final boolean isBlocking;

	private final EntityDamageEvent event;

	/**
	 * Event constructor
	 *
	 * @param tool  The ItemStack (MUST be a MineTinker-Tool)
	 * @param event The BlockBreakEvent from which it was called
	 */
	public MTEntityDamageEvent(@NotNull ItemStack tool, @NotNull EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			this.player = (Player) event.getEntity();
		} else {
			this.player = null;
		}
		this.tool = tool;
		this.event = event;
		this.isBlocking = false;
	}

	/**
	 * Event constructor (used for the Armor-Effects as the Player and the Entity are the same)
	 *
	 * @param player The Player
	 * @param tool The ItemStack (MUST be a MineTinker-Tool)
	 * @param event The BlockBreakEvent from which it was called
	 */
	public MTEntityDamageEvent(@NotNull Player player, @NotNull ItemStack tool, @NotNull EntityDamageEvent event) {
		this.player = player;
		this.tool = tool;
		this.event = event;
		this.isBlocking = false;
	}

	public MTEntityDamageEvent(@NotNull Player player, @NotNull ItemStack tool, @NotNull EntityDamageEvent event, boolean isBlocking) {
		this.player = player;
		this.tool = tool;
		this.event = event;
		this.isBlocking = isBlocking;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public Player getPlayer() {
		return player;
	}

	public ItemStack getTool() {
		return tool;
	}

	/**
	 * @return The original EntityDamageEvent
	 */
	public EntityDamageEvent getEvent() {
		return event;
	}

	@NotNull
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	/**
	 * @return if the original EntityDamageEvent is cancelled
	 */
	@Override
	public boolean isCancelled() {
		return event.isCancelled();
	}

	/**
	 * Sets the original EntityDamageEvent Cancelled-State
	 * This system is linked to the original Event as this is only a trigger for the MineTinker-Modifiers
	 *
	 * @param b true/false is cancelled
	 */
	@Override
	public void setCancelled(boolean b) {
		event.setCancelled(b);
	}

	public boolean isBlocking() {
		return isBlocking;
	}
}

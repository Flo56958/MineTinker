package de.flo56958.minetinker.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * The Event is only a extra trigger for the MineTinker-Modifiers
 * it's only purpose is it to activate the Listeners if a EntityDamageByEntityEvent matches
 * the criteria (right Tool, ...)
 */
public class MTEntityDamageByEntityEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();

	private final Player player;
	private final ItemStack tool;
	private final boolean isBlocking;

	private final Entity entity;

	private final EntityDamageByEntityEvent event;

	public MTEntityDamageByEntityEvent(@NotNull Player player, @NotNull ItemStack tool, Entity entity, @NotNull EntityDamageByEntityEvent event, boolean isBlocking) {
		this.player = player;
		this.tool = tool;
		this.entity = entity;
		this.event = event;
		this.isBlocking = isBlocking;
	}

	/**
	 * Event constructor (used for the Armor-Effects as the Player and the Entity are the same)
	 *
	 * @param player      The Player
	 * @param tool   The ItemStack (MUST be a MineTinker-Tool)
	 * @param entity The Entity to apply effects on
	 * @param event  The BlockBreakEvent from which it was called
	 */
	public MTEntityDamageByEntityEvent(@NotNull Player player, @NotNull ItemStack tool, Entity entity, @NotNull EntityDamageByEntityEvent event) {
		this.player = player;
		this.tool = tool;
		this.entity = entity;
		this.event = event;
		this.isBlocking = false;
	}

	/**
	 * Event constructor (used for the Armor-Effects as the Player and the Entity are the same)
	 *
	 * @param player     The Player
	 * @param tool  The ItemStack (MUST be a MineTinker-Tool)
	 * @param event The BlockBreakEvent from which it was called
	 */
	public MTEntityDamageByEntityEvent(@NotNull Player player, @NotNull ItemStack tool, @NotNull EntityDamageByEntityEvent event) {
		this.player = player;
		this.tool = tool;
		this.entity = event.getEntity();
		this.event = event;
		this.isBlocking = false;
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

	public Entity getEntity() {
		return entity;
	}

	/**
	 * @return The original EntityDamageByEntityEvent
	 */
	public EntityDamageByEntityEvent getEvent() {
		return event;
	}

	@NotNull
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	/**
	 * @return if the original EntityDamageByEntityEvent is cancelled
	 */
	@Override
	public boolean isCancelled() {
		return event.isCancelled();
	}

	/**
	 * Sets the original EntityDamageByEntityEvent Cancelled-State
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

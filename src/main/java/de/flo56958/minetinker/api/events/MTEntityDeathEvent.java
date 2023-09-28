package de.flo56958.minetinker.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * The Event is only an extra trigger for the MineTinker-Modifiers
 * it's only purpose is it to activate the Listeners if a EntityDeathEvent matches
 * the criteria (right tool, ...)
 */
public class MTEntityDeathEvent extends Event {
	private static final HandlerList handlers = new HandlerList();

	private final Player player;
	private final ItemStack tool;

	private final EntityDeathEvent event;

	/**
	 * Event constructor
	 *
	 * @param tool  The ItemStack (MUST be a MineTinker-Tool)
	 * @param event The EntityDeathEvent from which it was called
	 */
	public MTEntityDeathEvent(@NotNull Player player, @NotNull ItemStack tool, @NotNull EntityDeathEvent event) {
		this.player = player;
		this.tool = tool;
		this.event = event;
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
	 * @return The original EntityDeathEvent
	 */
	public EntityDeathEvent getEvent() {
		return event;
	}

	@NotNull
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}

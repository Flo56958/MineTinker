package de.flo56958.minetinker.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * The Event is only an extra trigger for the MineTinker-Modifiers
 * it's only purpose is it to activate the Listeners if a PlayerInteractEvent matches
 * the criteria (right tool, ...)
 */
public class MTPlayerInteractEvent extends Event {
	private static final HandlerList handlers = new HandlerList();

	private final Player player;
	private final ItemStack tool;

	private final PlayerInteractEvent event;

	/**
	 * Event constructor
	 *
	 * @param tool  The ItemStack (MUST be a MineTinker-Tool)
	 * @param event The PlayerInteractEvent from which it was called
	 */
	public MTPlayerInteractEvent(@NotNull ItemStack tool, @NotNull PlayerInteractEvent event) {
		this.player = event.getPlayer();
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
	 * @return The original PlayerInteractEvent
	 */
	public PlayerInteractEvent getEvent() {
		return event;
	}

	@NotNull
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}

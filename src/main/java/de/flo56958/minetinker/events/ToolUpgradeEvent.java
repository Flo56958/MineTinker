package de.flo56958.minetinker.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ToolUpgradeEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final Player player;
	private final ItemStack tool;
	private final boolean wasSuccessful;

	/**
	 * Event constructor
	 *
	 * @param player        The Player that is involved in the Event
	 * @param tool          The Tool that was upgraded or failed to upgrade
	 * @param wasSuccessful Was the upgrade successful?
	 */
	public ToolUpgradeEvent(Player player, ItemStack tool, boolean wasSuccessful) {
		this.player = player;
		this.tool = tool;
		this.wasSuccessful = wasSuccessful;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@NotNull
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public Player getPlayer() {
		return player;
	}

	public ItemStack getTool() {
		return tool;
	}

	public boolean isSuccessful() {
		return wasSuccessful;
	}

}

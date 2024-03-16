package de.flo56958.minetinker.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ToolLevelUpEvent extends Event {
	private static final HandlerList handlers = new HandlerList();

	private final Player player;
	private final ItemStack tool;

	private int newSlots = 0;

	/**
	 * Event constructor
	 *
	 * @param player The Player that was involved in the Event
	 * @param tool   The Tool that got a Level-up
	 */
	public ToolLevelUpEvent(@Nullable Player player, @NotNull ItemStack tool) {
		this.player = player;
		this.tool = tool;
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

	@NotNull
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public int getNewSlots() {
		return newSlots;
	}

	public void setNewSlots(int newSlots) {
		this.newSlots = newSlots;
	}
}

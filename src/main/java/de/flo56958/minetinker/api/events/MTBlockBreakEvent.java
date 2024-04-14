package de.flo56958.minetinker.api.events;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * The Event is only an extra trigger for the MineTinker-Modifiers
 * it's only purpose is it to activate the Listeners if a BlockBreakEvent matches
 * the criteria (right tool, ...)
 */
public class MTBlockBreakEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();

	private final Player player;
	private final ItemStack tool;
	private final BlockFace blockFace;
	private final BlockBreakEvent event;

	/**
	 * Event constructor
	 *
	 * @param tool  The ItemStack (MUST be a MineTinker-Tool)
	 * @param event The BlockBreakEvent from which it was called
	 */
	public MTBlockBreakEvent(@NotNull ItemStack tool, @NotNull BlockBreakEvent event, @NotNull BlockFace blockFace) {
		this.player = event.getPlayer();
		this.tool = tool;
		this.event = event;
		this.blockFace = blockFace;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public BlockFace getBlockFace() {
		return blockFace;
	}

	public Player getPlayer() {
		return player;
	}

	public ItemStack getTool() {
		return tool;
	}

	/**
	 * @return The original BlockBreakEvent
	 */
	public BlockBreakEvent getEvent() {
		return event;
	}

	@NotNull
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	@Override
	public boolean isCancelled() {
		return event.isCancelled();
	}

	@Override
	public void setCancelled(boolean b) {
		event.setCancelled(b);
	}

	public Block getBlock() {
		return event.getBlock();
	}
}

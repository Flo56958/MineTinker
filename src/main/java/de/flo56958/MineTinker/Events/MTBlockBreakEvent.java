package de.flo56958.MineTinker.Events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MTBlockBreakEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final ItemStack tool;

    private final BlockBreakEvent event;

    /**
     * Event constructor
     * @param player The Player that is involved in the Event
     * @param tool The ItemStack (MUST be a MineTinker-Tool)
     * @param e The BlockBreakEvent
     */
    public MTBlockBreakEvent(@NotNull Player player, @NotNull ItemStack tool, @NotNull BlockBreakEvent e) {
        this.player = player;
        this.tool = tool;
        this.event = e;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack getTool() {
        return tool;
    }

    public BlockBreakEvent getEvent() { return event; }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
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

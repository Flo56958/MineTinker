package de.flo56958.MineTinker.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * The Event is only a extra trigger for the MineTinker-Modifiers
 * it's only purpose is it to activate the Listeners if a PlayerInteractEvent matches
 * the criteria (right Tool, ...)
 */
public class MTPlayerInteractEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final ItemStack tool;

    private final PlayerInteractEvent event;

    /**
     * Event constructor
     * @param tool The ItemStack (MUST be a MineTinker-Tool)
     * @param e The PlayerInteractEvent from which it was called
     */
    public MTPlayerInteractEvent(@NotNull ItemStack tool, @NotNull PlayerInteractEvent e) {
        this.player = e.getPlayer();
        this.tool = tool;
        this.event = e;
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
}

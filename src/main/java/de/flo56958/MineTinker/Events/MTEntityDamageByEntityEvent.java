package de.flo56958.MineTinker.Events;

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

    private final EntityDamageByEntityEvent event;

    /**
     * Event constructor (used for the Armor-Effects as the Player and the Entity are the same)
     * @param p The Player
     * @param tool The ItemStack (MUST be a MineTinker-Tool)
     * @param e    The BlockBreakEvent from which it was called
     */
    public MTEntityDamageByEntityEvent(@NotNull Player p, @NotNull ItemStack tool, @NotNull EntityDamageByEntityEvent e) {
        this.player = p;
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
     * @return The original EntityDamageByEntityEvent
     */
    public EntityDamageByEntityEvent getEvent() {
        return event;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
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
     * @param b true/false is cancelled
     */
    @Override
    public void setCancelled(boolean b) {
        event.setCancelled(b);
    }
}

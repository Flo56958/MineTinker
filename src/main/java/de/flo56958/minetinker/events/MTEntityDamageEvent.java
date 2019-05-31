package de.flo56958.minetinker.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * The Event is only a extra trigger for the minetinker-modifiers (mostly used for Armor)
 * it's only purpose is it to activate the listeners if a EntityDamageEvent matches
 * the criteria (right Armor, ...)
 */
public class MTEntityDamageEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final ItemStack tool;

    private final EntityDamageEvent event;

    /**
     * Event constructor
     * @param tool The ItemStack (MUST be a minetinker-Tool)
     * @param e    The BlockBreakEvent from which it was called
     */
    public MTEntityDamageEvent(@NotNull ItemStack tool, @NotNull EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            this.player = (Player) e.getEntity();
        } else {
            this.player = null;
        }
        this.tool = tool;
        this.event = e;
    }

    /**
     * Event constructor (used for the Armor-Effects as the Player and the Entity are the same)
     * @param p The Player
     * @param tool The ItemStack (MUST be a minetinker-Tool)
     * @param e    The BlockBreakEvent from which it was called
     */
    public MTEntityDamageEvent(@NotNull Player p, @NotNull ItemStack tool, @NotNull EntityDamageEvent e) {
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

    public static HandlerList getHandlerList() {
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
     * This system is linked to the original Event as this is only a trigger for the minetinker-modifiers
     * @param b true/false is cancelled
     */
    @Override
    public void setCancelled(boolean b) {
        event.setCancelled(b);
    }
}

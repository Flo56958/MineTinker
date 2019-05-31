package de.flo56958.MineTinker.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ToolLevelUpEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final ItemStack tool;

    /**
     * Event constructor
     * @param player The Player that was involved in the Event
     * @param tool The Tool that got a Level-up
     */
    public ToolLevelUpEvent(@NotNull Player player, @NotNull ItemStack tool) {
        this.player = player;
        this.tool = tool;
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

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

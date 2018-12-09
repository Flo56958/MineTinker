package de.flo56958.MineTinker.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class ToolUpgradeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final ItemStack tool;
    private final boolean wasSuccessful;

    public ToolUpgradeEvent(Player player, ItemStack tool, boolean wasSuccessful) {
        this.player = player;
        this.tool = tool;
        this.wasSuccessful = wasSuccessful;
    }

    @Override
    public HandlerList getHandlers() { return handlers; }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() { return player; }

    public ItemStack getTool() { return tool; }

    public boolean isWasSuccessful() { return wasSuccessful; }

}

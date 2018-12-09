package de.flo56958.MineTinker.Events;

import de.flo56958.MineTinker.Modifiers.Modifier;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class ModifierApplyEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final ItemStack tool;
    private final Modifier mod;
    private final int slotsRemaining;

    private final boolean isCommand;

    public ModifierApplyEvent(Player player, ItemStack tool, Modifier mod, int slotsRemaining, boolean isCommand) {
        this.player = player;
        this.tool = tool;
        this.mod = mod;
        this.slotsRemaining = slotsRemaining;
        this.isCommand = isCommand;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack getTool() {
        return tool;
    }

    public Modifier getMod() {
        return mod;
    }

    public int getSlotsRemaining() {
        return slotsRemaining;
    }

    public boolean isCommand() {
        return isCommand;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

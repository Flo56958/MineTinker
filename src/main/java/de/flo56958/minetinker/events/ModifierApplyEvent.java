package de.flo56958.minetinker.events;

import de.flo56958.minetinker.modifiers.Modifier;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ModifierApplyEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final ItemStack tool;
    private final Modifier mod;
    private final int slotsRemaining;

    private final boolean isCommand;

    /**
     * Event constructor
     * @param player The Player that is involved in the Event
     * @param tool The Itemstack that gets modified
     * @param mod The Modifier that is involved
     * @param slotsRemaining How many Slots are remaining on the Tool/Armor
     * @param isCommand Was the Event triggered as a result of a command input?
     */
    public ModifierApplyEvent(Player player, ItemStack tool, @NotNull Modifier mod, int slotsRemaining, boolean isCommand) {
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

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

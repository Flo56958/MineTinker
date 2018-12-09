package de.flo56958.MineTinker.Events;

import de.flo56958.MineTinker.Data.ModifierFailCause;
import de.flo56958.MineTinker.Modifiers.Modifier;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class ModifierFailEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final ItemStack tool;
    private final Modifier mod;
    private final ModifierFailCause failCause;
    private final boolean isCommand;

    public ModifierFailEvent(Player player, ItemStack tool, Modifier mod, ModifierFailCause failCause, boolean isCommand) {
        this.player = player;
        this.tool = tool;
        this.mod = mod;
        this.failCause = failCause;
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

    public ModifierFailCause getFailCause() {
        return failCause;
    }

    public boolean isCommand() { return isCommand; }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}

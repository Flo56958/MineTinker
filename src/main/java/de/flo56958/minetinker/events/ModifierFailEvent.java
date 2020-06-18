package de.flo56958.minetinker.events;

import de.flo56958.minetinker.data.ModifierFailCause;
import de.flo56958.minetinker.modifiers.ModManager;
import de.flo56958.minetinker.modifiers.Modifier;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ModifierFailEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final Player player;
	private final ItemStack tool;
	private final Modifier mod;
	private final ModifierFailCause failCause;
	private final boolean isCommand;

	/**
	 * Event constructor
	 *
	 * @param player    The player that is involved in the Event
	 * @param tool      The Itemstack that could not get modified
	 * @param failCause The cause of the failure
	 * @param isCommand Was the Event triggered as a result of a command input?
	 */
	public ModifierFailEvent(Player player, ItemStack tool, ModifierFailCause failCause, boolean isCommand) {
		this.player = player;
		this.tool = tool;
		this.mod = ModManager.getInstance().getAllowedMods().get(0);
		this.failCause = failCause;
		this.isCommand = isCommand;
	}

	/**
	 * Event constructor
	 *
	 * @param player    The player that is involved in the Event
	 * @param tool      The Itemstack that could not get modified
	 * @param mod       The Modifier that was not able to be applied
	 * @param failCause The cause of the failure
	 * @param isCommand Was the Event triggered as a result of a command input?
	 */
	public ModifierFailEvent(Player player, ItemStack tool, Modifier mod, ModifierFailCause failCause, boolean isCommand) {
		this.player = player;
		this.tool = tool;
		this.mod = mod;
		this.failCause = failCause;
		this.isCommand = isCommand;
	}

	public static HandlerList getHandlerList() {
		return handlers;
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

	public boolean isCommand() {
		return isCommand;
	}

	@NotNull
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}

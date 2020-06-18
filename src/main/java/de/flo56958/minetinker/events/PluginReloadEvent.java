package de.flo56958.minetinker.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PluginReloadEvent extends Event {
	private static final HandlerList handlers = new HandlerList();

	@Override
	public @NotNull HandlerList getHandlers() {
		return null;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}

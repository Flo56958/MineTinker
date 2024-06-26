package de.flo56958.minetinker.api;

import de.flo56958.minetinker.commands.ArgumentType;
import de.flo56958.minetinker.modifiers.ModManager;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public interface SubCommand {
	ModManager modManager = ModManager.instance();

	@SuppressWarnings("SameReturnValue")
	boolean onCommand(@NotNull CommandSender sender, @NotNull String[] args);

	/**
	 * This method will be called by the CommandManager's TabCompleter after permission check.
	 *
	 * @param sender The command sender that will receive the auto-completion hints
	 * @param args   The arguments already written by the command sender
	 * @return The hints or null
	 */
	@Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String[] args);

	@NotNull String getName();

	@NotNull List<String> getAliases(boolean withName);

	@NotNull String getPermission();

	/**
	 * Gets the indices that have parsable data
	 *
	 * @return A Map which maps the indices to the ArgumentTypes to parse
	 */
	@NotNull
	default Map<Integer, List<ArgumentType>> getArgumentsToParse() { return Map.of(); }

	@NotNull String syntax();
}

package de.flo56958.MineTinker.Commands.subs;

import de.flo56958.MineTinker.Commands.ArgumentType;
import de.flo56958.MineTinker.Commands.CommandManager;
import de.flo56958.MineTinker.Data.GUIs;
import de.flo56958.MineTinker.Utilities.LanguageManager;
import de.flo56958.MineTinker.api.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditConfigurationCommand implements SubCommand {
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull String[] args) {
		if (!(sender instanceof Player)) {
			CommandManager.sendError(sender, LanguageManager.getString("Commands.Failure.Cause.PlayerOnlyCommand"));
			return true;
		}
		GUIs.getConfigurationsGUI().show((Player) sender);
		return true;
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
		return null;
	}

	@Override
	public @NotNull String getName() {
		return "editconfiguration";
	}

	@Override
	public @NotNull List<String> getAliases(boolean withName) {
		ArrayList<String> aliases = new ArrayList<>();
		if (withName) aliases.add(getName());
		aliases.add("ec");
		aliases.add("editconfig");
		return aliases;
	}

	@Override
	public @NotNull String getPermission() {
		return "minetinker.commands.editconfig";
	}

	@Override
	public @NotNull Map<Integer, List<ArgumentType>> getArgumentsToParse() {
		return new HashMap<>();
	}

	@Override
	public @NotNull String syntax() {
		return "/mt editconfiguration";
	}
}

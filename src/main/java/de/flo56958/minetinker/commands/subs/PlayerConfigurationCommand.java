package de.flo56958.minetinker.commands.subs;

import de.flo56958.minetinker.api.SubCommand;
import de.flo56958.minetinker.commands.CommandManager;
import de.flo56958.minetinker.utils.LanguageManager;
import de.flo56958.minetinker.utils.playerconfig.PlayerConfigurationManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PlayerConfigurationCommand implements SubCommand {
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull String[] args) {
		if (!(sender instanceof Player player)) {
			CommandManager.sendError(sender, LanguageManager.getString("Commands.Failure.Cause.PlayerMissing"));
			return true;
		}

		PlayerConfigurationManager.getInstance().getPlayerConfigGUI(player).show(player);
		return true;
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
		return null;
	}

	@Override
	public @NotNull String getName() {
		return "playerconfiguration";
	}

	@Override
	public @NotNull List<String> getAliases(boolean withName) {
		ArrayList<String> aliases = new ArrayList<>();
		if (withName) aliases.add(getName());
		aliases.add("pc");
		return aliases;
	}

	@Override
	public @NotNull String getPermission() {
		return "minetinker.commands.playerconfiguration";
	}

	@Override
	public @NotNull String syntax() {
		return "/mt playerconfiguration";
	}
}

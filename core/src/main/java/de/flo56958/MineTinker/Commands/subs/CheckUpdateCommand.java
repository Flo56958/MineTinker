package de.flo56958.MineTinker.Commands.subs;

import de.flo56958.MineTinker.Commands.ArgumentType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.LanguageManager;
import de.flo56958.MineTinker.Utilities.Updater;
import de.flo56958.MineTinker.api.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Syntax of /mt checkupdate:
 * 		/mt checkupdate
 *
 * Legend:
 * 		{ }: not necessary
 * 		[ ]: necessary
 */
public class CheckUpdateCommand implements SubCommand {
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull String[] args) {
		Player player = null;
		if (sender instanceof Player) {
			player = (Player) sender;
		}
		if (Main.getPlugin().getConfig().getBoolean("CheckForUpdates")) {
			ChatWriter.sendMessage(sender, ChatColor.WHITE,
					LanguageManager.getString("Commands.CheckUpdate.Start", player));

			new BukkitRunnable() {
				@Override
				public void run() {
					Updater.checkForUpdate(sender);
				}
			}.runTaskLater(Main.getPlugin(), 20);
		} else {
			ChatWriter.sendMessage(sender, ChatColor.RED,
					LanguageManager.getString("Commands.CheckUpdate.Disabled", player));
		}
		return true;
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
		return null;
	}

	@Override
	public @NotNull String getName() {
		return "checkupdate";
	}

	@Override
	public @NotNull List<String> getAliases(boolean withName) {
		ArrayList<String> aliases = new ArrayList<>();
		if (withName) aliases.add(getName());
		aliases.add("cu");
		return aliases;
	}

	@Override
	public @NotNull String getPermission() {
		return "minetinker.commands.checkupdate";
	}

	@Override
	public @NotNull Map<Integer, List<ArgumentType>> getArgumentsToParse() {
		return new HashMap<>();
	}
}

package de.flo56958.minetinker.commands.subs;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.api.SubCommand;
import de.flo56958.minetinker.commands.ArgumentType;
import de.flo56958.minetinker.data.GUIs;
import de.flo56958.minetinker.data.Lists;
import de.flo56958.minetinker.listeners.BuildersWandListener;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.ConfigurationManager;
import de.flo56958.minetinker.utils.LanguageManager;
import de.flo56958.minetinker.utils.Updater;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReloadCommand implements SubCommand {
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull String[] args) {
		Player player = null;

		if (sender instanceof Player) {
			player = (Player) sender;
		}

		ChatWriter.sendMessage(sender, ChatColor.RED,
				LanguageManager.getString("Commands.Reload.Note1", player));

		ChatWriter.sendMessage(sender, ChatColor.WHITE,
				LanguageManager.getString("Commands.Reload.Configs", player));
		MineTinker.getPlugin().reloadConfig();
		ChatWriter.reload();
		ConfigurationManager.reload();
		Lists.reload();

		LanguageManager.reload();

		ChatWriter.sendMessage(sender, ChatColor.WHITE,
				LanguageManager.getString("Commands.Reload.ModManager", player));
		modManager.reload();

		ChatWriter.sendMessage(sender, ChatColor.WHITE,
				LanguageManager.getString("Commands.Reload.Builderswands", player));
		BuildersWandListener.reload();

		ChatWriter.sendMessage(sender, ChatColor.WHITE,
				LanguageManager.getString("Commands.Reload.GUIs", player));
		GUIs.reload();

		ChatWriter.sendMessage(sender, ChatColor.WHITE,
				LanguageManager.getString("Commands.Reload.Finish", player));

		if (MineTinker.getPlugin().getConfig().getBoolean("CheckForUpdates")) {
			Bukkit.getScheduler().runTaskLaterAsynchronously(MineTinker.getPlugin(), (Runnable) Updater::checkForUpdate, 20);
		}
		return true;
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
		return null;
	}

	@Override
	public @NotNull String getName() {
		return "reload";
	}

	@Override
	public @NotNull List<String> getAliases(boolean withName) {
		ArrayList<String> aliases = new ArrayList<>();
		if (withName) aliases.add(getName());
		aliases.add("r");
		return aliases;
	}

	@Override
	public @NotNull String getPermission() {
		return "minetinker.commands.reload";
	}

	@Override
	public @NotNull Map<Integer, List<ArgumentType>> getArgumentsToParse() {
		return new HashMap<>();
	}

	@Override
	public @NotNull String syntax() {
		return "/mt reload";
	}
}

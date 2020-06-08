package de.flo56958.minetinker.commands.subs;

import de.flo56958.minetinker.commands.ArgumentType;
import de.flo56958.minetinker.Main;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.LanguageManager;
import de.flo56958.minetinker.api.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfoCommand implements SubCommand {
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull String[] args) {
		Player player = null;
		if(sender instanceof Player) player = (Player) sender;

		ChatWriter.sendMessage(sender, ChatColor.WHITE, LanguageManager.getString("Commands.Info.Line1", player)
				.replaceFirst("%ver", Main.getPlugin().getDescription().getVersion()));
		ChatWriter.sendMessage(sender, ChatColor.WHITE, LanguageManager.getString("Commands.Info.Line2", player));
		return true;
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
		return null;
	}

	@Override
	public @NotNull String getName() {
		return "info";
	}

	@Override
	public @NotNull List<String> getAliases(boolean withName) {
		ArrayList<String> aliases = new ArrayList<>();
		if (withName) aliases.add(getName());
		aliases.add("i");
		return aliases;
	}

	@Override
	public @NotNull String getPermission() {
		return "minetinker.commands.info";
	}

	@Override
	public @NotNull Map<Integer, List<ArgumentType>> getArgumentsToParse() {
		return new HashMap<>();
	}

	@Override
	public @NotNull String syntax() {
		return "/mt info";
	}
}

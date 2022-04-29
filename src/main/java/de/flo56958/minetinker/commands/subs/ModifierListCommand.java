package de.flo56958.minetinker.commands.subs;

import de.flo56958.minetinker.api.SubCommand;
import de.flo56958.minetinker.commands.ArgumentType;
import de.flo56958.minetinker.data.GUIs;
import de.flo56958.minetinker.modifiers.ModManager;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Syntax of /mt modifiers:
 * 		/mt modifiers {-t}
 *  -t will paste information in the chat instead of using the GUI
 *
 * Legend:
 * 		{ }: not necessary
 * 		[ ]: necessary
 */
public class ModifierListCommand implements SubCommand {
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull String[] args) {
		if (sender instanceof Player) {
			if (!(args.length >= 2 && args[1].equalsIgnoreCase("-t"))) {
				GUIs.getModGUI().show((Player) sender);
				return true;
			}
		}

		ChatWriter.sendMessage(sender, ChatColor.GOLD, LanguageManager.getString("Commands.ModList"));

		int index = 1;

		for (Modifier m : ModManager.instance().getAllowedMods()) {
			ChatWriter.sendMessage(sender, ChatColor.WHITE, index++ + ". " + m.getColor() + m.getName()
																		+ ChatColor.WHITE + ": " + m.getDescription());
		}
		return true;
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
		List<String> result = new ArrayList<>();
		if (args.length == 2) {
			result.add("-t");
		}
		return result;
	}

	@Override
	public @NotNull String getName() {
		return "modifiers";
	}

	@Override
	public @NotNull List<String> getAliases(boolean withName) {
		ArrayList<String> aliases = new ArrayList<>();
		if (withName) aliases.add(getName());
		aliases.add("mods");
		return aliases;
	}

	@Override
	public @NotNull String getPermission() {
		return "minetinker.commands.modifiers";
	}

	@Override
	public @NotNull Map<Integer, List<ArgumentType>> getArgumentsToParse() {
		return new HashMap<>();
	}

	@Override
	public @NotNull String syntax() {
		return "/mt modifiers {-t}";
	}
}

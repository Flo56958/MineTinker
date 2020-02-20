package de.flo56958.MineTinker.Commands.subs;

import de.flo56958.MineTinker.Commands.ArgumentType;
import de.flo56958.MineTinker.Commands.CommandManager;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Utilities.LanguageManager;
import de.flo56958.MineTinker.api.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Syntax of /mt convert:
 * 		/mt convert {Player}
 *
 * Legend:
 * 		{ }: not necessary
 * 		[ ]: necessary
 */
public class ConvertCommand implements SubCommand {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull String[] args) {
		Player player = null;

		if (args.length == 1) {
			if (sender instanceof Player) {
				player = (Player) sender;
			} else {
				CommandManager.sendError(sender, LanguageManager.getString("Commands.Failure.Cause.InvalidArguments"));
				return true;
			}
		} else if (args.length > 2) {
			player = Bukkit.getPlayer(args[1]);
		}

		ModManager.instance().convertItemStack(player.getInventory().getItemInMainHand());
		return true;
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
		List<String> result = new ArrayList<>();
		if (args.length == 2) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				result.add(player.getDisplayName());
			}

			if (sender instanceof Entity || sender instanceof BlockState) {
				result.add("@p");
			}
		}
		return result;
	}


	@Override @NotNull
	public String getName() {
		return "convert";
	}

	@Override @NotNull
	public List<String> getAliases(boolean withName) {
		ArrayList<String> aliases = new ArrayList<>();
		if (withName) aliases.add(getName());
		aliases.add("c");
		aliases.add("con");
		return aliases;
	}

	@Override @NotNull
	public String getPermission() {
		return "minetinker.commands.convert";
	}

	@Override @NotNull
	public Map<Integer, List<ArgumentType>> getArgumentsToParse() {
		Map<Integer, List<ArgumentType>> argumentsToParse = new HashMap<>();
		argumentsToParse.put(1, Collections.singletonList(ArgumentType.PLAYER));
		return argumentsToParse;
	}

	@Override
	public @NotNull String syntax() {
		return "/mt convert {Player}";
	}
}

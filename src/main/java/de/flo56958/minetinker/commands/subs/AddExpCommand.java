package de.flo56958.minetinker.commands.subs;

import de.flo56958.minetinker.api.SubCommand;
import de.flo56958.minetinker.commands.ArgumentType;
import de.flo56958.minetinker.commands.CommandManager;
import de.flo56958.minetinker.modifiers.ModManager;
import de.flo56958.minetinker.utils.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Syntax of /mt addexp:
 * 		/mt addexp {Player} [Amount]
 *
 * Legend:
 * 		{ }: not necessary
 * 		[ ]: necessary
 */
public class AddExpCommand implements SubCommand {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull String[] args) {
		Player player;
		int amount;

		if (args.length == 2) {
			if (sender instanceof Player) {
				player = (Player) sender;
			} else {
				CommandManager.sendError(sender,
						LanguageManager.getString("Commands.Failure.Cause.InvalidArguments"));
				return true;
			}
			try {
				amount = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				CommandManager.sendError(sender,
						LanguageManager.getString("Commands.Failure.Cause.NumberFormatException"));
				return true;
			}
		} else if (args.length > 2) {
			player = Bukkit.getPlayer(args[1]);
			try {
				amount = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				CommandManager.sendError(sender,
						LanguageManager.getString("Commands.Failure.Cause.NumberFormatException"));
				return true;
			}
		} else {
			CommandManager.sendError(sender,
					LanguageManager.getString("Commands.Failure.Cause.InvalidArguments"));
			return true;
		}
		if (player == null) {
			CommandManager.sendError(sender, LanguageManager.getString("Commands.Failure.Cause.PlayerNotFound")
					.replace("%p", args[1]));
			return true;
		}

		ItemStack tool = player.getInventory().getItemInMainHand();
		ModManager modManager = ModManager.instance();

		if (modManager.isToolViable(tool) || modManager.isArmorViable(tool)) {
			modManager.addExp(player, tool, amount, true);
		} else {
			CommandManager.sendError(sender, LanguageManager.getString("Commands.Failure.Cause.InvalidItem"));
		}

		return true;
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
		List<String> result = new ArrayList<>();
		if (args.length == 2) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				result.add(player.getName());
			}
			result.add("@a");
			result.add("@r");

			if (sender instanceof Entity || sender instanceof BlockState) {
				result.add("@aw");
				result.add("@p");
				result.add("@rw");
			}
		} else if (args.length == 3) {
			for (int i = 0; i < 10; i++) {
				result.add(String.valueOf(i));
			}
		}
		return result;
	}


	@Override @NotNull
	public String getName() {
		return "addexp";
	}

	@Override @NotNull
	public List<String> getAliases(boolean withName) {
		ArrayList<String> aliases = new ArrayList<>();
		if (withName) aliases.add(getName());
		aliases.add("ae");
		return aliases;
	}

	@Override @NotNull
	public String getPermission() {
		return "minetinker.commands.addexp";
	}

	@Override @NotNull
	public Map<Integer, List<ArgumentType>> getArgumentsToParse() {
		Map<Integer, List<ArgumentType>> argumentsToParse = new HashMap<>();
		argumentsToParse.put(1, Arrays.asList(ArgumentType.PLAYER, ArgumentType.RANDOM_NUMBER));
		argumentsToParse.put(2, Collections.singletonList(ArgumentType.RANDOM_NUMBER));
		return argumentsToParse;
	}

	@Override
	public @NotNull String syntax() {
		return "/mt addexp {Player} [Amount]";
	}
}

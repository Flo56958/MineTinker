package de.flo56958.minetinker.commands.subs;

import de.flo56958.minetinker.api.SubCommand;
import de.flo56958.minetinker.commands.ArgumentType;
import de.flo56958.minetinker.commands.CommandManager;
import de.flo56958.minetinker.utils.LanguageManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Syntax of /mt addexp:
 * /mt addexp [Amount] {CallLevelUpEvents}
 * <p>
 * Legend:
 * { }: not necessary
 * [ ]: necessary
 */
public class AddExpCommand implements SubCommand {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull String[] args) {
		Player player;
		int amount;
		boolean callLevelUpEvents = false;

		if (args.length >= 2) {
			if (sender instanceof Player p) {
				player = p;
			} else {
				CommandManager.sendError(sender,
						LanguageManager.getString("Commands.Failure.Cause.InvalidArguments"));
				return true;
			}
			try {
				amount = Integer.parseInt(args[1]);
				if (args.length >= 3) callLevelUpEvents = Boolean.parseBoolean(args[2]);
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

		ItemStack tool = player.getInventory().getItemInMainHand();

		if (modManager.isToolViable(tool) || modManager.isArmorViable(tool)) {
			modManager.addExp(player, tool, amount, callLevelUpEvents);
		} else {
			CommandManager.sendError(sender, LanguageManager.getString("Commands.Failure.Cause.InvalidItem"));
		}

		return true;
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
		List<String> result = new ArrayList<>();
		if (args.length == 2) {
			for (int i = 0; i < 10; i++) {
				result.add(String.valueOf(i));
			}
		} else if (args.length == 3) {
			result.add(String.valueOf(false));
			result.add(String.valueOf(true));
		}
		return result;
	}


	@Override
	public @NotNull String getName() {
		return "addexp";
	}

	@Override
	public @NotNull List<String> getAliases(boolean withName) {
		ArrayList<String> aliases = new ArrayList<>();
		if (withName) aliases.add(getName());
		aliases.add("ae");
		return aliases;
	}

	@Override
	public @NotNull String getPermission() {
		return "minetinker.commands.addexp";
	}

	@Override
	public @NotNull Map<Integer, List<ArgumentType>> getArgumentsToParse() {
		Map<Integer, List<ArgumentType>> argumentsToParse = new HashMap<>();
		argumentsToParse.put(1, Collections.singletonList(ArgumentType.RANDOM_NUMBER));
		argumentsToParse.put(2, Collections.singletonList(ArgumentType.BOOLEAN));
		return argumentsToParse;
	}

	@Override
	public @NotNull String syntax() {
		return "/mt addexp [Amount] {CallLevelUpEvents}";
	}
}

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
 * Syntax of /mt convert:
 * 		/mt convert {Player}
 * <p>
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
				CommandManager.sendError(sender,
						LanguageManager.getString("Commands.Failure.Cause.PlayerMissing"));
				return true;
			}
		} else if (args.length > 2) {
			player = Bukkit.getPlayer(args[1]);

			if (player == null) {
				CommandManager.sendError(sender,
						LanguageManager.getString("Commands.Failure.Cause.PlayerNotFound")
								.replaceAll("%p", args[1]));
				return true;
			}
		}

		if (player == null) {
			CommandManager.sendError(sender,
					LanguageManager.getString("Commands.Failure.Cause.PlayerMissing"));
			return true;
		}

		ItemStack item = player.getInventory().getItemInMainHand();

		ModManager.instance().convertItemStack(item, player);
		return true;
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
		List<String> result = new ArrayList<>();
		if (args.length == 2) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				result.add(player.getName());
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

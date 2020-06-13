package de.flo56958.minetinker.commands.subs;

import de.flo56958.minetinker.commands.ArgumentType;
import de.flo56958.minetinker.commands.CommandManager;
import de.flo56958.minetinker.data.Lists;
import de.flo56958.minetinker.modifiers.ModManager;
import de.flo56958.minetinker.utils.LanguageManager;
import de.flo56958.minetinker.api.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Syntax of /mt convert:
 * 		/mt convert {player} [name]
 *
 * Legend:
 * 		{ }: not necessary
 * 		[ ]: necessary
 */
public class NameCommand implements SubCommand {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull String[] args) {
		if (args.length < 2) {
			CommandManager.sendError(sender, LanguageManager.getString("Commands.Failure.Cause.InvalidArguments"));
			return true;
		}

		Player player = Bukkit.getPlayer(args[1]);
		if (sender instanceof Player && player == null) player = (Player) sender;
		if (player == null) {
			CommandManager.sendError(sender, LanguageManager.getString("Commands.Failure.Cause.PlayerMissing"));
			return true;
		}
		ItemStack tool = player.getInventory().getItemInMainHand();

		if (ModManager.instance().isToolViable(tool) || ModManager.instance().isArmorViable(tool)) {
			StringBuilder name = new StringBuilder();

			for (int i = 1; i < args.length; i++) {
				name.append(" ").append(args[i].replace('&', '§'));
			}

			name = new StringBuilder(name.substring(1));

			//Test given Name with Blacklist
			String name_ = name.toString();
			for (String pattern : Lists.NAME_COMMAND_BLACKLIST) {
				if (Pattern.compile(pattern).matcher(name_).find()) {
					CommandManager.sendError(sender,
							LanguageManager.getString("Commands.Failure.Cause.NameNotAllowed"));
					return true;
				}
			}

			ItemMeta meta = tool.getItemMeta();

			if (meta != null) {
				meta.setDisplayName(name_);
				tool.setItemMeta(meta);
			}
		} else {
			CommandManager.sendError(sender, LanguageManager.getString("Commands.Failure.Cause.InvalidItem"));
		}
		return true;
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
		ArrayList<String> result = new ArrayList<>();
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
		}
		return result;
	}


	@Override @NotNull
	public String getName() {
		return "name";
	}

	@Override @NotNull
	public List<String> getAliases(boolean withName) {
		ArrayList<String> aliases = new ArrayList<>();
		if (withName) aliases.add(getName());
		aliases.add("n");
		return aliases;
	}

	@Override @NotNull
	public String getPermission() {
		return "minetinker.commands.name";
	}

	@Override @NotNull
	public Map<Integer, List<ArgumentType>> getArgumentsToParse() {
		Map<Integer, List<ArgumentType>> argumentsToParse = new HashMap<>();
		argumentsToParse.put(1, Collections.singletonList(ArgumentType.PLAYER));
		return argumentsToParse;
	}

	@Override
	public @NotNull String syntax() {
		return "/mt name {Player} [Name] ...";
	}
}

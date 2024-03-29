package de.flo56958.minetinker.commands.subs;

import de.flo56958.minetinker.api.SubCommand;
import de.flo56958.minetinker.commands.CommandManager;
import de.flo56958.minetinker.data.Lists;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.LanguageManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Syntax of /mt convert:
 * /mt convert [name]
 * <p>
 * Legend:
 * { }: not necessary
 * [ ]: necessary
 */
public class NameCommand implements SubCommand {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull String[] args) {
		if (args.length < 1) {
			CommandManager.sendError(sender, LanguageManager.getString("Commands.Failure.Cause.InvalidArguments"));
			return true;
		}

		if (!(sender instanceof Player player)) {
			CommandManager.sendError(sender, LanguageManager.getString("Commands.Failure.Cause.PlayerMissing"));
			return true;
		}

		ItemStack tool = player.getInventory().getItemInMainHand();

		if (modManager.isToolViable(tool) || modManager.isArmorViable(tool)) {
			StringBuilder name = new StringBuilder();

			for (int i = 1; i < args.length; i++) {
				name.append(" ").append(args[i].replace('&', '§'));
			}

			//Test given Name with Blacklist
			String name_ = ChatWriter.addColors(name.substring(1));
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
		return new ArrayList<>();
	}


	@Override
	public @NotNull String getName() {
		return "name";
	}

	@Override
	public @NotNull List<String> getAliases(boolean withName) {
		ArrayList<String> aliases = new ArrayList<>();
		if (withName) aliases.add(getName());
		aliases.add("n");
		return aliases;
	}

	@Override
	public @NotNull String getPermission() {
		return "minetinker.commands.name";
	}

	@Override
	public @NotNull String syntax() {
		return "/mt name [Name] ...";
	}
}

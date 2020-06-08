package de.flo56958.minetinker.commands.subs;

import de.flo56958.minetinker.commands.ArgumentType;
import de.flo56958.minetinker.commands.CommandManager;
import de.flo56958.minetinker.modifiers.ModManager;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.LanguageManager;
import de.flo56958.minetinker.api.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Syntax of /mt removemodifier:
 * 		/mt removemodifier [Modifier] {Amount}
 *
 * Legend:
 * 		{ }: not necessary
 * 		[ ]: necessary
 */
public class RemoveModifierCommand implements SubCommand {
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull String[] args) {
		if (!(sender instanceof Player)) {
			CommandManager.sendError(sender, LanguageManager.getString("Commands.Failure.Cause.PlayerOnlyCommand"));
			return true;
		}
		Player player = (Player) sender;
		if (args.length >= 2) {
			ModManager modManager = ModManager.instance();
			for (Modifier m : modManager.getAllowedMods()) {
				if (m.getName().equalsIgnoreCase(args[1].replaceAll("_", " "))) {
					ItemStack tool = player.getInventory().getItemInMainHand();

					if (modManager.isToolViable(tool) || modManager.isArmorViable(tool)) {
						int toAdd = 0;
						if (args.length >= 3) {
							try {
								int a = Integer.parseInt(args[2]);
								toAdd = modManager.getModLevel(tool, m) - a;
							} catch (NumberFormatException ignored) {
								CommandManager.sendError(sender,
										LanguageManager.getString("Commands.Failure.Cause.NumberFormatException"));
								return true;
							}
						}
						modManager.removeMod(tool, m);
						for (int i = 0; i < toAdd; i++) {
							if (!modManager.addMod(player, tool, m, true, false, true))
								break;
						}
					} else
						CommandManager.sendError(sender,
								LanguageManager.getString("Commands.Failure.Cause.InvalidItem"));
					return true;
				}
			}
			CommandManager.sendError(sender,
					LanguageManager.getString("Commands.Failure.Cause.InvalidArguments"));
		}
		return true;
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
		List<String> result = new ArrayList<>();
		if (sender instanceof Player) {
			ItemStack item = ((Player) sender).getInventory().getItemInMainHand();
			if (args.length == 2) {
				for (Modifier mod : ModManager.instance().getAllowedMods()) {
					if (ModManager.instance().hasMod(item, mod))
						result.add(mod.getName().replaceAll(" ", "_"));
				}
			} else if (args.length == 3) {
				for (int i = 0; i < 10; i++) {
					result.add(String.valueOf(i));
				}
			}
		}
		return result;
	}

	@Override
	public @NotNull String getName() {
		return "removemodifier";
	}

	@Override
	public @NotNull List<String> getAliases(boolean withName) {
		ArrayList<String> aliases = new ArrayList<>();
		if (withName) aliases.add(getName());
		aliases.add("rm");
		aliases.add("rmod");
		return aliases;
	}

	@Override
	public @NotNull String getPermission() {
		return "minetinker.commands.removemod";
	}

	@Override
	public @NotNull Map<Integer, List<ArgumentType>> getArgumentsToParse() {
		Map<Integer, List<ArgumentType>> argumentsToParse = new HashMap<>();
		argumentsToParse.put(2, Collections.singletonList(ArgumentType.RANDOM_NUMBER));
		return argumentsToParse;
	}

	@Override
	public @NotNull String syntax() {
		return "/mt removemodifier [Modifier] {Amount}";
	}
}

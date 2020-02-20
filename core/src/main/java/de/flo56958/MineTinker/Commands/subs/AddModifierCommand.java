package de.flo56958.MineTinker.Commands.subs;

import de.flo56958.MineTinker.Commands.ArgumentType;
import de.flo56958.MineTinker.Commands.CommandManager;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.LanguageManager;
import de.flo56958.MineTinker.api.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Syntax of /mt addmodifier:
 * 		/mt addmodifier [Modifier] {Level}
 *
 * Legend:
 * 		{ }: not necessary
 * 		[ ]: necessary
 */
public class AddModifierCommand implements SubCommand {
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
						int amount = 1;
						if (args.length >= 3) {
							try {
								amount = Integer.parseInt(args[2]);
							} catch (NumberFormatException e) {
								CommandManager.sendError(sender, LanguageManager.getString("Commands.Failure.Cause.NumberFormatException"));
							}
						}
						for (int i = 0; i < amount; i++) {
							if (!modManager.addMod(player, tool, m, true, false, false)) break;
						}
					}
					break;
				}
			}
		}
		return true;
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
		List<String> result = new ArrayList<>();
		if (sender instanceof Player) {
			if (args.length == 2) {
				for (Modifier mod : ModManager.instance().getAllowedMods()) {
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
		return "addmodifier";
	}

	@Override
	public @NotNull List<String> getAliases(boolean withName) {
		ArrayList<String> aliases = new ArrayList<>();
		if (withName) aliases.add(getName());
		aliases.add("am");
		return aliases;
	}

	@Override
	public @NotNull String getPermission() {
		return "minetinker.commands.addmod";
	}

	@Override
	public @NotNull Map<Integer, List<ArgumentType>> getArgumentsToParse() {
		Map<Integer, List<ArgumentType>> argumentsToParse = new HashMap<>();
		argumentsToParse.put(3, Collections.singletonList(ArgumentType.RANDOM_NUMBER));
		return argumentsToParse;
	}

	@Override
	public @NotNull String syntax() {
		return "/mt addmodifier [Modifier] {Level}";
	}
}

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Syntax of /mt removemodifier:
 * 		/mt removemodifier [Modifier]
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

					if (modManager.isToolViable(tool) || modManager.isArmorViable(tool))
						modManager.removeMod(tool, m);
					else
						CommandManager.sendError(sender, LanguageManager.getString("Commands.Failure.Cause.InvalidItem"));
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
			ItemStack item = ((Player) sender).getInventory().getItemInMainHand();
			if (args.length == 2) {
				for (Modifier mod : ModManager.instance().getAllowedMods()) {
					if (ModManager.instance().hasMod(item, mod))
						result.add(mod.getName().replaceAll(" ", "_"));
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
		return new HashMap<>();
	}

	@Override
	public @NotNull String syntax() {
		return "/mt removemodifier [Modifier]";
	}
}

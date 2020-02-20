package de.flo56958.MineTinker.Commands.subs;

import de.flo56958.MineTinker.Commands.ArgumentType;
import de.flo56958.MineTinker.Commands.CommandManager;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.LanguageManager;
import de.flo56958.MineTinker.api.SubCommand;
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
 * Syntax of /mt givemodifieritem:
 * 		/mt givemodifieritem {Player} [Modifier] {Amount}
 *
 * Legend:
 * 		{ }: not necessary
 * 		[ ]: necessary
 */
public class GiveModifierItemCommand implements SubCommand {
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull String[] args) {
		int playerIndex;
		int modifierIndex;
		int amountIndex;

		Modifier mod = null;
		Player p = null;
		int amount = 1;
		switch (args.length) {
			case 2:
				for (Modifier m : ModManager.instance().getAllowedMods()) {
					if (m.getName().replaceAll(" ", "_").equalsIgnoreCase(args[1])) {
						mod = m;
						if (sender instanceof Player) {
							p = (Player) sender;
						} else {
							CommandManager.sendError(sender, LanguageManager.getString("Commands.Failure.Cause.PlayerMissing"));
							return true;
						}
						break;
					}
				}
				if(mod == null) {
					CommandManager.sendError(sender, LanguageManager.getString("Commands.Failure.Cause.InvalidArguments"));
					return true;
				}
				break;
			case 3:
				p = Bukkit.getPlayer(args[1]);
				if (p == null) {
					try {
						p = Bukkit.getPlayer(UUID.fromString(args[1]));
					} catch (IllegalArgumentException ignored) {
					}
				}

				if (p == null) modifierIndex = 1;
				else modifierIndex = 2;

				for (Modifier m : ModManager.instance().getAllowedMods()) {
					if (m.getName().replaceAll(" ", "_").equalsIgnoreCase(args[modifierIndex])) {
						mod = m;
						if (sender instanceof Player) {
							p = (Player) sender;
						} else if (modifierIndex == 1){
							CommandManager.sendError(sender, LanguageManager.getString("Commands.Failure.Cause.PlayerMissing"));
							return true;
						}
						break;
					}
				}
				if (mod == null) {
					CommandManager.sendError(sender, LanguageManager.getString("Commands.Failure.Cause.InvalidArguments"));
					return true;
				}

				if (modifierIndex == 1) {
					amountIndex = 2;
					try {
						amount = Integer.parseInt(args[amountIndex]);
					} catch (NumberFormatException ignored) {
						CommandManager.sendError(sender, LanguageManager.getString("Commands.Failure.Cause.NumberFormatException"));
						return true;
					}
				}
				break;
			case 4:
				playerIndex = 1;
				modifierIndex = 2;
				amountIndex = 3;
				p = Bukkit.getPlayer(args[playerIndex]);
				if (p == null) {
					try {
						p = Bukkit.getPlayer(UUID.fromString(args[playerIndex]));
					} catch (IllegalArgumentException ignored) {
					}
				}
				if (p == null) {
					CommandManager.sendError(sender, LanguageManager.getString("Commands.Failure.Cause.PlayerMissing"));
					return true;
				}
				for (Modifier m : ModManager.instance().getAllowedMods()) {
					if (m.getName().replaceAll(" ", "_").equalsIgnoreCase(args[modifierIndex])) {
						mod = m;
						break;
					}
				}
				if (mod == null) {
					CommandManager.sendError(sender, LanguageManager.getString("Commands.Failure.Cause.InvalidArguments"));
					return true;
				}

				try {
					amount = Integer.parseInt(args[amountIndex]);
				} catch (NumberFormatException ignored) {
					CommandManager.sendError(sender, LanguageManager.getString("Commands.Failure.Cause.NumberFormatException"));
					return true;
				}
				break;
			default:
				CommandManager.sendError(sender, LanguageManager.getString("Commands.Failure.Cause.InvalidArguments"));
				return true;
		}
		ItemStack item = mod.getModItem().clone();
		item.setAmount(amount);
		if (p.getInventory().addItem(item).size() != 0) { //adds items to (full) inventory
			p.getWorld().dropItem(p.getLocation(), item);
		} // no else as it gets added in if
		return true;
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
		ArrayList<String> result = new ArrayList<>();
		switch (args.length) {
			case 2:
				for (Player player : Bukkit.getOnlinePlayers()) {
					result.add(player.getDisplayName());
				}
				result.add("@a");
				result.add("@r");

				if (sender instanceof Entity || sender instanceof BlockState) {
					result.add("@aw");
					result.add("@p");
					result.add("@rw");
				}
				break;
			case 3:
				for (Modifier m : ModManager.instance().getAllowedMods()) {
					result.add(m.getName().replaceAll(" ", "_"));
				}
				break;
			case 4:
				for (int i = 0; i < 10; i++) {
					result.add(String.valueOf(i));
				}
				break;
		}
		return result;
	}

	@Override
	public @NotNull String getName() {
		return "givemodifieritem";
	}

	@Override
	public @NotNull List<String> getAliases(boolean withName) {
		ArrayList<String> aliases = new ArrayList<>();
		if (withName) aliases.add(getName());
		aliases.add("gmi");
		aliases.add("gm");
		return aliases;
	}

	@Override
	public @NotNull String getPermission() {
		return "minetinker.commands.givemodifieritem";
	}

	@Override
	public @NotNull Map<Integer, List<ArgumentType>> getArgumentsToParse() {
		Map<Integer, List<ArgumentType>> argumentsToParse = new HashMap<>();
		argumentsToParse.put(1, Collections.singletonList(ArgumentType.PLAYER));
		argumentsToParse.put(2, Collections.singletonList(ArgumentType.RANDOM_NUMBER));
		argumentsToParse.put(3, Collections.singletonList(ArgumentType.RANDOM_NUMBER));
		return argumentsToParse;
	}

	@Override
	public @NotNull String syntax() {
		return "/mt givemodifieritem {Player} [Modifier] {Amount}";
	}
}

package de.flo56958.MineTinker.Commands.subs;

import de.flo56958.MineTinker.Commands.ArgumentType;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Modifier;
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
							//TODO: Send missing Player to give item to
							return true;
						}
						break;
					}
				}
				if(mod == null) {
					//TODO: Send expected Modifier in Index 1
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
							//TODO: Send missing Player to give item to
							return true;
						}
						break;
					}
				}
				if (mod == null) {
					//TODO: Send expected Modifier in Index 1
					return true;
				}

				if (modifierIndex == 1) {
					amountIndex = 2;
					try {
						amount = Integer.parseInt(args[amountIndex]);
					} catch (NumberFormatException ignored) {
						//TODO: Expected Amount in Index 2 got args[2]
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
					//TODO: Send expected Player in Index 1 got args[1]
					return true;
				}
				for (Modifier m : ModManager.instance().getAllowedMods()) {
					if (m.getName().replaceAll(" ", "_").equalsIgnoreCase(args[modifierIndex])) {
						mod = m;
						break;
					}
				}
				if (mod == null) {
					//TODO: Send expected Modifier in Index 2
					return true;
				}

				try {
					amount = Integer.parseInt(args[amountIndex]);
				} catch (NumberFormatException ignored) {
					//TODO: Expected Amount in Index 2 got args[2]
					return true;
				}
				break;
			default:
				//TODO: Send wrong command argument length args.length
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
				result.add("0");
				result.add("1");
				result.add("2");
				result.add("3");
				result.add("4");
				result.add("5");
				result.add("6");
				result.add("7");
				result.add("8");
				result.add("9");
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

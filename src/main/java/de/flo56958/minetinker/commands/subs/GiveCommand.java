package de.flo56958.minetinker.commands.subs;

import de.flo56958.minetinker.api.SubCommand;
import de.flo56958.minetinker.commands.ArgumentType;
import de.flo56958.minetinker.commands.CommandManager;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.utils.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Syntax of /mt give:
 * /mt give {Player} [Material]
 * <p>
 * Legend:
 * { }: not necessary
 * [ ]: necessary
 */
public class GiveCommand implements SubCommand {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull String[] args) {
		Material material = null;
		Player player = null;

		if (args.length == 2) {
			material = Material.getMaterial(args[1].toUpperCase());
			if (sender instanceof Player) {
				player = (Player) sender;
			} else {
				CommandManager.sendError(sender,
						LanguageManager.getString("Commands.Failure.Cause.InvalidArguments"));
				return true;
			}
		} else if (args.length > 2) {
			material = Material.getMaterial(args[2].toUpperCase());
			player = Bukkit.getPlayer(args[1]);
		}

		if (player == null) {
			CommandManager.sendError(sender, LanguageManager.getString("Commands.Failure.Cause.PlayerMissing"));
			return true;
		}

		if (material == null) {
			CommandManager.sendError(sender, LanguageManager.getString("Commands.Failure.Cause.InvalidArguments", player));
			return true;
		}

		final ItemStack tool = new ItemStack(material, 1);
		modManager.convertItemStack(tool, player);

		if (!player.getInventory().addItem(tool).isEmpty()) { //adds items to (full) inventory
			player.getWorld().dropItem(player.getLocation(), tool);
		} // no else as it gets added in if

		return true;
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
		List<String> result = new ArrayList<>();
		switch (args.length) {
			case 2:
				result.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
				result.add("@a");
				result.add("@r");
				if (sender instanceof Entity || sender instanceof BlockState) {
					result.add("@aw");
					result.add("@p");
					result.add("@rw");
				}
			case 3:
				// rewrite this
				result.addAll(ToolType.ALL.getToolMaterials().stream().map(Material::toString).toList());
		}
		return result;
	}


	@Override
	public @NotNull String getName() {
		return "give";
	}

	@Override
	public @NotNull List<String> getAliases(boolean withName) {
		final ArrayList<String> aliases = new ArrayList<>();
		if (withName) aliases.add(getName());
		aliases.add("g");
		return aliases;
	}

	@Override
	public @NotNull String getPermission() {
		return "minetinker.commands.give";
	}

	@Override
	public @NotNull Map<Integer, List<ArgumentType>> getArgumentsToParse() {
		final Map<Integer, List<ArgumentType>> argumentsToParse = new HashMap<>();
		argumentsToParse.put(1, Collections.singletonList(ArgumentType.PLAYER));
		return argumentsToParse;
	}

	@Override
	public @NotNull String syntax() {
		return "/mt give {Player} [Material]";
	}
}

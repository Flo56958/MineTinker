package de.flo56958.minetinker.commands.subs;

import de.flo56958.minetinker.api.SubCommand;
import de.flo56958.minetinker.commands.ArgumentType;
import de.flo56958.minetinker.commands.CommandManager;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.listeners.BuildersWandListener;
import de.flo56958.minetinker.utils.ConfigurationManager;
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
 * 		/mt give {Player} [Material]
 * <p>
 * Legend:
 * 		{ }: not necessary
 * 		[ ]: necessary
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
			if (ConfigurationManager.getConfig("BuildersWand.yml").getBoolean("enabled")) {
				String name = args[1].replaceAll("_", " ");
				for (final ItemStack stack : BuildersWandListener.getWands()) {
					if (stack.getItemMeta().getDisplayName().equalsIgnoreCase(name)) {
						if (!player.getInventory().addItem(stack.clone()).isEmpty()) { //adds items to (full) inventory
							player.getWorld().dropItem(player.getLocation(), stack.clone());
						} // no else as it gets added in if
						return true;
					}
				}
			}
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
			case 2 -> {
				for (final Player player : Bukkit.getOnlinePlayers()) {
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
			case 3 -> {
				for (final ToolType type : ToolType.values()) {
					for (final Material mat : type.getToolMaterials()) {
						result.add(mat.toString());
					}
				}
				if (ConfigurationManager.getConfig("BuildersWand.yml").getBoolean("enabled")) {
					for (final ItemStack wand : BuildersWandListener.getWands()) {
						result.add(wand.getItemMeta().getDisplayName().replaceAll(" ", "_"));
					}
				}
			}
		}
		return result;
	}


	@Override @NotNull
	public String getName() {
		return "give";
	}

	@Override @NotNull
	public List<String> getAliases(boolean withName) {
		final ArrayList<String> aliases = new ArrayList<>();
		if (withName) aliases.add(getName());
		aliases.add("g");
		return aliases;
	}

	@Override @NotNull
	public String getPermission() {
		return "minetinker.commands.give";
	}

	@Override @NotNull
	public Map<Integer, List<ArgumentType>> getArgumentsToParse() {
		final Map<Integer, List<ArgumentType>> argumentsToParse = new HashMap<>();
		argumentsToParse.put(1, Collections.singletonList(ArgumentType.PLAYER));
		return argumentsToParse;
	}

	@Override
	public @NotNull String syntax() {
		return "/mt give {Player} [Material]";
	}
}

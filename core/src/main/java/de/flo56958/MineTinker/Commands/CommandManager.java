package de.flo56958.MineTinker.Commands;

import de.flo56958.MineTinker.Commands.subs.*;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.api.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CommandManager implements TabExecutor {

	private HashMap<String, SubCommand> map = new HashMap<>();
	private ArrayList<String> cmds = new ArrayList<>();

	public CommandManager() {
		ArrayList<SubCommand> commands = new ArrayList<>();
		commands.add(new GiveCommand());
		commands.add(new ModifierListCommand());
		commands.add(new AddModifierCommand());
		commands.add(new CheckUpdateCommand());
		commands.add(new GiveModifierItemCommand());

		commands.forEach(this::registerSubcommand);

		cmds.sort(String::compareToIgnoreCase);
	}

	public void registerSubcommand(SubCommand sub) {
		cmds.add(sub.getName());
		for (String alias : sub.getAliases(true)) {
			if (map.putIfAbsent(alias, sub) != null) {
				ChatWriter.logError(""); //make error if duplicate subcommand alias
			}
		}
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s,
							 @NotNull String[] args) {
		if (!(sender.hasPermission("minetinker.commands.main"))) {
			//TODO: send no Perm
			return true;
		}

		if (args.length <= 0) {
			//TODO: send not enough or wrong Arguments
			return true;
		}

		SubCommand sub = map.get(args[0]);
		if (sub == null) {
			//TODO: send unknown command
			return true;
		}

		parseArguments(sender, sub, args);

		//@all / @allworld
		{
			int index = -1;
			boolean worldOnly = false;
			for (int i = 0; i < args.length; i++) {
				List<ArgumentType> types = sub.getArgumentsToParse().get(i);
				if (types != null && types.contains(ArgumentType.PLAYER)) {
					if (args[i].startsWith("@aw")) {
						index = i;
						worldOnly = true;
						break;
					} else if (args[i].startsWith("@a")) {
						index = i;
						break;
					}
				}
			}
			if (index != -1) {
				Collection<? extends Player> players;
				if (worldOnly) {
					World world = null;
					if (sender instanceof BlockCommandSender) {
						world = ((BlockCommandSender) sender).getBlock().getWorld();
					} else if (sender instanceof Entity) {
						world = ((Entity) sender).getWorld();
					}

					if (world == null) return true;
					players = world.getPlayers();
				} else {
					players = Bukkit.getOnlinePlayers();
				}

				boolean ret = true;
				for (Player player : players) {
					String[] arg = args.clone();
					arg[index] = player.getDisplayName();
					ret = ret && onCommand(sender, command, s, arg);
				}
			}
		}

		return sub.onCommand(sender, args);
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command,
												@NotNull String s, @NotNull String[] args) {
		if (args.length == 0) {
			return null;
		} else if (args.length == 1) {
			return cmds;
		} else {
			SubCommand sub = map.get(args[0]);
			if (sub == null) return null;
			if (commandSender.hasPermission(sub.getPermission())) {
				List<String> result = sub.onTabComplete(commandSender, args);
				if (result != null) {
					//filter out any command that is not the beginning of the typed command
					result.removeIf(str ->  !str.toLowerCase().startsWith(args[args.length - 1].toLowerCase()));
					result.sort(String::compareToIgnoreCase);
				}
				return result;
			}
		}
		return null;
	}

	private void parseArguments(CommandSender sender, SubCommand sub, String[] args) {
		for (int i = 1; i < args.length; i++) {
			List<ArgumentType> atypes = sub.getArgumentsToParse().get(i);
			if (atypes == null) continue;

			for (ArgumentType type : atypes) {
				switch (type) {
					case COLORED_TEXT:
						break;
					case PLAYER:
						if (args[i].startsWith("@p")) {
							if (sender instanceof Player) {
								args[i] = ((Player) sender).getDisplayName();
							} else if (sender instanceof BlockCommandSender) {
								List<Player> players = ((BlockCommandSender) sender).getBlock()
																					.getWorld().getPlayers();
								double distance = Double.POSITIVE_INFINITY;

								for (Player player : players) {
									double newDist = player.getLocation().distance(((BlockCommandSender) sender)
																					.getBlock().getLocation());
									if (newDist < distance) {
										distance = newDist;
										args[i] = player.getDisplayName();
									}
								}
							}
						} else if (args[i].startsWith("@rw")) { //random Player in World
							World world = null;
							if (sender instanceof BlockCommandSender) {
								world = ((BlockCommandSender) sender).getBlock().getWorld();
							} else if (sender instanceof Entity) {
								world = ((Entity) sender).getWorld();
							}

							if (world == null) continue;

							List<Player> players = world.getPlayers();
							args[i] = players.get(new Random().nextInt(players.size())).getDisplayName();
						} else if (args[i].startsWith("@r")) {
							Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
							args[i] = onlinePlayers.toArray(new Player[0])
									[new Random().nextInt(onlinePlayers.size())].getDisplayName();
						} else {
							try {
								UUID uuid = UUID.fromString(args[i]);
								Player player = Bukkit.getPlayer(uuid);
								if (player != null) args[i] = player.getDisplayName();
								else {
									//TODO: Wrong Player UUID / Name or not Online
								}
							} catch (IllegalArgumentException ignored) {
							}
						}
						break;
					case RANDOM_NUMBER:
						//like range 1-5 (inclusive), or from 1,2,3,6,7,8, or all of them combined like 1-5,7-9,11,13
						String[] rules = args[i].split(",");
						int index = new Random().nextInt(rules.length);
						if (rules[index].indexOf('-') != 0) {
							String[] nums = rules[index].split("-");
							if (nums.length != 2) {
								//TODO: Return wrong random number format
							}
							try{
								int min = Integer.parseInt(nums[0]);
								int max = Integer.parseInt(nums[1]);
								int rand = new Random().nextInt(max - min) + min;
								args[i] = String.valueOf(rand);
							} catch (NumberFormatException e) {
								//TODO: Return wrong random number format
							}
						} else {
							args[i] = rules[index];
						}
						break;
					default:
				}
			}
		}
	}
}

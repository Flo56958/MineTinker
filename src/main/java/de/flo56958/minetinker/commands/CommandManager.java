package de.flo56958.minetinker.commands;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.api.SubCommand;
import de.flo56958.minetinker.commands.subs.*;
import de.flo56958.minetinker.modifiers.ModManager;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CommandManager implements TabExecutor {

	private final HashMap<String, SubCommand> map = new HashMap<>();
	private final ArrayList<String> cmds = new ArrayList<>();

	public CommandManager() {
		ArrayList<SubCommand> commands = new ArrayList<>();
		commands.add(new GiveCommand());
		commands.add(new ModifierListCommand());
		commands.add(new AddModifierCommand());
		commands.add(new CheckUpdateCommand());
		commands.add(new GiveModifierItemCommand());
		commands.add(new ConvertCommand());
		commands.add(new AddExpCommand());
		commands.add(new InfoCommand());
		commands.add(new ReloadCommand());
		commands.add(new NameCommand());
		commands.add(new RemoveModifierCommand());
		commands.add(new EditConfigurationCommand());
		commands.add(new ItemStatisticsCommand());

		commands.forEach(this::registerSubcommand);
		commands.forEach(c -> {
			if (c instanceof Listener)
				Bukkit.getPluginManager().registerEvents((Listener) c, MineTinker.getPlugin());
		});

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
			sendError(sender, LanguageManager.getInstance().getString("Commands.Failure.Cause.NoPermission"));
			return true;
		}

		if (args.length <= 0) {
			sendError(sender, LanguageManager.getInstance().getString("Commands.Failure.Cause.InvalidArguments"));
			sendHelp(sender, null);
			return true;
		}

		if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
			sendHelp(sender, args.length >= 2 ? map.get(args[1].toLowerCase()) : null);
			return true;
		}

		SubCommand sub = map.get(args[0].toLowerCase());
		if (sub == null) {
			sendError(sender, LanguageManager.getInstance().getString("Commands.Failure.Cause.UnknownCommand"));
			sendHelp(sender, null);
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
					arg[index] = player.getName();
					ret = ret && onCommand(sender, command, s, arg);
				}
			}
		}

		if (!sender.hasPermission(sub.getPermission())) {
			sendError(sender, LanguageManager.getInstance().getString("Commands.Failure.Cause.NoPermission"));
			return true;
		}

		return sub.onCommand(sender, args);
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command,
												@NotNull String s, @NotNull String[] args) {
		List<String> result = new ArrayList<>();
		if (args.length == 0) {
			return result;
		} else if (args.length == 1) {
			result.addAll(cmds);
			ArrayList<String> toRemove = new ArrayList<>();
			for (String st : result) {
				SubCommand sub = map.get(st);
				if (sub != null) {
					if (!commandSender.hasPermission(sub.getPermission())) {
						toRemove.add(st);
					}
				} else
					toRemove.add(st);
			}
			result.removeAll(toRemove);
		} else {
			SubCommand sub = map.get(args[0].toLowerCase());
			if (sub == null) {
				if (args.length == 2 && args[0].equalsIgnoreCase("help")) { //help auto-complete
					result.addAll(cmds);
					ArrayList<String> toRemove = new ArrayList<>();
					for (String st : result) {
						sub = map.get(st);
						if (sub != null) {
							if (!commandSender.hasPermission(sub.getPermission())) {
								toRemove.add(st);
							}
						} else
							toRemove.add(st);
					}
					result.removeAll(toRemove);
				}
				return result;
			}
			if (commandSender.hasPermission(sub.getPermission())) {
				result = sub.onTabComplete(commandSender, args);
			}
		}
		if (result != null) {
			if (args.length < 2) result.add("help");
			//filter out any command that is not the beginning of the typed command
			List<String> tempResult = new ArrayList<>();
			for (String str : result) {
				tempResult.add(ChatColor.stripColor(str));
			}
			result = tempResult;
			result.removeIf(str -> !str.toLowerCase().startsWith(args[args.length - 1].toLowerCase()));
			result.sort(String::compareToIgnoreCase);
		}
		return result;
	}

	private void parseArguments(@NotNull CommandSender sender, @NotNull SubCommand sub, String[] args) {
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
								args[i] = sender.getName();
							} else if (sender instanceof BlockCommandSender) {
								List<Player> players = ((BlockCommandSender) sender).getBlock()
																					.getWorld().getPlayers();
								double distance = Double.POSITIVE_INFINITY;

								for (Player player : players) {
									double newDist = player.getLocation().distance(((BlockCommandSender) sender)
																					.getBlock().getLocation());
									if (newDist < distance) {
										distance = newDist;
										args[i] = player.getName();
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
							args[i] = players.get(new Random().nextInt(players.size())).getName();
						} else if (args[i].startsWith("@r")) {
							Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
							args[i] = onlinePlayers.toArray(new Player[0])
									[new Random().nextInt(onlinePlayers.size())].getName();
						} else {
							try {
								UUID uuid = UUID.fromString(args[i]);
								Player player = Bukkit.getPlayer(uuid);
								if (player != null) args[i] = player.getName();
								else {
									sendError(sender, LanguageManager.getInstance().getString("Commands.Failure.Cause.PlayerNotFound")
											.replace("%p", args[i]));
								}
							} catch (IllegalArgumentException ignored) {
							}
						}
						break;
					case RANDOM_NUMBER:
						//like range 1-5 (inclusive), or from 1,2,3,6,7,8, or all of them combined like 1-5,7-9,11,13
						String[] rules = args[i].split(",");
						int index = new Random().nextInt(rules.length);
						boolean isMod = false;
						for (Modifier mod : ModManager.getInstance().getAllMods()) {
							if (mod.getName().replace(" ", "_").equals(rules[index])) {
								isMod = true;
								break;
							}
						}
						if (!isMod && rules[index].indexOf('-') != -1) {
							String[] nums = rules[index].split("-");
							if (nums.length != 2) {
								sendError(sender,
										LanguageManager.getInstance().getString("Commands.Failure.Cause.NumberFormatException"));
								break;
							}
							try{
								int min = Integer.parseInt(nums[0]);
								int max = Integer.parseInt(nums[1]);
								int rand = new Random().nextInt(max - min) + min;
								args[i] = String.valueOf(rand);
							} catch (NumberFormatException e) {
								sendError(sender,
										LanguageManager.getInstance().getString("Commands.Failure.Cause.NumberFormatException"));
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

	public static void sendError(CommandSender sender, String cause) {
		ChatWriter.sendMessage(sender, ChatColor.RED, LanguageManager.getInstance().getString("Commands.Failure.Main")
				.replaceAll("%cause", cause));
	}

	private void sendHelp(CommandSender sender, @Nullable SubCommand command) {
		if (command == null) {
			int index = 1;
			for (String cmd : cmds) {
				SubCommand sub = map.get(cmd);
				if (sender.hasPermission(sub.getPermission()))
				ChatWriter.sendMessage(sender, ChatColor.WHITE, index++ + ". " + cmd + " "
						+ sub.getAliases(false).toString() + ": " + sub.syntax());
			}
			return;
		}

		if (sender.hasPermission(command.getPermission()))
			ChatWriter.sendMessage(sender, ChatColor.WHITE, command.syntax());
	}
}

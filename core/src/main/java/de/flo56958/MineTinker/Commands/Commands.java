package de.flo56958.MineTinker.Commands;

import de.flo56958.MineTinker.Data.GUIs;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Commands {

	private static final ModManager modManager = ModManager.instance();
	/**
	 * all commands
	 */
	private static final String[] cmds = {"addexp", "addmod", "checkupdate",
			"convert", "editconfig", "give", "givemodifieritem", "help", "info", "itemstatistics", "modifiers",
			"name", "reload", "removemod", "setdurability"};
	/**
	 * all console commands
	 */
	private static final String[] cmds_console = {"checkupdate", "info", "modifiers", "reload", "givemodifieritem"};
	private static ArrayList<String> numbers = new ArrayList<>();

	static {
		for (int i = 0; i < 10; i++) {
			numbers.add(Integer.toString(i));
		}
	}

	/**
	 * Outputs the error message "Invalid Arguments" in the Players chat
	 *
	 * @param player The player to send the message to
	 */
	static void invalidArgs(Player player) {
		ChatWriter.sendMessage(player, ChatColor.RED, LanguageManager.getString("Commands.InvalidArguments", player));
	}

	/**
	 * Outputs the error message "Invalid Arguments" to the CommandSender
	 *
	 * @param sender The sender to send the message to
	 */
	static void invalidArgs(CommandSender sender) {
		ChatWriter.sendMessage(sender, ChatColor.RED, LanguageManager.getString("Commands.InvalidArguments"));
	}

	/**
	 * Outputs the error message "Invalid Tool/Armor" in the Players chat
	 *
	 * @param player The player to send the message to
	 */
	static void invalidTool(Player player) {
		ChatWriter.sendMessage(player, ChatColor.RED, LanguageManager.getString("Commands.InvalidTool", player));
	}

	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
		//TODO: Consider HashMap for Commands and Functions
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (sender.hasPermission("minetinker.commands.main")) {
				if (args.length > 0) {
					switch (args[0].toLowerCase()) { //first argument is the specifier for the command
						case "itemstatistics":
						case "is":
							if (player.hasPermission("minetinker.commands.itemstatistics")) {
								Functions.itemStatistics(player);
							} else noPerm(player);
							break;
						case "name":
						case "n":
							if (player.hasPermission("minetinker.commands.name")) {
								Functions.name(player, args);
							} else noPerm(player);
							break;
						case "removemod":
						case "rm":
							if (player.hasPermission("minetinker.commands.removemod")) {
								Functions.removeMod(player, args);
							} else noPerm(player);
							break;
						case "setdurability":
						case "sd":
							if (player.hasPermission("minetinker.commands.setdurability")) {
								Functions.setDurability(player, args);
							} else noPerm(player);
							break;
						case "ec":
						case "editconfig":
							if (player.hasPermission("minetinker.commands.editconfig")) {
								GUIs.getConfigurationsGUI().show(player);
							} else noPerm(player);
							break;
						default:
							invalidArgs(player);
							ChatWriter.sendMessage(player, ChatColor.WHITE, "Possible arguments are:");
					}
				}
			}
		}
		return true;
	}

	/**
	 * Outputs the error message "No Permissions" in the Players chat
	 *
	 * @param player The player to send the message to
	 */
	private void noPerm(Player player) {
		ChatWriter.sendMessage(player, ChatColor.RED, LanguageManager.getString("Commands.NoPermission", player));
	}
}

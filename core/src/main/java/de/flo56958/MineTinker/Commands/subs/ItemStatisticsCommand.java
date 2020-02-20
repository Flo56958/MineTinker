package de.flo56958.MineTinker.Commands.subs;

import de.flo56958.MineTinker.Commands.ArgumentType;
import de.flo56958.MineTinker.Commands.CommandManager;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import de.flo56958.MineTinker.Utilities.LanguageManager;
import de.flo56958.MineTinker.Utilities.nms.NBTUtils;
import de.flo56958.MineTinker.api.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.BlockState;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ItemStatisticsCommand implements SubCommand {
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull String[] args) {
		Player player;
		if (args.length < 2) {
			if (!(sender instanceof Player)) {
				CommandManager.sendError(sender, LanguageManager.getString("Commands.Failure.Cause.PlayerMissing"));
				return true;
			}
			player = (Player) sender;
		} else {
			player = Bukkit.getPlayer(args[1]);
		}
		if (player == null) {
			CommandManager.sendError(sender, LanguageManager.getString("Commands.Failure.Cause.PlayerNotFound").replace("%p", args[1]));
			return true;
		}
		ModManager modManager = ModManager.instance();
		for (ItemStack stack : player.getInventory().getContents()) {
			if (!modManager.isToolViable(stack) && !modManager.isArmorViable(stack)) continue;

			ChatWriter.sendMessage(sender, ChatColor.WHITE, LanguageManager.getString("Commands.ItemStatistics.Head")
					.replaceFirst("%toolname", ItemGenerator.getDisplayName(stack) + ChatColor.WHITE + " (" + stack.getType().toString() + ")"));
			ChatWriter.sendMessage(sender, ChatColor.WHITE, LanguageManager.getString("Commands.ItemStatistics.Level")
					.replaceFirst("%level", "" + modManager.getLevel(stack)));
			ChatWriter.sendMessage(sender, ChatColor.WHITE, LanguageManager.getString("Commands.ItemStatistics.Exp")
					.replaceFirst("%current", "" + modManager.getExp(stack))
					.replaceFirst("%nextlevel", "" + modManager.getNextLevelReq(modManager.getLevel(stack))));
			ChatWriter.sendMessage(sender, ChatColor.WHITE, LanguageManager.getString("Commands.ItemStatistics.FreeSlots")
					.replaceFirst("%slots", "" + modManager.getFreeSlots(stack)));
			ChatWriter.sendMessage(sender, ChatColor.WHITE, LanguageManager.getString("Commands.ItemStatistics.Modifiers"));

			for (Modifier mod : modManager.getAllowedMods()) {
				if (NBTUtils.getHandler().hasTag(stack, mod.getKey())) {
					ChatWriter.sendMessage(sender, ChatColor.WHITE, mod.getColor() + mod.getName() + ChatColor.WHITE + " " + NBTUtils.getHandler().getInt(stack, mod.getKey()));
				}
			}
		}
		return true;
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
		List<String> result = new ArrayList<>();
		if (args.length == 2) {
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
		}
		return result;
	}

	@Override
	public @NotNull String getName() {
		return "itemstatistics";
	}

	@Override
	public @NotNull List<String> getAliases(boolean withName) {
		ArrayList<String> aliases = new ArrayList<>();
		if (withName) aliases.add(getName());
		aliases.add("is");
		aliases.add("itemstats");
		return aliases;
	}

	@Override
	public @NotNull String getPermission() {
		return "minetinker.commands.itemstatistics";
	}

	@Override
	public @NotNull Map<Integer, List<ArgumentType>> getArgumentsToParse() {
		Map<Integer, List<ArgumentType>> argumentsToParse = new HashMap<>();
		argumentsToParse.put(1, Collections.singletonList(ArgumentType.PLAYER));
		return argumentsToParse;	}

	@Override
	public @NotNull String syntax() {
		return "/mt itemstatistics";
	}
}

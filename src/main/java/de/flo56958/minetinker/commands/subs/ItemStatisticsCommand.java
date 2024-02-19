package de.flo56958.minetinker.commands.subs;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.api.SubCommand;
import de.flo56958.minetinker.api.gui.ButtonAction;
import de.flo56958.minetinker.api.gui.GUI;
import de.flo56958.minetinker.commands.ArgumentType;
import de.flo56958.minetinker.commands.CommandManager;
import de.flo56958.minetinker.data.GUIs;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.ItemStatisticsHandler;
import de.flo56958.minetinker.utils.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.BlockState;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
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
			CommandManager.sendError(sender, LanguageManager.getString("Commands.Failure.Cause.PlayerNotFound")
					.replace("%p", args[1]));
			return true;
		}

		if (!player.equals(sender) && !sender.hasPermission(this.getPermission() + "other")) {
			CommandManager.sendError(sender, LanguageManager.getString("Commands.Failure.Cause.NoPermission"));
			return true;
		}

		List<ItemStack> items = new ArrayList<>();
		for (ItemStack stack : player.getInventory().getContents()) {
			if (!modManager.isToolViable(stack) && !modManager.isArmorViable(stack)) continue;
			items.add(stack);
		}
		items.sort(Comparator.comparing(modManager::getExp)); //Sorting

		if (sender instanceof Player && MineTinker.getPlugin().getConfig().getBoolean("EnableLore", true)) { //GUI instead of Wall of Text through chat
			int amount = items.size();
			GUI gui = new GUI(MineTinker.getPlugin());
			Bukkit.getScheduler().runTaskLater(MineTinker.getPlugin(), gui::close, 5 * 60 * 20);
			GUI.Window window = gui.addWindow(Math.min(Math.max((int) Math.ceil(amount / 9.0), 1), 6), player.getDisplayName());
			for (int i = 0; i < amount; i++) {
				GUI.Window.Button button = window.addButton(i, items.get(i));
				GUI statisticGUI = ItemStatisticsHandler.getGUI(items.get(i));
				button.addAction(ClickType.LEFT, new ButtonAction.PAGE_GOTO(button, Objects.requireNonNull(statisticGUI.getWindow(0))));
				GUI.Window.Button backButton = statisticGUI.getWindow(0).addButton(8, GUIs.backStack);
				backButton.addAction(ClickType.LEFT, new ButtonAction.PAGE_GOTO(backButton, window));
			}
			gui.show((Player) sender);
		} else {
			for (final ItemStack stack : items) {
				ChatWriter.sendMessage(sender, ChatColor.WHITE, LanguageManager.getString("Commands.ItemStatistics.Head")
						.replaceFirst("%toolname", ChatWriter.getDisplayName(stack) + ChatColor.WHITE
								+ " (" + stack.getType() + ")"));
				ChatWriter.sendMessage(sender, ChatColor.WHITE, LanguageManager.getString("Commands.ItemStatistics.Level")
						.replaceFirst("%level", String.valueOf(modManager.getLevel(stack))));
				ChatWriter.sendMessage(sender, ChatColor.WHITE, LanguageManager.getString("Commands.ItemStatistics.Exp")
						.replaceFirst("%current", String.valueOf(modManager.getExp(stack)))
						.replaceFirst("%nextlevel", String.valueOf(modManager.getNextLevelReq(modManager.getLevel(stack)))));
				ChatWriter.sendMessage(sender, ChatColor.WHITE, LanguageManager.getString("Commands.ItemStatistics.FreeSlots")
						.replaceFirst("%slots", String.valueOf(modManager.getFreeSlots(stack))));
				ChatWriter.sendMessage(sender, ChatColor.WHITE, LanguageManager.getString("Commands.ItemStatistics.Modifiers"));

				for (final Modifier mod : modManager.getAllowedMods()) {
					if (modManager.hasMod(stack, mod)) {
						ChatWriter.sendMessage(sender, ChatColor.WHITE, mod.getColor() + mod.getName() + ChatColor.WHITE
								+ " " + modManager.getModLevel(stack, mod));
					}
				}
			}
		}
		return true;
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
		List<String> result = new ArrayList<>();
		if (args.length == 2 && sender.hasPermission(this.getPermission() + "other")) {
			for (Player player : Bukkit.getOnlinePlayers()) {
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

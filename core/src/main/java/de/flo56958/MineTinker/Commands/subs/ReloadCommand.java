package de.flo56958.MineTinker.Commands.subs;

import de.flo56958.MineTinker.Commands.ArgumentType;
import de.flo56958.MineTinker.Data.GUIs;
import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Listeners.BuildersWandListener;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import de.flo56958.MineTinker.Utilities.LanguageManager;
import de.flo56958.MineTinker.Utilities.Updater;
import de.flo56958.MineTinker.Utilities.nms.NBTUtils;
import de.flo56958.MineTinker.api.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ReloadCommand implements SubCommand {
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull String[] args) {
		Player player = null;

		if (sender instanceof Player) {
			player = (Player) sender;
		}

		ChatWriter.sendMessage(sender, ChatColor.RED,
				LanguageManager.getString("Commands.Reload.Note1", player));
		ChatWriter.sendMessage(sender, ChatColor.WHITE,
				LanguageManager.getString("Commands.Reload.Recipes", player));

		Iterator<Recipe> it = NBTUtils.getHandler().getRecipeIterator();
		//TODO: Find a different way to remove recipes! Bukkit is bugged atm

		while (it.hasNext()) {
			ItemStack result = it.next().getResult();

			if (result.getType() != Material.EXPERIENCE_BOTTLE && result.getType() != Material.NETHER_STAR) {
				//Modifieritems
				if (ModManager.instance().isModifierItem(result)) {
					it.remove();
					continue;
				}

				//Builderswands
				if (ModManager.instance().isWandViable(result)) {
					it.remove();
				}
			}
		}

		ModManager.instance().recipe_Namespaces.clear();

		ChatWriter.sendMessage(sender, ChatColor.WHITE,
				LanguageManager.getString("Commands.Reload.Configs", player));
		Main.getPlugin().reloadConfig();
		ChatWriter.reload();
		ConfigurationManager.reload();
		Lists.reload();

		LanguageManager.reload();

		ChatWriter.sendMessage(sender, ChatColor.WHITE,
				LanguageManager.getString("Commands.Reload.ModManager", player));
		ModManager.instance().reload();

		ChatWriter.sendMessage(sender, ChatColor.WHITE,
				LanguageManager.getString("Commands.Reload.Builderswands", player));
		BuildersWandListener.reload();

		ChatWriter.sendMessage(sender, ChatColor.WHITE,
				LanguageManager.getString("Commands.Reload.GUIs", player));
		GUIs.reload();

		ChatWriter.sendMessage(sender, ChatColor.WHITE,
				LanguageManager.getString("Commands.Reload.Finish", player));

		if (Main.getPlugin().getConfig().getBoolean("CheckForUpdates")) {
			Bukkit.getScheduler().scheduleAsyncDelayedTask(Main.getPlugin(), Updater::checkForUpdate, 20);
		}
		return true;
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
		return null;
	}

	@Override
	public @NotNull String getName() {
		return "reload";
	}

	@Override
	public @NotNull List<String> getAliases(boolean withName) {
		ArrayList<String> aliases = new ArrayList<>();
		if (withName) aliases.add(getName());
		aliases.add("r");
		return aliases;
	}

	@Override
	public @NotNull String getPermission() {
		return "minetinker.commands.reload";
	}

	@Override
	public @NotNull Map<Integer, List<ArgumentType>> getArgumentsToParse() {
		return new HashMap<>();
	}

	@Override
	public @NotNull String syntax() {
		return "/mt reload";
	}
}

package de.flo56958.minetinker.commands.subs;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.api.SubCommand;
import de.flo56958.minetinker.api.gui.ButtonAction;
import de.flo56958.minetinker.api.gui.GUI;
import de.flo56958.minetinker.commands.ArgumentType;
import de.flo56958.minetinker.data.GUIs;
import de.flo56958.minetinker.data.contributor.Contributor;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.LanguageManager;
import de.flo56958.minetinker.utils.Updater;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class InfoCommand implements SubCommand {

	private GUI infoGUI;
	public InfoCommand() {
		infoGUI = new GUI(MineTinker.getPlugin());
		Bukkit.getScheduler().runTaskAsynchronously(MineTinker.getPlugin(), () -> {
			GUI.Window window = infoGUI.addWindow(1, "MineTinker-Info");
			ItemStack stack = new ItemStack(Material.DIAMOND_PICKAXE);
			ItemMeta meta = stack.getItemMeta();
			meta.setDisplayName(MineTinker.getPlugin().getDescription().getFullName());
			ArrayList<String> lore = new ArrayList<>();
			Updater.checkOnline();
			lore.add(ChatColor.GOLD + "Latest Version: " + ((Updater.hasUpdate()) ? ChatColor.RED : ChatColor.WHITE) + Updater.getOnlineVersion());
			//Obtain Information over GitHub API
			try {
				String cons = new Scanner(new URL("https://api.github.com/repos/Flo56958/MineTinker").openStream(), "UTF-8").useDelimiter("\\A").next();
				JsonObject json = new JsonParser().parse(cons).getAsJsonObject();
				lore.add(ChatColor.GOLD + "Repository Owner: " + ChatColor.WHITE + json.get("owner").getAsJsonObject().get("login").getAsString());
				lore.add(ChatColor.GOLD + "Forks: " + ChatColor.WHITE + json.get("forks_count").getAsInt());
				lore.add(ChatColor.GOLD + "Stars: " + ChatColor.WHITE + json.get("stargazers_count").getAsInt());
				lore.add(ChatColor.GOLD + "Watchers: " + ChatColor.WHITE + json.get("subscribers_count").getAsInt());
				lore.add(ChatColor.GOLD + "Open Issues: " + ChatColor.WHITE + json.get("open_issues_count").getAsInt());
				lore.add(ChatColor.GOLD + "License: " + ChatColor.WHITE + json.get("license").getAsJsonObject().get("name").getAsString());
			} catch (IOException e) {
				lore.add(ChatColor.RED + "Can not retrieve Informations from Github!");
			}
			meta.setLore(lore);
			stack.setItemMeta(meta);
			window.addButton(3, stack);
			stack = new ItemStack(Material.PLAYER_HEAD);
			meta = stack.getItemMeta();
			if (meta instanceof SkullMeta) {
				((SkullMeta) meta).setOwner("Flo56958");
				meta.setDisplayName("Contributors");
				meta.setLore(Collections.singletonList(ChatColor.WHITE + "Click here to see all contributors"));
			}
			stack.setItemMeta(meta);

			GUI contributorGUI = new GUI(MineTinker.getPlugin());
			{
				int pageNo = 1;
				GUI.Window contributors = contributorGUI.addWindow(6, "Contributors #" + pageNo);
				int i = 0;
				ArrayList<Contributor> conlist = Contributor.getContributors();
				conlist.sort(Comparator.comparing(Contributor::getCommits));
				for (Contributor c : conlist) {
					contributors.addButton(i++, c.getPlayerHead());
					if (i == 45) {
						i = 0;
						pageNo++;
						GUIs.addNavigationButtons(contributors);
						contributors = contributorGUI.addWindow(6, "Contributors #" + pageNo);
						GUIs.addNavigationButtons(contributors);
					}
				}
			}

			GUI.Window.Button button = window.addButton(5, stack);
			button.addAction(ClickType.LEFT, new ButtonAction.PAGE_GOTO(button, contributorGUI.getWindow(0)));
		});
	}
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull String[] args) {
		Player player = null;
		if(sender instanceof Player) {
			player = (Player) sender;
			infoGUI.show(player);
		} else {
			ChatWriter.sendMessage(sender, ChatColor.WHITE, LanguageManager.getString("Commands.Info.Line1", player)
					.replaceFirst("%ver", MineTinker.getPlugin().getDescription().getVersion()));
			ChatWriter.sendMessage(sender, ChatColor.WHITE, LanguageManager.getString("Commands.Info.Line2", player));
		}
		return true;
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
		return null;
	}

	@Override
	public @NotNull String getName() {
		return "info";
	}

	@Override
	public @NotNull List<String> getAliases(boolean withName) {
		ArrayList<String> aliases = new ArrayList<>();
		if (withName) aliases.add(getName());
		aliases.add("i");
		return aliases;
	}

	@Override
	public @NotNull String getPermission() {
		return "minetinker.commands.info";
	}

	@Override
	public @NotNull Map<Integer, List<ArgumentType>> getArgumentsToParse() {
		return new HashMap<>();
	}

	@Override
	public @NotNull String syntax() {
		return "/mt info";
	}
}

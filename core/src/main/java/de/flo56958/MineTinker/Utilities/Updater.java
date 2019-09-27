package de.flo56958.MineTinker.Utilities;

import de.flo56958.MineTinker.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class Updater {

	private static final String version = Main.getPlugin().getDescription().getVersion();
	private static String onlineVersion;

	public static String getOnlineVersion() {
		return onlineVersion;
	}

	public static synchronized boolean hasUpdate() {
		return onlineVersion != null && !onlineVersion.equals(version);
	}

	/**
	 * tries to get the newest MineTinker-Version number from api.spigotmc.org
	 */
	private static void checkOnline() {
		if (hasUpdate()) {
			return;
		}

		try {
			URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + 58940);
			URLConnection connection = url.openConnection();
			Scanner scan = new Scanner(connection.getInputStream());

			if (scan.hasNextLine()) {
				onlineVersion = scan.nextLine();
			}

			scan.close();
		} catch (Exception ignored) {
		}
	}

	/**
	 * Compares the online version number with the plugin version (initiated by a players command)
	 *
	 * @param sender That gets the information printed in his chat
	 */
	public static void checkForUpdate(CommandSender sender) {
		checkOnline();
		Player p = null;
		if (sender instanceof Player) {
			p = (Player) sender;
		}

		if (onlineVersion == null) {
			ChatWriter.sendMessage(sender, ChatColor.RED, LanguageManager.getString("Updater.Unable", p));
		} else if (!version.equals(onlineVersion)) {
			ChatWriter.sendMessage(sender, ChatColor.WHITE, LanguageManager.getString("Updater.UpdateAvailable", p));
			ChatWriter.sendMessage(sender, ChatColor.WHITE, LanguageManager.getString("Updater.YourVersion", p).replaceFirst("%ver", version));
			ChatWriter.sendMessage(sender, ChatColor.WHITE, LanguageManager.getString("Updater.OnlineVersion", p).replaceFirst("%ver", onlineVersion));
		} else {
			ChatWriter.sendMessage(sender, ChatColor.WHITE, LanguageManager.getString("Updater.UpToDate", p));
		}
	}

	public static void checkForUpdate() {
		checkForUpdate(Bukkit.getConsoleSender());
	}
}

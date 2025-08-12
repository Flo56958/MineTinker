package de.flo56958.minetinker.utils;

import de.flo56958.minetinker.MineTinker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;

import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Updater {

	private static final String version = MineTinker.getPlugin().getDescription().getVersion();
	private static String onlineVersion;

	@Contract(pure = true)
	public static String getOnlineVersion() {
		return onlineVersion;
	}

	public static synchronized boolean hasUpdate() {
		if (onlineVersion == null) return false;

		if (version.contains("-")) return false;

		final String[] ver = version.split("\\.");
		final String[] onl = onlineVersion.split("\\.");

		try {
			for (int i = 0; i < 3; i++) {
				int v = -1;
				if (ver.length > i) v = Integer.parseInt(ver[i]);

				int o = -1;
				if (onl.length > i) o = Integer.parseInt(onl[i]);

				if (v == -1 && o != -1) return true;
				if (v != -1 && o != -1 && v < o) return true;
				if (v != -1 && o != -1 && v > o) return false;
			}
		} catch (NumberFormatException ignored) {}

		return false;
	}

	/**
	 * tries to get the newest MineTinker-Version number from api.spigotmc.org
	 */
	public static void checkOnline() {
		if (hasUpdate()) return;

		try {
			final URL url = new URI("https://api.spigotmc.org/legacy/update.php?resource=" + 58940).toURL();
			final URLConnection connection = url.openConnection();
			final Scanner scan = new Scanner(connection.getInputStream(), StandardCharsets.UTF_8);

			if (scan.hasNextLine())
				onlineVersion = scan.nextLine();

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

		Player player = null;
		if (sender instanceof Player p) {
			player = p;
		}

		if (onlineVersion == null) {
			ChatWriter.sendMessage(sender, ChatColor.RED, LanguageManager.getString("Updater.Unable", player));
		} else if (hasUpdate()) {
			ChatWriter.sendMessage(sender, ChatColor.WHITE, LanguageManager.getString("Updater.UpdateAvailable", player));
			ChatWriter.sendMessage(sender, ChatColor.WHITE, LanguageManager.getString("Updater.YourVersion", player).replaceFirst("%ver", version));
			ChatWriter.sendMessage(sender, ChatColor.WHITE, LanguageManager.getString("Updater.OnlineVersion", player).replaceFirst("%ver", onlineVersion));
		} else {
			ChatWriter.sendMessage(sender, ChatColor.WHITE, LanguageManager.getString("Updater.UpToDate", player));
		}
	}

	public static void checkForUpdate() {
		checkForUpdate(Bukkit.getConsoleSender());
	}
}

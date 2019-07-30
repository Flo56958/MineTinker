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

    private static String onlineVersion;
    private static final String version = Main.getPlugin().getDescription().getVersion();

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
        if (hasUpdate()) return;
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
     * @param sender That gets the information printed in his chat
     */
    public static void checkForUpdate(CommandSender sender) {
        checkOnline();
        if (sender instanceof Player) {
            if (onlineVersion == null) {
                ChatWriter.sendMessage(sender, ChatColor.RED, LanguageManager.getString("Updater.Unable", (Player) sender));
            } else if (!version.equals(onlineVersion)) {
                ChatWriter.sendMessage(sender, ChatColor.WHITE, LanguageManager.getString("Updater.UpdateAvailable", (Player) sender));
                ChatWriter.sendMessage(sender, ChatColor.WHITE, LanguageManager.getString("Updater.YourVersion", (Player) sender).replaceFirst("%ver", version));
                ChatWriter.sendMessage(sender, ChatColor.WHITE, LanguageManager.getString("Updater.OnlineVersion", (Player) sender).replaceFirst("%ver", onlineVersion));
            } else {
                ChatWriter.sendMessage(sender, ChatColor.WHITE, LanguageManager.getString("Updater.UpToDate", (Player) sender));
            }
            return;
        }

        if (onlineVersion == null) {
            ChatWriter.sendMessage(sender, ChatColor.RED, LanguageManager.getString("Updater.Unable"));
        } else if (!version.equals(onlineVersion)) {
            ChatWriter.sendMessage(sender, ChatColor.WHITE, LanguageManager.getString("Updater.UpdateAvailable"));
            ChatWriter.sendMessage(sender, ChatColor.WHITE, LanguageManager.getString("Updater.YourVersion").replaceFirst("%ver", version));
            ChatWriter.sendMessage(sender, ChatColor.WHITE, LanguageManager.getString("Updater.OnlineVersion").replaceFirst("%ver", onlineVersion));
        } else {
            ChatWriter.sendMessage(sender, ChatColor.WHITE, LanguageManager.getString("Updater.UpToDate"));
        }
    }

    public static void checkForUpdate() {
        checkForUpdate(Bukkit.getConsoleSender());
    }
}

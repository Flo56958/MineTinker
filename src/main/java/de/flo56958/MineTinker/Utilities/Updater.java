package de.flo56958.MineTinker.Utilities;

import de.flo56958.MineTinker.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class Updater {

    private static String onlineVersion;
    private static final String version = Main.getPlugin().getDescription().getVersion();

    public static String getOnlineVersion() { return onlineVersion; }

    public static boolean hasUpdate() { return onlineVersion != null && !onlineVersion.equals(version); }

    /**
     * tries to get the newest MineTinker-Version number from api.spigotmc.org
     */
    private static synchronized void checkOnline() {
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
     * Compares the online version number with the plugin version
     */
    public static void checkForUpdate() {
        checkOnline();
        if (onlineVersion == null) {
            ChatWriter.logInfo("MineTinker is unable to check for updates.");
        } else if (!version.equals(onlineVersion)) {
            ChatWriter.logInfo("There is an update available on spigotmc.org!");
            ChatWriter.logInfo("Your version: " + version);
            ChatWriter.logInfo("Online Version: " + onlineVersion);
        } else {
            ChatWriter.log(false, "You have the newest version of MineTinker installed!");
        }
    }

    /**
     * Compares the online version number with the plugin version (initiated by a players command)
     * @param sender That gets the information printed in his chat
     */
    public static void checkForUpdate(CommandSender sender) {
        checkOnline();
        if (onlineVersion == null) {
            ChatWriter.sendMessage(sender, ChatColor.RED, "Unable to check for updates!");
        } else if (!version.equals(onlineVersion)) {
            ChatWriter.sendMessage(sender, ChatColor.WHITE, "There is an update available on spigotmc.org!");
            ChatWriter.sendMessage(sender, ChatColor.WHITE, "Your version: " + version);
            ChatWriter.sendMessage(sender, ChatColor.WHITE, "Online Version: " + onlineVersion);
        } else {
            ChatWriter.sendMessage(sender, ChatColor.WHITE, "You have the newest version of MineTinker installed!");
        }
    }
}

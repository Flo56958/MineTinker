package de.flo56958.MineTinker.Utilities;

import de.flo56958.MineTinker.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Updater {

    private String onlineVersion = "";
    private String version = Main.getPlugin().getDescription().getVersion();
    private boolean hasUpdate = false;

    public String getOnlineVersion() { return onlineVersion; }

    public boolean getHasUpdate() { return this.hasUpdate; }

    /**
     * tries to get the newest MineTinker-Version number from spigotmc.org
     * @return the online version number OR ""
     */
    private String checkOnline() {
        String oVersion = "";

        try {
            URL url = new URL("https://www.spigotmc.org/resources/minetinker.58940/");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            StringBuilder content;

            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {

                String line;
                content = new StringBuilder();

                while ((line = in.readLine()) != null) {
                    content.append(line);
                    content.append(System.lineSeparator());
                }
            }

            String s = content.toString();
            con.disconnect();

            String search = " <span class=\"muted\">";
            int start = s.indexOf(search) + search.length();
            s = s.substring(start);

            oVersion = s.split("<")[0];
        } catch (Exception e) {
            return "";
        }
        return oVersion;
    }

    /**
     * Compares the online version number with the plugin version
     */
    public void checkForUpdate() {
        if (!this.hasUpdate) {
            this.onlineVersion = this.checkOnline();
        }
        if (this.onlineVersion.equals("")) {
            ChatWriter.logInfo("MineTinker is unable to check for updates.");
            this.hasUpdate = false;
        } else if (!this.version.equals(this.onlineVersion)) {
            ChatWriter.logInfo("There is an update available on spigotmc.org!");
            ChatWriter.logInfo("Your version: " + this.version);
            ChatWriter.logInfo("Online Version: " + this.onlineVersion);
            this.hasUpdate = true;
        } else {
            ChatWriter.log(false, "You have the newest version of MineTinker installed!");
            this.hasUpdate = false;
        }
    }

    /**
     * Compares the online version number with the plugin version (initiated by a players command)
     * @param sender That gets the information printed in his chat
     */
    public void checkForUpdate(CommandSender sender) {
        if (!this.hasUpdate) {
            this.onlineVersion = this.checkOnline();
        }
        if (this.onlineVersion.equals("")) {
            ChatWriter.sendMessage(sender, ChatColor.RED, "Unable to check for updates!");
            this.hasUpdate = false;
        } else if (!this.version.equals(this.onlineVersion)) {
            ChatWriter.sendMessage(sender, ChatColor.WHITE, "There is an update available on spigotmc.org!");
            ChatWriter.sendMessage(sender, ChatColor.WHITE, "Your version: " + this.version);
            ChatWriter.sendMessage(sender, ChatColor.WHITE, "Online Version: " + this.onlineVersion);
            this.hasUpdate = true;
        } else {
            ChatWriter.log(false, "You have the newest version of MineTinker installed!");
            this.hasUpdate = false;
        }
    }
}

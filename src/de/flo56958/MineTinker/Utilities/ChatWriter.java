package de.flo56958.MineTinker.Utilities;

import de.flo56958.MineTinker.Data.Strings;
import de.flo56958.MineTinker.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ChatWriter {

    public static void sendMessage(CommandSender receiver, ChatColor color, String message) {
        receiver.sendMessage(Strings.CHAT_PREFIX + " " + color + message);
    }

    public static void log(boolean debug, String message) {
        if (debug) {
            if (Main.getPlugin().getConfig().getBoolean("logging.debug")) {
                Bukkit.getConsoleSender().sendMessage(Strings.CHAT_PREFIX + " " + ChatColor.RED + message);
            }
        } else {
            if (Main.getPlugin().getConfig().getBoolean("logging.standard")) {
                Bukkit.getConsoleSender().sendMessage(Strings.CHAT_PREFIX + " " + message);
            }
        }
    }
}

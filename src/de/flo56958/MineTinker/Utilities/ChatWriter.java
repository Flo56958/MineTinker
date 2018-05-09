package de.flo56958.MineTinker.Utilities;

import de.flo56958.MineTinker.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ChatWriter {

    public static void sendMessage(CommandSender receiver, ChatColor color, String message) {
        receiver.sendMessage(Main.getPlugin().getConfig().getString("chat-prefix") + " " + color + message);
    }
}

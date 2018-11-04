package de.flo56958.MineTinker.Utilities;

import de.flo56958.MineTinker.Data.Strings;
import de.flo56958.MineTinker.Main;
import net.minecraft.server.v1_13_R2.ChatComponentText;
import net.minecraft.server.v1_13_R2.ChatMessageType;
import net.minecraft.server.v1_13_R2.PacketPlayOutChat;
import net.minecraft.server.v1_13_R2.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ChatWriter {

    public static void sendMessage(CommandSender receiver, ChatColor color, String message) {
        if (Main.getPlugin().getConfig().getBoolean("chat-messages")) {
            receiver.sendMessage(Strings.CHAT_PREFIX + " " + color + message);
        }
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

    public static void sendActionBar(Player p, String message) { //Extract from the source code of the Actionbar-API (altered)
        if (!Main.getPlugin().getConfig().getBoolean("actionbar-messages")) { return; }
        if (!p.isOnline()) { return; } // Player may have logged out
        CraftPlayer cp = (CraftPlayer) p;
        ChatComponentText ccT = new ChatComponentText(message);
        PacketPlayOutChat ppOC = new PacketPlayOutChat(ccT, ChatMessageType.GAME_INFO);
        PlayerConnection pC = cp.getHandle().playerConnection;
        pC.sendPacket(ppOC);
    }

    public static void sendActionBar(Player p, String message, int duration) { //Extract from the source code of the Actionbar-API (altered)
        sendActionBar(p, message);

        if (duration >= 0) {
            // Sends empty message at the end of the duration. Allows messages shorter than 3 seconds, ensures precision.
            new BukkitRunnable() {
                @Override
                public void run() {
                    sendActionBar(p, "");
                }
            }.runTaskLater(Main.getPlugin(), duration + 1);
        }

        // Re-sends the messages every 3 seconds so it doesn't go away from the player's screen.
        while (duration > 40) {
            duration -= 40;
            new BukkitRunnable() {
                @Override
                public void run() {
                    sendActionBar(p, message);
                }
            }.runTaskLater(Main.getPlugin(), (long) duration);
        }
    }
}

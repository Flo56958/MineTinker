package de.flo56958.MineTinker.Utilities;

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

import java.util.logging.Level;

public class ChatWriter {

    public static final String CHAT_PREFIX = Main.getPlugin().getConfig().getString("chat-prefix");

    /**
     * Sends a chat message
     * @param receiver
     * @param color The ChatColor after the CHAT_PREFIX
     * @param message
     */
    public static void sendMessage(CommandSender receiver, ChatColor color, String message) {
        if (Main.getPlugin().getConfig().getBoolean("chat-messages")) {
            receiver.sendMessage(CHAT_PREFIX + " " + color + message);
        }
    }

    /**
     * Logs specific information on MineTinker-Activities (toggleable through config)
     * @param debug Is the information a (unnecessary) debug information?
     * @param message
     */
    public static void log(boolean debug, String message) {
        if (debug) {
            if (Main.getPlugin().getConfig().getBoolean("logging.debug")) {
                Bukkit.getConsoleSender().sendMessage(CHAT_PREFIX + " " + ChatColor.RED + message);
            }
        } else {
            if (Main.getPlugin().getConfig().getBoolean("logging.standard")) {
                Bukkit.getConsoleSender().sendMessage(CHAT_PREFIX + " " + message);
            }
        }
    }

    /**
     * Logs severe errors. (not toggleable)
     * @param message
     */
    public static void logError(String message) { Bukkit.getLogger().log(Level.SEVERE, CHAT_PREFIX + " "+ message); }

    /**
     * Logs information. (not toggleable)
     * @param message
     */
    public static void logInfo(String message) { Bukkit.getLogger().log(Level.INFO, CHAT_PREFIX + " " + message); }

    /**
     * Logs information with the ability to have text color (not toggleable)
     * @param message
     */
    public static void logColor(String message) { Bukkit.getConsoleSender().sendMessage(CHAT_PREFIX + " " + message); }

    /**
     * Sends a message to the players actionbar
     * @param player
     * @param message
     */
    public static void sendActionBar(Player player, String message) { //Extract from the source code of the Actionbar-API (altered)
        if (!Main.getPlugin().getConfig().getBoolean("actionbar-messages")) { return; }
        if (!player.isOnline()) { return; } // Player may have logged out
        CraftPlayer cp = (CraftPlayer) player;
        ChatComponentText ccT = new ChatComponentText(message);
        PacketPlayOutChat ppOC = new PacketPlayOutChat(ccT, ChatMessageType.GAME_INFO);
        PlayerConnection pC = cp.getHandle().playerConnection;
        pC.sendPacket(ppOC);
    }

    /**
     * Send a message to the players actionbar over a specific period of time
     * @param player
     * @param message
     * @param duration in ticks
     */
    public static void sendActionBar(Player player, String message, int duration) { //Extract from the source code of the Actionbar-API (altered)
        sendActionBar(player, message);

        if (duration >= 0) {
            // Sends empty message at the end of the duration. Allows messages shorter than 3 seconds, ensures precision.
            new BukkitRunnable() {
                @Override
                public void run() {
                    sendActionBar(player, "");
                }
            }.runTaskLater(Main.getPlugin(), duration + 1);
        }

        // Re-sends the messages every 3 seconds so it doesn't go away from the player's screen.
        while (duration > 40) {
            duration -= 40;
            new BukkitRunnable() {
                @Override
                public void run() {
                    sendActionBar(player, message);
                }
            }.runTaskLater(Main.getPlugin(), (long) duration);
        }
    }
}

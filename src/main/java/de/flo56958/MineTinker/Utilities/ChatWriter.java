package de.flo56958.MineTinker.Utilities;

import de.flo56958.MineTinker.Main;
import net.minecraft.server.v1_14_R1.ChatComponentText;
import net.minecraft.server.v1_14_R1.ChatMessageType;
import net.minecraft.server.v1_14_R1.PacketPlayOutChat;
import net.minecraft.server.v1_14_R1.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Level;

public class ChatWriter {

    public static final String CHAT_PREFIX;
    
    static {
    	CHAT_PREFIX = Main.getPlugin().getConfig().getString("chat-prefix");
    }

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
        if (!Main.getPlugin().getConfig().getBoolean("actionbar-messages")) return;
        if (!player.isOnline()) return; // Player may have logged out
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

    public static String addColors(String input) {
        input = input.replaceAll("%BLACK%", ChatColor.BLACK.toString());
        input = input.replaceAll("%DARK_BLUE%", ChatColor.DARK_BLUE.toString());
        input = input.replaceAll("%DARK_GREEN%", ChatColor.DARK_GREEN.toString());
        input = input.replaceAll("%DARK_AQUA%", ChatColor.DARK_AQUA.toString());
        input = input.replaceAll("%DARK_RED%", ChatColor.DARK_RED.toString());
        input = input.replaceAll("%DARK_PURPLE%", ChatColor.DARK_PURPLE.toString());
        input = input.replaceAll("%GOLD%", ChatColor.GOLD.toString());
        input = input.replaceAll("%GRAY%", ChatColor.GRAY.toString());
        input = input.replaceAll("%DARK_GRAY%", ChatColor.DARK_GRAY.toString());
        input = input.replaceAll("%BLUE%", ChatColor.BLUE.toString());
        input = input.replaceAll("%GREEN%", ChatColor.GREEN.toString());
        input = input.replaceAll("%AQUA%", ChatColor.AQUA.toString());
        input = input.replaceAll("%RED%", ChatColor.RED.toString());
        input = input.replaceAll("%LIGHT_PURPLE%", ChatColor.LIGHT_PURPLE.toString());
        input = input.replaceAll("%YELLOW%", ChatColor.YELLOW.toString());
        input = input.replaceAll("%WHITE%", ChatColor.WHITE.toString());
        input = input.replaceAll("%BOLD%", ChatColor.BOLD.toString());
        input = input.replaceAll("%UNDERLINE%", ChatColor.UNDERLINE.toString());
        input = input.replaceAll("%ITALIC%", ChatColor.ITALIC.toString());
        input = input.replaceAll("%STRIKE%", ChatColor.STRIKETHROUGH.toString());
        input = input.replaceAll("%MAGIC%", ChatColor.MAGIC.toString());
        input = input.replaceAll("%RESET%", ChatColor.RESET.toString());

        return input;
    }

    public static ChatColor getColor(String input) {
        switch (input) {
            case "%BLACK%":
                return ChatColor.BLACK;
            case "%DARK_BLUE%":
                return ChatColor.DARK_BLUE;
            case "%DARK_GREEN%":
                return ChatColor.DARK_GREEN;
            case "%DARK_AQUA%":
                return ChatColor.DARK_AQUA;
            case "%DARK_RED%":
                return ChatColor.DARK_RED;
            case "%DARK_PURPLE%":
                return ChatColor.DARK_PURPLE;
            case "%GOLD%":
                return ChatColor.GOLD;
            case "%GRAY%":
                return ChatColor.GRAY;
            case "%DARK_GRAY%":
                return ChatColor.DARK_GRAY;
            case "%BLUE%":
                return ChatColor.BLUE;
            case "%GREEN%":
                return ChatColor.GREEN;
            case "%AQUA%":
                return ChatColor.AQUA;
            case "%RED%":
                return ChatColor.RED;
            case "%LIGHT_PURPLE%":
                return ChatColor.LIGHT_PURPLE;
            case "%YELLOW%":
                return ChatColor.YELLOW;
            case "%BOLD%":
                return ChatColor.BOLD;
            case "%UNDERLINE%":
                return ChatColor.UNDERLINE;
            case "%ITALIC%":
                return ChatColor.ITALIC;
            case "%STRIKE%":
                return ChatColor.STRIKETHROUGH;
            case "%MAGIC%":
                return ChatColor.MAGIC;
            case "%RESET%":
                return ChatColor.RESET;
            default:
                return ChatColor.WHITE;
        }
    }

    public static String toRomanNumerals(int num) {
        if (num == 1337) return "LEET";
        String[] romanCharacters = { "%BOLD%M%RESET%", "%BOLD%CM%RESET%", "%BOLD%D%RESET%", "%BOLD%C%RESET%", "%BOLD%XC%RESET%", "%BOLD%L%RESET%", "%BOLD%X%RESET%", "%BOLD%IX%RESET%", "%BOLD%V%RESET%",
                                    "M", "CM", "D", "C", "XC", "L", "X", "IX", "V", "I" };
        int[] romanValues = { 1000000, 900000, 500000, 100000, 90000, 50000, 10000, 9000, 5000,
                                    1000, 900, 500, 100, 90, 50, 10, 9, 5, 1 };
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < romanValues.length; i++) {
            int numberInPlace = num / romanValues[i];
            if (numberInPlace == 0) continue;
            result.append(numberInPlace == 4 && i > 0 ? romanCharacters[i] + romanCharacters[i - 1] :
                    new String(new char[numberInPlace]).replace("\0", romanCharacters[i]));
            num = num % romanValues[i];
        }
        return addColors(result.toString());
    }
}

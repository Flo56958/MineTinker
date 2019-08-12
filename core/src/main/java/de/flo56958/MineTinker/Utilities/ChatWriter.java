package de.flo56958.MineTinker.Utilities;

import de.flo56958.MineTinker.Main;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;

public class ChatWriter {

    public static String CHAT_PREFIX;

    private final static TreeMap<Integer, String> map = new TreeMap<>();

    static {
        map.put(1000000, "%BOLD%%UNDERLINE%M%RESET%");
        map.put(500000, "%BOLD%%UNDERLINE%D%RESET%");
        map.put(100000, "%BOLD%%UNDERLINE%C%RESET%");
        map.put(50000, "%BOLD%%UNDERLINE%L%RESET%");
        map.put(10000, "%BOLD%%UNDERLINE%X%RESET%");
    	map.put(5000, "%BOLD%%UNDERLINE%V%RESET%");
        map.put(1000, "M");
        map.put(900, "CM");
        map.put(500, "D");
        map.put(400, "CD");
        map.put(100, "C");
        map.put(90, "XC");
        map.put(50, "L");
        map.put(40, "XL");
        map.put(10, "X");
        map.put(9, "IX");
        map.put(5, "V");
        map.put(4, "IV");
        map.put(1, "I");
    }

    public static void reload() {
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
        if (!Main.getPlugin().getConfig().getBoolean("actionbar-messages")) {
            return;
        }

        if (!player.isOnline()) {
            return; // Player may have logged out, unlikely but possible?
        }

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
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
        return ChatColor.valueOf(input.split("%")[1]);
    }

    public static String toRomanNumerals(int number) {
        if (number == 1337) {
            return "LEET";
        }

        if (number <= 0) {
            return "0";
        }

        int floorKey =  map.floorKey(number);

        if (number == floorKey) {
            return map.get(number);
        }

        return map.get(floorKey) + toRomanNumerals(number-floorKey);
    }

    public static List<String> splitString(String msg, int lineSize) {
        List<String> res = new ArrayList<>();

        /*
        Pattern pattern = Pattern.compile("\\b.+" + (lineSize-1) + "}\\b\\W?");
        Matcher matcher = pattern.matcher(msg);

        while(matcher.find()) {
            res.add(matcher.group());
        }
        */

        String[] str = msg.split(" ");
        int index = 0;
        while(index < str.length) {
            StringBuilder line = new StringBuilder();
            do {
                index++;
                line.append(str[index - 1]);
                line.append(" ");
            } while(index < str.length && line.length() + str[index].length() < lineSize);
            res.add(line.toString().substring(0, line.length() - 1));
        }

        return res;
    }
}

package de.flo56958.minetinker.utils;

import com.google.common.base.CaseFormat;
import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.events.*;
import de.flo56958.minetinker.modifiers.ModManager;
import de.flo56958.minetinker.modifiers.Modifier;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;

public class ChatWriter {

	private final static TreeMap<Integer, String> map = new TreeMap<>();
	public static String CHAT_PREFIX;

	static {
		CHAT_PREFIX = MineTinker.getPlugin().getConfig().getString("chat-prefix");
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
		CHAT_PREFIX = MineTinker.getPlugin().getConfig().getString("chat-prefix");
	}

	/**
	 * Sends a chat message
	 *
	 * @param receiver
	 * @param color    The ChatColor after the CHAT_PREFIX
	 * @param message
	 */
	public static void sendMessage(final CommandSender receiver, final ChatColor color, final String message) {
		if (MineTinker.getPlugin().getConfig().getBoolean("chat-messages")) {
			receiver.sendMessage(CHAT_PREFIX + " " + color + message);
		}
	}

	/**
	 * Logs specific information on MineTinker-Activities (toggleable through config)
	 *
	 * @param debug   Is the information a (unnecessary) debug information?
	 * @param message
	 */
	public static void log(final boolean debug, final String message) {
		if (debug) {
			if (MineTinker.getPlugin().getConfig().getBoolean("logging.debug")) {
				Bukkit.getConsoleSender().sendMessage(CHAT_PREFIX + " " + ChatColor.RED + message);
			}
		} else {
			if (MineTinker.getPlugin().getConfig().getBoolean("logging.standard") || MineTinker.getPlugin().getConfig().getBoolean("logging.debug")) {
				Bukkit.getConsoleSender().sendMessage(CHAT_PREFIX + " " + message);
			}
		}
	}

	/**
	 * Logs severe errors. (not toggleable)
	 *
	 * @param message
	 */
	public static void logError(final String message) {
		Bukkit.getLogger().log(Level.SEVERE, CHAT_PREFIX + " " + message);
	}

	/**
	 * Logs information. (not toggleable)
	 *
	 * @param message
	 */
	public static void logInfo(final String message) {
		Bukkit.getLogger().log(Level.INFO, CHAT_PREFIX + " " + message);
	}

	/**
	 * Logs information with the ability to have text color (not toggleable)
	 *
	 * @param message
	 */
	public static void logColor(final String message) {
		Bukkit.getConsoleSender().sendMessage(CHAT_PREFIX + " " + message);
	}

	public static void logModifier(final @NotNull Player p, final @Nullable Event event, final @NotNull Modifier mod, final @NotNull ItemStack tool, String... args) {
		if (!(MineTinker.getPlugin().getConfig().getBoolean("logging.modifiers"))) return;

		//Example: 0x00FF: Flo56958/Melting - DIAMOND_CHESTPLATE - Damage(30 -> 20)
		final StringBuilder sb = new StringBuilder();
		if (event != null) {
			sb.append(event.getEventName()).append("(").append(String.format("%x", event.hashCode() % 0x100));
			if (event instanceof MTBlockBreakEvent) {
				sb.append("/").append(String.format("%x", ((MTBlockBreakEvent) event).getEvent().hashCode() % 0x100));
			} else if (event instanceof MTEntityDamageByEntityEvent) {
				sb.append("/").append(String.format("%x", ((MTEntityDamageByEntityEvent) event).getEvent().hashCode() % 0x100));
			} else if (event instanceof MTEntityDamageEvent) {
				sb.append("/").append(String.format("%x", ((MTEntityDamageEvent) event).getEvent().hashCode() % 0x100));
			} else if (event instanceof MTEntityDeathEvent) {
				sb.append("/").append(String.format("%x", ((MTEntityDeathEvent) event).getEvent().hashCode() % 0x100));
			} else if (event instanceof MTPlayerInteractEvent) {
				sb.append("/").append(String.format("%x", ((MTPlayerInteractEvent) event).getEvent().hashCode() % 0x100));
			} else if (event instanceof MTProjectileHitEvent) {
				sb.append("/").append(String.format("%x", ((MTProjectileHitEvent) event).getEvent().hashCode() % 0x100));
			}
			sb.append(")").append(": ");
		} else {
			sb.append("No event: ");
		}
		sb.append(p.getName()).append("/").append(mod.getKey()).append("(")
				.append(ModManager.instance().getModLevel(tool, mod)).append(")").append(" - ").append(tool.getType());
		Arrays.sort(args);
		for (final String s : args) {
			sb.append(" - ").append(s);
		}

		Bukkit.getLogger().log(Level.INFO, sb.toString());
	}

	/**
	 * Sends a message to the players actionbar
	 *
	 * @param player
	 * @param message
	 */
	public static void sendActionBar(final Player player, final String message) {
		//Extract from the source code of the Actionbar-API (altered)
		if (!MineTinker.getPlugin().getConfig().getBoolean("actionbar-messages")) {
			return;
		}

		if (player == null || !player.isOnline()) {
			return; // Player may have logged out, unlikely but possible?
		}

		try {
			player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
					TextComponent.fromLegacyText(message));
		} catch (NoSuchMethodError e) {
			ChatWriter.logError("You have Spigot features enabled but don't use Spigot." +
					"Please turn off actionbar-messages in the main config.");
		}
	}

	@NotNull
	public static String addColors(final @NotNull String input) {
		return input.replaceAll("%BLACK%", ChatColor.BLACK.toString())
			.replaceAll("%DARK_BLUE%", ChatColor.DARK_BLUE.toString())
			.replaceAll("%DARK_GREEN%", ChatColor.DARK_GREEN.toString())
			.replaceAll("%DARK_AQUA%", ChatColor.DARK_AQUA.toString())
			.replaceAll("%DARK_RED%", ChatColor.DARK_RED.toString())
			.replaceAll("%DARK_PURPLE%", ChatColor.DARK_PURPLE.toString())
			.replaceAll("%GOLD%", ChatColor.GOLD.toString())
			.replaceAll("%GRAY%", ChatColor.GRAY.toString())
			.replaceAll("%DARK_GRAY%", ChatColor.DARK_GRAY.toString())
			.replaceAll("%BLUE%", ChatColor.BLUE.toString())
			.replaceAll("%GREEN%", ChatColor.GREEN.toString())
			.replaceAll("%AQUA%", ChatColor.AQUA.toString())
			.replaceAll("%RED%", ChatColor.RED.toString())
			.replaceAll("%LIGHT_PURPLE%", ChatColor.LIGHT_PURPLE.toString())
			.replaceAll("%YELLOW%", ChatColor.YELLOW.toString())
			.replaceAll("%WHITE%", ChatColor.WHITE.toString())
			.replaceAll("%BOLD%", ChatColor.BOLD.toString())
			.replaceAll("%UNDERLINE%", ChatColor.UNDERLINE.toString())
			.replaceAll("%ITALIC%", ChatColor.ITALIC.toString())
			.replaceAll("%STRIKE%", ChatColor.STRIKETHROUGH.toString())
			.replaceAll("%MAGIC%", ChatColor.MAGIC.toString())
			.replaceAll("%RESET%", ChatColor.RESET.toString());
	}

	public static ChatColor getColor(final @NotNull String input)
			throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
		return ChatColor.valueOf(input.split("%")[1]);
	}

	public static String toRomanNumerals(final int number) {
		if (number == 1337) {
			return "LEET";
		}

		if (number == 0) {
			return "0"; //Roman Numbers do not have a zero (need to switch to arabic numerals)
		} else if (number < 0) {
			return "-" + toRomanNumerals(Math.abs(number)); //So negative numbers get shown correctly and are not 0
		}

		int floorKey = map.floorKey(number);

		if (number == floorKey) {
			return map.get(number);
		}

		return map.get(floorKey) + toRomanNumerals(number - floorKey);
	}

	@NotNull
	@Contract("null, _ -> new")
	public static List<String> splitString(@Nullable final String msg, final int lineSize) {
		if (msg == null) return new ArrayList<>();
		final List<String> res = new ArrayList<>();

		final String[] str = msg.split(" ");
		int index = 0;
		while (index < str.length) {
			final StringBuilder line = new StringBuilder();
			do {
				index++;
				line.append(str[index - 1]);
				line.append(" ");
			} while (index < str.length && line.length() + str[index].length() < lineSize);
			res.add(ChatColor.WHITE + line.substring(0, line.length() - 1));
		}

		return res;
	}

	public static String getDisplayName(final @NotNull ItemStack tool) {
		if (tool.getItemMeta() == null || !tool.getItemMeta().hasDisplayName()) {
			String type = tool.getType().toString();
			type = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, type);
			// https://stackoverflow.com/a/2560017
			type = type.replaceAll(String.format("%s|%s|%s",
					"(?<=[A-Z])(?=[A-Z][a-z])",
					"(?<=[^A-Z])(?=[A-Z])",
					"(?<=[A-Za-z])(?=[^A-Za-z])"
					),
					" ");
			return type;
		} else {
			return tool.getItemMeta().getDisplayName();
		}
	}
}

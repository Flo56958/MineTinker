package de.flo56958.MineTinker.Utilities;

import de.flo56958.MineTinker.Main;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class LanguageManager {

	private static YamlConfiguration langFile;
	private static YamlConfiguration langBackup;

	private static boolean usingFallback = false;
	private static long completenessPercent = 10_000;

	private static boolean playerLocale;

	private LanguageManager() {} //only to make it impossible to instantiate an object

	public static void reload() {
		String lang = Main.getPlugin().getConfig().getString("Language", "en_US");

		langFile = loadLanguage(lang);
		langBackup = loadLanguage("en_US");

		playerLocale = Main.getPlugin().getConfig().getBoolean("EnablePlayerLocale", false);

		if (langFile == null) {
			langFile = langBackup;
			usingFallback = true;
			ChatWriter.logError(lang + " is currently not supported. If you want MineTinker to support this language you can help translating on Transifex!");
		} else {
			if (!lang.equals("en_US") && !lang.equals("de_DE")) {
				ChatWriter.logInfo("You are using a community translation. Therefore the translation is not 100% reviewed and checked! Use with caution!");
			}

			double percentage = langFile.getKeys(true).size() /  (double) langBackup.getKeys(true).size();
			if (percentage < 1.0) {
				completenessPercent = Math.round(percentage * 10_000);
				ChatWriter.logColor(ChatColor.RED + "The translation you are using is only "
						+ completenessPercent / 100 + "." + completenessPercent % 100
						+ "% complete. The missing strings will be loaded from the Language 'en_US'!");
			}
			ChatWriter.logInfo(getString("LanguageManager.LoadedLanguage").replaceFirst("%lang", lang));
		}
	}

	public static void cleanup() {
		langFile = null;
		langBackup = null;
	}

	/**
	 * @param path the Path to the Strings location
	 * @return  "" on failure (empty String)
	 * 			the requested String on success
	 */
	@NotNull
	public static String getString(@NotNull String path) {
		String ret = langFile.getString(path);
		if (ret == null) {
			ret = langBackup.getString(path, "");
		}
		return ChatWriter.addColors(ret);
	}

	/**
	 * This function has the same effect as getString(String path) if the Player in null.
	 * @param path the Path to the Strings location
	 * @return  "" on failure (empty String)
	 * 			the requested String on success
	 */
	@NotNull
	public static String getString(@NotNull String path, @Nullable Player player) {
		if (player == null) return getString(path);
		if (playerLocale && !player.getLocale().equals(Main.getPlugin().getConfig().getString("Language"))) {
			YamlConfiguration langFile = loadLanguage(player.getLocale());
			if (langFile != null) {
				String ret = langFile.getString(path);
				if (ret != null) {
					return ChatWriter.addColors(ret);
				}
			}
		}
		return getString(path);
	}

	@Nullable
	private static YamlConfiguration loadLanguage(@NotNull String lang) {
		InputStream stream = LanguageManager.class.getResourceAsStream("/lang/" + lang + ".yml");
		if (stream == null) return null;
		InputStreamReader ir = new InputStreamReader(stream, StandardCharsets.UTF_8);

		YamlConfiguration file = YamlConfiguration.loadConfiguration(ir);
		try {
			ir.close();
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}

	@Contract(pure = true)
	public static boolean isUsingFallback() {
		return usingFallback;
	}

	@Contract(pure = true)
	public static boolean isComplete() { return completenessPercent == 10_000; }

	/**
	 *  @return the completeness of the used language file in percent * 100. So range is [0, 10000]
	 */
	@Contract(pure = true)
	public static Long getCompleteness() {
		return completenessPercent;
	}
}

package de.flo56958.MineTinker.Utilities;

import de.flo56958.MineTinker.Main;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LanguageManager {

    private static YamlConfiguration langFile;
    private static YamlConfiguration langBackup;

    private LanguageManager() {}

    public static void reload() {
        String lang = Main.getPlugin().getConfig().getString("Language", "en_US");

        langFile = loadLanguage(lang);
        langBackup = loadLanguage("en_US");

        if(langFile == null) langFile = langBackup;
        else ChatWriter.logInfo(getString("LanguageManager.LoadedLanguage").replaceFirst("%lang", lang));
    }

    public static void cleanup() {
        langFile = null;
        langBackup = null;
    }

    @NotNull
    public static String getString(@NotNull String path) {
        String ret = langFile.getString(path);
        if (ret == null) {
            ret = langBackup.getString(path, "");
        }
        return ChatWriter.addColors(ret);
    }

    @NotNull
    public static String getString (@NotNull String path, Player player) {
        if (player == null) return getString(path);
        if (!player.getLocale().equals(Main.getPlugin().getConfig().getString("Language"))) { //TODO: Make config option to turn this off
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
        InputStreamReader ir = new InputStreamReader(stream);

        YamlConfiguration file = YamlConfiguration.loadConfiguration(ir);
        try {
            ir.close();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}

package de.flo56958.MineTinker.Data;

import de.flo56958.MineTinker.Main;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class Strings {

    //TODO: DELETE MODIFIER STRINGS

    private static final FileConfiguration config = Main.getPlugin().getConfig();

    public static final String CHAT_PREFIX = config.getString("chat-prefix");

    public static final String IDENTIFIER = ChatColor.WHITE + config.getString("Language.Identifier");
    public static final String IDENTIFIER_BUILDERSWAND = ChatColor.WHITE + config.getString("Language.Identifier_Builderswand");

    public static final String LEVELLINE = ChatColor.GOLD + config.getString("Language.LevelLine") + ":" + ChatColor.WHITE + " ";
    public static final String EXPLINE = ChatColor.GOLD + config.getString("Language.ExpLine") + ":" + ChatColor.WHITE + " ";
    public static final String FREEMODIFIERSLOTS = ChatColor.WHITE + config.getString("Language.FreeModifierSlotsLine") + ": ";
    public static final String MODIFIERSTART = ChatColor.WHITE + config.getString("Language.ModifiersLine") + ":";
}

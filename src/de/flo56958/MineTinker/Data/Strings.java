package de.flo56958.MineTinker.Data;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import de.flo56958.MineTinker.Main;

public class Strings {

    private static final FileConfiguration config = Main.getPlugin().getConfig();

    public static final String CHAT_PREFIX = config.getString("chat-prefix");

    public static final String IDENTIFIER = ChatColor.WHITE + config.getString("Language.Identifier");
    public static final String IDENTIFIER_BUILDERSWAND = ChatColor.WHITE + config.getString("Language.Identifier_Builderswand");

    public static final String LEVELLINE = ChatColor.GOLD + config.getString("Language.LevelLine") + ":" + ChatColor.WHITE + " ";
    public static final String EXPLINE = ChatColor.GOLD + config.getString("Language.ExpLine") + ":" + ChatColor.WHITE + " ";
    public static final String FREEMODIFIERSLOTS = ChatColor.WHITE + config.getString("Language.FreeModifierSlotsLine") + ": ";
    public static final String MODIFIERSTART = ChatColor.WHITE + config.getString("Language.ModifiersLine") + ":";

    //Modifiers
    public static final String AUTOSMELT = ChatColor.YELLOW + config.getString("Modifiers.Auto-Smelt.name") + ": ";
    public static final String BEHEADING = ChatColor.DARK_GRAY + config.getString("Modifiers.Beheading.name") + ": ";
    public static final String DIRECTING = ChatColor.GRAY + config.getString("Modifiers.Directing.name");
    public static final String ENDER = ChatColor.DARK_GREEN + config.getString("Modifiers.Ender.name");
    public static final String FIERY = ChatColor.YELLOW + config.getString("Modifiers.Fiery.name") + ": ";
    public static final String GLOWING = ChatColor.YELLOW + config.getString("Modifiers.Glowing.name");
    public static final String HASTE = ChatColor.DARK_RED + config.getString("Modifiers.Haste.name") + ": ";
    public static final String KNOCKBACK = ChatColor.GRAY + config.getString("Modifiers.Knockback.name") + ": ";
    public static final String INFINITY = ChatColor.WHITE + config.getString("Modifiers.Infinity.name");
    public static final String MELTING = ChatColor.GOLD + config.getString("Modifiers.Melting.name") + ": ";
    public static final String LUCK = ChatColor.BLUE + config.getString("Modifiers.Luck.name") + ": ";
    public static final String POISONOUS = ChatColor.DARK_GREEN + config.getString("Modifiers.Poisonous.name") + ": ";
    public static final String POWER = ChatColor.GREEN + config.getString("Modifiers.Power.name") + ": ";
    public static final String REINFORCED = ChatColor.GRAY + config.getString("Modifiers.Reinforced.name") + ": ";
    public static final String SELFREPAIR = ChatColor.GREEN + config.getString("Modifiers.Self-Repair.name") + ": ";
    public static final String SHARPNESS = ChatColor.WHITE + config.getString("Modifiers.Sharpness.name") +  ": ";
    public static final String SHULKING = ChatColor.LIGHT_PURPLE + config.getString("Modifiers.Shulking.name") + ": ";
    public static final String SILKTOUCH = ChatColor.WHITE + config.getString("Modifiers.Silk-Touch.name");
    public static final String SWEEPING = ChatColor.RED + config.getString("Modifiers.Sweeping.name") + ": ";
    public static final String TIMBER = ChatColor.GREEN + config.getString("Modifiers.Timber.name");
    public static final String WEBBED = ChatColor.WHITE + config.getString("Modifiers.Webbed.name") + ": ";
    public static final String XP = ChatColor.GREEN + config.getString("Modifiers.XP.name") + ": ";
}

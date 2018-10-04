package de.flo56958.MineTinker.Data;

import de.flo56958.MineTinker.Main;
import org.bukkit.ChatColor;

public class Strings {

    public static final String CHAT_PREFIX = Main.getPlugin().getConfig().getString("chat-prefix");

    public static final String IDENTIFIER = ChatColor.WHITE + "MineTinker-Tool";
    public static final String IDENTIFIER_BUILDERSWAND = ChatColor.WHITE + "MineTinker-Builderswand";

    public static final String LEVELLINE = ChatColor.GOLD + "Level:" + ChatColor.WHITE + " ";
    public static final String EXPLINE = ChatColor.GOLD + "Exp:" + ChatColor.WHITE + " ";
    public static final String FREEMODIFIERSLOTS = ChatColor.WHITE + "Free Modifier Slots: ";
    public static final String MODIFIERSTART = ChatColor.WHITE + "Modifiers:";

    //Modifiers
    public static final String AUTOSMELT = ChatColor.YELLOW + Main.getPlugin().getConfig().getString("Modifiers.Auto-Smelt.name") + ": ";
    public static final String BEHEADING = ChatColor.DARK_GRAY + Main.getPlugin().getConfig().getString("Modifiers.Beheading.name") + ": ";
    public static final String ENDER = ChatColor.DARK_GREEN + Main.getPlugin().getConfig().getString("Modifiers.Ender.name");
    public static final String FIERY = ChatColor.YELLOW + Main.getPlugin().getConfig().getString("Modifiers.Fiery.name") + ": ";
    public static final String GLOWING = ChatColor.YELLOW + Main.getPlugin().getConfig().getString("Modifiers.Glowing.name");
    public static final String HASTE = ChatColor.DARK_RED + Main.getPlugin().getConfig().getString("Modifiers.Haste.name") + ": ";
    public static final String KNOCKBACK = ChatColor.GRAY + Main.getPlugin().getConfig().getString("Modifiers.Knockback.name") + ": ";
    public static final String INFINITY = ChatColor.WHITE + Main.getPlugin().getConfig().getString("Modifiers.Infinity.name");
    public static final String LUCK = ChatColor.BLUE + Main.getPlugin().getConfig().getString("Modifiers.Luck.name") + ": ";
    public static final String POISONOUS = ChatColor.DARK_GREEN + Main.getPlugin().getConfig().getString("Modifiers.Poisonous.name") + ": ";
    public static final String POWER = ChatColor.GREEN + Main.getPlugin().getConfig().getString("Modifiers.Power.name") + ": ";
    public static final String REINFORCED = ChatColor.GRAY + Main.getPlugin().getConfig().getString("Modifiers.Reinforced.name") + ": ";
    public static final String SELFREPAIR = ChatColor.GREEN + Main.getPlugin().getConfig().getString("Modifiers.Self-Repair.name") + ": ";
    public static final String SHARPNESS = ChatColor.WHITE + Main.getPlugin().getConfig().getString("Modifiers.Sharpness.name") +  ": ";
    public static final String SHULKING = ChatColor.LIGHT_PURPLE + Main.getPlugin().getConfig().getString("Modifiers.Shulking.name") + ": ";
    public static final String SILKTOUCH = ChatColor.WHITE + Main.getPlugin().getConfig().getString("Modifiers.Silk-Touch.name");
    public static final String SWEEPING = ChatColor.RED + Main.getPlugin().getConfig().getString("Modifiers.Sweeping.name") + ": ";
    public static final String TIMBER = ChatColor.GREEN + Main.getPlugin().getConfig().getString("Modifiers.Timber.name");
    public static final String XP = ChatColor.GREEN + Main.getPlugin().getConfig().getString("Modifiers.XP.name") + ": ";
}

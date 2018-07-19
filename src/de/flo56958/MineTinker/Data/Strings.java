package de.flo56958.MineTinker.Data;

import de.flo56958.MineTinker.Main;
import org.bukkit.ChatColor;

public class Strings {

    public static final String CHAT_PREFIX = Main.getPlugin().getConfig().getString("chat-prefix");

    public static final String IDENTIFIER = ChatColor.WHITE + "MineTinker-Tool";
    public static final String LEVELLINE = ChatColor.GOLD + "Level:" + ChatColor.WHITE + " ";
    public static final String EXPLINE = ChatColor.GOLD + "Exp:" + ChatColor.WHITE + " ";
    public static final String FREEMODIFIERSLOTS = ChatColor.WHITE + "Free Modifier Slots: ";
    public static final String MODIFIERSTART = ChatColor.WHITE + "Modifiers:";

    //Modifiers
    public static final String SELFREPAIR = ChatColor.GREEN + "Self-Repair: ";
    public static final String SHARPNESS = ChatColor.WHITE + "Sharpness: ";
    public static final String REINFORCED = ChatColor.BLACK + "Reinforced: ";
    public static final String HASTE = ChatColor.DARK_RED + "Haste: ";
    public static final String XP = ChatColor.GREEN + "XP: ";
    public static final String LUCK = ChatColor.BLUE + "Luck: ";
    public static final String SILKTOUCH = ChatColor.WHITE + "Silk-Touch";
    public static final String FIERY = ChatColor.YELLOW + "Fiery: ";
    public static final String AUTOSMELT = ChatColor.YELLOW + "Auto-Smelt: ";
    public static final String POWER = ChatColor.GREEN + "Power: ";
}

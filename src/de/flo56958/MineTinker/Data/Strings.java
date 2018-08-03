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
    public static final String SELFREPAIR = ChatColor.GREEN + Main.getPlugin().getConfig().getString("Modifiers.Self-Repair.name") + ": ";
    public static final String SHARPNESS = ChatColor.WHITE + Main.getPlugin().getConfig().getString("Modifiers.Sharpness.name") +  ": ";
    public static final String REINFORCED = ChatColor.GRAY + Main.getPlugin().getConfig().getString("Modifiers.Reinforced.name") + ": ";
    public static final String HASTE = ChatColor.DARK_RED + Main.getPlugin().getConfig().getString("Modifiers.Haste.name") + ": ";
    public static final String XP = ChatColor.GREEN + Main.getPlugin().getConfig().getString("Modifiers.XP.name") + ": ";
    public static final String LUCK = ChatColor.BLUE + Main.getPlugin().getConfig().getString("Modifiers.Luck.name") + ": ";
    public static final String SILKTOUCH = ChatColor.WHITE + Main.getPlugin().getConfig().getString("Modifiers.Silk-Touch.name");
    public static final String FIERY = ChatColor.YELLOW + Main.getPlugin().getConfig().getString("Modifiers.Fiery.name") + ": ";
    public static final String AUTOSMELT = ChatColor.YELLOW + Main.getPlugin().getConfig().getString("Modifiers.Auto-Smelt.name") + ": ";
    public static final String POWER = ChatColor.GREEN + Main.getPlugin().getConfig().getString("Modifiers.Power.name") + ": ";
    public static final String BEHEADING = ChatColor.DARK_GRAY + Main.getPlugin().getConfig().getString("Modifiers.Beheading.name") + ": ";
}

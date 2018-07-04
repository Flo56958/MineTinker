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
    public static final String AUTOREPAIR = ChatColor.GREEN + "Auto-Repair: ";
}

package de.flo56958.MineTinker.Commands;

import de.flo56958.MineTinker.Utilities.ChatWriter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

class cmd_Info {

    static void info(Player p) {
        ChatWriter.sendMessage(p, ChatColor.WHITE, "MineTinker is a Plugin made by Flo56958.");
        ChatWriter.sendMessage(p, ChatColor.WHITE, "It is inspired by different mods (e.g. TinkersConstruct)");
    }
}

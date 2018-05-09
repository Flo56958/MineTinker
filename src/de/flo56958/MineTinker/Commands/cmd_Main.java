package de.flo56958.MineTinker.Commands;

import de.flo56958.MineTinker.Utilities.ChatWriter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class cmd_Main implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (sender.hasPermission("minetinker.main")) {
                if (args.length == 0) {
                    ChatWriter.sendMessage(p, ChatColor.RED, "You have entered to few arguments!");
                    ChatWriter.sendMessage(p, ChatColor.WHITE, "Possible arguments are:");
                }
            }
        } else {
            sender.sendMessage("This is a player only command");
        }
        return false;
    }
}

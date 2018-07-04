package de.flo56958.MineTinker.Commands;

import de.flo56958.MineTinker.Main;
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
                    int index = 1;
                    if (p.hasPermission("minetinker.reload")) {
                        ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". Reload");
                        index++;
                    }
                    if (p.hasPermission("minetinker.modifiers")) {
                        ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". Modifiers");
                        index++;
                    }
                }

                if (args.length > 0) {
                    if (args[0].toLowerCase().equals("modifiers")) {
                        ChatWriter.sendMessage(p, ChatColor.GOLD, "Possible Modifiers:");
                        int index = 1;
                        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Auto-Repair.allowed")) {
                            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". " + ChatColor.GREEN + "Auto-Repair" + ChatColor.WHITE + ": [Enchanted Mossy Cobblestone] Chance to repair the tool while using the tool!");
                            index++;
                        }
                        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Extra-Durability.allowed")) {
                            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". " + ChatColor.DARK_GRAY + "Extra-Durability" + ChatColor.WHITE + ": [Diamond] Adds  additional durability to the tool!");
                            index++;
                        }
                        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Extra-Modifier.allowed")) {
                            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". " + ChatColor.GRAY + "Extra-Modifier-Slot" + ChatColor.WHITE + ": [Netherstar] Adds a additional Modifier-Slot to the tool!");
                            index++;
                        }
                        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Haste.allowed")) {
                            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". " + ChatColor.DARK_RED + "Haste" + ChatColor.WHITE + ": [Redstone] Tool can destroy blocks faster!");
                            index++;
                        }
                        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Power.allowed")) {
                            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". " + ChatColor.RED + "Power" + ChatColor.WHITE + ": [Emerald] Tool can destroy more blocks per swing!");
                            index++;
                        }
                        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Reinforced.allowed")) {
                            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". " + ChatColor.BLACK + "Reinforced" + ChatColor.WHITE + ": [Obsidian] Chance to not use durability when using the tool!");
                            index++;
                        }
                        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Sharpness.allowed")) {
                            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". " + ChatColor.DARK_RED + "Sharpness" + ChatColor.WHITE + ": [Netherquartz] Tool does additional damage!");
                            index++;
                        }
                    }
                }
            }
        } else {
            sender.sendMessage("This is a player only command");
        }
        return false;
    }
}

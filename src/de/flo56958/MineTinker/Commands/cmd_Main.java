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
                    onHelp(p);
                }
                if (args.length > 0) {
                    if ((args[0].toLowerCase().equals("help") || args[0].toLowerCase().equals("?")) && p.hasPermission("minetinker.help")) {
                        onHelp(p);
                    } else if ((args[0].toLowerCase().equals("info") || args[0].toLowerCase().equals("i")) && p.hasPermission("minetinker.info")) {

                    } else if ((args[0].toLowerCase().equals("modifiers") || args[0].toLowerCase().equals("mods")) && p.hasPermission("minetinker.modifiers")) {
                        ChatWriter.sendMessage(p, ChatColor.GOLD, "Possible Modifiers:");
                        int index = 1;
                        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Self-Repair.allowed")) {
                            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". " + ChatColor.GREEN + "Self-Repair" + ChatColor.WHITE + ": [Enchanted Mossy Cobblestone] Chance to repair the tool while using it!");
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
                    } else {
                        ChatWriter.sendMessage(p, ChatColor.RED, "You have entered a wrong or too many argument(s)!");
                        ChatWriter.sendMessage(p, ChatColor.WHITE, "Possible arguments are:");
                        onHelp(p);
                    }
                }
            }
        } else {
            sender.sendMessage("This is a player only command");
        }
        return true;
    }

    private void onHelp (Player p) {
        int index = 1;
        if (p.hasPermission("minetinker.info")) {
            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". Info");
            index++;
        }
        if (p.hasPermission("minetinker.help")) {
            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". Help (?)");
            index++;
        }
        if (p.hasPermission("minetinker.modifiers")) {
            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". Modifiers");
            index++;
        }
        if (p.hasPermission("minetinker.reload")) {
            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". Reload");
            index++;
        }
    }
}

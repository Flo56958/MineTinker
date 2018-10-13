package com.minetinker.commands;

import com.minetinker.Main;
import com.minetinker.data.Strings;
import com.minetinker.utilities.ChatWriter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {

    private static FileConfiguration config = Main.getPlugin().getConfig();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (sender.hasPermission("minetinker.commands.main")) {
                if (args.length == 0) {
                    invalidArgs(p);
                }
                if (args.length > 0) {
                    switch (args[0].toLowerCase()) {
                        case "addexp":
                        case "ae":
                            if (p.hasPermission("minetinker.commands.addexp")) {
                                Functions.addExp(p, args);
                            } else noPerm(p);
                            break;
                        case "addmod":
                        case "am":
                            if (p.hasPermission("minetinker.commands.addmod")) {
                                Functions.addMod(p, args);
                            } else noPerm(p);
                            break;
                        case "convert":
                        case "c":
                            if (p.hasPermission("minetinker.command.convert")) {
                                Functions.convert(p, args);
                            } else noPerm(p);
                            break;
                        case "give":
                        case "g":
                            if (p.hasPermission("minetinker.command.give")) {
                                Functions.give(p, args);
                            } else noPerm(p);
                            break;
                        case "help":
                        case "?":
                        case "h":
                            if (p.hasPermission("minetinker.commands.help")) {
                                onHelp(p);
                            } else noPerm(p);
                            break;
                        case "info":
                        case "i":
                            if (p.hasPermission("minetinker.commands.info")) {
                                ChatWriter.sendMessage(p, ChatColor.WHITE, "MineTinker is a Plugin made by Flo56958.");
                                ChatWriter.sendMessage(p, ChatColor.WHITE, "It is inspired by different mods (e.g. TinkersConstruct)");
                            } else noPerm(p);
                            break;
                        case "modifiers":
                        case "mods":
                            if (p.hasPermission("minetinker.commands.modifiers")) {
                                Mods.list(p);
                            } else noPerm(p);
                            break;
                        case "name":
                        case "n":
                            if (p.hasPermission("minetinker.commands.name")) {
                                Functions.name(p, args);
                            } else noPerm(p);
                            break;
                        case "removemod":
                        case "rm":
                            if (p.hasPermission("minetinker.commands.removemod")) {
                                Functions.removeMod(p, args);
                            } else noPerm(p);
                            break;
                        case "setdurability":
                        case "sd":
                            if (p.hasPermission("minetinker.commands.setdurability")) {
                                Functions.setDurability(p, args);
                            } else noPerm(p);
                            break;
                        default:
                            invalidArgs(p);
                            ChatWriter.sendMessage(p, ChatColor.WHITE, "Possible arguments are:");
                            onHelp(p);
                    }
                }
            }
        } else {
            sender.sendMessage(Strings.CHAT_PREFIX + " " + config.getString("Language.Commands.NotAPlayer"));
        }
        return true;
    }

    static void invalidArgs(Player p) {
        ChatWriter.sendMessage(p, ChatColor.RED, config.getString("Language.Commands.InvalidArguments"));
    }

    private void noPerm(Player p) {
        ChatWriter.sendMessage(p, ChatColor.RED, config.getString("Language.Commands.NoPermission"));
    }

    private void onHelp(Player p) {
        int index = 1;
        if (p.hasPermission("minetinker.commands.addexp")) {
            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". AddExp (ae)");
            index++;
        }
        if (p.hasPermission("minetinker.commands.addmod")) {
            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". AddMod (am)");
            index++;
        }
        if (p.hasPermission("minetinker.commands.convert")) {
            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". Convert (c)");
            index++;
        }
        if (p.hasPermission("minetinker.commands.give")) {
            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". Give (g)");
            index++;
        }
        if (p.hasPermission("minetinker.commands.help")) {
            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". Help (?)");
            index++;
        }
        if (p.hasPermission("minetinker.commands.info")) {
            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". Info (i)");
            index++;
        }
        if (p.hasPermission("minetinker.commands.modifiers")) {
            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". Modifiers (mods)");
            index++;
        }
        if (p.hasPermission("minetinker.commands.name")) {
            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". Name (n)");
            index++;
        }
        if (p.hasPermission("minetinker.commands.removemod")) {
            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". RemoveMod (rm)");
            index++;
        }
        if (p.hasPermission("minetinker.commands.setdurability")) {
            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". SetDurability (sd)");
        }
    }
}

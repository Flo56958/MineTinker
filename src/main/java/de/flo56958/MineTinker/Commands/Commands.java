package de.flo56958.MineTinker.Commands;

import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Commands implements CommandExecutor {

    private static final FileConfiguration config = Main.getPlugin().getConfig();
    private static final ModManager modManager = ModManager.instance();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (sender.hasPermission("minetinker.commands.main")) {
                if (args.length == 0) {
                    invalidArgs(p);
                }
                if (args.length > 0) {
                    switch (args[0].toLowerCase()) { //first argument is the specifier for the command
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
                        case "checkupdate":
                        case "cu":
                            if (p.hasPermission("minetinker.command.checkupdate")) {
                                if (Main.getPlugin().getConfig().getBoolean("CheckForUpdates")) {
                                    Main.getUpdater().checkForUpdate(p);
                                } else ChatWriter.sendMessage(p, ChatColor.RED, "Checking for updates is disabled by the server admin!");
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
                        case "givemodifieritem":
                        case "gm":
                            if (p.hasPermission("minetinker.command.givemodifieritem")) {
                                Functions.giveModifierItem(p, args);
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
                                ChatWriter.sendMessage(p, ChatColor.WHITE, "MineTinker (" + Main.getPlugin().getDescription().getVersion() + ") is a Plugin made by Flo56958.");
                                ChatWriter.sendMessage(p, ChatColor.WHITE, "It is inspired by different mods (e.g. TinkersConstruct)");
                            } else noPerm(p);
                            break;
                        case "modifiers":
                        case "mods":
                            if (p.hasPermission("minetinker.commands.modifiers")) {
                                Functions.modList(p);
                            } else noPerm(p);
                            break;
                        case "name":
                        case "n":
                            if (p.hasPermission("minetinker.commands.name")) {
                                Functions.name(p, args);
                            } else noPerm(p);
                            break;
                        case "reload":
                        case "r":
                            if (p.hasPermission("minetinker.commands.reload")) {
                                reload(p);
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
            sender.sendMessage(ChatWriter.CHAT_PREFIX + " " + config.getString("Language.Commands.NotAPlayer"));
        }
        return true;
    }

    /**
     * Outputs the error message "Invalid Arguments" in the Players chat
     * @param player
     */
    static void invalidArgs(Player player) {
        ChatWriter.sendMessage(player, ChatColor.RED, config.getString("Language.Commands.InvalidArguments"));
    }

    /**
     * Outputs the error message "Invalid Tool/Armor" in the Players chat
     * @param player
     */
    static void invalidTool(Player player) {
        ChatWriter.sendMessage(player, ChatColor.RED, config.getString("Language.Commands.InvalidTool"));
    }

    /**
     * Outputs the error message "No Permissions" in the Players chat
     * @param player
     */
    private void noPerm(Player player) {
        ChatWriter.sendMessage(player, ChatColor.RED, config.getString("Language.Commands.NoPermission"));
    }

    /**
     * Outputs all available commands from MineTinker in the chat of the Player
     * @param player
     */
    private void onHelp(Player player) {
        int index = 1;
        if (player.hasPermission("minetinker.commands.addexp")) {
            ChatWriter.sendMessage(player, ChatColor.WHITE, index + ". AddExp (ae)");
            index++;
        }
        if (player.hasPermission("minetinker.commands.addmod")) {
            ChatWriter.sendMessage(player, ChatColor.WHITE, index + ". AddMod (am)");
            index++;
        }
        if (player.hasPermission("minetinker.commands.checkupdate")) {
            ChatWriter.sendMessage(player, ChatColor.WHITE, index + ". Checkupdate (cu)");
            index++;
        }
        if (player.hasPermission("minetinker.commands.convert")) {
            ChatWriter.sendMessage(player, ChatColor.WHITE, index + ". Convert (c)");
            index++;
        }
        if (player.hasPermission("minetinker.commands.give")) {
            ChatWriter.sendMessage(player, ChatColor.WHITE, index + ". Give (g)");
            index++;
        }
        if (player.hasPermission("minetinker.commands.givemodifieritem")) {
            ChatWriter.sendMessage(player, ChatColor.WHITE, index + ". GiveModifierItem (gm)");
            index++;
        }
        if (player.hasPermission("minetinker.commands.help")) {
            ChatWriter.sendMessage(player, ChatColor.WHITE, index + ". Help (?)");
            index++;
        }
        if (player.hasPermission("minetinker.commands.info")) {
            ChatWriter.sendMessage(player, ChatColor.WHITE, index + ". Info (i)");
            index++;
        }
        if (player.hasPermission("minetinker.commands.modifiers")) {
            ChatWriter.sendMessage(player, ChatColor.WHITE, index + ". Modifiers (mods)");
            index++;
        }
        if (player.hasPermission("minetinker.commands.name")) {
            ChatWriter.sendMessage(player, ChatColor.WHITE, index + ". Name (n)");
            index++;
        }
        if (player.hasPermission("minetinker.commands.reload")) {
            ChatWriter.sendMessage(player, ChatColor.WHITE, index + ". Reload (r)");
            index++;
        }
        if (player.hasPermission("minetinker.commands.removemod")) {
            ChatWriter.sendMessage(player, ChatColor.WHITE, index + ". RemoveMod (rm)");
            index++;
        }
        if (player.hasPermission("minetinker.commands.setdurability")) {
            ChatWriter.sendMessage(player, ChatColor.WHITE, index + ". SetDurability (sd)");
        }
    }

    private void reload(Player p) { //TODO: Make reload command work again
        ChatWriter.sendMessage(p, ChatColor.RED, "Reloading Config!");
        Main.getPlugin().reloadConfig();
        Main.getMain().getConfigurations().reload();

        ChatWriter.sendMessage(p, ChatColor.RED, "Reloading ModManager!");
        modManager.reload();

        if (config.getBoolean("CheckForUpdates")) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Main.getUpdater().checkForUpdate();
                }
            }.runTaskLater(Main.getPlugin(), 20);
        }
    }
}

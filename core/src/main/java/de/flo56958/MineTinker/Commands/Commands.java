package de.flo56958.MineTinker.Commands;

import de.flo56958.MineTinker.Data.GUIs;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Listeners.BuildersWandListener;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import de.flo56958.MineTinker.Utilities.Updater;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Commands implements TabExecutor {

    private static final FileConfiguration config = Main.getPlugin().getConfig();
    private static final ModManager modManager = ModManager.instance();

    /**
     * all commands
     */
    private static final String[] cmds = {"addexp", "addmod", "checkupdate",
            "convert", "give", "givemodifieritem", "help", "info", "itemstatistics", "modifiers",
            "name", "reload", "removemod", "setdurability"};
    /**
     * all console commands
     */
    private static final String[] cmds_console = {"checkupdate", "info", "modifiers", "reload", "givemodifieritem"};

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (sender.hasPermission("minetinker.commands.main")) {
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
                            if (p.hasPermission("minetinker.commands.checkupdate")) {
                                Functions.checkUpdate(p);
                            } else noPerm(p);
                            break;
                        case "convert":
                        case "c":
                            if (p.hasPermission("minetinker.commands.convert")) {
                                Functions.convert(p, args);
                            } else noPerm(p);
                            break;
                        case "give":
                        case "g":
                            if (p.hasPermission("minetinker.commands.give")) {
                                Functions.give(p, args);
                            } else noPerm(p);
                            break;
                        case "givemodifieritem":
                        case "gm":
                            if (p.hasPermission("minetinker.commands.givemodifieritem")) {
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
                        case "itemstatistics":
                        case "is":
                            if (p.hasPermission("minetinker.commands.itemstatistics")) {
                                Functions.itemStatistics(p);
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
                } else {
                    onHelp(p);
                }
            }
        } else {
            if (args.length > 0) {
                switch (args[0].toLowerCase()) { //first argument is the specifier for the command
                    case "checkupdate":
                    case "cu":
                        Functions.checkUpdate(sender);
                        break;
                    case "info":
                    case "i":
                        sender.sendMessage(ChatWriter.CHAT_PREFIX + " " + "MineTinker (" + Main.getPlugin().getDescription().getVersion() + ") is a Plugin made by Flo56958.");
                        sender.sendMessage(ChatWriter.CHAT_PREFIX + " " + "It is inspired by different mods (e.g. TinkersConstruct)");
                        break;
                    case "modifiers":
                    case "mods":
                        Functions.modList(sender);
                        break;
                    case "reload":
                    case "r":
                        reload(sender);
                        break;
                    default:
                        onHelpConsole(sender);
                }
            } else {
                onHelpConsole(sender);
            }
        }
        return true;
    }

    /**
     * Outputs the error message "Invalid Arguments" in the Players chat
     *
     * @param player The player to send the message to
     */
    static void invalidArgs(Player player) {
        ChatWriter.sendMessage(player, ChatColor.RED, config.getString("Language.Commands.InvalidArguments"));
    }

    /**
     * Outputs the error message "Invalid Arguments" to the CommandSender
     *
     * @param sender The sender to send the message to
     */
    static void invalidArgs(CommandSender sender) {
        ChatWriter.sendMessage(sender, ChatColor.RED, config.getString("Language.Commands.InvalidArguments"));
    }

    /**
     * Outputs the error message "Invalid Tool/Armor" in the Players chat
     *
     * @param player The player to send the message to
     */
    static void invalidTool(Player player) {
        ChatWriter.sendMessage(player, ChatColor.RED, config.getString("Language.Commands.InvalidTool"));
    }

    /**
     * Outputs the error message "No Permissions" in the Players chat
     *
     * @param player The player to send the message to
     */
    private void noPerm(Player player) {
        ChatWriter.sendMessage(player, ChatColor.RED, config.getString("Language.Commands.NoPermission"));
    }

    /**
     * Outputs all available commands from MineTinker in the chat of the Player
     *
     * @param player The player to send the message to
     */
    private void onHelp(Player player) {
        // TODO: Turn into a HashMap<String, String> that's iterated over
        int index = 1;

        if (player.hasPermission("minetinker.commands.addexp")) {
            ChatWriter.sendMessage(player, ChatColor.WHITE, index++ + ". AddExp (ae)");
        }
        if (player.hasPermission("minetinker.commands.addmod")) {
            ChatWriter.sendMessage(player, ChatColor.WHITE, index++ + ". AddMod (am)");
        }
        if (player.hasPermission("minetinker.commands.checkupdate")) {
            ChatWriter.sendMessage(player, ChatColor.WHITE, index++ + ". Checkupdate (cu)");
        }
        if (player.hasPermission("minetinker.commands.convert")) {
            ChatWriter.sendMessage(player, ChatColor.WHITE, index++ + ". Convert (c)");
        }
        if (player.hasPermission("minetinker.commands.give")) {
            ChatWriter.sendMessage(player, ChatColor.WHITE, index++ + ". Give (g)");
        }
        if (player.hasPermission("minetinker.commands.givemodifieritem")) {
            ChatWriter.sendMessage(player, ChatColor.WHITE, index++ + ". GiveModifierItem (gm)");
        }
        if (player.hasPermission("minetinker.commands.help")) {
            ChatWriter.sendMessage(player, ChatColor.WHITE, index++ + ". Help (?)");
        }
        if (player.hasPermission("minetinker.commands.info")) {
            ChatWriter.sendMessage(player, ChatColor.WHITE, index++ + ". Info (i)");
        }
        if (player.hasPermission("minetinker.commands.itemstatistics")) {
            ChatWriter.sendMessage(player, ChatColor.WHITE, index++ + ". ItemStatistics (is)");
        }
        if (player.hasPermission("minetinker.commands.modifiers")) {
            ChatWriter.sendMessage(player, ChatColor.WHITE, index++ + ". Modifiers (mods)");
        }
        if (player.hasPermission("minetinker.commands.name")) {
            ChatWriter.sendMessage(player, ChatColor.WHITE, index++ + ". Name (n)");
        }
        if (player.hasPermission("minetinker.commands.reload")) {
            ChatWriter.sendMessage(player, ChatColor.WHITE, index++ + ". Reload (r)");
        }
        if (player.hasPermission("minetinker.commands.removemod")) {
            ChatWriter.sendMessage(player, ChatColor.WHITE, index++ + ". RemoveMod (rm)");
        }
        if (player.hasPermission("minetinker.commands.setdurability")) {
            ChatWriter.sendMessage(player, ChatColor.WHITE, index + ". SetDurability (sd)");
        }

        ChatWriter.sendMessage(player, ChatColor.WHITE, "For more indepth help visit: ");
        ChatWriter.sendMessage(player, ChatColor.GOLD, "https://flo56958.github.io/MineTinker");
        ChatWriter.sendMessage(player, ChatColor.WHITE, "Or ask on the official MineTinker-Discordserver: ");
        ChatWriter.sendMessage(player, ChatColor.GOLD, "http://discord.gg/ZEVNKhN");
    }

    /**
     * Outputs all available commands from MineTinker in the console
     *
     * @param sender The sender to send the message to
     */
    private void onHelpConsole(CommandSender sender) {
        int index = 1;
        sender.sendMessage(ChatWriter.CHAT_PREFIX + " " + index++ + ". CheckUpdate (cu)");
        sender.sendMessage(ChatWriter.CHAT_PREFIX + " " + index++ + ". GiveModifierItem (gm)");
        sender.sendMessage(ChatWriter.CHAT_PREFIX + " " + index++ + ". Info (i)");
        sender.sendMessage(ChatWriter.CHAT_PREFIX + " " + index++ + ". Modifiers (mods)");
        sender.sendMessage(ChatWriter.CHAT_PREFIX + " " + index + ". reload (r)");

        ChatWriter.sendMessage(sender, ChatColor.GRAY, "For more indepth help visit: ");
        ChatWriter.sendMessage(sender, ChatColor.GOLD, "https://flo56958.github.io/MineTinker");
        ChatWriter.sendMessage(sender, ChatColor.GRAY, "Or ask on the official MineTinker-Discordserver: ");
        ChatWriter.sendMessage(sender, ChatColor.GOLD, "http://discord.gg/ZEVNKhN");
    }

    /**
     * reloads the plugins configuration
     *
     * @param sender The sender to send the message to
     */
    private void reload(CommandSender sender) {
        ChatWriter.sendMessage(sender, ChatColor.RED, "NOTE: It is possible that the plugin will not work correctly after reload!");
        ChatWriter.sendMessage(sender, ChatColor.RED, "NOTE: Builderswands need a complete restart to function correctly on the new configurations!");

        ChatWriter.sendMessage(sender, ChatColor.WHITE, "Clearing recipes!");

        Iterator<Recipe> it = Main.getPlugin().getServer().recipeIterator(); //TODO: Better algorithm for removing recipes from modifiers

        while (it.hasNext()) {
            Recipe rec = it.next();

            for (Modifier mod : modManager.getAllMods()) {
                if (mod.getModItem().equals(rec.getResult())) {
                    it.remove();
                    break;
                }
            }

            if (BuildersWandListener.getWands().contains(rec.getResult())) {
                it.remove();
            }
        }

        ModManager.instance().recipe_Namespaces.clear();

        ChatWriter.sendMessage(sender, ChatColor.WHITE, "Reloading Config!");
        Main.getPlugin().reloadConfig();
        ConfigurationManager.reload();

        ChatWriter.sendMessage(sender, ChatColor.WHITE, "Reloading ModManager!");
        modManager.reload();

        ChatWriter.sendMessage(sender, ChatColor.WHITE, "Reloading Builderswands!");
        BuildersWandListener.reload();

        ChatWriter.sendMessage(sender, ChatColor.WHITE, "Reloading GUIs!");
        GUIs.reload();

        ChatWriter.sendMessage(sender, ChatColor.WHITE, "Done reloading!");

        if (config.getBoolean("CheckForUpdates")) {
            Bukkit.getScheduler().scheduleAsyncDelayedTask(Main.getPlugin(), Updater::checkForUpdate, 20);
        }

    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        ArrayList<String> result = new ArrayList<>();
        ArrayList<String> numbers = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            numbers.add(Integer.toString(i));
        }

        switch (args.length) {
            case 0:
                return null;
            case 1: //top level command
                if (sender instanceof Player) {
                    for (String s : cmds) {
                        if (sender.hasPermission("minetinker.commmands." + s)) {
                            result.add(s);
                        }
                    }
                } else {
                    result.addAll(Arrays.asList(cmds_console));
                }
                break;

            case 2:
                switch (args[0]) {
                    case "addmod":
                    case "am":
                    case "givemodifieritem":
                    case "gm":
                        if (sender instanceof Player) {
                            for (Modifier mod : modManager.getAllowedMods()) {
                                result.add(mod.getName());
                            }
                        }
                        break;
                    case "removemod":
                    case "rm":
                        if (sender instanceof Player) {
                            for (Modifier mod : modManager.getAllowedMods()) {
                                if (modManager.hasMod(((Player) sender).getInventory().getItemInMainHand(), mod)) {
                                    result.add(mod.getName());
                                }
                            }
                        }
                        break;
                    case "give":
                    case "g":
                        for (ToolType type : ToolType.values()) {
                            for (Material mat : type.getMaterials()) {
                                result.add(mat.toString());
                            }
                        }
                }
                break;
            case 3:
                switch (args[0]) {
                    case "givemodifieritem":
                    case "gm":
                        result.addAll(numbers);
                }
            case 4:
                switch (args[0]) {
                    case "givemodifieritem":
                    case "gm":
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            result.add(p.getName());
                        }
                        break;
                }
        }

        result.removeIf (s -> !s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()));
        // filter out any command that is not the beginning of the typed command

        return result;
    }
}

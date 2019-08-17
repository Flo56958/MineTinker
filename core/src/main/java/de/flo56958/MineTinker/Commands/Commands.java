package de.flo56958.MineTinker.Commands;

import de.flo56958.MineTinker.Data.GUIs;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Listeners.BuildersWandListener;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import de.flo56958.MineTinker.Utilities.LanguageManager;
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
            Player player = (Player) sender;
            if (sender.hasPermission("minetinker.commands.main")) {
                if (args.length > 0) {
                    switch (args[0].toLowerCase()) { //first argument is the specifier for the command
                        case "addexp":
                        case "ae":
                            if (player.hasPermission("minetinker.commands.addexp")) {
                                Functions.addExp(player, args);
                            } else noPerm(player);
                            break;
                        case "addmod":
                        case "am":
                            if (player.hasPermission("minetinker.commands.addmod")) {
                                Functions.addMod(player, args);
                            } else noPerm(player);
                            break;
                        case "checkupdate":
                        case "cu":
                            if (player.hasPermission("minetinker.commands.checkupdate")) {
                                Functions.checkUpdate(player);
                            } else noPerm(player);
                            break;
                        case "convert":
                        case "c":
                            if (player.hasPermission("minetinker.commands.convert")) {
                                Functions.convert(player, args);
                            } else noPerm(player);
                            break;
                        case "give":
                        case "g":
                            if (player.hasPermission("minetinker.commands.give")) {
                                Functions.give(player, args);
                            } else noPerm(player);
                            break;
                        case "givemodifieritem":
                        case "gm":
                            if (player.hasPermission("minetinker.commands.givemodifieritem")) {
                                Functions.giveModifierItem(player, args);
                            } else noPerm(player);
                            break;
                        case "help":
                        case "?":
                        case "h":
                            if (player.hasPermission("minetinker.commands.help")) {
                                onHelp(player);
                            } else noPerm(player);
                            break;
                        case "info":
                        case "i":
                            if (player.hasPermission("minetinker.commands.info")) {
                                ChatWriter.sendMessage(player, ChatColor.WHITE, LanguageManager.getString("Commands.Info.Line1", player).replaceFirst("%ver", Main.getPlugin().getDescription().getVersion()));
                                ChatWriter.sendMessage(player, ChatColor.WHITE, LanguageManager.getString("Commands.Info.Line2", player));
                            } else noPerm(player);
                            break;
                        case "itemstatistics":
                        case "is":
                            if (player.hasPermission("minetinker.commands.itemstatistics")) {
                                Functions.itemStatistics(player);
                            } else noPerm(player);
                            break;
                        case "modifiers":
                        case "mods":
                            if (player.hasPermission("minetinker.commands.modifiers")) {
                                Functions.modList(player);
                            } else noPerm(player);
                            break;
                        case "name":
                        case "n":
                            if (player.hasPermission("minetinker.commands.name")) {
                                Functions.name(player, args);
                            } else noPerm(player);
                            break;
                        case "reload":
                        case "r":
                            if (player.hasPermission("minetinker.commands.reload")) {
                                reload(player);
                            } else noPerm(player);
                            break;
                        case "removemod":
                        case "rm":
                            if (player.hasPermission("minetinker.commands.removemod")) {
                                Functions.removeMod(player, args);
                            } else noPerm(player);
                            break;
                        case "setdurability":
                        case "sd":
                            if (player.hasPermission("minetinker.commands.setdurability")) {
                                Functions.setDurability(player, args);
                            } else noPerm(player);
                            break;
                        default:
                            invalidArgs(player);
                            ChatWriter.sendMessage(player, ChatColor.WHITE, "Possible arguments are:");
                            onHelp(player);
                    }
                } else {
                    onHelp(player);
                }
            }
        } else {
            if (args.length > 0) {
                switch (args[0].toLowerCase()) { //first argument is the specifier for the command
                    case "checkupdate":
                    case "cu":
                        Functions.checkUpdate(sender);
                        break;
                    case "givemodifieritem":
                    case "gm":
                        Functions.giveModifierItem(sender, args);
                        break;
                    case "info":
                    case "i":
                        sender.sendMessage(ChatWriter.CHAT_PREFIX + " " + LanguageManager.getString("Commands.Info.Line1").replaceFirst("%ver", Main.getPlugin().getDescription().getVersion()));
                        sender.sendMessage(ChatWriter.CHAT_PREFIX + " " + LanguageManager.getString("Commands.Info.Line2"));
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
        ChatWriter.sendMessage(player, ChatColor.RED, LanguageManager.getString("Commands.InvalidArguments", player));
    }

    /**
     * Outputs the error message "Invalid Arguments" to the CommandSender
     *
     * @param sender The sender to send the message to
     */
    static void invalidArgs(CommandSender sender) {
        ChatWriter.sendMessage(sender, ChatColor.RED, LanguageManager.getString("Commands.InvalidArguments"));
    }

    /**
     * Outputs the error message "Invalid Tool/Armor" in the Players chat
     *
     * @param player The player to send the message to
     */
    static void invalidTool(Player player) {
        ChatWriter.sendMessage(player, ChatColor.RED, LanguageManager.getString("Commands.InvalidTool", player));
    }

    /**
     * Outputs the error message "No Permissions" in the Players chat
     *
     * @param player The player to send the message to
     */
    private void noPerm(Player player) {
        ChatWriter.sendMessage(player, ChatColor.RED, LanguageManager.getString("Commands.NoPermission", player));
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

        ChatWriter.sendMessage(player, ChatColor.WHITE, LanguageManager.getString("Commands.Help.Website", player));
        ChatWriter.sendMessage(player, ChatColor.GOLD, "https://flo56958.github.io/MineTinker");
        ChatWriter.sendMessage(player, ChatColor.WHITE, LanguageManager.getString("Commands.Help.Discord", player));
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

        ChatWriter.sendMessage(sender, ChatColor.GRAY, LanguageManager.getString("Commands.Help.Website"));
        ChatWriter.sendMessage(sender, ChatColor.GOLD, "https://flo56958.github.io/MineTinker");
        ChatWriter.sendMessage(sender, ChatColor.GRAY, LanguageManager.getString("Commands.Help.Discord"));
        ChatWriter.sendMessage(sender, ChatColor.GOLD, "http://discord.gg/ZEVNKhN");
    }

    /**
     * reloads the plugins configuration
     *
     * @param sender The sender to send the message to
     */
    private void reload(CommandSender sender) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }
        ChatWriter.sendMessage(sender, ChatColor.RED, LanguageManager.getString("Commands.Reload.Note1", player));
        ChatWriter.sendMessage(sender, ChatColor.RED, LanguageManager.getString("Commands.Reload.Note2", player));

        ChatWriter.sendMessage(sender, ChatColor.WHITE, LanguageManager.getString("Commands.Reload.Recipes", player));

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

        ChatWriter.sendMessage(sender, ChatColor.WHITE, LanguageManager.getString("Commands.Reload.Configs", player));
        Main.getPlugin().reloadConfig();
        ChatWriter.reload();
        ConfigurationManager.reload();

        LanguageManager.reload();

        ChatWriter.sendMessage(sender, ChatColor.WHITE,  LanguageManager.getString("Commands.Reload.ModManager", player));
        modManager.reload();

        ChatWriter.sendMessage(sender, ChatColor.WHITE,  LanguageManager.getString("Commands.Reload.Builderswands", player));
        BuildersWandListener.reload();

        ChatWriter.sendMessage(sender, ChatColor.WHITE,  LanguageManager.getString("Commands.Reload.GUIs", player));
        GUIs.reload();

        ChatWriter.sendMessage(sender, ChatColor.WHITE,  LanguageManager.getString("Commands.Reload.Finish", player));

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
                            for (Material mat : type.getToolMaterials()) {
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
                        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                            result.add(onlinePlayer.getName());
                        }
                        break;
                }
        }

        result.removeIf (s -> !s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()));
        // filter out any command that is not the beginning of the typed command

        return result;
    }
}

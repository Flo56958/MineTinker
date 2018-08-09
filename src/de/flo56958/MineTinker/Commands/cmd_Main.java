package de.flo56958.MineTinker.Commands;

import de.flo56958.MineTinker.Data.Strings;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.LevelCalculator;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
                        ChatWriter.sendMessage(p, ChatColor.WHITE, "MineTinker is a Plugin made by Flo56958.");
                        ChatWriter.sendMessage(p, ChatColor.WHITE, "It is inspired by different mods (e.g. TinkersConstruct)");
                    } else if ((args[0].toLowerCase().equals("modifiers") || args[0].toLowerCase().equals("mods")) && p.hasPermission("minetinker.modifiers")) {
                        //<editor-fold desc="MODIFIERS">
                        ChatWriter.sendMessage(p, ChatColor.GOLD, "Possible Modifiers:");
                        int index = 1;
                        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Auto-Smelt.allowed")) {
                            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". " + Strings.AUTOSMELT + ChatColor.WHITE + "[Enhanced Furnace] Chance to smelt ore when mined! (P)");
                            index++;
                        }
                        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Beheading.allowed")) {
                            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". " + Strings.BEHEADING + ChatColor.WHITE + "[Enchanted Wither-Skull] Chance to drop the head of the mob! (SW)");
                            index++;
                        }
                        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Self-Repair.allowed")) {
                            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". " + Strings.SELFREPAIR + ChatColor.WHITE + "[Enchanted Mossy Cobblestone] Chance to repair the tool while using it!");
                            index++;
                        }
                        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Silk-Touch.allowed")) {
                            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". " + Strings.SILKTOUCH + ChatColor.WHITE + "[Enchanted Cobweb] Applies Silk-Touch! (P/A/S)");
                            index++;
                        }
                        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Luck.allowed")) {
                            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". " + Strings.LUCK + ChatColor.WHITE + "[Compressed Lapis-Block] Lets you get more drops from blocks or mobs!");
                            index++;
                        }
                        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Extra-Modifier.allowed")) {
                            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". " + ChatColor.GRAY + "Extra-Modifier-Slot: " + ChatColor.WHITE + "[Netherstar] Adds a additional Modifier-Slot to the tool!");
                            index++;
                        }
                        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Fiery.allowed")) {
                            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". " + Strings.FIERY + ChatColor.WHITE + "[Blaze-Rod] Enflames enemies! (SW)");
                            index++;
                        }
                        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Haste.allowed")) {
                            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". " + Strings.HASTE + ChatColor.WHITE + "[Redstone] Tool can destroy blocks faster! (P/A/S)");
                            index++;
                        }
                        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Power.allowed")) {
                            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". " + Strings.POWER + ChatColor.WHITE + "[Emerald] Tool can destroy more blocks per swing! (P/A/S)");
                            index++;
                        }
                        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Reinforced.allowed")) {
                            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". " + Strings.REINFORCED + ChatColor.WHITE + "[Compressed Obsidian] Chance to not use durability when using the tool!");
                            index++;
                        }
                        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Sharpness.allowed")) {
                            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". " + Strings.SHARPNESS + ChatColor.WHITE + "[Compressed Quartzblock] Tool does additional damage! (SW)");
                            index++;
                        }
                        if (Main.getPlugin().getConfig().getBoolean("Modifiers.XP.allowed")) {
                            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". " + Strings.XP + ChatColor.WHITE + "[XP-Bottle] Tool has the chance to drop XP while using it!");
                            index++;
                        }
                        //</editor-fold>
                    } else if ((args[0].toLowerCase().equals("addexp") || args[0].toLowerCase().equals("ae")) && p.hasPermission("minetinker.addexp")){
                        if (!args[1].isEmpty()) {
                            ItemStack tool = p.getInventory().getItemInMainHand();
                            if (tool.hasItemMeta()) {
                                if (tool.getItemMeta().hasLore()) {
                                    if (tool.getItemMeta().getLore().contains(Strings.IDENTIFIER)) {
                                        try {
                                            int amount = Integer.parseInt(args[1]);
                                            LevelCalculator.addExp(p, tool, amount);
                                        } catch (Exception e) {
                                            ChatWriter.sendMessage(p, ChatColor.RED, "You need to enter a number!");
                                        }
                                    } else {
                                        ChatWriter.sendMessage(p, ChatColor.RED, "This command works only with a MineTinker-Tool!");
                                    }
                                } else {
                                    ChatWriter.sendMessage(p, ChatColor.RED, "This command works only with a MineTinker-Tool!");
                                }
                            } else {
                                ChatWriter.sendMessage(p, ChatColor.RED, "This command works only with a MineTinker-Tool!");
                            }
                        } else {
                            ChatWriter.sendMessage(p, ChatColor.RED, "You have entered to few arguments!");
                        }
                    } else if ((args[0].toLowerCase().equals("name") || args[0].toLowerCase().equals("n")) && p.hasPermission("minetinker.name")){
                        if (!args[1].isEmpty()) {
                            ItemStack tool = p.getInventory().getItemInMainHand();
                            if (tool.hasItemMeta()) {
                                if (tool.getItemMeta().hasLore()) {
                                    if (tool.getItemMeta().getLore().contains(Strings.IDENTIFIER)) {
                                        String name = args[1].replace('^', '§');
                                        ItemMeta meta = tool.getItemMeta().clone();
                                        meta.setDisplayName(name);
                                        tool.setItemMeta(meta);
                                    } else {
                                        ChatWriter.sendMessage(p, ChatColor.RED, "This command works only with a MineTinker-Tool!");
                                    }
                                } else {
                                    ChatWriter.sendMessage(p, ChatColor.RED, "This command works only with a MineTinker-Tool!");
                                }
                            } else {
                                ChatWriter.sendMessage(p, ChatColor.RED, "This command works only with a MineTinker-Tool!");
                            }
                        } else {
                            ChatWriter.sendMessage(p, ChatColor.RED, "Please enter a name!");
                        }
                    } else {
                        ChatWriter.sendMessage(p, ChatColor.RED, "You have entered a wrong or too many argument(s)! Or you do not have permission to use this command");
                        ChatWriter.sendMessage(p, ChatColor.WHITE, "Possible arguments are:");
                        onHelp(p);
                    }
                }
            }
        } else {
            sender.sendMessage(Strings.CHAT_PREFIX + " This is a player only command");
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
        if (p.hasPermission("minetinker.addexp")) {
            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". AddExp");
            index++;
        }
        if (p.hasPermission("minetinker.name")) {
            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". Name");
            index++;
        }
    }
}

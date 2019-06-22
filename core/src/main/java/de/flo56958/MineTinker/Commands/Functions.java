package de.flo56958.MineTinker.Commands;

import de.flo56958.MineTinker.Data.GUIs;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.Updater;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

class Functions {

    private static final ModManager modManager = ModManager.instance();
    private static final FileConfiguration config = Main.getPlugin().getConfig();

    /**
     * Outputs all available mods to the command sender chat
     * @param sender
     */
    static void modList(CommandSender sender) {
        if (sender instanceof Player) {
            GUIs.getModGUI().show((Player) sender);
            return;
        }
        ChatWriter.sendMessage(sender, ChatColor.GOLD, "Possible Modifiers:");

        int index = 1;

        for (Modifier m : modManager.getAllowedMods())
            ChatWriter.sendMessage(sender, ChatColor.WHITE, index++ + ". " + m.getColor() + m.getName() + ChatColor.WHITE + ": " + m.getDescription());
    }

    /**
     * Adds Exp to the ItemStack in the main hand
     * @param player
     * @param args command input of the player - parsed down from onCommand()
     */
    static void addExp(Player player, String[] args) {
        if (args.length == 2) {
            ItemStack tool = player.getInventory().getItemInMainHand();

            if (modManager.isToolViable(tool) || modManager.isArmorViable(tool)) {
                try {
                    int amount = Integer.parseInt(args[1]);
                    modManager.addExp(player, tool, amount);
                } catch (Exception e) {
                    Commands.invalidArgs(player);
                }
            } else {
                Commands.invalidTool(player);
            }
        } else {
            Commands.invalidArgs(player);
        }
    }

    /**
     * renames the tool in the main hand
     * @param player
     * @param args command input of the player - parsed down from onCommand()
     */
    static void name(Player player, String[] args) {
        if (args.length >= 2) {
            ItemStack tool = player.getInventory().getItemInMainHand();

            if (modManager.isToolViable(tool) || modManager.isArmorViable(tool)) {
                StringBuilder name = new StringBuilder();

                for (int i = 1; i < args.length; i++)
                    name.append(" ").append(args[i].replace('^', '§'));

                name = new StringBuilder(name.substring(1));

                ItemMeta meta = tool.getItemMeta();

                if (meta != null) {
                    meta.setDisplayName(name.toString());
                    tool.setItemMeta(meta);
                }
            } else {
                Commands.invalidTool(player);
            }
        } else {
            Commands.invalidArgs(player);
        }
    }

    /**
     * Removes the specified modifier (index) from a valid MineTinker-Tool/Armor in the players main hand
     * @param player
     * @param args command input of the player - parsed down from onCommand()
     */
    static void removeMod(Player player, String[] args) {
        if (args.length >= 2) {
            ItemStack tool = player.getInventory().getItemInMainHand();

            if (!modManager.isToolViable(tool) && !modManager.isArmorViable(tool)) { Commands.invalidArgs(player); return; }

            for (Modifier m : modManager.getAllMods()) {
                if (args[1].equals(m.getName())) {
                    modManager.removeMod(player.getInventory().getItemInMainHand(), m);
                    return;
                }
            }
            Commands.invalidArgs(player);
        } else {
            Commands.invalidArgs(player);
        }
    }

    /**
     * Adds the specified modifier to the valid MineTinker-Tool/Armor in the players main hand
     * @param player
     * @param args command input of the player - parsed down from onCommand()
     */
    static void addMod(Player player, String[] args) {
        if (args.length == 2) {
            for (Modifier m : modManager.getAllowedMods()) {
                if (m.getName().equalsIgnoreCase(args[1])) {
                    ItemStack tool = player.getInventory().getItemInMainHand();

                    if (modManager.isToolViable(tool) || modManager.isArmorViable(tool)) m.applyMod(player, tool, true);
                    break;
                }
            }
        } else {
            Commands.invalidArgs(player);
        }
    }

    /**
     * Sets the durability of a valid MineTinker-Tool/Armor in the players main hand to the specified amount
     * @param player
     * @param args command input of the player - parsed down from onCommand()
     */
    @SuppressWarnings("deprecation")
	static void setDurability(Player player, String[] args) {
        if (args.length == 2) {
            ItemStack tool = player.getInventory().getItemInMainHand();

            if (modManager.isToolViable(tool) || modManager.isArmorViable(tool)) {
                try {
                    int dura = Integer.parseInt(args[1]);

                    if (dura <= tool.getType().getMaxDurability()) {
                        tool.setDurability((short) (tool.getType().getMaxDurability() - dura));
                    } else {
                        ChatWriter.sendMessage(player, ChatColor.RED, "Please enter a valid number or 'full'!");
                    }
                } catch (Exception e) {
                    if (args[1].toLowerCase().equals("full") || args[1].toLowerCase().equals("f"))
                        tool.setDurability((short) 0);
                    else
                        ChatWriter.sendMessage(player, ChatColor.RED, "Please enter a valid number or 'full'!");
                }
            } else {
                Commands.invalidTool(player);
            }
        } else {
            Commands.invalidArgs(player);
        }
    }

    /**
     * adds a MineTinker-Tool/Armor of the specified type to the players inventory
     * @param player
     * @param args command input of the player - parsed down from onCommand()
     */
    static void give(Player player, String[] args) {
        Material material;

        if (args.length >= 2) {
            try {
                material = Material.getMaterial(args[1].toUpperCase());
            } catch (Exception ignored) {
                Commands.invalidArgs(player);
                return;
            }
        } else {
            Commands.invalidArgs(player);
            return;
        }
        if (material == null) {
            Commands.invalidArgs(player);
            return;
        }

        ItemStack tool = new ItemStack(material, 1);
        modManager.convertItemStack(tool);

        if (player.getInventory().addItem(tool).size() != 0) //adds items to (full) inventory
            player.getWorld().dropItem(player.getLocation(), tool);
        // no else as it gets added in if
    }

    /**
     * converts a viable item in the players main hand to a MineTinker-Tool/Armor
     * @param player
     * @param args command input of the player - parsed down from onCommand()
     */
    static void convert(Player player, String[] args) {
        ItemStack tool = player.getInventory().getItemInMainHand();
        modManager.convertItemStack(tool);
    }

    /**
     * gives the player the requested modifier item from the specific modifier
     * @param player
     * @param args command input of the player - parsed down from onCommand()
     */
    static void giveModifierItem(Player player, String[] args) {
        boolean allOnline = false;

        if (args.length >= 2) {
            for (Modifier mod : modManager.getAllowedMods()) {
                if (mod.getName().equalsIgnoreCase(args[1])) {
                    int amount = 1;

                    if (args.length >= 3) {
                        try {
                            amount = Integer.parseInt(args[2]);
                        } catch (Exception e) {
                            Commands.invalidArgs(player);
                            return;
                        }
                    }

                    if (args.length >= 4) {
                        if (args[3].equalsIgnoreCase("*")) {
                            allOnline = true;
                        } else {
                            Player temp = Bukkit.getServer().getPlayer(args[3]);

                            if (temp == null) {
                                ChatWriter.sendMessage(player, ChatColor.RED, "Player " + args[3] + " not found or not online!");
                                return;
                            }
                            player = temp;
                        }

                    }

                    if (allOnline) {
                        for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
                            for (int i = 0; i < amount; i++) {
                                if (onlinePlayer.getInventory().addItem(mod.getModItem()).size() != 0) { //adds items to (full) inventory
                                    onlinePlayer.getWorld().dropItem(onlinePlayer.getLocation(), mod.getModItem());
                                } // no else as it gets added in if
                            }
                        }
                    } else {
                        for (int i = 0; i < amount; i++) {
                            if (player.getInventory().addItem(mod.getModItem()).size() != 0) { //adds items to (full) inventory
                                player.getWorld().dropItem(player.getLocation(), mod.getModItem());
                            } // no else as it gets added in if
                        }
                    }

                    break;
                }
            }
        }
    }

    static void checkUpdate(CommandSender sender) {
        if (config.getBoolean("CheckForUpdates")) {
            ChatWriter.sendMessage(sender, ChatColor.WHITE, "Checking for Updates...");

            new BukkitRunnable() {
                @Override
                public void run() {
                    Updater.checkForUpdate(sender);
                }
            }.runTaskLater(Main.getPlugin(), 20);
        } else {
            ChatWriter.sendMessage(sender, ChatColor.RED, "Checking for updates is disabled by the server admin!");
        }
    }
}

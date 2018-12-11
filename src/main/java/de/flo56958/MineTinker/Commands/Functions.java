package de.flo56958.MineTinker.Commands;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

class Functions {

    private static final ModManager modManager = ModManager.instance();

    /**
     * Outputs all available mods to the players chat
     * @param player
     */
    static void modList(Player player) {
        ChatWriter.sendMessage(player, ChatColor.GOLD, "Possible Modifiers:");
        int index = 1;
        for (Modifier m : modManager.getAllMods()) {
            ChatWriter.sendMessage(player, ChatColor.WHITE, index + ". " + m.getColor() + m.getName() + ChatColor.WHITE + ": " + m.getDescription());
            index++;
        }
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
                for (int i = 1; i < args.length; i++) {
                    name.append(" ").append(args[i].replace('^', '§'));
                }
                name = new StringBuilder(name.substring(1));
                ItemMeta meta = tool.getItemMeta().clone();
                meta.setDisplayName(name.toString());
                tool.setItemMeta(meta);
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
    @SuppressWarnings("IfCanBeSwitch")
    static void removeMod(Player player, String[] args) {
        if (args.length == 2) {
            try {
                int index = Integer.parseInt(args[1]);
                ItemStack tool = player.getInventory().getItemInMainHand();
                if (modManager.isToolViable(tool) || modManager.isArmorViable(tool)) {
                    ItemMeta meta = tool.getItemMeta();
                    List<String> lore = meta.getLore();
                    index = index + 4; //To start when modifier start
                    if (!(index >= lore.size())) {
                        String remove = lore.get(index);
                        String[] mod = remove.split(": ");
                        mod[0] = mod[0].substring(2); //Skips the ChatColor-Code at the Beginning TODO: Dual Chatcodes need to be implemented
                        if (mod[0].equals(Main.getPlugin().getConfig().getString("Modifiers.Fiery.name"))) {
                            meta.removeEnchant(Enchantment.FIRE_ASPECT);
                            meta.removeEnchant(Enchantment.ARROW_FIRE);
                        } else if (mod[0].equals(Main.getPlugin().getConfig().getString("Modifiers.Haste.name"))) {
                            meta.removeEnchant(Enchantment.DIG_SPEED);
                        } else if (mod[0].equals(Main.getPlugin().getConfig().getString("Modifiers.Luck.name"))) {
                            meta.removeEnchant(Enchantment.LOOT_BONUS_BLOCKS);
                            meta.removeEnchant(Enchantment.LOOT_BONUS_MOBS);
                        } else if (mod[0].equals(Main.getPlugin().getConfig().getString("Modifiers.Reinforced.name"))) {
                            meta.removeEnchant(Enchantment.DURABILITY);
                        } else if (mod[0].equals(Main.getPlugin().getConfig().getString("Modifiers.Sweeping.name"))) {
                            meta.removeEnchant(Enchantment.SWEEPING_EDGE);
                        } else if (mod[0].equals(Main.getPlugin().getConfig().getString("Modifiers.Knockback.name"))) {
                            meta.removeEnchant(Enchantment.KNOCKBACK);
                            meta.removeEnchant(Enchantment.ARROW_KNOCKBACK);
                        } else if (mod[0].equals(Main.getPlugin().getConfig().getString("Modifiers.Self-Repair.name"))) {
                            meta.removeEnchant(Enchantment.MENDING);
                        } else if (mod[0].equals(Main.getPlugin().getConfig().getString("Modifiers.Sharpness.name"))) {
                            meta.removeEnchant(Enchantment.ARROW_DAMAGE);
                            meta.removeEnchant(Enchantment.DAMAGE_ALL);
                        } else if (mod[0].equals(Main.getPlugin().getConfig().getString("Modifiers.Silk-Touch.name"))) {
                            meta.removeEnchant(Enchantment.SILK_TOUCH);
                        } else if (mod[0].equals(Main.getPlugin().getConfig().getString("Modifiers.Infinity.name"))) {
                            meta.removeEnchant(Enchantment.ARROW_INFINITE);
                        } else if (mod[0].equals(Main.getPlugin().getConfig().getString("Modifiers.Protecting.name"))) {
                            meta.removeEnchant(Enchantment.PROTECTION_ENVIRONMENTAL);
                        } else if (mod[0].equals(Main.getPlugin().getConfig().getString("Modifiers.Light-Weight.name"))) {
                            meta.removeEnchant(Enchantment.PROTECTION_FALL);
                        }
                        lore.remove(index);
                        player.getInventory().setItemInMainHand(ItemGenerator.changeItem(tool, meta, lore));
                    }
                }
            } catch (Exception e) {
                Commands.invalidArgs(player);
            }
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
            for (Modifier m : modManager.getAllMods()) {
                if (m.getName().equalsIgnoreCase(args[1])) {
                    ItemStack tool = player.getInventory().getItemInMainHand().clone();
                    if (modManager.isToolViable(tool) || modManager.isArmorViable(tool)) {
                        tool = m.applyMod(player, tool, true);
                        if (tool != null) {
                            player.getInventory().setItemInMainHand(tool);
                        }
                    }
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
                            if (args[1].toLowerCase().equals("full") || args[1].toLowerCase().equals("f")) {
                                tool.setDurability((short) 0);
                            } else {
                                ChatWriter.sendMessage(player, ChatColor.RED, "Please enter a valid number or 'full'!");
                            }
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
            if (!(ToolType.AXE.getMaterials().contains(material) ||
                    ToolType.BOW.getMaterials().contains(material) ||
                    ToolType.HOE.getMaterials().contains(material) ||
                    ToolType.PICKAXE.getMaterials().contains(material) ||
                    ToolType.SHOVEL.getMaterials().contains(material) ||
                    ToolType.SWORD.getMaterials().contains(material) ||
                    ToolType.HELMET.getMaterials().contains(material) ||
                    ToolType.CHESTPLATE.getMaterials().contains(material) ||
                    ToolType.LEGGINGS.getMaterials().contains(material) ||
                    ToolType.BOOTS.getMaterials().contains(material))) {
                Commands.invalidArgs(player);
                return;
            }
        } else {
            Commands.invalidArgs(player);
            return;
        }
        if (args.length == 2) {
            if (player.getInventory().addItem(ItemGenerator.changeLore(new ItemStack(material, 1), ItemGenerator.createLore())).size() != 0) { //adds items to (full) inventory
                player.getWorld().dropItem(player.getLocation(), ItemGenerator.changeLore(new ItemStack(material, 1), ItemGenerator.createLore()));
            } // no else as it gets added in if
        } else if (args.length == 3) {
            int level;
            try {
                level = Integer.parseInt(args[2]);
            } catch (Exception ignored) {
                Commands.invalidArgs(player);
                return;
            }
            if (player.getInventory().addItem(ItemGenerator.changeLore(new ItemStack(material, 1), ItemGenerator.createLore(level))).size() != 0) { //adds items to (full) inventory
                player.getWorld().dropItem(player.getLocation(), ItemGenerator.changeLore(new ItemStack(material, 1), ItemGenerator.createLore(level)));
            } // no else as it gets added in if
        } else {
            Commands.invalidArgs(player);
        }
    }

    /**
     * converts a viable item in the players main hand to a MineTinker-Tool/Armor
     * @param player
     * @param args command input of the player - parsed down from onCommand()
     */
    static void convert(Player player, String[] args) {
        ItemStack tool = player.getInventory().getItemInMainHand();
        if (ToolType.AXE.getMaterials().contains(tool.getType()) ||
                ToolType.BOW.getMaterials().contains(tool.getType()) ||
                ToolType.HOE.getMaterials().contains(tool.getType()) ||
                ToolType.PICKAXE.getMaterials().contains(tool.getType()) ||
                ToolType.SHOVEL.getMaterials().contains(tool.getType()) ||
                ToolType.SWORD.getMaterials().contains(tool.getType()) ||
                ToolType.HELMET.getMaterials().contains(tool.getType()) ||
                ToolType.CHESTPLATE.getMaterials().contains(tool.getType()) ||
                ToolType.LEGGINGS.getMaterials().contains(tool.getType()) ||
                ToolType.BOOTS.getMaterials().contains(tool.getType())) {
            if (args.length < 2) {
                tool.setItemMeta(null);
                ItemGenerator.changeLore(tool, ItemGenerator.createLore());
            } else if (args.length < 3) {
                try {
                    int level = Integer.parseInt(args[1]);
                    tool.setItemMeta(null);
                    ItemGenerator.changeLore(tool, ItemGenerator.createLore(level));
                } catch (Exception ignored) {
                    Commands.invalidArgs(player);
                }
            }
        } else {
            ChatWriter.sendMessage(player, ChatColor.RED, "Item can't be converted!");
        }

    }

    /**
     * gives the player the requested modifier item from the specific modifier
     * @param player
     * @param args command input of the player - parsed down from onCommand()
     */
    public static void giveModifierItem(Player player, String[] args) {
        if (args.length >= 2) {
            for (Modifier mod : modManager.getAllMods()) {
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
                    for (int i = 0; i < amount; i++) {
                        if (player.getInventory().addItem(mod.getModItem()).size() != 0) { //adds items to (full) inventory
                            player.getWorld().dropItem(player.getLocation(), mod.getModItem());
                        } // no else as it gets added in if
                    }
                    break;
                }
            }
        }
    }
}

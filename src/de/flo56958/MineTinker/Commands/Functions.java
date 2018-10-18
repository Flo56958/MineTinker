package de.flo56958.MineTinker.Commands;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import de.flo56958.MineTinker.Utilities.LevelCalculator;
import de.flo56958.MineTinker.Utilities.PlayerInfo;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

class Functions {

    static void addExp(Player p, String[] args) {
        if (args.length == 2) {
            ItemStack tool = p.getInventory().getItemInMainHand();
            if (PlayerInfo.isToolViable(tool)) {
                try {
                    int amount = Integer.parseInt(args[1]);
                    LevelCalculator.addExp(p, tool, amount);
                } catch (Exception e) {
                    Commands.invalidArgs(p);
                }
            } else {
                Commands.invalidTool(p);
            }
        } else {
            Commands.invalidArgs(p);
        }
    }

    static void name(Player p, String[] args) {
        if (args.length >= 2) {
            ItemStack tool = p.getInventory().getItemInMainHand();
            if (PlayerInfo.isToolViable(tool)) {
                String name = "";
                for (int i = 1; i < args.length; i++) {
                    name = name + " " + args[i].replace('^', '§');
                }
                name = name.substring(1);
                System.out.println("----" + name);
                ItemMeta meta = tool.getItemMeta().clone();
                meta.setDisplayName(name);
                tool.setItemMeta(meta);
            } else {
                Commands.invalidTool(p);
            }
        } else {
            Commands.invalidArgs(p);
        }
    }

    static void removeMod(Player p, String[] args) {
        if (args.length == 2) {
            try {
                int index = Integer.parseInt(args[1]);
                ItemStack tool = p.getInventory().getItemInMainHand();
                if (PlayerInfo.isToolViable(tool)) {
                    ItemMeta meta = tool.getItemMeta();
                    List<String> lore = meta.getLore();
                    index = index + 4; //To start when modifier start
                    if (!(index >= lore.size())) {
                        String remove = lore.get(index);
                        String[] mod = remove.split(":");
                        mod[0] = mod[0].substring(2); //Skipps the ChatColor-Code at the Beginning TODO: Dual Chatcodes need to be implemented
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
                        }
                        lore.remove(index);
                        p.getInventory().setItemInMainHand(ItemGenerator.changeItem(tool, meta, lore));
                    }
                }
            } catch (Exception e) {
                Commands.invalidArgs(p);
            }
        } else {
            Commands.invalidArgs(p);
        }
    }

    static void addMod(Player p, String[] args) {
        if (args.length == 2) {
            if (Lists.getAllowedModifiers().contains(args[1].toLowerCase())) {
                ItemStack tool = p.getInventory().getItemInMainHand().clone();
                if (PlayerInfo.isToolViable(tool)) {
                    tool = ItemGenerator.ToolModifier(tool, args[1].toLowerCase(), p, true);
                    if (tool != null) {
                        p.getInventory().setItemInMainHand(tool);
                    }
                } else {
                    Commands.invalidTool(p);
                }
            } else {
                ChatWriter.sendMessage(p, ChatColor.RED, "Please enter a available modifier! (You need to use custom names)");
            }
        } else {
            Commands.invalidArgs(p);
        }
    }

    static void setDurability(Player p, String[] args) {
        if (args.length == 2) {
            ItemStack tool = p.getInventory().getItemInMainHand();
            if (PlayerInfo.isToolViable(tool)) {
                        try {
                            int dura = Integer.parseInt(args[1]);
                            if (dura <= tool.getType().getMaxDurability()) {
                                tool.setDurability((short) (tool.getType().getMaxDurability() - dura));
                            } else {
                                ChatWriter.sendMessage(p, ChatColor.RED, "Please enter a valid number or 'full'!");
                            }
                        } catch (Exception e) {
                            if (args[1].toLowerCase().equals("full") || args[1].toLowerCase().equals("f")) {
                                tool.setDurability((short) 0);
                            } else {
                                ChatWriter.sendMessage(p, ChatColor.RED, "Please enter a valid number or 'full'!");
                            }
                        }
            } else {
                Commands.invalidTool(p);
            }
        } else {
            Commands.invalidArgs(p);
        }
    }

    public static void give(Player p, String[] args) {
        Material material;
        if (args.length >= 2) {
            if (Lists.SWORDS.contains(args[1].toUpperCase()) ||
                    Lists.AXES.contains(args[1].toUpperCase()) ||
                    Lists.BOWS.contains(args[1].toUpperCase()) ||
                    Lists.SHOVELS.contains(args[1].toUpperCase()) ||
                    Lists.HOES.contains(args[1].toUpperCase()) ||
                    Lists.PICKAXES.contains(args[1].toUpperCase())) {
                try {
                    material = Material.getMaterial(args[1].toUpperCase());
                } catch (Exception ignored) {
                    Commands.invalidArgs(p);
                    return;
                }
            } else {
                Commands.invalidArgs(p);
                return;
            }
        } else {
            Commands.invalidArgs(p);
            return;
        }
        if (args.length == 2) {
            if (p.getInventory().addItem(ItemGenerator.changeLore(new ItemStack(material, 1), ItemGenerator.createLore())).size() != 0) { //adds items to (full) inventory
                p.getWorld().dropItem(p.getLocation(), ItemGenerator.changeLore(new ItemStack(material, 1), ItemGenerator.createLore()));
            } // no else as it gets added in if
        } else if (args.length == 3) {
            int level;
            try {
                level = Integer.parseInt(args[2]);
            } catch (Exception ignored) {
                Commands.invalidArgs(p);
                return;
            }
            if (p.getInventory().addItem(ItemGenerator.changeLore(new ItemStack(material, 1), ItemGenerator.createLore(level))).size() != 0) { //adds items to (full) inventory
                p.getWorld().dropItem(p.getLocation(), ItemGenerator.changeLore(new ItemStack(material, 1), ItemGenerator.createLore(level)));
            } // no else as it gets added in if
        } else {
            Commands.invalidArgs(p);
        }
    }

    public static void convert(Player p, String[] args) {
        ItemStack tool = p.getInventory().getItemInMainHand();
        if (Lists.SWORDS.contains(tool.getType().toString()) ||
                Lists.AXES.contains(tool.getType().toString()) ||
                Lists.BOWS.contains(tool.getType().toString()) ||
                Lists.SHOVELS.contains(tool.getType().toString()) ||
                Lists.HOES.contains(tool.getType().toString()) ||
                Lists.PICKAXES.contains(tool.getType().toString())) {
            if (args.length < 2) {
                tool.setItemMeta(null);
                ItemGenerator.changeLore(tool, ItemGenerator.createLore());
            } else if (args.length < 3) {
                try {
                    int level = Integer.parseInt(args[1]);
                    tool.setItemMeta(null);
                    ItemGenerator.changeLore(tool, ItemGenerator.createLore(level));
                } catch (Exception ignored) {
                    Commands.invalidArgs(p);
                }
            }
        } else {
            ChatWriter.sendMessage(p, ChatColor.RED, "Item can't be converted!");
        }

    }
}

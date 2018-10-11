package de.flo56958.MineTinker.Commands;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Data.Strings;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import de.flo56958.MineTinker.Utilities.LevelCalculator;
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
    }

    static void name(Player p, String[] args) {
        if (args.length >= 2) {
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
    }

    static void removeMod(Player p, String[] args) {
        if (args.length == 2) {
            try {
                int index = Integer.parseInt(args[1]);
                ItemStack tool = p.getInventory().getItemInMainHand();
                if (tool.hasItemMeta()) {
                    ItemMeta meta = tool.getItemMeta();
                    if (meta.hasLore()) {
                        List<String> lore = meta.getLore();
                        index = index + 4;
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
                            meta.setLore(lore);
                            tool.setItemMeta(meta);
                            p.getInventory().setItemInMainHand(tool);
                        }
                    }
                }
            } catch (Exception e) {
                ChatWriter.sendMessage(p, ChatColor.RED, "Please enter a mod-index number!");
            }
        } else {
            ChatWriter.sendMessage(p, ChatColor.RED, "Please enter a mod-index number!");
        }
    }

    static void addMod(Player p, String[] args) {
        if (args.length == 2) {
            if (Lists.getAllowedModifiers().contains(args[1].toLowerCase())) {
                ItemStack tool = p.getInventory().getItemInMainHand().clone();
                if (tool.hasItemMeta()) {
                    if (tool.getItemMeta().hasLore()) {
                        if (tool.getItemMeta().getLore().contains(Strings.IDENTIFIER)) {
                            tool = ItemGenerator.ToolModifier(tool, args[1].toLowerCase(), p, true);
                            if (tool != null) {
                                p.getInventory().setItemInMainHand(tool);
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
                ChatWriter.sendMessage(p, ChatColor.RED, "Please enter a available mod! (You need to use custom names)");
            }
        } else {
            ChatWriter.sendMessage(p, ChatColor.RED, "Please enter a mod!");
        }
    }

    static void setDurability(Player p, String[] args) {
        if (args.length == 2) {
            ItemStack tool = p.getInventory().getItemInMainHand();
            if (tool.hasItemMeta()) {
                if (tool.getItemMeta().hasLore()) {
                    if (tool.getItemMeta().getLore().contains(Strings.IDENTIFIER)) {
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
                        ChatWriter.sendMessage(p, ChatColor.RED, "This command works only with a MineTinker-Tool!");
                    }
                } else {
                    ChatWriter.sendMessage(p, ChatColor.RED, "This command works only with a MineTinker-Tool!");
                }
            } else {
                ChatWriter.sendMessage(p, ChatColor.RED, "This command works only with a MineTinker-Tool!");
            }
        } else {
            ChatWriter.sendMessage(p, ChatColor.RED, "Please enter a value!");
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
                    ChatWriter.sendMessage(p, ChatColor.RED, "Please enter a valid tool type!");
                    return;
                }
            } else {
                ChatWriter.sendMessage(p, ChatColor.RED, "Please enter a valid tool type!");
                return;
            }
        } else {
            ChatWriter.sendMessage(p, ChatColor.RED, "Too few arguments!");
            return;
        }
        if (args.length == 2) {
            if(p.getInventory().addItem(ItemGenerator.toolCreate(material)).size() != 0) { //adds items to (full) inventory
                p.getWorld().dropItem(p.getLocation(), ItemGenerator.toolCreate(material));
            } // no else as it gets added in if
        } else if (args.length == 3) {
            int level;
            try {
                level = Integer.parseInt(args[2]);
            } catch (Exception ignored) {
                ChatWriter.sendMessage(p, ChatColor.RED, "Please enter a valid number!");
                return;
            }
            if(p.getInventory().addItem(ItemGenerator.toolCreate(material, level)).size() != 0) { //adds items to (full) inventory
                p.getWorld().dropItem(p.getLocation(), ItemGenerator.toolCreate(material, level));
            } // no else as it gets added in if
        } else {
            ChatWriter.sendMessage(p, ChatColor.RED, "Too many arguments!");
        }
    }
}

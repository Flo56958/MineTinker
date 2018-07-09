package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Data.Modifiers;
import de.flo56958.MineTinker.Data.Strings;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import de.flo56958.MineTinker.Utilities.LevelCalculator;
import org.bukkit.*;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Random;

public class BlockListener implements Listener {

    @EventHandler (priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent e) {
        if (!e.isCancelled()) {
            if (!(e.getBlock().getType().equals(Material.DOUBLE_PLANT) ||
                    e.getBlock().getType().equals(Material.DEAD_BUSH) ||
                    e.getBlock().getType().equals(Material.LONG_GRASS) ||
                    e.getBlock().getType().equals(Material.RED_MUSHROOM) ||
                    e.getBlock().getType().equals(Material.BROWN_MUSHROOM) ||
                    e.getBlock().getType().equals(Material.YELLOW_FLOWER))) {
                if (e.getPlayer().getGameMode().equals(GameMode.SURVIVAL) || e.getPlayer().getGameMode().equals(GameMode.ADVENTURE)) {
                    ItemStack tool = e.getPlayer().getInventory().getItemInMainHand();
                    ItemMeta meta = tool.getItemMeta();
                    if (!tool.getType().equals(Material.AIR)) {
                        if (meta.hasLore()) {
                            ArrayList<String> lore = (ArrayList<String>) meta.getLore();
                            if (lore.contains(Strings.IDENTIFIER)) {
                                if (tool.getType().getMaxDurability() - tool.getDurability() == 1) {
                                    e.setCancelled(true);
                                    if (Main.getPlugin().getConfig().getBoolean("Sound.OnBreaking")) {
                                        e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_ITEM_BREAK, 0.5F, 0.5F);
                                    }
                                    return;
                                }
                                LevelCalculator.addExp(e.getPlayer(), tool, Main.getPlugin().getConfig().getInt("ExpPerBlockBreak"));
                                if (Main.getPlugin().getConfig().getBoolean("Modifiers.Self-Repair.allowed")) {
                                    //<editor-fold desc="self-repair check">
                                    searchloop:
                                    for (int i = 0; i <= Main.getPlugin().getConfig().getInt("Modifiers.Self-Repair.MaxLevel"); i++) {
                                        if (lore.contains(Strings.SELFREPAIR + i)) {
                                            //self-repair
                                            Random rand = new Random();
                                            int n = rand.nextInt(100);
                                            if (n <= Main.getPlugin().getConfig().getInt("Modifiers.Self-Repair.PercentagePerLevel") * i) {
                                                int heal = Main.getPlugin().getConfig().getInt("Modifiers.Self-Repair.HealthRepair");
                                                short dura = (short) (tool.getDurability() - heal);
                                                if (dura < 0) { dura = 0; }
                                                e.getPlayer().getInventory().getItemInMainHand().setDurability(dura);
                                                ChatWriter.log(false, e.getPlayer().getDisplayName() + " triggered Self-Repair on " + ItemGenerator.getDisplayName(tool) + " (" + tool.getType().toString() + ")!");
                                            }
                                            break searchloop;
                                        }
                                    }
                                    //</editor-fold>
                                }
                                if (Main.getPlugin().getConfig().getBoolean("Modifiers.XP.allowed")) {
                                    //<editor-fold desc="xp check">
                                    searchloop:
                                    for (int i = 0; i <= Main.getPlugin().getConfig().getInt("Modifiers.XP.MaxLevel"); i++) {
                                        if (lore.contains(Strings.XP + i)) {
                                            //self-repair
                                            Random rand = new Random();
                                            int n = rand.nextInt(100);
                                            if (n <= Main.getPlugin().getConfig().getInt("Modifiers.XP.PercentagePerLevel") * i) {
                                                ExperienceOrb orb = e.getPlayer().getWorld().spawn(e.getBlock().getLocation(), ExperienceOrb.class);
                                                orb.setExperience(Main.getPlugin().getConfig().getInt("Modifiers.XP.XPAmount"));
                                                ChatWriter.log(false, e.getPlayer().getDisplayName() + " triggered XP on " + ItemGenerator.getDisplayName(tool) + " (" + tool.getType().toString() + ")!");
                                            }
                                            break searchloop;
                                        }
                                    }
                                    //</editor-fold>
                                }
                                if (Main.getPlugin().getConfig().getBoolean("Modifiers.Auto-Smelt.allowed")) {
                                    //<editor-fold desc="auto-smelt check">
                                    boolean goodBlock = false;
                                    boolean luck = false;
                                    Material loot = Material.AIR;
                                    Short data = 0;
                                    switch (e.getBlock().getType()) {
                                        case COBBLESTONE:
                                            goodBlock = true;
                                            loot = Material.STONE;
                                            break;
                                        case CLAY:
                                            goodBlock = true;
                                            loot = Material.HARD_CLAY;
                                            break;
                                        case SAND:
                                            goodBlock = true;
                                            loot = Material.GLASS;
                                            break;
                                        case LOG:
                                        case LOG_2:
                                            goodBlock = true;
                                            loot = Material.COAL;
                                            data = 1;
                                            break;
                                        case IRON_ORE:
                                            goodBlock = true;
                                            luck = true;
                                            loot = Material.IRON_INGOT;
                                            break;
                                        case GOLD_ORE:
                                            goodBlock = true;
                                            luck = true;
                                            loot = Material.GOLD_INGOT;
                                            break;
                                        case NETHERRACK:
                                            goodBlock = true;
                                            loot = Material.NETHER_BRICK_ITEM;
                                            break;
                                    }
                                    if (goodBlock) {
                                        searchloop:
                                        for (int i = 0; i <= Main.getPlugin().getConfig().getInt("Modifiers.Auto-Smelt.MaxLevel"); i++) {
                                            if (lore.contains(Strings.AUTOSMELT + i)) {
                                                //self-repair
                                                Random rand = new Random();
                                                int n = rand.nextInt(100);
                                                if (n <= Main.getPlugin().getConfig().getInt("Modifiers.Auto-Smelt.PercentagePerLevel") * i) {
                                                    int amount = 1;
                                                    if (Main.getPlugin().getConfig().getBoolean("Modifiers.Luck.allowed") && luck) {
                                                        searchloop2:
                                                        for (int j = 0; j <= 3; j++) {
                                                            if (lore.contains(Strings.LUCK + j)) {
                                                                amount = amount + j;
                                                                break searchloop2;
                                                            }
                                                        }
                                                    }
                                                    e.setDropItems(false);
                                                    ItemStack items = new ItemStack(loot, amount, data);
                                                    e.getBlock().getLocation().getWorld().dropItemNaturally(e.getBlock().getLocation(), items);
                                                    e.getBlock().getLocation().getWorld().spawnParticle(Particle.FLAME, e.getBlock().getLocation(), 5);
                                                    if (Main.getPlugin().getConfig().getBoolean("Modifiers.Auto-Smelt.Sound")) {
                                                        e.getBlock().getLocation().getWorld().playSound(e.getBlock().getLocation(), Sound.ENTITY_GENERIC_BURN, 0.2F, 0.5F);
                                                    }
                                                    ChatWriter.log(false, e.getPlayer().getDisplayName() + " triggered Auto-Smelt on " + ItemGenerator.getDisplayName(tool) + " (" + tool.getType().toString() + ") while mining " + e.getBlock().getType().toString() + "!");
                                                }
                                                break searchloop;
                                            }
                                        }
                                    }
                                    //</editor-fold>
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        if (!e.isCancelled()) {
            if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                ItemStack norm = e.getPlayer().getInventory().getItemInMainHand();
                int temp = norm.getAmount();
                norm.setAmount(1);
                if (norm.equals(Modifiers.LUCK_MODIFIER) ||
                        norm.equals(Modifiers.SHARPNESS_MODIFIER) ||
                        norm.equals(Modifiers.HASTE_MODIFIER) ||
                        norm.equals(Modifiers.REINFORCED_MODIFIER) ||
                        norm.equals(Modifiers.SELFREPAIR_MODIFIER) ||
                        norm.equals(Modifiers.SILKTOUCH_MODIFIER) ||
                        norm.equals(Modifiers.AUTOSMELT_MODIFIER)) {
                    norm.setAmount(temp);
                    e.setCancelled(true);
                    return;
                }
                norm.setAmount(temp);
                if (e.getClickedBlock().getType().equals(Material.BOOKSHELF)) {
                    if (Main.getPlugin().getConfig().getBoolean("Modifiers.Self-Repair.allowed")) {
                        //<editor-fold desc="SELF-REPAIR">
                        if (e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.MOSSY_COBBLESTONE) && !e.getPlayer().isSneaking()) {
                            if (e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
                                e.getPlayer().getLocation().getWorld().dropItemNaturally(e.getPlayer().getLocation(), Modifiers.SELFREPAIR_MODIFIER);
                                if (Main.getPlugin().getConfig().getBoolean("Sound.OnEnchanting")) {
                                    e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 0.5F);
                                }
                                ChatWriter.log(false, e.getPlayer().getDisplayName() + " created a Self-Repair-Modifier in Creative!");
                            } else if (e.getPlayer().getLevel() >= Main.getPlugin().getConfig().getInt("Modifiers.Self-Repair.EnchantCost")) {
                                int amount = e.getPlayer().getInventory().getItemInMainHand().getAmount();
                                int newLevel = e.getPlayer().getLevel() - Main.getPlugin().getConfig().getInt("Modifiers.Self-Repair.EnchantCost");
                                e.getPlayer().setLevel(newLevel);
                                e.getPlayer().getInventory().getItemInMainHand().setAmount(amount - 1);
                                e.getPlayer().getLocation().getWorld().dropItemNaturally(e.getPlayer().getLocation(), Modifiers.SELFREPAIR_MODIFIER);
                                if (Main.getPlugin().getConfig().getBoolean("Sound.OnEnchanting")) {
                                    e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 0.5F);
                                }
                                ChatWriter.log(false, e.getPlayer().getDisplayName() + " created a Self-Repair-Modifier!");
                            } else {
                                ChatWriter.sendMessage(e.getPlayer(), ChatColor.RED, "You do not have enough Levels to perform this action!");
                                ChatWriter.sendMessage(e.getPlayer(), ChatColor.RED, Main.getPlugin().getConfig().getInt("Modifiers.Self-Repair.EnchantCost") + " levels are required!");
                                ChatWriter.log(false,  e.getPlayer().getDisplayName() + " tried to create a Self-Repair-Modifier but had not enough levels!");
                            }
                            e.setCancelled(true);
                        }
                        //</editor-fold>
                    }
                    if (Main.getPlugin().getConfig().getBoolean("Modifiers.Silk-Touch.allowed")) {
                        //<editor-fold desc="SILK-TOUCH">
                        if (e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.WEB) && !e.getPlayer().isSneaking()) {
                            if (e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
                                e.getPlayer().getLocation().getWorld().dropItemNaturally(e.getPlayer().getLocation(), Modifiers.SILKTOUCH_MODIFIER);
                                if (Main.getPlugin().getConfig().getBoolean("Sound.OnEnchanting")) {
                                    e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 0.5F);
                                }
                                ChatWriter.log(false, e.getPlayer().getDisplayName() + " created a Silk-Touch-Modifier in Creative!");
                            } else if (e.getPlayer().getLevel() >= Main.getPlugin().getConfig().getInt("Modifiers.Silk-Touch.EnchantCost")) {
                                int amount = e.getPlayer().getInventory().getItemInMainHand().getAmount();
                                int newLevel = e.getPlayer().getLevel() - Main.getPlugin().getConfig().getInt("Modifiers.Silk-Touch.EnchantCost");
                                e.getPlayer().setLevel(newLevel);
                                e.getPlayer().getInventory().getItemInMainHand().setAmount(amount - 1);
                                e.getPlayer().getLocation().getWorld().dropItemNaturally(e.getPlayer().getLocation(), Modifiers.SILKTOUCH_MODIFIER);
                                if (Main.getPlugin().getConfig().getBoolean("Sound.OnEnchanting")) {
                                    e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 0.5F);
                                }
                                ChatWriter.log(false, e.getPlayer().getDisplayName() + " created a Silk-Touch-Modifier!");
                            } else {
                                ChatWriter.sendMessage(e.getPlayer(), ChatColor.RED, "You do not have enough Levels to perform this action!");
                                ChatWriter.sendMessage(e.getPlayer(), ChatColor.RED, Main.getPlugin().getConfig().getInt("Modifiers.Silk-Touch.EnchantCost") + " levels are required!");
                                ChatWriter.log(false,  e.getPlayer().getDisplayName() + " tried to create a Silk-Touch-Modifier but had not enough levels!");
                            }
                            e.setCancelled(true);
                        }
                        //</editor-fold>
                    }
                    if (Main.getPlugin().getConfig().getBoolean("Modifiers.Fiery.allowed")) {
                        //<editor-fold desc="FIERY">
                        if (e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.BLAZE_ROD) && !e.getPlayer().isSneaking()) {
                            if (e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
                                e.getPlayer().getLocation().getWorld().dropItemNaturally(e.getPlayer().getLocation(), Modifiers.FIERY_MODIFIER);
                                if (Main.getPlugin().getConfig().getBoolean("Sound.OnEnchanting")) {
                                    e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 0.5F);
                                }
                                ChatWriter.log(false, e.getPlayer().getDisplayName() + " created a Fiery-Modifier in Creative!");
                            } else if (e.getPlayer().getLevel() >= Main.getPlugin().getConfig().getInt("Modifiers.Fiery.EnchantCost")) {
                                int amount = e.getPlayer().getInventory().getItemInMainHand().getAmount();
                                int newLevel = e.getPlayer().getLevel() - Main.getPlugin().getConfig().getInt("Modifiers.Fiery.EnchantCost");
                                e.getPlayer().setLevel(newLevel);
                                e.getPlayer().getInventory().getItemInMainHand().setAmount(amount - 1);
                                e.getPlayer().getLocation().getWorld().dropItemNaturally(e.getPlayer().getLocation(), Modifiers.FIERY_MODIFIER);
                                if (Main.getPlugin().getConfig().getBoolean("Sound.OnEnchanting")) {
                                    e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 0.5F);
                                }
                                ChatWriter.log(false, e.getPlayer().getDisplayName() + " created a Fiery-Modifier!");
                            } else {
                                ChatWriter.sendMessage(e.getPlayer(), ChatColor.RED, "You do not have enough Levels to perform this action!");
                                ChatWriter.sendMessage(e.getPlayer(), ChatColor.RED, Main.getPlugin().getConfig().getInt("Modifiers.Fiery.EnchantCost") + " levels are required!");
                                ChatWriter.log(false,  e.getPlayer().getDisplayName() + " tried to create a Fiery-Modifier but had not enough levels!");
                            }
                            e.setCancelled(true);
                        }
                        //</editor-fold>
                    }
                }
            }
        }
    }
}

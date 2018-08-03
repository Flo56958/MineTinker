package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Data.Modifiers;
import de.flo56958.MineTinker.Data.PlayerData;
import de.flo56958.MineTinker.Data.Strings;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import de.flo56958.MineTinker.Utilities.LevelCalculator;
import de.flo56958.MineTinker.Utilities.PlayerInfo;
import net.minecraft.server.v1_13_R1.BlockPosition;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
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
            if (!Lists.WORLDS.contains(e.getPlayer().getWorld().getName())) { return; }
             if (!(e.getBlock().getType().equals(Material.KELP_PLANT) ||
                    e.getBlock().getType().equals(Material.CHORUS_PLANT) ||
                    e.getBlock().getType().equals(Material.DEAD_BUSH) ||
                    e.getBlock().getType().equals(Material.SEAGRASS) ||
                    e.getBlock().getType().equals(Material.GRASS) ||
                    e.getBlock().getType().equals(Material.TALL_SEAGRASS) ||
                    e.getBlock().getType().equals(Material.TALL_GRASS) ||
                    e.getBlock().getType().equals(Material.RED_MUSHROOM) ||
                    e.getBlock().getType().equals(Material.BROWN_MUSHROOM) ||
                    e.getBlock().getType().equals(Material.SUNFLOWER) ||
                    e.getBlock().getType().equals(Material.DANDELION) ||
                    e.getBlock().getType().equals(Material.DANDELION_YELLOW) ||
                    e.getBlock().getType().equals(Material.LILAC) ||
                    e.getBlock().getType().equals(Material.LILY_PAD) ||
                    e.getBlock().getType().equals(Material.BLUE_ORCHID) ||
                    e.getBlock().getType().equals(Material.TORCH))) {
                if (e.getPlayer().getGameMode().equals(GameMode.SURVIVAL) || e.getPlayer().getGameMode().equals(GameMode.ADVENTURE)) {
                    ItemStack tool = e.getPlayer().getInventory().getItemInMainHand();
                    ItemMeta meta = tool.getItemMeta();
                    if (!tool.getType().equals(Material.AIR)) {
                        if (meta.hasLore()) {
                            ArrayList<String> lore = (ArrayList<String>) meta.getLore();
                            if (lore.contains(Strings.IDENTIFIER)) {
                                if (tool.getType().getMaxDurability() - tool.getDurability() <= 1) {
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
                                                e.setExpToDrop(e.getExpToDrop() + Main.getPlugin().getConfig().getInt("Modifiers.XP.XPAmount"));
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
                                    switch (e.getBlock().getType()) {
                                        case COBBLESTONE:
                                            goodBlock = true;
                                            loot = Material.STONE;
                                            break;
                                        case TERRACOTTA:
                                            goodBlock = true;
                                            loot = Material.TERRACOTTA;
                                            break;
                                        case SAND:
                                            goodBlock = true;
                                            loot = Material.GLASS;
                                            break;
                                        case ACACIA_LOG:
                                        case BIRCH_LOG:
                                        case DARK_OAK_LOG:
                                        case JUNGLE_LOG:
                                        case OAK_LOG:
                                        case SPRUCE_LOG:
                                            goodBlock = true;
                                            luck = true;
                                            loot = Material.CHARCOAL;
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
                                            luck = true;
                                            loot = Material.NETHER_BRICK;
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
                                                    ItemStack items = new ItemStack(loot, amount);
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
                                if (Main.getPlugin().getConfig().getBoolean("Modifiers.Power.allowed")) {
                                    if (!PlayerData.hasPower.get(e.getPlayer()) && !e.getPlayer().isSneaking()) {
                                        if (lore.contains(Strings.POWER + 1)) {
                                            PlayerData.hasPower.replace(e.getPlayer(), true);
                                            //<editor-fold desc="POWER 1">
                                            if (PlayerData.BlockFace.get(e.getPlayer()).equals(BlockFace.DOWN) || PlayerData.BlockFace.get(e.getPlayer()).equals(BlockFace.UP)) {
                                                if (PlayerInfo.getFacingDirection(e.getPlayer()).equals("N") || PlayerInfo.getFacingDirection(e.getPlayer()).equals("S")) {
                                                    Block b1 = e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().add(1, 0, 0));
                                                    Block b2 = e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().add(-1, 0, 0));
                                                    if (!b1.getType().equals(Material.AIR) && !b1.getType().equals(Material.CAVE_AIR) && !b1.getType().equals(Material.BEDROCK) && !b1.getType().equals(Material.WATER) && !b1.getType().equals(Material.LAVA)) {
                                                        ((CraftPlayer) e.getPlayer()).getHandle().playerInteractManager.breakBlock(new BlockPosition(b1.getX(), b1.getY(), b1.getZ()));
                                                    }
                                                    if (!b2.getType().equals(Material.AIR) && !b2.getType().equals(Material.CAVE_AIR) && !b2.getType().equals(Material.BEDROCK) && !b2.getType().equals(Material.WATER) && !b2.getType().equals(Material.LAVA)) {
                                                        ((CraftPlayer) e.getPlayer()).getHandle().playerInteractManager.breakBlock(new BlockPosition(b2.getX(), b2.getY(), b2.getZ()));
                                                    }
                                                } else if (PlayerInfo.getFacingDirection(e.getPlayer()).equals("W") || PlayerInfo.getFacingDirection(e.getPlayer()).equals("E")) {
                                                    Block b1 = e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().add(0, 0, 1));
                                                    Block b2 = e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().add(0, 0, -1));
                                                    if (!b1.getType().equals(Material.AIR) && !b1.getType().equals(Material.CAVE_AIR) && !b1.getType().equals(Material.BEDROCK) && !b1.getType().equals(Material.WATER) && !b1.getType().equals(Material.LAVA)) {
                                                        ((CraftPlayer) e.getPlayer()).getHandle().playerInteractManager.breakBlock(new BlockPosition(b1.getX(), b1.getY(), b1.getZ()));
                                                    }
                                                    if (!b2.getType().equals(Material.AIR) && !b2.getType().equals(Material.CAVE_AIR) && !b2.getType().equals(Material.BEDROCK) && !b2.getType().equals(Material.WATER) && !b2.getType().equals(Material.LAVA)) {
                                                        ((CraftPlayer) e.getPlayer()).getHandle().playerInteractManager.breakBlock(new BlockPosition(b2.getX(), b2.getY(), b2.getZ()));
                                                    }
                                                }
                                            } else if (PlayerData.BlockFace.get(e.getPlayer()).equals(BlockFace.NORTH) || PlayerData.BlockFace.get(e.getPlayer()).equals(BlockFace.SOUTH)) {
                                                Block b1 = e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().add(1, 0, 0));
                                                Block b2 = e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().add(-1, 0, 0));
                                                if (!b1.getType().equals(Material.AIR) && !b1.getType().equals(Material.CAVE_AIR) && !b1.getType().equals(Material.BEDROCK) && !b1.getType().equals(Material.WATER) && !b1.getType().equals(Material.LAVA)) {
                                                    ((CraftPlayer) e.getPlayer()).getHandle().playerInteractManager.breakBlock(new BlockPosition(b1.getX(), b1.getY(), b1.getZ()));
                                                }
                                                if (!b2.getType().equals(Material.AIR) && !b2.getType().equals(Material.CAVE_AIR) && !b2.getType().equals(Material.BEDROCK) && !b2.getType().equals(Material.WATER) && !b2.getType().equals(Material.LAVA)) {
                                                    ((CraftPlayer) e.getPlayer()).getHandle().playerInteractManager.breakBlock(new BlockPosition(b2.getX(), b2.getY(), b2.getZ()));
                                                }
                                            } else if (PlayerData.BlockFace.get(e.getPlayer()).equals(BlockFace.WEST) || PlayerData.BlockFace.get(e.getPlayer()).equals(BlockFace.EAST)) {
                                                Block b1 = e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().add(0, 0, 1));
                                                Block b2 = e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().add(0, 0, -1));
                                                if (!b1.getType().equals(Material.AIR) && !b1.getType().equals(Material.CAVE_AIR) && !b1.getType().equals(Material.BEDROCK) && !b1.getType().equals(Material.WATER) && !b1.getType().equals(Material.LAVA)) {
                                                    ((CraftPlayer) e.getPlayer()).getHandle().playerInteractManager.breakBlock(new BlockPosition(b1.getX(), b1.getY(), b1.getZ()));
                                                }
                                                if (!b2.getType().equals(Material.AIR) && !b2.getType().equals(Material.CAVE_AIR) && !b2.getType().equals(Material.BEDROCK) && !b2.getType().equals(Material.WATER) && !b2.getType().equals(Material.LAVA)) {
                                                    ((CraftPlayer) e.getPlayer()).getHandle().playerInteractManager.breakBlock(new BlockPosition(b2.getX(), b2.getY(), b2.getZ()));
                                                }
                                            }
                                            //</editor-fold>
                                        } else if (lore.contains(Strings.POWER + 2)) {
                                            //<editor-fold desc="POWER 2">
                                            PlayerData.hasPower.replace(e.getPlayer(), true);
                                            if (PlayerData.BlockFace.get(e.getPlayer()).equals(BlockFace.DOWN) || PlayerData.BlockFace.get(e.getPlayer()).equals(BlockFace.UP)) {
                                                for (int x = -1; x <= 1; x++) {
                                                    for (int z = -1; z <= 1; z++) {
                                                        if (!(x == 0 && z == 0)) {
                                                            Block b = e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().add(x, 0, z));
                                                            if (!b.getType().equals(Material.AIR) && !b.getType().equals(Material.CAVE_AIR) && !b.getType().equals(Material.BEDROCK) && !b.getType().equals(Material.WATER) && !b.getType().equals(Material.LAVA)) {
                                                                ((CraftPlayer) e.getPlayer()).getHandle().playerInteractManager.breakBlock(new BlockPosition(b.getX(), b.getY(), b.getZ()));
                                                            }
                                                        }
                                                    }
                                                }
                                            } else if (PlayerData.BlockFace.get(e.getPlayer()).equals(BlockFace.NORTH) || PlayerData.BlockFace.get(e.getPlayer()).equals(BlockFace.SOUTH)) {
                                                for (int x = -1; x <= 1; x++) {
                                                    for (int y = -1; y <= 1; y++) {
                                                        if (!(x == 0 && y == 0)) {
                                                            Block b = e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().add(x, y, 0));
                                                            if (!b.getType().equals(Material.AIR) && !b.getType().equals(Material.CAVE_AIR) && !b.getType().equals(Material.BEDROCK) && !b.getType().equals(Material.WATER) && !b.getType().equals(Material.LAVA)) {
                                                                ((CraftPlayer) e.getPlayer()).getHandle().playerInteractManager.breakBlock(new BlockPosition(b.getX(), b.getY(), b.getZ()));
                                                            }
                                                        }
                                                    }
                                                }
                                            } else if (PlayerData.BlockFace.get(e.getPlayer()).equals(BlockFace.EAST) || PlayerData.BlockFace.get(e.getPlayer()).equals(BlockFace.WEST)) {
                                                for (int z = -1; z <= 1; z++) {
                                                    for (int y = -1; y <= 1; y++) {
                                                        if (!(z == 0 && y == 0)) {
                                                            Block b = e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().add(0, y, z));
                                                            if (!b.getType().equals(Material.AIR) && !b.getType().equals(Material.CAVE_AIR) && !b.getType().equals(Material.BEDROCK) && !b.getType().equals(Material.WATER) && !b.getType().equals(Material.LAVA)) {
                                                                ((CraftPlayer) e.getPlayer()).getHandle().playerInteractManager.breakBlock(new BlockPosition(b.getX(), b.getY(), b.getZ()));
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            //</editor-fold>
                                        } else if (lore.contains(Strings.POWER + 3)) {
                                            //<editor-fold desc="POWER 3">
                                            PlayerData.hasPower.replace(e.getPlayer(), true);
                                            if (PlayerData.BlockFace.get(e.getPlayer()).equals(BlockFace.DOWN) || PlayerData.BlockFace.get(e.getPlayer()).equals(BlockFace.UP)) {
                                                for (int x = -2; x <= 2; x++) {
                                                    for (int z = -2; z <= 2; z++) {
                                                        if (!(x == 0 && z == 0)) {
                                                            Block b = e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().add(x, 0, z));
                                                            if (!b.getType().equals(Material.AIR) && !b.getType().equals(Material.CAVE_AIR) && !b.getType().equals(Material.BEDROCK) && !b.getType().equals(Material.WATER) && !b.getType().equals(Material.LAVA)) {
                                                                ((CraftPlayer) e.getPlayer()).getHandle().playerInteractManager.breakBlock(new BlockPosition(b.getX(), b.getY(), b.getZ()));
                                                            }
                                                        }
                                                    }
                                                }
                                            } else if (PlayerData.BlockFace.get(e.getPlayer()).equals(BlockFace.NORTH) || PlayerData.BlockFace.get(e.getPlayer()).equals(BlockFace.SOUTH)) {
                                                for (int x = -2; x <= 2; x++) {
                                                    for (int y = -2; y <= 2; y++) {
                                                        if (!(x == 0 && y == 0)) {
                                                            Block b = e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().add(x, y, 0));
                                                            if (!b.getType().equals(Material.AIR) && !b.getType().equals(Material.CAVE_AIR) && !b.getType().equals(Material.BEDROCK) && !b.getType().equals(Material.WATER) && !b.getType().equals(Material.LAVA)) {
                                                                ((CraftPlayer) e.getPlayer()).getHandle().playerInteractManager.breakBlock(new BlockPosition(b.getX(), b.getY(), b.getZ()));
                                                            }
                                                        }
                                                    }
                                                }
                                            } else if (PlayerData.BlockFace.get(e.getPlayer()).equals(BlockFace.EAST) || PlayerData.BlockFace.get(e.getPlayer()).equals(BlockFace.WEST)) {
                                                for (int z = -2; z <= 2; z++) {
                                                    for (int y = -2; y <= 2; y++) {
                                                        if (!(z == 0 && y == 0)) {
                                                            Block b = e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().add(0, y, z));
                                                            if (!b.getType().equals(Material.AIR) && !b.getType().equals(Material.CAVE_AIR) && !b.getType().equals(Material.BEDROCK) && !b.getType().equals(Material.WATER) && !b.getType().equals(Material.LAVA)) {
                                                                ((CraftPlayer) e.getPlayer()).getHandle().playerInteractManager.breakBlock(new BlockPosition(b.getX(), b.getY(), b.getZ()));
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            //</editor-fold>
                                        }
                                        PlayerData.hasPower.replace(e.getPlayer(), false);
                                    }
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
                        norm.equals(Modifiers.AUTOSMELT_MODIFIER) ||
                        norm.equals(Modifiers.BEHEADING_MODIFIER)) {
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
                        if (e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.COBWEB) && !e.getPlayer().isSneaking()) {
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
                    if (Main.getPlugin().getConfig().getBoolean("Modifiers.Power.allowed")) {
                        //<editor-fold desc="POWER">
                        if (e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.EMERALD) && !e.getPlayer().isSneaking()) {
                            if (e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
                                e.getPlayer().getLocation().getWorld().dropItemNaturally(e.getPlayer().getLocation(), Modifiers.POWER_MODIFIER);
                                if (Main.getPlugin().getConfig().getBoolean("Sound.OnEnchanting")) {
                                    e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 0.5F);
                                }
                                ChatWriter.log(false, e.getPlayer().getDisplayName() + " created a Power-Modifier in Creative!");
                            } else if (e.getPlayer().getLevel() >= Main.getPlugin().getConfig().getInt("Modifiers.Power.EnchantCost")) {
                                int amount = e.getPlayer().getInventory().getItemInMainHand().getAmount();
                                int newLevel = e.getPlayer().getLevel() - Main.getPlugin().getConfig().getInt("Modifiers.Power.EnchantCost");
                                e.getPlayer().setLevel(newLevel);
                                e.getPlayer().getInventory().getItemInMainHand().setAmount(amount - 1);
                                e.getPlayer().getLocation().getWorld().dropItemNaturally(e.getPlayer().getLocation(), Modifiers.POWER_MODIFIER);
                                if (Main.getPlugin().getConfig().getBoolean("Sound.OnEnchanting")) {
                                    e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 0.5F);
                                }
                                ChatWriter.log(false, e.getPlayer().getDisplayName() + " created a Power-Modifier!");
                            } else {
                                ChatWriter.sendMessage(e.getPlayer(), ChatColor.RED, "You do not have enough Levels to perform this action!");
                                ChatWriter.sendMessage(e.getPlayer(), ChatColor.RED, Main.getPlugin().getConfig().getInt("Modifiers.Power.EnchantCost") + " levels are required!");
                                ChatWriter.log(false,  e.getPlayer().getDisplayName() + " tried to create a Power-Modifier but had not enough levels!");
                            }
                            e.setCancelled(true);
                        }
                        //</editor-fold>
                    }
                    if (Main.getPlugin().getConfig().getBoolean("Modifiers.Beheading.allowed")) {
                        //<editor-fold desc="POWER">
                        if (e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.WITHER_SKELETON_SKULL) && !e.getPlayer().isSneaking()) {
                            if (e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
                                e.getPlayer().getLocation().getWorld().dropItemNaturally(e.getPlayer().getLocation(), Modifiers.BEHEADING_MODIFIER);
                                if (Main.getPlugin().getConfig().getBoolean("Sound.OnEnchanting")) {
                                    e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 0.5F);
                                }
                                ChatWriter.log(false, e.getPlayer().getDisplayName() + " created a Beheading-Modifier in Creative!");
                            } else if (e.getPlayer().getLevel() >= Main.getPlugin().getConfig().getInt("Modifiers.Beheading.EnchantCost")) {
                                int amount = e.getPlayer().getInventory().getItemInMainHand().getAmount();
                                int newLevel = e.getPlayer().getLevel() - Main.getPlugin().getConfig().getInt("Modifiers.Beheading.EnchantCost");
                                e.getPlayer().setLevel(newLevel);
                                e.getPlayer().getInventory().getItemInMainHand().setAmount(amount - 1);
                                e.getPlayer().getLocation().getWorld().dropItemNaturally(e.getPlayer().getLocation(), Modifiers.BEHEADING_MODIFIER);
                                if (Main.getPlugin().getConfig().getBoolean("Sound.OnEnchanting")) {
                                    e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 0.5F);
                                }
                                ChatWriter.log(false, e.getPlayer().getDisplayName() + " created a Beheading-Modifier!");
                            } else {
                                ChatWriter.sendMessage(e.getPlayer(), ChatColor.RED, "You do not have enough Levels to perform this action!");
                                ChatWriter.sendMessage(e.getPlayer(), ChatColor.RED, Main.getPlugin().getConfig().getInt("Modifiers.Beheading.EnchantCost") + " levels are required!");
                                ChatWriter.log(false,  e.getPlayer().getDisplayName() + " tried to create a Beheading-Modifier but had not enough levels!");
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

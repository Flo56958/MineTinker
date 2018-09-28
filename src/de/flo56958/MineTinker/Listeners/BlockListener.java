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
import net.minecraft.server.v1_13_R2.BlockPosition;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Random;

public class BlockListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (!e.isCancelled()) {
            Player p = e.getPlayer();
            if (!Lists.WORLDS.contains(p.getWorld().getName())) { return; }
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
                    e.getBlock().getType().equals(Material.POPPY) ||
                    e.getBlock().getType().equals(Material.OXEYE_DAISY) ||
                    e.getBlock().getType().equals(Material.AZURE_BLUET) ||
                    e.getBlock().getType().equals(Material.TORCH))) {
                if (p.getGameMode().equals(GameMode.SURVIVAL) || p.getGameMode().equals(GameMode.ADVENTURE)) {
                    ItemStack tool = p.getInventory().getItemInMainHand();
                    ItemMeta meta = tool.getItemMeta();
                    if (!tool.getType().equals(Material.AIR)) {
                        if (meta.hasLore()) {
                            ArrayList<String> lore = (ArrayList<String>) meta.getLore();
                            if (lore.contains(Strings.IDENTIFIER)) {
                                if (tool.getType().getMaxDurability() - ((Damageable) meta).getDamage() <= 1) {
                                    e.setCancelled(true);
                                    if (Main.getPlugin().getConfig().getBoolean("Sound.OnBreaking")) {
                                        p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.5F, 0.5F);
                                    }
                                    return;
                                }
                                if (PlayerData.HASPOWER.get(e.getPlayer()) && !e.isDropItems()) {
                                    e.setCancelled(true);
                                    return;
                                }
                                LevelCalculator.addExp(e.getPlayer(), tool, Main.getPlugin().getConfig().getInt("ExpPerBlockBreak"));
                                if (Main.getPlugin().getConfig().getBoolean("Modifiers.Self-Repair.allowed") && p.hasPermission("minetinker.modifiers.selfrepair.use")) {
                                    for (int i = 1; i <= Main.getPlugin().getConfig().getInt("Modifiers.Self-Repair.MaxLevel"); i++) {
                                        if (lore.contains(Strings.SELFREPAIR + i)) {
                                            Random rand = new Random();
                                            int n = rand.nextInt(100);
                                            if (n <= Main.getPlugin().getConfig().getInt("Modifiers.Self-Repair.PercentagePerLevel") * i) {
                                                int heal = Main.getPlugin().getConfig().getInt("Modifiers.Self-Repair.HealthRepair");
                                                short dura = (short) (tool.getDurability() - heal);
                                                if (dura < 0) {
                                                    dura = 0;
                                                }
                                                p.getInventory().getItemInMainHand().setDurability(dura);
                                                ChatWriter.log(false, p.getDisplayName() + " triggered Self-Repair on " + ItemGenerator.getDisplayName(tool) + ChatColor.WHITE + " (" + tool.getType().toString() + ")!");
                                            }
                                            break;
                                        }
                                    }
                                }
                                if ((Main.getPlugin().getConfig().getBoolean("Modifiers.Silk-Touch.allowed") && Main.getPlugin().getConfig().getBoolean("Spawners.dropable") && Main.getPlugin().getConfig().getBoolean("Spawners.onlyWithSilkTouch"))
                                        || (Main.getPlugin().getConfig().getBoolean("Spawners.dropable") && !Main.getPlugin().getConfig().getBoolean("Spawners.onlyWithSilkTouch"))) {
                                    if (e.getBlock().getType().equals(Material.SPAWNER) && p.hasPermission("minetinker.spawners.mine")) {
                                        CreatureSpawner cs = (CreatureSpawner) e.getBlock().getState();
                                        ItemStack s = new ItemStack(Material.SPAWNER, 1, e.getBlock().getData());
                                        ItemMeta s_meta = s.getItemMeta();
                                        s_meta.setDisplayName(cs.getSpawnedType().toString());
                                        s.setItemMeta(s_meta);
                                        p.getWorld().dropItemNaturally(e.getBlock().getLocation(), s);
                                        e.setExpToDrop(0);
                                        ChatWriter.log(false, p.getDisplayName() + " successfully mined a Spawner!");
                                    }
                                }
                                if (Main.getPlugin().getConfig().getBoolean("Modifiers.XP.allowed") && p.hasPermission("minetinker.modifiers.xp.use")) {
                                    for (int i = 1; i <= Main.getPlugin().getConfig().getInt("Modifiers.XP.MaxLevel"); i++) {
                                        if (lore.contains(Strings.XP + i)) {
                                            //self-repair
                                            Random rand = new Random();
                                            int n = rand.nextInt(100);
                                            if (n <= Main.getPlugin().getConfig().getInt("Modifiers.XP.PercentagePerLevel") * i) {
                                                e.setExpToDrop(e.getExpToDrop() + Main.getPlugin().getConfig().getInt("Modifiers.XP.XPAmount"));
                                                ChatWriter.log(false, p.getDisplayName() + " triggered XP on " + ItemGenerator.getDisplayName(tool) + ChatColor.WHITE + " (" + tool.getType().toString() + ")!");
                                            }
                                            break;
                                        }
                                    }
                                }
                                if (Main.getPlugin().getConfig().getBoolean("Modifiers.Auto-Smelt.allowed") && p.hasPermission("minetinker.modifiers.autosmelt.use")) {
                                    boolean goodBlock = false;
                                    boolean luck = false;
                                    Material loot = Material.AIR;
                                    switch (e.getBlock().getType()) {
                                        case COBBLESTONE:
                                            goodBlock = true;
                                            loot = Material.STONE;
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
                                        case STRIPPED_ACACIA_LOG:
                                        case STRIPPED_BIRCH_LOG:
                                        case STRIPPED_DARK_OAK_LOG:
                                        case STRIPPED_JUNGLE_LOG:
                                        case STRIPPED_OAK_LOG:
                                        case STRIPPED_SPRUCE_LOG:
                                        case ACACIA_WOOD:
                                        case BIRCH_WOOD:
                                        case DARK_OAK_WOOD:
                                        case JUNGLE_WOOD:
                                        case OAK_WOOD:
                                        case SPRUCE_WOOD:
                                        case STRIPPED_ACACIA_WOOD:
                                        case STRIPPED_BIRCH_WOOD:
                                        case STRIPPED_DARK_OAK_WOOD:
                                        case STRIPPED_JUNGLE_WOOD:
                                        case STRIPPED_OAK_WOOD:
                                        case STRIPPED_SPRUCE_WOOD:
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
                                        case KELP_PLANT:
                                            goodBlock = true;
                                            loot = Material.DRIED_KELP;
                                            break;
                                        case WET_SPONGE:
                                            goodBlock = true;
                                            loot = Material.SPONGE;
                                            break;
                                    }
                                    if (goodBlock) {
                                        for (int i = 1; i <= Main.getPlugin().getConfig().getInt("Modifiers.Auto-Smelt.MaxLevel"); i++) {
                                            if (lore.contains(Strings.AUTOSMELT + i)) {
                                                Random rand = new Random();
                                                int n = rand.nextInt(100);
                                                if (n <= Main.getPlugin().getConfig().getInt("Modifiers.Auto-Smelt.PercentagePerLevel") * i) {
                                                    int amount = 1;
                                                    if (Main.getPlugin().getConfig().getBoolean("Modifiers.Luck.allowed") && luck) {
                                                        for (int j = 0; j <= Main.getPlugin().getConfig().getInt("Modifiers.Luck.MaxLevel"); j++) {
                                                            if (lore.contains(Strings.LUCK + j)) {
                                                                amount = amount + rand.nextInt(j);
                                                                break;
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
                                                    ChatWriter.log(false, p.getDisplayName() + " triggered Auto-Smelt on " + ItemGenerator.getDisplayName(tool) + ChatColor.WHITE + " (" + tool.getType().toString() + ") while mining " + e.getBlock().getType().toString() + "!");
                                                }
                                                break;
                                            }
                                        }
                                    }
                                }
                                if (Main.getPlugin().getConfig().getBoolean("Modifiers.Power.allowed") && p.hasPermission("minetinker.modifiers.power.use")) {
                                    if (!PlayerData.HASPOWER.get(e.getPlayer()) && !p.isSneaking()) {
                                        if (lore.contains(Strings.POWER + 1)) {
                                            PlayerData.HASPOWER.replace(e.getPlayer(), true);
                                            if (PlayerData.BLOCKFACE.get(e.getPlayer()).equals(BlockFace.DOWN) || PlayerData.BLOCKFACE.get(e.getPlayer()).equals(BlockFace.UP)) {
                                                if (PlayerInfo.getFacingDirection(e.getPlayer()).equals("N") || PlayerInfo.getFacingDirection(e.getPlayer()).equals("S")) {
                                                    Block b1 = e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().add(1, 0, 0));
                                                    Block b2 = e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().add(-1, 0, 0));
                                                    if (!b1.getType().equals(Material.AIR) && !b1.getType().equals(Material.CAVE_AIR) && !b1.getType().equals(Material.BEDROCK) && !b1.getType().equals(Material.WATER) && !b1.getType().equals(Material.BUBBLE_COLUMN) && !b1.getType().equals(Material.LAVA)) {
                                                        ((CraftPlayer) e.getPlayer()).getHandle().playerInteractManager.breakBlock(new BlockPosition(b1.getX(), b1.getY(), b1.getZ()));
                                                    }
                                                    if (!b2.getType().equals(Material.AIR) && !b2.getType().equals(Material.CAVE_AIR) && !b2.getType().equals(Material.BEDROCK) && !b2.getType().equals(Material.WATER) && !b2.getType().equals(Material.BUBBLE_COLUMN) && !b2.getType().equals(Material.LAVA)) {
                                                        ((CraftPlayer) e.getPlayer()).getHandle().playerInteractManager.breakBlock(new BlockPosition(b2.getX(), b2.getY(), b2.getZ()));
                                                    }
                                                } else if (PlayerInfo.getFacingDirection(e.getPlayer()).equals("W") || PlayerInfo.getFacingDirection(e.getPlayer()).equals("E")) {
                                                    Block b1 = e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().add(0, 0, 1));
                                                    Block b2 = e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().add(0, 0, -1));
                                                    if (!b1.getType().equals(Material.AIR) && !b1.getType().equals(Material.CAVE_AIR) && !b1.getType().equals(Material.BEDROCK) && !b1.getType().equals(Material.WATER) && !b1.getType().equals(Material.BUBBLE_COLUMN) && !b1.getType().equals(Material.LAVA)) {
                                                        ((CraftPlayer) e.getPlayer()).getHandle().playerInteractManager.breakBlock(new BlockPosition(b1.getX(), b1.getY(), b1.getZ()));
                                                    }
                                                    if (!b2.getType().equals(Material.AIR) && !b2.getType().equals(Material.CAVE_AIR) && !b2.getType().equals(Material.BEDROCK) && !b2.getType().equals(Material.WATER) && !b2.getType().equals(Material.BUBBLE_COLUMN) && !b2.getType().equals(Material.LAVA)) {
                                                        ((CraftPlayer) e.getPlayer()).getHandle().playerInteractManager.breakBlock(new BlockPosition(b2.getX(), b2.getY(), b2.getZ()));
                                                    }
                                                }
                                            } else if (PlayerData.BLOCKFACE.get(e.getPlayer()).equals(BlockFace.NORTH) || PlayerData.BLOCKFACE.get(e.getPlayer()).equals(BlockFace.SOUTH)) {
                                                Block b1 = e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().add(1, 0, 0));
                                                Block b2 = e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().add(-1, 0, 0));
                                                if (!b1.getType().equals(Material.AIR) && !b1.getType().equals(Material.CAVE_AIR) && !b1.getType().equals(Material.BEDROCK) && !b1.getType().equals(Material.WATER) && !b1.getType().equals(Material.BUBBLE_COLUMN) && !b1.getType().equals(Material.LAVA)) {
                                                    ((CraftPlayer) e.getPlayer()).getHandle().playerInteractManager.breakBlock(new BlockPosition(b1.getX(), b1.getY(), b1.getZ()));
                                                }
                                                if (!b2.getType().equals(Material.AIR) && !b2.getType().equals(Material.CAVE_AIR) && !b2.getType().equals(Material.BEDROCK) && !b2.getType().equals(Material.WATER) && !b2.getType().equals(Material.BUBBLE_COLUMN) && !b2.getType().equals(Material.LAVA)) {
                                                    ((CraftPlayer) e.getPlayer()).getHandle().playerInteractManager.breakBlock(new BlockPosition(b2.getX(), b2.getY(), b2.getZ()));
                                                }
                                            } else if (PlayerData.BLOCKFACE.get(e.getPlayer()).equals(BlockFace.WEST) || PlayerData.BLOCKFACE.get(e.getPlayer()).equals(BlockFace.EAST)) {
                                                Block b1 = e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().add(0, 0, 1));
                                                Block b2 = e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().add(0, 0, -1));
                                                if (!b1.getType().equals(Material.AIR) && !b1.getType().equals(Material.CAVE_AIR) && !b1.getType().equals(Material.BEDROCK) && !b1.getType().equals(Material.WATER) && !b1.getType().equals(Material.BUBBLE_COLUMN) && !b1.getType().equals(Material.LAVA)) {
                                                    ((CraftPlayer) e.getPlayer()).getHandle().playerInteractManager.breakBlock(new BlockPosition(b1.getX(), b1.getY(), b1.getZ()));
                                                }
                                                if (!b2.getType().equals(Material.AIR) && !b2.getType().equals(Material.CAVE_AIR) && !b2.getType().equals(Material.BEDROCK) && !b2.getType().equals(Material.WATER) && !b2.getType().equals(Material.BUBBLE_COLUMN) && !b2.getType().equals(Material.LAVA)) {
                                                    ((CraftPlayer) e.getPlayer()).getHandle().playerInteractManager.breakBlock(new BlockPosition(b2.getX(), b2.getY(), b2.getZ()));
                                                }
                                            }
                                        }
                                        if (!PlayerData.HASPOWER.get(e.getPlayer())) {
                                            for (int level = 2; level <= Main.getPlugin().getConfig().getInt("Modifiers.Power.MaxLevel"); level++) {
                                                if (lore.contains(Strings.POWER + level)) {
                                                    PlayerData.HASPOWER.replace(e.getPlayer(), true);
                                                    if (PlayerData.BLOCKFACE.get(e.getPlayer()).equals(BlockFace.DOWN) || PlayerData.BLOCKFACE.get(e.getPlayer()).equals(BlockFace.UP)) {
                                                        for (int x = -(level - 1); x <= (level - 1); x++) {
                                                            for (int z = -(level - 1); z <= (level - 1); z++) {
                                                                if (!(x == 0 && z == 0)) {
                                                                    Block b = e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().add(x, 0, z));
                                                                    if (!b.getType().equals(Material.AIR) && !b.getType().equals(Material.CAVE_AIR) && !b.getType().equals(Material.BEDROCK) && !b.getType().equals(Material.WATER) && !b.getType().equals(Material.BUBBLE_COLUMN) && !b.getType().equals(Material.LAVA)) {
                                                                        ((CraftPlayer) e.getPlayer()).getHandle().playerInteractManager.breakBlock(new BlockPosition(b.getX(), b.getY(), b.getZ()));
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    } else if (PlayerData.BLOCKFACE.get(e.getPlayer()).equals(BlockFace.NORTH) || PlayerData.BLOCKFACE.get(e.getPlayer()).equals(BlockFace.SOUTH)) {
                                                        for (int x = -(level - 1); x <= (level - 1); x++) {
                                                            for (int y = -(level - 1); y <= (level - 1); y++) {
                                                                if (!(x == 0 && y == 0)) {
                                                                    Block b = e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().add(x, y, 0));
                                                                    if (!b.getType().equals(Material.AIR) && !b.getType().equals(Material.CAVE_AIR) && !b.getType().equals(Material.BEDROCK) && !b.getType().equals(Material.WATER) && !b.getType().equals(Material.BUBBLE_COLUMN) && !b.getType().equals(Material.LAVA)) {
                                                                        ((CraftPlayer) e.getPlayer()).getHandle().playerInteractManager.breakBlock(new BlockPosition(b.getX(), b.getY(), b.getZ()));
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    } else if (PlayerData.BLOCKFACE.get(e.getPlayer()).equals(BlockFace.EAST) || PlayerData.BLOCKFACE.get(e.getPlayer()).equals(BlockFace.WEST)) {
                                                        for (int z = -(level - 1); z <= (level - 1); z++) {
                                                            for (int y = -(level - 1); y <= (level - 1); y++) {
                                                                if (!(z == 0 && y == 0)) {
                                                                    Block b = e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().add(0, y, z));
                                                                    if (!b.getType().equals(Material.AIR) && !b.getType().equals(Material.CAVE_AIR) && !b.getType().equals(Material.BEDROCK) && !b.getType().equals(Material.WATER) && !b.getType().equals(Material.BUBBLE_COLUMN) && !b.getType().equals(Material.LAVA)) {
                                                                        ((CraftPlayer) e.getPlayer()).getHandle().playerInteractManager.breakBlock(new BlockPosition(b.getX(), b.getY(), b.getZ()));
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        PlayerData.HASPOWER.replace(e.getPlayer(), false);
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
            Player p = e.getPlayer();
            ItemStack norm = p.getInventory().getItemInMainHand();
            if (e.getAction().equals(Action.RIGHT_CLICK_AIR) && (norm.equals(Modifiers.ENDER_MODIFIER))) {
                e.setCancelled(true);
                return;
            }
            if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                int temp = norm.getAmount();
                norm.setAmount(1);
                if (norm.equals(Modifiers.LUCK_MODIFIER) ||
                        norm.equals(Modifiers.SHARPNESS_MODIFIER) ||
                        norm.equals(Modifiers.HASTE_MODIFIER) ||
                        norm.equals(Modifiers.REINFORCED_MODIFIER) ||
                        norm.equals(Modifiers.SELFREPAIR_MODIFIER) ||
                        norm.equals(Modifiers.SILKTOUCH_MODIFIER) ||
                        norm.equals(Modifiers.AUTOSMELT_MODIFIER) ||
                        norm.equals(Modifiers.BEHEADING_MODIFIER) ||
                        norm.equals(Modifiers.ENDER_MODIFIER) ||
                        norm.equals(Modifiers.GLOWING_MODIFIER) ||
                        norm.equals(Modifiers.KNOCKBACK_MODIFIER)) {
                    norm.setAmount(temp);
                    e.setCancelled(true);
                    return;
                }
                norm.setAmount(temp);
                if (e.getClickedBlock().getType().equals(Material.BOOKSHELF)) {
                    if (Main.getPlugin().getConfig().getBoolean("Modifiers.Self-Repair.allowed") && p.hasPermission("minetinker.modifiers.selfrepair.craft")) {
                        //<editor-fold desc="SELF-REPAIR">
                        if (p.getInventory().getItemInMainHand().getType().equals(Material.MOSSY_COBBLESTONE) && !p.isSneaking()) {
                            if (p.getGameMode().equals(GameMode.CREATIVE)) {
                                p.getLocation().getWorld().dropItemNaturally(p.getLocation(), Modifiers.SELFREPAIR_MODIFIER);
                                if (Main.getPlugin().getConfig().getBoolean("Sound.OnEnchanting")) {
                                    p.playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 0.5F);
                                }
                                ChatWriter.log(false, p.getDisplayName() + " created a Self-Repair-Modifier in Creative!");
                            } else if (p.getLevel() >= Main.getPlugin().getConfig().getInt("Modifiers.Self-Repair.EnchantCost")) {
                                int amount = p.getInventory().getItemInMainHand().getAmount();
                                int newLevel = p.getLevel() - Main.getPlugin().getConfig().getInt("Modifiers.Self-Repair.EnchantCost");
                                p.setLevel(newLevel);
                                p.getInventory().getItemInMainHand().setAmount(amount - 1);
                                p.getLocation().getWorld().dropItemNaturally(p.getLocation(), Modifiers.SELFREPAIR_MODIFIER);
                                if (Main.getPlugin().getConfig().getBoolean("Sound.OnEnchanting")) {
                                    p.playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 0.5F);
                                }
                                ChatWriter.log(false, p.getDisplayName() + " created a Self-Repair-Modifier!");
                            } else {
                                ChatWriter.sendMessage(e.getPlayer(), ChatColor.RED, "You do not have enough Levels to perform this action!");
                                ChatWriter.sendMessage(e.getPlayer(), ChatColor.RED, Main.getPlugin().getConfig().getInt("Modifiers.Self-Repair.EnchantCost") + " levels are required!");
                                ChatWriter.log(false,  p.getDisplayName() + " tried to create a Self-Repair-Modifier but had not enough levels!");
                            }
                            e.setCancelled(true);
                        }
                        //</editor-fold>
                    }
                    if (Main.getPlugin().getConfig().getBoolean("Modifiers.Knockback.allowed") && p.hasPermission("minetinker.modifiers.knockback.craft")) {
                        //<editor-fold desc="SELF-REPAIR">
                        if (p.getInventory().getItemInMainHand().getType().equals(Material.TNT) && !p.isSneaking()) {
                            if (p.getGameMode().equals(GameMode.CREATIVE)) {
                                p.getLocation().getWorld().dropItemNaturally(p.getLocation(), Modifiers.KNOCKBACK_MODIFIER);
                                if (Main.getPlugin().getConfig().getBoolean("Sound.OnEnchanting")) {
                                    p.playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 0.5F);
                                }
                                ChatWriter.log(false, p.getDisplayName() + " created a Knockback-Modifier in Creative!");
                            } else if (p.getLevel() >= Main.getPlugin().getConfig().getInt("Modifiers.Knockback.EnchantCost")) {
                                int amount = p.getInventory().getItemInMainHand().getAmount();
                                int newLevel = p.getLevel() - Main.getPlugin().getConfig().getInt("Modifiers.Knockback.EnchantCost");
                                p.setLevel(newLevel);
                                p.getInventory().getItemInMainHand().setAmount(amount - 1);
                                p.getLocation().getWorld().dropItemNaturally(p.getLocation(), Modifiers.KNOCKBACK_MODIFIER);
                                if (Main.getPlugin().getConfig().getBoolean("Sound.OnEnchanting")) {
                                    p.playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 0.5F);
                                }
                                ChatWriter.log(false, p.getDisplayName() + " created a Knockback-Modifier!");
                            } else {
                                ChatWriter.sendMessage(e.getPlayer(), ChatColor.RED, "You do not have enough Levels to perform this action!");
                                ChatWriter.sendMessage(e.getPlayer(), ChatColor.RED, Main.getPlugin().getConfig().getInt("Modifiers.Knockback.EnchantCost") + " levels are required!");
                                ChatWriter.log(false,  p.getDisplayName() + " tried to create a Knockback-Modifier but had not enough levels!");
                            }
                            e.setCancelled(true);
                        }
                        //</editor-fold>
                    }
                    if (Main.getPlugin().getConfig().getBoolean("Modifiers.Silk-Touch.allowed") && p.hasPermission("minetinker.modifiers.silktouch.craft")) {
                        //<editor-fold desc="SILK-TOUCH">
                        if (p.getInventory().getItemInMainHand().getType().equals(Material.COBWEB) && !p.isSneaking()) {
                            if (p.getGameMode().equals(GameMode.CREATIVE)) {
                                p.getLocation().getWorld().dropItemNaturally(p.getLocation(), Modifiers.SILKTOUCH_MODIFIER);
                                if (Main.getPlugin().getConfig().getBoolean("Sound.OnEnchanting")) {
                                    p.playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 0.5F);
                                }
                                ChatWriter.log(false, p.getDisplayName() + " created a Silk-Touch-Modifier in Creative!");
                            } else if (p.getLevel() >= Main.getPlugin().getConfig().getInt("Modifiers.Silk-Touch.EnchantCost")) {
                                int amount = p.getInventory().getItemInMainHand().getAmount();
                                int newLevel = p.getLevel() - Main.getPlugin().getConfig().getInt("Modifiers.Silk-Touch.EnchantCost");
                                p.setLevel(newLevel);
                                p.getInventory().getItemInMainHand().setAmount(amount - 1);
                                p.getLocation().getWorld().dropItemNaturally(p.getLocation(), Modifiers.SILKTOUCH_MODIFIER);
                                if (Main.getPlugin().getConfig().getBoolean("Sound.OnEnchanting")) {
                                    p.playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 0.5F);
                                }
                                ChatWriter.log(false, p.getDisplayName() + " created a Silk-Touch-Modifier!");
                            } else {
                                ChatWriter.sendMessage(e.getPlayer(), ChatColor.RED, "You do not have enough Levels to perform this action!");
                                ChatWriter.sendMessage(e.getPlayer(), ChatColor.RED, Main.getPlugin().getConfig().getInt("Modifiers.Silk-Touch.EnchantCost") + " levels are required!");
                                ChatWriter.log(false,  p.getDisplayName() + " tried to create a Silk-Touch-Modifier but had not enough levels!");
                            }
                            e.setCancelled(true);
                        }
                        //</editor-fold>
                    }
                    if (Main.getPlugin().getConfig().getBoolean("Modifiers.Fiery.allowed") && p.hasPermission("minetinker.modifiers.fiery.craft")) {
                        //<editor-fold desc="FIERY">
                        if (p.getInventory().getItemInMainHand().getType().equals(Material.BLAZE_ROD) && !p.isSneaking()) {
                            if (p.getGameMode().equals(GameMode.CREATIVE)) {
                                p.getLocation().getWorld().dropItemNaturally(p.getLocation(), Modifiers.FIERY_MODIFIER);
                                if (Main.getPlugin().getConfig().getBoolean("Sound.OnEnchanting")) {
                                    p.playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 0.5F);
                                }
                                ChatWriter.log(false, p.getDisplayName() + " created a Fiery-Modifier in Creative!");
                            } else if (p.getLevel() >= Main.getPlugin().getConfig().getInt("Modifiers.Fiery.EnchantCost")) {
                                int amount = p.getInventory().getItemInMainHand().getAmount();
                                int newLevel = p.getLevel() - Main.getPlugin().getConfig().getInt("Modifiers.Fiery.EnchantCost");
                                p.setLevel(newLevel);
                                p.getInventory().getItemInMainHand().setAmount(amount - 1);
                                p.getLocation().getWorld().dropItemNaturally(p.getLocation(), Modifiers.FIERY_MODIFIER);
                                if (Main.getPlugin().getConfig().getBoolean("Sound.OnEnchanting")) {
                                    p.playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 0.5F);
                                }
                                ChatWriter.log(false, p.getDisplayName() + " created a Fiery-Modifier!");
                            } else {
                                ChatWriter.sendMessage(e.getPlayer(), ChatColor.RED, "You do not have enough Levels to perform this action!");
                                ChatWriter.sendMessage(e.getPlayer(), ChatColor.RED, Main.getPlugin().getConfig().getInt("Modifiers.Fiery.EnchantCost") + " levels are required!");
                                ChatWriter.log(false,  p.getDisplayName() + " tried to create a Fiery-Modifier but had not enough levels!");
                            }
                            e.setCancelled(true);
                        }
                        //</editor-fold>
                    }
                    if (Main.getPlugin().getConfig().getBoolean("Modifiers.Power.allowed") && p.hasPermission("minetinker.modifiers.power.craft")) {
                        //<editor-fold desc="POWER">
                        if (p.getInventory().getItemInMainHand().getType().equals(Material.EMERALD) && !p.isSneaking()) {
                            if (p.getGameMode().equals(GameMode.CREATIVE)) {
                                p.getLocation().getWorld().dropItemNaturally(p.getLocation(), Modifiers.POWER_MODIFIER);
                                if (Main.getPlugin().getConfig().getBoolean("Sound.OnEnchanting")) {
                                    p.playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 0.5F);
                                }
                                ChatWriter.log(false, p.getDisplayName() + " created a Power-Modifier in Creative!");
                            } else if (p.getLevel() >= Main.getPlugin().getConfig().getInt("Modifiers.Power.EnchantCost")) {
                                int amount = p.getInventory().getItemInMainHand().getAmount();
                                int newLevel = p.getLevel() - Main.getPlugin().getConfig().getInt("Modifiers.Power.EnchantCost");
                                p.setLevel(newLevel);
                                p.getInventory().getItemInMainHand().setAmount(amount - 1);
                                p.getLocation().getWorld().dropItemNaturally(p.getLocation(), Modifiers.POWER_MODIFIER);
                                if (Main.getPlugin().getConfig().getBoolean("Sound.OnEnchanting")) {
                                    p.playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 0.5F);
                                }
                                ChatWriter.log(false, p.getDisplayName() + " created a Power-Modifier!");
                            } else {
                                ChatWriter.sendMessage(e.getPlayer(), ChatColor.RED, "You do not have enough Levels to perform this action!");
                                ChatWriter.sendMessage(e.getPlayer(), ChatColor.RED, Main.getPlugin().getConfig().getInt("Modifiers.Power.EnchantCost") + " levels are required!");
                                ChatWriter.log(false,  p.getDisplayName() + " tried to create a Power-Modifier but had not enough levels!");
                            }
                            e.setCancelled(true);
                        }
                        //</editor-fold>
                    }
                    if (Main.getPlugin().getConfig().getBoolean("Modifiers.Beheading.allowed") && p.hasPermission("minetinker.modifiers.beheading.craft")) {
                        //<editor-fold desc="BEHEADING">
                        if (p.getInventory().getItemInMainHand().getType().equals(Material.WITHER_SKELETON_SKULL) && !p.isSneaking()) {
                            if (p.getGameMode().equals(GameMode.CREATIVE)) {
                                p.getLocation().getWorld().dropItemNaturally(p.getLocation(), Modifiers.BEHEADING_MODIFIER);
                                if (Main.getPlugin().getConfig().getBoolean("Sound.OnEnchanting")) {
                                    p.playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 0.5F);
                                }
                                ChatWriter.log(false, p.getDisplayName() + " created a Beheading-Modifier in Creative!");
                            } else if (p.getLevel() >= Main.getPlugin().getConfig().getInt("Modifiers.Beheading.EnchantCost")) {
                                int amount = p.getInventory().getItemInMainHand().getAmount();
                                int newLevel = p.getLevel() - Main.getPlugin().getConfig().getInt("Modifiers.Beheading.EnchantCost");
                                p.setLevel(newLevel);
                                p.getInventory().getItemInMainHand().setAmount(amount - 1);
                                p.getLocation().getWorld().dropItemNaturally(p.getLocation(), Modifiers.BEHEADING_MODIFIER);
                                if (Main.getPlugin().getConfig().getBoolean("Sound.OnEnchanting")) {
                                    p.playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 0.5F);
                                }
                                ChatWriter.log(false, p.getDisplayName() + " created a Beheading-Modifier!");
                            } else {
                                ChatWriter.sendMessage(e.getPlayer(), ChatColor.RED, "You do not have enough Levels to perform this action!");
                                ChatWriter.sendMessage(e.getPlayer(), ChatColor.RED, Main.getPlugin().getConfig().getInt("Modifiers.Beheading.EnchantCost") + " levels are required!");
                                ChatWriter.log(false,  p.getDisplayName() + " tried to create a Beheading-Modifier but had not enough levels!");
                            }
                            e.setCancelled(true);
                        }
                        //</editor-fold>
                    }
                    if (Main.getPlugin().getConfig().getBoolean("Modifiers.Infinity.allowed") && p.hasPermission("minetinker.modifiers.infinity.craft")) {
                        //<editor-fold desc="INFINITY">
                        if (p.getInventory().getItemInMainHand().getType().equals(Material.ARROW) && !p.isSneaking()) {
                            if (p.getGameMode().equals(GameMode.CREATIVE)) {
                                p.getLocation().getWorld().dropItemNaturally(p.getLocation(), Modifiers.INFINITY_MODIFIER);
                                if (Main.getPlugin().getConfig().getBoolean("Sound.OnEnchanting")) {
                                    p.playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 0.5F);
                                }
                                ChatWriter.log(false, p.getDisplayName() + " created a Infinity-Modifier in Creative!");
                            } else if (p.getLevel() >= Main.getPlugin().getConfig().getInt("Modifiers.Infinity.EnchantCost")) {
                                int amount = p.getInventory().getItemInMainHand().getAmount();
                                int newLevel = p.getLevel() - Main.getPlugin().getConfig().getInt("Modifiers.Infinity.EnchantCost");
                                p.setLevel(newLevel);
                                p.getInventory().getItemInMainHand().setAmount(amount - 1);
                                p.getLocation().getWorld().dropItemNaturally(p.getLocation(), Modifiers.INFINITY_MODIFIER);
                                if (Main.getPlugin().getConfig().getBoolean("Sound.OnEnchanting")) {
                                    p.playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 0.5F);
                                }
                                ChatWriter.log(false, p.getDisplayName() + " created a Infinity-Modifier!");
                            } else {
                                ChatWriter.sendMessage(e.getPlayer(), ChatColor.RED, "You do not have enough Levels to perform this action!");
                                ChatWriter.sendMessage(e.getPlayer(), ChatColor.RED, Main.getPlugin().getConfig().getInt("Modifiers.Infinity.EnchantCost") + " levels are required!");
                                ChatWriter.log(false,  p.getDisplayName() + " tried to create a Infinity-Modifier but had not enough levels!");
                            }
                            e.setCancelled(true);
                        }
                        //</editor-fold>
                    }
                    if (Main.getPlugin().getConfig().getBoolean("Modifiers.Poisonous.allowed") && p.hasPermission("minetinker.modifiers.poisonous.craft")) {
                        //<editor-fold desc="POISONOUS">
                        if (p.getInventory().getItemInMainHand().getType().equals(Material.ROTTEN_FLESH) && !p.isSneaking()) {
                            if (p.getGameMode().equals(GameMode.CREATIVE)) {
                                p.getLocation().getWorld().dropItemNaturally(p.getLocation(), Modifiers.POISONOUS_MODIFIER);
                                if (Main.getPlugin().getConfig().getBoolean("Sound.OnEnchanting")) {
                                    p.playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 0.5F);
                                }
                                ChatWriter.log(false, p.getDisplayName() + " created a Poisonous-Modifier in Creative!");
                            } else if (p.getLevel() >= Main.getPlugin().getConfig().getInt("Modifiers.Poisonous.EnchantCost")) {
                                int amount = p.getInventory().getItemInMainHand().getAmount();
                                int newLevel = p.getLevel() - Main.getPlugin().getConfig().getInt("Modifiers.Poisonous.EnchantCost");
                                p.setLevel(newLevel);
                                p.getInventory().getItemInMainHand().setAmount(amount - 1);
                                p.getLocation().getWorld().dropItemNaturally(p.getLocation(), Modifiers.POISONOUS_MODIFIER);
                                if (Main.getPlugin().getConfig().getBoolean("Sound.OnEnchanting")) {
                                    p.playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 0.5F);
                                }
                                ChatWriter.log(false, p.getDisplayName() + " created a Poisonous-Modifier!");
                            } else {
                                ChatWriter.sendMessage(e.getPlayer(), ChatColor.RED, "You do not have enough Levels to perform this action!");
                                ChatWriter.sendMessage(e.getPlayer(), ChatColor.RED, Main.getPlugin().getConfig().getInt("Modifiers.Poisonous.EnchantCost") + " levels are required!");
                                ChatWriter.log(false,  p.getDisplayName() + " tried to create a Poisonous-Modifier but had not enough levels!");
                            }
                            e.setCancelled(true);
                        }
                        //</editor-fold>
                    }
                    if (Main.getPlugin().getConfig().getBoolean("Modifiers.Sweeping.allowed") && p.hasPermission("minetinker.modifiers.sweeping.craft")) {
                        //<editor-fold desc="POISONOUS">
                        if (p.getInventory().getItemInMainHand().getType().equals(Material.IRON_INGOT) && !p.isSneaking()) {
                            if (p.getGameMode().equals(GameMode.CREATIVE)) {
                                p.getLocation().getWorld().dropItemNaturally(p.getLocation(), Modifiers.SWEEPING_MODIFIER);
                                if (Main.getPlugin().getConfig().getBoolean("Sound.OnEnchanting")) {
                                    p.playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 0.5F);
                                }
                                ChatWriter.log(false, p.getDisplayName() + " created a Sweeping-Modifier in Creative!");
                            } else if (p.getLevel() >= Main.getPlugin().getConfig().getInt("Modifiers.Sweeping.EnchantCost")) {
                                int amount = p.getInventory().getItemInMainHand().getAmount();
                                int newLevel = p.getLevel() - Main.getPlugin().getConfig().getInt("Modifiers.Sweeping.EnchantCost");
                                p.setLevel(newLevel);
                                p.getInventory().getItemInMainHand().setAmount(amount - 1);
                                p.getLocation().getWorld().dropItemNaturally(p.getLocation(), Modifiers.SWEEPING_MODIFIER);
                                if (Main.getPlugin().getConfig().getBoolean("Sound.OnEnchanting")) {
                                    p.playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 0.5F);
                                }
                                ChatWriter.log(false, p.getDisplayName() + " created a Sweeping-Modifier!");
                            } else {
                                ChatWriter.sendMessage(e.getPlayer(), ChatColor.RED, "You do not have enough Levels to perform this action!");
                                ChatWriter.sendMessage(e.getPlayer(), ChatColor.RED, Main.getPlugin().getConfig().getInt("Modifiers.Sweeping.EnchantCost") + " levels are required!");
                                ChatWriter.log(false, p.getDisplayName() + " tried to create a Sweeping-Modifier but had not enough levels!");
                            }
                            e.setCancelled(true);
                        }
                        //</editor-fold>
                    }
                }
            }
        }
    }

    @EventHandler
    public static void onBlockPlace(BlockPlaceEvent e) {
        if (e.isCancelled()) {
            return;
        }
        Player p = e.getPlayer();
        Block b = e.getBlockPlaced();
        BlockState bs = b.getState();
        if (!p.hasPermission("minetinker.spawners.place") && b.getType().equals(Material.SPAWNER)) {
            e.setCancelled(true);
            //return;
        } else if (p.hasPermission("minetinker.spawners.place") && b.getType().equals(Material.SPAWNER)) {
            CreatureSpawner cs = (CreatureSpawner) bs;
            cs.setSpawnedType(EntityType.fromName(e.getItemInHand().getItemMeta().getDisplayName()));
            bs.update(true);
            System.out.println(EntityType.fromName(e.getItemInHand().getItemMeta().getDisplayName()));
            System.out.println(cs.getSpawnedType());
            ChatWriter.log(false,  p.getDisplayName() + " successfully placed a Spawner!");
        }
    }

    @EventHandler
    public static void onHoeUse(PlayerInteractEvent e) {
        if (e.isCancelled()) { return; }
        Player p = e.getPlayer();
        if (!Lists.WORLDS.contains(p.getWorld().getName())) { return; }
        if (!(p.getGameMode().equals(GameMode.SURVIVAL) || p.getGameMode().equals(GameMode.ADVENTURE))) { return; }
        ItemStack tool = p.getInventory().getItemInMainHand();
        if (!Lists.HOES.contains(tool.getType().toString())) { return; }
        if (!tool.hasItemMeta()) { return; }
        ItemMeta meta = tool.getItemMeta();
        if (!meta.hasLore()) { return; }
        ArrayList<String> lore = (ArrayList<String>) meta.getLore();
        if (!lore.contains(Strings.IDENTIFIER)) { return; }

        Boolean apply = false;

        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (e.getClickedBlock().getType().equals(Material.GRASS_BLOCK) ||
                e.getClickedBlock().getType().equals(Material.DIRT)) {
                apply = true;
            }
            if (!p.getWorld().getBlockAt(e.getClickedBlock().getLocation().add(0, 1, 0)).getType().equals(Material.AIR)) { //Case Block is on top of clicked Block -> No Soil Tilt -> no Exp
                apply = false;
            }
        }

        if (!apply) { return; }

        if (tool.getType().getMaxDurability() - ((Damageable) meta).getDamage() <= 1) {
            e.setCancelled(true);
            if (Main.getPlugin().getConfig().getBoolean("Sound.OnBreaking")) {
                p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.5F, 0.5F);
            }
            return;
        }

        LevelCalculator.addExp(e.getPlayer(), tool, Main.getPlugin().getConfig().getInt("ExpPerBlockBreak"));

        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Self-Repair.allowed") && p.hasPermission("minetinker.modifiers.selfrepair.use")) {
            for (int i = 1; i <= Main.getPlugin().getConfig().getInt("Modifiers.Self-Repair.MaxLevel"); i++) {
                if (lore.contains(Strings.SELFREPAIR + i)) {
                    Random rand = new Random();
                    int n = rand.nextInt(100);
                    if (n <= Main.getPlugin().getConfig().getInt("Modifiers.Self-Repair.PercentagePerLevel") * i) {
                        int heal = Main.getPlugin().getConfig().getInt("Modifiers.Self-Repair.HealthRepair");
                        short dura = (short) (tool.getDurability() - heal);
                        if (dura < 0) {
                            dura = 0;
                        }
                        tool.setDurability(dura);
                        ChatWriter.log(false, p.getDisplayName() + " triggered Self-Repair on " + ItemGenerator.getDisplayName(tool) + ChatColor.WHITE + " (" + tool.getType().toString() + ")!");
                    }
                    break;
                }
            }
        }

        if (Main.getPlugin().getConfig().getBoolean("Modifiers.XP.allowed") && p.hasPermission("minetinker.modifiers.xp.use")) {
            for (int i = 1; i <= Main.getPlugin().getConfig().getInt("Modifiers.XP.MaxLevel"); i++) {
                if (lore.contains(Strings.XP + i)) {
                    Random rand = new Random();
                    int n = rand.nextInt(100);
                    if (n <= Main.getPlugin().getConfig().getInt("Modifiers.XP.PercentagePerLevel") * i) {
                        ExperienceOrb orb = p.getWorld().spawn(p.getLocation(), ExperienceOrb.class);
                        orb.setExperience(Main.getPlugin().getConfig().getInt("Modifiers.XP.XPAmount"));
                        ChatWriter.log(false, p.getDisplayName() + " triggered XP on " + ItemGenerator.getDisplayName(tool) + ChatColor.WHITE + " (" + tool.getType().toString() + ")!");
                    }
                    break;
                }
            }
        }

        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Power.allowed") && p.hasPermission("minetinker.modifiers.power.use")) {
            if (!PlayerData.HASPOWER.get(e.getPlayer()) && !p.isSneaking()) {
                if (lore.contains(Strings.POWER + 1)) {
                    PlayerData.HASPOWER.replace(e.getPlayer(), true);
                    if (PlayerData.BLOCKFACE.get(e.getPlayer()).equals(BlockFace.DOWN) || PlayerData.BLOCKFACE.get(e.getPlayer()).equals(BlockFace.UP)) {
                        if (PlayerInfo.getFacingDirection(e.getPlayer()).equals("N") || PlayerInfo.getFacingDirection(e.getPlayer()).equals("S")) {
                            Block b1 = p.getWorld().getBlockAt(e.getClickedBlock().getLocation().add(1, 0, 0));
                            Block b2 = p.getWorld().getBlockAt(e.getClickedBlock().getLocation().add(-1, 0, 0));
                            if (b1.getType().equals(Material.GRASS_BLOCK) || b1.getType().equals(Material.DIRT)) {
                                if (b1.getWorld().getBlockAt(b1.getLocation().add(0, 1, 0)).getType().equals(Material.AIR)) {
                                    tool.setDurability((short) (tool.getDurability() + 1));
                                    Bukkit.getPluginManager().callEvent(new PlayerInteractEvent(p, Action.RIGHT_CLICK_BLOCK, tool, b1, BlockFace.UP));
                                    b1.setType(Material.FARMLAND); //Event only does Plugin event (no vanilla conversion to Farmland and Tool-Damage)
                                }
                            }
                            if (b2.getType().equals(Material.GRASS_BLOCK) || b2.getType().equals(Material.DIRT)) {
                                if (b2.getWorld().getBlockAt(b2.getLocation().add(0, 1, 0)).getType().equals(Material.AIR)) {
                                    tool.setDurability((short) (tool.getDurability() + 1));
                                    Bukkit.getPluginManager().callEvent(new PlayerInteractEvent(p, Action.RIGHT_CLICK_BLOCK, tool, b2, BlockFace.UP));
                                    b2.setType(Material.FARMLAND); //Event only does Plugin event (no vanilla conversion to Farmland and Tool-Damage)
                                }
                            }
                        } else if (PlayerInfo.getFacingDirection(e.getPlayer()).equals("W") || PlayerInfo.getFacingDirection(e.getPlayer()).equals("E")) {
                            Block b1 = p.getWorld().getBlockAt(e.getClickedBlock().getLocation().add(0, 0, 1));
                            Block b2 = p.getWorld().getBlockAt(e.getClickedBlock().getLocation().add(0, 0, -1));
                            if (b1.getType().equals(Material.GRASS_BLOCK) || b1.getType().equals(Material.DIRT)) {
                                if (b1.getWorld().getBlockAt(b1.getLocation().add(0, 1, 0)).getType().equals(Material.AIR)) {
                                    tool.setDurability((short) (tool.getDurability() + 1));
                                    Bukkit.getPluginManager().callEvent(new PlayerInteractEvent(p, Action.RIGHT_CLICK_BLOCK, tool, b1, BlockFace.UP));
                                    b1.setType(Material.FARMLAND); //Event only does Plugin event (no vanilla conversion to Farmland and Tool-Damage)
                                }
                            }
                            if (b2.getType().equals(Material.GRASS_BLOCK) || b2.getType().equals(Material.DIRT)) {
                                if (b2.getWorld().getBlockAt(b2.getLocation().add(0, 1, 0)).getType().equals(Material.AIR)) {
                                    tool.setDurability((short) (tool.getDurability() + 1));
                                    Bukkit.getPluginManager().callEvent(new PlayerInteractEvent(p, Action.RIGHT_CLICK_BLOCK, tool, b2, BlockFace.UP));
                                    b2.setType(Material.FARMLAND); //Event only does Plugin event (no vanilla conversion to Farmland and Tool-Damage)
                                }
                            }
                        }
                    }
                }
                if (!PlayerData.HASPOWER.get(e.getPlayer())) {
                    for (int level = 2; level <= Main.getPlugin().getConfig().getInt("Modifiers.Power.MaxLevel"); level++) {
                        if (lore.contains(Strings.POWER + level)) {
                            PlayerData.HASPOWER.replace(e.getPlayer(), true);
                            if (PlayerData.BLOCKFACE.get(e.getPlayer()).equals(BlockFace.DOWN) || PlayerData.BLOCKFACE.get(e.getPlayer()).equals(BlockFace.UP)) {
                                for (int x = -(level - 1); x <= (level - 1); x++) {
                                    for (int z = -(level - 1); z <= (level - 1); z++) {
                                        if (!(x == 0 && z == 0)) {
                                            Block b = p.getWorld().getBlockAt(e.getClickedBlock().getLocation().add(x, 0, z));
                                            if (b.getType().equals(Material.GRASS_BLOCK) || b.getType().equals(Material.DIRT)) {
                                                if (b.getWorld().getBlockAt(b.getLocation().add(0, 1, 0)).getType().equals(Material.AIR)) {
                                                    tool.setDurability((short) (tool.getDurability() + 1));
                                                    Bukkit.getPluginManager().callEvent(new PlayerInteractEvent(p, Action.RIGHT_CLICK_BLOCK, tool, b, BlockFace.UP));
                                                    b.setType(Material.FARMLAND); //Event only does Plugin event (no vanilla conversion to Farmland and Tool-Damage)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                PlayerData.HASPOWER.replace(e.getPlayer(), false);
            }
        }
    }

    @EventHandler
    public static void onAxeUse(PlayerInteractEvent e) {
        if (e.isCancelled()) { return; }
        Player p = e.getPlayer();
        if (!Lists.WORLDS.contains(p.getWorld().getName())) { return; }
        if (!(p.getGameMode().equals(GameMode.SURVIVAL) || p.getGameMode().equals(GameMode.ADVENTURE))) { return; }
        ItemStack tool = p.getInventory().getItemInMainHand();
        if (!Lists.AXES.contains(tool.getType().toString())) { return; }
        if (!tool.hasItemMeta()) { return; }
        ItemMeta meta = tool.getItemMeta();
        if (!meta.hasLore()) { return; }
        ArrayList<String> lore = (ArrayList<String>) meta.getLore();
        if (!lore.contains(Strings.IDENTIFIER)) { return; }

        Boolean apply = false;

        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (e.getClickedBlock().getType().equals(Material.ACACIA_LOG) ||
                e.getClickedBlock().getType().equals(Material.BIRCH_LOG) ||
                e.getClickedBlock().getType().equals(Material.OAK_LOG) ||
                e.getClickedBlock().getType().equals(Material.DARK_OAK_LOG) ||
                e.getClickedBlock().getType().equals(Material.JUNGLE_LOG) ||
                e.getClickedBlock().getType().equals(Material.SPRUCE_LOG)) {
                apply = true;
            } else if (e.getClickedBlock().getType().equals(Material.ACACIA_WOOD) ||
                    e.getClickedBlock().getType().equals(Material.BIRCH_WOOD) ||
                    e.getClickedBlock().getType().equals(Material.OAK_WOOD) ||
                    e.getClickedBlock().getType().equals(Material.DARK_OAK_WOOD) ||
                    e.getClickedBlock().getType().equals(Material.JUNGLE_WOOD) ||
                    e.getClickedBlock().getType().equals(Material.SPRUCE_WOOD)) {
                apply = true;
            }
        }

        if (!apply) { return; }

        if (tool.getType().getMaxDurability() - ((Damageable) meta).getDamage() <= 1) {
            e.setCancelled(true);
            if (Main.getPlugin().getConfig().getBoolean("Sound.OnBreaking")) {
                p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.5F, 0.5F);
            }
            return;
        }

        LevelCalculator.addExp(e.getPlayer(), tool, Main.getPlugin().getConfig().getInt("ExpPerBlockBreak"));

        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Self-Repair.allowed") && p.hasPermission("minetinker.modifiers.selfrepair.use")) {
            for (int i = 1; i <= Main.getPlugin().getConfig().getInt("Modifiers.Self-Repair.MaxLevel"); i++) {
                if (lore.contains(Strings.SELFREPAIR + i)) {
                    //self-repair
                    Random rand = new Random();
                    int n = rand.nextInt(100);
                    if (n <= Main.getPlugin().getConfig().getInt("Modifiers.Self-Repair.PercentagePerLevel") * i) {
                        int heal = Main.getPlugin().getConfig().getInt("Modifiers.Self-Repair.HealthRepair");
                        short dura = (short) (tool.getDurability() - heal);
                        if (dura < 0) {
                            dura = 0;
                        }
                        p.getInventory().getItemInMainHand().setDurability(dura);
                        ChatWriter.log(false, p.getDisplayName() + " triggered Self-Repair on " + ItemGenerator.getDisplayName(tool) + ChatColor.WHITE + " (" + tool.getType().toString() + ")!");
                    }
                    break;
                }
            }
        }

        if (Main.getPlugin().getConfig().getBoolean("Modifiers.XP.allowed") && p.hasPermission("minetinker.modifiers.xp.use")) {
            for (int i = 1; i <= Main.getPlugin().getConfig().getInt("Modifiers.XP.MaxLevel"); i++) {
                if (lore.contains(Strings.XP + i)) {
                    //xp
                    Random rand = new Random();
                    int n = rand.nextInt(100);
                    if (n <= Main.getPlugin().getConfig().getInt("Modifiers.XP.PercentagePerLevel") * i) {
                        ExperienceOrb orb = p.getWorld().spawn(p.getLocation(), ExperienceOrb.class);
                        orb.setExperience(Main.getPlugin().getConfig().getInt("Modifiers.XP.XPAmount"));
                        ChatWriter.log(false, p.getDisplayName() + " triggered XP on " + ItemGenerator.getDisplayName(tool) + ChatColor.WHITE + " (" + tool.getType().toString() + ")!");
                    }
                    break;
                }
            }
        }
    }
}

package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Data.Modifiers;
import de.flo56958.MineTinker.Data.PlayerData;
import de.flo56958.MineTinker.Data.Strings;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Utilities.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
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

import java.util.List;
import java.util.Random;

public class BlockListener implements Listener {

    private static final FileConfiguration config = Main.getPlugin().getConfig();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.isCancelled()) { return; }
        Player p = e.getPlayer();
        if (!Lists.WORLDS.contains(p.getWorld().getName())) { return; }
        if (e.getBlock().getType().equals(Material.KELP_PLANT) ||
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
                e.getBlock().getType().equals(Material.TORCH)) { return; }
        if (!(p.getGameMode().equals(GameMode.SURVIVAL) || p.getGameMode().equals(GameMode.ADVENTURE))) { return; }
        ItemStack tool = p.getInventory().getItemInMainHand();

        if (!PlayerInfo.isToolViable(tool)) { return; }

        ItemMeta meta = tool.getItemMeta();
        List<String> lore = meta.getLore();

        if (tool.getType().getMaxDurability() - ((Damageable) meta).getDamage() <= 2) {
            e.setCancelled(true);
            if (config.getBoolean("Sound.OnBreaking")) {
                p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.5F, 0.5F);
            }
            return;
        }

        if (PlayerData.HASPOWER.get(p) && e.getBlock().getDrops(tool).isEmpty()) {
            e.setCancelled(true);
            return;
        }

        LevelCalculator.addExp(p, tool, config.getInt("ExpPerBlockBreak"));

        ModifierEffect.selfRepair(p, tool);

        if (Lists.WORLDS_SPAWNERS.contains(p.getWorld().toString())) {
            if ((config.getBoolean("Modifiers.Silk-Touch.allowed") && config.getBoolean("Spawners.enabled") && config.getBoolean("Spawners.onlyWithSilkTouch"))
                    || (config.getBoolean("Spawners.enabled") && !config.getBoolean("Spawners.onlyWithSilkTouch"))) {
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
        }

        ModifierEffect.xp(p, tool);

        boolean autosmeltTrigger = false;

        if (config.getBoolean("Modifiers.Auto-Smelt.allowed") && p.hasPermission("minetinker.modifiers.autosmelt.use")) {
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
                for (int i = 1; i <= config.getInt("Modifiers.Auto-Smelt.MaxLevel"); i++) {
                    if (lore.contains(Strings.AUTOSMELT + i)) {
                        Random rand = new Random();
                        int n = rand.nextInt(100);
                        if (n <= config.getInt("Modifiers.Auto-Smelt.PercentagePerLevel") * i) {
                            int amount = 1;
                            if (config.getBoolean("Modifiers.Luck.allowed") && luck) {
                                for (int j = 0; j <= config.getInt("Modifiers.Luck.MaxLevel"); j++) {
                                    if (lore.contains(Strings.LUCK + j)) {
                                        amount = amount + rand.nextInt(j);
                                        break;
                                    }
                                }
                            }
                            e.setDropItems(false);
                            autosmeltTrigger = true;
                            ItemStack items = new ItemStack(loot, amount);
                            if (lore.contains(Strings.DIRECTING) && config.getBoolean("Modifiers.Directing.allowed") && p.hasPermission("minetinker.modifiers.directing.use")) {
                                if(p.getInventory().addItem(items).size() != 0) { //adds items to (full) inventory
                                    p.getWorld().dropItem(p.getLocation(), items);
                                } // no else as it gets added in if-clause
                            } else {
                                e.getBlock().getLocation().getWorld().dropItemNaturally(e.getBlock().getLocation(), items);
                            }
                            e.getBlock().getLocation().getWorld().spawnParticle(Particle.FLAME, e.getBlock().getLocation(), 5);
                            if (config.getBoolean("Modifiers.Auto-Smelt.Sound")) {
                                e.getBlock().getLocation().getWorld().playSound(e.getBlock().getLocation(), Sound.ENTITY_GENERIC_BURN, 0.2F, 0.5F);
                            }
                            ChatWriter.log(false, p.getDisplayName() + " triggered Auto-Smelt on " + ItemGenerator.getDisplayName(tool) + ChatColor.WHITE + " (" + tool.getType().toString() + ") while mining " + e.getBlock().getType().toString() + "!");
                        }
                        break;
                    }
                }
            }
        }

        if (config.getBoolean("Modifiers.Timber.allowed") && p.hasPermission("minetinker.modifiers.timber.use")) {
            if (lore.contains(Strings.TIMBER)) {
                if (!PlayerData.HASPOWER.get(p) && !p.isSneaking()) {
                    if (Lists.AXES.contains(tool.getType().toString()) && (e.getBlock().getType().equals(Material.ACACIA_LOG)
                            || e.getBlock().getType().equals(Material.OAK_LOG)
                            || e.getBlock().getType().equals(Material.SPRUCE_LOG)
                            || e.getBlock().getType().equals(Material.JUNGLE_LOG)
                            || e.getBlock().getType().equals(Material.DARK_OAK_LOG)
                            || e.getBlock().getType().equals(Material.BIRCH_LOG)
                            || e.getBlock().getType().equals(Material.ACACIA_WOOD)
                            || e.getBlock().getType().equals(Material.OAK_WOOD)
                            || e.getBlock().getType().equals(Material.SPRUCE_WOOD)
                            || e.getBlock().getType().equals(Material.JUNGLE_WOOD)
                            || e.getBlock().getType().equals(Material.DARK_OAK_WOOD)
                            || e.getBlock().getType().equals(Material.BIRCH_WOOD)) && Timber.init(p, e.getBlock())) {
                        PlayerData.HASPOWER.replace(p, false);
                    }
                }
            }
        }

        if (config.getBoolean("Modifiers.Directing.allowed") && p.hasPermission("minetinker.modifiers.directing.use")) {
            if (lore.contains(Strings.DIRECTING) && !autosmeltTrigger) {
                List<ItemStack> drops = (List<ItemStack>) e.getBlock().getDrops(tool);  //TODO: Get real drops (for Luck and Silk-Touch compability
                for (ItemStack current : drops) {
                    if(p.getInventory().addItem(current).size() != 0) { //adds items to (full) inventory
                        p.getWorld().dropItem(p.getLocation(), current);
                    } // no else as it gets added in if
                }
                e.setDropItems(false);
            }
        }

        if (config.getBoolean("Modifiers.Power.allowed") && p.hasPermission("minetinker.modifiers.power.use")) {
            if (!PlayerData.HASPOWER.get(p) && !p.isSneaking()) {
                if (lore.contains(Strings.POWER + 1)) {
                    PlayerData.HASPOWER.replace(p, true);
                    if (PlayerData.BLOCKFACE.get(p).equals(BlockFace.DOWN) || PlayerData.BLOCKFACE.get(p).equals(BlockFace.UP)) {
                        if (PlayerInfo.getFacingDirection(p).equals("N") || PlayerInfo.getFacingDirection(p).equals("S")) {
                            Block b1 = e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().add(1, 0, 0));
                            Block b2 = e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().add(-1, 0, 0));
                            ModifierEffect.powerBlockBreak(b1, (CraftPlayer) p);
                            ModifierEffect.powerBlockBreak(b2, (CraftPlayer) p);
                        } else if (PlayerInfo.getFacingDirection(p).equals("W") || PlayerInfo.getFacingDirection(p).equals("E")) {
                            Block b1 = e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().add(0, 0, 1));
                            Block b2 = e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().add(0, 0, -1));
                            ModifierEffect.powerBlockBreak(b1, (CraftPlayer) p);
                            ModifierEffect.powerBlockBreak(b2, (CraftPlayer) p);
                        }
                    } else if (PlayerData.BLOCKFACE.get(p).equals(BlockFace.NORTH) || PlayerData.BLOCKFACE.get(p).equals(BlockFace.SOUTH)) {
                        Block b1 = e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().add(1, 0, 0));
                        Block b2 = e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().add(-1, 0, 0));
                        ModifierEffect.powerBlockBreak(b1, (CraftPlayer) p);
                        ModifierEffect.powerBlockBreak(b2, (CraftPlayer) p);
                    } else if (PlayerData.BLOCKFACE.get(p).equals(BlockFace.WEST) || PlayerData.BLOCKFACE.get(p).equals(BlockFace.EAST)) {
                        Block b1 = e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().add(0, 0, 1));
                        Block b2 = e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().add(0, 0, -1));
                        ModifierEffect.powerBlockBreak(b1, (CraftPlayer) p);
                        ModifierEffect.powerBlockBreak(b2, (CraftPlayer) p);
                    }

                }
                if (!PlayerData.HASPOWER.get(p)) {
                    for (int level = 2; level <= config.getInt("Modifiers.Power.MaxLevel"); level++) {
                        if (lore.contains(Strings.POWER + level)) {
                            PlayerData.HASPOWER.replace(p, true);
                            if (PlayerData.BLOCKFACE.get(p).equals(BlockFace.DOWN) || PlayerData.BLOCKFACE.get(p).equals(BlockFace.UP)) {
                                for (int x = -(level - 1); x <= (level - 1); x++) {
                                    for (int z = -(level - 1); z <= (level - 1); z++) {
                                        if (!(x == 0 && z == 0)) {
                                            Block b = e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().add(x, 0, z));
                                            ModifierEffect.powerBlockBreak(b, (CraftPlayer) p);
                                        }
                                    }
                                }
                            } else if (PlayerData.BLOCKFACE.get(p).equals(BlockFace.NORTH) || PlayerData.BLOCKFACE.get(p).equals(BlockFace.SOUTH)) {
                                for (int x = -(level - 1); x <= (level - 1); x++) {
                                    for (int y = -(level - 1); y <= (level - 1); y++) {
                                        if (!(x == 0 && y == 0)) {
                                            Block b = e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().add(x, y, 0));
                                            ModifierEffect.powerBlockBreak(b, (CraftPlayer) p);
                                        }
                                    }
                                }
                            } else if (PlayerData.BLOCKFACE.get(p).equals(BlockFace.EAST) || PlayerData.BLOCKFACE.get(p).equals(BlockFace.WEST)) {
                                for (int z = -(level - 1); z <= (level - 1); z++) {
                                    for (int y = -(level - 1); y <= (level - 1); y++) {
                                        if (!(z == 0 && y == 0)) {
                                            Block b = e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().add(0, y, z));
                                            ModifierEffect.powerBlockBreak(b, (CraftPlayer) p);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                PlayerData.HASPOWER.replace(p, false);
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
                        norm.equals(Modifiers.KNOCKBACK_MODIFIER) ||
                        norm.equals(Modifiers.TIMBER_MODIFIER) ||
                        norm.equals(Modifiers.MELTING_MODIFIER)) {
                    norm.setAmount(temp);
                    e.setCancelled(true);
                    return;
                }
                norm.setAmount(temp);
                if (e.getClickedBlock().getType().equals(Material.BOOKSHELF)) {
                    if (config.getBoolean("Modifiers.Self-Repair.allowed") && p.hasPermission("minetinker.modifiers.selfrepair.craft")) {
                        if (p.getInventory().getItemInMainHand().getType().equals(Material.MOSSY_COBBLESTONE) && !p.isSneaking()) {
                            ItemGenerator.createModifierItem(p, "Self-Repair", Modifiers.SELFREPAIR_MODIFIER);
                            e.setCancelled(true);
                        }
                    }
                    if (config.getBoolean("Modifiers.Knockback.allowed") && p.hasPermission("minetinker.modifiers.knockback.craft")) {
                        if (p.getInventory().getItemInMainHand().getType().equals(Material.TNT) && !p.isSneaking()) {
                            ItemGenerator.createModifierItem(p, "Knockback", Modifiers.KNOCKBACK_MODIFIER);
                            e.setCancelled(true);
                        }
                    }
                    if (config.getBoolean("Modifiers.Silk-Touch.allowed") && p.hasPermission("minetinker.modifiers.silktouch.craft")) {
                        if (p.getInventory().getItemInMainHand().getType().equals(Material.COBWEB) && !p.isSneaking()) {
                            ItemGenerator.createModifierItem(p, "Silk-Touch", Modifiers.SILKTOUCH_MODIFIER);
                            e.setCancelled(true);
                        }
                    }
                    if (config.getBoolean("Modifiers.Fiery.allowed") && p.hasPermission("minetinker.modifiers.fiery.craft")) {
                        if (p.getInventory().getItemInMainHand().getType().equals(Material.BLAZE_ROD) && !p.isSneaking()) {
                            ItemGenerator.createModifierItem(p, "Fiery", Modifiers.FIERY_MODIFIER);
                            e.setCancelled(true);
                        }
                    }
                    if (config.getBoolean("Modifiers.Power.allowed") && p.hasPermission("minetinker.modifiers.power.craft")) {
                        if (p.getInventory().getItemInMainHand().getType().equals(Material.EMERALD) && !p.isSneaking()) {
                            ItemGenerator.createModifierItem(p, "Power", Modifiers.POWER_MODIFIER);
                            e.setCancelled(true);
                        }
                    }
                    if (config.getBoolean("Modifiers.Beheading.allowed") && p.hasPermission("minetinker.modifiers.beheading.craft")) {
                        if (p.getInventory().getItemInMainHand().getType().equals(Material.WITHER_SKELETON_SKULL) && !p.isSneaking()) {
                            ItemGenerator.createModifierItem(p, "Beheading", Modifiers.BEHEADING_MODIFIER);
                            e.setCancelled(true);
                        }
                    }
                    if (config.getBoolean("Modifiers.Infinity.allowed") && p.hasPermission("minetinker.modifiers.infinity.craft")) {
                        if (p.getInventory().getItemInMainHand().getType().equals(Material.ARROW) && !p.isSneaking()) {
                            ItemGenerator.createModifierItem(p, "Infinity", Modifiers.INFINITY_MODIFIER);
                            e.setCancelled(true);
                        }
                    }
                    if (config.getBoolean("Modifiers.Poisonous.allowed") && p.hasPermission("minetinker.modifiers.poisonous.craft")) {
                        if (p.getInventory().getItemInMainHand().getType().equals(Material.ROTTEN_FLESH) && !p.isSneaking()) {
                            ItemGenerator.createModifierItem(p, "Poisonous", Modifiers.POISONOUS_MODIFIER);
                            e.setCancelled(true);
                        }
                    }
                    if (config.getBoolean("Modifiers.Sweeping.allowed") && p.hasPermission("minetinker.modifiers.sweeping.craft")) {
                        if (p.getInventory().getItemInMainHand().getType().equals(Material.IRON_INGOT) && !p.isSneaking()) {
                            ItemGenerator.createModifierItem(p, "Sweeping", Modifiers.SWEEPING_MODIFIER);
                            e.setCancelled(true);
                        }
                    }
                    if (config.getBoolean("Modifiers.Melting.allowed") && p.hasPermission("minetinker.modifiers.melting.craft")) {
                        if (p.getInventory().getItemInMainHand().getType().equals(Material.MAGMA_BLOCK) && !p.isSneaking()) {
                            ItemGenerator.createModifierItem(p, "Melting", Modifiers.MELTING_MODIFIER);
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public static void onBlockPlace(BlockPlaceEvent e) {
        if (e.isCancelled()) { return; }

        Player p = e.getPlayer();
        Block b = e.getBlockPlaced();
        BlockState bs = b.getState();
        if (config.getBoolean("Spawners.enabled") && Lists.WORLDS_SPAWNERS.contains(p.getWorld().toString())) {
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
    }

    @EventHandler
    public static void onHoeUse(PlayerInteractEvent e) {
        if (e.isCancelled()) { return; }
        Player p = e.getPlayer();
        if (!Lists.WORLDS.contains(p.getWorld().getName())) { return; }
        if (!(p.getGameMode().equals(GameMode.SURVIVAL) || p.getGameMode().equals(GameMode.ADVENTURE))) { return; }
        ItemStack tool = p.getInventory().getItemInMainHand();
        if (!Lists.HOES.contains(tool.getType().toString())) { return; }

        if (!PlayerInfo.isToolViable(tool)) { return; }

        ItemMeta meta = tool.getItemMeta();
        List<String> lore = meta.getLore();

        boolean apply = false;

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
            if (config.getBoolean("Sound.OnBreaking")) {
                p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.5F, 0.5F);
            }
            return;
        }

        LevelCalculator.addExp(p, tool, config.getInt("ExpPerBlockBreak"));

        ModifierEffect.selfRepair(p, tool);

        ModifierEffect.xp(p, tool);

        if (config.getBoolean("Modifiers.Power.allowed") && p.hasPermission("minetinker.modifiers.power.use")) {
            if (!PlayerData.HASPOWER.get(p) && !p.isSneaking()) {
                if (lore.contains(Strings.POWER + 1)) {
                    PlayerData.HASPOWER.replace(p, true);
                    if (PlayerData.BLOCKFACE.get(p).equals(BlockFace.DOWN) || PlayerData.BLOCKFACE.get(p).equals(BlockFace.UP)) {
                        if (PlayerInfo.getFacingDirection(p).equals("N") || PlayerInfo.getFacingDirection(p).equals("S")) {
                            Block b1 = p.getWorld().getBlockAt(e.getClickedBlock().getLocation().add(1, 0, 0));
                            Block b2 = p.getWorld().getBlockAt(e.getClickedBlock().getLocation().add(-1, 0, 0));
                            ModifierEffect.powerCreateFarmland(p, tool, b1);
                            ModifierEffect.powerCreateFarmland(p, tool, b2);
                        } else if (PlayerInfo.getFacingDirection(p).equals("W") || PlayerInfo.getFacingDirection(p).equals("E")) {
                            Block b1 = p.getWorld().getBlockAt(e.getClickedBlock().getLocation().add(0, 0, 1));
                            Block b2 = p.getWorld().getBlockAt(e.getClickedBlock().getLocation().add(0, 0, -1));
                            ModifierEffect.powerCreateFarmland(p, tool, b1);
                            ModifierEffect.powerCreateFarmland(p, tool, b2);
                        }
                    }
                }
                if (!PlayerData.HASPOWER.get(p)) {
                    for (int level = 2; level <= config.getInt("Modifiers.Power.MaxLevel"); level++) {
                        if (lore.contains(Strings.POWER + level)) {
                            PlayerData.HASPOWER.replace(p, true);
                            if (PlayerData.BLOCKFACE.get(p).equals(BlockFace.DOWN) || PlayerData.BLOCKFACE.get(p).equals(BlockFace.UP)) {
                                for (int x = -(level - 1); x <= (level - 1); x++) {
                                    for (int z = -(level - 1); z <= (level - 1); z++) {
                                        if (!(x == 0 && z == 0)) {
                                            Block b = p.getWorld().getBlockAt(e.getClickedBlock().getLocation().add(x, 0, z));
                                            ModifierEffect.powerCreateFarmland(p, tool, b);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                PlayerData.HASPOWER.replace(p, false);
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

        if (!PlayerInfo.isToolViable(tool)) { return; }

        ItemMeta meta = tool.getItemMeta();
        List<String> lore = meta.getLore();

        boolean apply = false;

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
            if (config.getBoolean("Sound.OnBreaking")) {
                p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.5F, 0.5F);
            }
            return;
        }

        LevelCalculator.addExp(p, tool, config.getInt("ExpPerBlockBreak"));

        ModifierEffect.selfRepair(p, tool);

        ModifierEffect.xp(p, tool);
    }

    @EventHandler
    public static void onShovelUse(PlayerInteractEvent e) {
        if (e.isCancelled()) { return; }
        Player p = e.getPlayer();
        if (!Lists.WORLDS.contains(p.getWorld().getName())) { return; }
        if (!(p.getGameMode().equals(GameMode.SURVIVAL) || p.getGameMode().equals(GameMode.ADVENTURE))) { return; }
        ItemStack tool = p.getInventory().getItemInMainHand();
        if (!Lists.SHOVELS.contains(tool.getType().toString())) { return; }

        if (!PlayerInfo.isToolViable(tool)) { return; }

        ItemMeta meta = tool.getItemMeta();
        List<String> lore = meta.getLore();

        boolean apply = false;

        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (e.getClickedBlock().getType().equals(Material.GRASS_BLOCK)) {
                apply = true;
            }
        }

        if (!apply) { return; }

        if (tool.getType().getMaxDurability() - ((Damageable) meta).getDamage() <= 1) {
            e.setCancelled(true);
            if (config.getBoolean("Sound.OnBreaking")) {
                p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.5F, 0.5F);
            }
            return;
        }

        LevelCalculator.addExp(p, tool, config.getInt("ExpPerBlockBreak"));

        ModifierEffect.selfRepair(p, tool);

        ModifierEffect.xp(p, tool);
    }
}

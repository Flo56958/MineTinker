package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Modifiers.Types.*;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import de.flo56958.MineTinker.Utilities.PlayerInfo;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.file.FileConfiguration;
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

public class BlockListener implements Listener {

    private static final FileConfiguration config = Main.getPlugin().getConfig();
    private static final ModManager modManager = Main.getModManager();


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

        if (Power.HASPOWER.get(p) && e.getBlock().getDrops(tool).isEmpty()) {
            e.setCancelled(true);
            return;
        }

        modManager.addExp(p, tool, config.getInt("ExpPerBlockBreak"));

        if (Lists.WORLDS_SPAWNERS.contains(p.getWorld().getName())) {
            if (config.getBoolean("Spawners.enabled")) {
                if (e.getBlock().getState() instanceof CreatureSpawner && p.hasPermission("minetinker.spawners.mine")) {
                    if ((config.getBoolean("Spawners.onlyWithSilkTouch") && modManager.hasMod(tool, modManager.get(ModifierType.SILK_TOUCH))) || !config.getBoolean("Spawners.onlyWithSilkTouch")) {
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
        }

        if (modManager.get(ModifierType.EXPERIENCED) != null) {
            ((Experienced) modManager.get(ModifierType.EXPERIENCED)).effect(p, tool);
        }

        if (modManager.get(ModifierType.AUTO_SMELT) != null) {
            ((AutoSmelt) modManager.get(ModifierType.AUTO_SMELT)).effect(p, tool, e.getBlock(), e);
        }

        if (modManager.get(ModifierType.POWER) != null) {
            ((Power) modManager.get(ModifierType.POWER)).effect(p, tool, e.getBlock());
        }

        if (modManager.get(ModifierType.SELF_REPAIR) != null) {
            ((SelfRepair) modManager.get(ModifierType.SELF_REPAIR)).effect(p, tool);
        }

        if (modManager.get(ModifierType.TIMBER) != null) {
            if (!Power.HASPOWER.get(p) && !p.isSneaking()) {
                ((Timber) modManager.get(ModifierType.TIMBER)).effect(p, tool, e.getBlock());
            }
        }
    }


    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        if (e.isCancelled()) { return; }
        Player p = e.getPlayer();
        ItemStack norm = p.getInventory().getItemInMainHand();
        if (e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            if (modManager.get(ModifierType.ENDER) != null) {
                if (modManager.get(ModifierType.ENDER).getModItem().equals(norm)) {
                    e.setCancelled(true);
                }
            }
        } else if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            int temp = norm.getAmount();
            norm.setAmount(1);
            for (Modifier m : modManager.getAllMods()) {
                if (m.getModItem().equals(norm)) {
                    norm.setAmount(temp);
                    e.setCancelled(true);
                    return;
                }
            }
            norm.setAmount(temp);
            if (e.getClickedBlock().getType().equals(Material.BOOKSHELF)) {
                if (config.getBoolean("Modifiers.Beheading.allowed") && p.hasPermission("minetinker.modifiers.beheading.craft")) {
                    if (p.getInventory().getItemInMainHand().getType().equals(Material.WITHER_SKELETON_SKULL) && !p.isSneaking()) {
                        ItemGenerator.createModifierItem(p, "Beheading", modManager.get(ModifierType.BEHEADING).getModItem());
                        e.setCancelled(true);
                    }
                }
                if (config.getBoolean("Modifiers.Fiery.allowed") && p.hasPermission("minetinker.modifiers.fiery.craft")) {
                    if (p.getInventory().getItemInMainHand().getType().equals(Material.BLAZE_ROD) && !p.isSneaking()) {
                        ItemGenerator.createModifierItem(p, "Fiery", modManager.get(ModifierType.FIERY).getModItem());
                        e.setCancelled(true);
                    }
                }
                if (config.getBoolean("Modifiers.Infinity.allowed") && p.hasPermission("minetinker.modifiers.infinity.craft")) {
                    if (p.getInventory().getItemInMainHand().getType().equals(Material.ARROW) && !p.isSneaking()) {
                        ItemGenerator.createModifierItem(p, "Infinity", modManager.get(ModifierType.INFINITY).getModItem());
                        e.setCancelled(true);
                    }
                }
                if (config.getBoolean("Modifiers.Knockback.allowed") && p.hasPermission("minetinker.modifiers.knockback.craft")) {
                    if (p.getInventory().getItemInMainHand().getType().equals(Material.TNT) && !p.isSneaking()) {
                        ItemGenerator.createModifierItem(p, "Knockback", modManager.get(ModifierType.KNOCKBACK).getModItem());
                        e.setCancelled(true);
                    }
                }
                if (config.getBoolean("Modifiers.Melting.allowed") && p.hasPermission("minetinker.modifiers.melting.craft")) {
                    if (p.getInventory().getItemInMainHand().getType().equals(Material.MAGMA_BLOCK) && !p.isSneaking()) {
                        ItemGenerator.createModifierItem(p, "Melting", modManager.get(ModifierType.MELTING).getModItem());
                        e.setCancelled(true);
                    }
                }
                if (config.getBoolean("Modifiers.Power.allowed") && p.hasPermission("minetinker.modifiers.power.craft")) {
                    if (p.getInventory().getItemInMainHand().getType().equals(Material.EMERALD) && !p.isSneaking()) {
                        ItemGenerator.createModifierItem(p, "Power", modManager.get(ModifierType.POWER).getModItem());
                        e.setCancelled(true);
                    }
                }
                if (config.getBoolean("Modifiers.Poisonous.allowed") && p.hasPermission("minetinker.modifiers.poisonous.craft")) {
                    if (p.getInventory().getItemInMainHand().getType().equals(Material.ROTTEN_FLESH) && !p.isSneaking()) {
                        ItemGenerator.createModifierItem(p, "Poisonous", modManager.get(ModifierType.POISONOUS).getModItem());
                        e.setCancelled(true);
                    }
                }
                if (config.getBoolean("Modifiers.Self-Repair.allowed") && p.hasPermission("minetinker.modifiers.selfrepair.craft")) {
                    if (p.getInventory().getItemInMainHand().getType().equals(Material.MOSSY_COBBLESTONE) && !p.isSneaking()) {
                        ItemGenerator.createModifierItem(p, "Self-Repair", modManager.get(ModifierType.SELF_REPAIR).getModItem());
                        e.setCancelled(true);
                    }
                }
                if (config.getBoolean("Modifiers.Silk-Touch.allowed") && p.hasPermission("minetinker.modifiers.silktouch.craft")) {
                    if (p.getInventory().getItemInMainHand().getType().equals(Material.COBWEB) && !p.isSneaking()) {
                        ItemGenerator.createModifierItem(p, "Silk-Touch", modManager.get(ModifierType.SILK_TOUCH).getModItem());
                        e.setCancelled(true);
                    }
                }
                if (config.getBoolean("Modifiers.Sweeping.allowed") && p.hasPermission("minetinker.modifiers.sweeping.craft")) {
                    if (p.getInventory().getItemInMainHand().getType().equals(Material.IRON_INGOT) && !p.isSneaking()) {
                        ItemGenerator.createModifierItem(p, "Sweeping", modManager.get(ModifierType.SWEEPING).getModItem());
                        e.setCancelled(true);
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
        if (!ToolType.HOE.getMaterials().contains(tool.getType())) { return; }

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

        modManager.addExp(p, tool, config.getInt("ExpPerBlockBreak"));

        if (modManager.get(ModifierType.SELF_REPAIR) != null) {
            ((SelfRepair) modManager.get(ModifierType.SELF_REPAIR)).effect(p, tool);
        }

        if (modManager.get(ModifierType.EXPERIENCED) != null) {
            ((Experienced) modManager.get(ModifierType.EXPERIENCED)).effect(p, tool);
        }

        if (modManager.get(ModifierType.POWER) != null) {
            ((Power) modManager.get(ModifierType.POWER)).effect(p, tool, e);
        }

    }

    @EventHandler
    public static void onAxeUse(PlayerInteractEvent e) {
        if (e.isCancelled()) { return; }
        Player p = e.getPlayer();
        if (!Lists.WORLDS.contains(p.getWorld().getName())) { return; }
        if (!(p.getGameMode().equals(GameMode.SURVIVAL) || p.getGameMode().equals(GameMode.ADVENTURE))) { return; }
        ItemStack tool = p.getInventory().getItemInMainHand();
        if (!ToolType.AXE.getMaterials().contains(tool.getType())) { return; }

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

        modManager.addExp(p, tool, config.getInt("ExpPerBlockBreak"));

        if (modManager.get(ModifierType.SELF_REPAIR) != null) {
            ((SelfRepair) modManager.get(ModifierType.SELF_REPAIR)).effect(p, tool);
        }

        if (modManager.get(ModifierType.EXPERIENCED) != null) {
            ((Experienced) modManager.get(ModifierType.EXPERIENCED)).effect(p, tool);
        }
    }

    @EventHandler
    public static void onShovelUse(PlayerInteractEvent e) {
        if (e.isCancelled()) { return; }
        Player p = e.getPlayer();
        if (!Lists.WORLDS.contains(p.getWorld().getName())) { return; }
        if (!(p.getGameMode().equals(GameMode.SURVIVAL) || p.getGameMode().equals(GameMode.ADVENTURE))) { return; }
        ItemStack tool = p.getInventory().getItemInMainHand();
        if (!ToolType.SHOVEL.getMaterials().contains(tool.getType())) { return; }

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

        modManager.addExp(p, tool, config.getInt("ExpPerBlockBreak"));

        if (modManager.get(ModifierType.SELF_REPAIR) != null) {
            ((SelfRepair) modManager.get(ModifierType.SELF_REPAIR)).effect(p, tool);
        }

        if (modManager.get(ModifierType.EXPERIENCED) != null) {
            ((Experienced) modManager.get(ModifierType.EXPERIENCED)).effect(p, tool);
        }
    }
}

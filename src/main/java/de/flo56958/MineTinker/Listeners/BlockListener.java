package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Enchantable;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Modifiers.Types.*;
import de.flo56958.MineTinker.Utilities.ChatWriter;
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
import org.bukkit.event.EventPriority;
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
    private static final ModManager modManager = ModManager.instance();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.isCancelled()) { return; }
        Player p = e.getPlayer();
        ItemStack tool = p.getInventory().getItemInMainHand();
        if (!Lists.WORLDS.contains(p.getWorld().getName())) { return; }
        if (e.getBlock().getType().getHardness() == 0 && !(tool.getType() == Material.SHEARS || ToolType.HOE.getMaterials().contains(tool.getType()))) { return; }

        if (!(p.getGameMode().equals(GameMode.SURVIVAL) || p.getGameMode().equals(GameMode.ADVENTURE))) { return; }


        if (!modManager.isToolViable(tool)) { return; }

        ItemMeta meta = tool.getItemMeta();
        List<String> lore = meta.getLore();

        if (tool.getType().getMaxDurability() - ((Damageable) meta).getDamage() <= 2) {
            e.setCancelled(true);
            if (config.getBoolean("Sound.OnBreaking")) {
                p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.5F, 0.5F);
            }
            return;
        }

        if (Power.HASPOWER.get(p) && e.getBlock().getDrops(tool).isEmpty() && !e.getBlock().getType().equals(Material.NETHER_WART)) { //Necessary for EasyHarvest NetherWard-Break
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


    @EventHandler(priority = EventPriority.HIGHEST)
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
                ItemStack item = p.getInventory().getItemInMainHand();
                for (Modifier m : modManager.getEnchantableMods()) {
                    if (m.getModItem().getType().equals(item.getType())) {
                        ((Enchantable) m).enchantItem(p, item);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void onBlockPlace(BlockPlaceEvent e) {
        if (e.isCancelled()) { return; }

        Player p = e.getPlayer();
        Block b = e.getBlockPlaced();
        BlockState bs = b.getState();
        if (config.getBoolean("Spawners.enabled") && Lists.WORLDS_SPAWNERS.contains(p.getWorld().getName())) {
            if (!p.hasPermission("minetinker.spawners.place") && b.getState() instanceof CreatureSpawner) {
                e.setCancelled(true);
                //return;
            } else if (p.hasPermission("minetinker.spawners.place") && b.getState() instanceof CreatureSpawner) {
                CreatureSpawner cs = (CreatureSpawner) bs;
                cs.setSpawnedType(EntityType.fromName(e.getItemInHand().getItemMeta().getDisplayName()));
                bs.update(true);
                ChatWriter.log(false,  p.getDisplayName() + " successfully placed a Spawner!");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void onHoeUse(PlayerInteractEvent e) {
        if (e.isCancelled()) { return; }
        Player p = e.getPlayer();
        if (!Lists.WORLDS.contains(p.getWorld().getName())) { return; }
        if (!(p.getGameMode().equals(GameMode.SURVIVAL) || p.getGameMode().equals(GameMode.ADVENTURE))) { return; }
        ItemStack tool = p.getInventory().getItemInMainHand();
        if (!ToolType.HOE.getMaterials().contains(tool.getType())) { return; }

        if (!modManager.isToolViable(tool)) { return; }

        ItemMeta meta = tool.getItemMeta();
        List<String> lore = meta.getLore();

        Block b = e.getClickedBlock();

        boolean apply = false;

        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (b.getType().equals(Material.GRASS_BLOCK) ||
                b.getType().equals(Material.DIRT)) {
                apply = true;
            }
            if (!p.getWorld().getBlockAt(b.getLocation().add(0, 1, 0)).getType().equals(Material.AIR)) { //Case Block is on top of clicked Block -> No Soil Tilt -> no Exp
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void onAxeUse(PlayerInteractEvent e) {
        if (e.isCancelled()) { return; }
        Player p = e.getPlayer();
        if (!Lists.WORLDS.contains(p.getWorld().getName())) { return; }
        if (!(p.getGameMode().equals(GameMode.SURVIVAL) || p.getGameMode().equals(GameMode.ADVENTURE))) { return; }
        ItemStack tool = p.getInventory().getItemInMainHand();
        if (!ToolType.AXE.getMaterials().contains(tool.getType())) { return; }

        if (!modManager.isToolViable(tool)) { return; }

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

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void onShovelUse(PlayerInteractEvent e) {
        if (e.isCancelled()) { return; }
        Player p = e.getPlayer();
        if (!Lists.WORLDS.contains(p.getWorld().getName())) { return; }
        if (!(p.getGameMode().equals(GameMode.SURVIVAL) || p.getGameMode().equals(GameMode.ADVENTURE))) { return; }
        ItemStack tool = p.getInventory().getItemInMainHand();
        if (!ToolType.SHOVEL.getMaterials().contains(tool.getType())) { return; }

        if (!modManager.isToolViable(tool)) { return; }

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

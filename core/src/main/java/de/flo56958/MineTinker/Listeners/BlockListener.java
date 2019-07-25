package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.MTBlockBreakEvent;
import de.flo56958.MineTinker.Events.MTPlayerInteractEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Enchantable;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Modifiers.Types.Ender;
import de.flo56958.MineTinker.Modifiers.Types.Power;
import de.flo56958.MineTinker.Modifiers.Types.SilkTouch;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
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
import org.bukkit.inventory.meta.ItemMeta;

public class BlockListener implements Listener {

    private static final FileConfiguration config = Main.getPlugin().getConfig();
    private static final ModManager modManager = ModManager.instance();

    @SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        ItemStack tool = p.getInventory().getItemInMainHand();

        if (Lists.WORLDS.contains(p.getWorld().getName())) {
            return;
        }

        if (e.getBlock().getType().getHardness() == 0 && !(tool.getType() == Material.SHEARS || ToolType.HOE.getMaterials().contains(tool.getType()))) {
            return;
        }

        if (!(p.getGameMode().equals(GameMode.SURVIVAL) || p.getGameMode().equals(GameMode.ADVENTURE))) {
            return;
        }

        if (!modManager.isToolViable(tool)) {
            return;
        }
        if (!modManager.durabilityCheck(e, p, tool)) {
            return;
        }

        int expAmount = config.getInt("ExpPerBlockBreak");
        expAmount += config.getInt("ExtraExpPerBlock." + e.getBlock().getType().toString()); //adds 0 if not in found in config (negative values are also fine)

        modManager.addExp(p, tool, expAmount);

        //-------------------------------------------POWERCHECK---------------------------------------------
        if (Power.HASPOWER.get(p).get() && !ToolType.PICKAXE.getMaterials().contains(tool.getType())
                && e.getBlock().getDrops(tool).isEmpty() && !e.getBlock().getType().equals(Material.NETHER_WART)) { //Necessary for EasyHarvest NetherWard-Break
            e.setCancelled(true);
            return;
        }

        MTBlockBreakEvent event = new MTBlockBreakEvent(tool, e);
        Bukkit.getPluginManager().callEvent(event); //Event-Trigger for Modifiers

        //-------------------------------------------SPAWNERS---------------------------------------------
        //TODO: CHANGE TO NBT
        if (!Lists.WORLDS_SPAWNERS.contains(p.getWorld().getName())) {
            if (config.getBoolean("Spawners.enabled")) {
                if (e.getBlock().getState() instanceof CreatureSpawner && p.hasPermission("minetinker.spawners.mine")) {
                    if ((config.getBoolean("Spawners.onlyWithSilkTouch") && modManager.hasMod(tool, SilkTouch.instance()))
                            || !config.getBoolean("Spawners.onlyWithSilkTouch")) {

                        CreatureSpawner cs = (CreatureSpawner) e.getBlock().getState();
                        ItemStack s = new ItemStack(Material.SPAWNER, 1, e.getBlock().getData());
                        ItemMeta meta = s.getItemMeta();

                        if (meta != null) {
                            meta.setDisplayName(cs.getSpawnedType().toString());
                            s.setItemMeta(meta);
                        }

                        p.getWorld().dropItemNaturally(e.getBlock().getLocation(), s);
                        e.setExpToDrop(0);

                        ChatWriter.log(false, p.getDisplayName() + " successfully mined a Spawner!");
                    }
                }
            }
        }
    }


    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onClick(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Block b = e.getClickedBlock();

        if (b == null) {
            return;
        }

        ItemStack norm = p.getInventory().getItemInMainHand();

        if (e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            if (Ender.instance().getModItem().equals(norm)) {
                e.setCancelled(true);
            }
        } else if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (!p.isSneaking()) {
                if (b.getType().equals(Material.ANVIL)
                    || b.getType().equals(Material.CRAFTING_TABLE)
                    || b.getType().equals(Material.CHEST)
                    || b.getType().equals(Material.ENDER_CHEST)
                    || b.getType().equals(Material.DROPPER)
                    || b.getType().equals(Material.HOPPER)
                    || b.getType().equals(Material.DISPENSER)
                    || b.getType().equals(Material.TRAPPED_CHEST)
                    || b.getType().equals(Material.FURNACE)
                    || b.getType().equals(Material.ENCHANTING_TABLE)) {

                    return;
                }
            }

            if (norm.getType().equals(Material.EXPERIENCE_BOTTLE)) {
                return;
            }

            int temp = norm.getAmount();
            norm.setAmount(1);

            for (Modifier m : modManager.getAllowedMods()) {
                if (m.getModItem().equals(norm)) {
                    norm.setAmount(temp);
                    e.setCancelled(true);

                    return;
                }
            }

            norm.setAmount(temp);

            if (b.getType().equals(Material.getMaterial(config.getString("BlockToEnchantModifiers")))) {
                ItemStack item = p.getInventory().getItemInMainHand();

                for (Modifier m : modManager.getEnchantableMods()) {
                    if (m.getModItem().getType().equals(item.getType())) {
                        ((Enchantable) m).enchantItem(p, item);
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public static void onBlockPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        Block b = e.getBlockPlaced();
        BlockState bs = b.getState();

        //-------------------------------------------SPAWNERS---------------------------------------------
        //TODO: CHANGE TO NBT
        if (config.getBoolean("Spawners.enabled") && !Lists.WORLDS_SPAWNERS.contains(p.getWorld().getName())) {
            if (!p.hasPermission("minetinker.spawners.place") && b.getState() instanceof CreatureSpawner) {
                e.setCancelled(true);
                //return;
            } else if (p.hasPermission("minetinker.spawners.place") && b.getState() instanceof CreatureSpawner) {
                CreatureSpawner cs = (CreatureSpawner) bs;

                // TODO: Make safe
                cs.setSpawnedType(EntityType.fromName(e.getItemInHand().getItemMeta().getDisplayName()));
                bs.update(true);

                ChatWriter.log(false,  p.getDisplayName() + " successfully placed a Spawner!");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public static void onHoeUse(PlayerInteractEvent e) {
        Player p = e.getPlayer();

        if (Lists.WORLDS.contains(p.getWorld().getName())) {
            return;
        }

        if (!(p.getGameMode().equals(GameMode.SURVIVAL) || p.getGameMode().equals(GameMode.ADVENTURE))) {
            return;
        }

        ItemStack tool = p.getInventory().getItemInMainHand();

        if (!ToolType.HOE.getMaterials().contains(tool.getType())) {
            return;
        }

        if (!modManager.isToolViable(tool)) {
            return;
        }

        Block b = e.getClickedBlock();

        boolean apply = false;

        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && b != null) {
            if (b.getType().equals(Material.GRASS_BLOCK) || b.getType().equals(Material.DIRT))
                apply = true;

            if (!p.getWorld().getBlockAt(b.getLocation().add(0, 1, 0)).getType().equals(Material.AIR)) //Case Block is on top of clicked Block -> No Soil Tilt -> no Exp
                apply = false;
        }

        if (!apply) {
            return;
        }

        if (!modManager.durabilityCheck(e, p, tool)) {
            return;
        }

        modManager.addExp(p, tool, config.getInt("ExpPerBlockBreak"));

        MTPlayerInteractEvent event = new MTPlayerInteractEvent(tool, e);
        Bukkit.getPluginManager().callEvent(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public static void onAxeUse(PlayerInteractEvent e) {
        Player p = e.getPlayer();

        if (Lists.WORLDS.contains(p.getWorld().getName())) {
            return;
        }

        if (!(p.getGameMode().equals(GameMode.SURVIVAL) || p.getGameMode().equals(GameMode.ADVENTURE))) {
            return;
        }

        ItemStack tool = p.getInventory().getItemInMainHand();

        if (!ToolType.AXE.getMaterials().contains(tool.getType())) {
            return;
        }

        if (!modManager.isToolViable(tool)) {
            return;
        }

        boolean apply = false;

        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock() != null) {
            if (Lists.getWoodLogs().contains(e.getClickedBlock().getType()))
                apply = true;
            else if (Lists.getWoodWood().contains(e.getClickedBlock().getType()))
                apply = true;
        }

        if (!apply) {
            return;
        }

        if (!modManager.durabilityCheck(e, p, tool)) {
            return;
        }

        modManager.addExp(p, tool, config.getInt("ExpPerBlockBreak"));

        MTPlayerInteractEvent event = new MTPlayerInteractEvent(tool, e);
        Bukkit.getPluginManager().callEvent(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public static void onShovelUse(PlayerInteractEvent e) {
        Player p = e.getPlayer();

        if (Lists.WORLDS.contains(p.getWorld().getName())) {
            return;
        }

        if (!(p.getGameMode().equals(GameMode.SURVIVAL) || p.getGameMode().equals(GameMode.ADVENTURE))) {
            return;
        }

        ItemStack tool = p.getInventory().getItemInMainHand();

        if (!ToolType.SHOVEL.getMaterials().contains(tool.getType())) {
            return;
        }

        if (!modManager.isToolViable(tool)) {
            return;
        }

        boolean apply = false;

        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock() != null) {
            if (e.getClickedBlock().getType().equals(Material.GRASS_BLOCK)) {
                apply = true;
            }
        }

        if (!apply) {
            return;
        }

        if (!modManager.durabilityCheck(e, p, tool)) {
            return;
        }

        modManager.addExp(p, tool, config.getInt("ExpPerBlockBreak"));

        MTPlayerInteractEvent event = new MTPlayerInteractEvent(tool, e);
        Bukkit.getPluginManager().callEvent(event);
    }
}
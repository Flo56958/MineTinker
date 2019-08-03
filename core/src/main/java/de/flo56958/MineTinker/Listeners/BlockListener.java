package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.MTBlockBreakEvent;
import de.flo56958.MineTinker.Events.MTPlayerInteractEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Modifiers.Types.Ender;
import de.flo56958.MineTinker.Modifiers.Types.Power;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class BlockListener implements Listener {

    private static final FileConfiguration config = Main.getPlugin().getConfig();
    private static final ModManager modManager = ModManager.instance();

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player p = event.getPlayer();
        ItemStack tool = p.getInventory().getItemInMainHand();

        if (Lists.WORLDS.contains(p.getWorld().getName())) {
            return;
        }

        if (event.getBlock().getType().getHardness() == 0 && !(tool.getType() == Material.SHEARS || ToolType.HOE.contains(tool.getType()))) {
            return;
        }

        if (!(p.getGameMode() == GameMode.SURVIVAL || p.getGameMode() == GameMode.ADVENTURE)) {
            return;
        }

        if (!modManager.isToolViable(tool)) {
            return;
        }
        if (!modManager.durabilityCheck(event, p, tool)) {
            return;
        }

        int expAmount = config.getInt("ExpPerBlockBreak");
        expAmount += config.getInt("ExtraExpPerBlock." + event.getBlock().getType().toString()); //adds 0 if not in found in config (negative values are also fine)

        modManager.addExp(p, tool, expAmount);

        //-------------------------------------------POWERCHECK---------------------------------------------
        if (Power.HASPOWER.get(p).get() && !ToolType.PICKAXE.contains(tool.getType())
                && event.getBlock().getDrops(tool).isEmpty()
                && event.getBlock().getType() != Material.NETHER_WART) { //Necessary for EasyHarvest NetherWard-Break

            event.setCancelled(true);
            return;
        }

        MTBlockBreakEvent breakEvent = new MTBlockBreakEvent(tool, event);
        Bukkit.getPluginManager().callEvent(breakEvent); //Event-Trigger for Modifiers
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onClick(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        Block b = event.getClickedBlock();

        if (b == null) {
            return;
        }

        ItemStack norm = p.getInventory().getItemInMainHand();

        if (norm.getType() == Material.EXPERIENCE_BOTTLE) {
            return;
        }

        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            if (Ender.instance().getModItem().equals(norm)) {
                event.setCancelled(true);
            }
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (!p.isSneaking()) {
                Material type = b.getType();

                if (type == Material.ANVIL || type == Material.CRAFTING_TABLE
                    || type == Material.CHEST || type == Material.ENDER_CHEST
                    || type == Material.DROPPER || type == Material.HOPPER
                    || type == Material.DISPENSER || type == Material.TRAPPED_CHEST
                    || type == Material.FURNACE || type == Material.ENCHANTING_TABLE) {

                    return;
                }
            }

            int temp = norm.getAmount();
            norm.setAmount(1);

            for (Modifier m : modManager.getAllowedMods()) {
                if (m.getModItem().equals(norm)) {
                    norm.setAmount(temp);
                    event.setCancelled(true);

                    return;
                }
            }

            norm.setAmount(temp);

            if (b.getType() == Material.getMaterial(config.getString("BlockToEnchantModifiers"))) {
                ItemStack item = p.getInventory().getItemInMainHand();

                for (Modifier m : modManager.getAllMods()) {
                    if (m.getModItem().getType().equals(item.getType())) {
                        m.enchantItem(p, item);
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public static void onHoeUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (Lists.WORLDS.contains(player.getWorld().getName())) {
            return;
        }

        if (!(player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE)) {
            return;
        }

        ItemStack tool = player.getInventory().getItemInMainHand();

        if (!ToolType.HOE.contains(tool.getType())) {
            return;
        }

        if (!modManager.isToolViable(tool)) {
            return;
        }

        Block block = event.getClickedBlock();

        boolean apply = false;

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && block != null) {
            if (block.getType() == Material.GRASS_BLOCK || block.getType() == Material.DIRT)
                apply = true;

            if (player.getWorld().getBlockAt(block.getLocation().add(0, 1, 0)).getType() != Material.AIR) //Case Block is on top of clicked Block -> No Soil Tilt -> no Exp
                apply = false;
        }

        if (!apply) {
            return;
        }

        if (!modManager.durabilityCheck(event, player, tool)) {
            return;
        }

        modManager.addExp(player, tool, config.getInt("ExpPerBlockBreak"));

        MTPlayerInteractEvent interactEvent = new MTPlayerInteractEvent(tool, event);
        Bukkit.getPluginManager().callEvent(interactEvent);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public static void onAxeUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (Lists.WORLDS.contains(player.getWorld().getName())) {
            return;
        }

        if (!(player.getGameMode().equals(GameMode.SURVIVAL) || player.getGameMode().equals(GameMode.ADVENTURE))) {
            return;
        }

        ItemStack tool = player.getInventory().getItemInMainHand();

        if (!ToolType.AXE.contains(tool.getType())) {
            return;
        }

        if (!modManager.isToolViable(tool)) {
            return;
        }

        boolean apply = false;

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null) {
            if (Lists.getWoodLogs().contains(event.getClickedBlock().getType()))
                apply = true;
            else if (Lists.getWoodWood().contains(event.getClickedBlock().getType()))
                apply = true;
        }

        if (!apply) {
            return;
        }

        if (!modManager.durabilityCheck(event, player, tool)) {
            return;
        }

        modManager.addExp(player, tool, config.getInt("ExpPerBlockBreak"));

        MTPlayerInteractEvent interactEvent = new MTPlayerInteractEvent(tool, event);
        Bukkit.getPluginManager().callEvent(interactEvent);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public static void onShovelUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (Lists.WORLDS.contains(player.getWorld().getName())) {
            return;
        }

        if (!(player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE)) {
            return;
        }

        ItemStack tool = player.getInventory().getItemInMainHand();

        if (!ToolType.SHOVEL.contains(tool.getType())) {
            return;
        }

        if (!modManager.isToolViable(tool)) {
            return;
        }

        boolean apply = false;

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null) {
            if (event.getClickedBlock().getType() == Material.GRASS_BLOCK) {
                apply = true;
            }
        }

        if (!apply) {
            return;
        }

        if (!modManager.durabilityCheck(event, player, tool)) {
            return;
        }

        modManager.addExp(player, tool, config.getInt("ExpPerBlockBreak"));

        MTPlayerInteractEvent interactEvent = new MTPlayerInteractEvent(tool, event);
        Bukkit.getPluginManager().callEvent(interactEvent);
    }
}
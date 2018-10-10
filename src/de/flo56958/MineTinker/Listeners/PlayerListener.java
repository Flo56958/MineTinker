package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Data.PlayerData;
import de.flo56958.MineTinker.Data.Strings;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftInventory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PlayerListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!e.isCancelled()) {
            if (!Lists.WORLDS.contains(e.getWhoClicked().getWorld().getName())) { return; }
            if (!(e.getClickedInventory() instanceof PlayerInventory || e.getClickedInventory() instanceof DoubleChestInventory || e.getClickedInventory() instanceof CraftInventory)) { return; }

            ItemStack tool = e.getClickedInventory().getItem(e.getSlot());

            if (tool == null) { return; }
            if (!tool.hasItemMeta()) { return; }
            if (!tool.getItemMeta().hasLore()) { return; }
            if (!(tool.getItemMeta().getLore().contains(Strings.IDENTIFIER) || tool.getItemMeta().getLore().contains(Strings.IDENTIFIER_BUILDERSWAND))) { return; }

            if (Main.getPlugin().getConfig().getBoolean("Repairable") && e.getWhoClicked().hasPermission("minetinker.tool.repair")) {
                if (e.getWhoClicked().getItemOnCursor() != null) {
                    ItemStack repair = e.getWhoClicked().getItemOnCursor();
                    String[] name = tool.getType().toString().split("_");
                    boolean eligible = false;
                    if (name[0].toLowerCase().equals("wooden") &&
                            (repair.getType().equals(Material.ACACIA_PLANKS) ||
                             repair.getType().equals(Material.BIRCH_PLANKS) ||
                             repair.getType().equals(Material.DARK_OAK_PLANKS) ||
                             repair.getType().equals(Material.JUNGLE_PLANKS) ||
                             repair.getType().equals(Material.OAK_PLANKS) ||
                             repair.getType().equals(Material.SPRUCE_PLANKS))) {
                        eligible = true;
                    } else if (name[0].toLowerCase().equals("stone") && (repair.getType().equals(Material.COBBLESTONE) || repair.getType().equals(Material.STONE))) {
                        eligible = true;
                    } else if (name[0].toLowerCase().equals("iron") && repair.getType().equals(Material.IRON_INGOT)) {
                        eligible = true;
                    } else if (name[0].toLowerCase().equals("golden") && repair.getType().equals(Material.GOLD_INGOT)) {
                        eligible = true;
                    } else if (name[0].toLowerCase().equals("diamond") && repair.getType().equals(Material.DIAMOND)) {
                        eligible = true;
                    } else if (name[0].toLowerCase().equals("bow") && (repair.getType().equals(Material.STICK) || repair.getType().equals(Material.STRING))) {
                        eligible = true;
                    } else if (name[0].toLowerCase().equals("shield") &&
                            (repair.getType().equals(Material.ACACIA_PLANKS) ||
                             repair.getType().equals(Material.BIRCH_PLANKS) ||
                             repair.getType().equals(Material.DARK_OAK_PLANKS) ||
                             repair.getType().equals(Material.JUNGLE_PLANKS) ||
                             repair.getType().equals(Material.OAK_PLANKS) ||
                             repair.getType().equals(Material.SPRUCE_PLANKS))) {
                        eligible = true;
                    }
                    if (eligible) {
                        double dura = tool.getDurability();
                        double maxDura = tool.getType().getMaxDurability();
                        int amount = e.getWhoClicked().getItemOnCursor().getAmount();
                        double percent = Main.getPlugin().getConfig().getDouble("DurabilityPercentageRepair");
                        while (amount > 0 && dura > 0) {
                            dura = dura - (maxDura * percent);
                            amount--;
                        }
                        if (dura < 0) {
                            dura = 0;
                        }
                        tool.setDurability((short) dura);
                        e.getWhoClicked().getItemOnCursor().setAmount(amount);
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        PlayerData.BLOCKFACE.put(e.getPlayer(), null);
        PlayerData.HASPOWER.put(e.getPlayer(), false);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        PlayerData.BLOCKFACE.remove(e.getPlayer());
        PlayerData.HASPOWER.remove(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInteract(PlayerInteractEvent e) {
        if (!Lists.WORLDS.contains(e.getPlayer().getWorld().getName())) { return; }
        if (!e.getBlockFace().equals(BlockFace.SELF)) {
            PlayerData.BLOCKFACE.replace(e.getPlayer(), e.getBlockFace());
        }
    }
}

package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Data.PlayerData;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Types.Power;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import org.bukkit.ChatColor;
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

    private static final ModManager modManager = ModManager.instance();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.isCancelled()) { return; }
        if (!Lists.WORLDS.contains(e.getWhoClicked().getWorld().getName())) { return; }
        if (!(e.getClickedInventory() instanceof PlayerInventory || e.getClickedInventory() instanceof DoubleChestInventory || e.getClickedInventory() instanceof CraftInventory)) { return; }

        ItemStack tool = e.getClickedInventory().getItem(e.getSlot());

        if (!(modManager.isToolViable(tool) || modManager.isWandViable(tool) || modManager.isArmorViable(tool))) { return; }

        if (!(Main.getPlugin().getConfig().getBoolean("Repairable") && e.getWhoClicked().hasPermission("minetinker.tool.repair"))) { return; }

        if (e.getWhoClicked().getItemOnCursor() == null) { return; }

        ItemStack repair = e.getWhoClicked().getItemOnCursor();
        String[] name = tool.getType().toString().split("_");
        boolean eligible = false;
        if (name[0].toLowerCase().equals("wooden") && Lists.getWoodPlanks().contains(repair.getType())) {
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
        } else if (name[0].toLowerCase().equals("shield") && Lists.getWoodPlanks().contains(repair.getType())) {
            eligible = true;
        } else if (name[0].toLowerCase().equals("leather") && repair.getType().equals(Material.LEATHER)) {
            eligible = true;
        } else if (name[0].toLowerCase().equals("chainmail") && repair.getType().equals(Material.IRON_BARS)) {
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

    /**
     * Adds the Player to the HashMaps BLOCKFACE and HASPOWER
     * @param e PlayerJoinEvent
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        PlayerData.BLOCKFACE.put(e.getPlayer(), null);
        Power.HASPOWER.put(e.getPlayer(), false);
        if (Main.getPlugin().getConfig().getBoolean("SendUpdateNotificationToOPs")) {
            if (e.getPlayer().isOp()) {
                if (Main.getUpdater() != null) {
                    if (Main.getUpdater().getHasUpdate()) {
                        ChatWriter.sendMessage(e.getPlayer(), ChatColor.GOLD, "There's is an update available on spigotmc.org!");
                        ChatWriter.sendMessage(e.getPlayer(), ChatColor.WHITE, "Your version: " + Main.getPlugin().getDescription().getVersion());
                        ChatWriter.sendMessage(e.getPlayer(), ChatColor.WHITE, "Online version: " + Main.getUpdater().getOnlineVersion());
                    }
                }
            }
        }
    }

    /**
     * Removes the Player form the HashMaps BLOCKFACE and HASPOWER
     * @param e PlayerQuitEvent
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        PlayerData.BLOCKFACE.remove(e.getPlayer());
        Power.HASPOWER.remove(e.getPlayer());
    }

    /**
     * Updates the HashMap BLOCKFACE with the clicked face of the Block
     * @param e PlayerInteractEvent
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onInteract(PlayerInteractEvent e) {
        if (!Lists.WORLDS.contains(e.getPlayer().getWorld().getName())) { return; }
        if (!e.getBlockFace().equals(BlockFace.SELF)) {
            PlayerData.BLOCKFACE.replace(e.getPlayer(), e.getBlockFace());
        }
    }
}

package de.flo56958.minetinker.listeners;

import de.flo56958.minetinker.data.Lists;
import de.flo56958.minetinker.Main;
import de.flo56958.minetinker.modifiers.ModManager;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.modifiers.types.Power;
import de.flo56958.minetinker.utilities.ChatWriter;
import de.flo56958.minetinker.utilities.Updater;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftInventory;
import org.bukkit.enchantments.Enchantment;
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
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class PlayerListener implements Listener {

    private static final ModManager modManager = ModManager.instance();

    @SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent e) {
        if (Lists.WORLDS.contains(e.getWhoClicked().getWorld().getName())) return;
        if (e.getSlot() < 0) return;
        if (!(e.getClickedInventory() instanceof PlayerInventory || e.getClickedInventory() instanceof DoubleChestInventory || e.getClickedInventory() instanceof CraftInventory)) return;

        ItemStack tool = e.getClickedInventory().getItem(e.getSlot());

        if (!(modManager.isToolViable(tool) || modManager.isWandViable(tool) || modManager.isArmorViable(tool))) return;
        if (!(Main.getPlugin().getConfig().getBoolean("Repairable") && e.getWhoClicked().hasPermission("minetinker.tool.repair"))) return;
        if (tool == null) return;

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
        } else if (name[0].toLowerCase().equals("elytra") && repair.getType().equals(Material.PHANTOM_MEMBRANE)) {
            eligible = true;
        } else if (name[0].toLowerCase().equals("trident") && repair.getType().equals(Material.PRISMARINE_SHARD)) {
            eligible = true;
        } else if (name[0].toLowerCase().equals("turtle") && repair.getType().equals(Material.SCUTE)) {
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
        Lists.BLOCKFACE.put(e.getPlayer(), null);
        Power.HASPOWER.computeIfAbsent(e.getPlayer(), player -> new AtomicBoolean(false));

        if (Main.getPlugin().getConfig().getBoolean("CheckForUpdates")) {
            if (e.getPlayer().hasPermission("minetinker.update.notify")) {
                if (Updater.hasUpdate()) {
                    ChatWriter.sendMessage(e.getPlayer(), ChatColor.GOLD, "There's is an update available on spigotmc.org!");
                    ChatWriter.sendMessage(e.getPlayer(), ChatColor.WHITE, "Your version: " + Main.getPlugin().getDescription().getVersion());
                    ChatWriter.sendMessage(e.getPlayer(), ChatColor.WHITE, "Online version: " + Updater.getOnlineVersion());
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
        Lists.BLOCKFACE.remove(e.getPlayer());
        Power.HASPOWER.remove(e.getPlayer());
    }

    /**
     * Updates the HashMap BLOCKFACE with the clicked face of the Block
     * @param e PlayerInteractEvent
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onInteract(PlayerInteractEvent e) {
        if (Lists.WORLDS.contains(e.getPlayer().getWorld().getName())) return;

        if (!e.getBlockFace().equals(BlockFace.SELF)) {
            Lists.BLOCKFACE.replace(e.getPlayer(), e.getBlockFace());
        }

        if (!modManager.allowBookToModifier()) return;

        if (e.getClickedBlock() == null || e.getClickedBlock().getType() != Material.BOOKSHELF) return;
        if (e.getItem() == null || e.getItem().getType() != Material.ENCHANTED_BOOK) return;
        if (e.getItem().getItemMeta() == null || !(e.getItem().getItemMeta() instanceof EnchantmentStorageMeta)) return;

        EnchantmentStorageMeta meta = (EnchantmentStorageMeta)e.getItem().getItemMeta();

        for (Map.Entry<Enchantment, Integer> entry : meta.getStoredEnchants().entrySet()) {
            Modifier modifier = modManager.getModifierFromEnchantment(entry.getKey());

            if (modifier == null) continue;

            ItemStack modDrop = modifier.getModItem();
            modDrop.setAmount(entry.getValue());

            e.getClickedBlock().getWorld().dropItem(e.getClickedBlock().getLocation(), modDrop);

            meta.removeStoredEnchant(entry.getKey());
        }

        if (meta.getStoredEnchants().isEmpty()) {
            e.getPlayer().getInventory().removeItem(e.getItem());
        } else {
            e.getItem().setItemMeta(meta);
        }
    }
}

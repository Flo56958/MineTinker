package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Modifiers.Types.Power;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.Updater;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
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
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class PlayerListener implements Listener {

    private static final ModManager modManager = ModManager.instance();

	@EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent e) {
        if (Lists.WORLDS.contains(e.getWhoClicked().getWorld().getName())) {
            return;

        }
        if (e.getSlot() < 0) {
            return;
        }

        if (!(e.getClickedInventory() instanceof PlayerInventory || e.getClickedInventory() instanceof DoubleChestInventory)) {
            return;
        }

        ItemStack tool = e.getClickedInventory().getItem(e.getSlot());

        if (tool == null) {
            return;
        }

        if (!(modManager.isToolViable(tool) || modManager.isWandViable(tool) || modManager.isArmorViable(tool))) {
            return;
        }

        if (!(Main.getPlugin().getConfig().getBoolean("Repairable") && e.getWhoClicked().hasPermission("minetinker.tool.repair"))) {
            return;
        }

        ItemStack repair = e.getWhoClicked().getItemOnCursor();
        String[] name = tool.getType().toString().split("_");

        boolean eligible = false;

        String beginning = name[0].toLowerCase();

        switch (beginning) {
            case "shield":
            case "wooden":
                if (Lists.getWoodPlanks().contains(repair.getType())) {
                    eligible = true;
                }
                break;
            case "stone":
                if (repair.getType().equals(Material.COBBLESTONE) || repair.getType().equals(Material.STONE)) {
                    eligible = true;
                }
                break;
            case "shears":
            case "iron":
                if (repair.getType().equals(Material.IRON_INGOT)) {
                    eligible = true;
                }
                break;
            case "golden":
                if (repair.getType().equals(Material.GOLD_INGOT)) {
                    eligible = true;
                }
                break;
            case "diamond":
                if (repair.getType().equals(Material.DIAMOND)) {
                    eligible = true;
                }
                break;
            case "bow":
            case "crossbow":
            case "fishing":
                if (repair.getType().equals(Material.STICK) || repair.getType().equals(Material.STRING)) {
                    eligible = true;
                }
                break;
            case "leather":
                if (repair.getType().equals(Material.LEATHER)) {
                    eligible = true;
                }
                break;
            case "chainmail":
                if (repair.getType().equals(Material.IRON_BARS)) {
                    eligible = true;
                }
                break;
            case "elytra":
                if (repair.getType().equals(Material.PHANTOM_MEMBRANE)) {
                    eligible = true;
                }
                break;
            case "trident":
                if (repair.getType().equals(Material.PRISMARINE_SHARD)) {
                    eligible = true;
                }
                break;
            case "turtle":
                if (repair.getType().equals(Material.SCUTE)) {
                    eligible = true;
                }
                break;
        }

        if (eligible) {
            Damageable meta = (Damageable)tool;

            int dura = meta.getDamage();
            short maxDura = tool.getType().getMaxDurability();
            int amount = e.getWhoClicked().getItemOnCursor().getAmount();
            float percent = (float)Main.getPlugin().getConfig().getDouble("DurabilityPercentageRepair");

            while (amount > 0 && dura > 0) {
                dura = (int)(dura - (maxDura * percent));
                amount--;
            }

            if (dura < 0) {
                dura = 0;
            }

            meta.setDamage(dura);

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
        if (Lists.WORLDS.contains(e.getPlayer().getWorld().getName())) {
            return;
        }

        if (!e.getBlockFace().equals(BlockFace.SELF)) {
            Lists.BLOCKFACE.replace(e.getPlayer(), e.getBlockFace());
        }

        if (!modManager.allowBookToModifier()) {
            return;
        }

        if (e.getClickedBlock() == null || e.getClickedBlock().getType() != Material.BOOKSHELF) {
            return;
        }

        if (e.getItem() == null || e.getItem().getType() != Material.ENCHANTED_BOOK) {
            return;
        }

        if (e.getItem().getItemMeta() == null || !(e.getItem().getItemMeta() instanceof EnchantmentStorageMeta)) {
            return;
        }

        EnchantmentStorageMeta meta = (EnchantmentStorageMeta)e.getItem().getItemMeta();

        for (Map.Entry<Enchantment, Integer> entry : meta.getStoredEnchants().entrySet()) {
            Modifier modifier = modManager.getModifierFromEnchantment(entry.getKey());

            if (modifier == null) {
                continue;
            }

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

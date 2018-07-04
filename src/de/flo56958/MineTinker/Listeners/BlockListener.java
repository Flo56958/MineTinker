package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Data.Modifiers;
import de.flo56958.MineTinker.Data.Strings;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.LevelCalculator;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class BlockListener implements Listener {

    @EventHandler (priority = EventPriority.LOW)
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.getPlayer().getGameMode().equals(GameMode.SURVIVAL) || e.getPlayer().getGameMode().equals(GameMode.ADVENTURE)) {
            ItemStack tool = e.getPlayer().getInventory().getItemInMainHand();
            ItemMeta meta = tool.getItemMeta();
            if (!tool.getType().equals(Material.AIR)) {
                if (meta.hasLore()) {
                    ArrayList<String> lore = (ArrayList<String>) meta.getLore();
                    if (lore.contains(Strings.IDENTIFIER)) {
                        LevelCalculator.addExp(e.getPlayer(), tool, 1);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (e.getClickedBlock().getType().equals(Material.BOOKSHELF)) {
                if (Main.getPlugin().getConfig().getBoolean("Modifiers.Auto-Repair.allowed")) {
                    if (e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.MOSSY_COBBLESTONE)) {
                        if (e.getPlayer().getGameMode().equals(GameMode.SURVIVAL) || e.getPlayer().getGameMode().equals(GameMode.ADVENTURE)) {
                            int amount = e.getPlayer().getInventory().getItemInMainHand().getAmount();
                            if (e.getPlayer().getLevel() >= Main.getPlugin().getConfig().getInt("Modifiers.Auto-Repair.EnchantCost")) {
                                int newLevel = e.getPlayer().getLevel() - Main.getPlugin().getConfig().getInt("Modifiers.Auto-Repair.EnchantCost");
                                e.getPlayer().setLevel(newLevel);
                                e.getPlayer().getInventory().getItemInMainHand().setAmount(amount - 1);
                                e.getPlayer().getLocation().getWorld().dropItemNaturally(e.getPlayer().getLocation(), Modifiers.AUTOREPAIR_MODIFIER);
                                if (Main.getPlugin().getConfig().getBoolean("Sound.OnEnchanting")) {
                                    e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 0.5F);
                                }
                                } else {
                                    ChatWriter.sendMessage(e.getPlayer(), ChatColor.RED, "You do not have enough Levels to perform this action!");
                                    ChatWriter.sendMessage(e.getPlayer(), ChatColor.RED, Main.getPlugin().getConfig().getInt("Modifiers.Auto-Repair.EnchantCost") + " levels are required!");
                                }
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }
    }
}

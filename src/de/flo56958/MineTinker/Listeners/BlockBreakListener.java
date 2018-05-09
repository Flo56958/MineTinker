package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Utilities.Events;
import de.flo56958.MineTinker.Utilities.LevelCalculator;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class BlockBreakListener implements Listener {

    @EventHandler (priority = EventPriority.LOW)
    public void onBlockBreak(BlockBreakEvent e) {
        ItemStack tool = e.getPlayer().getInventory().getItemInMainHand();
        ItemMeta meta = tool.getItemMeta();
        ArrayList<String> lore = (ArrayList<String>) meta.getLore();
        if(lore.contains(ChatColor.WHITE + "MineTinker-Tool")) {
            LevelCalculator.addExp(e.getPlayer(), tool, 1);
        }
    }
}

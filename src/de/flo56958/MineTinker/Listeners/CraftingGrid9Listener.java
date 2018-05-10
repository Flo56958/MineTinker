package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import de.flo56958.MineTinker.Utilities.LevelCalculator;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CraftingGrid9Listener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onCraft(CraftItemEvent e) {
        System.out.println(e.getCurrentItem().getType().toString());
        List<String> Tools = (List<String>) Main.getPlugin().getConfig().getList("AllowedTools");
        for (String current : Tools) {
            if (e.getCurrentItem().getType().toString().equals(current)) {
                ArrayList<String> lore = new ArrayList<>();
                lore.add(ChatColor.WHITE + "MineTinker-Tool");
                lore.add(ChatColor.GOLD + "Level:" + ChatColor.WHITE + " 1");
                lore.add(ChatColor.GOLD + "Exp:" + ChatColor.WHITE + " 0 / " + LevelCalculator.getNextLevelReq(1));
                lore.add(ChatColor.WHITE + "Free Modifier Slots: " + "1");
                lore.add(ChatColor.WHITE + "Modifiers:");
                ItemStack temp = ItemGenerator.changeItem(e.getCurrentItem(), lore);
                e.setCurrentItem(temp);
                if (Main.getPlugin().getConfig().getBoolean("Sound.OnCrafting")) {
                    ((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(), Sound.BLOCK_ANVIL_USE, 1.0F, 0.5F);
                }
                break;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onClick(InventoryClickEvent e) {
        if (e.getClickedInventory() instanceof CraftingInventory) {
            if (e.getSlot() != 5) {
                ItemStack[] contents = e.getClickedInventory().getContents();
                for (int i = 0; i < contents.length; i++) {
                }
            }
        }
    }
}

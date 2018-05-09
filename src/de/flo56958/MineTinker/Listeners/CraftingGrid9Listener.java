package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Utilities.ItemGenerator;
import de.flo56958.MineTinker.Utilities.LevelCalculator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class CraftingGrid9Listener implements Listener {

    private Material[] Tools = new Material[] {Material.WOOD_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD,
                                               Material.WOOD_AXE, Material.STONE_AXE, Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE,
                                               Material.WOOD_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE, Material.GOLD_PICKAXE, Material.DIAMOND_PICKAXE,
                                               Material.WOOD_SPADE, Material.STONE_SPADE, Material.IRON_SPADE, Material.GOLD_SPADE, Material.DIAMOND_SPADE};

    @EventHandler(priority = EventPriority.LOW)
    public void onCraft(CraftItemEvent e) {
        for (int i = 0; i < Tools.length; i++) {
            if (e.getCurrentItem().getType() == Tools[i]) {
                ArrayList<String> lore = new ArrayList<>();
                lore.add(ChatColor.WHITE + "MineTinker-Tool");
                lore.add(ChatColor.GOLD + "Level:" + ChatColor.WHITE + " 1");
                lore.add(ChatColor.GOLD + "Exp:" + ChatColor.WHITE + " 0 / " + LevelCalculator.getNextLevelReq(1));
                ItemStack temp = ItemGenerator.changeItem(e.getCurrentItem(), lore);
                e.setCurrentItem(temp);
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
                    System.out.println(i + ": " + contents[i].toString());
                }
            }
        }
    }
}

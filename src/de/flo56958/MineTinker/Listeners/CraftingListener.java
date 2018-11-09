package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

import java.util.ArrayList;

public class CraftingListener implements Listener {

    private static final FileConfiguration config = Main.getPlugin().getConfig();

    @EventHandler
    public void onCraft(CraftItemEvent e) {
        ArrayList<Material> tools = new ArrayList<>(ToolType.SWORD.getMaterials()); //Setting up List of tools
        tools.addAll(ToolType.AXE.getMaterials());
        tools.addAll(ToolType.PICKAXE.getMaterials());
        tools.addAll(ToolType.SHOVEL.getMaterials());
        tools.addAll(ToolType.HOE.getMaterials());
        tools.addAll(ToolType.BOW.getMaterials());
        if (e.isCancelled()) { return; }
        if (!Lists.WORLDS.contains(e.getWhoClicked().getWorld().getName())) { return; }
        if (!e.getWhoClicked().hasPermission("minetinker.tool.create")) { return; }
        if (tools.contains(e.getCurrentItem().getType())) {
            if (e.getCurrentItem().hasItemMeta()) {
                if (config.getBoolean("Sound.OnCrafting") || config.getBoolean("Sound.OnEveryCrafting")) {
                    ((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(), Sound.BLOCK_ANVIL_USE, 1.0F, 0.5F);
                }
                return;
            }
            e.setCurrentItem(ItemGenerator.changeLore(e.getCurrentItem(), ItemGenerator.createLore()));
            if (config.getBoolean("Sound.OnCrafting") || config.getBoolean("Sound.OnEveryCrafting")) {
                ((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(), Sound.BLOCK_ANVIL_USE, 1.0F, 0.5F);
            }
            ChatWriter.log(false, e.getWhoClicked().getName() + " crafted " + ItemGenerator.getDisplayName(e.getCurrentItem()) + "! It is now a MineTinker-Tool!");
        } else {
            if (config.getBoolean("Sound.OnEveryCrafting")) {
                ((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(), Sound.BLOCK_ANVIL_USE, 1.0F, 0.5F);
            }
        }
    }
}

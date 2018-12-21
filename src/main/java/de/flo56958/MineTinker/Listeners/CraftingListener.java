package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

public class CraftingListener implements Listener {

    private static final FileConfiguration config = Main.getPlugin().getConfig();
    private static final ModManager modManager = ModManager.instance();

    private static ArrayList<ToolType> tools;
    private static ArrayList<ToolType> armor;

    static {
        tools = new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.HOE, ToolType.PICKAXE, ToolType.SHOVEL, ToolType.SWORD));
        armor = new ArrayList<>(Arrays.asList(ToolType.HELMET, ToolType.CHESTPLATE, ToolType.LEGGINGS, ToolType.BOOTS, ToolType.ELYTRA));
    }

    @EventHandler
    public void onCraft(CraftItemEvent e) {
        if (e.isCancelled()) { return; }
        if (!(e.getWhoClicked() instanceof Player)) { return; }
        Player player = (Player) e.getWhoClicked();

        if (!player.hasPermission("minetinker.tool.create")) { return; }

        ItemStack currentItem = e.getCurrentItem().clone();

        if (Lists.WORLDS.contains(player.getWorld().getName())) { return; }

        ArrayList<String> lore = new ArrayList<>();
        if (tools.contains(ToolType.get(currentItem.getType()))) {
            lore.add(modManager.IDENTIFIER_TOOL);
        } else if (armor.contains(ToolType.get(currentItem.getType()))) {
            lore.add(modManager.IDENTIFIER_ARMOR);
        } else {
            if (config.getBoolean("Sound.OnEveryCrafting"))
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0F, 0.5F);
            return;
        }

        lore.addAll(ItemGenerator.createLore());

        currentItem = ItemGenerator.changeLore(currentItem, lore);
        e.setCurrentItem(currentItem);

        //Shift Clicking -> Add All CraftItems (converted) + cancel event
        if(e.isShiftClick()) {
            CraftingInventory inv = e.getInventory();
            if(inv == null) { return; }

            ItemStack[] contents = inv.getContents();
            if(contents == null) { return; }

            //Search min Item in 9x9 Field -> Max Items can be crafted
            int min = -1;

            for(int i = 1; i < 9; i++) {
                ItemStack is = inv.getItem(i);
                if(is != null && is.getAmount() > 0 && (min == -1 || is.getAmount() < min)) {
                    min = is.getAmount();
                }
            }

            //Add All Items
            int counter = 0;
            for(int i = min; i > 0; i--) {
                if (player.getInventory().addItem(currentItem.clone()).size() != 0) { //adds items to (full) inventory and then case if inventory is full
                    break;
                } // no else as it gets added in if-clause
                counter++;
            }

            //Remove all used Items in CraftingField
            for(ItemStack is : contents) {
                if(is != null && is.getAmount() > 0) {
                    is.setAmount(is.getAmount() - counter);
                }
            }

            e.setCancelled(true);
        }

        if (config.getBoolean("Sound.OnCrafting") || config.getBoolean("Sound.OnEveryCrafting"))
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0F, 0.5F);

        ChatWriter.log(false, player.getName() + " crafted " + ItemGenerator.getDisplayName(currentItem) + "! It is now a MineTinker-Item!");
    }
}
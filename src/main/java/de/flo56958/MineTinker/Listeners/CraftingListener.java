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

import java.util.ArrayList;
import java.util.Arrays;

public class CraftingListener implements Listener {

    private static final FileConfiguration config = Main.getPlugin().getConfig();
    private static final ModManager modManager = ModManager.instance();

    @EventHandler
    public void onCraft(CraftItemEvent e) {
        ArrayList<ToolType> tools = new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.HOE, ToolType.PICKAXE, ToolType.SHOVEL, ToolType.SWORD));

        ArrayList<ToolType> armor = new ArrayList<>(Arrays.asList(ToolType.HELMET, ToolType.CHESTPLATE, ToolType.LEGGINGS, ToolType.BOOTS, ToolType.ELYTRA));
        if (e.isCancelled()) { return; }
        if (!(e.getWhoClicked() instanceof Player)) { return; }
        if (Lists.WORLDS.contains(e.getWhoClicked().getWorld().getName())) { return; }
        if (!e.getWhoClicked().hasPermission("minetinker.tool.create")) { return; }

        ArrayList<String> lore = new ArrayList<>();
        if (tools.contains(ToolType.get(e.getCurrentItem().getType()))) {
            lore.add(modManager.IDENTIFIER_TOOL);
        } else if (armor.contains(ToolType.get(e.getCurrentItem().getType()))) {
            lore.add(modManager.IDENTIFIER_ARMOR);
        } else {
            if (config.getBoolean("Sound.OnEveryCrafting")) {
                ((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(), Sound.BLOCK_ANVIL_USE, 1.0F, 0.5F);
            }
            return;
        }

        lore.add(modManager.LEVELLINE + "1");
        lore.add(modManager.EXPLINE + "0 / " + modManager.getNextLevelReq(1));
        lore.add(modManager.FREEMODIFIERSLOTS + config.getInt("StartingModifierSlots"));
        lore.add(modManager.MODIFIERSTART);

        if (e.getCurrentItem().hasItemMeta()) {
            if (config.getBoolean("Sound.OnCrafting") || config.getBoolean("Sound.OnEveryCrafting")) {
                ((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(), Sound.BLOCK_ANVIL_USE, 1.0F, 0.5F);
            }
            return;
        }

        e.setCurrentItem(ItemGenerator.changeLore(e.getCurrentItem(), lore));

        if (config.getBoolean("Sound.OnCrafting") || config.getBoolean("Sound.OnEveryCrafting")) {
            ((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(), Sound.BLOCK_ANVIL_USE, 1.0F, 0.5F);
        }
        ChatWriter.log(false, e.getWhoClicked().getName() + " crafted " + ItemGenerator.getDisplayName(e.getCurrentItem()) + "! It is now a MineTinker-Item!");

    }
}

package de.flo56958.MineTinker.Listeners;

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
import org.bukkit.inventory.ItemStack;

public class CraftItemListener implements Listener {

	private static final FileConfiguration config = Main.getPlugin().getConfig();
    private static final ModManager modManager = ModManager.instance();
	
	@EventHandler(ignoreCancelled = true)
    public void onCraft(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
        	return;
		}

        Player player = (Player) event.getWhoClicked();
        
        if (config.getBoolean("Sound.OnEveryCrafting")) {
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0F, 0.5F);

            return;
        }
        
        ItemStack tool = event.getInventory().getResult();
        
        if (!(modManager.isToolViable(tool) || modManager.isArmorViable(tool) || modManager.isWandViable(tool))) {
        	return;
		}

        if (config.getBoolean("Sound.OnCrafting")) {
        	player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0F, 0.5F);
		}

        if (tool != null) {
			ChatWriter.log(false, player.getName() + " crafted " + ItemGenerator.getDisplayName(tool) + "! It is now a MineTinker-Item!");
		}
    }
}

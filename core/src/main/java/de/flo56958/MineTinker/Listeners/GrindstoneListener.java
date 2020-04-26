package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.LanguageManager;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.ItemStack;

public class GrindstoneListener implements Listener {

	private final ModManager modManager = ModManager.instance();

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onGrind(InventoryClickEvent event) {
		FileConfiguration config = Main.getPlugin().getConfig();

		if(config.getBoolean("AllowGrindstone")) {
			return;
		}

		if (!(event.getInventory() instanceof GrindstoneInventory)) {
			return;
		}

		// Avoid handling the clicks inside of the inventory.
		if(event.getSlotType() != InventoryType.SlotType.RESULT && event.getSlotType() != InventoryType.SlotType.CRAFTING) {
			return;
		}

		// Works fine even if the getItem method returns null.
		ItemStack slot1 =  event.getClickedInventory().getItem(0);
		ItemStack slot2 =  event.getClickedInventory().getItem(1);

		if (!(modManager.isToolViable(slot1) || modManager.isArmorViable(slot1) || modManager.isToolViable(slot2) || modManager.isArmorViable(slot2))) {
			return;
		}

		Player player = (Player) event.getWhoClicked();

		if(event.getSlotType() != InventoryType.SlotType.RESULT) {
			return;
		}

		event.setResult(Event.Result.DENY);
		event.setCancelled(true);

		ChatWriter.sendActionBar(player, LanguageManager.getString("Alert.OnItemGrind", player));
		player.playSound(player.getLocation(), Sound.BLOCK_CHEST_LOCKED, 1.0F, 2F);
	}

	@EventHandler
	public void onGrid(CraftItemEvent event) {
		//TODO: Implement me
	}
}
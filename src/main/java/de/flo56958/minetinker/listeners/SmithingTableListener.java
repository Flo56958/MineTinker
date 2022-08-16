package de.flo56958.minetinker.listeners;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.modifiers.ModManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.ItemStack;

public class SmithingTableListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onSmithPrepare(PrepareSmithingEvent event) {
		if (ModManager.instance().isArmorViable(event.getResult())) {
			//The attributes need to be reapplied
			ModManager.instance().addArmorAttributes(event.getResult());
		}

		if (!MineTinker.getPlugin().getConfig().getBoolean("UseSmithingTable", false)) {
			return;
		}

		ItemStack newTool = AnvilListener.onPrepare(event.getInventory(), event.getViewers());

		if (newTool != null) {
			event.setResult(newTool);
		}
	}
}

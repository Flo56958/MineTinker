package de.flo56958.minetinker.listeners;

import de.flo56958.minetinker.modifiers.ModManager;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class TridentListener implements Listener {

	static final HashMap<Trident, ItemStack> TridentToItemStack = new HashMap<>();

	@EventHandler(ignoreCancelled = true)
	public void onTridentLaunch(@NotNull final ProjectileLaunchEvent event) {
		if (!(event.getEntity().getShooter() instanceof Player)) {
			return;
		}

		if (!(event.getEntity() instanceof Trident)) {
			return;
		}

		final Player player = (Player) event.getEntity().getShooter();
		final ItemStack trident = player.getInventory().getItemInMainHand().clone();

		if (!ModManager.instance().isToolViable(trident)) {
			return;
		}

		ModManager.instance().addExp(player, trident, -20000);
		//trident is a item clone and only for triggering modifier effects
		//this makes sure that the item duplicate does not get any level ups
		TridentToItemStack.put((Trident) event.getEntity(), trident);
	}
}

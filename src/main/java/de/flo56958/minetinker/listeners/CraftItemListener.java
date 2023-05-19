package de.flo56958.minetinker.listeners;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.modifiers.ModManager;
import de.flo56958.minetinker.utils.ChatWriter;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CraftItemListener implements Listener {

	private static final ModManager modManager = ModManager.instance();

	@EventHandler(ignoreCancelled = true)
	public void onCraft(@NotNull final CraftItemEvent event) {
		if (!(event.getWhoClicked() instanceof final Player player)) {
			return;
		}

		final FileConfiguration config = MineTinker.getPlugin().getConfig();

		if (config.getBoolean("Sound.OnEveryCrafting")) {
			player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0F, 0.5F);
			return;
		}

		final ItemStack tool = event.getInventory().getResult();

		if (!(modManager.isToolViable(tool) || modManager.isArmorViable(tool) || modManager.isWandViable(tool))) {
			return;
		}

		//If the tools are stacked because of a different plugin we do not want to interfere with that plugin
		//tool.setAmount(1);

		if (config.getBoolean("Sound.OnCrafting")) {
			player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0F, 0.5F);
		}

		ChatWriter.log(false, player.getName() + " crafted " + ChatWriter.getDisplayName(tool)
					+ "! It is now a MineTinker-Item!");
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPrepare(@NotNull final PrepareItemCraftEvent event) {
		if (MineTinker.getPlugin().getConfig().getBoolean("ModifiersCanBeUsedForCrafting")) return;
		final CraftingInventory inv = event.getInventory();
		for (ItemStack is : inv.getMatrix()) {
			if (is == null) continue;
			if (modManager.isModifierItem(is)) {
				inv.setResult(null);
				break;
			}
		}
	}
}

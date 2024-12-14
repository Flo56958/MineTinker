package de.flo56958.minetinker.utils.playerconfig;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface PlayerConfigurationInterface {

	String getPCIKey();

	String getPCIDisplayName();

	default ChatColor getPCIDisplayColor() {
		return ChatColor.WHITE;
	};

	List<PlayerConfigurationOption> getPCIOptions();

	default ItemStack getPCIDisplayItem() {
		return new ItemStack(Material.DIRT);
	}

}

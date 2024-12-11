package de.flo56958.minetinker.utils.playerconfig;

import org.bukkit.ChatColor;

import java.util.List;

public interface PlayerConfigurationInterface {

	String getPCIKey();

	String getPCIDisplayName();

	default ChatColor getPCIDisplayColor() {
		return ChatColor.WHITE;
	};

	List<PlayerConfigurationOption> getPCIOptions();

}

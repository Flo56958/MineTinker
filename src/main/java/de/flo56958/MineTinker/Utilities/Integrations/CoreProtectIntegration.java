package de.flo56958.MineTinker.Utilities.Integrations;

import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.LanguageManager;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class CoreProtectIntegration {

	private static final CoreProtectAPI api;

	static {
		api = getCoreProtect();
	}

	private static CoreProtectAPI getCoreProtect() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("CoreProtect");

		// Check that CoreProtect is loaded
		if (!(plugin instanceof CoreProtect)) {
			return null;
		}

		// Check that the API is enabled
		CoreProtectAPI CoreProtect = ((CoreProtect) plugin).getAPI();
		if (!CoreProtect.isEnabled()) {
			return null;
		}

		// Check that a compatible version of the API is loaded
		if (CoreProtect.APIVersion() < 6) {
			ChatWriter.logError(LanguageManager.getString("StartUp.CoreProtect.Failure"));
			return null;
		}

		ChatWriter.logInfo(LanguageManager.getString("StartUp.CoreProtect.Success"));
		return CoreProtect;
	}

	public static void init() {}

	/**
	 * Checks the block if it is valid to give Exp to the tool
	 * @param player The Player
	 * @param b	The Block to check
	 * @return true if valid, false if not
	 */
	public static boolean checkBlock(Player player, Block b) {
		if (api == null) return true; //CoreProtect is not loaded
		FileConfiguration config = Main.getPlugin().getConfig();
		int seconds = config.getInt("CoreProtect.BlockExpCooldownInSeconds", 60);
		if (seconds <= 0) return true;
		List<String[]> lookup = api.blockLookup(b, seconds);
		if (lookup == null || lookup.isEmpty()) return true;

		/*
		Actions:
		0 - removed
		1 - placed
		2 - interaction
		 */
		boolean playerOnly = config.getBoolean("CoreProtect.CheckOnlyForSpecificPlayer", true);
		for (String[] entry : lookup) {
			CoreProtectAPI.ParseResult result = api.parseResult(entry);
			if (playerOnly && !result.getPlayer().equals(player.getName())) continue;
			if (result.getActionId() == 1) return false;
		}

		return true;
	}
}

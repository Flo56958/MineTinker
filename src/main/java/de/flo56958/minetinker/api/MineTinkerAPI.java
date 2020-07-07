package de.flo56958.minetinker.api;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.modifiers.ModManager;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ConfigurationManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

/**
 * This class is the interface for customization of Modifiers
 * and other features of MineTinker.
 * The methods used in this API are mostly just wrappers around
 * other methods only with better documentation.
 * <p>
 * It is possible to mess with Modifiers outside this API with
 * MineTinker-Methods, but it is recommended to do everything
 * with this class.
 * <p>
 * This API is in a very unfinished state.
 *
 * @author Flo56958
 * @version 2020/07/05
 * @apiNote USE AT OWN RISK. MAY CHANGE WITH FUTURE UPDATES.
 * @see de.flo56958.minetinker.modifiers.Modifier
 * <p>
 * MineTinker-Events:
 * @see de.flo56958.minetinker.events.ModifierApplyEvent
 * @see de.flo56958.minetinker.events.ModifierFailEvent
 * @see de.flo56958.minetinker.events.MTBlockBreakEvent
 * @see de.flo56958.minetinker.events.MTEntityDamageByEntityEvent
 * @see de.flo56958.minetinker.events.MTEntityDamageEvent
 * @see de.flo56958.minetinker.events.MTEntityDeathEvent
 * @see de.flo56958.minetinker.events.MTPlayerInteractEvent
 * @see de.flo56958.minetinker.events.MTProjectileHitEvent
 * @see de.flo56958.minetinker.events.ToolLevelUpEvent
 * @see de.flo56958.minetinker.events.ToolUpgradeEvent
 */
public class MineTinkerAPI {

	private static final ModManager modManager = ModManager.instance();
	private static MineTinkerAPI api;

	private MineTinkerAPI() {
	}

	public static MineTinkerAPI instance() {
		if (api == null) {
			api = new MineTinkerAPI();
		}

		return api;
	}

	public void registerModifier(Modifier mod) {
		modManager.register(mod);
	}

	public void unregisterModifier(Modifier mod) {
		modManager.unregister(mod);
	}

	/**
	 * Gets the specified Configuration
	 *
	 * @param folder the folder in the MineTinker-Data directory; empty string if in main directory
	 * @param name   the name of the file with extension
	 * @return the configuration
	 */
	public FileConfiguration getConfig(String folder, String name) {
		ConfigurationManager.loadConfig(folder, name);

		return ConfigurationManager.getConfig(name);
	}

	/**
	 * Saves the specified Configuration
	 *
	 * @param config the config you got through getConfig()
	 */
	public void saveConfig(FileConfiguration config) {
		ConfigurationManager.saveConfig(config);
	}

	public Plugin getPlugin() {
		return MineTinker.getPlugin();
	}
}

package de.flo56958.minetinker;

import de.flo56958.minetinker.modifiers.ModManager;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utilities.ConfigurationManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

/**
 * This class is the interface for customization of modifiers
 * and other features of MineTinker.
 * The methods used in this API are mostly just wrappers around
 * other methods only with better documentation.
 *
 * It is possible to mess with modifiers outside this API with
 * MineTinker-Methods, but it is recommended to do everything
 * with this class.
 *
 * This API is in a very unfinished state.
 *
 * @see de.flo56958.minetinker.modifiers.Modifier
 *
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
 *
 * @author Flo56958
 * @version 2019/03/27
 * @apiNote USE ON OWN RISK. MAY CHANGE WITH FUTURE UPDATES.
 */
public class MineTinkerAPI {

    private static MineTinkerAPI api;
    private static final ModManager modManager = ModManager.instance();

    public static MineTinkerAPI instance() {
        if (api == null) {
            api = new MineTinkerAPI();
        }

        return api;
    }

    private MineTinkerAPI() {}

    public void registerModifier(Modifier mod) {
        modManager.register(mod);
    }

    public void unregisterModifier(Modifier mod) {
        modManager.unregister(mod);
    }

    /**
     * Gets the specified Configuration
     * @param folder the folder in the MineTinker-data directory; empty string if in main directory
     * @param name the name of the file with extension
     * @return the configuration
     */
    public FileConfiguration getConfig(String folder, String name) {
        ConfigurationManager.loadConfig(folder, name);

        return ConfigurationManager.getConfig(name);
    }

    /**
     * Saves the specified Configuration
     * @param config the config you got through getConfig()
     */
    public void saveConfig(FileConfiguration config) {
        ConfigurationManager.saveConfig(config);
    }

    public Plugin getMineTinker() {
        return Main.getPlugin();
    }
}

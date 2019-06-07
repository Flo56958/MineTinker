package de.flo56958.MineTinker;

import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import de.flo56958.MineTinker.Utilities.nms.NBTHandler;
import de.flo56958.MineTinker.Utilities.nms.NBTUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

/**
 * This class is the interface for customization of Modifiers
 * and other features of MineTinker.
 * The methods used in this API are mostly just wrappers around
 * other methods only with better documentation.
 *
 * It is possible to mess with Modifiers outside this API with
 * MineTinker-Methods, but it is recommended to do everything
 * with this class.
 *
 * This API is in a very unfinished state.
 *
 * @see de.flo56958.MineTinker.Modifiers.Modifier
 * @see de.flo56958.MineTinker.Utilities.nms.NBTUtils
 *
 * MineTinker-Events:
 * @see de.flo56958.MineTinker.Events.ModifierApplyEvent
 * @see de.flo56958.MineTinker.Events.ModifierFailEvent
 * @see de.flo56958.MineTinker.Events.MTBlockBreakEvent
 * @see de.flo56958.MineTinker.Events.MTEntityDamageByEntityEvent
 * @see de.flo56958.MineTinker.Events.MTEntityDamageEvent
 * @see de.flo56958.MineTinker.Events.MTEntityDeathEvent
 * @see de.flo56958.MineTinker.Events.MTPlayerInteractEvent
 * @see de.flo56958.MineTinker.Events.MTProjectileHitEvent
 * @see de.flo56958.MineTinker.Events.ToolLevelUpEvent
 * @see de.flo56958.MineTinker.Events.ToolUpgradeEvent
 *
 * @author Flo56958
 * @version 2019/06/08
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
     * @param folder the folder in the MineTinker-Data directory; empty string if in main directory
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

    public NBTHandler getNBTHandler() { return NBTUtils.getHandler(); }
}

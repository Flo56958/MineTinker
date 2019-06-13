package de.flo56958.MineTinker;

import de.flo56958.MineTinker.Commands.Commands;
import de.flo56958.MineTinker.Data.CraftingRecipes;
import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Listeners.*;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Types.Power;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import de.flo56958.MineTinker.Utilities.Updater;
import de.flo56958.MineTinker.Utilities.nms.NBTUtils;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.atomic.AtomicBoolean;

public class Main extends JavaPlugin {

    private static JavaPlugin plugin;

    @Override
    public void onEnable() {
        plugin = this;
        ChatWriter.log(false, "Setting up internals...");
        if (!NBTUtils.init()) {
            Bukkit.getPluginManager().disablePlugin(this); //Disable Plugin for safety
            return;
        }

        ConfigurationManager.reload();
        BuildersWandListener.init();
        loadConfig();

        ModManager.instance();

        Commands cmd = new Commands();
        this.getCommand("minetinker").setExecutor(cmd); // must be after internals as it would throw a NullPointerException
        this.getCommand("minetinker").setTabCompleter(cmd);

        ChatWriter.logInfo("Registered commands!");

        if (getConfig().getBoolean("AllowCrafting")) {
            ConvertListener convertListener = new ConvertListener();
            convertListener.register();
            Bukkit.getPluginManager().registerEvents(convertListener, this);
        }

        Bukkit.getPluginManager().registerEvents(new BlockListener(), this);
        Bukkit.getPluginManager().registerEvents(new AnvilListener(), this);
        Bukkit.getPluginManager().registerEvents(new ArmorListener(), this);
        Bukkit.getPluginManager().registerEvents(new ItemListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new EntityListener(), this);
        Bukkit.getPluginManager().registerEvents(new TinkerListener(), this);
        Bukkit.getPluginManager().registerEvents(new TridentListener(), this);

        CraftingRecipes.registerMTElytra();
        CraftingRecipes.registerMTTrident();

        if (!getConfig().getBoolean("AllowEnchanting")) Bukkit.getPluginManager().registerEvents(new EnchantingTableListener(), this);

        if (ConfigurationManager.getConfig("BuildersWand.yml").getBoolean("BuildersWand.enabled")) {
            Bukkit.getPluginManager().registerEvents(new BuildersWandListener(), this);
            BuildersWandListener.reload();
            ChatWriter.log(false, "Enabled BuildersWands!");
        }

        if (getConfig().getBoolean("EasyHarvest.enabled")) {
            Bukkit.getPluginManager().registerEvents(new EasyHarvestListener(), this);
            ChatWriter.log(false, "Enabled EasyHarvest!");
        }

        ChatWriter.log(false, "Registered events!");

        if (getConfig().getBoolean("logging.metrics")) new Metrics(this);

        ChatWriter.log(false, "Standard Logging is enabled. You can disable it in the config under Logging.Standard!");
        ChatWriter.log(true, "Debug Logging is enabled. You should disable it in the config under Logging.Debug!");

        for (Player current : Bukkit.getServer().getOnlinePlayers()) {
            Power.HASPOWER.computeIfAbsent(current, player -> new AtomicBoolean(false));
            Lists.BLOCKFACE.put(current, null);
        }

        if (getConfig().getBoolean("CheckForUpdates")) Bukkit.getScheduler().scheduleAsyncDelayedTask(this, Updater::checkForUpdate, 20);
    }

    public void onDisable() {
        ChatWriter.logInfo("Shutting down!");
    }

    /**
     * loads the main config of MineTinker
     */
    private void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();

        ChatWriter.log(false, "Config loaded!");
    }

    public static Plugin getPlugin() { // necessary to do getConfig() in other classes
        return plugin;
    }
}

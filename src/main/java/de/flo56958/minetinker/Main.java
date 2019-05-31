package de.flo56958.minetinker;

import de.flo56958.minetinker.commands.Commands;
import de.flo56958.minetinker.data.CraftingRecipes;
import de.flo56958.minetinker.data.Lists;
import de.flo56958.minetinker.listeners.*;
import de.flo56958.minetinker.modifiers.ModManager;
import de.flo56958.minetinker.modifiers.types.Power;
import de.flo56958.minetinker.utilities.ChatWriter;
import de.flo56958.minetinker.utilities.ConfigurationManager;
import de.flo56958.minetinker.utilities.Updater;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.atomic.AtomicBoolean;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        ChatWriter.log(false, "Setting up internals...");
        ConfigurationManager.reload();
        ElevatorListener.init();
        BuildersWandListener.init();
        loadConfig();

        ModManager.instance();

        Commands cmd = new Commands();
        PluginCommand mt = this.getCommand("minetinker");

        if (mt != null) {
            // must be after internals as it would throw a NullPointerException
            mt.setExecutor(cmd);
            mt.setTabCompleter(cmd);
        }

        ChatWriter.log(false, "Registered commands!");

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

        if (!getConfig().getBoolean("AllowEnchanting"))
            Bukkit.getPluginManager().registerEvents(new EnchantingTableListener(), this);

        if (ConfigurationManager.getConfig("Elevator.yml").getBoolean("Elevator.enabled")) {
            Bukkit.getPluginManager().registerEvents(new ElevatorListener(), this);
            CraftingRecipes.registerElevatorMotor();
            ChatWriter.log(false, "Enabled Elevators!");
        }

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

        if (getConfig().getBoolean("logging.metrics"))
            new Metrics(this);

        ChatWriter.log(false, "Standard Logging is enabled. You can disable it in the config under Logging.Standard!");
        ChatWriter.log(true, "Debug Logging is enabled. You should disable it in the config under Logging.Debug!");

        for (Player current : Bukkit.getServer().getOnlinePlayers()) {
            Power.HASPOWER.computeIfAbsent(current, player -> new AtomicBoolean(false));
            Lists.BLOCKFACE.put(current, null);
        }

        if (getConfig().getBoolean("CheckForUpdates")) {
            Bukkit.getScheduler().scheduleAsyncDelayedTask(this, Updater::checkForUpdate, 20);
        }
    }

    public void onDisable() {
        ChatWriter.log(false, "Shutting down!");
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
        return Bukkit.getPluginManager().getPlugin("MineTinker");
    }
}
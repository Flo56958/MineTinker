package de.flo56958.MineTinker;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import de.flo56958.MineTinker.Commands.Commands;
import de.flo56958.MineTinker.Data.CraftingRecipes;
import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Listeners.AnvilListener;
import de.flo56958.MineTinker.Listeners.ArmorListener;
import de.flo56958.MineTinker.Listeners.BlockListener;
import de.flo56958.MineTinker.Listeners.BuildersWandListener;
import de.flo56958.MineTinker.Listeners.ConvertListener;
import de.flo56958.MineTinker.Listeners.EasyHarvestListener;
import de.flo56958.MineTinker.Listeners.ElevatorListener;
import de.flo56958.MineTinker.Listeners.EnchantingTableListener;
import de.flo56958.MineTinker.Listeners.EntityListener;
import de.flo56958.MineTinker.Listeners.PlayerListener;
import de.flo56958.MineTinker.Listeners.TinkerListener;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Types.Power;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import de.flo56958.MineTinker.Utilities.Updater;
import de.flo56958.MineTinker.bStats.Metrics;

public class Main extends JavaPlugin {

    private static Updater updater;
    
    private static Main main;

    @Override
    public void onEnable() {
    	main = this;
        ConfigurationManager.reload();
        BuildersWandListener.init();
        ElevatorListener.init();
        loadConfig();

        ModManager.instance();
        
        ChatWriter.log(false, "Set up internals.");
        
        Commands cmd = new Commands();
        this.getCommand("minetinker").setExecutor(cmd); //must be after internals as it would throw a NullPointerException
        this.getCommand("minetinker").setTabCompleter(cmd);
        ChatWriter.log(false, "Registered commands!");

        if (getConfig().getBoolean("AllowCrafting")) {
        	ConvertListener convertListener = new ConvertListener();
        	convertListener.register();
            Bukkit.getPluginManager().registerEvents(convertListener, this);
        }
        Bukkit.getPluginManager().registerEvents(new BlockListener(), this);
        Bukkit.getPluginManager().registerEvents(new AnvilListener(), this);
        Bukkit.getPluginManager().registerEvents(new ArmorListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new EntityListener(), this);
        Bukkit.getPluginManager().registerEvents(new TinkerListener(), this);
        if (!getConfig().getBoolean("AllowEnchanting")) {
            Bukkit.getPluginManager().registerEvents(new EnchantingTableListener(), this);
        }
        if (ConfigurationManager.getConfig("Elevator.yml").getBoolean("Elevator.enabled")) {
            Bukkit.getPluginManager().registerEvents(new ElevatorListener(), this);
            CraftingRecipes.registerElevatorMotor();
            ChatWriter.log(false, "Enabled Elevators!");
        }
        if (ConfigurationManager.getConfig("BuildersWand.yml").getBoolean("BuildersWand.enabled")) {
            Bukkit.getPluginManager().registerEvents(new BuildersWandListener(),this);
            CraftingRecipes.registerBuildersWands();
            ChatWriter.log(false, "Enabled BuildersWands!");
        }
        if (getConfig().getBoolean("EasyHarvest.enabled")) {
            Bukkit.getPluginManager().registerEvents(new EasyHarvestListener(),this);
            ChatWriter.log(false, "Enabled EasyHarvest!");
        }
        ChatWriter.log(false, "Registered events!");

        if (getConfig().getBoolean("logging.metrics")) {
            new Metrics(this);
            Bukkit.getConsoleSender().sendMessage(ChatWriter.CHAT_PREFIX + " Started Metrics-service! Thank you for enabling it in the config! It helps to maintain the Plugin and fix bugs. (Data is send anonymously)");
        } else {
            Bukkit.getConsoleSender().sendMessage(ChatWriter.CHAT_PREFIX + " Metrics-service disabled! The service helps to maintain the Plugin and fix bugs. (Data is send anonymously)");
        }
        ChatWriter.log(false, "Standard logging is enabled! You can disable it in the config!");
        ChatWriter.log(true, "Debug logging is enabled! You should disable it in the config!");

        for (Player current : Bukkit.getServer().getOnlinePlayers()) {
            Power.HASPOWER.put(current, false);
            Lists.BLOCKFACE.put(current, null);
        }

        if (getConfig().getBoolean("CheckForUpdates")) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    updater = new Updater();
                    updater.checkForUpdate();
                }
            }.runTaskLater(Main.getPlugin(), 20);
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

    public static Plugin getPlugin() { //necessary to do getConfig() in other classes
        return Bukkit.getPluginManager().getPlugin("MineTinker");
    }

    public static Updater getUpdater() { return updater; }

	public static Main getMain() {
		return main;
	}
}

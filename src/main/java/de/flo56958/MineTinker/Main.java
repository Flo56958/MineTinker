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
import de.flo56958.MineTinker.bStats.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Main extends JavaPlugin {

    private static ConfigurationManager configurations;
    private static Main tinkerMain;

    private static Updater updater;

    @Override
    public void onEnable() {
        tinkerMain = this;

        configurations = new ConfigurationManager(this);
        loadConfig();

        ModManager modManager = ModManager.instance();
        ChatWriter.log(false, "Set up internals.");

        Commands cmd = new Commands();
        this.getCommand("minetinker").setExecutor(cmd); //must be after internals as it would throw a NullPointerException
        this.getCommand("minetinker").setTabCompleter(cmd);
        ChatWriter.log(false, "Registered commands!");

        if (getConfig().getBoolean("AllowCrafting")) {
            Bukkit.getPluginManager().registerEvents(new CraftingListener(), this);
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
        if (configurations.getConfig("Elevator.yml").getBoolean("Elevator.enabled")) {
            Bukkit.getPluginManager().registerEvents(new ElevatorListener(), this);
            CraftingRecipes.registerElevatorMotor();
            ChatWriter.log(false, "Enabled Elevators!");
        }
        if (configurations.getConfig("BuildersWand.yml").getBoolean("BuildersWand.enabled")) {
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
            Metrics metrics = new Metrics(this);
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

    public static Main getMain() { return tinkerMain; }

    public static Updater getUpdater() { return updater; }

    /**
     * @return The ConfigurationManager of MineTinker
     */
    public ConfigurationManager getConfigurations() { return configurations; }
}

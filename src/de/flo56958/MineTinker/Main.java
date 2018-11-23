package de.flo56958.MineTinker;

import de.flo56958.MineTinker.Commands.Commands;
import de.flo56958.MineTinker.Data.CraftingRecipes;
import de.flo56958.MineTinker.Data.PlayerData;
import de.flo56958.MineTinker.Listeners.*;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Types.Power;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.bStats.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static ModManager modManager;

    @Override
    public void onEnable() {
        this.getCommand("minetinker").setExecutor(new Commands());
        ChatWriter.log(false, "Registered commands!");

        loadConfig();

        modManager = new ModManager();
        modManager.init();

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
        if (getConfig().getBoolean("Elevator.enabled")) {
            Bukkit.getPluginManager().registerEvents(new ElevatorListener(), this);
            CraftingRecipes.registerElevatorMotor();
        }
        if (getConfig().getBoolean("Builderswands.enabled")) {
            Bukkit.getPluginManager().registerEvents(new BuildersWandListener(),this);
            CraftingRecipes.registerBuildersWands();
        }
        if (getConfig().getBoolean("EasyHarvest.enabled")) {
            Bukkit.getPluginManager().registerEvents(new EasyHarvestListener(),this);
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
            PlayerData.BLOCKFACE.put(current, null);
        }
    }

    public void onDisable() {
        ChatWriter.log(false, "Shutting down!");
    }

    private void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();
        ChatWriter.log(false, "Config loaded!");
    }

    public static Plugin getPlugin() { //necessary to do getConfig() in other classes
        return Bukkit.getPluginManager().getPlugin("MineTinker");
    }

    public static ModManager getModManager() { return modManager; }
}

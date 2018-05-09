package de.flo56958.MineTinker;

import de.flo56958.MineTinker.Commands.cmd_Main;
import de.flo56958.MineTinker.Listeners.BlockBreakListener;
import de.flo56958.MineTinker.Listeners.CraftingGrid9Listener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        this.getCommand("minetinker").setExecutor(new cmd_Main());

        Bukkit.getPluginManager().registerEvents(new CraftingGrid9Listener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockBreakListener(), this);

        loadConfig();
    }

    private void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    public static Plugin getPlugin() { //necessary to do getConfig() in other classes
        return Bukkit.getPluginManager().getPlugin("MineTinker");
    }
}

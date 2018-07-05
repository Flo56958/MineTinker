package de.flo56958.MineTinker;

import de.flo56958.MineTinker.Commands.cmd_Main;
import de.flo56958.MineTinker.Data.CraftingRecipes;
import de.flo56958.MineTinker.Data.Strings;
import de.flo56958.MineTinker.Listeners.AnvilListener;
import de.flo56958.MineTinker.Listeners.BlockListener;
import de.flo56958.MineTinker.Listeners.CraftingGrid9Listener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        this.getCommand("minetinker").setExecutor(new cmd_Main());
        Bukkit.getLogger().log(Level.INFO, Strings.CHAT_PREFIX + " Registered commands!");

        Bukkit.getPluginManager().registerEvents(new CraftingGrid9Listener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockListener(), this);
        Bukkit.getPluginManager().registerEvents(new AnvilListener(), this);
        Bukkit.getLogger().log(Level.INFO, Strings.CHAT_PREFIX + " Registered events!");

        loadConfig();

        if (getConfig().getBoolean("Modifiers.Reinforced.allowed")) {
            CraftingRecipes.registerReinforcedModifier();
        }
        Bukkit.getLogger().log(Level.INFO, Strings.CHAT_PREFIX + " Registered crafting recipes!");
    }

    private void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();
        Bukkit.getLogger().log(Level.INFO, Strings.CHAT_PREFIX + " Config loaded!");
    }

    public static Plugin getPlugin() { //necessary to do getConfig() in other classes
        return Bukkit.getPluginManager().getPlugin("MineTinker");
    }
}

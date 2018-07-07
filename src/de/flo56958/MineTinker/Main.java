package de.flo56958.MineTinker;

import de.flo56958.MineTinker.Commands.cmd_Main;
import de.flo56958.MineTinker.Data.CraftingRecipes;
import de.flo56958.MineTinker.Listeners.AnvilListener;
import de.flo56958.MineTinker.Listeners.BlockListener;
import de.flo56958.MineTinker.Listeners.CraftingGrid9Listener;
import de.flo56958.MineTinker.Listeners.EnchantingTableListener;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        this.getCommand("minetinker").setExecutor(new cmd_Main());
        ChatWriter.log(false, "Registered commands!");

        Bukkit.getPluginManager().registerEvents(new CraftingGrid9Listener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockListener(), this);
        Bukkit.getPluginManager().registerEvents(new AnvilListener(), this);
        Bukkit.getPluginManager().registerEvents(new EnchantingTableListener(), this);
        ChatWriter.log(false, "Registered events!");

        loadConfig();

        if (getConfig().getBoolean("Modifiers.Reinforced.allowed")) {
            CraftingRecipes.registerReinforcedModifier();
        }
        if (getConfig().getBoolean("Modifiers.Haste.allowed")) {
            CraftingRecipes.registerHasteModifier();
        }
        if (getConfig().getBoolean("Modifiers.Sharpness.allowed")) {
            CraftingRecipes.registerSharpnessModifier();
        }
        ChatWriter.log(false, "Registered crafting recipes!");
        ChatWriter.log(false, "Standard logging is enabled! You can disable it in the config!");
        ChatWriter.log(true, "Debug logging is enabled! You should disable it in the config!");
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
}

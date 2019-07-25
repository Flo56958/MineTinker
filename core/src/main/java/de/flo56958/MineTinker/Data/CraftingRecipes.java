package de.flo56958.MineTinker.Data;

import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class CraftingRecipes {

    private static final ModManager modManager = ModManager.instance();

    static {
        FileConfiguration elytraConf = ConfigurationManager.getConfig("Elytra.yml");
        elytraConf.options().copyDefaults(true);

        String key = "Elytra";
        elytraConf.addDefault(key + ".craftable", true);
        elytraConf.addDefault(key + ".ExpChanceWhileFlying", 10);

        String recipe = key + ".Recipe";
        elytraConf.addDefault(recipe + ".Top", "M M");
        elytraConf.addDefault(recipe + ".Middle", " E ");
        elytraConf.addDefault(recipe + ".Bottom", "M M");
        elytraConf.addDefault(recipe + ".Materials.E", "ELYTRA");
        elytraConf.addDefault(recipe + ".Materials.M", "PHANTOM_MEMBRANE");

        ConfigurationManager.saveConfig(elytraConf);

        FileConfiguration tridentConf = ConfigurationManager.getConfig("Trident.yml");
        tridentConf.options().copyDefaults(true);

        key = "Trident";
        tridentConf.addDefault(key + ".craftable", true);

        recipe = key + ".Recipe";
        tridentConf.addDefault(recipe + ".Top", "P P");
        tridentConf.addDefault(recipe + ".Middle", " T ");
        tridentConf.addDefault(recipe + ".Bottom", "P P");
        tridentConf.addDefault(recipe + ".Materials.T", "TRIDENT");
        tridentConf.addDefault(recipe + ".Materials.P", "PRISMARINE_SHARD");
        ConfigurationManager.saveConfig(tridentConf);
    }

    /**
     * tries to register the recipe for the MineTinker-Elytra
     */
    public static void registerMTElytra() {
        FileConfiguration config = ConfigurationManager.getConfig("Elytra.yml");
        String ckey = "Elytra";

        if (!config.getBoolean(ckey + ".craftable")) return;

        try {
            NamespacedKey nkey = new NamespacedKey(Main.getPlugin(), "MineTinker_Elytra");
            ItemStack elytra = new ItemStack(Material.ELYTRA, 1);
            modManager.convertItemStack(elytra);
            ShapedRecipe newRecipe = new ShapedRecipe(nkey, elytra); //init recipe
            String top = config.getString(ckey + ".Recipe.Top");
            String middle = config.getString(ckey + ".Recipe.Middle");
            String bottom = config.getString(ckey + ".Recipe.Bottom");
            ConfigurationSection materials = config.getConfigurationSection(ckey + ".Recipe.Materials");

            // TODO: Make safe
            newRecipe.shape(top, middle, bottom); //makes recipe

            for (String key : materials.getKeys(false)) {
                newRecipe.setIngredient(key.charAt(0), Material.getMaterial(materials.getString(key)));
            }

            Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
            ModManager.instance().recipe_Namespaces.add(nkey);
        } catch (Exception e) {
            ChatWriter.logError("Could not register recipe for the MineTinker-Elytra!"); //executes if the recipe could not initialize
            e.printStackTrace();
        }
    }

    //TODO: BUG-REPORT: MT-TRIDENTS CAN BE RECONVERTED TO MT-TRIDENTS
    //TODO: TWEAK: MT-TRIDENTS LOSE ALL ENCHANTMENTS WHEN CRAFTED
    //TODO: -> Make Crafting-Output alter from the input
    /**
     * tries to register the recipe for the MineTinker-Elytra
     */
    public static void registerMTTrident() {
        FileConfiguration config = ConfigurationManager.getConfig("Trident.yml");
        String ckey = "Trident";

        if (!config.getBoolean(ckey + ".craftable")) {
            return;
        }

        try {
            NamespacedKey nkey = new NamespacedKey(Main.getPlugin(), "MineTinker_Trident");
            ItemStack trident = new ItemStack(Material.TRIDENT, 1);
            modManager.convertItemStack(trident);
            ShapedRecipe newRecipe = new ShapedRecipe(nkey, trident); //init recipe
            String top = config.getString(ckey + ".Recipe.Top");
            String middle = config.getString(ckey + ".Recipe.Middle");
            String bottom = config.getString(ckey + ".Recipe.Bottom");
            ConfigurationSection materials = config.getConfigurationSection(ckey + ".Recipe.Materials");

            // TODO: Make safe
            newRecipe.shape(top, middle, bottom); //makes recipe

            for (String key : materials.getKeys(false)) {
                newRecipe.setIngredient(key.charAt(0), Material.getMaterial(materials.getString(key)));
            }

            Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
            ModManager.instance().recipe_Namespaces.add(nkey);
        } catch (Exception e) {
            ChatWriter.logError("Could not register recipe for the MineTinker-Trident!"); //executes if the recipe could not initialize
            e.printStackTrace();
        }
    }

}

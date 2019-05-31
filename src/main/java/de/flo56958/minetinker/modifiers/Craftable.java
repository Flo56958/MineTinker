package de.flo56958.minetinker.modifiers;

import de.flo56958.minetinker.Main;
import de.flo56958.minetinker.utilities.ChatWriter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ShapedRecipe;

//TODO: REMOVE INTERFACE AS ALL MODIFIERS HAVE IT
public interface Craftable {
    void registerCraftingRecipe();

    default void _registerCraftingRecipe(FileConfiguration config, Modifier mod, String name, String keyName) {
        if (config.getBoolean(name + ".Recipe.Enabled")) {
            try {
                NamespacedKey nkey = new NamespacedKey(Main.getPlugin(), keyName);
                ShapedRecipe newRecipe = new ShapedRecipe(nkey, mod.getModItem()); //init recipe
                String top = config.getString(name + ".Recipe.Top");
                String middle = config.getString(name + ".Recipe.Middle");
                String bottom = config.getString(name + ".Recipe.Bottom");
                ConfigurationSection materials = config.getConfigurationSection(name + ".Recipe.Materials");

                if (top == null || middle == null || bottom == null) return;

                newRecipe.shape(top, middle, bottom); //makes recipe

                if (materials == null) return;

                for (String key : materials.getKeys(false)) {
                    String matName = materials.getString(key);

                    if (matName != null) {
                        Material material = Material.getMaterial(matName);
                        if (material != null) newRecipe.setIngredient(key.charAt(0), material);
                    }
                }

                Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
                ChatWriter.log(false, "Registered recipe for the " + name + "-Modifier!");
                ModManager.instance().recipe_Namespaces.add(nkey);
            } catch (Exception e) {
                ChatWriter.logError("Could not register recipe for the " + name + "-Modifier!"); //executes if the recipe could not initialize
                e.printStackTrace();
            }
        }
    }
}

package de.flo56958.MineTinker.Data;

import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ShapedRecipe;

public class CraftingRecipes {

    /**
     * tries to register the recipe for the elevator motor
     */
    public static void registerElevatorMotor() {
        FileConfiguration config = ConfigurationManager.getConfig("Elevator.yml");
        try {
            NamespacedKey nkey = new NamespacedKey(Main.getPlugin(), "Elevator_Motor");
            ShapedRecipe newRecipe = new ShapedRecipe(nkey, ItemGenerator.itemEnchanter(Material.HOPPER, ChatColor.GRAY + config.getString("Elevator.name"), 1, Enchantment.FIRE_ASPECT, 1)); //init recipe
            String top = config.getString("Elevator.Recipe.Top");
            String middle = config.getString("Elevator.Recipe.Middle");
            String bottom = config.getString("Elevator.Recipe.Bottom");
            ConfigurationSection materials = config.getConfigurationSection("Elevator.Recipe.Materials");
            newRecipe.shape(top, middle, bottom); //makes recipe
            for (String key : materials.getKeys(false)) {
                newRecipe.setIngredient(key.charAt(0), Material.getMaterial(materials.getString(key)));
            }
            Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
            ModManager.instance().recipe_Namespaces.add(nkey);
        } catch (Exception e) {
            ChatWriter.logError("Could not register recipe for the " + config.getString("Elevator.name") + "!"); //executes if the recipe could not initialize
        }
    }

}

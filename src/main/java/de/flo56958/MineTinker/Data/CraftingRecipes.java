package de.flo56958.MineTinker.Data;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ShapedRecipe;

import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ItemGenerator;

public class CraftingRecipes {

    /**
     * tries to register the recipe for the elevator motor
     */
    public static void registerElevatorMotor() {
        FileConfiguration config = Main.getMain().getConfigurations().getConfig("Elevator.yml");
        try {
            ShapedRecipe newRecipe = new ShapedRecipe(new NamespacedKey(Main.getPlugin(), "Elevator_Motor"), ItemGenerator.itemEnchanter(Material.HOPPER, ChatColor.GRAY + config.getString("Elevator.name"), 1, Enchantment.FIRE_ASPECT, 1)); //init recipe
            String top = config.getString("Elevator.Recipe.Top");
            String middle = config.getString("Elevator.Recipe.Middle");
            String bottom = config.getString("Elevator.Recipe.Bottom");
            ConfigurationSection materials = config.getConfigurationSection("Elevator.Recipe.Materials");
            newRecipe.shape(top, middle, bottom); //makes recipe
            for (String key : materials.getKeys(false)) {
                newRecipe.setIngredient(key.charAt(0), Material.getMaterial(materials.getString(key)));
            }
            Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
        } catch (Exception e) {
            ChatWriter.logError("Could not register recipe for the " + config.getString("Elevator.name") + "!"); //executes if the recipe could not initialize
        }
    }

    /**
     * tries to register the Builderswand recipes
     */
    public static void registerBuildersWands() {
        FileConfiguration config = Main.getMain().getConfigurations().getConfig("BuildersWand.yml");
        try {
            ShapedRecipe newRecipe = new ShapedRecipe(new NamespacedKey(Main.getPlugin(), "Builderswand_Wood"), ItemGenerator.buildersWandCreator(Material.WOODEN_SHOVEL, config.getString("BuildersWand.name_wood"), 1)); //init recipe
            String top = config.getString("BuildersWand.Recipes.Wood.Top");
            String middle = config.getString("BuildersWand.Recipes.Wood.Middle");
            String bottom = config.getString("BuildersWand.Recipes.Wood.Bottom");
            ConfigurationSection materials = config.getConfigurationSection("BuildersWand.Recipes.Wood.Materials");
            newRecipe.shape(top, middle, bottom); //makes recipe
            for (String key : materials.getKeys(false)) {
                newRecipe.setIngredient(key.charAt(0), Material.getMaterial(materials.getString(key)));
            }
            Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
        } catch (Exception e) {
            ChatWriter.logError("Could not register recipe for the Wooden Builderswand!"); //executes if the recipe could not initialize
        }
        try {
            ShapedRecipe newRecipe = new ShapedRecipe(new NamespacedKey(Main.getPlugin(), "Builderswand_Stone"), ItemGenerator.buildersWandCreator(Material.STONE_SHOVEL, config.getString("BuildersWand.name_stone"), 1)); //init recipe
            String top = config.getString("BuildersWand.Recipes.Stone.Top");
            String middle = config.getString("BuildersWand.Recipes.Stone.Middle");
            String bottom = config.getString("BuildersWand.Recipes.Stone.Bottom");
            ConfigurationSection materials = config.getConfigurationSection("BuildersWand.Recipes.Stone.Materials");
            newRecipe.shape(top, middle, bottom); //makes recipe
            for (String key : materials.getKeys(false)) {
                newRecipe.setIngredient(key.charAt(0), Material.getMaterial(materials.getString(key)));
            }
            Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
        } catch (Exception e) {
            ChatWriter.logError("Could not register recipe for the Stone Builderswand!"); //executes if the recipe could not initialize
        }
        try {
            ShapedRecipe newRecipe = new ShapedRecipe(new NamespacedKey(Main.getPlugin(), "Builderswand_Iron"), ItemGenerator.buildersWandCreator(Material.IRON_SHOVEL, config.getString("BuildersWand.name_iron"), 1)); //init recipe
            String top = config.getString("BuildersWand.Recipes.Iron.Top");
            String middle = config.getString("BuildersWand.Recipes.Iron.Middle");
            String bottom = config.getString("BuildersWand.Recipes.Iron.Bottom");
            ConfigurationSection materials = config.getConfigurationSection("BuildersWand.Recipes.Iron.Materials");
            newRecipe.shape(top, middle, bottom); //makes recipe
            for (String key : materials.getKeys(false)) {
                newRecipe.setIngredient(key.charAt(0), Material.getMaterial(materials.getString(key)));
            }
            Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
        } catch (Exception e) {
            ChatWriter.logError("Could not register recipe for the Iron Builderswand!"); //executes if the recipe could not initialize
        }
        try {
            ShapedRecipe newRecipe = new ShapedRecipe(new NamespacedKey(Main.getPlugin(), "Builderswand_Gold"), ItemGenerator.buildersWandCreator(Material.GOLDEN_SHOVEL, config.getString("BuildersWand.name_gold"), 1)); //init recipe
            String top = config.getString("BuildersWand.Recipes.Gold.Top");
            String middle = config.getString("BuildersWand.Recipes.Gold.Middle");
            String bottom = config.getString("BuildersWand.Recipes.Gold.Bottom");
            ConfigurationSection materials = config.getConfigurationSection("BuildersWand.Recipes.Gold.Materials");
            newRecipe.shape(top, middle, bottom); //makes recipe
            for (String key : materials.getKeys(false)) {
                newRecipe.setIngredient(key.charAt(0), Material.getMaterial(materials.getString(key)));
            }
            Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
        } catch (Exception e) {
            ChatWriter.logError("Could not register recipe for the Golden Builderswand!"); //executes if the recipe could not initialize
        }
        try {
            ShapedRecipe newRecipe = new ShapedRecipe(new NamespacedKey(Main.getPlugin(), "Builderswand_Diamond"), ItemGenerator.buildersWandCreator(Material.DIAMOND_SHOVEL, config.getString("BuildersWand.name_diamond"), 1)); //init recipe
            String top = config.getString("BuildersWand.Recipes.Diamond.Top");
            String middle = config.getString("BuildersWand.Recipes.Diamond.Middle");
            String bottom = config.getString("BuildersWand.Recipes.Diamond.Bottom");
            ConfigurationSection materials = config.getConfigurationSection("BuildersWand.Recipes.Diamond.Materials");
            newRecipe.shape(top, middle, bottom); //makes recipe
            for (String key : materials.getKeys(false)) {
                newRecipe.setIngredient(key.charAt(0), Material.getMaterial(materials.getString(key)));
            }
            Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
        } catch (Exception e) {
            ChatWriter.logError("Could not register recipe for the Diamond Builderswand!"); //executes if the recipe could not initialize
        }
    }
}

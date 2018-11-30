package de.flo56958.MineTinker.Data;

import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ShapedRecipe;

public class CraftingRecipes {

    private static final ModManager modManager = Main.getModManager();

    public static void registerElevatorMotor() {
        try {
            ShapedRecipe newRecipe = new ShapedRecipe(new NamespacedKey(Main.getPlugin(), "Elevator_Motor"), ItemGenerator.itemEnchanter(Material.HOPPER, ChatColor.GRAY + Main.getPlugin().getConfig().getString("Elevator.name"), 1, Enchantment.FIRE_ASPECT, 1)); //init recipe
            newRecipe.shape("RRR", "RHR", "RRR"); //makes recipe
            newRecipe.setIngredient('R', Material.REDSTONE); //set ingredients
            newRecipe.setIngredient('H', Material.HOPPER);
            Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
        } catch (Exception e) {
            ChatWriter.log(true, "Could not register recipe for the Elevator-Motor!"); //executes if the recipe could not initialize
        }
    }

    public static void registerBuildersWands() {
        try {
            ShapedRecipe newRecipe = new ShapedRecipe(new NamespacedKey(Main.getPlugin(), "Builderswand_Wood"), ItemGenerator.buildersWandCreator(Material.WOODEN_SHOVEL, Main.getPlugin().getConfig().getString("Builderswands.name_wood"), 1)); //init recipe
            newRecipe.shape("  W", " S ", "S  "); //makes recipe
            newRecipe.setIngredient('S', Material.STICK); //set ingredients
            newRecipe.setIngredient('W', Material.LEGACY_WOOD);
            Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
        } catch (Exception e) {
            ChatWriter.log(true, "Could not register recipe for the Wooden Builderswand!"); //executes if the recipe could not initialize
        }
        try {
            ShapedRecipe newRecipe = new ShapedRecipe(new NamespacedKey(Main.getPlugin(), "Builderswand_Stone"), ItemGenerator.buildersWandCreator(Material.STONE_SHOVEL, Main.getPlugin().getConfig().getString("Builderswands.name_stone"), 1)); //init recipe
            newRecipe.shape("  C", " S ", "S  "); //makes recipe
            newRecipe.setIngredient('S', Material.STICK); //set ingredients
            newRecipe.setIngredient('C', Material.COBBLESTONE);
            Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
        } catch (Exception e) {
            ChatWriter.log(true, "Could not register recipe for the Stone Builderswand!"); //executes if the recipe could not initialize
        }
        try {
            ShapedRecipe newRecipe = new ShapedRecipe(new NamespacedKey(Main.getPlugin(), "Builderswand_Iron"), ItemGenerator.buildersWandCreator(Material.IRON_SHOVEL, Main.getPlugin().getConfig().getString("Builderswands.name_iron"), 1)); //init recipe
            newRecipe.shape("  I", " S ", "S  "); //makes recipe
            newRecipe.setIngredient('S', Material.STICK); //set ingredients
            newRecipe.setIngredient('I', Material.IRON_INGOT);
            Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
        } catch (Exception e) {
            ChatWriter.log(true, "Could not register recipe for the Iron Builderswand!"); //executes if the recipe could not initialize
        }
        try {
            ShapedRecipe newRecipe = new ShapedRecipe(new NamespacedKey(Main.getPlugin(), "Builderswand_Gold"), ItemGenerator.buildersWandCreator(Material.GOLDEN_SHOVEL, Main.getPlugin().getConfig().getString("Builderswands.name_gold"), 1)); //init recipe
            newRecipe.shape("  G", " S ", "S  "); //makes recipe
            newRecipe.setIngredient('S', Material.STICK); //set ingredients
            newRecipe.setIngredient('G', Material.GOLD_INGOT);
            Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
        } catch (Exception e) {
            ChatWriter.log(true, "Could not register recipe for the Golden Builderswand!"); //executes if the recipe could not initialize
        }
        try {
            ShapedRecipe newRecipe = new ShapedRecipe(new NamespacedKey(Main.getPlugin(), "Builderswand_Diamond"), ItemGenerator.buildersWandCreator(Material.DIAMOND_SHOVEL, Main.getPlugin().getConfig().getString("Builderswands.name_diamond"), 1)); //init recipe
            newRecipe.shape("  D", " S ", "S  "); //makes recipe
            newRecipe.setIngredient('S', Material.STICK); //set ingredients
            newRecipe.setIngredient('D', Material.DIAMOND);
            Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
        } catch (Exception e) {
            ChatWriter.log(true, "Could not register recipe for the Diamond Builderswand!"); //executes if the recipe could not initialize
        }
    }
}

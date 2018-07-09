package de.flo56958.MineTinker.Data;

import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import org.bukkit.Material;
import org.bukkit.inventory.ShapedRecipe;

public class CraftingRecipes {

    public static void registerReinforcedModifier() {
        try {
            ShapedRecipe newRecipe = new ShapedRecipe(Modifiers.REINFORCED_MODIFIER); //init recipe
            newRecipe.shape("OOO", "OOO", "OOO"); //makes recipe
            newRecipe.setIngredient('O', Material.OBSIDIAN); //set ingredients
            Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
        } catch (Exception e) {
            ChatWriter.log(true, "Could not register recipe for the Reinforced-Modifier!"); //executes if the recipe could not initialize
        }
    }

    public static void registerHasteModifier() {
        try {
            ShapedRecipe newRecipe = new ShapedRecipe(Modifiers.HASTE_MODIFIER); //init recipe
            newRecipe.shape("RRR", "RRR", "RRR"); //makes recipe
            newRecipe.setIngredient('R', Material.REDSTONE_BLOCK); //set ingredients
            Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
        } catch (Exception e) {
            ChatWriter.log(true, "Could not register recipe for the Haste-Modifier!"); //executes if the recipe could not initialize
        }
    }

    public static void registerSharpnessModifier() {
        try {
            ShapedRecipe newRecipe = new ShapedRecipe(Modifiers.SHARPNESS_MODIFIER); //init recipe
            newRecipe.shape("QQQ", "QQQ", "QQQ"); //makes recipe
            newRecipe.setIngredient('Q', Material.QUARTZ_BLOCK); //set ingredients
            Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
        } catch (Exception e) {
            ChatWriter.log(true, "Could not register recipe for the Sharpness-Modifier!"); //executes if the recipe could not initialize
        }
    }

    public static void registerLuckModifier() {
        try {
            ShapedRecipe newRecipe = new ShapedRecipe(Modifiers.LUCK_MODIFIER); //init recipe
            newRecipe.shape("LLL", "LLL", "LLL"); //makes recipe
            newRecipe.setIngredient('L', Material.LAPIS_BLOCK); //set ingredients
            Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
        } catch (Exception e) {
            ChatWriter.log(true, "Could not register recipe for the Luck-Modifier!"); //executes if the recipe could not initialize
        }
    }

    public static void registerAutoSmeltModifier() {
        try {
            ShapedRecipe newRecipe = new ShapedRecipe(Modifiers.AUTOSMELT_MODIFIER); //init recipe
            newRecipe.shape("CCC", "CFC", "CCC"); //makes recipe
            newRecipe.setIngredient('C', Material.FURNACE); //set ingredients
            newRecipe.setIngredient('F', Material.BLAZE_ROD);
            Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
        } catch (Exception e) {
            ChatWriter.log(true, "Could not register recipe for the Auto-Smelt-Modifier!"); //executes if the recipe could not initialize
        }
    }
}

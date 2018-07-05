package de.flo56958.MineTinker.Data;

import de.flo56958.MineTinker.Main;
import org.bukkit.Material;
import org.bukkit.inventory.ShapedRecipe;

import java.util.logging.Level;

public class CraftingRecipes {

    public static void registerReinforcedModifier() {
        try {
            ShapedRecipe newRecipe = new ShapedRecipe(Modifiers.REINFORCED_MODIFIER); //init recipe
            newRecipe.shape("OOO", "OOO", "OOO"); //makes recipe
            newRecipe.setIngredient('O', Material.OBSIDIAN); //set ingredients
            Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
        } catch (Exception e) {
            Main.getPlugin().getLogger().log(Level.WARNING, "Could not register recipe for the Reinforced-Modifier!"); //executes if the recipe could not initialize
        }
    }
}

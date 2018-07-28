package de.flo56958.MineTinker.Data;

import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
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

    public static void registerElevatorMotor() {
        try {
            ShapedRecipe newRecipe = new ShapedRecipe(ItemGenerator.itemEnchanter(Material.HOPPER, ChatColor.GRAY + "Elevator-Motor", 1, Enchantment.FIRE_ASPECT, 1)); //init recipe
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
            ShapedRecipe newRecipe1 = new ShapedRecipe(ItemGenerator.buildersWandCreator(Material.WOODEN_SHOVEL, "Wooden Builderswand", 1)); //init recipe
            newRecipe1.shape("  W", " S ", "S  "); //makes recipe
            newRecipe1.setIngredient('S', Material.STICK); //set ingredients
            newRecipe1.setIngredient('W', Material.LEGACY_WOOD);
            Main.getPlugin().getServer().addRecipe(newRecipe1); //adds recipe
        } catch (Exception e) {
            ChatWriter.log(true, "Could not register recipe for the Wooden Builderswand!"); //executes if the recipe could not initialize
        }
        try {
            ShapedRecipe newRecipe2 = new ShapedRecipe(ItemGenerator.buildersWandCreator(Material.STONE_SHOVEL, "Stone Builderswand", 1)); //init recipe
            newRecipe2.shape("  C", " S ", "S  "); //makes recipe
            newRecipe2.setIngredient('S', Material.STICK); //set ingredients
            newRecipe2.setIngredient('C', Material.COBBLESTONE);
            Main.getPlugin().getServer().addRecipe(newRecipe2); //adds recipe
        } catch (Exception e) {
            ChatWriter.log(true, "Could not register recipe for the Stone Builderswand!"); //executes if the recipe could not initialize
        }
        try {
            ShapedRecipe newRecipe3 = new ShapedRecipe(ItemGenerator.buildersWandCreator(Material.IRON_SHOVEL, "Iron Builderswand", 1)); //init recipe
            newRecipe3.shape("  I", " S ", "S  "); //makes recipe
            newRecipe3.setIngredient('S', Material.STICK); //set ingredients
            newRecipe3.setIngredient('I', Material.IRON_INGOT);
            Main.getPlugin().getServer().addRecipe(newRecipe3); //adds recipe
        } catch (Exception e) {
            ChatWriter.log(true, "Could not register recipe for the Iron Builderswand!"); //executes if the recipe could not initialize
        }
        try {
            ShapedRecipe newRecipe4 = new ShapedRecipe(ItemGenerator.buildersWandCreator(Material.GOLDEN_SHOVEL, "Golden Builderswand", 1)); //init recipe
            newRecipe4.shape("  G", " S ", "S  "); //makes recipe
            newRecipe4.setIngredient('S', Material.STICK); //set ingredients
            newRecipe4.setIngredient('G', Material.GOLD_INGOT);
            Main.getPlugin().getServer().addRecipe(newRecipe4); //adds recipe
        } catch (Exception e) {
            ChatWriter.log(true, "Could not register recipe for the Golden Builderswand!"); //executes if the recipe could not initialize
        }
        try {
            ShapedRecipe newRecipe5 = new ShapedRecipe(ItemGenerator.buildersWandCreator(Material.DIAMOND_SHOVEL, "Diamond Builderswand", 1)); //init recipe
            newRecipe5.shape("  D", " S ", "S  "); //makes recipe
            newRecipe5.setIngredient('S', Material.STICK); //set ingredients
            newRecipe5.setIngredient('D', Material.DIAMOND);
            Main.getPlugin().getServer().addRecipe(newRecipe5); //adds recipe
        } catch (Exception e) {
            ChatWriter.log(true, "Could not register recipe for the Diamond Builderswand!"); //executes if the recipe could not initialize
        }
    }
}

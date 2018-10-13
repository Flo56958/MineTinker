package com.minetinker.data;

import com.minetinker.Main;
import com.minetinker.utilities.ChatWriter;
import com.minetinker.utilities.ItemGenerator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ShapedRecipe;

public class CraftingRecipes {

    public static void registerReinforcedModifier() {
        try {
            ShapedRecipe newRecipe = new ShapedRecipe(new NamespacedKey(Main.getPlugin(), "Modifier_Reinforced"), Modifiers.REINFORCED_MODIFIER); //init recipe
            newRecipe.shape("OOO", "OOO", "OOO"); //makes recipe
            newRecipe.setIngredient('O', Material.OBSIDIAN); //set ingredients
            Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
        } catch (Exception e) {
            ChatWriter.log(true, "Could not register recipe for the Reinforced-Modifier!"); //executes if the recipe could not initialize
        }
    }

    public static void registerHasteModifier() {
        try {
            ShapedRecipe newRecipe = new ShapedRecipe(new NamespacedKey(Main.getPlugin(), "Modifier_Haste"), Modifiers.HASTE_MODIFIER); //init recipe
            newRecipe.shape("RRR", "RRR", "RRR"); //makes recipe
            newRecipe.setIngredient('R', Material.REDSTONE_BLOCK); //set ingredients
            Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
        } catch (Exception e) {
            ChatWriter.log(true, "Could not register recipe for the Haste-Modifier!"); //executes if the recipe could not initialize
        }
    }

    public static void registerSharpnessModifier() {
        try {
            ShapedRecipe newRecipe = new ShapedRecipe(new NamespacedKey(Main.getPlugin(), "Modifier_Sharpness"), Modifiers.SHARPNESS_MODIFIER); //init recipe
            newRecipe.shape("QQQ", "QQQ", "QQQ"); //makes recipe
            newRecipe.setIngredient('Q', Material.QUARTZ_BLOCK); //set ingredients
            Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
        } catch (Exception e) {
            ChatWriter.log(true, "Could not register recipe for the Sharpness-Modifier!"); //executes if the recipe could not initialize
        }
    }

    public static void registerLuckModifier() {
        try {
            ShapedRecipe newRecipe = new ShapedRecipe(new NamespacedKey(Main.getPlugin(), "Modifier_Luck"), Modifiers.LUCK_MODIFIER); //init recipe
            newRecipe.shape("LLL", "LLL", "LLL"); //makes recipe
            newRecipe.setIngredient('L', Material.LAPIS_BLOCK); //set ingredients
            Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
        } catch (Exception e) {
            ChatWriter.log(true, "Could not register recipe for the Luck-Modifier!"); //executes if the recipe could not initialize
        }
    }

    public static void registerGlowingModifier() {
        try {
            ShapedRecipe newRecipe = new ShapedRecipe(new NamespacedKey(Main.getPlugin(), "Modifier_Glowing"), Modifiers.GLOWING_MODIFIER); //init recipe
            newRecipe.shape("GGG", "GEG", "GGG"); //makes recipe
            newRecipe.setIngredient('G', Material.GLOWSTONE_DUST); //set ingredients
            newRecipe.setIngredient('E', Material.ENDER_EYE);
            Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
        } catch (Exception e) {
            ChatWriter.log(true, "Could not register recipe for the Glowing-Modifier!"); //executes if the recipe could not initialize
        }
    }

    public static void registerAutoSmeltModifier() {
        try {
            ShapedRecipe newRecipe = new ShapedRecipe(new NamespacedKey(Main.getPlugin(), "Modifier_Autosmelt"), Modifiers.AUTOSMELT_MODIFIER); //init recipe
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
            ShapedRecipe newRecipe = new ShapedRecipe(new NamespacedKey(Main.getPlugin(), "Elevator_Motor"), ItemGenerator.itemEnchanter(Material.HOPPER, ChatColor.GRAY + "Elevator-Motor", 1, Enchantment.FIRE_ASPECT, 1)); //init recipe
            newRecipe.shape("RRR", "RHR", "RRR"); //makes recipe
            newRecipe.setIngredient('R', Material.REDSTONE); //set ingredients
            newRecipe.setIngredient('H', Material.HOPPER);
            Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
        } catch (Exception e) {
            ChatWriter.log(true, "Could not register recipe for the Elevator-Motor!"); //executes if the recipe could not initialize
        }
    }

    public static void registerShulkingModifier() {
        try {
            ShapedRecipe newRecipe = new ShapedRecipe(new NamespacedKey(Main.getPlugin(), "Modifier_Shulking"), Modifiers.SHULKING_MODIFIER); //init recipe
            newRecipe.shape(" S ", " C ", " S "); //makes recipe
            newRecipe.setIngredient('S', Material.SHULKER_SHELL); //set ingredients
            newRecipe.setIngredient('C', Material.CHORUS_FRUIT);
            Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
        } catch (Exception e) {
            ChatWriter.log(true, "Could not register recipe for the Shulking-Modifier!"); //executes if the recipe could not initialize
        }
    }

    public static void registerEnderModifier() {
        try {
            ShapedRecipe newRecipe = new ShapedRecipe(new NamespacedKey(Main.getPlugin(), "Modifier_Ender"), Modifiers.ENDER_MODIFIER); //init recipe
            newRecipe.shape("PPP", "PEP", "PPP"); //makes recipe
            newRecipe.setIngredient('P', Material.ENDER_PEARL); //set ingredients
            newRecipe.setIngredient('E', Material.ENDER_EYE);
            Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
        } catch (Exception e) {
            ChatWriter.log(true, "Could not register recipe for the Ender-Modifier!"); //executes if the recipe could not initialize
        }
    }

    public static void registerBuildersWands() {
        try {
            ShapedRecipe newRecipe = new ShapedRecipe(new NamespacedKey(Main.getPlugin(), "Builderswand_Wood"), ItemGenerator.buildersWandCreator(Material.WOODEN_SHOVEL, "Wooden Builderswand", 1)); //init recipe
            newRecipe.shape("  W", " S ", "S  "); //makes recipe
            newRecipe.setIngredient('S', Material.STICK); //set ingredients
            newRecipe.setIngredient('W', Material.LEGACY_WOOD);
            Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
        } catch (Exception e) {
            ChatWriter.log(true, "Could not register recipe for the Wooden Builderswand!"); //executes if the recipe could not initialize
        }
        try {
            ShapedRecipe newRecipe = new ShapedRecipe(new NamespacedKey(Main.getPlugin(), "Builderswand_Stone"), ItemGenerator.buildersWandCreator(Material.STONE_SHOVEL, "Stone Builderswand", 1)); //init recipe
            newRecipe.shape("  C", " S ", "S  "); //makes recipe
            newRecipe.setIngredient('S', Material.STICK); //set ingredients
            newRecipe.setIngredient('C', Material.COBBLESTONE);
            Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
        } catch (Exception e) {
            ChatWriter.log(true, "Could not register recipe for the Stone Builderswand!"); //executes if the recipe could not initialize
        }
        try {
            ShapedRecipe newRecipe = new ShapedRecipe(new NamespacedKey(Main.getPlugin(), "Builderswand_Iron"), ItemGenerator.buildersWandCreator(Material.IRON_SHOVEL, "Iron Builderswand", 1)); //init recipe
            newRecipe.shape("  I", " S ", "S  "); //makes recipe
            newRecipe.setIngredient('S', Material.STICK); //set ingredients
            newRecipe.setIngredient('I', Material.IRON_INGOT);
            Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
        } catch (Exception e) {
            ChatWriter.log(true, "Could not register recipe for the Iron Builderswand!"); //executes if the recipe could not initialize
        }
        try {
            ShapedRecipe newRecipe = new ShapedRecipe(new NamespacedKey(Main.getPlugin(), "Builderswand_Gold"), ItemGenerator.buildersWandCreator(Material.GOLDEN_SHOVEL, "Golden Builderswand", 1)); //init recipe
            newRecipe.shape("  G", " S ", "S  "); //makes recipe
            newRecipe.setIngredient('S', Material.STICK); //set ingredients
            newRecipe.setIngredient('G', Material.GOLD_INGOT);
            Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
        } catch (Exception e) {
            ChatWriter.log(true, "Could not register recipe for the Golden Builderswand!"); //executes if the recipe could not initialize
        }
        try {
            ShapedRecipe newRecipe = new ShapedRecipe(new NamespacedKey(Main.getPlugin(), "Builderswand_Diamond"), ItemGenerator.buildersWandCreator(Material.DIAMOND_SHOVEL, "Diamond Builderswand", 1)); //init recipe
            newRecipe.shape("  D", " S ", "S  "); //makes recipe
            newRecipe.setIngredient('S', Material.STICK); //set ingredients
            newRecipe.setIngredient('D', Material.DIAMOND);
            Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
        } catch (Exception e) {
            ChatWriter.log(true, "Could not register recipe for the Diamond Builderswand!"); //executes if the recipe could not initialize
        }
    }

    public static void registerTimberModifier() {
        try {
            ShapedRecipe newRecipe = new ShapedRecipe(new NamespacedKey(Main.getPlugin(), "Modifier_Timber"), Modifiers.TIMBER_MODIFIER); //init recipe
            newRecipe.shape("LLL", "LEL", "LLL"); //makes recipe
            newRecipe.setIngredient('L', Material.OAK_WOOD); //set ingredients
            newRecipe.setIngredient('E', Material.EMERALD);
            Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
        } catch (Exception e) {
            ChatWriter.log(true, "Could not register recipe for the Timber-Modifier!"); //executes if the recipe could not initialize
        }
    }

    public static void registerWebbedModifier() {
        try {
            ShapedRecipe newRecipe = new ShapedRecipe(new NamespacedKey(Main.getPlugin(), "Modifier_Webbed"), Modifiers.WEBBED_MODIFIER); //init recipe
            newRecipe.shape("WWW", "WWW", "WWW"); //makes recipe
            newRecipe.setIngredient('W', Material.COBWEB); //set ingredients
            Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
        } catch (Exception e) {
            ChatWriter.log(true, "Could not register recipe for the Webbed-Modifier!"); //executes if the recipe could not initialize
        }
    }

    public static void registerDirectingModifier() {
        try {
            ShapedRecipe newRecipe = new ShapedRecipe(new NamespacedKey(Main.getPlugin(), "Modifier_Directing"), Modifiers.DIRECTING_MODIFIER); //init recipe
            newRecipe.shape("ECE", "CIC", "ECE"); //makes recipe
            newRecipe.setIngredient('C', Material.COMPASS); //set ingredients
            newRecipe.setIngredient('E', Material.ENDER_PEARL);
            newRecipe.setIngredient('I', Material.IRON_BLOCK);
            Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
        } catch (Exception e) {
            ChatWriter.log(true, "Could not register recipe for the Directing-Modifier!"); //executes if the recipe could not initialize
        }
    }
}

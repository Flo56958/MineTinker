package de.flo56958.MineTinker.Data;

import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class Modifiers {

    /*
    To add a new modifier:
    config
    Modifiers
    Lists
    Strings
    AnvilListener
    ItemGenerator
    ModifierApply
    */

    private static final FileConfiguration config = Main.getPlugin().getConfig();

    public static final ItemStack AUTOSMELT_MODIFIER = ItemGenerator.itemEnchanter(Material.FURNACE, ChatColor.YELLOW + config.getString("Modifiers.Auto-Smelt.name_modifier"), 1, Enchantment.FIRE_ASPECT, 1);
    public static final ItemStack BEHEADING_MODIFIER = ItemGenerator.itemEnchanter(Material.WITHER_SKELETON_SKULL, ChatColor.DARK_GRAY + config.getString("Modifiers.Beheading.name_modifier"), 1, Enchantment.LOOT_BONUS_MOBS, 1);
    public static final ItemStack DIRECTING_MODIFIER = ItemGenerator.itemEnchanter(Material.COMPASS, ChatColor.GRAY + config.getString("Modifiers.Directing.name_modifier"), 1, Enchantment.BINDING_CURSE, 1);
    public static final ItemStack ENDER_MODIFIER = ItemGenerator.itemEnchanter(Material.ENDER_EYE, ChatColor.DARK_GREEN + config.getString("Modifiers.Ender.name_modifier"), 1, Enchantment.DURABILITY, 1);
    public static final ItemStack FIERY_MODIFIER = ItemGenerator.itemEnchanter(Material.BLAZE_ROD, ChatColor.YELLOW + config.getString("Modifiers.Fiery.name_modifier"), 1, Enchantment.FIRE_ASPECT, 1);
    public static final ItemStack GLOWING_MODIFIER = ItemGenerator.itemEnchanter(Material.GLOWSTONE, ChatColor.YELLOW + config.getString("Modifiers.Glowing.name_modifier"), 1, Enchantment.DURABILITY, 1);
    public static final ItemStack HASTE_MODIFIER = ItemGenerator.itemEnchanter(Material.REDSTONE_BLOCK, ChatColor.DARK_RED + config.getString("Modifiers.Haste.name_modifier"), 1, Enchantment.DIG_SPEED, 1);
    public static final ItemStack INFINITY_MODIFIER = ItemGenerator.itemEnchanter(Material.ARROW, ChatColor.WHITE + config.getString("Modifiers.Infinity.name_modifier"), 1, Enchantment.ARROW_INFINITE, 1);
    public static final ItemStack KNOCKBACK_MODIFIER = ItemGenerator.itemEnchanter(Material.TNT, ChatColor.GRAY + config.getString("Modifiers.Knockback.name_modifier"), 1, Enchantment.KNOCKBACK, 1);
    public static final ItemStack LUCK_MODIFIER = ItemGenerator.itemEnchanter(Material.LAPIS_BLOCK, ChatColor.BLUE + config.getString("Modifiers.Luck.name_modifier"), 1, Enchantment.LOOT_BONUS_BLOCKS, 1);
    public static final ItemStack MELTING_MODIFIER = ItemGenerator.itemEnchanter(Material.MAGMA_BLOCK, ChatColor.GOLD + config.getString("Modifiers.Melting.name_modifier"), 1, Enchantment.FIRE_ASPECT, 1);
    public static final ItemStack POISONOUS_MODIFIER = ItemGenerator.itemEnchanter(Material.ROTTEN_FLESH, ChatColor.DARK_GREEN + config.getString("Modifiers.Poisonous.name_modifier"), 1, Enchantment.DURABILITY, 1);
    public static final ItemStack POWER_MODIFIER = ItemGenerator.itemEnchanter(Material.EMERALD, ChatColor.GREEN + config.getString("Modifiers.Power.name_modifier"), 1, Enchantment.ARROW_DAMAGE, 1);
    public static final ItemStack REINFORCED_MODIFIER = ItemGenerator.itemEnchanter(Material.OBSIDIAN, ChatColor.GRAY + config.getString("Modifiers.Reinforced.name_modifier"), 1, Enchantment.DURABILITY, 1);
    public static final ItemStack SELFREPAIR_MODIFIER = ItemGenerator.itemEnchanter(Material.MOSSY_COBBLESTONE, ChatColor.GREEN + config.getString("Modifiers.Self-Repair.name_modifier"), 1, Enchantment.MENDING, 1);
    public static final ItemStack SHARPNESS_MODIFIER = ItemGenerator.itemEnchanter(Material.QUARTZ_BLOCK, ChatColor.WHITE + config.getString("Modifiers.Sharpness.name_modifier"), 1 , Enchantment.DAMAGE_ALL, 1);
    public static final ItemStack SHULKING_MODIFIER = ItemGenerator.itemEnchanter(Material.SHULKER_SHELL, ChatColor.LIGHT_PURPLE + config.getString("Modifiers.Shulking.name_modifier"), 1, Enchantment.DURABILITY, 1);
    public static final ItemStack SILKTOUCH_MODIFIER = ItemGenerator.itemEnchanter(Material.COBWEB, ChatColor.WHITE + config.getString("Modifiers.Silk-Touch.name_modifier"), 1, Enchantment.SILK_TOUCH, 1);
    public static final ItemStack SWEEPING_MODIFIER = ItemGenerator.itemEnchanter(Material.IRON_INGOT, ChatColor.RED + config.getString("Modifiers.Sweeping.name_modifier"), 1, Enchantment.SWEEPING_EDGE, 1);
    public static final ItemStack TIMBER_MODIFIER = ItemGenerator.itemEnchanter(Material.EMERALD, ChatColor.GREEN + config.getString("Modifiers.Timber.name_modifier"), 1, Enchantment.DIG_SPEED, 1);
    public static final ItemStack WEBBED_MODIFIER = ItemGenerator.itemEnchanter(Material.COBWEB, ChatColor.WHITE + config.getString("Modifiers.Webbed.name_modifier"), 1, Enchantment.DAMAGE_ALL, 1);
}

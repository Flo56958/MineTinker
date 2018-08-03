package de.flo56958.MineTinker.Data;

import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class Modifiers {

    public static final ItemStack SELFREPAIR_MODIFIER = ItemGenerator.itemEnchanter(Material.MOSSY_COBBLESTONE, ChatColor.GREEN + Main.getPlugin().getConfig().getString("Modifiers.Self-Repair.name_modifier"), 1, Enchantment.MENDING, 1);
    public static final ItemStack REINFORCED_MODIFIER = ItemGenerator.itemEnchanter(Material.OBSIDIAN, ChatColor.GRAY + Main.getPlugin().getConfig().getString("Modifiers.Reinforced.name_modifier"), 1, Enchantment.DURABILITY, 1);
    public static final ItemStack HASTE_MODIFIER = ItemGenerator.itemEnchanter(Material.REDSTONE_BLOCK, ChatColor.DARK_RED + Main.getPlugin().getConfig().getString("Modifiers.Haste.name_modifier"), 1, Enchantment.DIG_SPEED, 1);
    public static final ItemStack SHARPNESS_MODIFIER = ItemGenerator.itemEnchanter(Material.QUARTZ_BLOCK, ChatColor.WHITE + Main.getPlugin().getConfig().getString("Modifiers.Sharpness.name_modifier"), 1 , Enchantment.DAMAGE_ALL, 1);
    public static final ItemStack LUCK_MODIFIER = ItemGenerator.itemEnchanter(Material.LAPIS_BLOCK, ChatColor.BLUE + Main.getPlugin().getConfig().getString("Modifiers.Luck.name_modifier"), 1, Enchantment.LOOT_BONUS_BLOCKS, 1);
    public static final ItemStack SILKTOUCH_MODIFIER = ItemGenerator.itemEnchanter(Material.COBWEB, ChatColor.WHITE + Main.getPlugin().getConfig().getString("Modifiers.Silk-Touch.name_modifier"), 1, Enchantment.SILK_TOUCH, 1);
    public static final ItemStack FIERY_MODIFIER = ItemGenerator.itemEnchanter(Material.BLAZE_ROD, ChatColor.YELLOW + Main.getPlugin().getConfig().getString("Modifiers.Fiery.name_modifier"), 1, Enchantment.FIRE_ASPECT, 1);
    public static final ItemStack AUTOSMELT_MODIFIER = ItemGenerator.itemEnchanter(Material.FURNACE, ChatColor.YELLOW + Main.getPlugin().getConfig().getString("Modifiers.Auto-Smelt.name_modifier"), 1, Enchantment.FIRE_ASPECT, 1);
    public static final ItemStack POWER_MODIFIER = ItemGenerator.itemEnchanter(Material.EMERALD, ChatColor.GREEN + Main.getPlugin().getConfig().getString("Modifiers.Power.name_modifier"), 1, Enchantment.ARROW_DAMAGE, 1);
    public static final ItemStack BEHEADING_MODIFIER = ItemGenerator.itemEnchanter(Material.WITHER_SKELETON_SKULL, ChatColor.DARK_GRAY + Main.getPlugin().getConfig().getString("Modifiers.Beheading.name_modifier"), 1, Enchantment.LOOT_BONUS_MOBS, 1);

}

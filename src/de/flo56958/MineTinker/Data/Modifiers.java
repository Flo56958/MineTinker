package de.flo56958.MineTinker.Data;

import de.flo56958.MineTinker.Utilities.ItemGenerator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class Modifiers {

    public static final ItemStack SELFREPAIR_MODIFIER = ItemGenerator.itemEnchanter(Material.MOSSY_COBBLESTONE, ChatColor.GREEN + "Self-Repair-Modifier", 1, Enchantment.MENDING, 1);
    public static final ItemStack REINFORCED_MODIFIER = ItemGenerator.itemEnchanter(Material.OBSIDIAN, ChatColor.BLACK + "Reinforced-Modifier", 1, Enchantment.DURABILITY, 1);
    public static final ItemStack HASTE_MODIFIER = ItemGenerator.itemEnchanter(Material.REDSTONE_BLOCK, ChatColor.DARK_RED + "Haste-Modifier", 1, Enchantment.DIG_SPEED, 1);
    public static final ItemStack SHARPNESS_MODIFIER = ItemGenerator.itemEnchanter(Material.QUARTZ_BLOCK, ChatColor.WHITE + "Sharpness-Modifier", 1 , Enchantment.DAMAGE_ALL, 1);
    public static final ItemStack LUCK_MODIFIER = ItemGenerator.itemEnchanter(Material.LAPIS_BLOCK, ChatColor.BLUE + "Luck-Modifier", 1, Enchantment.LOOT_BONUS_BLOCKS, 1);
    public static final ItemStack SILKTOUCH_MODIFIER = ItemGenerator.itemEnchanter(Material.WEB, ChatColor.WHITE + "Silk-Touch-Modifier", 1, Enchantment.SILK_TOUCH, 1);
    public static final ItemStack FIERY_MODIFIER = ItemGenerator.itemEnchanter(Material.BLAZE_ROD, ChatColor.YELLOW + "Fiery-Modifier", 1, Enchantment.FIRE_ASPECT, 1);
    public static final ItemStack AUTOSMELT_MODIFIER = ItemGenerator.itemEnchanter(Material.FURNACE, ChatColor.YELLOW + "Auto-Smelt-Modifier", 1, Enchantment.FIRE_ASPECT, 1);

}

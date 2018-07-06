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

}

package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ModifierFailCause;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.ModifierFailEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Craftable;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.Arrays;

public class Luck extends Modifier implements Craftable {

    private static final ModManager modManager = Main.getModManager();
    private static PluginManager pluginManager = Bukkit.getPluginManager();
    private static final FileConfiguration config = Main.getPlugin().getConfig();

    public Luck() {
        super(config.getString("Modifiers.Luck.name"),
                "[Compressed Lapis Block] Get more loot from enemies and blocks!",
                ModifierType.LUCK,
                ChatColor.BLUE,
                config.getInt("Modifiers.Luck.MaxLevel"),
                ItemGenerator.itemEnchanter(Material.LAPIS_BLOCK, ChatColor.BLUE + config.getString("Modifiers.Luck.name_modifier"), 1, Enchantment.LOOT_BONUS_BLOCKS, 1),
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.HOE, ToolType.PICKAXE, ToolType.SHOVEL, ToolType.SWORD)),
                Main.getPlugin());
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (modManager.get(ModifierType.SILK_TOUCH) != null) {
            if (modManager.hasMod(tool, modManager.get(ModifierType.SILK_TOUCH))) {
                pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
                return null;
            }
        }
        if (Modifier.checkAndAdd(p, tool, this, "luck", isCommand) == null) {
            return null;
        }

        ItemMeta meta = tool.getItemMeta();

        if (ToolType.AXE.getMaterials().contains(tool.getType())) {
            meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, modManager.getModLevel(tool, this), true);
            meta.addEnchant(Enchantment.LOOT_BONUS_MOBS, modManager.getModLevel(tool, this), true);
        } else if (ToolType.BOW.getMaterials().contains(tool.getType())) {
            meta.addEnchant(Enchantment.LOOT_BONUS_MOBS, modManager.getModLevel(tool, this), true);
        } else if (ToolType.HOE.getMaterials().contains(tool.getType())) {
            meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, modManager.getModLevel(tool, this), true);
        } else if (ToolType.PICKAXE.getMaterials().contains(tool.getType())) {
            meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, modManager.getModLevel(tool, this), true);
        } else if (ToolType.SHOVEL.getMaterials().contains(tool.getType())) {
            meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, modManager.getModLevel(tool, this), true);
        } else if (ToolType.SWORD.getMaterials().contains(tool.getType())) {
            meta.addEnchant(Enchantment.LOOT_BONUS_MOBS, modManager.getModLevel(tool, this), true);
        }
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        tool.setItemMeta(meta);

        return tool;
    }

    @Override
    public void registerCraftingRecipe() {
        try {
            ShapedRecipe newRecipe = new ShapedRecipe(new NamespacedKey(Main.getPlugin(), "Modifier_Luck"), modManager.get(ModifierType.LUCK).getModItem()); //init recipe
            newRecipe.shape("LLL", "LLL", "LLL"); //makes recipe
            newRecipe.setIngredient('L', Material.LAPIS_BLOCK); //set ingredients
            Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
        } catch (Exception e) {
            ChatWriter.log(true, "Could not register recipe for the Luck-Modifier!"); //executes if the recipe could not initialize
        }
    }
}

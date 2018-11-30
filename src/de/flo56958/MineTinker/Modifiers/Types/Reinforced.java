package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
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

public class Reinforced extends Modifier implements Craftable {
    private static final ModManager modManager = Main.getModManager();
    private static PluginManager pluginManager = Bukkit.getPluginManager();
    private static final FileConfiguration config = Main.getPlugin().getConfig();

    public Reinforced() {
        super(config.getString("Modifiers.Reinforced.name"),
                "[Compressed Obsidian] Chance to not use durability when using the tool/armor!",
                ModifierType.REINFORCED,
                ChatColor.DARK_GRAY,
                config.getInt("Modifiers.Reinforced.MaxLevel"),
                ItemGenerator.itemEnchanter(Material.OBSIDIAN, ChatColor.DARK_GRAY + config.getString("Modifiers.Reinforced.name_modifier"), 1, Enchantment.DURABILITY, 1),
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.HOE, ToolType.PICKAXE, ToolType.SHOVEL, ToolType.SWORD,
                                                ToolType.HELMET, ToolType.CHESTPLATE, ToolType.LEGGINGS, ToolType.BOOTS, ToolType.ELYTRA)),
                Main.getPlugin());
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (Modifier.checkAndAdd(p, tool, this, "reinforced", isCommand) == null) {
            return null;
        }

        ItemMeta meta = tool.getItemMeta();

        meta.addEnchant(Enchantment.DURABILITY, modManager.getModLevel(tool, this), true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        tool.setItemMeta(meta);

        return tool;
    }

    @Override
    public void registerCraftingRecipe() {
        try {
            ShapedRecipe newRecipe = new ShapedRecipe(new NamespacedKey(Main.getPlugin(), "Modifier_Reinforced"), modManager.get(ModifierType.REINFORCED).getModItem()); //init recipe
            newRecipe.shape("OOO", "OOO", "OOO"); //makes recipe
            newRecipe.setIngredient('O', Material.OBSIDIAN); //set ingredients
            Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
        } catch (Exception e) {
            ChatWriter.log(true, "Could not register recipe for the Reinforced-Modifier!"); //executes if the recipe could not initialize
        }
    }
}

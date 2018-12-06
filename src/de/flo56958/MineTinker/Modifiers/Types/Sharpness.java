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
import org.bukkit.configuration.ConfigurationSection;
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

public class Sharpness extends Modifier implements Craftable {

    private static final ModManager modManager = Main.getModManager();
    private static PluginManager pluginManager = Bukkit.getPluginManager();
    private static final FileConfiguration config = Main.getPlugin().getConfig();
    private static final FileConfiguration recipesConfig = Main.getMain().getRecipeConfig();

    public Sharpness() {
        super(config.getString("Modifiers.Sharpness.name"),
                "[Compressed Quartzblock] Tool does additional damage!",
                ModifierType.SHARPNESS,
                ChatColor.WHITE,
                config.getInt("Modifiers.Sharpness.MaxLevel"),
                ItemGenerator.itemEnchanter(Material.QUARTZ_BLOCK, ChatColor.WHITE + config.getString("Modifiers.Sharpness.name_modifier"), 1 , Enchantment.DAMAGE_ALL, 1),
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.SWORD)),
                Main.getPlugin());
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (Modifier.checkAndAdd(p, tool, this, "sharpness", isCommand) == null) {
            return null;
        }

        ItemMeta meta = tool.getItemMeta();

        if (ToolType.AXE.getMaterials().contains(tool.getType())) {
            meta.addEnchant(Enchantment.DAMAGE_ALL, modManager.getModLevel(tool, this), true);
        } else if (ToolType.BOW.getMaterials().contains(tool.getType())) {
            meta.addEnchant(Enchantment.ARROW_DAMAGE, modManager.getModLevel(tool, this), true);
        } else if (ToolType.SWORD.getMaterials().contains(tool.getType())) {
            meta.addEnchant(Enchantment.DAMAGE_ALL, modManager.getModLevel(tool, this), true);
        }

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        tool.setItemMeta(meta);

        return tool;
    }

    @Override
    public void registerCraftingRecipe() {
        try {
            ShapedRecipe newRecipe = new ShapedRecipe(new NamespacedKey(Main.getPlugin(), "Modifier_Sharpness"), modManager.get(ModifierType.SHARPNESS).getModItem()); //init recipe
            String top = recipesConfig.getString("Recipes.Sharpness.Top");
            String middle = recipesConfig.getString("Recipes.Sharpness.Middle");
            String bottom = recipesConfig.getString("Recipes.Sharpness.Bottom");
            ConfigurationSection materials = recipesConfig.getConfigurationSection("Recipes.Sharpness.Materials");
            newRecipe.shape(top, middle, bottom); //makes recipe
            for (String key : materials.getKeys(false)) {
                newRecipe.setIngredient(key.charAt(0), Material.getMaterial(materials.getString(key)));
            }
            Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
        } catch (Exception e) {
            ChatWriter.log(true, "Could not register recipe for the Sharpness-Modifier!"); //executes if the recipe could not initialize
        }
    }
}

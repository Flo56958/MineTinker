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
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Directing extends Modifier implements Craftable {

    private static final ModManager modManager = Main.getModManager();
    private static PluginManager pluginManager = Bukkit.getPluginManager();
    private static final FileConfiguration config = Main.getPlugin().getConfig();
    private static final FileConfiguration recipesConfig = Main.getMain().getRecipeConfig();

    public Directing() {
        super(config.getString("Modifiers.Directing.name"),
                "[Enhanced Compass] Loot goes directly into Inventory.",
                ModifierType.DIRECTING,
                ChatColor.GRAY,
                1,
                ItemGenerator.itemEnchanter(Material.COMPASS, ChatColor.GRAY + config.getString("Modifiers.Directing.name_modifier"), 1, Enchantment.BINDING_CURSE, 1),
                new ArrayList<>(Arrays.asList(ToolType.BOW, ToolType.SWORD)),
                Main.getPlugin());
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        return Modifier.checkAndAdd(p, tool, this, "directing", isCommand);
    }

    public void effect(Player p, ItemStack tool, ItemStack loot, EntityDeathEvent e) {
        if (p.hasPermission("minetinker.modifiers.directing.use")) {
            if (modManager.hasMod(tool, this)) {
                List<ItemStack> drops = e.getDrops();
                if (!loot.equals(new ItemStack(Material.AIR, 1))) {
                    drops.add(loot);
                }
                for (ItemStack current : drops) {
                    if(p.getInventory().addItem(current).size() != 0) { //adds items to (full) inventory
                        p.getWorld().dropItem(p.getLocation(), current);
                    } // no else as it gets added in if-clause
                }
                drops.clear();
            } else {
                if (!loot.equals(new ItemStack(Material.AIR, 1))) {
                    p.getWorld().dropItemNaturally(e.getEntity().getLocation(), loot);
                }
            }
        } else {
            if (!loot.equals(new ItemStack(Material.AIR, 1))) {
                p.getWorld().dropItemNaturally(e.getEntity().getLocation(), loot);
            }
        }
    }

    @Override
    public void registerCraftingRecipe() {
        try {
            ShapedRecipe newRecipe = new ShapedRecipe(new NamespacedKey(Main.getPlugin(), "Modifier_Directing"), modManager.get(ModifierType.DIRECTING).getModItem()); //init recipe
            String top = recipesConfig.getString("Recipes.Directing.Top");
            String middle = recipesConfig.getString("Recipes.Directing.Middle");
            String bottom = recipesConfig.getString("Recipes.Directing.Bottom");
            ConfigurationSection materials = recipesConfig.getConfigurationSection("Recipes.Directing.Materials");
            newRecipe.shape(top, middle, bottom); //makes recipe
            for (String key : materials.getKeys(false)) {
                newRecipe.setIngredient(key.charAt(0), Material.getMaterial(materials.getString(key)));
            }
            Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
        } catch (Exception e) {
            ChatWriter.log(true, "Could not register recipe for the Directing-Modifier!"); //executes if the recipe could not initialize
        }
    }
}

package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.ModManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

public class ConvertToolListener implements Listener {

    private static final ModManager modManager = ModManager.instance();

    public ConvertToolListener() {
        for (Material m : ToolType.getAllToolMaterials()) {
            ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(Main.getPlugin(), m.toString() + "_Converter"), new ItemStack(m, 1));
            recipe.addIngredient(m);

            Bukkit.addRecipe(recipe);
        }
    }

    @EventHandler
    public void onCraft(PrepareItemCraftEvent event) {
        if (event.getInventory().getResult() == null) {
            return;
        }

        boolean canConvert = false;
        World world = null;

        for (HumanEntity human : event.getInventory().getViewers()) {
            if (human.hasPermission("minetinker.tool.create")) {
                canConvert = true;
                world = human.getWorld();
            }
        }

        if (!canConvert) {
            return;
        }

        if (Lists.WORLDS.contains(world.getName())) {
            return;
        }

        int recipeItems = 0;
        ItemStack lastItem = null;

        for (ItemStack item : event.getInventory().getMatrix()) {
            if (item != null && item.getType() != Material.AIR) {
                recipeItems += 1;
                lastItem = item;
            }
        }

        ItemStack result = event.getInventory().getResult();

        if (recipeItems == 1 && lastItem.getType() == result.getType()) {
            if (modManager.isArmorViable(lastItem) || modManager.isToolViable(lastItem) || modManager.isWandViable(lastItem)) {
                event.getInventory().setResult(new ItemStack(Material.AIR, 1));
                return;
            }

            if (ModManager.instance().convertItemStack(result)) {
                result.setAmount(1);
            }
        }
    }
}

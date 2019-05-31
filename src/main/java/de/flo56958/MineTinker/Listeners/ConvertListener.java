package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class ConvertListener implements Listener{
	private static final FileConfiguration config = Main.getPlugin().getConfig();
    private static final ModManager modManager = ModManager.instance();
	
	public void register() {
	    ArrayList<Material> converting = new ArrayList<>();
	    converting.addAll(ToolType.PICKAXE.getMaterials());
		converting.addAll(ToolType.AXE.getMaterials());
		converting.addAll(ToolType.HOE.getMaterials());
		converting.addAll(ToolType.SWORD.getMaterials());
		converting.addAll(ToolType.SHEARS.getMaterials());
		converting.addAll(ToolType.FISHINGROD.getMaterials());
		converting.addAll(ToolType.HELMET.getMaterials());
		converting.addAll(ToolType.CHESTPLATE.getMaterials());
		converting.addAll(ToolType.LEGGINGS.getMaterials());
		converting.addAll(ToolType.BOOTS.getMaterials());
		converting.addAll(ToolType.BOW.getMaterials());
	    
	    for (Material m : converting) {
	    	ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(Main.getPlugin(), m.toString() + "_Converter"), new ItemStack(m, 1));
			recipe.addIngredient(m);
			Bukkit.addRecipe(recipe);
	    }
	}
	
	@EventHandler
	public void PrepareCraft(PrepareItemCraftEvent e) {
		if (e.getRecipe() == null) return;

		Player player = null;

		for (HumanEntity humans : e.getViewers()) {
			if (humans instanceof Player) { player = (Player) humans; }
		}

		if (player == null) return;
		if (!player.hasPermission("minetinker.tool.create")) return;
		if (Lists.WORLDS.contains(player.getWorld().getName())) return;

		ItemStack currentItem = e.getInventory().getResult();

		if (currentItem == null) return;

		int actualItems = 0;
		ItemStack lastItem = null;

		for (ItemStack item : e.getInventory().getMatrix()) {
			if (item != null && item.getType() != Material.AIR) {
				actualItems++;
				lastItem = item;
			}
		}

		if (lastItem != null && actualItems == 1 && currentItem.getType() == lastItem.getType()) {
			if (modManager.isArmorViable(lastItem) || modManager.isToolViable(lastItem) || modManager.isWandViable(lastItem)) {
				e.getInventory().setResult(new ItemStack(Material.AIR, 1));
				return;
			}
		}

		ItemMeta m = currentItem.getItemMeta();

		if (m != null) {
			if (modManager.isWandViable(currentItem)) {
				return;
			}
		}

		modManager.convertItemStack(currentItem);
	}
	
	@EventHandler(ignoreCancelled = true)
    public void onCraft(CraftItemEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;

        Player player = (Player) e.getWhoClicked();
        
        if (config.getBoolean("Sound.OnEveryCrafting")) {
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0F, 0.5F);
            return;
        }
        
        ItemStack tool = e.getInventory().getResult();
        
        if (!(modManager.isToolViable(tool) || modManager.isArmorViable(tool) || modManager.isWandViable(tool))) return;

        if (config.getBoolean("Sound.OnCrafting")) {
			player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0F, 0.5F);
		}

        if (tool != null) {
			ChatWriter.log(false, player.getName() + " crafted " + ItemGenerator.getDisplayName(tool) + "! It is now a MineTinker-Item!");
		}
    }
}

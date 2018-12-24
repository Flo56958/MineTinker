package de.flo56958.MineTinker.Listeners;

import java.util.ArrayList;
import java.util.Arrays;

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

import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ItemGenerator;

public class ConvertListener implements Listener{
	private static final FileConfiguration config = Main.getPlugin().getConfig();
    private static final ModManager modManager = ModManager.instance();

    private static final ArrayList<ToolType> tools;
    private static final ArrayList<ToolType> armor;

    static {
        tools = new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.HOE, ToolType.PICKAXE, ToolType.SHOVEL, ToolType.SWORD));
        armor = new ArrayList<>(Arrays.asList(ToolType.HELMET, ToolType.CHESTPLATE, ToolType.LEGGINGS, ToolType.BOOTS, ToolType.ELYTRA));
    }
	
	public void register() {
	    ArrayList<Material> converting = new ArrayList<>();
	    converting.add(Material.WOODEN_PICKAXE);
	    converting.add(Material.STONE_PICKAXE);
	    converting.add(Material.GOLDEN_PICKAXE);
	    converting.add(Material.IRON_PICKAXE);
	    converting.add(Material.DIAMOND_PICKAXE);
	    
	    converting.add(Material.WOODEN_AXE);
	    converting.add(Material.STONE_AXE);
	    converting.add(Material.GOLDEN_AXE);
	    converting.add(Material.IRON_AXE);
	    converting.add(Material.DIAMOND_AXE);
	    
	    converting.add(Material.WOODEN_HOE);
	    converting.add(Material.STONE_HOE);
	    converting.add(Material.GOLDEN_HOE);
	    converting.add(Material.IRON_HOE);
	    converting.add(Material.DIAMOND_HOE);
	    
	    converting.add(Material.WOODEN_SWORD);
	    converting.add(Material.STONE_SWORD);
	    converting.add(Material.GOLDEN_SWORD);
	    converting.add(Material.IRON_SWORD);
	    converting.add(Material.DIAMOND_SWORD);
	    
	    converting.add(Material.LEATHER_HELMET);
	    converting.add(Material.CHAINMAIL_HELMET);
	    converting.add(Material.GOLDEN_HELMET);
	    converting.add(Material.IRON_HELMET);
	    converting.add(Material.DIAMOND_HELMET);
	    
	    converting.add(Material.LEATHER_CHESTPLATE);
	    converting.add(Material.CHAINMAIL_CHESTPLATE);
	    converting.add(Material.GOLDEN_CHESTPLATE);
	    converting.add(Material.IRON_CHESTPLATE);
	    converting.add(Material.DIAMOND_CHESTPLATE);
	    
	    converting.add(Material.LEATHER_LEGGINGS);
	    converting.add(Material.CHAINMAIL_LEGGINGS);
	    converting.add(Material.GOLDEN_LEGGINGS);
	    converting.add(Material.IRON_LEGGINGS);
	    converting.add(Material.DIAMOND_LEGGINGS);
	    
	    converting.add(Material.LEATHER_BOOTS);
	    converting.add(Material.CHAINMAIL_BOOTS);
	    converting.add(Material.GOLDEN_BOOTS);
	    converting.add(Material.IRON_BOOTS);
	    converting.add(Material.DIAMOND_BOOTS);
	    
	    converting.add(Material.BOW);
	    converting.add(Material.TURTLE_HELMET);
	    converting.add(Material.ELYTRA);
	    
	    for(Material m : converting) {
	    	ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(Main.getPlugin(), m.toString() + "_Converter"), new ItemStack(m, 1));
			recipe.addIngredient(m);
			Bukkit.addRecipe(recipe);
	    }
	}
	
	@EventHandler
	public void PrepareCraft(PrepareItemCraftEvent e) {
		if(e.getRecipe() != null) {
			Player player = null;
			for(HumanEntity humans : e.getViewers()) {
				if(humans instanceof Player)
					player = (Player) humans;
			}
			
			if(player == null)return;
	        if (!player.hasPermission("minetinker.tool.create")) { return; }
	        if (Lists.WORLDS.contains(player.getWorld().getName())) { return; }

	        ItemStack currentItem = e.getInventory().getResult();
	        
	        if(currentItem != null) {
	        	ItemMeta m = currentItem.getItemMeta();
	        	if(m != null) {
	        		if(m.getDisplayName() != null && m.getDisplayName().contains("Builderswand")) {
	        			return;
	        		}
	        	}
	        	
		        ArrayList<String> lore = new ArrayList<>();
		        if (tools.contains(ToolType.get(currentItem.getType()))) {
		            lore.add(modManager.IDENTIFIER_TOOL);
		        } else if (armor.contains(ToolType.get(currentItem.getType()))) {
		            lore.add(modManager.IDENTIFIER_ARMOR);
		        } else { return; }
	
		        lore.addAll(ItemGenerator.createLore());
		        ItemGenerator.changeLore(currentItem, lore);
	        }
		}
	}
	
	@EventHandler
    public void onCraft(CraftItemEvent e) {
        if (e.isCancelled()) { return; }
        if (!(e.getWhoClicked() instanceof Player)) { return; }
        Player player = (Player) e.getWhoClicked();

        if (!player.hasPermission("minetinker.tool.create")) { return; }
        if (Lists.WORLDS.contains(player.getWorld().getName())) { return; }
        
        if (config.getBoolean("Sound.OnEveryCrafting")) {
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0F, 0.5F);
            return;
        }
        
        ItemStack tool = e.getInventory().getResult();
        
        if (!(modManager.isToolViable(tool) || modManager.isArmorViable(tool))) { return; }

        if (config.getBoolean("Sound.OnCrafting"))
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0F, 0.5F);

        ChatWriter.log(false, player.getName() + " crafted " + ItemGenerator.getDisplayName(tool) + "! It is now a MineTinker-Item!");
    }
}

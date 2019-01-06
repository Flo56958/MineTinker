package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Craftable;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import de.flo56958.MineTinker.Utilities.Modifiers_Config;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Directing extends Modifier implements Craftable {

    public Directing() {
        super(ModifierType.DIRECTING,
                new ArrayList<>(Arrays.asList(ToolType.BOW, ToolType.SWORD)),
                Main.getPlugin());
    }
    
    public void reload() {
    	FileConfiguration config = getConfig();
    	config.options().copyDefaults(true);
    	
    	String key = "Directing";
    	config.addDefault(key + ".allowed", true);
    	config.addDefault(key + ".name", key);
    	config.addDefault(key + ".name_modifier", "Enhanced Compass");
    	config.addDefault(key + ".description", "Loot goes directly into Inventory!");
        config.addDefault(key + ".description_modifier", "%WHITE%Modifier-Item for the Directing-Modifier");
        config.addDefault(key + ".Color", "%GRAY%");
        config.addDefault(key + ".Recipe.Enabled", true);
    	config.addDefault(key + ".Recipe.Top", "ECE");
    	config.addDefault(key + ".Recipe.Middle", "CIC");
    	config.addDefault(key + ".Recipe.Bottom", "ECE");
    	config.addDefault(key + ".Recipe.Materials.C", "COMPASS");
    	config.addDefault(key + ".Recipe.Materials.E", "ENDER_PEARL");
    	config.addDefault(key + ".Recipe.Materials.I", "IRON_BLOCK");
    	
    	ConfigurationManager.saveConfig(config);
    	
        init(config.getString("Directing.name"),
                "[" + config.getString("Directing.name_modifier") + "] " + config.getString("Directing.description"),
                ChatWriter.getColor(config.getString(key + ".Color")),
                1,
                modManager.createModifierItem(Material.COMPASS, ChatWriter.getColor(config.getString(key + ".Color")) + config.getString(key + ".name_modifier"), ChatWriter.addColors(config.getString(key + ".description_modifier")), this));
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        return Modifier.checkAndAdd(p, tool, this, "directing", isCommand);
    }

    @Override
    public void removeMod(ItemStack tool) { }

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
        _registerCraftingRecipe(getConfig(), this, "Directing", "Modifier_Directing");
    }
    
    private static FileConfiguration getConfig() {
    	return ConfigurationManager.getConfig(Modifiers_Config.Directing);
    }
    
    public boolean isAllowed() {
    	return getConfig().getBoolean("Directing.allowed");
    }
}

package de.flo56958.MineTinker.Modifiers.Types;

import java.util.ArrayList;
import java.util.Collections;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Data.ModifierFailCause;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.ModifierFailEvent;
import de.flo56958.MineTinker.Modifiers.Craftable;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import de.flo56958.MineTinker.Utilities.Modifiers_Config;
import net.minecraft.server.v1_13_R2.BlockPosition;

public class Timber extends Modifier implements Craftable {

    private static final ArrayList<Location> locs = new ArrayList<>();

    public Timber() {
        super(ModifierType.TIMBER,
                ChatColor.GREEN,
                new ArrayList<>(Collections.singletonList(ToolType.AXE)),
                Main.getPlugin());
    }
    
    public void reload() {
        FileConfiguration config = getConfig();
    	config.options().copyDefaults(true);
    	
    	String key = "Timber";
    	config.addDefault(key + ".allowed", true);
    	config.addDefault(key + ".name", key);
    	config.addDefault(key + ".name_modifier", "Wooden Emerald");
    	config.addDefault(key + ".description", "Chop down trees in an instant!");
    	config.addDefault(key + ".Recipe.Enabled", true);
    	config.addDefault(key + ".Recipe.Top", "LLL");
    	config.addDefault(key + ".Recipe.Middle", "LEL");
    	config.addDefault(key + ".Recipe.Bottom", "LLL");
    	config.addDefault(key + ".Recipe.Materials.L", "OAK_WOOD");
    	config.addDefault(key + ".Recipe.Materials.E", "EMERALD");
    	
    	ConfigurationManager.saveConfig(config);
        
        init(config.getString("Timber.name"),
                "[" + config.getString("Timber.name_modifier") + "] " + config.getString("Timber.description"),
                1,
                ItemGenerator.itemEnchanter(Material.EMERALD, ChatColor.GREEN + config.getString("Timber.name_modifier"), 1, Enchantment.DIG_SPEED, 1));    	
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (modManager.get(ModifierType.POWER) != null) {
            if (modManager.hasMod(tool, modManager.get(ModifierType.POWER))) {
                pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
                return null;
            }
        }

        return Modifier.checkAndAdd(p, tool, this, "timber", isCommand);
    }

    public void effect(Player p, ItemStack tool, Block b) {
        if (!modManager.hasMod(tool, this)) { return; }

        ArrayList<Material> allowed = new ArrayList<>();
        allowed.addAll(Lists.getWoodLogs());
        allowed.addAll(Lists.getWoodWood());
        boolean isTreeBottom = false; //checks for Grass or Dirt under Log
        boolean isTreeTop = false; //checks for Leaves above Log
        for (int y = b.getY() - 1; y > 0; y--) {
            if (p.getWorld().getBlockAt(b.getX(), y, b.getZ()).getType().equals(Material.GRASS_BLOCK) //for freshly grown trees
                    || p.getWorld().getBlockAt(b.getX(), y, b.getZ()).getType().equals(Material.DIRT)
                    || p.getWorld().getBlockAt(b.getX(), y, b.getZ()).getType().equals(Material.PODZOL)) { //for the 2x2 spruce trees
                isTreeBottom = true;
            }
            if (!p.getWorld().getBlockAt(b.getX(), y, b.getZ()).getType().equals(b.getType())) {
                break;
            }
        }

        for (int dy = b.getY(); dy < 256; dy++) {
            if (!allowed.contains(p.getWorld().getBlockAt(b.getX(), dy, b.getZ()).getType())) {
                Location loc = b.getLocation().clone();
                loc.setY(dy);
                if (Lists.getWoodLeaves().contains(p.getWorld().getBlockAt(loc).getType())) {
                    isTreeTop = true;
                }
                break;
            }
        }

        if (!isTreeBottom || !isTreeTop) { return; } //TODO: Improve tree check

        Power.HASPOWER.replace(p, true);
        locs.add(b.getLocation());

        breakTree(p, b, allowed);

        locs.clear();
        Power.HASPOWER.replace(p, false);

        ChatWriter.log(false, p.getDisplayName() + " triggered Timber on " + ItemGenerator.getDisplayName(tool) + ChatColor.GRAY + " (" + tool.getType().toString() + ")!");
    }

    private static void breakTree(Player p, Block b, ArrayList<Material> allowed) { //TODO: Improve algorythm
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    Location loc = b.getLocation().clone();
                    loc.add(dx, dy, dz);
                    if (locs.contains(loc)) { continue; }
                    locs.add(loc);
                    if (allowed.contains(p.getWorld().getBlockAt(loc).getType())) {
                        breakTree(p, p.getWorld().getBlockAt(loc), allowed);
                        ((CraftPlayer) p).getHandle().playerInteractManager.breakBlock(new BlockPosition(loc.getX(), loc.getY(), loc.getZ()));
                    }
                }
            }
        }
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Timber", "Modifier_Timber");
    }
    
    private static FileConfiguration getConfig() {
    	return ConfigurationManager.getConfig(Modifiers_Config.Timber);
    }
    
    public boolean isAllowed() {
    	return getConfig().isBoolean("Timber.allowed");
    }
}

package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.data.Lists;
import de.flo56958.minetinker.data.ModifierFailCause;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.events.MTBlockBreakEvent;
import de.flo56958.minetinker.events.ModifierFailEvent;
import de.flo56958.minetinker.Main;
import de.flo56958.minetinker.modifiers.Craftable;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utilities.ChatWriter;
import de.flo56958.minetinker.utilities.ConfigurationManager;
import de.flo56958.minetinker.utilities.ItemGenerator;
import de.flo56958.minetinker.utilities.Modifiers_Config;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Timber extends Modifier implements Craftable, Listener {

    private static final ArrayList<Location> locs = new ArrayList<>();

    private static Timber instance;

    public static Timber instance() {
        synchronized (Timber.class) {
            if (instance == null) instance = new Timber();
        }
        return instance;
    }

    private Timber() {
        super(ModifierType.TIMBER,
                new ArrayList<>(Collections.singletonList(ToolType.AXE)),
                Main.getPlugin());
        Bukkit.getPluginManager().registerEvents(this, Main.getPlugin());
    }

    @Override
    public List<Enchantment> getAppliedEnchantments() {
        return new ArrayList<>();
    }

    @Override
    public void reload() {
        FileConfiguration config = getConfig();
    	config.options().copyDefaults(true);
    	
    	String key = "Timber";
    	config.addDefault(key + ".allowed", true);
    	config.addDefault(key + ".name", key);
    	config.addDefault(key + ".name_modifier", "Wooden Emerald");
        config.addDefault(key + ".modifier_item", "EMERALD"); //Needs to be a viable Material-Type
        config.addDefault(key + ".description", "Chop down trees in an instant!");
        config.addDefault(key + ".description_modifier", "%WHITE%Modifier-Item for the Timber-Modifier");
        config.addDefault(key + ".Color", "%GREEN%");
        config.addDefault(key + ".MaximumBlocksPerSwing", -1);
    	config.addDefault(key + ".Recipe.Enabled", true);
    	config.addDefault(key + ".Recipe.Top", "LLL");
    	config.addDefault(key + ".Recipe.Middle", "LEL");
    	config.addDefault(key + ".Recipe.Bottom", "LLL");
    	config.addDefault(key + ".Recipe.Materials.L", "OAK_WOOD");
    	config.addDefault(key + ".Recipe.Materials.E", "EMERALD");
    	
    	ConfigurationManager.saveConfig(config);

        String name = config.getString(key + ".name");
        String modName = config.getString(key + ".name_modifier");
        String description = config.getString(key + ".description");
        String color = config.getString(key + ".Color");
        String modItem = config.getString(key + ".modifier_item");
        String modDescription = config.getString(key + ".description_modifier");

        if (name == null || modName == null || description == null || color == null || modItem == null || modDescription == null) return;

        init(name, "[" + modName + "] " + description, ChatWriter.getColor(color), 1,
                modManager.createModifierItem(Material.getMaterial(modItem), ChatWriter.getColor(color) + modName,
                        ChatWriter.addColors(modDescription), this));
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

    @Override
    public void removeMod(ItemStack tool) { }

    @EventHandler
    public void effect(MTBlockBreakEvent event) {
        if (event.isCancelled() || !this.isAllowed()) return;

        Player p = event.getPlayer();
        ItemStack tool = event.getTool();
        Block b = event.getBlock();

        if (Power.HASPOWER.get(p).get() || p.isSneaking()) return;
        if (!modManager.hasMod(tool, this)) return;

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

        if (!isTreeBottom || !isTreeTop) return; //TODO: Improve tree check

        Power.HASPOWER.get(p).set(true);
        locs.add(b.getLocation());

        breakTree(p, b, allowed);

        locs.clear();
        Power.HASPOWER.get(p).set(false);

        ChatWriter.log(false, p.getDisplayName() + " triggered Timber on " + ItemGenerator.getDisplayName(tool) + ChatColor.GRAY + " (" + tool.getType().toString() + ")!");
    }

    private static void breakTree(Player p, Block b, ArrayList<Material> allowed) { //TODO: Improve algorythm and performance -> async?
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    Location loc = b.getLocation().clone();
                    loc.add(dx, dy, dz);

                    if (locs.contains(loc)) { continue; }

                    if (getConfig().getInt("Timber.MaximumBlocksPerSwing") > 0 && locs.size() >= getConfig().getInt("Timber.MaximumBlocksPerSwing")) return;

                    locs.add(loc);

                    if (allowed.contains(p.getWorld().getBlockAt(loc).getType())) {
                        breakTree(p, p.getWorld().getBlockAt(loc), allowed);

                        BlockBreakEvent event = new BlockBreakEvent(b, p);
                        Bukkit.getServer().getPluginManager().callEvent(event);

                        if (!event.isCancelled()) b.breakNaturally(p.getInventory().getItemInMainHand());
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

    @Override
    public boolean isAllowed() {
    	return getConfig().getBoolean("Timber.allowed");
    }
}
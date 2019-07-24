package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Data.ModifierFailCause;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.MTBlockBreakEvent;
import de.flo56958.MineTinker.Events.ModifierFailEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import de.flo56958.MineTinker.Utilities.nms.NBTUtils;
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
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

public class Timber extends Modifier implements Listener {

    private static final ArrayList<Location> locs = new ArrayList<>();

    private static Timber instance;

    public static Timber instance() {
        synchronized (Timber.class) {
            if (instance == null) instance = new Timber();
        }
        return instance;
    }

    private Timber() {
        super("Timber", "Timber.yml",
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

        Map<String, String> recipeMaterials = new HashMap<>();
        recipeMaterials.put("L", "OAK_WOOD");
        recipeMaterials.put("E", "EMERALD");

        config.addDefault(key + ".Recipe.Materials", recipeMaterials);

        ConfigurationManager.saveConfig(config);
        ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

        init(config.getString(key + ".name"),
                "[" + config.getString(key + ".name_modifier") + "] \u200B" + config.getString(key + ".description"),
                ChatWriter.getColor(config.getString(key + ".Color")),
                1,
                modManager.createModifierItem(Material.getMaterial(config.getString(key + ".modifier_item")), ChatWriter.getColor(config.getString(key + ".Color")) + config.getString(key + ".name_modifier"), ChatWriter.addColors(config.getString(key + ".description_modifier")), this));
    }

    @Override
    public boolean applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (modManager.hasMod(tool, Power.instance())) {
            pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
            return false;
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

        if (!allowed.contains(b.getType())) return;

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

        for (int dy = b.getY() + 1, airgap = 0; dy < 256 && airgap < 6; dy++) {
            if (!allowed.contains(p.getWorld().getBlockAt(b.getX(), dy, b.getZ()).getType())) {
                Location loc = b.getLocation().clone();
                loc.setY(dy);

                Material mat = p.getWorld().getBlockAt(loc).getType();
                if (Lists.getWoodLeaves().contains(mat)) {
                    isTreeTop = true;
                } else if (mat.equals(Material.AIR) || mat.equals(Material.CAVE_AIR)) {
                    airgap++;
                    continue;
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

    private void breakTree(Player p, Block b, ArrayList<Material> allowed) { //TODO: Improve algorythm and performance -> async?
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;
                    Location loc = b.getLocation().clone();
                    loc.add(dx, dy, dz);

                    if (locs.contains(loc)) { continue; }

                    if (getConfig().getInt("Timber.MaximumBlocksPerSwing") > 0 && locs.size() >= getConfig().getInt("Timber.MaximumBlocksPerSwing")) return;

                    locs.add(loc);

                    Block toBreak = p.getWorld().getBlockAt(loc);
                    if (allowed.contains(toBreak.getType())) {
                        breakTree(p, toBreak, allowed);
                        NBTUtils.getHandler().playerBreakBlock(p, toBreak);
                    }
                }
            }
        }
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Timber", "Modifier_Timber");
    }

    @Override
    public boolean isAllowed() {
    	return getConfig().getBoolean("Timber.allowed");
    }
}

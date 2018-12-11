package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Data.ModifierFailCause;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.ModifierFailEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Craftable;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import net.minecraft.server.v1_13_R2.BlockPosition;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.Collections;

public class Timber extends Modifier implements Craftable {

    private static final ModManager modManager = ModManager.instance();
    private static final PluginManager pluginManager = Bukkit.getPluginManager();
    private static final FileConfiguration config = Main.getMain().getConfigurations().getConfig("Timber.yml");

    private static final ArrayList<Location> locs = new ArrayList<>();

    public Timber() {
        super(config.getString("Timber.name"),
                "[" + config.getString("Timber.name_modifier") + "] " + config.getString("Timber.description"),
                ModifierType.TIMBER,
                ChatColor.GREEN,
                1,
                ItemGenerator.itemEnchanter(Material.EMERALD, ChatColor.GREEN + config.getString("Timber.name_modifier"), 1, Enchantment.DIG_SPEED, 1),
                new ArrayList<>(Collections.singletonList(ToolType.AXE)),
                Main.getPlugin());
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
            if (p.getWorld().getBlockAt(b.getX(), y, b.getZ()).getType().equals(Material.GRASS_BLOCK) || p.getWorld().getBlockAt(b.getX(), y, b.getZ()).getType().equals(Material.DIRT)) {
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
        _registerCraftingRecipe(config, this, "Timber", "Modifier_Timber");
    }
}

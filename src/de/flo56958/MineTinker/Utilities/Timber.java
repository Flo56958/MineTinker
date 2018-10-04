package de.flo56958.MineTinker.Utilities;

import de.flo56958.MineTinker.Data.PlayerData;
import net.minecraft.server.v1_13_R2.BlockPosition;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Timber {

    static ArrayList<Location> locs = new ArrayList<>();

    public static boolean init(Player p, Block b) {

        if (!p.hasPermission("minetinker.modifiers.timber.use")) { return false; }

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
            if (!p.getWorld().getBlockAt(b.getX(), dy, b.getZ()).getType().equals(b.getType())) {
                Location loc = b.getLocation().clone();
                loc.setY(dy);
                if (p.getWorld().getBlockAt(loc).getType().equals(Material.ACACIA_LEAVES) ||
                        p.getWorld().getBlockAt(loc).getType().equals(Material.BIRCH_LEAVES) ||
                        p.getWorld().getBlockAt(loc).getType().equals(Material.DARK_OAK_LEAVES) ||
                        p.getWorld().getBlockAt(loc).getType().equals(Material.JUNGLE_LEAVES) ||
                        p.getWorld().getBlockAt(loc).getType().equals(Material.OAK_LEAVES) ||
                        p.getWorld().getBlockAt(loc).getType().equals(Material.SPRUCE_LEAVES)) {
                    isTreeTop = true;
                }
                break;
            }
        }

        if (!isTreeBottom || !isTreeTop) { return false; }

        PlayerData.HASPOWER.replace(p, true);

        locs.add(b.getLocation());

        breakTree(p, b);

        locs.clear();

        return true;
    }

    private static void breakTree(Player p, Block b) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    Location loc = b.getLocation().clone();
                    loc.add(dx, dy, dz);
                    if (!locs.contains(loc)) {
                        locs.add(loc);
                        if (p.getWorld().getBlockAt(loc).getType().equals(b.getType())) {
                            breakTree(p, p.getWorld().getBlockAt(loc));
                            ((CraftPlayer) p).getHandle().playerInteractManager.breakBlock(new BlockPosition(loc.getX(), loc.getY(), loc.getZ()));
                        }
                    }
                }
            }
        }
    }
}

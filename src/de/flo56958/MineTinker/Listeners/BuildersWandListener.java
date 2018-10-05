package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Data.Strings;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Utilities.PlayerInfo;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class BuildersWandListener implements Listener {

    @EventHandler
    public void onBlockBreak (BlockBreakEvent e) {
        if (e.isCancelled()) { return; }
        if (!Lists.WORLDS_BUILDERSWANDS.contains(e.getPlayer().getWorld().getName())) { return; }

        ItemStack wand = e.getPlayer().getInventory().getItemInMainHand();

        if (!wand.hasItemMeta()) { return; }
        if (!wand.getItemMeta().hasLore()) { return; }
        if (!wand.getItemMeta().getLore().contains(Strings.IDENTIFIER_BUILDERSWAND)) { return; }

        e.setCancelled(true);
    }

    @EventHandler
    public void onClick (PlayerInteractEvent e) {
        if (e.isCancelled()) { return; }
        if (!Lists.WORLDS_BUILDERSWANDS.contains(e.getPlayer().getWorld().getName())) { return; }

        ItemStack wand = e.getPlayer().getInventory().getItemInMainHand();
        if (!wand.hasItemMeta()) { return; }

        if (!wand.getItemMeta().hasLore()) { return; }
        if (!wand.getItemMeta().getLore().contains(Strings.IDENTIFIER_BUILDERSWAND)) { return; }

        e.setCancelled(true);

        if (!e.getPlayer().hasPermission("minetinker.builderswands.use")) { return; }
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            int _u = 0;
            int _w = 0;
            switch (wand.getType()) {
                case STONE_SHOVEL:
                    _w = 1;
                    break;
                case IRON_SHOVEL:
                    _u = 1;
                    _w = 1;
                    break;
                case GOLDEN_SHOVEL:
                    _u = 1;
                    _w = 2;
                    break;
                case DIAMOND_SHOVEL:
                    _u = 2;
                    _w = 2;
                    break;
            }
            Player p = e.getPlayer();
            if (p.isSneaking()) {
                _w = 0;
                _u = 0;
            }
            Block b = e.getClickedBlock();
            BlockFace bf = e.getBlockFace();
            ItemStack[] inv = p.getInventory().getContents();
            Vector u = new Vector(0, 0, 0);
            Vector v = new Vector(0, 0, 0);
            Vector w = new Vector(0, 0, 0);
            if (bf.equals(BlockFace.UP)) {
                v = new Vector(0, 1, 0);
                switch (PlayerInfo.getFacingDirection(p)) {
                    case "N":
                        w = new Vector(-1, 0, 0);
                        break;
                    case "E":
                        w = new Vector(0, 0, -1);
                        break;
                    case "S":
                        w = new Vector(1, 0, 0);
                        break;
                    case "W":
                        w = new Vector(0, 0, 1);
                        break;
                }
                u = v.getCrossProduct(w);
            } else if (bf.equals(BlockFace.DOWN)) {
                v = new Vector(0, -1, 0);
                switch (PlayerInfo.getFacingDirection(p)) {
                    case "N":
                        w = new Vector(-1, 0, 0);
                        break;
                    case "E":
                        w = new Vector(0, 0, -1);
                        break;
                    case "S":
                        w = new Vector(1, 0, 0);
                        break;
                    case "W":
                        w = new Vector(0, 0, 1);
                        break;
                }
                u = v.getCrossProduct(w);
            } else if (bf.equals(BlockFace.NORTH)) {
                v = new Vector(0, 0, -1);
                w = new Vector(-1, 0, 0);
                u = new Vector(0, -1, 0);
            } else if (bf.equals(BlockFace.EAST)) {
                v = new Vector(1, 0, 0);
                w = new Vector(0, 0, -1);
                u = new Vector(0, 1, 0);
            } else if (bf.equals(BlockFace.SOUTH)) {
                v = new Vector(0, 0, 1);
                w = new Vector(1, 0, 0);
                u = new Vector(0, 1, 0);
            } else if (bf.equals(BlockFace.WEST)) {
                v = new Vector(-1, 0, 0);
                w = new Vector(0, 0, 1);
                u = new Vector(0, -1, 0);
            }
            for (ItemStack current : inv) {
                if (current != null) {
                    if (current.getType().equals(b.getType())) {
                        if (!current.hasItemMeta()) {
                            loop:
                            for (int i = -_w; i <= _w; i++) {
                                for (int j = -_u; j <= _u; j++) {
                                    Location l = b.getLocation().clone();
                                    l.subtract(w.clone().multiply(i));
                                    l.subtract(u.clone().multiply(j));
                                    Location loc = l.clone().subtract(v.clone().multiply(-1));
                                    if (b.getWorld().getBlockAt(l).getType().equals(b.getType())) {
                                        if (b.getWorld().getBlockAt(loc).getType().equals(Material.AIR) ||
                                                b.getWorld().getBlockAt(loc).getType().equals(Material.CAVE_AIR) ||
                                                b.getWorld().getBlockAt(loc).getType().equals(Material.WATER) ||
                                                b.getWorld().getBlockAt(loc).getType().equals(Material.BUBBLE_COLUMN) ||
                                                b.getWorld().getBlockAt(loc).getType().equals(Material.LAVA) ||
                                                b.getWorld().getBlockAt(loc).getType().equals(Material.GRASS)) {
                                            if (wand.getType().getMaxDurability() - wand.getDurability() <= 1) {
                                                break loop;
                                            }
                                            b.getWorld().getBlockAt(loc).setType(current.getType());
                                            current.setAmount(current.getAmount() - 1);
                                            if (Main.getPlugin().getConfig().getBoolean("Builderswands.useDurability")) {
                                                wand.setDurability((short) (wand.getDurability() + 1));
                                            }
                                            if (current.getAmount() == 0) {
                                                break loop;
                                            }
                                        }
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
    }
}

package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Data.Lists;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class ElevatorListener implements Listener {

    @EventHandler
    public void onSneak (PlayerToggleSneakEvent e) {
        if (e.isCancelled()) { return; }

        if (!Lists.WORLDS_ELEVATOR.contains(e.getPlayer().getWorld().getName())) { return; }

        if (!e.isSneaking()) { return; }

        Player p = e.getPlayer();
        if (!p.hasPermission("minetinker.elevator.use")) { return; }

        Location l = p.getLocation();

        Block b = p.getWorld().getBlockAt(l.add(0, -2, 0));
        if (!b.getType().equals(Material.HOPPER)) { return; }

        Hopper h1 = (Hopper) b.getState();
        if (!h1.getCustomName().equals(ChatColor.GRAY + "Elevator-Motor")) { return; }

        for (int i = l.getBlockY() - 1; i >= 0; i--) {
            if (p.getWorld().getBlockAt(l.getBlockX(), i, l.getBlockZ()).getType().equals(Material.HOPPER)) {
                Hopper h2 = (Hopper) p.getWorld().getBlockAt(l.getBlockX(), i, l.getBlockZ()).getState();
                if (h2.getCustomName().equals(ChatColor.GRAY + "Elevator-Motor")) {
                    l.add(0, i - l.getBlockY() + 2, 0);
                    p.teleport(l);
                    if (Main.getPlugin().getConfig().getBoolean("Elevator.Sound")) {
                        p.playSound(p.getLocation(), Sound.BLOCK_CHEST_CLOSE, 0.5F, 0.5F);
                    }
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onJump (PlayerMoveEvent e) {
        if (e.isCancelled()) { return; }
        if (!Lists.WORLDS_ELEVATOR.contains(e.getPlayer().getWorld().getName())) { return; }

        Player p = e.getPlayer();
        if (!p.hasPermission("minetinker.elevator.use")) { return; }

        Location l = p.getLocation();

        if (!(e.getTo().getY() > e.getFrom().getY() && e.getTo().getX() == e.getFrom().getX() && e.getTo().getZ() == e.getFrom().getZ())) { return; }

        Block b = p.getWorld().getBlockAt(l.add(0, -2, 0));
        if (!b.getType().equals(Material.HOPPER)) { return; }

        Hopper h1 = (Hopper) b.getState();
        if (h1.getCustomName().equals(ChatColor.GRAY + "Elevator-Motor")) {
            for (int i = l.getBlockY() + 1; i <= 256; i++) {
                if (p.getWorld().getBlockAt(l.getBlockX(), i, l.getBlockZ()).getType().equals(Material.HOPPER)) {
                    Hopper h2 = (Hopper) p.getWorld().getBlockAt(l.getBlockX(), i, l.getBlockZ()).getState();
                    if (h2.getCustomName().equals(ChatColor.GRAY + "Elevator-Motor")) {
                        l.add(0, i - l.getBlockY() + 2, 0);
                        p.teleport(l);
                        if (Main.getPlugin().getConfig().getBoolean("Elevator.Sound")) {
                            p.playSound(p.getLocation(), Sound.BLOCK_CHEST_OPEN, 0.5F, 0.5F);
                        }
                        break;
                    }
                }
            }
        }
    }
}

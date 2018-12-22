package de.flo56958.MineTinker.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Hopper;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;

public class ElevatorListener implements Listener {

    private static final FileConfiguration config = ConfigurationManager.getConfig("Elevator.yml");

    @EventHandler
    public void onSneak (PlayerToggleSneakEvent e) {
        if (e.isCancelled()) { return; }

        if (Lists.WORLDS_ELEVATOR.contains(e.getPlayer().getWorld().getName())) { return; }

        if (!e.isSneaking()) { return; }

        Player p = e.getPlayer();
        if (!p.hasPermission("minetinker.elevator.use")) { return; }

        Location l = p.getLocation();

        Block b = p.getWorld().getBlockAt(l.add(0, -2, 0));
        if (!(b.getState() instanceof Hopper)) { return; }

        Hopper h1 = (Hopper) b.getState();
        if (h1.getCustomName() == null) { return; } //name could be NULL
        if (!h1.getCustomName().equals(ChatColor.GRAY + config.getString("Elevator.name"))) { return; }

        for (int i = l.getBlockY() - 1; i >= 0; i--) {
            if (p.getWorld().getBlockAt(l.getBlockX(), i, l.getBlockZ()).getState() instanceof Hopper) {
                Hopper h2 = (Hopper) p.getWorld().getBlockAt(l.getBlockX(), i, l.getBlockZ()).getState();
                if (h2.getCustomName() == null) { continue; } //name could be NULL
                if (h2.getCustomName().equals(ChatColor.GRAY + config.getString("Elevator.name"))) {
                    l.add(0, i - l.getBlockY() + 2, 0);
                    p.teleport(l);
                    if (config.getBoolean("Elevator.Sound")) {
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
        if (Lists.WORLDS_ELEVATOR.contains(e.getPlayer().getWorld().getName())) { return; }

        Player p = e.getPlayer();
        if (!p.hasPermission("minetinker.elevator.use")) { return; }

        Location l = p.getLocation();

        if (!(e.getTo().getY() > e.getFrom().getY() && e.getTo().getX() == e.getFrom().getX() && e.getTo().getZ() == e.getFrom().getZ())) { return; }

        Block b = p.getWorld().getBlockAt(l.add(0, -2, 0));
        if (!(b.getState() instanceof Hopper)) { return; }

        Hopper h1 = (Hopper) b.getState();
        if (h1.getCustomName() == null) { return; }
        if (h1.getCustomName().equals(ChatColor.GRAY + config.getString("Elevator.name"))) {
            for (int i = l.getBlockY() + 1; i <= 256; i++) {
                if (p.getWorld().getBlockAt(l.getBlockX(), i, l.getBlockZ()).getState() instanceof Hopper) {
                    Hopper h2 = (Hopper) p.getWorld().getBlockAt(l.getBlockX(), i, l.getBlockZ()).getState();
                    if (h2.getCustomName() == null) { continue; }
                    if (h2.getCustomName().equals(ChatColor.GRAY + config.getString("Elevator.name"))) {
                        l.add(0, i - l.getBlockY() + 2, 0);
                        p.teleport(l);
                        if (config.getBoolean("Elevator.Sound")) {
                            p.playSound(p.getLocation(), Sound.BLOCK_CHEST_OPEN, 0.5F, 0.5F);
                        }
                        break;
                    }
                }
            }
        }
    }
}

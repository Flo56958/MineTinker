package de.flo56958.minetinker.api.serverhandler;

import de.flo56958.minetinker.MineTinker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class BukkitServerHandler extends ServerHandler {
    @Override
    public void runTaskLater(Runnable runnable, long delay) {
        Bukkit.getScheduler().runTaskLater(MineTinker.getPlugin(), runnable, delay);
    }

    @Override
    public void runTaskLaterAsynchronously(Runnable runnable, long delay) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(MineTinker.getPlugin(), runnable, delay);
    }

    @Override
    public void cancelTask(int taskId) {
        Bukkit.getScheduler().cancelTask(taskId);
    }

    @Override
    public int scheduleSyncRepeatingTask(Runnable runnable, long delay, long period) {
        return Bukkit.getScheduler().scheduleSyncRepeatingTask(MineTinker.getPlugin(), runnable, delay, period);
    }

    @Override
    public void runTask(Runnable runnable) {
        Bukkit.getScheduler().runTask(MineTinker.getPlugin(), runnable);
    }

    @Override
    public void runTaskAsynchronously(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(MineTinker.getPlugin(), runnable);
    }

    @Override
    public void teleportEntity(Entity entity, Location location) {
        entity.teleport(location);
    }
}

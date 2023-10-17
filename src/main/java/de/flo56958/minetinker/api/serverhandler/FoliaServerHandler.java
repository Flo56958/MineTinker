package de.flo56958.minetinker.api.serverhandler;

import com.comphenix.protocol.scheduler.FoliaScheduler;
import de.flo56958.minetinker.MineTinker;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class FoliaServerHandler extends ServerHandler {

    private FoliaScheduler scheduler;

    public FoliaServerHandler() {
        this.scheduler = new FoliaScheduler(MineTinker.getPlugin());
    }

    @Override
    public void runTaskLater(Runnable runnable, long delay) {
        scheduler.scheduleSyncDelayedTask(runnable, delay);
    }

    @Override
    public void runTaskLaterAsynchronously(Runnable runnable, long delay) {
        scheduler.scheduleSyncDelayedTask(runnable, Math.max(1, delay));
    }

    @Override
    public void cancelTask(int taskId) {

    }

    @Override
    public int scheduleSyncRepeatingTask(Runnable runnable, long delay, long period) {
        scheduler.scheduleSyncRepeatingTask(runnable, Math.max(1, delay), Math.max(1, period));
        return 0;
    }

    @Override
    public void runTask(Runnable runnable) {
        scheduler.runTask(runnable);
    }

    @Override
    public void runTaskAsynchronously(Runnable runnable) {
        scheduler.runTask(runnable);
    }

    @Override
    public void teleportEntity(Entity entity, Location location) {
        entity.teleportAsync(location);
    }
}

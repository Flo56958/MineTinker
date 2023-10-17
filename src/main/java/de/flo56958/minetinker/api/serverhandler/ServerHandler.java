package de.flo56958.minetinker.api.serverhandler;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public abstract class ServerHandler {

    private static ServerHandler handler;

    public abstract void runTaskLater(Runnable runnable, long delay);

    public abstract void runTaskLaterAsynchronously(Runnable runnable, long delay);

    public abstract void cancelTask(int taskId);

    public abstract int scheduleSyncRepeatingTask(Runnable runnable, long delay, long period);

    public abstract void runTask(Runnable runnable);

    public abstract void runTaskAsynchronously(Runnable runnable);

    public abstract void teleportEntity(Entity entity, Location location);

    public static void init() {
        System.out.println(Bukkit.getServer().getVersion());
        if (Bukkit.getServer().getVersion().contains("Folia"))
            handler = new FoliaServerHandler();
        else
            handler = new BukkitServerHandler();
    }

    public static ServerHandler getServerHandler() {
        return handler;
    }
}

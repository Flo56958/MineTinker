package de.flo56958.minetinker.listeners;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;


public class ActionBarListener implements Listener {
	private static final HashMap<UUID, AtomicInteger> xpbuffer = new HashMap<>();
	private static final Runnable run = new Runnable() {
		@Override
		public void run() {
			Bukkit.getOnlinePlayers().forEach(p -> {
				final int xpamount = xpbuffer.get(p.getUniqueId()).getAndSet(0);
				if (xpamount > 0)
					ChatWriter.sendActionBar(p, LanguageManager.getString("ActionBar.ExpGain", p)
						.replaceAll("%amount", "+" + xpamount));
			});
			Bukkit.getScheduler().runTaskLater(MineTinker.getPlugin(), this, 20);
		}
	};


	public ActionBarListener() {
		Bukkit.getOnlinePlayers().forEach(p -> xpbuffer.put(p.getUniqueId(), new AtomicInteger(0)));
		run.run();
	}

	public static void addXP(@NotNull Player p, int amount) {
		final AtomicInteger i = xpbuffer.get(p.getUniqueId());
		if (i != null)
			i.addAndGet(amount);
	}


	@EventHandler
	private void onJoin(@NotNull final PlayerJoinEvent e) {
		xpbuffer.put(e.getPlayer().getUniqueId(), new AtomicInteger(0));
	}


	@EventHandler
	private void onQuit(@NotNull final PlayerQuitEvent e) {
		xpbuffer.remove(e.getPlayer().getUniqueId());
	}
}
package de.flo56958.minetinker.listeners;

import de.flo56958.minetinker.Main;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;


public class ActionBarListener implements Listener {
	private static final HashMap<UUID, AtomicInteger> xpbuffer = new HashMap<>();
	private static final Runnable run = new Runnable() {
		@Override
		public void run() {
			for (Player p : Bukkit.getOnlinePlayers()) {
				int xpamount = xpbuffer.get(p.getUniqueId()).getAndSet(0);
				if (xpamount != 0) {
					String a = String.valueOf(xpamount);
					if (xpamount > 0) a = "+" + a;
					else continue;
					ChatWriter.sendActionBar(p, LanguageManager.getString("ActionBar.ExpGain", p)
							.replaceAll("%amount", a));
				}
			}
			Bukkit.getScheduler().runTaskLater(Main.getPlugin(), this, 20);
		}
	};


	public ActionBarListener() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			xpbuffer.put(p.getUniqueId(), new AtomicInteger(0));
		}
		run.run();
	}

	public static void addXP(Player p, int amount) {
		if (p == null) return;
		AtomicInteger i = xpbuffer.get(p.getUniqueId());
		if (i != null) {
			i.addAndGet(amount);
		}
	}


	@EventHandler
	private void onJoin(PlayerJoinEvent e) {
		xpbuffer.put(e.getPlayer().getUniqueId(), new AtomicInteger(0));
	}


	@EventHandler
	private void onQuit(PlayerQuitEvent e) {
		xpbuffer.remove(e.getPlayer().getUniqueId());
	}
}
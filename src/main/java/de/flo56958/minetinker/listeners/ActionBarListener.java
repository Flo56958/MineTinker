package de.flo56958.minetinker.listeners;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.LanguageManager;
import de.flo56958.minetinker.utils.playerconfig.GeneralPCOptions;
import de.flo56958.minetinker.utils.playerconfig.PlayerConfigurationManager;
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

	public ActionBarListener() {
		Bukkit.getOnlinePlayers().forEach(p -> xpbuffer.put(p.getUniqueId(), new AtomicInteger(0)));
		Bukkit.getScheduler().runTaskTimerAsynchronously(MineTinker.getPlugin(), () -> Bukkit.getOnlinePlayers().forEach(p -> {
			final int xpamount = xpbuffer.get(p.getUniqueId()).getAndSet(0);
			if (xpamount > 0 && PlayerConfigurationManager.getInstance().getBoolean(p, GeneralPCOptions.INSTANCE.ACTIONBAR_ON_EXP_GAIN))
				ChatWriter.sendActionBar(p, LanguageManager.getString("ActionBar.ExpGain", p)
						.replaceAll("%amount", "+" + xpamount));
		}), 20, 20);
	}

	public static void addEXP(@NotNull Player p, int amount) {
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
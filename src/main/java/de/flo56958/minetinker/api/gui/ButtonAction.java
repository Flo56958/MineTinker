package de.flo56958.minetinker.api.gui;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;

interface PlayerAction {
	void run(Player player);
}

public abstract class ButtonAction {

	static {
		Bukkit.getPluginManager().registerEvents(new ChatListener(), MineTinker.getPlugin());
	}

	protected GUI.Window.Button button;

	protected ButtonAction(GUI.Window.Button button) {
		this.button = button;
	}

	public void run() {}

	public static class PAGE_UP extends ButtonAction implements PlayerAction {

		public PAGE_UP(@NotNull GUI.Window.Button button) {
			super(button);
		}

		public void run(Player player) {
			final GUI gui = this.button.getWindow().getGUI();
			final int pageNo = gui.getWindowNumber(this.button.getWindow()) + 1;
			gui.show(player, pageNo % gui.getWindowAmount());
		}
	}

	public static class PAGE_DOWN extends ButtonAction implements PlayerAction {

		public PAGE_DOWN(@NotNull GUI.Window.Button button) {
			super(button);
		}

		public void run(Player player) {
			final GUI gui = this.button.getWindow().getGUI();
			int pageNo = gui.getWindowNumber(this.button.getWindow()) - 1;

			if (pageNo == -1)
				pageNo = gui.getWindowAmount() - 1;

			gui.show(player, pageNo);
		}
	}

	public static class PAGE_GOTO extends ButtonAction implements PlayerAction {

		private final int page;
		protected final GUI.Window window;

		public PAGE_GOTO(@NotNull final GUI.Window.Button button, int page) {
			super(button);
			this.page = page;
			this.window = null;
		}

		public PAGE_GOTO(@NotNull final GUI.Window.Button button, @NotNull final GUI.Window window) {
			super(button);
			this.window = window;
			this.page = -1;
		}

		public void run(@NotNull final Player player) {
			final GUI gui = (window != null) ? window.getGUI() : button.getWindow().getGUI();
			final int pageNo = (window != null) ? gui.getWindowNumber(window) : page;
			gui.show(player, pageNo);
		}
	}

	public static class RUN_RUNNABLE extends ButtonAction {
		private final Runnable runnable;

		public RUN_RUNNABLE(final GUI.Window.Button button, final Runnable runnable) {
			super(button);
			this.runnable = runnable;
		}

		@Override
		public void run() {
			runnable.run();
		}
	}

	public static class RUN_RUNNABLE_ON_PLAYER extends ButtonAction implements PlayerAction {
		private final PlayerRunnable runnable;

		public RUN_RUNNABLE_ON_PLAYER(final GUI.Window.Button button, final PlayerRunnable runnable) {
			super(button);
			this.runnable = runnable;
		}

		@Override
		public void run(Player player) {
			runnable.run(player, "");
		}
	}

	public static class REQUEST_INPUT extends ButtonAction implements PlayerAction {

		private static final ConcurrentHashMap<Player, REQUEST_INPUT> playerToAction = new ConcurrentHashMap<>();

		private final PlayerRunnable runnable;
		private final String data;

		public REQUEST_INPUT(final GUI.Window.Button button, final PlayerRunnable runnable, final String data) {
			super(button);
			this.runnable = runnable;
			this.data = data;
		}

		@Override
		public void run(@NotNull final Player player) {
			playerToAction.put(player, this);
			player.closeInventory();
			ChatWriter.sendMessage(player, ChatColor.RED, LanguageManager.getString("GUI.ButtonAction.REQUEST_INPUT")
					.replace("%data", data + ChatColor.RESET + ChatColor.RED));
		}

		private void afterRun(@NotNull final Player player) {
			Bukkit.getScheduler().runTaskLater(this.button.getWindow().getGUI().getPlugin(),
					() -> button.getWindow().getGUI().show(player, button.getWindow()), 10);
		}
	}

	public interface PlayerRunnable {
		void run(Player player, String input);
	}

	private static class ChatListener implements Listener {

		@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
		public static void onChat(@NotNull final AsyncPlayerChatEvent event) {
			final REQUEST_INPUT ri = REQUEST_INPUT.playerToAction.remove(event.getPlayer());
			if (ri == null) return;
			event.setCancelled(true);

			ri.runnable.run(event.getPlayer(), event.getMessage());
			ri.afterRun(event.getPlayer());
		}

	}
}
package de.flo56958.MineTinker.api.gui;

import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;

public abstract class ButtonAction {

    static {
        Bukkit.getPluginManager().registerEvents(new ChatListener(), Main.getPlugin());
    }

    protected GUI.Window.Button button;

    protected ButtonAction(GUI.Window.Button button) {
        this.button = button;
    }

    public void run() {}

    public static class NOTHING extends ButtonAction {

        private static NOTHING instance;

        private NOTHING() {
            super(null);
        }

        public static NOTHING instance() {
            synchronized (NOTHING.class) {
                if (instance == null) instance = new NOTHING();
            }
            return instance;
        }
    }

    public static class PAGE_UP extends ButtonAction implements PlayerAction {

        public PAGE_UP(@NotNull GUI.Window.Button button) {
            super(button);
        }

        @Override
        public void run() {}

        public void run(Player player) {
            GUI gui = this.button.getWindow().getGUI();
            int pageNo = gui.getWindowNumber(this.button.getWindow());
            gui.show(player, ++pageNo % gui.getWindowAmount());
        }
    }

    public static class PAGE_DOWN extends ButtonAction implements PlayerAction {

        public PAGE_DOWN(@NotNull GUI.Window.Button button) {
            super(button);
        }

        public void run(Player player) {
            GUI gui = this.button.getWindow().getGUI();
            int pageNo = gui.getWindowNumber(this.button.getWindow());
            gui.show(player, Math.abs(--pageNo) % gui.getWindowAmount());
        }
    }

    public static class PAGE_GOTO extends ButtonAction implements PlayerAction {

        private final int page;
        private final GUI.Window window;

        public PAGE_GOTO(@NotNull GUI.Window.Button button, int page) {
            super(button);
            this.page = page;
            this.window = null;
        }

        public PAGE_GOTO(@NotNull GUI.Window.Button button, @NotNull GUI.Window window) {
            super(button);
            this.window = window;
            this.page = -1;
        }

        public void run(Player player) {
            GUI gui = (window != null) ? window.getGUI() : button.getWindow().getGUI();
            int pageNo = (window != null) ? gui.getWindowNumber(window) : page;
            gui.show(player, pageNo);
        }
    }

    public static class RUN_RUNNABLE extends ButtonAction {
        private final Runnable runnable;

        public RUN_RUNNABLE(GUI.Window.Button button, Runnable runnable) {
            super(button);
            this.runnable = runnable;
        }

        @Override
        public void run() {
            runnable.run();
        }
    }

    public static class REQUEST_INPUT extends ButtonAction implements PlayerAction {

        private static ConcurrentHashMap<Player, REQUEST_INPUT> playerToAction = new ConcurrentHashMap<>();

        private PlayerRunnable runnable;
        private String data;

        public REQUEST_INPUT(GUI.Window.Button button, PlayerRunnable runnable, String data) {
            super(button);
            this.runnable = runnable;
            this.data = data;
        }

        @Override
        public void run(Player p) {
            playerToAction.put(p, this);
            p.closeInventory();
            ChatWriter.sendMessage(p, ChatColor.RED, LanguageManager.getString("GUI.ButtonAction.REQUEST_INPUT").replace("%data", data + ChatColor.RESET + "" + ChatColor.RED));
        }

        private void afterRun(Player p) {
            Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> button.getWindow().getGUI().show(p, button.getWindow()), 10);
        }

        public abstract static class PlayerRunnable {
            public abstract void run(Player player, String input);
        }
    }

    private static class ChatListener implements Listener {

        @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
        public static void onChat(AsyncPlayerChatEvent e) {
            REQUEST_INPUT ri = REQUEST_INPUT.playerToAction.remove(e.getPlayer());
            if (ri == null) return;
            e.setCancelled(true);

            ri.runnable.run(e.getPlayer(), e.getMessage());
            ri.afterRun(e.getPlayer());
        }

    }
}

interface PlayerAction {
    void run(Player p);
}
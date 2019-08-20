package de.flo56958.MineTinker.api.gui;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class ButtonAction {

    protected GUI.Window.Button button;

    protected ButtonAction(GUI.Window.Button button) {
        this.button = button;
    }

    public abstract void run();

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

        @Override
        public void run() {}
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

        @Override
        public void run() {}

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

        @Override
        public void run() {}

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
}

interface PlayerAction {
    void run(Player p);
}
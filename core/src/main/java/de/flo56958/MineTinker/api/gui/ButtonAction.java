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
}

interface PlayerAction {
    void run(Player p);
}
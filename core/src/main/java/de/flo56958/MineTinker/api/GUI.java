package de.flo56958.MineTinker.api;

import de.flo56958.MineTinker.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is for all User-Interfaces and Items within them.
 */
public class GUI implements Listener {

    private List<Window> windows = Collections.synchronizedList(new ArrayList<>());

    private volatile boolean isClosed = true;

    public GUI() {
        open();
    }

    /**
     * Adds a window to the GUI
     * @param window The window that is inserted into the GUI
     */
    public boolean addWindow(@NotNull final Window window) {
        return windows.add(window);
    }

    /**
     * Adds a window to the GUI
     * @param window The window that is inserted into the GUI
     * @param isStart Should the windows be the start of the GUI when opened
     * @throws IllegalStateException when GUI is already closed
     */
    public void addWindow(@NotNull final Window window, final boolean isStart) {
        if (isClosed) throw new IllegalStateException("GUI (" + this.hashCode() + ") is already closed!");
        if (isStart)
            windows.add(0, window);
        else
            addWindow(window);
    }

    public boolean removeWindow(@NotNull final Window window) {
        return windows.remove(window);
    }

    /**
     * Returns if possible the associated Window-object in the GUI-Framework
     * @param inv the Inventory to find the Window for
     * @return the found window or null
     */
    @Nullable
    public Window getWindowFromInventory(final Inventory inv) {
        if (inv == null) return null;
        for (Window w : windows) {
            if (w.getInventory().equals(inv)) return w;
        }
        return null;
    }

    /**
     * shows the first page of the GUI to the specified Player
     * @param p the Player
     */
    public void show(@NotNull final Player p) {
        show(p, 0);
    }

    /**
     * shows the [page] page of the GUI to the specified Player
     * @param p the Player
     * @param page the Page shown to the Player
     */
    public void show(@NotNull final Player p, final int page) {
        synchronized (this) {
            if (isClosed) throw new IllegalStateException("GUI (" + this.hashCode() + ") is closed.");
            p.openInventory(windows.get(page).getInventory());
        }
    }

    public void open() {
        synchronized (this) {
            if (!isClosed) return;

            Bukkit.getPluginManager().registerEvents(this, Main.getPlugin());
            isClosed = false;
        }
    }

    /**
     * this will close the Listener section of the GUI
     */
    public void close() {
        synchronized (this) {
            if (isClosed) return;

            HandlerList.unregisterAll(this);
            isClosed = true;
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onClick(InventoryClickEvent e) {
        Window w = getWindowFromInventory(e.getClickedInventory());
        if (w == null) return;

        e.setCancelled(true);
        //TODO: Trigger Buttons
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDrag(InventoryDragEvent e) {
        Window w = getWindowFromInventory(e.getInventory());
        if (w == null) return;

        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onMove(InventoryMoveItemEvent e) {
        Window w = getWindowFromInventory(e.getDestination());
        if (w == null) return;

        w = getWindowFromInventory(e.getInitiator());
        if (w == null) return;

        w = getWindowFromInventory(e.getSource());
        if (w == null) return;

        e.setCancelled(true);
    }

    /**
     * A wrapper class for the Minecraft Inventory Window
     */
    public static class Window {

        private Inventory inventory;
        private Button[][] buttonMap;

        public Window(@NotNull final InventoryType type, @NotNull final String title) {
            inventory = Bukkit.createInventory(null, type, title);
        }

        public Window(final int size, @NotNull final String title) {
            this(Bukkit.createInventory(null, size, title));
        }

        public Window(@NotNull final Inventory inventory) {
            this.inventory = inventory;
            this.buttonMap = new Button[9][inventory.getSize() / 9];
        }

        public void addButton(final int x, final int y, final ItemStack item) {
            Button b = new Button(item);
            buttonMap[x][y] = b;
            inventory.setItem(getSlot(x, y), b.item);
        }

        @NotNull
        public Inventory getInventory() {
            return inventory;
        }

        /**
         * Calculates the slot nr. from the coordinates
         * @param x x-Coordinate
         * @param y y-Coordinate
         * @return the slot
         * @throws IllegalArgumentException when Coordinates less than zero
         */
        public static int getSlot(final int x, final int y) {
            if (x < 0 || y < 0) throw new IllegalArgumentException("Coordinates can not be less than zero!");
            int slot = 0;
            for (int i = y; i > 0; i--)
                slot += 9;
            slot += x;
            return slot;
        }

        /**
         * This class is a Button for the Window. The button can be clicked by the User to trigger certain methods.
         */
        private static class Button {
            private final ItemStack item;

            private Runnable leftClick; /**left click action*/
            private Runnable s_leftClick; /**shift left click action*/
            private Runnable rightClick; /**right click action*/
            private Runnable s_rightClick; /**shift right click action*/

            /**
             * creates a Button with no actions
             * @param item the ItemStack that will appear in the Window
             */
            Button(@NotNull ItemStack item) {
                this.item = item;
            }

            //TODO: Implement functions for clicking
        }
    }
}

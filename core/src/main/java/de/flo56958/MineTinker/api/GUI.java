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
 * This class should be thread-safe to use.
 *
 * @author Flo56958
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

    /**
     * Removes a given window from the GUI
     * @param window the window to remove
     * @return  true:  if window was in GUI and was successfully removed
     *          false: if window was not in GUI or was not removed
     */
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
     * @throws IllegalStateException when show() was called as the GUI was closed
     */
    public void show(@NotNull final Player p, final int page) {
        synchronized (this) {
            if (isClosed) throw new IllegalStateException("GUI (" + this.hashCode() + ") is closed.");
            p.openInventory(windows.get(page).getInventory());
        }
    }

    /**
     * This will open the GUI again for further interactions.
     * can be used to micro manage the performance of the GUIs
     */
    public void open() {
        synchronized (this) {
            if (!isClosed) return;

            Bukkit.getPluginManager().registerEvents(this, Main.getPlugin());
            isClosed = false;
        }
    }

    /**
     * this will close the Listener section of the GUI
     * show() will throw Exception when called after close()
     * can be used to micro manage the performance of the GUIs
     */
    public void close() {
        synchronized (this) {
            if (isClosed) return;

            HandlerList.unregisterAll(this);
            isClosed = true;
        }
    }

    //<------------------------------------Events------------------------------------------->

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

    //<------------------------------------Events------------------------------------------->

    /**
     * A wrapper class for the Minecraft Inventory Window
     */
    public static class Window {

        private Inventory inventory;
        private Button[][] buttonMap;

        public Window(@NotNull final InventoryType type, @NotNull final String title) {
            inventory = Bukkit.createInventory(null, type, title);
        }

        /**
         * Creates a new Window with the given size and the given title.
         * @param size  The number of rows in the Inventory. Due to Minecraft-Limitations must be between 1 and 6.
         * @param title The Title of the Window
         * @throws IllegalArgumentException when size is does not match the limitations.
         */
        public Window(int size, @NotNull final String title) {
            if (size <= 0)       throw new IllegalArgumentException("Size of Inventory needs to be at least ONE!");
            else if (size > 6)  throw new IllegalArgumentException("Size of Inventory needs to be at least SIX!");

            size *= 9;
            this.inventory = Bukkit.createInventory(null, size, title);
            this.buttonMap = new Button[9][inventory.getSize() / 9];
        }

        public void addButton(final int x, final int y, @NotNull final ItemStack item) {
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
            if (x < 0 || y < 0) throw new IllegalArgumentException("Coordinates can not be less than ZERO!");
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

            private ButtonAction leftClick    = ButtonAction.NOTHING; //      left  click action
            private ButtonAction s_leftClick  = ButtonAction.NOTHING; //shift left  click action
            private ButtonAction rightClick   = ButtonAction.NOTHING; //      right click action
            private ButtonAction s_rightClick = ButtonAction.NOTHING; //shift right click action

            /**
             * creates a Button with no actions
             * @param item the ItemStack that will appear in the Window
             */
            Button(@NotNull ItemStack item) {
                this.item = item;
            }
        }

        /**
         * Every action a Button can do on a Click
         */
        public enum ButtonAction {
            //TODO: Implement actions
            PAGE_UP,                    //go to next Page (in the ArrayList)
            PAGE_DOWN,                  //go to prior Page (in the ArrayList)
            PAGE_GOTO,                  //go to specific Page (index in ArrayList)

            RUN_TASK,                   //run a Runnable-Task
            RUN_FUTURE,                 //run a Future-Object
            RUN_COMMAND,                //run a server command with permission check
            RUN_COMMAND_WO_PERMCHECK,   //run a server command with out permission check

            NOTHING                     //do nothing on click
        }
    }
}

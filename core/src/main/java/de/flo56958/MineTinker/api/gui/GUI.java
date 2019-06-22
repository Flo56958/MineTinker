package de.flo56958.MineTinker.api.gui;

import de.flo56958.MineTinker.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

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
     * @param size      The size of the window
     * @param title     The title of the window
     * @throws          IllegalStateException when GUI is closed
     */
    public Window addWindow(final int size, @NotNull final String title) {
        if (isClosed) throw new IllegalStateException("GUI (" + this.hashCode() + ") is already closed!");
        Window window = new Window(size, title, this);
        windows.add(window);
        return window;
    }

    /**
     * Removes a given window from the GUI
     * @param window    the window to remove
     * @return          true:  if window was in GUI and was successfully removed
     *                  false: if window was not in GUI or was not removed
     */
    public boolean removeWindow(@NotNull final Window window) {
        return windows.remove(window);
    }

    /**
     * Returns if possible the associated Window-object in the GUI-Framework
     * @param inv       the Inventory to find the Window for
     * @return          the found window or null
     */
    @Nullable
    public Window getWindowFromInventory(final Inventory inv) {
        if (inv == null) return null;
        for (Window w : windows) {
            if (w.inventory.equals(inv)) return w;
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
     * @param p         the Player
     * @param page      the Page shown to the Player
     * @throws          IllegalStateException when show() was called as the GUI was closed
     */
    public void show(@NotNull final Player p, final int page) {
        synchronized (this) {
            if (isClosed) throw new IllegalStateException("GUI (" + this.hashCode() + ") is closed.");
            p.openInventory(windows.get(page).inventory);
        }
    }

    public void show(@NotNull final Player p, final Window window) {
        synchronized (this) {
            if (isClosed) throw new IllegalStateException("GUI (" + this.hashCode() + ") is closed.");
            if (!window.getGUI().equals(this)) throw new IllegalArgumentException("GUI (" + this.hashCode() + ") does not manage Window (" + window.hashCode() + ")!");
            p.openInventory(window.inventory);
        }
    }

    public int getWindowNumber(Window window) {
        return windows.indexOf(window);
    }

    public int getWindowAmount() {
        return windows.size();
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

        Window.Button clickedButton = w.getButtonFromSlot(e.getSlot());
        if (clickedButton != null)
            clickedButton.executeAction(e);
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

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEvent(InventoryInteractEvent e) {
        Window w = getWindowFromInventory(e.getInventory());
        if (w == null) return;

        e.setCancelled(true);
    }

    //<------------------------------------Events------------------------------------------->

    /**
     * A wrapper class for the Minecraft Inventory Window
     */
    public static class Window {

        private final Inventory inventory;
        private final GUI gui;
        private final Button[] buttonMap;

        /**
         * Creates a new Window with the given size and the given title.
         * @param size      The number of rows in the standard Inventory. Due to Minecraft-Limitations must be between 1 and 6.
         * @param title     The Title of the Window
         * @throws          IllegalArgumentException when size is does not match the limitations.
         */
        private Window(int size, @NotNull final String title, @NotNull final GUI gui) {
            if (size <= 0)       throw new IllegalArgumentException("Size of Inventory needs to be at least ONE!");
            else if (size > 6)  throw new IllegalArgumentException("Size of Inventory needs to be at least SIX!");

            size *= 9;
            this.inventory = Bukkit.createInventory(null, size, title);
            this.buttonMap = new Button[54];
            this.gui = gui;
        }

        public Button addButton(final int x, final int y, @NotNull final ItemStack item) {
            return addButton(getSlot(x, y, this), item);
        }

        public Button addButton(final int slot, @NotNull final ItemStack item) {
            Button b = new Button(item, this);
            buttonMap[slot] = b;
            inventory.setItem(slot, b.item);
            return b;
        }

        /**
         *
         * @param x
         * @param y
         * @return  null on failure
         */
        @Nullable
        public Button getButton(final int x, final int y) {
            //TODO: Parameter check
            return buttonMap[getSlot(x, y, this)];
        }

        @Nullable
        public Button getButton(final int slot) {
            //TODO: Parameter check
            return buttonMap[slot];
        }

        @NotNull
        public Inventory getInventory() {
            return inventory;
        }

        @NotNull
        public GUI getGUI() {
            return gui;
        }

        /**
         * Calculates the slot nr. from the coordinates
         * @param x     x-Coordinate
         * @param y     y-Coordinate
         * @return      the slot
         * @throws      IllegalArgumentException when Coordinates less than zero
         */
        public static int getSlot(final int x, final int y, Window window) {
            if (x < 0 || y < 0) throw new IllegalArgumentException("Coordinates can not be less than ZERO!");
            int slot = 0;

            for (int i = y; i > 0; i--)
                slot += 9;
            slot += x;

            if (slot >= window.inventory.getSize()) throw new IllegalArgumentException("Coordinates are to big for the given Inventory!");
            return slot;
        }

        @Nullable
        public Button getButtonFromSlot(final int slot) {
            return buttonMap[slot];
        }

        /**
         * This class is a Button for the Window. The button can be clicked by the User to trigger certain methods.
         */
        public static class Button {
            private final ItemStack item;
            private final Window window;

            private ConcurrentHashMap<ClickType, ButtonAction> actions = new ConcurrentHashMap<>();

            /**
             * creates a Button with no actions
             * @param item the ItemStack that will appear in the Window
             */
            private Button(@NotNull ItemStack item, @NotNull Window window) {
                this.window = window;
                this.item = item;
            }

            public void addAction(@NotNull ClickType c_action, @NotNull ButtonAction b_action) {
                actions.put(c_action, b_action);
            }

            private void executeAction(@NotNull InventoryClickEvent event) {
                ButtonAction action = actions.get(event.getClick());
                if (action == null) return;
                action.run();
                if (action instanceof PlayerAction && event.getWhoClicked() instanceof Player) {
                    ((PlayerAction) action).run((Player) event.getWhoClicked());
                }
            }

            @NotNull
            public Window getWindow() {
                return window;
            }
        }

        /**
         * Every action a Button can do on a Click
         */
        /*
        public enum ButtonAction {
            //TODO: Implement actions
            PAGE_UP, --DONE                   //go to next Page (in the ArrayList)
            PAGE_DOWN,    --DONE              //go to prior Page (in the ArrayList)
            PAGE_GOTO,    --DONE              //go to specific Page (index in ArrayList)

            RUN_TASK,                   //run a Runnable-Task
            RUN_BUKKITTASK,             //run a Bukkit-Runnable-Task
            RUN_FUTURE,                 //run a Future-Object
            RUN_COMMAND,                //run a server command with permission check
            RUN_COMMAND_WO_PERMCHECK,   //run a server command with out permission check

            CHANGE_ITEM,                //Change the Item of the Button to another Item

            NOTHING                     //do nothing on click
        }
        */
    }
}

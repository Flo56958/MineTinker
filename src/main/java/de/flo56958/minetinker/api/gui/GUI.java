package de.flo56958.minetinker.api.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

/**
 * This class is for all User-Interfaces and Items within them.
 *
 * @author Flo56958
 */
public class GUI implements Listener {

	private final List<Window> windows = Collections.synchronizedList(new ArrayList<>());

	private final JavaPlugin plugin;

	public JavaPlugin getPlugin() {
		return plugin;
	}

	private volatile boolean isClosed = true;

	public GUI(@NotNull final JavaPlugin plugin) {
		this.plugin = plugin;
		open();
	}

	/**
	 * Adds a window to the GUI
	 *
	 * @param size  The size of the window
	 * @param title The title of the window
	 * @throws IllegalStateException when GUI is closed
	 */
	public Window addWindow(final int size, @NotNull final String title) {
		if (isClosed)
			throw new IllegalStateException("GUI (" + this.hashCode() + ") is already closed!");

		final Window window = new Window(size, title, this);
		windows.add(window);

		return window;
	}

	public Window addWindow(@NotNull final Inventory inventory) {
		if (isClosed)
			throw new IllegalStateException("GUI (" + this.hashCode() + ") is already closed!");

		final Window window = new Window(inventory, this);
		windows.add(window);

		return window;
	}

	/**
	 * Removes a given window from the GUI
	 *
	 * @param window the window to remove
	 * @return true:  if window was in GUI and was successfully removed
	 * false: if window was not in GUI or was not removed
	 */
	public boolean removeWindow(@NotNull final Window window) {
		return windows.remove(window);
	}

	/**
	 * Returns if possible the associated Window-object in the GUI-Framework
	 *
	 * @param inv the Inventory to find the Window for
	 * @return the found window or null
	 */
	@Nullable
	@Contract(pure = true)
	public Window getWindowFromInventory(@NotNull final Inventory inv) {
		return windows.stream().filter(window -> window.inventory.equals(inv)).findFirst().orElse(null);
	}

	@Nullable
	public Window getWindow(final int i) {
		return windows.get(i);
	}

	/**
	 * shows the first page of the GUI to the specified Player
	 *
	 * @param player the Player
	 */
	public void show(@NotNull final Player player) {
		show(player, 0);
	}

	/**
	 * shows the [page] page of the GUI to the specified Player
	 *
	 * @param player the Player
	 * @param page   the Page shown to the Player
	 * @throws IllegalStateException when show() was called as the GUI was closed
	 */
	public void show(@NotNull final Player player, final int page) {
		synchronized (this) {
			if (isClosed)
				throw new IllegalStateException("GUI (" + this.hashCode() + ") is closed.");

			windows.get(page).show(player);
		}
	}

	public void show(@NotNull final Player player, final Window window) {
		synchronized (this) {
			if (isClosed)
				throw new IllegalStateException("GUI (" + this.hashCode() + ") is closed.");

			if (!window.getGUI().equals(this))
				throw new IllegalArgumentException("GUI (" + this.hashCode()
						+ ") does not manage Window (" + window.hashCode() + ")!");

			window.show(player);
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
	 * can be used to micromanage the performance of the GUIs
	 */
	public void open() {
		synchronized (this) {
			if (!isClosed) throw new IllegalStateException("GUI (" + this.hashCode() + ") is already open!");

			Bukkit.getPluginManager().registerEvents(this, this.plugin);
			isClosed = false;
		}
	}

	/**
	 * this will close the Listener section of the GUI
	 * show() will throw Exception when called after close()
	 * can be used to micromanage the performance of the GUIs.
	 * <p>
	 * close() will also close GUIs that are connected to the GUI via {@link ButtonAction.PAGE_GOTO}
	 */
	public void close() {
		synchronized (this) {
			if (isClosed) throw new IllegalStateException("GUI (" + this.hashCode() + ") is already closed!");

			isClosed = true;
			windows.forEach(Window::close);

			HandlerList.unregisterAll(this);
		}
	}

	public boolean isClosed() {
		return isClosed;
	}

	//<------------------------------------Events------------------------------------------->

	@EventHandler(ignoreCancelled = true)
	public void onDisable(@NotNull final PluginDisableEvent event) {
		// check if GUI is already closed due to a connecting Button Action
		if (this.plugin.equals(event.getPlugin()) && !this.isClosed()) this.close();
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onClick(@NotNull final InventoryClickEvent event) {
		if (event.getClickedInventory() == null) return;
		final Window w1 = getWindowFromInventory(event.getClickedInventory());
		final Window w2 = getWindowFromInventory(event.getWhoClicked().getOpenInventory().getTopInventory());
		if (w1 == null && w2 == null) return;

		event.setCancelled(true);

		if (w1 == null) return;
		final Window.Button clickedButton = w1.getButton(event.getSlot());

		if (clickedButton != null) clickedButton.executeAction(event);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onDrag(@NotNull final InventoryDragEvent event) {
		final Window w = getWindowFromInventory(event.getInventory());
		if (w == null) return;

		event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onMove(@NotNull final InventoryMoveItemEvent event) {
		final Window w1 = getWindowFromInventory(event.getDestination());
		final Window w2 = getWindowFromInventory(event.getInitiator());
		final Window w3 = getWindowFromInventory(event.getSource());
		if (w1 == null && w2 == null && w3 == null) return;

		event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onEvent(@NotNull final InventoryInteractEvent event) {
		final Window w = getWindowFromInventory(event.getInventory());
		if (w == null) return;

		event.setCancelled(true);
	}

	//<------------------------------------Events------------------------------------------->

	/**
	 * A wrapper class for the Minecraft Inventory Window
	 */
	public static class Window {
		private final Inventory inventory;
		private final GUI gui;
		private final Button[] buttonMap;
		private Runnable showRunnable = null;
		private int runnableRepeatTime = -1;
		private int showRunnableTaskID = -1;

		/**
		 * Creates a new Window with the given size and the given title.
		 *
		 * @param size  The number of rows in the standard Inventory. Due to Minecraft-Limitations must be between 1 and 6.
		 * @param title The Title of the Window
		 * @throws IllegalArgumentException when size is does not match the limitations.
		 */
		private Window(int size, @NotNull final String title, @NotNull final GUI gui) {
			if (size <= 0) {
				throw new IllegalArgumentException("Size of Inventory needs to be at least ONE!");
			} else if (size > 6) {
				throw new IllegalArgumentException("Size of Inventory needs to be at most SIX!");
			}

			size *= 9;

			this.inventory = Bukkit.createInventory(null, size, title);
			this.buttonMap = new Button[this.inventory.getSize()];
			this.gui = gui;
		}

		private Window(@NotNull final Inventory inventory, @NotNull final GUI gui) {
			this.inventory = inventory;
			this.buttonMap = new Button[inventory.getSize()];
			this.gui = gui;
		}

		/**
		 * Calculates the slot number from the coordinates
		 *
		 * @param x x-Coordinate
		 * @param y y-Coordinate
		 * @return the slot
		 * @throws IllegalArgumentException when Coordinates less than zero
		 */
		private int getSlot(final int x, final int y) throws IllegalArgumentException {
			if (x < 0 || y < 0 || x > 8)
				throw new IllegalArgumentException("Coordinates can not be less than ZERO or too big!");

			final int slot = (9 * y) + x;

			if (slot >= this.inventory.getSize())
				throw new IllegalArgumentException("Coordinates are to big for the given Inventory!");

			return slot;
		}

		public Button addButton(final int x, final int y, @NotNull final ItemStack item) throws IllegalArgumentException {
			return addButton(getSlot(x, y), item);
		}

		/**
		 * Get the button at the given coordinates
		 *
		 * @param x x-Coordinate
		 * @param y y-Coordinate
		 * @return the Button or null if there is no Button
		 * @throws IllegalArgumentException when Coordinates less than zero or bigger than the Inventory
		 */
		@Nullable
		public Button getButton(final int x, final int y) throws IllegalArgumentException {
			return buttonMap[getSlot(x, y)];
		}

		/**
		 * Get the button at the given slot
		 *
		 * @param slot the slot index
		 * @return the Button or null if there is no Button
		 * @throws IllegalArgumentException when slot is out of bounds
		 */
		@Nullable
		public Button getButton(final int slot) throws IllegalArgumentException {
			if (slot < 0 || slot >= buttonMap.length) throw new IllegalArgumentException("Slot is out of bounds!");
			return buttonMap[slot];
		}

		/**
		 * Adds a new Button to the Window
		 *
		 * @param slot the slot where the Button should be placed
		 * @param item the ItemStack that will appear in the Window
		 * @return the Button
		 * @throws IllegalArgumentException when slot is out of bounds
		 */
		public Button addButton(final int slot, @NotNull final ItemStack item) throws IllegalArgumentException {
			if (slot < 0 || slot >= buttonMap.length) throw new IllegalArgumentException("Slot is out of bounds!");

			Button b = new Button(item, this);

			buttonMap[slot] = b;
			inventory.setItem(slot, b.item);

			b.item = inventory.getItem(slot); //Update item as it gets changed during inventory.setItem();
			return b;
		}

		@NotNull
		public GUI getGUI() {
			return gui;
		}

		public Window setShowRunnable(final Runnable runnable, final int repeatTime) {
			this.showRunnable = runnable;
			this.runnableRepeatTime = repeatTime;
			return this;
		}

		private void show(@NotNull final Player player) {
			player.openInventory(this.inventory);

			if (showRunnable != null && this.showRunnableTaskID == -1) {
				this.showRunnableTaskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.gui.plugin, () -> {
					if (inventory.getViewers().isEmpty()) {
						Bukkit.getScheduler().cancelTask(this.showRunnableTaskID);
						this.showRunnableTaskID = -1;
						return;
					}

					this.showRunnable.run();
				}, 0, runnableRepeatTime);
			}
		}

		private void close() {
			if (showRunnableTaskID != -1) {
				Bukkit.getScheduler().cancelTask(this.showRunnableTaskID);
				this.showRunnableTaskID = -1;
			}
			for (final HumanEntity humanEntity : new ArrayList<>(this.inventory.getViewers())) {
				// new ArrayList is required because of ModificationException
				humanEntity.closeInventory();
			}

			for (final GUI.Window.Button button : this.buttonMap) {
				if (button == null) continue;
				for (final ButtonAction action : button.actions.values()) {
					if (!(action instanceof ButtonAction.PAGE_GOTO gotoAction)) continue;
					final GUI other = gotoAction.window.gui;
					if (other != null && !other.isClosed()) { //Close other GUIs
						other.close();
					}
				}
			}
		}

		public int getSize() {
			return this.inventory.getSize();
		}

		/**
		 * This class is a Button for the Window. The button can be clicked by the User to trigger certain methods.
		 */
		public static class Button {
			private final Window window;
			private ItemStack item;
			private final EnumMap<ClickType, ButtonAction> actions = new EnumMap<>(ClickType.class);

			/**
			 * creates a Button with no actions
			 *
			 * @param item the ItemStack that will appear in the Window
			 */
			private Button(@NotNull ItemStack item, @NotNull Window window) {
				this.window = window;
				this.item = item;
			}

			public Button addAction(@NotNull ClickType c_action, @NotNull ButtonAction b_action) {
				actions.put(c_action, b_action);
				return this;
			}

			private void executeAction(@NotNull InventoryClickEvent event) {
				ButtonAction action = actions.get(event.getClick());
				if (action == null) return;

				action.run();
				if (action instanceof PlayerAction playerAction && event.getWhoClicked() instanceof Player player) {
					playerAction.run(player);
				}
			}

			public ItemStack getItemStack() {
				return this.item;
			}

			@NotNull
			public Window getWindow() {
				return window;
			}
		}
	}
}

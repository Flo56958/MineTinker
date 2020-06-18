package de.flo56958.minetinker.api.gui;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.utils.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

/**
 * This class is for all User-Interfaces and Items within them.
 * This class should be thread-safe to use.
 *
 * @author Flo56958
 */
@SuppressWarnings("unused")
public class GUI implements Listener {

	private final List<Window> windows = Collections.synchronizedList(new ArrayList<>());
	private JavaPlugin plugin;

	private volatile boolean isClosed = true;

	public GUI(JavaPlugin plugin) {
		this.plugin = plugin;
		open();
	}

	public static void addNavigationButtons(Window currentPage) {
		ItemStack forwardStack = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
		ItemMeta forwardMeta = forwardStack.getItemMeta();
		if (forwardMeta != null) {
			forwardMeta.setDisplayName(ChatColor.GREEN + LanguageManager.getInstance().getString("GUIs.Forward"));
			forwardStack.setItemMeta(forwardMeta);
		}

		ItemStack backStack = new ItemStack(Material.RED_STAINED_GLASS_PANE);
		ItemMeta backMeta = backStack.getItemMeta();
		if (backMeta != null) {
			backMeta.setDisplayName(ChatColor.RED + LanguageManager.getInstance().getString("GUIs.Back"));
			backStack.setItemMeta(backMeta);
		}

		Window.Button back = currentPage.addButton(0, 5, backStack);
		back.addAction(ClickType.LEFT, new ButtonAction.PAGE_DOWN(back));

		Window.Button forward = currentPage.addButton(8, 5, forwardStack);
		forward.addAction(ClickType.LEFT, new ButtonAction.PAGE_UP(forward));
	}

	/**
	 * Adds a window to the GUI
	 *
	 * @param size  The size of the window
	 * @param title The title of the window
	 * @throws IllegalStateException when GUI is closed
	 */
	public Window addWindow(final int size, @NotNull final String title) {
		if (isClosed) {
			throw new IllegalStateException("GUI (" + this.hashCode() + ") is already closed!");
		}

		Window window = new Window(size, title, this);
		windows.add(window);

		return window;
	}

	public Window addWindow(Inventory inventory) {
		if (isClosed) {
			throw new IllegalStateException("GUI (" + this.hashCode() + ") is already closed!");
		}

		Window window = new Window(inventory, this);
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
	public Window getWindowFromInventory(final Inventory inv) {
		if (inv == null) {
			return null;
		}

		for (Window window : windows) {
			if (window.inventory.equals(inv)) {
				return window;
			}
		}

		return null;
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
	 * @param player    the Player
	 * @param page the Page shown to the Player
	 * @throws IllegalStateException when show() was called as the GUI was closed
	 */
	public void show(@NotNull final Player player, final int page) {
		synchronized (this) {
			if (isClosed) {
				throw new IllegalStateException("GUI (" + this.hashCode() + ") is closed.");
			}

			player.openInventory(windows.get(page).inventory);
		}
	}

	public void show(@NotNull final Player player, final Window window) {
		synchronized (this) {
			if (isClosed) {
				throw new IllegalStateException("GUI (" + this.hashCode() + ") is closed.");
			}

			if (!window.getGUI().equals(this)) {
				throw new IllegalArgumentException("GUI (" + this.hashCode()
						+ ") does not manage Window (" + window.hashCode() + ")!");
			}

			player.openInventory(window.inventory);
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
			if (!isClosed) {
				return;
			}

			Bukkit.getPluginManager().registerEvents(this, MineTinker.getPlugin());
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
			if (isClosed) {
				return;
			}

			isClosed = true;
			for (Window w : windows) {
				for (HumanEntity humanEntity : new ArrayList<>(w.getInventory().getViewers())) {
					//new ArrayList is required as of ModificationException
					humanEntity.closeInventory();
				}

				for (GUI.Window.Button button : w.buttonMap) {
					if (button == null) continue;
					for (ButtonAction action : button.actions.values()) {
						if (action instanceof ButtonAction.PAGE_GOTO) {
							GUI other = ((ButtonAction.PAGE_GOTO) action).window.gui;
							if (!other.equals(this)) { //Close other GUIs
								other.close();
							}
						}
					}
				}
			}

			HandlerList.unregisterAll(this);
		}
	}

	//<------------------------------------Events------------------------------------------->

	@EventHandler(ignoreCancelled = true)
	public void onDisable(PluginDisableEvent event) {
		if (event.getPlugin().equals(this.plugin)) {
			this.close();
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onClick(InventoryClickEvent event) {
		Window w1 = getWindowFromInventory(event.getClickedInventory());
		Window w2 = getWindowFromInventory(event.getWhoClicked().getOpenInventory().getTopInventory());

		if (w1 == null && w2 == null) {
			return;
		}

		event.setCancelled(true);

		if (w1 == null) return;

		Window.Button clickedButton = w1.getButtonFromSlot(event.getSlot());

		if (clickedButton != null) {
			clickedButton.executeAction(event);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onDrag(InventoryDragEvent event) {
		Window w = getWindowFromInventory(event.getInventory());

		if (w == null) {
			return;
		}

		event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onMove(InventoryMoveItemEvent event) {
		Window w1 = getWindowFromInventory(event.getDestination());

		Window w2 = getWindowFromInventory(event.getInitiator());

		Window w3 = getWindowFromInventory(event.getSource());

		if (w1 == null && w2 == null && w3 == null) return;

		event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onEvent(InventoryInteractEvent event) {
		Window w = getWindowFromInventory(event.getInventory());

		if (w == null) {
			return;
		}

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
				throw new IllegalArgumentException("Size of Inventory needs to be at least SIX!");
			}

			size *= 9;

			this.inventory = Bukkit.createInventory(null, size, title);
			this.buttonMap = new Button[size];
			this.gui = gui;
		}

		private Window(@NotNull final Inventory inventory, @NotNull final GUI gui) {
			this.inventory = inventory;
			this.buttonMap = new Button[inventory.getSize()];
			this.gui = gui;
		}

		/**
		 * Calculates the slot nr. from the coordinates
		 *
		 * @param x x-Coordinate
		 * @param y y-Coordinate
		 * @return the slot
		 * @throws IllegalArgumentException when Coordinates less than zero
		 */
		public static int getSlot(final int x, final int y, Window window) {
			if (x < 0 || y < 0) {
				throw new IllegalArgumentException("Coordinates can not be less than ZERO!");
			}

			int slot = (9 * y) + x;

			if (slot >= window.inventory.getSize()) {
				throw new IllegalArgumentException("Coordinates are to big for the given Inventory!");
			}

			return slot;
		}

		public Button addButton(final int x, final int y, @NotNull final ItemStack item) {
			return addButton(getSlot(x, y, this), item);
		}

		public Button addButton(final int slot, @NotNull final ItemStack item) {
			Button b = new Button(item, this);

			buttonMap[slot] = b;
			inventory.setItem(slot, b.item);

			b.item = inventory.getItem(slot); //Update item as it gets changed during inventory.setItem();

			return b;
		}

		@NotNull
		public Inventory getInventory() {
			return inventory;
		}

		@NotNull
		public GUI getGUI() {
			return gui;
		}

		@Nullable
		public Button getButtonFromSlot(final int slot) {
			return buttonMap[slot];
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

			public void addAction(@NotNull ClickType c_action, @NotNull ButtonAction b_action) {
				actions.put(c_action, b_action);
			}

			private void executeAction(@NotNull InventoryClickEvent event) {
				ButtonAction action = actions.get(event.getClick());

				if (action == null) {
					return;
				}

				action.run();

				if (action instanceof PlayerAction && event.getWhoClicked() instanceof Player) {
					((PlayerAction) action).run((Player) event.getWhoClicked());
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

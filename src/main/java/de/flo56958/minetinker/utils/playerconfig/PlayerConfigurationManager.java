package de.flo56958.minetinker.utils.playerconfig;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.api.gui.ButtonAction;
import de.flo56958.minetinker.api.gui.GUI;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static de.flo56958.minetinker.data.GUIs.addNavigationButtons;

public class PlayerConfigurationManager implements Listener {

	private final ArrayList<PlayerConfigurationInterface> playerConfigInterfaces = new ArrayList<>();

	private final HashMap<Player, PlayerConfigurationHandle> playerConfigs = new HashMap<>();

	private static PlayerConfigurationManager instance;

	private PlayerConfigurationManager() {}

	public static PlayerConfigurationManager getInstance() {
		if (instance == null) {
			instance = new PlayerConfigurationManager();
		}
		return instance;
	}

	public void registerPlayerConfigInterface(final PlayerConfigurationInterface pci) {
		playerConfigInterfaces.add(pci);
		playerConfigInterfaces.sort(Comparator.comparing(PlayerConfigurationInterface::getPCIDisplayName));
	}

	public void unregisterPlayerConfigInterface(final PlayerConfigurationInterface pci) {
		playerConfigInterfaces.remove(pci);
	}

	@NonNull
	private PlayerConfigurationHandle loadConfig(final Player player) {
		final File playerConfigFile = new File(MineTinker.getPlugin().getDataFolder(), "PlayerConfigs" + File.separator + player.getUniqueId() + ".yml");
		final FileConfiguration fileConfiguration = new YamlConfiguration();
		final PlayerConfigurationHandle handle = new PlayerConfigurationHandle(playerConfigFile, fileConfiguration);

		playerConfigs.put(player, handle);

		if (!playerConfigFile.exists()) return handle;

		try {
			fileConfiguration.load(playerConfigFile);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		return handle;
	}

	private FileConfiguration getConfig(final Player player) {
		PlayerConfigurationHandle handle = playerConfigs.get(player);
		if (handle == null)
			handle = loadConfig(player);

		return handle.config;
	}

	public boolean getBoolean(final Player player, final PlayerConfigurationOption option) {
		if (option.type() != PlayerConfigurationOption.Type.BOOLEAN)
			throw new IllegalArgumentException("Option is not of type BOOLEAN");

		return getConfig(player).getBoolean(option.getKey(), (boolean) option.defaultValue());
	}

	public void setBoolean(final Player player, final PlayerConfigurationOption option, final boolean value) {
		if (option.type() != PlayerConfigurationOption.Type.BOOLEAN)
			throw new IllegalArgumentException("Option is not of type BOOLEAN");

		getConfig(player).set(option.getKey(), value);
	}

	public int getInteger(final Player player, final PlayerConfigurationOption option) {
		if (option.type() != PlayerConfigurationOption.Type.INTEGER)
			throw new IllegalArgumentException("Option is not of type INTEGER");

		return getConfig(player).getInt(option.getKey(), (Integer) option.defaultValue());
	}

	public void setInteger(final Player player, final PlayerConfigurationOption option, final int value) {
		if (option.type() != PlayerConfigurationOption.Type.INTEGER)
			throw new IllegalArgumentException("Option is not of type INTEGER");

		getConfig(player).set(option.getKey(), value);
	}

	public double getDouble(final Player player, final PlayerConfigurationOption option) {
		if (option.type() != PlayerConfigurationOption.Type.DOUBLE)
			throw new IllegalArgumentException("Option is not of type DOUBLE");

		return getConfig(player).getDouble(option.getKey(), (Double) option.defaultValue());
	}

	public void setDouble(final Player player, final PlayerConfigurationOption option, final double value) {
		if (option.type() != PlayerConfigurationOption.Type.DOUBLE)
			throw new IllegalArgumentException("Option is not of type DOUBLE");

		getConfig(player).set(option.getKey(), value);
	}

	public double getString(final Player player, final PlayerConfigurationOption option) {
		if (option.type() != PlayerConfigurationOption.Type.DOUBLE)
			throw new IllegalArgumentException("Option is not of type DOUBLE");

		return getConfig(player).getDouble(option.getKey(), (Double) option.defaultValue());
	}

	public void setString(final Player player, final PlayerConfigurationOption option, final String value) {
		if (option.type() != PlayerConfigurationOption.Type.STRING)
			throw new IllegalArgumentException("Option is not of type STRING");

		getConfig(player).set(option.getKey(), value);
	}

	public GUI getPlayerConfigGUI(final Player player) {
		final GUI gui = new GUI(MineTinker.getPlugin());

		class IndexHelper {
			int pageNumber = 0;
			int yIndex = 0;
			int xIndex = 0;

			GUI.Window currentPage;

			public void addPage() {
				pageNumber++;
				currentPage = gui.addWindow(6, LanguageManager.getString("GUIs.ConfigurationEditor.Title")
						.replace("%pageNo", String.valueOf(pageNumber)));
				addNavigationButtons(currentPage);
			}

			void nextIndex() {
				xIndex++;
				if (xIndex >= 9) {
					xIndex = 0;
					yIndex++;

					if (yIndex >= 5) {
						yIndex = 0;
						addPage();
					}
				}
			}

			void newLine() {
				xIndex = 0;
				yIndex++;
				if (yIndex >= 5) {
					yIndex = 0;
					addPage();
				}
			}
		}

		final IndexHelper indexHelper = new IndexHelper();
		indexHelper.addPage();

		for (PlayerConfigurationInterface pci : playerConfigInterfaces) {
			final List<PlayerConfigurationOption> pciOptions = pci.getPCIOptions();
			if (pciOptions.isEmpty()) continue;

			final ItemStack titleItem = pci.getPCIDisplayItem();
			final ItemMeta titleMeta = titleItem.getItemMeta();
			titleMeta.setDisplayName(pci.getPCIDisplayColor() + pci.getPCIDisplayName());
			titleMeta.setLore(Collections.emptyList());
			titleItem.setItemMeta(titleMeta);
			indexHelper.currentPage.addButton(indexHelper.xIndex, indexHelper.yIndex, titleItem);
			indexHelper.nextIndex();

			for (final PlayerConfigurationOption option : pciOptions) {
				ItemStack buttonStack = new ItemStack(Material.DIRT, 1);
				final GUI.Window.Button currentButton = indexHelper.currentPage.addButton(indexHelper.xIndex, indexHelper.yIndex, buttonStack);
				buttonStack = currentButton.getItemStack(); //needs to be updated as it gets changed when added to the inventory
				final ItemMeta buttonMeta = buttonStack.getItemMeta();
				buttonMeta.setDisplayName(pci.getPCIDisplayColor() + pci.getPCIDisplayName() + ": " + ChatColor.WHITE + option.displayName());

				ItemStack buttonStackForRunnable = buttonStack;
				switch (option.type()) {
					case BOOLEAN -> {
						boolean value = getBoolean(player, option);
						buttonMeta.setLore(List.of(
								ChatColor.WHITE + LanguageManager.getString("GUIs.ConfigurationEditor.Type")
										.replace("%type", LanguageManager.getString("DataType.Boolean")),
								ChatColor.WHITE + LanguageManager.getString("GUIs.ConfigurationEditor.Value")
										.replace("%value", String.valueOf(value)),
								ChatColor.WHITE + LanguageManager.getString("GUIs.ConfigurationEditor.ToggleValue")
										.replace("%key", LanguageManager.getString("GUIs.LeftClick")))
						);

						if (value) buttonStack.setType(Material.GREEN_WOOL);
						else buttonStack.setType(Material.RED_WOOL);

						Runnable buttonRunnable = () -> {
							ItemMeta meta = buttonStackForRunnable.getItemMeta();
							if (meta == null) return;

							boolean oldValue = PlayerConfigurationManager.this.getBoolean(player, option);
							PlayerConfigurationManager.this.setBoolean(player, option, !oldValue);

							if (!oldValue) {
								buttonStackForRunnable.setType(Material.GREEN_WOOL);
							} else {
								buttonStackForRunnable.setType(Material.RED_WOOL);
							}

							List<String> lore;
							if (meta.hasLore()) {
								lore = meta.getLore();
							} else {
								lore = new ArrayList<>();
							}

							lore.set(1, ChatColor.WHITE + LanguageManager.getString("GUIs.ConfigurationEditor.Value")
									.replace("%value", String.valueOf(!oldValue)));
							meta.setLore(lore);
							buttonStackForRunnable.setItemMeta(meta);
						};
						currentButton.addAction(ClickType.LEFT, new ButtonAction.RUN_RUNNABLE(currentButton, buttonRunnable));
					}
					case INTEGER -> {
						int value = getInteger(player, option);
						buttonMeta.setLore(List.of(ChatColor.WHITE + LanguageManager.getString("GUIs.ConfigurationEditor.Type")
								.replace("%type", LanguageManager.getString("DataType.Integer")),
								ChatColor.WHITE + LanguageManager.getString("GUIs.ConfigurationEditor.Value")
										.replace("%value", String.valueOf(value)),
								"",
								ChatColor.WHITE + LanguageManager.getString("GUIs.ConfigurationEditor.IncrementValueClick")
										.replace("%key", LanguageManager.getString("GUIs.LeftClick"))
										.replace("%amount", "1 (shift +10)"),
								ChatColor.WHITE + LanguageManager.getString("GUIs.ConfigurationEditor.DecrementValueClick")
										.replace("%key", LanguageManager.getString("GUIs.RightClick"))
										.replace("%amount", "1 (shift -10)"),
								ChatColor.WHITE + LanguageManager.getString("GUIs.ConfigurationEditor.InsertValue")
										.replace("%key", LanguageManager.getString("GUIs.MiddleClick"))
								));

						buttonStack.setType(Material.COBBLESTONE);

						class intHelper {
							private void setAmount(ItemStack stack, int amount) {
								if (amount > 0 && amount <= 64) {
									stack.setAmount(amount);
								} else if (amount > 64) {
									stack.setAmount(64);
								} else {
									stack.setAmount(1);
								}
							}

							private void saveInt(int newValue) {
								ItemMeta meta = buttonStackForRunnable.getItemMeta();
								if (meta == null) return;

								List<String> lore;
								if (meta.hasLore()) {
									lore = meta.getLore();
								} else {
									lore = new ArrayList<>();
								}

								lore.set(1, ChatColor.WHITE + LanguageManager.getString("GUIs.ConfigurationEditor.Value")
										.replace("%value", String.valueOf(newValue)));
								meta.setLore(lore);
								buttonStackForRunnable.setItemMeta(meta);
							}

							private Runnable getRunnable(int i) {
								return () -> {
									int newValue = PlayerConfigurationManager.this.getInteger(player, option) + i;
									PlayerConfigurationManager.this.setInteger(player, option, newValue);
									saveInt(newValue);
									setAmount(buttonStackForRunnable, newValue);
								};
							}
						}

						intHelper helper = new intHelper();
						helper.setAmount(buttonStack, value);

						currentButton.addAction(ClickType.LEFT, new ButtonAction.RUN_RUNNABLE(currentButton, helper.getRunnable(1)))
								.addAction(ClickType.RIGHT, new ButtonAction.RUN_RUNNABLE(currentButton, helper.getRunnable(-1)))
								.addAction(ClickType.SHIFT_LEFT, new ButtonAction.RUN_RUNNABLE(currentButton, helper.getRunnable(10)))
								.addAction(ClickType.SHIFT_RIGHT, new ButtonAction.RUN_RUNNABLE(currentButton, helper.getRunnable(-10)));

						ButtonAction.PlayerRunnable pRun = (ignored, input) -> {
							try {
								int in = Integer.parseInt(input);
								PlayerConfigurationManager.this.setInteger(player, option, in);

								helper.saveInt(in);
								helper.setAmount(buttonStackForRunnable, in);
							} catch (NumberFormatException e) {
								ChatWriter.sendMessage(player, ChatColor.RED, LanguageManager.getString("GUIs.ConfigurationEditor.WrongInput")
										.replace("%type", LanguageManager.getString("DataType.Integer")).replace("%input", input));
							}
						};

						currentButton.addAction(ClickType.MIDDLE,
								new ButtonAction.REQUEST_INPUT(currentButton, pRun, ChatColor.WHITE + pci.getPCIDisplayName() + ": " + option.displayName()));
					}
					case DOUBLE, STRING -> {
					}
				}

				buttonStack.setItemMeta(buttonMeta);
				indexHelper.nextIndex();
			}

			if (indexHelper.xIndex != 0)
				indexHelper.newLine(); // add a space between the different PCI
		}

		return gui;
	}

	@EventHandler
	private void onPlayerJoin(final PlayerJoinEvent e) {
		loadConfig(e.getPlayer());
	}

	@EventHandler
	private void onPlayerQuit(final PlayerQuitEvent e) {
		PlayerConfigurationHandle handle = playerConfigs.remove(e.getPlayer());
		if (handle == null) return;

		try {
			handle.config.save(handle.configFile);
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	@EventHandler
	private void onWorldSave(final WorldSaveEvent e) {
		saveAllPlayerConfigs();
	}

	public void saveAllPlayerConfigs() {
		for (final PlayerConfigurationHandle handle : playerConfigs.values()) {
			try {
				handle.config.save(handle.configFile);
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}
	}

	private record PlayerConfigurationHandle(File configFile, FileConfiguration config) {}
}

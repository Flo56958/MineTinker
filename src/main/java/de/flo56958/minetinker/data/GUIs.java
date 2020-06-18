package de.flo56958.minetinker.data;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.api.gui.ButtonAction;
import de.flo56958.minetinker.api.gui.GUI;
import de.flo56958.minetinker.commands.subs.ReloadCommand;
import de.flo56958.minetinker.modifiers.ModManager;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.ConfigurationManager;
import de.flo56958.minetinker.utils.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class GUIs {

	private final static ItemStack forwardStack;
	private final static ItemStack backStack;
	private final static ItemStack backOtherMenuStack;
	private static GUI configurationsGUI;

	static {
		forwardStack = new ItemStack(Material.GREEN_STAINED_GLASS_PANE, 1);
		ItemMeta forwardMeta = forwardStack.getItemMeta();

		if (forwardMeta != null) {
			forwardMeta.setDisplayName(ChatColor.GREEN + LanguageManager.getInstance().getString("GUIs.Forward"));
			forwardStack.setItemMeta(forwardMeta);
		}

		backStack = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
		ItemMeta backMeta = backStack.getItemMeta();

		if (backMeta != null) {
			backMeta.setDisplayName(ChatColor.RED + LanguageManager.getInstance().getString("GUIs.Back"));
			backStack.setItemMeta(backMeta);
		}

		backOtherMenuStack = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE, 1);
		ItemMeta backOtherMenuMeta = backOtherMenuStack.getItemMeta();

		if (backOtherMenuMeta != null) {
			backOtherMenuMeta.setDisplayName(ChatColor.YELLOW + LanguageManager.getInstance().getString("GUIs.BackOtherMenu"));
			backOtherMenuStack.setItemMeta(backOtherMenuMeta);
		}

		reload();
	}

	public static void reload() {
		if (configurationsGUI != null) configurationsGUI.close();

		/* Main Configuration Manager*/
		{
			configurationsGUI = new GUI(MineTinker.getPlugin());
			int pageNo = 1;
			GUI.Window currentPage = configurationsGUI.addWindow(6,
					LanguageManager.getInstance().getString("GUIs.ConfigurationEditor.Title")
					.replace("%pageNo", String.valueOf(pageNo++)));
			GUI.addNavigationButtons(currentPage);
			ItemStack reload = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
			ItemMeta meta = reload.getItemMeta();
			if (meta != null) {
				meta.setDisplayName(ChatColor.YELLOW + LanguageManager.getInstance().getString("GUIs.ConfigurationEditor.ReloadPlugin"));
				reload.setItemMeta(meta);
			}
			GUI.Window.Button reloadButton = currentPage.addButton(4, 5, reload);
			reloadButton.addAction(ClickType.LEFT, new ButtonAction.RUN_RUNNABLE_ON_PLAYER(reloadButton, (player, ignored) -> {
				new ReloadCommand().onCommand(player, new String[2]);
				configurationsGUI.show(player, 0);
			}));

			int i = 0;
			ArrayList<String> names = new ArrayList<>(ConfigurationManager.getInstance().getAllConfigNames());
			names.sort(String::compareToIgnoreCase);
			for (String name : names) {
				ItemStack buttonStack = new ItemStack(Material.WHITE_WOOL, 1);
				ItemMeta buttonMeta = buttonStack.getItemMeta();

				switch (name) {
					case "config.yml":
						buttonStack.setType(Material.DIAMOND_PICKAXE);
						break;
					case "Elytra.yml":
						buttonStack.setType(Material.ELYTRA);
						break;
					case "BuildersWand.yml":
						buttonStack.setType(Material.DIAMOND_SHOVEL);
						break;
					default:
						for (Modifier mod : ModManager.getInstance().getAllMods()) {
							if (mod.getKey().equals(name.replace(".yml", ""))) {
								buttonStack = mod.getModItem().clone();
								buttonMeta = buttonStack.getItemMeta();
							}
						}
						break;
				}

				if (buttonMeta == null) continue;
				buttonMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.WHITE + "" + ChatColor.BOLD + name);
				buttonMeta.setLore(new ArrayList<>());
				buttonStack.setItemMeta(buttonMeta);

				GUI.Window.Button button = currentPage.addButton(i, buttonStack);
				button.addAction(ClickType.LEFT, new ButtonAction.PAGE_GOTO(button, getGUIforConfig(name, currentPage).getWindow(0)));

				i++;
				if (i >= 45) {
					currentPage = configurationsGUI.addWindow(6, LanguageManager.getInstance().getString("GUIs.ConfigurationEditor.Title")
							.replace("%pageNo", String.valueOf(pageNo++)));

					GUI.addNavigationButtons(currentPage);

					reloadButton = currentPage.addButton(4, 5, reload);
					int pagenumber = pageNo;
					reloadButton.addAction(ClickType.LEFT, new ButtonAction.RUN_RUNNABLE_ON_PLAYER(reloadButton, (player, ignored) -> {
						new ReloadCommand().onCommand(player, new String[2]);
						configurationsGUI.show(player, pagenumber - 2);
					}));
					i = 0;
				}
			}
		}
	}

	@NotNull
	private static GUI getGUIforConfig(String configName, GUI.Window backPage) {
		FileConfiguration config = ConfigurationManager.getInstance().getConfig(configName);
		GUI configGUI = new GUI(MineTinker.getPlugin());
		int pageNo = 1;
		GUI.Window currentPage = configGUI.addWindow(6, LanguageManager.getInstance().getString("GUIs.ConfigurationEditor.TitleConfigs")
				.replace("%pageNo", String.valueOf(pageNo++)).replace("%config", configName));
		if (config != null) {
			GUI.addNavigationButtons(currentPage);

			GUI.Window.Button backConfig = currentPage.addButton(4, 5, backOtherMenuStack.clone());
			backConfig.addAction(ClickType.LEFT, new ButtonAction.PAGE_GOTO(backConfig, backPage));

			ArrayList<String> keys = new ArrayList<>(config.getKeys(true));
			keys.sort(String::compareToIgnoreCase);

			HashMap<String, String> explanations = getExplanations(config, configName.replace(".yml", ""));
			int i = 0;
			for (String key : keys) {
				ItemStack buttonStack = new ItemStack(Material.DIRT, 1);
				ItemMeta buttonStackMeta = buttonStack.getItemMeta();
				if (buttonStackMeta == null) continue;
				buttonStackMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.BOLD + key);
				List<String> buttonStackLore = buttonStackMeta.getLore();
				if (buttonStackLore == null) buttonStackLore = new ArrayList<>();
				GUI.Window.Button currentButton = currentPage.addButton(i, buttonStack);
				buttonStack = currentButton.getItemStack(); //needs to be updated as it gets changed when added to the inventory

				ConfigurationSection configurationSection = config.getConfigurationSection(key);
				if (configurationSection == null) {
					Object value = config.get(key);
					final ItemStack buttonStackForRunnable = buttonStack;

					if (value instanceof Boolean) {
						buttonStackLore.add(ChatColor.WHITE + LanguageManager.getInstance().getString("GUIs.ConfigurationEditor.Type")
								.replace("%type", LanguageManager.getInstance().getString("DataType.Boolean")));
						buttonStackLore.add(ChatColor.WHITE + LanguageManager.getInstance().getString("GUIs.ConfigurationEditor.Value")
								.replace("%value", String.valueOf(value)));
						buttonStackLore.add("");
						buttonStackLore.add(ChatColor.WHITE + LanguageManager.getInstance().getString("GUIs.ConfigurationEditor.ToggleValue")
								.replace("%key", LanguageManager.getInstance().getString("GUIs.LeftClick")));

						if ((boolean) value) {
							buttonStack.setType(Material.GREEN_WOOL);
						} else {
							buttonStack.setType(Material.RED_WOOL);
						}

						Runnable buttonRunnable = () -> {
							ItemMeta meta = buttonStackForRunnable.getItemMeta();
							if (meta == null) return;

							//Boolean-Toggle
							config.set(key, !config.getBoolean(key));
							boolean newValue = config.getBoolean(key);

							if (newValue) {
								buttonStackForRunnable.setType(Material.GREEN_WOOL);
							} else {
								buttonStackForRunnable.setType(Material.RED_WOOL);
							}

							ConfigurationManager.getInstance().saveConfig(config);

							List<String> lore;
							if (meta.hasLore()) {
								lore = meta.getLore();
							} else {
								lore = new LinkedList<>();
							}

							lore.set(1, ChatColor.WHITE + LanguageManager.getInstance().getString("GUIs.ConfigurationEditor.Value")
									.replace("%value", String.valueOf(newValue)));
							meta.setLore(lore);
							buttonStackForRunnable.setItemMeta(meta);

							broadcastChange(configName + ":" + key, String.valueOf(!newValue), String.valueOf(newValue));
						};
						currentButton.addAction(ClickType.LEFT, new ButtonAction.RUN_RUNNABLE(currentButton, buttonRunnable));
					} else if (value instanceof Integer) {
						buttonStackLore.add(ChatColor.WHITE + LanguageManager.getInstance().getString("GUIs.ConfigurationEditor.Type")
								.replace("%type", LanguageManager.getInstance().getString("DataType.Integer")));
						buttonStackLore.add(ChatColor.WHITE + LanguageManager.getInstance().getString("GUIs.ConfigurationEditor.Value")
								.replace("%value", String.valueOf(value)));
						buttonStackLore.add("");
						buttonStackLore.add(ChatColor.WHITE + LanguageManager.getInstance().getString("GUIs.ConfigurationEditor.IncrementValueClick")
								.replace("%key", LanguageManager.getInstance().getString("GUIs.LeftClick"))
								.replace("%amount", "1 (shift +10)"));
						buttonStackLore.add(ChatColor.WHITE + LanguageManager.getInstance().getString("GUIs.ConfigurationEditor.DecrementValueClick")
								.replace("%key", LanguageManager.getInstance().getString("GUIs.RightClick"))
								.replace("%amount", "1 (shift -10)"));
						buttonStackLore.add(ChatColor.WHITE + LanguageManager.getInstance().getString("GUIs.ConfigurationEditor.InsertValue")
								.replace("%key", LanguageManager.getInstance().getString("GUIs.MiddleClick")));
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
								ConfigurationManager.getInstance().saveConfig(config);
								ItemMeta meta = buttonStackForRunnable.getItemMeta();
								if (meta == null) return;

								List<String> lore;
								if (meta.hasLore()) {
									lore = meta.getLore();
								} else {
									lore = new LinkedList<>();
								}

								lore.set(1, ChatColor.WHITE + LanguageManager.getInstance().getString("GUIs.ConfigurationEditor.Value")
										.replace("%value", String.valueOf(newValue)));
								meta.setLore(lore);
								buttonStackForRunnable.setItemMeta(meta);
							}

							private Runnable getRunnable(int i) {
								return () -> {
									int oldValue = config.getInt(key);
									config.set(key, oldValue + i);
									int newValue = config.getInt(key);
									saveInt(newValue);
									setAmount(buttonStackForRunnable, newValue);
									broadcastChange(configName + ":" + key, String.valueOf(oldValue), String.valueOf(newValue));
								};
							}
						}

						intHelper helper = new intHelper();
						helper.setAmount(buttonStack, (int) value);

						currentButton.addAction(ClickType.LEFT, new ButtonAction.RUN_RUNNABLE(currentButton, helper.getRunnable(1)));
						currentButton.addAction(ClickType.RIGHT, new ButtonAction.RUN_RUNNABLE(currentButton, helper.getRunnable(-1)));

						currentButton.addAction(ClickType.SHIFT_LEFT, new ButtonAction.RUN_RUNNABLE(currentButton, helper.getRunnable(10)));
						currentButton.addAction(ClickType.SHIFT_RIGHT, new ButtonAction.RUN_RUNNABLE(currentButton, helper.getRunnable(-10)));

						ButtonAction.REQUEST_INPUT.PlayerRunnable pRun = (player, input) -> {
							try {
								int in = Integer.parseInt(input);
								int oldValue = config.getInt(key);
								config.set(key, in);

								helper.saveInt(in);
								helper.setAmount(buttonStackForRunnable, in);

								broadcastChange(configName + ":" + key, String.valueOf(oldValue), String.valueOf(in));
							} catch (NumberFormatException e) {
								ChatWriter.sendMessage(player, ChatColor.RED, LanguageManager.getInstance().getString("GUIs.ConfigurationEditor.WrongInput")
										.replace("%type", LanguageManager.getInstance().getString("DataType.Integer")).replace("%input", input));
							}
						};

						currentButton.addAction(ClickType.MIDDLE,
								new ButtonAction.REQUEST_INPUT(currentButton, pRun, ChatColor.WHITE + configName + ":" + key));
					} else if (value instanceof Double) {
						buttonStackLore.add(ChatColor.WHITE + LanguageManager.getInstance().getString("GUIs.ConfigurationEditor.Type")
								.replace("%type", LanguageManager.getInstance().getString("DataType.Double")));
						buttonStackLore.add(ChatColor.WHITE + LanguageManager.getInstance().getString("GUIs.ConfigurationEditor.Value")
								.replace("%value", String.valueOf(value)));
						buttonStackLore.add("");
						buttonStackLore.add(ChatColor.WHITE + LanguageManager.getInstance().getString("GUIs.ConfigurationEditor.InsertValue")
								.replace("%key", LanguageManager.getInstance().getString("GUIs.LeftClick")));

						buttonStack.setType(Material.STONE);

						ButtonAction.REQUEST_INPUT.PlayerRunnable pRun = (player, input) -> {
							try {
								ItemMeta meta = buttonStackForRunnable.getItemMeta();
								if (meta == null) return;

								double in = Double.parseDouble(input);
								double oldValue = config.getDouble(key);
								config.set(key, in);

								ConfigurationManager.getInstance().saveConfig(config);

								List<String> lore;
								if (meta.hasLore()) {
									lore = meta.getLore();
								} else {
									lore = new LinkedList<>();
								}

								lore.set(1, ChatColor.WHITE + LanguageManager.getInstance().getString("GUIs.ConfigurationEditor.Value")
										.replace("%value", String.valueOf(in)));
								meta.setLore(lore);
								buttonStackForRunnable.setItemMeta(meta);

								broadcastChange(configName + ":" + key, String.valueOf(oldValue), input);
							} catch (NumberFormatException e) {
								ChatWriter.sendMessage(player, ChatColor.RED, LanguageManager.getInstance().getString("GUIs.ConfigurationEditor.WrongInput")
										.replace("%type", LanguageManager.getInstance().getString("DataType.Double")).replace("%input", input));
							}
						};

						currentButton.addAction(ClickType.LEFT,
								new ButtonAction.REQUEST_INPUT(currentButton, pRun, ChatColor.WHITE + configName + ":" + key));
					} else if (value instanceof String) {
						buttonStackLore.add(ChatColor.WHITE + LanguageManager.getInstance().getString("GUIs.ConfigurationEditor.Type")
								.replace("%type", LanguageManager.getInstance().getString("DataType.String")));
						buttonStackLore.add(ChatColor.WHITE + LanguageManager.getInstance().getString("GUIs.ConfigurationEditor.Value")
								.replace("%value", String.valueOf(value)));
						buttonStackLore.add("");
						buttonStackLore.add(ChatColor.WHITE + LanguageManager.getInstance().getString("GUIs.ConfigurationEditor.InsertValue")
								.replace("%key", LanguageManager.getInstance().getString("GUIs.LeftClick")));

						buttonStack.setType(Material.WHITE_WOOL);

						ButtonAction.REQUEST_INPUT.PlayerRunnable pRun = (player, input) -> {
							ItemMeta meta = buttonStackForRunnable.getItemMeta();
							if (meta == null) return;

							String oldValue = config.getString(key);

							//input = ChatWriter.addColors(input);
							config.set(key, input);

							ConfigurationManager.getInstance().saveConfig(config);

							List<String> lore;
							if (meta.hasLore()) {
								lore = meta.getLore();
							} else {
								lore = new LinkedList<>();
							}
							lore.set(1, ChatColor.WHITE + LanguageManager.getInstance().getString("GUIs.ConfigurationEditor.Value")
									.replace("%value", String.valueOf(input)));
							meta.setLore(lore);
							buttonStackForRunnable.setItemMeta(meta);

							broadcastChange(configName + ":" + key, oldValue, input);
						};

						currentButton.addAction(ClickType.LEFT, new ButtonAction.REQUEST_INPUT(currentButton, pRun,
								ChatColor.WHITE + configName + ":" + key));
					} else if (value instanceof List) {
						buttonStackLore.add(ChatColor.WHITE + LanguageManager.getInstance().getString("GUIs.ConfigurationEditor.Type")
								.replace("%type", LanguageManager.getInstance().getString("DataType.List")));
						buttonStackLore.add(ChatColor.WHITE + LanguageManager.getInstance().getString("GUIs.ConfigurationEditor.Value")
								.replace("%value", String.valueOf(value)));
						for (String line : ChatWriter.splitString(LanguageManager.getInstance().getString("GUIs.ConfigurationEditor.UnsupportedType"), 30)) {
							buttonStackLore.add(ChatColor.RED + line);
						}

						buttonStack.setType(Material.BEDROCK);
						//TODO: Action to edit List
					}
					buttonStackLore.add("");
					for (String line : ChatWriter.splitString(explanations.get(key), 50)) {
						buttonStackLore.add(ChatColor.WHITE + line);
					}

				} else { //got a configuration section that contains children, skipping as children will also be iterated over
					continue;
				}
				buttonStackMeta.setLore(buttonStackLore);
				buttonStack.setItemMeta(buttonStackMeta);

				i++;
				if (i >= 45) {
					currentPage = configGUI.addWindow(6, LanguageManager.getInstance().getString("GUIs.ConfigurationEditor.TitleConfigs")
							.replace("%pageNo", String.valueOf(pageNo++)).replace("%config", configName));

					GUI.addNavigationButtons(currentPage);

					backConfig = currentPage.addButton(4, 5, backOtherMenuStack.clone());
					backConfig.addAction(ClickType.LEFT, new ButtonAction.PAGE_GOTO(backConfig, backPage));

					i = 0;
				}
			}
		}
		return configGUI;
	}

	private static void broadcastChange(String configSetting, String oldValue, String newValue) {
		ChatWriter.logInfo(LanguageManager.getInstance().getString("GUIs.ConfigurationEditor.Change")
				.replace("%key", configSetting).replace("%old", oldValue).replace("%new", newValue));

		if (MineTinker.getPlugin().getConfig().getBoolean("BroadcastConfigChanges") || configSetting.equals("config.yml:BroadcastConfigChanges")) {
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				if (player.hasPermission("minetinker.commands.editconfigbroadcast")) {
					ChatWriter.sendMessage(player, ChatColor.RED, LanguageManager.getInstance().getString("GUIs.ConfigurationEditor.Change")
							.replace("%key", configSetting).replace("%old", oldValue).replace("%new", newValue));
				}
			}
		}
	}

	private static HashMap<String, String> getExplanations(FileConfiguration root, String config) {
		HashMap<String, String> explanations = new HashMap<>();
		String start = "GUIs.ConfigurationEditor." + config + ".";

		for (String key : root.getKeys(true)) {
			String explanation = LanguageManager.getInstance().getString(start + key);
			if (!explanation.equals("")) {
				explanations.put(key, explanation);
			}
		}

		return explanations;
	}

	public static GUI getConfigurationsGUI() {
		return configurationsGUI;
	}
}

package de.flo56958.MineTinker.Data;

import de.flo56958.MineTinker.Commands.subs.ReloadCommand;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import de.flo56958.MineTinker.Utilities.LanguageManager;
import de.flo56958.MineTinker.Utilities.nms.NBTUtils;
import de.flo56958.MineTinker.api.gui.ButtonAction;
import de.flo56958.MineTinker.api.gui.GUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class GUIs {

	private final static ItemStack forwardStack;
	private final static ItemStack backStack;
	private final static ItemStack backOtherMenuStack;
	private static GUI modGUI;
	private static GUI configurationsGUI;

	static {
		forwardStack = new ItemStack(Material.GREEN_STAINED_GLASS_PANE, 1);
		ItemMeta forwardMeta = forwardStack.getItemMeta();

		if (forwardMeta != null) {
			forwardMeta.setDisplayName(ChatColor.GREEN + LanguageManager.getString("GUIs.Forward"));
			forwardStack.setItemMeta(forwardMeta);
		}

		backStack = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
		ItemMeta backMeta = backStack.getItemMeta();

		if (backMeta != null) {
			backMeta.setDisplayName(ChatColor.RED + LanguageManager.getString("GUIs.Back"));
			backStack.setItemMeta(backMeta);
		}

		backOtherMenuStack = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE, 1);
		ItemMeta backOtherMenuMeta = backOtherMenuStack.getItemMeta();

		if (backOtherMenuMeta != null) {
			backOtherMenuMeta.setDisplayName(ChatColor.YELLOW + LanguageManager.getString("GUIs.BackOtherMenu"));
			backOtherMenuStack.setItemMeta(backOtherMenuMeta);
		}

		reload();
	}

	public static void reload() {
		GUI.guis.forEach(GUI::close);
		GUI.guis.clear(); //TODO: Remove if GUI-class is part of public API

		/*/mt mods GUIs*/
		{
			int pageNo = 0;
			modGUI = new GUI();
			GUI modRecipes = new GUI();
			GUI.Window currentPage = modGUI.addWindow(6, LanguageManager.getString("GUIs.Modifiers.Title")
					.replaceFirst("%pageNo", String.valueOf(++pageNo)));

			int i = 0;

			addNavigationButtons(currentPage);

			for (Modifier m : ModManager.instance().getAllowedMods()) {
				ItemStack item = m.getModItem().clone();
				ItemMeta meta = item.getItemMeta();

				if (meta != null) {
					meta.setDisplayName(m.getColor() + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + m.getName());
					ArrayList<String> lore = new ArrayList<>();

					String modifierItemName = Objects.requireNonNull(m.getModItem().getItemMeta()).getDisplayName();
					if (!modifierItemName.equals("")) {
						lore.add(ChatColor.WHITE + modifierItemName);
						lore.add("");
					}

					List<String> descList = ChatWriter.splitString(m.getDescription(), 30);
					for (String descPart : descList) {
						lore.add(ChatColor.WHITE + descPart);
					}

					lore.add("");

					// Max level
					String maxLevel = ChatColor.WHITE + ChatWriter.toRomanNumerals(m.getMaxLvl()) + ChatColor.GOLD;
					lore.add(ChatColor.GOLD + LanguageManager.getString("GUIs.Modifiers.MaxLevel")
							.replaceFirst("%maxLevel", maxLevel));

					// Enchant Cost
					if (m.isEnchantable()) {
						String cost = ChatColor.YELLOW + LanguageManager.getString("GUIs.Modifiers.EnchantCost");
						lore.add(cost.replaceFirst("%enchantCost", ChatWriter.toRomanNumerals(m.getEnchantCost())));
						lore.addAll(ChatWriter.splitString(LanguageManager.getString("GUIs.Modifiers.BlockToEnchant")
								.replace("%block", ChatColor.ITALIC
										+ Main.getPlugin().getConfig().getString("BlockToEnchantModifiers", "")
										+ ChatColor.RESET + "" + ChatColor.WHITE)
								.replace("%mat", m.getModItem().getType().name()).replace("%key",
										LanguageManager.getString("GUIs.RightClick")), 30));
					} else if (m.hasRecipe()) {
						lore.addAll(ChatWriter.splitString(LanguageManager.getString("GUIs.Modifiers.ClickToRecipe")
								.replace("%key", LanguageManager.getString("GUIs.LeftClick")), 30));
					}

					//Slot cost
					if (m.getSlotCost() > 0) {
						lore.add(ChatColor.WHITE + LanguageManager.getString("GUIs.Modifiers.SlotCost")
								.replaceFirst("%amount", String.valueOf(m.getSlotCost())));
					}

					lore.add("");

					//Modifier incompatibilities
					Set<Modifier> incomp = ModManager.instance().getIncompatibilities(m);
					if (!incomp.isEmpty()) {
						StringBuilder incompatibilities = new StringBuilder();
						for (Modifier in : incomp) {
							incompatibilities.append(in.getName()).append(", ");
						}

						lore.add(ChatColor.DARK_RED + "" + ChatColor.BOLD
								+ LanguageManager.getString("GUIs.Modifiers.IncompatibleWith"));

						lore.addAll(ChatWriter.splitString(incompatibilities.toString()
								.substring(0, incompatibilities.length() - 2), 30));
					}

					// Applied Enchantments
					List<Enchantment> enchants = m.getAppliedEnchantments();
					if (!enchants.isEmpty()) {
						lore.add(ChatColor.BLUE + "" + ChatColor.BOLD + LanguageManager.getString("GUIs.Modifiers.CanApply"));

						StringBuilder e = new StringBuilder();
						for (Enchantment enchant : enchants) {
							e.append(LanguageManager.getString("Enchantment." + enchant.getKey().getKey())).append(", ");
						}

						List<String> lines = ChatWriter.splitString(e.toString().substring(0, e.length() - 2),30);
						lore.addAll(lines);
					}

					// Allowed Tools
					lore.add(ChatColor.BLUE + "" + ChatColor.BOLD + LanguageManager.getString("GUIs.Modifiers.WorksOn"));

					StringBuilder builder = new StringBuilder();

					builder.append(ChatColor.WHITE);

					for (ToolType toolType : m.getAllowedTools()) {
						builder.append(LanguageManager.getString("ToolType." + toolType.name())).append(", ");
					}

					List<String> lines = ChatWriter.splitString(builder.toString().substring(0, builder.length() - 2),30);
					lore.addAll(lines);

					// Apply lore changes
					meta.setLore(lore);
					item.setItemMeta(meta);

					// Setup click actions
					GUI.Window.Button modButton = currentPage.addButton((i % 7) + 1, (i / 7) + 1, item);
					Recipe rec = null;

					Iterator<Recipe> it = Bukkit.getServer().recipeIterator();

					while (it.hasNext()) {
						Recipe temp = it.next();
						if (temp.getResult().equals(m.getModItem())) {
							rec = temp;
							break;
						}
					}

					if (rec != null) {
						GUI.Window modRecipe = modRecipes.addWindow(3, m.getColor() + m.getName());
						if (rec instanceof ShapedRecipe) {
							ShapedRecipe srec = (ShapedRecipe) rec;
							ItemStack modItem = m.getModItem().clone();
							NBTUtils.getHandler().setInt(modItem, "Showcase", (int) Math.round(Math.random() * 1000));
							GUI.Window.Button result = modRecipe.addButton(6, 1, modItem);
							result.addAction(ClickType.LEFT, new ButtonAction.PAGE_GOTO(result, currentPage));

							int slot = -1;

							for (String s : srec.getShape()) {
								if (s.length() == 1 || s.length() == 2) {
									slot++;
								}

								for (char c : s.toCharArray()) {
									slot++;

									if (c == ' ') {
										continue;
									}

									try {
										ItemStack resItem = srec.getIngredientMap().get(c).clone();
										NBTUtils.getHandler().setLong(resItem, "MT-MODS Recipe Item",
												Math.round(Math.random() * 42));
										modRecipe.addButton((slot % 3) + 2, (slot / 3), resItem);
									} catch (NullPointerException ignored) {
									}
								}

								if (s.length() == 1) {
									slot++;
								}
							}
						}
						modButton.addAction(ClickType.LEFT, new ButtonAction.PAGE_GOTO(modButton, modRecipe));
						GUI.Window.Button returnButton = modRecipe.addButton(8, 2, backOtherMenuStack.clone());
						returnButton.addAction(ClickType.LEFT, new ButtonAction.PAGE_GOTO(returnButton, currentPage));
					}

					i++;

					if (i % 28 == 0) {
						currentPage = modGUI.addWindow(6, LanguageManager.getString("GUIs.Modifiers.Title")
								.replace("%pageNo", String.valueOf(++pageNo)));

						addNavigationButtons(currentPage);
						i = 0;
					}
				}
			}
		}
		/* Main Configuration Manager*/
		{
			configurationsGUI = new GUI();
			int pageNo = 1;
			GUI.Window currentPage = configurationsGUI.addWindow(6,
					LanguageManager.getString("GUIs.ConfigurationEditor.Title")
					.replace("%pageNo", String.valueOf(pageNo++)));
			addNavigationButtons(currentPage);
			ItemStack reload = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
			ItemMeta meta = reload.getItemMeta();
			if (meta != null) {
				meta.setDisplayName(ChatColor.YELLOW + LanguageManager.getString("GUIs.ConfigurationEditor.ReloadPlugin"));
				reload.setItemMeta(meta);
			}
			GUI.Window.Button reloadButton = currentPage.addButton(4, 5, reload);
			reloadButton.addAction(ClickType.LEFT, new ButtonAction.RUN_RUNNABLE_ON_PLAYER(reloadButton, (player, ignored) -> {
				new ReloadCommand().onCommand(player, new String[2]);
				configurationsGUI.show(player, 0);
			}));

			int i = 0;
			ArrayList<String> names = new ArrayList<>(ConfigurationManager.getAllConfigNames());
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
						for (Modifier mod : ModManager.instance().getAllMods()) {
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
					currentPage = configurationsGUI.addWindow(6, LanguageManager.getString("GUIs.ConfigurationEditor.Title")
							.replace("%pageNo", String.valueOf(pageNo++)));

					addNavigationButtons(currentPage);

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

	private static void addNavigationButtons(GUI.Window currentPage) {
		ItemMeta forwardMeta = forwardStack.getItemMeta();
		if (forwardMeta != null) {
			forwardMeta.setDisplayName(ChatColor.GREEN + LanguageManager.getString("GUIs.Forward"));
			forwardStack.setItemMeta(forwardMeta);
		}

		ItemMeta backMeta = backStack.getItemMeta();
		if (backMeta != null) {
			backMeta.setDisplayName(ChatColor.RED + LanguageManager.getString("GUIs.Back"));
			backStack.setItemMeta(backMeta);
		}

		GUI.Window.Button back = currentPage.addButton(0, 5, backStack);
		back.addAction(ClickType.LEFT, new ButtonAction.PAGE_DOWN(back));

		GUI.Window.Button forward = currentPage.addButton(8, 5, forwardStack);
		forward.addAction(ClickType.LEFT, new ButtonAction.PAGE_UP(forward));
	}

	@NotNull
	private static GUI getGUIforConfig(String configName, GUI.Window backPage) {
		FileConfiguration config = ConfigurationManager.getConfig(configName);
		GUI configGUI = new GUI();
		int pageNo = 1;
		GUI.Window currentPage = configGUI.addWindow(6, LanguageManager.getString("GUIs.ConfigurationEditor.TitleConfigs")
				.replace("%pageNo", String.valueOf(pageNo++)).replace("%config", configName));
		if (config != null) {
			addNavigationButtons(currentPage);

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
						buttonStackLore.add(ChatColor.WHITE + LanguageManager.getString("GUIs.ConfigurationEditor.Type")
								.replace("%type", LanguageManager.getString("DataType.Boolean")));
						buttonStackLore.add(ChatColor.WHITE + LanguageManager.getString("GUIs.ConfigurationEditor.Value")
								.replace("%value", String.valueOf(value)));
						buttonStackLore.add("");
						buttonStackLore.add(ChatColor.WHITE + LanguageManager.getString("GUIs.ConfigurationEditor.ToggleValue")
								.replace("%key", LanguageManager.getString("GUIs.LeftClick")));

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

							ConfigurationManager.saveConfig(config);

							List<String> lore;
							if (meta.hasLore()) {
								lore = meta.getLore();
							} else {
								lore = new LinkedList<>();
							}

							lore.set(1, ChatColor.WHITE + LanguageManager.getString("GUIs.ConfigurationEditor.Value")
									.replace("%value", String.valueOf(newValue)));
							meta.setLore(lore);
							buttonStackForRunnable.setItemMeta(meta);

							broadcastChange(configName + ":" + key, String.valueOf(!newValue), String.valueOf(newValue));
						};
						currentButton.addAction(ClickType.LEFT, new ButtonAction.RUN_RUNNABLE(currentButton, buttonRunnable));
					} else if (value instanceof Integer) {
						buttonStackLore.add(ChatColor.WHITE + LanguageManager.getString("GUIs.ConfigurationEditor.Type")
								.replace("%type", LanguageManager.getString("DataType.Integer")));
						buttonStackLore.add(ChatColor.WHITE + LanguageManager.getString("GUIs.ConfigurationEditor.Value")
								.replace("%value", String.valueOf(value)));
						buttonStackLore.add("");
						buttonStackLore.add(ChatColor.WHITE + LanguageManager.getString("GUIs.ConfigurationEditor.IncrementValueClick")
								.replace("%key", LanguageManager.getString("GUIs.LeftClick"))
								.replace("%amount", "1 (shift +10)"));
						buttonStackLore.add(ChatColor.WHITE + LanguageManager.getString("GUIs.ConfigurationEditor.DecrementValueClick")
								.replace("%key", LanguageManager.getString("GUIs.RightClick"))
								.replace("%amount", "1 (shift -10)"));
						buttonStackLore.add(ChatColor.WHITE + LanguageManager.getString("GUIs.ConfigurationEditor.InsertValue")
								.replace("%key", LanguageManager.getString("GUIs.MiddleClick")));
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
								ConfigurationManager.saveConfig(config);
								ItemMeta meta = buttonStackForRunnable.getItemMeta();
								if (meta == null) return;

								List<String> lore;
								if (meta.hasLore()) {
									lore = meta.getLore();
								} else {
									lore = new LinkedList<>();
								}

								lore.set(1, ChatColor.WHITE + LanguageManager.getString("GUIs.ConfigurationEditor.Value")
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
								ChatWriter.sendMessage(player, ChatColor.RED, LanguageManager.getString("GUIs.ConfigurationEditor.WrongInput")
										.replace("%type", LanguageManager.getString("DataType.Integer")).replace("%input", input));
							}
						};

						currentButton.addAction(ClickType.MIDDLE,
								new ButtonAction.REQUEST_INPUT(currentButton, pRun, ChatColor.WHITE + configName + ":" + key));
					} else if (value instanceof Double) {
						buttonStackLore.add(ChatColor.WHITE + LanguageManager.getString("GUIs.ConfigurationEditor.Type")
								.replace("%type", LanguageManager.getString("DataType.Double")));
						buttonStackLore.add(ChatColor.WHITE + LanguageManager.getString("GUIs.ConfigurationEditor.Value")
								.replace("%value", String.valueOf(value)));
						buttonStackLore.add("");
						buttonStackLore.add(ChatColor.WHITE + LanguageManager.getString("GUIs.ConfigurationEditor.InsertValue")
								.replace("%key", LanguageManager.getString("GUIs.LeftClick")));

						buttonStack.setType(Material.STONE);

						ButtonAction.REQUEST_INPUT.PlayerRunnable pRun = (player, input) -> {
							try {
								ItemMeta meta = buttonStackForRunnable.getItemMeta();
								if (meta == null) return;

								double in = Double.parseDouble(input);
								double oldValue = config.getDouble(key);
								config.set(key, in);

								ConfigurationManager.saveConfig(config);

								List<String> lore;
								if (meta.hasLore()) {
									lore = meta.getLore();
								} else {
									lore = new LinkedList<>();
								}

								lore.set(1, ChatColor.WHITE + LanguageManager.getString("GUIs.ConfigurationEditor.Value")
										.replace("%value", String.valueOf(in)));
								meta.setLore(lore);
								buttonStackForRunnable.setItemMeta(meta);

								broadcastChange(configName + ":" + key, String.valueOf(oldValue), input);
							} catch (NumberFormatException e) {
								ChatWriter.sendMessage(player, ChatColor.RED, LanguageManager.getString("GUIs.ConfigurationEditor.WrongInput")
										.replace("%type", LanguageManager.getString("DataType.Double")).replace("%input", input));
							}
						};

						currentButton.addAction(ClickType.LEFT,
								new ButtonAction.REQUEST_INPUT(currentButton, pRun, ChatColor.WHITE + configName + ":" + key));
					} else if (value instanceof String) {
						buttonStackLore.add(ChatColor.WHITE + LanguageManager.getString("GUIs.ConfigurationEditor.Type")
								.replace("%type", LanguageManager.getString("DataType.String")));
						buttonStackLore.add(ChatColor.WHITE + LanguageManager.getString("GUIs.ConfigurationEditor.Value")
								.replace("%value", String.valueOf(value)));
						buttonStackLore.add("");
						buttonStackLore.add(ChatColor.WHITE + LanguageManager.getString("GUIs.ConfigurationEditor.InsertValue")
								.replace("%key", LanguageManager.getString("GUIs.LeftClick")));

						buttonStack.setType(Material.WHITE_WOOL);

						ButtonAction.REQUEST_INPUT.PlayerRunnable pRun = (player, input) -> {
							ItemMeta meta = buttonStackForRunnable.getItemMeta();
							if (meta == null) return;

							String oldValue = config.getString(key);

							//input = ChatWriter.addColors(input);
							config.set(key, input);

							ConfigurationManager.saveConfig(config);

							List<String> lore;
							if (meta.hasLore()) {
								lore = meta.getLore();
							} else {
								lore = new LinkedList<>();
							}
							lore.set(1, ChatColor.WHITE + LanguageManager.getString("GUIs.ConfigurationEditor.Value")
									.replace("%value", String.valueOf(input)));
							meta.setLore(lore);
							buttonStackForRunnable.setItemMeta(meta);

							broadcastChange(configName + ":" + key, oldValue, input);
						};

						currentButton.addAction(ClickType.LEFT, new ButtonAction.REQUEST_INPUT(currentButton, pRun,
								ChatColor.WHITE + configName + ":" + key));
					} else if (value instanceof List) {
						buttonStackLore.add(ChatColor.WHITE + LanguageManager.getString("GUIs.ConfigurationEditor.Type")
								.replace("%type", LanguageManager.getString("DataType.List")));
						buttonStackLore.add(ChatColor.WHITE + LanguageManager.getString("GUIs.ConfigurationEditor.Value")
								.replace("%value", String.valueOf(value)));
						for (String line : ChatWriter.splitString(LanguageManager.getString("GUIs.ConfigurationEditor.UnsupportedType"), 30)) {
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
					currentPage = configGUI.addWindow(6, LanguageManager.getString("GUIs.ConfigurationEditor.TitleConfigs")
							.replace("%pageNo", String.valueOf(pageNo++)).replace("%config", configName));

					addNavigationButtons(currentPage);

					backConfig = currentPage.addButton(4, 5, backOtherMenuStack.clone());
					backConfig.addAction(ClickType.LEFT, new ButtonAction.PAGE_GOTO(backConfig, backPage));

					i = 0;
				}
			}
		}
		return configGUI;
	}

	private static void broadcastChange(String configSetting, String oldValue, String newValue) {
		ChatWriter.logInfo(LanguageManager.getString("GUIs.ConfigurationEditor.Change")
				.replace("%key", configSetting).replace("%old", oldValue).replace("%new", newValue));

		if (Main.getPlugin().getConfig().getBoolean("BroadcastConfigChanges") || configSetting.equals("config.yml:BroadcastConfigChanges")) {
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				if (player.hasPermission("minetinker.commands.editconfigbroadcast")) {
					ChatWriter.sendMessage(player, ChatColor.RED, LanguageManager.getString("GUIs.ConfigurationEditor.Change")
							.replace("%key", configSetting).replace("%old", oldValue).replace("%new", newValue));
				}
			}
		}
	}

	private static HashMap<String, String> getExplanations(FileConfiguration root, String config) {
		HashMap<String, String> explanations = new HashMap<>();
		String start = "GUIs.ConfigurationEditor." + config + ".";

		for (String key : root.getKeys(true)) {
			String explanation = LanguageManager.getString(start + key);
			if (!explanation.equals("")) {
				explanations.put(key, explanation);
			}
		}

		return explanations;
	}

	public static GUI getModGUI() {
		return modGUI;
	}

	public static GUI getConfigurationsGUI() {
		return configurationsGUI;
	}
}

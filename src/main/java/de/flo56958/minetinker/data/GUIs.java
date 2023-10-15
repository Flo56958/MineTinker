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
import de.flo56958.minetinker.utils.data.DataHandler;
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
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class GUIs {

	public final static ItemStack forwardStack;
	public final static ItemStack backStack;
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
		if (modGUI != null) modGUI.close();
		if (configurationsGUI != null) configurationsGUI.close();

		/*/mt mods GUIs*/
		{
			int pageNo = 0;
			modGUI = new GUI(MineTinker.getPlugin());
			GUI modRecipes = new GUI(MineTinker.getPlugin());
			GUI.Window currentPage = modGUI.addWindow(6, LanguageManager.getString("GUIs.Modifiers.Title")
					.replaceFirst("%pageNo", String.valueOf(++pageNo)));

			int i = 0;

			addNavigationButtons(currentPage);

			for (Modifier m : ModManager.instance().getAllowedMods()) {
				ItemStack item = m.getModItem().clone();
				ItemMeta meta = item.getItemMeta();

				if (meta != null) {
					meta.setDisplayName(m.getColor() + "" + ChatColor.UNDERLINE + ChatColor.BOLD + m.getName());
					ArrayList<String> lore = new ArrayList<>();

					String modifierItemName = Objects.requireNonNull(m.getModItem().getItemMeta()).getDisplayName();
					if (!modifierItemName.isEmpty()) {
						lore.add(ChatColor.WHITE + modifierItemName);
						lore.add("");
					}

					List<String> descList = ChatWriter.splitString(m.getDescription(), 30);
					for (String descPart : descList) {
						lore.add(ChatColor.WHITE + descPart);
					}

					lore.add("");

					// Max level
					final String maxLevel = ChatColor.WHITE
							+ (ModManager.layout.getBoolean("UseRomans.Level")
							? ChatWriter.toRomanNumerals(m.getMaxLvl())
							: String.valueOf(m.getMaxLvl())) + ChatColor.GOLD;
					lore.add(ChatColor.GOLD + LanguageManager.getString("GUIs.Modifiers.MaxLevel")
							.replaceFirst("%maxLevel", maxLevel));

					//Minimum Tool Level
					if (m.getMinimumLevelRequirement() >= 1) {
						lore.add(ChatColor.GOLD + LanguageManager.getString("GUIs.Modifiers.MinimumToolLevel")
								.replaceFirst("%level",
										ModManager.layout.getBoolean("UseRomans.Level")
												? ChatWriter.toRomanNumerals(m.getMinimumLevelRequirement())
												: String.valueOf(m.getMinimumLevelRequirement())));
					}

					// Enchant Cost
					if (m.isEnchantable()) {
						final String cost = ChatColor.YELLOW + LanguageManager.getString("GUIs.Modifiers.EnchantCost");
						lore.add(cost.replaceFirst("%enchantCost",
								ModManager.layout.getBoolean("UseRomans.Level")
										? ChatWriter.toRomanNumerals(m.getEnchantCost())
										: String.valueOf(m.getEnchantCost())));
						lore.addAll(ChatWriter.splitString(LanguageManager.getString("GUIs.Modifiers.BlockToEnchant")
								.replace("%block", ChatColor.ITALIC
										+ ChatWriter.toCamel(MineTinker.getPlugin().getConfig().getString("BlockToEnchantModifiers", ""))
										+ ChatColor.RESET + ChatColor.WHITE)
								.replace("%mat", ChatWriter.toCamel(m.getModItem().getType().name())).replace("%key",
										LanguageManager.getString("GUIs.RightClick")), 30));
					}

					// Recipe Hint
					if (m.hasRecipe()) {
						lore.addAll(ChatWriter.splitString(LanguageManager.getString("GUIs.Modifiers.ClickToRecipe")
								.replace("%key", LanguageManager.getString("GUIs.LeftClick")), 30));
					}

					//Slot cost
					lore.add(ChatColor.WHITE + LanguageManager.getString("GUIs.Modifiers.SlotCost")
							.replaceFirst("%amount",
										ModManager.layout.getBoolean("UseRomans.FreeSlots")
												? ChatWriter.toRomanNumerals(m.getSlotCost())
												: String.valueOf(m.getSlotCost())));

					lore.add("");

					//Modifier incompatibilities
					List<Modifier> incomp = new ArrayList<>(ModManager.instance().getIncompatibilities(m));
					incomp.removeIf(mod -> !mod.isAllowed());
					if (!incomp.isEmpty()) {
						incomp.sort(Comparator.comparing(Modifier::getName));
						final StringBuilder incompatibilities = new StringBuilder();
						for (final Modifier in : incomp) {
							incompatibilities.append(in.getName()).append(", ");
						}

						lore.add(ChatColor.DARK_RED + "" + ChatColor.BOLD
								+ LanguageManager.getString("GUIs.Modifiers.IncompatibleWith"));

						lore.addAll(ChatWriter.splitString(incompatibilities
								.substring(0, incompatibilities.length() - 2), 30));
					}

					// Applied Enchantments
					final List<Enchantment> enchants = m.getAppliedEnchantments();
					if (!enchants.isEmpty()) {
						enchants.sort(Comparator.comparing(e -> LanguageManager.getString("Enchantment." + e.getKey().getKey())));
						lore.add(ChatColor.BLUE + "" + ChatColor.BOLD + LanguageManager.getString("GUIs.Modifiers.CanApply"));

						final StringBuilder e = new StringBuilder();
						for (final Enchantment enchant : enchants) {
							e.append(LanguageManager.getString("Enchantment." + enchant.getKey().getKey())).append(", ");
						}

						final List<String> lines = ChatWriter.splitString(e.substring(0, e.length() - 2),30);
						lore.addAll(lines);
					}

					// Allowed Tools
					lore.add(ChatColor.BLUE + "" + ChatColor.BOLD + LanguageManager.getString("GUIs.Modifiers.WorksOn"));

					final StringBuilder builder = new StringBuilder();

					builder.append(ChatColor.WHITE);

					final List<ToolType> types = m.getAllowedTools();
					types.sort(Comparator.comparing(t -> LanguageManager.getString("ToolType." + t.name())));

					for (final ToolType toolType : types) {
						builder.append(LanguageManager.getString("ToolType." + toolType.name())).append(", ");
					}

					final List<String> lines = ChatWriter.splitString(builder.substring(0, builder.length() - 2),30);
					lore.addAll(lines);

					// Apply lore changes
					meta.setLore(lore);
					item.setItemMeta(meta);

					// Setup click actions
					GUI.Window.Button modButton = currentPage.addButton((i % 7) + 1, (i / 7) + 1, item);
					//GiveModifierItem-Action
					modButton.addAction(ClickType.SHIFT_LEFT, new ButtonAction.RUN_RUNNABLE_ON_PLAYER(modButton,
							(player, input) -> {
								if (player.hasPermission("minetinker.commands.givemodifieritem")) {
									if (!player.getInventory().addItem(m.getModItem()).isEmpty()) { //adds items to (full) inventory
										player.getWorld().dropItem(player.getLocation(), m.getModItem());
									} // no else as it gets added in if-clause
								}
							}));

					//Recipe Action
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
						if (rec instanceof ShapedRecipe srec) {
							HashMap<GUI.Window.Button, RecipeChoice.MaterialChoice> choices = new HashMap<>();
							ItemStack modItem = m.getModItem().clone();
							DataHandler.setTag(modItem, "Showcase", (int) Math.round(Math.random() * 1000), PersistentDataType.INTEGER);
							GUI.Window.Button result = modRecipe.addButton(6, 1, modItem);
							result.addAction(ClickType.LEFT, new ButtonAction.PAGE_GOTO(result, currentPage));

							int slot = -1;

							for (String s : srec.getShape()) {
								if (s.length() == 1 || s.length() == 2) {
									slot++;
								}

								for (final char c : s.toCharArray()) {
									slot++;

									if (c == ' ') {
										continue;
									}

									try {
										ItemStack resItem = srec.getIngredientMap().get(c).clone();
										DataHandler.setTag(resItem, "MT-MODSRecipeItem",
												Math.round(Math.random() * 42), PersistentDataType.LONG);
										final GUI.Window.Button recButton = modRecipe.addButton((slot % 3) + 2, (slot / 3), resItem);

										// prepare runnable for multiple choices
										if (srec.getChoiceMap().get(c) instanceof RecipeChoice.MaterialChoice mchoice) {
											if (mchoice.getChoices().size() > 1) {
												choices.put(recButton, mchoice);
											}
										}
									} catch (NullPointerException ignored) {
									}
								}

								if (s.length() == 1) {
									slot++;
								}
							}

							if (!choices.isEmpty()) {
								final Runnable runnable = new Runnable() {
									private final HashMap<GUI.Window.Button, RecipeChoice.MaterialChoice> map = choices;
									private int counter = 0;
									@Override
									public void run() {
										for (Map.Entry<GUI.Window.Button, RecipeChoice.MaterialChoice> entry : map.entrySet()) {
											final List<Material> choices = entry.getValue().getChoices();
											entry.getKey().getItemStack().setType(choices.get(counter % choices.size()));
										}

										counter++;
										if (counter < 0) {
											counter = 0;
										}
									}
								};
								modRecipe.setShowRunnable(runnable, 20);
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
			configurationsGUI = new GUI(MineTinker.getPlugin());
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
					case "config.yml" -> buttonStack.setType(Material.DIAMOND_PICKAXE);
					case "Elytra.yml" -> buttonStack.setType(Material.ELYTRA);
					case "BuildersWand.yml" -> buttonStack.setType(Material.DIAMOND_SHOVEL);
					default -> {
						for (Modifier mod : ModManager.instance().getAllMods()) {
							if (mod.getKey().equals(name.replace(".yml", ""))) {
								buttonStack = mod.getModItem().clone();
								buttonMeta = buttonStack.getItemMeta();
							}
						}
					}
				}

				if (buttonMeta == null) continue;
				buttonMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.WHITE + ChatColor.BOLD + name);
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

	public static void addNavigationButtons(GUI.Window currentPage) {
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
		GUI configGUI = new GUI(MineTinker.getPlugin());
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

						currentButton.addAction(ClickType.LEFT, new ButtonAction.RUN_RUNNABLE(currentButton, helper.getRunnable(1)))
							.addAction(ClickType.RIGHT, new ButtonAction.RUN_RUNNABLE(currentButton, helper.getRunnable(-1)))
							.addAction(ClickType.SHIFT_LEFT, new ButtonAction.RUN_RUNNABLE(currentButton, helper.getRunnable(10)))
							.addAction(ClickType.SHIFT_RIGHT, new ButtonAction.RUN_RUNNABLE(currentButton, helper.getRunnable(-10)));

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

		if (MineTinker.getPlugin().getConfig().getBoolean("BroadcastConfigChanges") || configSetting.equals("config.yml:BroadcastConfigChanges")) {
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
			if (!explanation.isEmpty()) {
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

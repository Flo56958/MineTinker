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

	@NotNull
	private static GUI createModGUI(@NotNull final Collection<Modifier> modifiers, @NotNull final String title) {
		int pageNo = 0;
		GUI modifierGUI = new GUI(MineTinker.getPlugin());
		GUI modRecipes = new GUI(MineTinker.getPlugin());
		GUI.Window currentPage = modifierGUI.addWindow(6, title + LanguageManager.getString("GUIs.Modifiers.Title")
				.replaceFirst("%pageNo", String.valueOf(++pageNo)));

		int i = 0;

		if (modifiers.size() > 28)
			addNavigationButtons(currentPage);

		for (final Modifier m : modifiers) {
			final ItemStack item = m.getModItem().clone();
			final ItemMeta meta = item.getItemMeta();

			if (meta != null) {
				meta.setDisplayName(m.getColor() + "" + ChatColor.UNDERLINE + ChatColor.BOLD + m.getName());
				final ArrayList<String> lore = new ArrayList<>();

				String modifierItemName = Objects.requireNonNull(m.getModItem().getItemMeta()).getDisplayName();
				if (!modifierItemName.isEmpty()) {
					lore.add(ChatColor.WHITE + modifierItemName);
					lore.add("");
				}

				lore.addAll(ChatWriter.splitString(m.getDescription(), 30));

				lore.add("");

				// Max level
				final String maxLevel = ChatColor.WHITE
						+ (ModManager.layout.getBoolean("UseRomans.Level")
						? ChatWriter.toRomanNumerals(m.getMaxLvl())
						: String.valueOf(m.getMaxLvl())) + ChatColor.GOLD;
				lore.add(ChatColor.GOLD + LanguageManager.getString("GUIs.Modifiers.MaxLevel")
						.replaceFirst("%maxLevel", maxLevel));

				//Minimum Tool Level
				if (m.getMinimumLevelRequirement() > 1) {
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
				if (m.getSlotCost() > 0) {
					lore.add(ChatColor.WHITE + LanguageManager.getString("GUIs.Modifiers.SlotCost")
							.replaceFirst("%amount",
									ModManager.layout.getBoolean("UseRomans.FreeSlots")
											? ChatWriter.toRomanNumerals(m.getSlotCost())
											: String.valueOf(m.getSlotCost())));
				}

				lore.add("");

				//Modifier incompatibilities
				List<Modifier> incomp = new ArrayList<>(ModManager.instance().getIncompatibilities(m));
				incomp.removeIf(mod -> !mod.isAllowed());
				if (!incomp.isEmpty()) {
					incomp.sort(Comparator.comparing(Modifier::getName));
					final StringBuilder incompatibilities = new StringBuilder();
					incomp.forEach(mod -> incompatibilities.append(mod.getName()).append(ChatColor.WHITE).append(", "));

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
					enchants.forEach(enchant ->
							e.append(LanguageManager.getString("Enchantment." + enchant.getKey().getKey())).append(", "));
					lore.addAll(ChatWriter.splitString(e.substring(0, e.length() - 2),30));
				}

				// Allowed Tools
				lore.add(ChatColor.BLUE + "" + ChatColor.BOLD + LanguageManager.getString("GUIs.Modifiers.WorksOn"));

				final StringBuilder builder = new StringBuilder();
				builder.append(ChatColor.WHITE);
				final List<ToolType> types = m.getAllowedTools();
				types.sort(Comparator.comparing(t -> LanguageManager.getString("ToolType." + t.name())));
				types.forEach(type -> builder.append(LanguageManager.getString("ToolType." + type.name())).append(", "));
				lore.addAll(ChatWriter.splitString(builder.substring(0, builder.length() - 2),30));

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
				if (m.getNamespaceKey() != null) {
					final Recipe rec = Bukkit.getRecipe(m.getNamespaceKey());

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
										// Add empty button if no ingredient is present
										modRecipe.addButton((slot % 3) + 2, (slot / 3), new ItemStack(Material.AIR));
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
				}

				i++;

				if (i % 28 == 0) {
					currentPage = modifierGUI.addWindow(6, title + LanguageManager.getString("GUIs.Modifiers.Title")
							.replace("%pageNo", String.valueOf(++pageNo)));

					addNavigationButtons(currentPage);
					i = 0;
				}
			}
		}

		if (i == 0) modifierGUI.removeWindow(currentPage);

		while(i % 28 != 0) {
			currentPage.addButton((i % 7) + 1, (i / 7) + 1, new ItemStack(Material.AIR));
			i++;
		}

		final ItemStack filler = new ItemStack(Material.WHITE_STAINED_GLASS_PANE, 1);
		final ItemMeta fillerMeta = filler.getItemMeta();
		if (fillerMeta != null) {
			fillerMeta.setDisplayName(ChatColor.WHITE + "");
			filler.setItemMeta(fillerMeta);
		}

		for (GUI gui : List.of(modifierGUI, modRecipes)) {
			for (int id = 0; id < gui.getWindowAmount(); id++) {
				GUI.Window window = gui.getWindow(id);
				for (int slot = 0; slot < window.getSize(); slot++) {
					if (window.getButton(slot) == null) {
						window.addButton(slot, filler);
					}
				}
			}
		}

		return modifierGUI;
	}

	public static void reload() {
		if (modGUI != null && !modGUI.isClosed()) modGUI.close();
		if (configurationsGUI != null && !configurationsGUI.isClosed()) configurationsGUI.close();

		modGUI = createModGUI(ModManager.instance().getAllowedMods(),
				LanguageManager.getString("ToolType.ALL") + ": ");
		if (MineTinker.getPlugin().getConfig().getBoolean("ExtendedModifierGUI", true)) {
			ArrayList<GUI> guis = new ArrayList<>();
			guis.add(modGUI);
			ItemStack filterStack = new ItemStack(Material.BOOK, 1);
			ItemMeta filterMeta = filterStack.getItemMeta();
			if (filterMeta != null) {
				filterMeta.setDisplayName(ChatColor.YELLOW + LanguageManager.getString("GUIs.Modifiers.FilterButton"));
				filterStack.setItemMeta(filterMeta);
			}

			final HashMap<ToolType, ItemStack> toolmap = new HashMap<>();
			toolmap.put(ToolType.ALL, new ItemStack(Material.ANVIL, 1));
			toolmap.put(ToolType.ARMOR, new ItemStack(Material.ARMOR_STAND, 1));
			toolmap.put(ToolType.TOOLS, new ItemStack(Material.STICK, 1));

			toolmap.put(ToolType.AXE, new ItemStack(Material.DIAMOND_AXE, 1));
			toolmap.put(ToolType.HOE, new ItemStack(Material.DIAMOND_HOE, 1));
			toolmap.put(ToolType.PICKAXE, new ItemStack(Material.DIAMOND_PICKAXE, 1));
			toolmap.put(ToolType.SHOVEL, new ItemStack(Material.DIAMOND_SHOVEL, 1));
			toolmap.put(ToolType.SWORD, new ItemStack(Material.DIAMOND_SWORD, 1));

			toolmap.put(ToolType.BOW, new ItemStack(Material.BOW, 1));
			toolmap.put(ToolType.CROSSBOW, new ItemStack(Material.CROSSBOW, 1));
			toolmap.put(ToolType.TRIDENT, new ItemStack(Material.TRIDENT, 1));

            toolmap.put(ToolType.ELYTRA, new ItemStack(Material.ELYTRA, 1));
			toolmap.put(ToolType.HELMET, new ItemStack(Material.DIAMOND_HELMET, 1));
			toolmap.put(ToolType.CHESTPLATE, new ItemStack(Material.DIAMOND_CHESTPLATE, 1));
			toolmap.put(ToolType.LEGGINGS, new ItemStack(Material.DIAMOND_LEGGINGS, 1));
			toolmap.put(ToolType.BOOTS, new ItemStack(Material.DIAMOND_BOOTS, 1));
			toolmap.put(ToolType.SHIELD, new ItemStack(Material.SHIELD, 1));

			toolmap.put(ToolType.FISHINGROD, new ItemStack(Material.FISHING_ROD, 1));
			toolmap.put(ToolType.SHEARS, new ItemStack(Material.SHEARS, 1));

			List<ToolType> toolTypes = new ArrayList<>(List.of(ToolType.values()));
			toolTypes.remove(ToolType.INVALID);
			toolTypes.remove(ToolType.OTHER);

			final GUI filterGUI = new GUI(MineTinker.getPlugin());
			final GUI.Window filterPage = filterGUI.addWindow(6, LanguageManager.getString("GUIs.Modifiers.FilterButton"));
            for (final ToolType type : toolTypes) {
                final List<Material> materials = Arrays.asList(type.getToolMaterials().toArray(new Material[0]));
                materials.sort(Comparator.comparing(Material::getMaxDurability));
                final ItemStack item = toolmap.get(type);
                final ItemMeta itemMeta = item.getItemMeta();
                int slot = switch (type) {
                    case ALL -> 5 * 9 + 4;
                    case ARMOR -> 5 * 9 + 5;
                    case TOOLS -> 5 * 9 + 3;
                    case AXE -> 2 * 9 + 0;
                    case HOE -> 4 * 9 + 0;
                    case PICKAXE -> 1 * 9 + 0;
                    case SHOVEL -> 3 * 9 + 0;
                    case SWORD -> 0 * 9 + 0;
                    case BOW -> 1 * 9 + 2;
                    case CROSSBOW -> 2 * 9 + 2;
                    case TRIDENT -> 3 * 9 + 2;
                    case ELYTRA -> 1 * 9 + 6;
                    case HELMET -> 0 * 9 + 5;
                    case CHESTPLATE -> 1 * 9 + 5;
                    case LEGGINGS -> 2 * 9 + 5;
                    case BOOTS -> 3 * 9 + 5;
                    case SHIELD -> 1 * 9 + 4;
                    case FISHINGROD -> 1 * 9 + 8;
                    case SHEARS -> 2 * 9 + 8;
                    default -> -1;
                };
                final List<Modifier> mods = ModManager.instance().getAllowedMods();
                mods.removeIf(mod -> mod.getAllowedTools().stream().map(ToolType::getToolMaterials)
                        .flatMap(HashSet::stream).noneMatch(type.getToolMaterials()::contains));

                if (itemMeta != null) {
                    itemMeta.setDisplayName(ChatColor.WHITE + LanguageManager.getString("ToolType." + type.name()));
                    itemMeta.setLore(List.of(ChatColor.WHITE + LanguageManager.getString("GUIs.Modifiers.FilterLore")
                            .replace("%amount", String.valueOf(mods.size()))));
                    item.setItemMeta(itemMeta);
                }

                final GUI.Window.Button button = filterPage.addButton(slot, item);

                if (mods.isEmpty()) continue;

                final GUI filteredGUI = (type == ToolType.ALL)
                        ? modGUI : createModGUI(mods, LanguageManager.getString("ToolType." + type.name()) + ": ");
                if (filteredGUI != modGUI) guis.add(filteredGUI);
                button.addAction(ClickType.LEFT, new ButtonAction.PAGE_GOTO(button, filteredGUI.getWindow(0)));
            }

			final ItemStack filler = new ItemStack(Material.WHITE_STAINED_GLASS_PANE, 1);
			final ItemMeta fillerMeta = filler.getItemMeta();
			if (fillerMeta != null) {
				fillerMeta.setDisplayName(ChatColor.WHITE + "");
				filler.setItemMeta(fillerMeta);
			}

			for (int i = 0; i < 6 * 9; ++i) {
				if (filterPage.getButton(i) == null) {
					filterPage.addButton(i, filler);
				}
			}

			guis.forEach(gui -> {
				for (int i = 0; i < gui.getWindowAmount(); i++) {
					GUI.Window window = gui.getWindow(i);

					GUI.Window.Button button = window.addButton(4, 5, filterStack);
					button.addAction(ClickType.LEFT, new ButtonAction.PAGE_GOTO(button, filterPage));
				}
			});
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

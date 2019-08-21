package de.flo56958.MineTinker.Data;

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
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class GUIs {

    private static GUI modGUI;

    private static GUI configurationsGUI;

    private static ItemStack forwardStack;
    private static ItemStack backStack;
    private static ItemStack backOtherMenuStack;

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
        /*/mt mods GUIs*/
        {
            int pageNo = 0;
            modGUI = new GUI();
            GUI modRecipes = new GUI();
            GUI.Window currentPage = modGUI.addWindow(6, LanguageManager.getString("GUIs.Modifiers.Title").replaceFirst("%pageNo", "" + ++pageNo));

            int i = 0;

            GUI.Window.Button back = currentPage.addButton(0, 5, backStack.clone());
            back.addAction(ClickType.LEFT, new ButtonAction.PAGE_DOWN(back));

            GUI.Window.Button forward = currentPage.addButton(8, 5, forwardStack.clone());
            forward.addAction(ClickType.LEFT, new ButtonAction.PAGE_UP(forward));

            for (Modifier m : ModManager.instance().getAllowedMods()) {
                ItemStack item = m.getModItem().clone();
                ItemMeta meta = item.getItemMeta();

                if (meta != null) {
                    meta.setDisplayName(m.getColor() + m.getName());
                    ArrayList<String> lore = new ArrayList<>();

                    lore.add(ChatColor.WHITE + Objects.requireNonNull(m.getModItem().getItemMeta()).getDisplayName());
                    lore.add("");

                    List<String> descList = ChatWriter.splitString(m.getDescription(), 30);
                    for (String descPart : descList) {
                        lore.add(ChatColor.WHITE + descPart);
                    }

                    lore.add("");

                    // Max level
                    String maxLevel = ChatColor.WHITE + ChatWriter.toRomanNumerals(m.getMaxLvl()) + ChatColor.GOLD;
                    lore.add(ChatColor.GOLD + LanguageManager.getString("GUIs.Modifiers.MaxLevel").replaceFirst("%maxLevel", maxLevel));

                    lore.add("");

                    // Enchant Cost
                    if (m.isEnchantable()) {
                        String cost = ChatWriter.addColors(LanguageManager.getString("GUIs.Modifiers.EnchantCost"));
                        lore.add(cost.replaceFirst("%enchantCost", ChatWriter.toRomanNumerals(m.getEnchantCost())));

                        lore.add("");
                    }

                    // Allowed Tools
                    lore.add(ChatWriter.addColors(LanguageManager.getString("GUIs.Modifiers.WorksOn")));

                    StringBuilder builder = new StringBuilder();

                    builder.append(ChatColor.WHITE);

                    int count = 0;

                    for (ToolType toolType : m.getAllowedTools()) {
                        builder.append(LanguageManager.getString("ToolType." + toolType.name())).append(", ");

                        if (++count > 2) {
                            lore.add(builder.toString());

                            builder = new StringBuilder();
                            builder.append(ChatColor.WHITE);

                            count = 0;
                        }
                    }

                    String lastLine = builder.toString();

                    lore.add(lastLine.substring(0, lastLine.length() - 2));

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
                                        NBTUtils.getHandler().setLong(resItem, "MT-MODS Recipe Item", Math.round(Math.random() * 42));
                                        modRecipe.addButton((slot % 3) + 2, (slot / 3), resItem);
                                    } catch (NullPointerException ignored) {}
                                }

                                if (s.length() == 1) {
                                    slot++;
                                }
                            }
                        }
                        modButton.addAction(ClickType.LEFT, new ButtonAction.PAGE_GOTO(modButton, modRecipe));
                    }

                    i++;

                    if (i % 28 == 0) {
                        currentPage = modGUI.addWindow(6, LanguageManager.getString("GUIs.Modifiers.Title").replaceFirst("%pageNo", "" + ++pageNo));

                        back = currentPage.addButton(0, 5, backStack.clone());
                        back.addAction(ClickType.LEFT, new ButtonAction.PAGE_DOWN(back));

                        forward = currentPage.addButton(8, 5, forwardStack.clone());
                        forward.addAction(ClickType.LEFT, new ButtonAction.PAGE_UP(forward));

                        i = 0;
                    }
                }
            }
        }
        /* Main Configuration Manager*/
        {
            configurationsGUI = new GUI();
            int pageNo = 1;
            GUI.Window currentPage = configurationsGUI.addWindow(6, "Configuration Manager, Page " + pageNo++);
            GUI.Window.Button back = currentPage.addButton(0, 5, backStack.clone());
            back.addAction(ClickType.LEFT, new ButtonAction.PAGE_DOWN(back));
            GUI.Window.Button forward = currentPage.addButton(8, 5, forwardStack.clone());
            forward.addAction(ClickType.LEFT, new ButtonAction.PAGE_UP(forward));

            int i = 0;
            ArrayList<String> names = new ArrayList<>(ConfigurationManager.getAllConfigNames());
            names.sort(String::compareToIgnoreCase);
            for (String name : names) {
                ItemStack buttonStack = new ItemStack(Material.WHITE_WOOL, 1);
                ItemMeta buttonMeta = buttonStack.getItemMeta();

                for (Modifier mod : ModManager.instance().getAllMods()) {
                    if (mod.getKey().equals(name.replace(".yml", ""))) {
                        buttonStack = mod.getModItem().clone();
                        buttonMeta = buttonStack.getItemMeta();
                    }
                }

                if (buttonMeta == null) continue;
                buttonMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.WHITE + "" + ChatColor.BOLD + name);
                buttonMeta.setLore(new ArrayList<>());
                buttonStack.setItemMeta(buttonMeta);

                GUI.Window.Button button = currentPage.addButton(i, buttonStack);
                button.addAction(ClickType.LEFT, new ButtonAction.PAGE_GOTO(button, getGUIforConfig(name, currentPage).getWindow(0)));

                i++;
                if (i >= 45) {
                    currentPage = configurationsGUI.addWindow(6, "Configuration Manager, Page " + pageNo++);

                    back = currentPage.addButton(0, 5, backStack.clone());
                    back.addAction(ClickType.LEFT, new ButtonAction.PAGE_DOWN(back));

                    forward = currentPage.addButton(8, 5, forwardStack.clone());
                    forward.addAction(ClickType.LEFT, new ButtonAction.PAGE_UP(forward));

                    i = 0;
                }
            }
        }
    }

    @NotNull
    private static GUI getGUIforConfig(String configName, GUI.Window backPage) {
        FileConfiguration config = ConfigurationManager.getConfig(configName);
        GUI configGUI = new GUI();
        int pageNo = 1;
        GUI.Window currentPage = configGUI.addWindow(6, configName + ", Page " + pageNo++);
        if (config != null) {
            GUI.Window.Button back = currentPage.addButton(0, 5, backStack.clone());
            back.addAction(ClickType.LEFT, new ButtonAction.PAGE_DOWN(back));
            GUI.Window.Button forward = currentPage.addButton(8, 5, forwardStack.clone());
            forward.addAction(ClickType.LEFT, new ButtonAction.PAGE_UP(forward));

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
                        buttonStackLore.add(ChatColor.WHITE + "Type: Boolean");
                        buttonStackLore.add(ChatColor.WHITE + "Value: " + ChatColor.GOLD + value);

                        if ((boolean) value) {
                            buttonStack.setType(Material.GREEN_WOOL);
                        } else {
                            buttonStack.setType(Material.RED_WOOL);
                        }

                        Runnable buttonRunnable = () -> {
                            //Boolean-Toggle
                            config.set(key, !config.getBoolean(key));
                            boolean newValue = config.getBoolean(key);

                            if (newValue) {
                                buttonStackForRunnable.setType(Material.GREEN_WOOL);
                            } else {
                                buttonStackForRunnable.setType(Material.RED_WOOL);
                            }

                            ConfigurationManager.saveConfig(config);
                            ItemMeta meta = buttonStackForRunnable.getItemMeta();
                            List<String> lore = meta.getLore();
                            lore.set(1, ChatColor.WHITE + "Value: " + ChatColor.GOLD + newValue);
                            meta.setLore(lore);
                            buttonStackForRunnable.setItemMeta(meta);
                        };
                        currentButton.addAction(ClickType.LEFT, new ButtonAction.RUN_RUNNABLE(currentButton, buttonRunnable));
                    } else if (value instanceof Integer) {
                        buttonStackLore.add(ChatColor.WHITE + "Type: Integer");
                        buttonStackLore.add(ChatColor.WHITE + "Value: " + ChatColor.GOLD + value);
                        buttonStackLore.add("");
                        buttonStackLore.add(ChatColor.WHITE + "Left Click to increment by 1 (with shift +10)!");
                        buttonStackLore.add(ChatColor.WHITE + "Right Click to decrement by 1 (with shift -10)!");
                        buttonStackLore.add(ChatColor.WHITE + "Middle Click to insert new value!");
                        buttonStack.setType(Material.COBBLESTONE);
                        if ((int) value > 0 && (int) value <= 64) {
                            buttonStack.setAmount((int) value);
                        } else if ((int) value > 64) {
                            buttonStack.setAmount(64);
                        } else {
                            buttonStack.setAmount(1);
                        }

                        class intRunnables {
                            private Runnable getRunnable(int i) {
                                return () -> {
                                    config.set(key, config.getInt(key) + i);
                                    int newValue = config.getInt(key);

                                    ConfigurationManager.saveConfig(config);
                                    ItemMeta meta = buttonStackForRunnable.getItemMeta();
                                    List<String> lore = meta.getLore();
                                    lore.set(1, ChatColor.WHITE + "Value: " + ChatColor.GOLD + newValue);
                                    meta.setLore(lore);
                                    buttonStackForRunnable.setItemMeta(meta);
                                    if (newValue > 0 && newValue <= 64) {
                                        buttonStackForRunnable.setAmount(newValue);
                                    } else if (newValue > 64) {
                                        buttonStackForRunnable.setAmount(64);
                                    } else {
                                        buttonStackForRunnable.setAmount(1);
                                    }
                                };
                            }
                        }
                        currentButton.addAction(ClickType.LEFT, new ButtonAction.RUN_RUNNABLE(currentButton, new intRunnables().getRunnable(1)));
                        currentButton.addAction(ClickType.RIGHT, new ButtonAction.RUN_RUNNABLE(currentButton, new intRunnables().getRunnable(-1)));

                        currentButton.addAction(ClickType.SHIFT_LEFT, new ButtonAction.RUN_RUNNABLE(currentButton, new intRunnables().getRunnable(10)));
                        currentButton.addAction(ClickType.SHIFT_RIGHT, new ButtonAction.RUN_RUNNABLE(currentButton, new intRunnables().getRunnable(-10)));

                        ButtonAction.REQUEST_INPUT.PlayerRunnable pRun = new ButtonAction.REQUEST_INPUT.PlayerRunnable() {
                            @Override
                            public void run(Player player, String input) {
                                try {
                                    int in = Integer.parseInt(input);
                                    config.set(key, in);

                                    ConfigurationManager.saveConfig(config);
                                    ItemMeta meta = buttonStackForRunnable.getItemMeta();
                                    List<String> lore = meta.getLore();
                                    lore.set(1, ChatColor.WHITE + "Value: " + ChatColor.GOLD + in);
                                    meta.setLore(lore);
                                    buttonStackForRunnable.setItemMeta(meta);
                                    if (in > 0 && in <= 64) {
                                        buttonStackForRunnable.setAmount(in);
                                    } else if (in > 64) {
                                        buttonStackForRunnable.setAmount(64);
                                    } else {
                                        buttonStackForRunnable.setAmount(1);
                                    }
                                } catch (NumberFormatException e) {
                                    ChatWriter.sendMessage(player, ChatColor.RED, "Your input was not correct! Expected an int, got " + input);
                                }
                            }
                        };

                        currentButton.addAction(ClickType.MIDDLE, new ButtonAction.REQUEST_INPUT(currentButton, pRun, ChatColor.WHITE + configName + ":" + key));
                    } else if (value instanceof Double) {
                        buttonStackLore.add(ChatColor.WHITE + "Type: Double");
                        buttonStackLore.add(ChatColor.WHITE + "Value: " + ChatColor.GOLD + value);
                        buttonStackLore.add("");
                        buttonStackLore.add(ChatColor.WHITE + "Left-Click to insert new value!");

                        buttonStack.setType(Material.STONE);

                        ButtonAction.REQUEST_INPUT.PlayerRunnable pRun = new ButtonAction.REQUEST_INPUT.PlayerRunnable() {
                            @Override
                            public void run(Player player, String input) {
                                try {
                                    double in = Double.parseDouble(input);
                                    config.set(key, in);

                                    ConfigurationManager.saveConfig(config);
                                    ItemMeta meta = buttonStackForRunnable.getItemMeta();
                                    List<String> lore = meta.getLore();
                                    lore.set(1, ChatColor.WHITE + "Value: " + ChatColor.GOLD + in);
                                    meta.setLore(lore);
                                    buttonStackForRunnable.setItemMeta(meta);
                                } catch (NumberFormatException e) {
                                    ChatWriter.sendMessage(player, ChatColor.RED, "Your input was not correct! Expected an double, got " + input);
                                }
                            }
                        };

                        currentButton.addAction(ClickType.LEFT, new ButtonAction.REQUEST_INPUT(currentButton, pRun, ChatColor.WHITE + configName + ":" + key));
                    } else if (value instanceof String) {
                        buttonStackLore.add(ChatColor.WHITE + "Type: String");
                        buttonStackLore.add(ChatColor.WHITE + "Value: " + ChatColor.GOLD + value);
                        buttonStackLore.add("");
                        buttonStackLore.add(ChatColor.WHITE + "Left-Click to insert new value!");

                        buttonStack.setType(Material.WHITE_WOOL);

                        ButtonAction.REQUEST_INPUT.PlayerRunnable pRun = new ButtonAction.REQUEST_INPUT.PlayerRunnable() {
                            @Override
                            public void run(Player player, String input) {
                                input = ChatWriter.addColors(input);
                                config.set(key, input);

                                ConfigurationManager.saveConfig(config);
                                ItemMeta meta = buttonStackForRunnable.getItemMeta();
                                List<String> lore = meta.getLore();
                                lore.set(1, ChatColor.WHITE + "Value: " + ChatColor.GOLD + input);
                                meta.setLore(lore);
                                buttonStackForRunnable.setItemMeta(meta);
                            }
                        };

                        currentButton.addAction(ClickType.LEFT, new ButtonAction.REQUEST_INPUT(currentButton, pRun, ChatColor.WHITE + configName + ":" + key));
                    } else if (value instanceof List) {
                        buttonStackLore.add(ChatColor.WHITE + "Type: List<UNKNOWN>");
                        buttonStackLore.add(ChatColor.WHITE + "Value: " + ChatColor.GOLD + value);
                        for (String line : ChatWriter.splitString("For now this Type is unsupported and can neither be viewed or edited!", 30)) {
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
                    currentPage = configGUI.addWindow(6, configName + ", Page " + pageNo++);

                    back = currentPage.addButton(0, 5, backStack.clone());
                    back.addAction(ClickType.LEFT, new ButtonAction.PAGE_DOWN(back));

                    forward = currentPage.addButton(8, 5, forwardStack.clone());
                    forward.addAction(ClickType.LEFT, new ButtonAction.PAGE_UP(forward));

                    backConfig = currentPage.addButton(4, 5, backOtherMenuStack.clone());
                    backConfig.addAction(ClickType.LEFT, new ButtonAction.PAGE_GOTO(backConfig, backPage));

                    i = 0;
                }
            }
        }
        return configGUI;
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

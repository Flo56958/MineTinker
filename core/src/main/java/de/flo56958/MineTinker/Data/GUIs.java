package de.flo56958.MineTinker.Data;

import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.LanguageManager;
import de.flo56958.MineTinker.Utilities.nms.NBTUtils;
import de.flo56958.MineTinker.api.gui.ButtonAction;
import de.flo56958.MineTinker.api.gui.GUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class GUIs {

    private static GUI modGUI;

    private static GUI mainConfigSettings;

    private static ItemStack forwardStack;
    private static ItemStack backStack;

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
            //TODO: Simplify and shorten code
            //TODO: Implement LanguageSystem
            //TODO: Implement other object types
            //TODO: Implement Broadcast changes to console and OPs
            //TODO: Polish interface
            //TODO: Add HashMap to have explanations to the configuration options
            FileConfiguration config = Main.getPlugin().getConfig();
            ConfigurationSection defaultSection = config.getDefaultSection();
            if (defaultSection != null) {
                mainConfigSettings = new GUI();
                //GUI childrenSettings = new GUI();
                int pageNo = 1;
                GUI.Window currentPage = mainConfigSettings.addWindow(6, "Main Configuration, Page" + pageNo++);
                GUI.Window.Button back = currentPage.addButton(0, 5, backStack.clone());
                back.addAction(ClickType.LEFT, new ButtonAction.PAGE_DOWN(back));
                GUI.Window.Button forward = currentPage.addButton(8, 5, forwardStack.clone());
                forward.addAction(ClickType.LEFT, new ButtonAction.PAGE_UP(forward));

                ArrayList<String> keys = new ArrayList<>(defaultSection.getKeys(true));
                keys.sort(String::compareToIgnoreCase);
                int i = 0;
                for (String key : keys) {
                    ItemStack buttonStack = new ItemStack(Material.DIRT, 1);
                    ItemMeta buttonStackMeta = buttonStack.getItemMeta();
                    if (buttonStackMeta == null) continue;
                    buttonStackMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.BOLD + key);
                    List<String> buttonStackLore = buttonStackMeta.getLore();
                    if (buttonStackLore == null) buttonStackLore = new ArrayList<>();
                    GUI.Window.Button currentButton = currentPage.addButton(i, buttonStack);
                    buttonStack = currentButton.getItemStack(); //needs to be updated

                    ConfigurationSection configurationSection = defaultSection.getConfigurationSection(key);
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
                                Main.getPlugin().saveConfig();
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
                            if ((int) value > 0) {
                                buttonStack.setAmount((int) value);
                            } else {
                                buttonStack.setAmount(1);
                            }

                            class intRunnables {
                                private Runnable getRunnable(int i) {
                                    return () -> {
                                        config.set(key, config.getInt(key) + i);
                                        int newValue = config.getInt(key);

                                        Main.getPlugin().saveConfig();
                                        ItemMeta meta = buttonStackForRunnable.getItemMeta();
                                        List<String> lore = meta.getLore();
                                        lore.set(1, ChatColor.WHITE + "Value: " + ChatColor.GOLD + newValue);
                                        meta.setLore(lore);
                                        buttonStackForRunnable.setItemMeta(meta);
                                        if (newValue > 0) {
                                            buttonStackForRunnable.setAmount(newValue);
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

                            //TODO: Middle click action
                        } else if (value instanceof Double) {
                            buttonStackLore.add(ChatColor.WHITE + "Type: Double");
                            buttonStackLore.add(ChatColor.WHITE + "Value: " + ChatColor.GOLD + value);

                            buttonStack.setType(Material.STONE);
                            //TODO: Action to change Double
                        } else if (value instanceof String) {
                            buttonStackLore.add(ChatColor.WHITE + "Type: String");
                            buttonStackLore.add(ChatColor.WHITE + "Value: " + ChatColor.GOLD + value);

                            buttonStack.setType(Material.WHITE_WOOL);
                            //TODO: Action to change String
                        }
                    } else {
                        continue;
                    }
                    buttonStackMeta.setLore(buttonStackLore);
                    buttonStack.setItemMeta(buttonStackMeta);

                    i++;
                    if (i >= 45) {
                        currentPage = mainConfigSettings.addWindow(6, "Main config #" + pageNo++);

                        back = currentPage.addButton(0, 5, backStack.clone());
                        back.addAction(ClickType.LEFT, new ButtonAction.PAGE_DOWN(back));

                        forward = currentPage.addButton(8, 5, forwardStack.clone());
                        forward.addAction(ClickType.LEFT, new ButtonAction.PAGE_UP(forward));

                        i = 0;
                    }
                }
            }
        }
    }

    public static GUI getModGUI() {
        return modGUI;
    }

    public static GUI getMainConfigSettings() {
        return mainConfigSettings;
    }
}

package de.flo56958.MineTinker.Data;

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
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GUIs {

    private static GUI modGUI;

    static {
        reload();
    }

    public static void reload() {
        ItemStack forwardStack = new ItemStack(Material.GREEN_STAINED_GLASS_PANE, 1);
        ItemMeta forwardMeta = forwardStack.getItemMeta();

        if (forwardMeta != null) {
            forwardMeta.setDisplayName(ChatColor.GREEN + LanguageManager.getString("GUIs.Forward"));
            forwardStack.setItemMeta(forwardMeta);
        }

        ItemStack backStack = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
        ItemMeta backMeta = backStack.getItemMeta();

        if (backMeta != null) {
            backMeta.setDisplayName(ChatColor.RED + LanguageManager.getString("GUIs.Back"));
            backStack.setItemMeta(backMeta);
        }

        { /*/mt mods GUIs*/
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

                    String[] desc = m.getDescription().split("\u200B");

                    ArrayList<String> lore = new ArrayList<>();

                    lore.add("");

                    // Description
                    if (desc.length == 2) {
                        lore.add(ChatColor.AQUA + desc[0]);

                        List<String> descList = ChatWriter.splitString(desc[1], 30);

                        for (String descPart : descList) {
                            lore.add(ChatColor.WHITE + descPart);
                        }
                    } else {
                        lore.add(ChatColor.WHITE + m.getDescription());
                    }

                    lore.add("");

                    // Max level
                    String maxLevel = ChatColor.WHITE + ChatWriter.toRomanNumerals(m.getMaxLvl()) + ChatColor.GOLD;
                    lore.add(ChatColor.GOLD + LanguageManager.getString("GUIs.Modifiers.MaxLevel").replaceFirst("%maxLevel", maxLevel));

                    lore.add("");

                    // Allowed Tools

                    lore.add(ChatWriter.addColors(LanguageManager.getString("GUIs.Modifiers.WorksOn")));

                    StringBuilder builder = new StringBuilder();

                    builder.append(ChatColor.WHITE);

                    int count = 0;

                    for (ToolType toolType : m.getAllowedTools()) {
                        builder.append(toolType.name().replace("_", " ")).append(", ");

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
    }

    public static GUI getModGUI() {
        return modGUI;
    }

}

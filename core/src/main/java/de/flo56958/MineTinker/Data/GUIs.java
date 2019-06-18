package de.flo56958.MineTinker.Data;

import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
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
    private static GUI modRecipes;

    static {
        reload();
    }

    public static void reload() {
        ItemStack forwardStack = new ItemStack(Material.GREEN_STAINED_GLASS_PANE, 1);
        ItemMeta meta_ = forwardStack.getItemMeta();

        meta_.setDisplayName(ChatColor.GREEN + "Forward");
        forwardStack.setItemMeta(meta_);

        ItemStack backStack = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
        meta_ = backStack.getItemMeta();
        meta_.setDisplayName(ChatColor.RED + "Back");
        backStack.setItemMeta(meta_);

        { /*/mt mods GUIs*/
            int pageNo = 0;
            modGUI = new GUI();
            modRecipes = new GUI();
            GUI.Window currentPage = modGUI.addWindow(6, "MineTinker-Modifiers, " + ++pageNo);

            int i = 0;
            GUI.Window.Button back = currentPage.addButton(0, 5, backStack.clone());
            back.addAction(ClickType.LEFT, new ButtonAction.PAGE_DOWN(back));
            GUI.Window.Button forward = currentPage.addButton(8, 5, forwardStack.clone());
            forward.addAction(ClickType.LEFT, new ButtonAction.PAGE_UP(forward));

            for (Modifier m : ModManager.instance().getAllowedMods()) {
                ItemStack item = m.getModItem().clone();
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(m.getColor() + m.getName());

                String[] desc = m.getDescription().split("\u200B");

                ArrayList<String> lore = new ArrayList<>();

                lore.add("");

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
                lore.add(ChatColor.GOLD + "Max Level: " + ChatColor.WHITE + ChatWriter.toRomanNumerals(m.getMaxLvl()));
                meta.setLore(lore);
                item.setItemMeta(meta);
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
                        GUI.Window.Button result = modRecipe.addButton(6, 1, m.getModItem().clone());
                        result.addAction(ClickType.LEFT, new ButtonAction.PAGE_GOTO(result, currentPage));
                        int slot = -1;
                        for (String s : srec.getShape()) {
                            if (s.length() == 1 || s.length() == 2) slot++;
                            for (char c : s.toCharArray()) {
                                slot++;
                                if (c == ' ') continue;
                                try {
                                    modRecipe.addButton((slot % 3) + 2, (slot / 3), srec.getIngredientMap().get(c).clone());
                                } catch (NullPointerException ignored) {
                                }
                            }
                            if (s.length() == 1) slot++;
                        }
                    }
                    modButton.addAction(ClickType.LEFT, new ButtonAction.PAGE_GOTO(modButton, modRecipe));
                }

                i++;
                if (i % 28 == 0) {
                    currentPage = modGUI.addWindow(6, "MineTinker-Modifiers, " + ++pageNo);
                    back = currentPage.addButton(0, 5, backStack.clone());
                    back.addAction(ClickType.LEFT, new ButtonAction.PAGE_DOWN(back));
                    forward = currentPage.addButton(8, 5, forwardStack.clone());
                    forward.addAction(ClickType.LEFT, new ButtonAction.PAGE_UP(forward));
                    i = 0;
                }
            }
        }
    }

    public static GUI getModGUI() {
        return modGUI;
    }

}

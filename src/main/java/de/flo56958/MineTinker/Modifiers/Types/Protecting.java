package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Craftable;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.Arrays;

public class Protecting extends Modifier implements Craftable {
    private static final ModManager modManager = ModManager.instance();
    private static final PluginManager pluginManager = Bukkit.getPluginManager();
    private static final FileConfiguration config = Main.getMain().getConfigurations().getConfig("Protecting.yml");

    public Protecting() {
        super(config.getString("Protecting.name"),
                "[" + config.getString("Protecting.name_modifier") + "] " + config.getString("Protecting.description"),
                ModifierType.PROTECTING,
                ChatColor.GRAY,
                config.getInt("Protecting.MaxLevel"),
                ItemGenerator.itemEnchanter(Material.OBSIDIAN, ChatColor.GRAY + config.getString("Protecting.name_modifier"), 1, Enchantment.PROTECTION_ENVIRONMENTAL, 1),
                new ArrayList<>(Arrays.asList(ToolType.HELMET, ToolType.CHESTPLATE, ToolType.LEGGINGS, ToolType.BOOTS)),
                Main.getPlugin());
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (Modifier.checkAndAdd(p, tool, this, "protecting", isCommand) == null) {
            return null;
        }

        ItemMeta meta = tool.getItemMeta();

        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, modManager.getModLevel(tool, this), true);
        if (config.getBoolean("HideEnchants")) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        } else {
            meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        tool.setItemMeta(meta);

        return tool;
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(config, this, "Protecting", "Modifier_Protecting");
    }
}

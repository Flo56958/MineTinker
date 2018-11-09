package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
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

public class Haste extends Modifier {

    private static final ModManager modManager = Main.getModManager();
    private static PluginManager pluginManager = Bukkit.getPluginManager();
    private static final FileConfiguration config = Main.getPlugin().getConfig();

    public Haste() {
        super(config.getString("Modifiers.Haste.name"),
                "[Compressed Redstoneblock] Tool can destroy blocks faster!",
                ModifierType.HASTE,
                ChatColor.DARK_RED,
                config.getInt("Modifiers.Haste.MaxLevel"),
                ItemGenerator.itemEnchanter(Material.REDSTONE_BLOCK, ChatColor.DARK_RED + config.getString("Modifiers.Haste.name_modifier"), 1, Enchantment.DIG_SPEED, 1),
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.PICKAXE, ToolType.SHOVEL)),
                Main.getPlugin());
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (Modifier.checkAndAdd(p, tool, this, "haste", isCommand) == null) {
            return null;
        }

        ItemMeta meta = tool.getItemMeta();

        meta.addEnchant(Enchantment.DIG_SPEED, modManager.getModLevel(tool, this), true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        tool.setItemMeta(meta);

        return tool;
    }
}

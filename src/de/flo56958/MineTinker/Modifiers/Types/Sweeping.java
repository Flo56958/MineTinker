package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Craftable;
import de.flo56958.MineTinker.Modifiers.Enchantable;
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
import java.util.Collections;

public class Sweeping extends Modifier implements Enchantable, Craftable {

    private static final ModManager modManager = Main.getModManager();
    private static final PluginManager pluginManager = Bukkit.getPluginManager();
    private static final FileConfiguration config = Main.getMain().getConfigurations().getConfig("Sweeping.yml");

    public Sweeping() {
        super(config.getString("Sweeping.name"),
                "[Enchanted Iron Ingot] More damage over a greater area!",
                ModifierType.SWEEPING,
                ChatColor.RED,
                config.getInt("Sweeping.MaxLevel"),
                ItemGenerator.itemEnchanter(Material.IRON_INGOT, ChatColor.RED + config.getString("Sweeping.name_modifier"), 1, Enchantment.SWEEPING_EDGE, 1),
                new ArrayList<>(Collections.singletonList(ToolType.SWORD)),
                Main.getPlugin());
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (Modifier.checkAndAdd(p, tool, this, "sweeping", isCommand) == null) {
            return null;
        }

        ItemMeta meta = tool.getItemMeta();

        meta.addEnchant(Enchantment.SWEEPING_EDGE, modManager.getModLevel(tool, this), true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        tool.setItemMeta(meta);

        return tool;
    }

    @Override
    public void enchantItem(Player p, ItemStack item) {
        if (!p.hasPermission("minetinker.modifiers.sweeping.craft")) { return; }
        ItemGenerator.createModifierItem(p, this, "Sweeping");
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(config, modManager, ModifierType.SWEEPING, "Sweeping", "Modifier_Sweeping");
    }
}

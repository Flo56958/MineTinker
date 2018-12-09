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
import java.util.Arrays;

public class Fiery extends Modifier implements Enchantable, Craftable {

    private static final ModManager modManager = Main.getModManager();
    private static final PluginManager pluginManager = Bukkit.getPluginManager();
    private static final FileConfiguration config = Main.getMain().getConfigurations().getConfig("Fiery.yml");

    public Fiery() {
        super(config.getString("Fiery.name"),
                "[Blaze-Rod] Inflames enemies!",
                ModifierType.FIERY,
                ChatColor.YELLOW,
                config.getInt("Fiery.MaxLevel"),
                ItemGenerator.itemEnchanter(Material.BLAZE_ROD, ChatColor.YELLOW + config.getString("Fiery.name_modifier"), 1, Enchantment.FIRE_ASPECT, 1),
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.SWORD)),
                Main.getPlugin());
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (Modifier.checkAndAdd(p, tool, this, "fiery", isCommand) == null) {
            return null;
        }

        ItemMeta meta = tool.getItemMeta();

        if (ToolType.BOW.getMaterials().contains(tool.getType())) {
            meta.addEnchant(Enchantment.ARROW_FIRE, modManager.getModLevel(tool, this), true);
        } else if (ToolType.SWORD.getMaterials().contains(tool.getType())) {
            meta.addEnchant(Enchantment.FIRE_ASPECT, modManager.getModLevel(tool, this), true);
        }
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        tool.setItemMeta(meta);

        return tool;
    }

    @Override
    public void enchantItem(Player p, ItemStack item) {
        if (!p.hasPermission("minetinker.modifiers.fiery.craft")) { return; }
        ItemGenerator.createModifierItem(p, this, "Fiery");
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(config, modManager, ModifierType.FIERY, "Fiery", "Modifier_Fiery");
    }
}

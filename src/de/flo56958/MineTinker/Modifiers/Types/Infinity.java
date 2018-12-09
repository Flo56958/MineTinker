package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ModifierFailCause;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.ModifierFailEvent;
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

public class Infinity extends Modifier implements Enchantable, Craftable {

    private static final ModManager modManager = Main.getModManager();
    private static final PluginManager pluginManager = Bukkit.getPluginManager();
    private static final FileConfiguration config = Main.getMain().getConfigurations().getConfig("Infinity.yml");

    private final boolean compatibleWithEnder;

    public Infinity() {
        super(config.getString("Infinity.name"),
                "[" + config.getString("Infinity.name_modifier") + "] " + config.getString("Infinity.description"),
                ModifierType.INFINITY,
                ChatColor.WHITE,
                1,
                ItemGenerator.itemEnchanter(Material.ARROW, ChatColor.WHITE + config.getString("Infinity.name_modifier"), 1, Enchantment.ARROW_INFINITE, 1),
                new ArrayList<>(Collections.singletonList(ToolType.BOW)),
                Main.getPlugin());
        this.compatibleWithEnder = Main.getMain().getConfigurations().getConfig("Ender.yml").getBoolean("Ender.CompatibleWithInfinity");
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (!this.compatibleWithEnder) {
            if (modManager.get(ModifierType.ENDER) != null) {
                if (modManager.hasMod(tool, modManager.get(ModifierType.ENDER))) {
                    pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
                    return null;
                }
            }
        }
        if (Modifier.checkAndAdd(p, tool, this, "infinity", isCommand) == null) {
            return null;
        }

        ItemMeta meta = tool.getItemMeta();

        meta.addEnchant(Enchantment.ARROW_INFINITE, modManager.getModLevel(tool, this), true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        tool.setItemMeta(meta);

        return tool;
    }

    @Override
    public void enchantItem(Player p, ItemStack item) {
        if (!p.hasPermission("minetinker.modifiers.infinity.craft")) { return; }
        _createModifierItem(config, p, this, "Infinity");
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(config, this, "Infinity", "Modifier_Infinity");
    }
}

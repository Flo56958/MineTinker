package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ModifierFailCause;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.ModifierFailEvent;
import de.flo56958.MineTinker.Main;
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

public class SilkTouch extends Modifier implements Enchantable {

    private static final ModManager modManager = Main.getModManager();
    private static PluginManager pluginManager = Bukkit.getPluginManager();
    private static final FileConfiguration config = Main.getPlugin().getConfig();

    public SilkTouch() {
        super(config.getString("Modifiers.Silk-Touch.name"),
                "[Enchanted Cobweb] Applies Silk-Touch!",
                ModifierType.SILK_TOUCH,
                ChatColor.WHITE,
                1,
                ItemGenerator.itemEnchanter(Material.COBWEB, ChatColor.WHITE + config.getString("Modifiers.Silk-Touch.name_modifier"), 1, Enchantment.SILK_TOUCH, 1),
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.HOE, ToolType.PICKAXE, ToolType.SHOVEL)),
                Main.getPlugin());
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {

        if (modManager.get(ModifierType.AUTO_SMELT) != null) {
            if (modManager.hasMod(tool, modManager.get(ModifierType.AUTO_SMELT))) {
                pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
                return null;
            }
        }
        if (modManager.get(ModifierType.LUCK) != null) {
            if (modManager.hasMod(tool, modManager.get(ModifierType.LUCK))) {
                pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
                return null;
            }
        }
        if (Modifier.checkAndAdd(p, tool, this, "silktouch", isCommand) == null) {
            return null;
        }

        ItemMeta meta = tool.getItemMeta();

        meta.addEnchant(Enchantment.SILK_TOUCH, modManager.getModLevel(tool, this), true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        tool.setItemMeta(meta);

        return tool;
    }

    @Override
    public void enchantItem(Player p, ItemStack item) {
        if (!p.hasPermission("minetinker.modifiers.silktouch.craft")) { return; }
        ItemGenerator.createModifierItem(p, this, "Silk-Touch");
    }
}

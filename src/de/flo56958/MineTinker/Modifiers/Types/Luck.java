package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ModifierFailCause;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.ModifierFailEvent;
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

public class Luck extends Modifier implements Craftable {

    private static final ModManager modManager = Main.getModManager();
    private static final PluginManager pluginManager = Bukkit.getPluginManager();
    private static final FileConfiguration config = Main.getMain().getConfigurations().getConfig("Luck.yml");

    public Luck() {
        super(config.getString("Luck.name"),
                "[Compressed Lapis Block] Get more loot from enemies and blocks!",
                ModifierType.LUCK,
                ChatColor.BLUE,
                config.getInt("Luck.MaxLevel"),
                ItemGenerator.itemEnchanter(Material.LAPIS_BLOCK, ChatColor.BLUE + config.getString("Luck.name_modifier"), 1, Enchantment.LOOT_BONUS_BLOCKS, 1),
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.HOE, ToolType.PICKAXE, ToolType.SHOVEL, ToolType.SWORD)),
                Main.getPlugin());
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (modManager.get(ModifierType.SILK_TOUCH) != null) {
            if (modManager.hasMod(tool, modManager.get(ModifierType.SILK_TOUCH))) {
                pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
                return null;
            }
        }
        if (Modifier.checkAndAdd(p, tool, this, "luck", isCommand) == null) {
            return null;
        }

        ItemMeta meta = tool.getItemMeta();

        if (ToolType.AXE.getMaterials().contains(tool.getType())) {
            meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, modManager.getModLevel(tool, this), true);
            meta.addEnchant(Enchantment.LOOT_BONUS_MOBS, modManager.getModLevel(tool, this), true);
        } else if (ToolType.BOW.getMaterials().contains(tool.getType())) {
            meta.addEnchant(Enchantment.LOOT_BONUS_MOBS, modManager.getModLevel(tool, this), true);
        } else if (ToolType.HOE.getMaterials().contains(tool.getType())) {
            meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, modManager.getModLevel(tool, this), true);
        } else if (ToolType.PICKAXE.getMaterials().contains(tool.getType())) {
            meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, modManager.getModLevel(tool, this), true);
        } else if (ToolType.SHOVEL.getMaterials().contains(tool.getType())) {
            meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, modManager.getModLevel(tool, this), true);
        } else if (ToolType.SWORD.getMaterials().contains(tool.getType())) {
            meta.addEnchant(Enchantment.LOOT_BONUS_MOBS, modManager.getModLevel(tool, this), true);
        }
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        tool.setItemMeta(meta);

        return tool;
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(config, modManager, ModifierType.LUCK, "Luck", "Modifier_Luck");
    }
}

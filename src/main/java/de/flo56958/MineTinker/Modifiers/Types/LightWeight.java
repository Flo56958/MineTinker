package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Craftable;
import de.flo56958.MineTinker.Modifiers.Enchantable;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import de.flo56958.MineTinker.Utilities.modifiers_Config;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;

public class LightWeight extends Modifier implements Enchantable, Craftable {

    public LightWeight() {
        super(ModifierType.LIGHT_WEIGHT,
                ChatColor.GRAY,
                new ArrayList<>(Collections.singletonList(ToolType.BOOTS)),
                Main.getPlugin());
    }
    
    public void reload() {
    	FileConfiguration config = getConfig();
        
        init(config.getString("Light-Weight.name"),
                "[" + config.getString("Light-Weight.name_modifier") + "] " + config.getString("Light-Weight.description"),
                config.getInt("Light-Weight.MaxLevel"),
                ItemGenerator.itemEnchanter(Material.FEATHER, ChatColor.GRAY + config.getString("Light-Weight.name_modifier"), 1, Enchantment.DURABILITY, 1));
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (Modifier.checkAndAdd(p, tool, this, "lightweight", isCommand) == null) {
            return null;
        }

        ItemMeta meta = tool.getItemMeta();

        meta.addEnchant(Enchantment.PROTECTION_FALL, modManager.getModLevel(tool, this), true);
        if (Main.getPlugin().getConfig().getBoolean("HideEnchants")) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        } else {
            meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        tool.setItemMeta(meta);

        return tool;
    }

    @Override
    public void enchantItem(Player p, ItemStack item) {
        if (!p.hasPermission("minetinker.modifiers.lightweight.craft")) { return; }
        _createModifierItem(getConfig(), p, this, "Light-Weight");
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Light-Weight", "Modifier_LightWeight");
    }
    
    private static FileConfiguration getConfig() {
    	return Main.getConfigurations().getConfig(modifiers_Config.Light_Weight);
    }
}

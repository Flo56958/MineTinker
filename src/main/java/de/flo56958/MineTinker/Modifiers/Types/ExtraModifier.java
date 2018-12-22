package de.flo56958.MineTinker.Modifiers.Types;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Data.ModifierFailCause;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.ModifierFailEvent;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.modifiers_Config;

public class ExtraModifier extends Modifier {

    private final int gain;

    public ExtraModifier() {
        super(ModifierType.EXTRA_MODIFIER,
                ChatColor.WHITE,
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.HOE, ToolType.PICKAXE, ToolType.SHOVEL, ToolType.SWORD,
                                                ToolType.HELMET, ToolType.CHESTPLATE, ToolType.LEGGINGS, ToolType.BOOTS, ToolType.ELYTRA)),
                Main.getPlugin());
        
        FileConfiguration config = getConfig();
        
        init(config.getString("Extra-Modifier.name"),
                "[" + config.getString("Extra-Modifier.modifier_item")+ "] " + config.getString("Extra-Modifier.description"),
                -1,
                new ItemStack(Material.getMaterial(config.getString("Extra-Modifier.modifier_item")), 1));
        
        this.gain = config.getInt("Extra-Modifier.ExtraModifierGain");
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (!p.hasPermission("minetinker.modifiers.extramodifier.apply")) {
            pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.NO_PERMISSION, isCommand));
            return null;
        }
        if (!getAllowedTools().contains(ToolType.get(tool.getType()))) {
            pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INVALID_TOOLTYPE, isCommand));
            return null;
        }

        int slotsRemaining = modManager.getFreeSlots(tool);
        if (slotsRemaining + gain == Integer.MAX_VALUE || slotsRemaining + gain < 0) {
            pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.MAXIMUM_SLOTS_REACHED, isCommand));
            return null;
        }
        int amount = slotsRemaining + gain;

        modManager.setFreeSlots(tool, amount);
        return tool;
    }
    
    private static FileConfiguration getConfig() {
    	return Main.getConfigurations().getConfig(modifiers_Config.Extra_Modifier);
    }
}

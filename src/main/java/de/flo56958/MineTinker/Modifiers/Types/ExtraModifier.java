package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ModifierFailCause;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.ModifierFailEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import de.flo56958.MineTinker.Utilities.Modifiers_Config;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

public class ExtraModifier extends Modifier {

    private int gain;

    public ExtraModifier() {
        super(ModifierType.EXTRA_MODIFIER,
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.HOE, ToolType.PICKAXE, ToolType.SHOVEL, ToolType.SWORD, ToolType.TRIDENT,
                                                ToolType.HELMET, ToolType.CHESTPLATE, ToolType.LEGGINGS, ToolType.BOOTS, ToolType.ELYTRA)),
                Main.getPlugin());
    }

    @Override
    public void reload() {
    	FileConfiguration config = getConfig();
    	config.options().copyDefaults(true);
    	
    	String key = "Extra-Modifier";
    	config.addDefault(key + ".allowed", true);
    	config.addDefault(key + ".name", key);
    	config.addDefault(key + ".description", "Adds a additional Modifiers-Slot to the tool!");
        config.addDefault(key + ".Color", "%WHITE%");
        config.addDefault(key + ".ExtraModifierGain", 1); //How much Slots should be added per Nether-Star
    	config.addDefault(key + ".modifier_item", "NETHER_STAR"); //Needs to be a viable Material-Type
        
    	ConfigurationManager.saveConfig(config);
    	
        init(config.getString(key + ".name"),
                "[" + config.getString(key + ".modifier_item")+ "] " + config.getString(key + ".description"),
                ChatWriter.getColor(config.getString(key + ".Color")),
                -1,
                new ItemStack(Material.getMaterial(config.getString(key + ".modifier_item")), 1));
        
        this.gain = config.getInt(key + ".ExtraModifierGain");
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

    @Override
    public void removeMod(ItemStack tool) { }

    private static FileConfiguration getConfig() {
    	return ConfigurationManager.getConfig(Modifiers_Config.Extra_Modifier);
    }

    @Override
    public boolean isAllowed() {
    	return getConfig().getBoolean("Extra-Modifier.allowed");
    }
}

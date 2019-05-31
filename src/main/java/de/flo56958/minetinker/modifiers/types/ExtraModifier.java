package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.data.ModifierFailCause;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.events.ModifierFailEvent;
import de.flo56958.minetinker.Main;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utilities.ChatWriter;
import de.flo56958.minetinker.utilities.ConfigurationManager;
import de.flo56958.minetinker.utilities.Modifiers_Config;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExtraModifier extends Modifier {

    private int gain;

    private static ExtraModifier instance;

    public static ExtraModifier instance() {
        synchronized (ExtraModifier.class) {
            if (instance == null) instance = new ExtraModifier();
        }
        return instance;
    }

    private ExtraModifier() {
        super(ModifierType.EXTRA_MODIFIER,
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.HOE, ToolType.PICKAXE, ToolType.SHEARS, ToolType.FISHINGROD,
                                                ToolType.SHOVEL, ToolType.SWORD, ToolType.TRIDENT,
                                                ToolType.HELMET, ToolType.CHESTPLATE, ToolType.LEGGINGS, ToolType.BOOTS, ToolType.ELYTRA)),
                Main.getPlugin());
    }

    @Override
    public List<Enchantment> getAppliedEnchantments() {
        return new ArrayList<>();
    }

    @Override
    public void reload() {
    	FileConfiguration config = getConfig();
    	config.options().copyDefaults(true);
    	
    	String key = "Extra-Modifier";
    	config.addDefault(key + ".allowed", true);
    	config.addDefault(key + ".name", key);
        config.addDefault(key + ".modifier_item", "NETHER_STAR"); //Needs to be a viable Material-Type
        config.addDefault(key + ".description", "Adds a additional modifiers-Slot to the tool!");
        config.addDefault(key + ".Color", "%WHITE%");
        config.addDefault(key + ".ExtraModifierGain", 1); //How much Slots should be added per Nether-Star

    	ConfigurationManager.saveConfig(config);

        String name = config.getString(key + ".name");
        String description = config.getString(key + ".description");
        String color = config.getString(key + ".Color");
        String modItem = config.getString(key + ".modifier_item");

        if (name == null || description == null || color == null || modItem == null) return;

        Material item = Material.getMaterial(modItem);

        if (item == null) return;

        init(name, "[" + modItem + "] " + description, ChatWriter.getColor(color), -1, new ItemStack(item, 1));
        
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

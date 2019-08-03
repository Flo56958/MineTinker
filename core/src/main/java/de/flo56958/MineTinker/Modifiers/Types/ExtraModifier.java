package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ModifierFailCause;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.ModifierFailEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
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
        super("Extra-Modifier", "Extra-Modifier.yml",
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.CROSSBOW, ToolType.HOE, ToolType.PICKAXE, ToolType.SHEARS, ToolType.FISHINGROD,
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
        config.addDefault(key + ".description", "Adds a additional Modifiers-Slot to the tool!");
        config.addDefault(key + ".Color", "%WHITE%");
        config.addDefault(key + ".ExtraModifierGain", 1); //How much Slots should be added per Nether-Star

    	ConfigurationManager.saveConfig(config);
        ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

        init(config.getString(key + ".name"),
                "[" + config.getString(key + ".modifier_item")+ "] \u200B" + config.getString(key + ".description"),
                ChatWriter.getColor(config.getString(key + ".Color")),
                -1,
                new ItemStack(Material.getMaterial(config.getString(key + ".modifier_item")), 1));
        
        this.gain = config.getInt(key + ".ExtraModifierGain");
    }

    @Override
    public boolean applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (!p.hasPermission("minetinker.modifiers.extramodifier.apply")) {
            pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.NO_PERMISSION, isCommand));
            return false;
        }

        if (!getAllowedTools().contains(ToolType.get(tool.getType()))) {
            pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INVALID_TOOLTYPE, isCommand));
            return false;
        }

        int slotsRemaining = modManager.getFreeSlots(tool);

        if (slotsRemaining + gain == Integer.MAX_VALUE || slotsRemaining + gain < 0) {
            pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.MAXIMUM_SLOTS_REACHED, isCommand));
            return false;
        }
        int amount = slotsRemaining + gain;

        modManager.setFreeSlots(tool, amount);

        return true;
    }

    @Override
    public void registerCraftingRecipe() {
        // no recipe
    }

    @Override
    public boolean isAllowed() {
    	return getConfig().getBoolean("Extra-Modifier.allowed");
    }
}

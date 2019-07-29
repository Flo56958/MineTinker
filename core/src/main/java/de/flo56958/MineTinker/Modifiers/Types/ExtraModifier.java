package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ModifierFailCause;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.ModifierFailEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Modifier;
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

    	config.addDefault("Allowed", true);
    	config.addDefault("Name", "Extra-Modifier");
        config.addDefault("ModifierItem", "NETHER_STAR"); //Needs to be a viable Material-Type
        config.addDefault("Description", "Adds a additional Modifiers-Slot to the tool!");
        config.addDefault("Color", "%WHITE%");
        config.addDefault("ExtraModifierGain", 1); //How much Slots should be added per Nether-Star

    	ConfigurationManager.saveConfig(config);
        ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

        init(Material.getMaterial(config.getString("ModifierItem", "NETHER_STAR")), false);
        
        this.gain = config.getInt("ExtraModifierGain");
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
    public void removeMod(ItemStack tool) { }

    @Override
    public void registerCraftingRecipe() {
        // no recipe
    }
}

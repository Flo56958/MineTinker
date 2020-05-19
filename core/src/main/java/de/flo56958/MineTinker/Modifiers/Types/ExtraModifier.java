package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ModifierFailCause;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.ModifierFailEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExtraModifier extends Modifier {

	private static ExtraModifier instance;
	private int gain;

	private ExtraModifier() {
		super(Main.getPlugin());
		this.customModelData = 10_047;
	}

	public static ExtraModifier instance() {
		synchronized (ExtraModifier.class) {
			if (instance == null) {
				instance = new ExtraModifier();
			}
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Extra-Modifier";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Collections.singletonList(ToolType.ALL);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Name", "Extra-Modifier");
		config.addDefault("ModifierItemName", "Refined Netherstar");
		config.addDefault("Description", "Adds a additional Modifiers-Slot to the tool!");
		config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Extra-Modifier");
		config.addDefault("Color", "%WHITE%");
		config.addDefault("ExtraModifierGain", 1); //How much Slots should be added per Nether-Star
		config.addDefault("OverrideLanguagesystem", false);

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", "   ");
		config.addDefault("Recipe.Middle", " N ");
		config.addDefault("Recipe.Bottom", "   ");

		Map<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("N", Material.NETHER_STAR.name());

		config.addDefault("Recipe.Materials", recipeMaterials);
		config.addDefault("OverrideLanguagesystem", false);


		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init(Material.NETHER_STAR);

		this.slotCost = 0;

		this.gain = config.getInt("ExtraModifierGain", 1);
		this.description = this.description.replace("%amount", String.valueOf(this.gain));
	}

	@Override
	public boolean applyMod(Player player, ItemStack tool, boolean isCommand) {
		if (!player.hasPermission("minetinker.modifiers.extramodifier.apply")) {
			pluginManager.callEvent(new ModifierFailEvent(player, tool, this, ModifierFailCause.NO_PERMISSION, isCommand));
			return false;
		}

		if (!isMaterialCompatible(tool.getType())) {
			pluginManager.callEvent(new ModifierFailEvent(player, tool, this, ModifierFailCause.INVALID_TOOLTYPE, isCommand));
			return false;
		}

		int slotsRemaining = modManager.getFreeSlots(tool);

		if (slotsRemaining + gain == Integer.MAX_VALUE || slotsRemaining + gain < 0) {
			pluginManager.callEvent(new ModifierFailEvent(player, tool, this, ModifierFailCause.MAXIMUM_SLOTS_REACHED, isCommand));
			return false;
		}

		int amount = slotsRemaining + gain;

		modManager.setFreeSlots(tool, amount);

		return true;
	}
}

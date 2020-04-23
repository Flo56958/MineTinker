package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class LightWeight extends Modifier {

	private static LightWeight instance;

	private LightWeight() {
		super(Main.getPlugin());
		customModelData = 10_019;
	}

	public static LightWeight instance() {
		synchronized (LightWeight.class) {
			if (instance == null) {
				instance = new LightWeight();
			}
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Light-Weight";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Collections.singletonList(ToolType.BOOTS);
	}

	@Override
	public List<Enchantment> getAppliedEnchantments() {
		return Collections.singletonList(Enchantment.PROTECTION_FALL);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Name", "Light-Weight");
		config.addDefault("ModifierItemName", "Enhanced Feather");
		config.addDefault("Description", "You fall like a feather - sort of...");
		config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Light-Weight-Modifier");
		config.addDefault("Color", "%GRAY%");
		config.addDefault("MaxLevel", 4);

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", true);

		config.addDefault("Recipe.Enabled", false);
		config.addDefault("OverrideLanguagesystem", false);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init(Material.FEATHER, true);
	}

	@Override
	public boolean applyMod(Player player, ItemStack tool, boolean isCommand) {
		ItemMeta meta = tool.getItemMeta();

		if (meta != null) {
			meta.addEnchant(Enchantment.PROTECTION_FALL, modManager.getModLevel(tool, this), true);

			tool.setItemMeta(meta);
		}

		return true;
	}
}

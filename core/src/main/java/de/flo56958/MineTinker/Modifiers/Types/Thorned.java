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
import java.util.*;

public class Thorned extends Modifier {

	private static Thorned instance;

	private Thorned() {
		super(Main.getPlugin());
		customModelData = 10_041;
	}

	public static Thorned instance() {
		synchronized (Thorned.class) {
			if (instance == null) {
				instance = new Thorned();
			}
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Thorned";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Arrays.asList(ToolType.HELMET, ToolType.CHESTPLATE, ToolType.LEGGINGS, ToolType.BOOTS);
	}

	@Override
	public List<Enchantment> getAppliedEnchantments() {
		return Collections.singletonList(Enchantment.THORNS);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Name", "Thorned");
		config.addDefault("ModifierItemName", "Spiked Plating");
		config.addDefault("Description", "Your armor harms others when they damage you!");
		config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Thorned-Modifier");
		config.addDefault("Color", "%DARK_GREEN%");
		config.addDefault("MaxLevel", 3);
		config.addDefault("OverrideLanguagesystem", false);

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", "VAV");
		config.addDefault("Recipe.Middle", "ASA");
		config.addDefault("Recipe.Bottom", "VAV");

		Map<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("V", Material.VINE.name());
		recipeMaterials.put("A", Material.ARROW.name());
		recipeMaterials.put("S", Material.SLIME_BALL.name());

		config.addDefault("Recipe.Materials", recipeMaterials);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init(Material.VINE, true);
	}

	@Override
	public boolean applyMod(Player player, ItemStack tool, boolean isCommand) {
		ItemMeta meta = tool.getItemMeta();

		if (meta != null) {
			meta.addEnchant(Enchantment.THORNS, modManager.getModLevel(tool, this), true);

			tool.setItemMeta(meta);
		}

		return true;
	}
}

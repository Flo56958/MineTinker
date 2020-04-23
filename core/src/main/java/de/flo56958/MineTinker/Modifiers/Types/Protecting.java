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

public class Protecting extends Modifier {

	private static Protecting instance;

	private Protecting() {
		super(Main.getPlugin());
		customModelData = 10_029;
	}

	public static Protecting instance() {
		synchronized (Protecting.class) {
			if (instance == null) {
				instance = new Protecting();
			}
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Protecting";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Arrays.asList(ToolType.HELMET, ToolType.CHESTPLATE, ToolType.LEGGINGS, ToolType.BOOTS, ToolType.ELYTRA);
	}

	@Override
	public List<Enchantment> getAppliedEnchantments() {
		return Collections.singletonList(Enchantment.PROTECTION_ENVIRONMENTAL);

	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Name", "Protecting");
		config.addDefault("ModifierItemName", "Enriched Obsidian");
		config.addDefault("Description", "Your armor protects you better against all damage!");
		config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Protecting-Modifier");
		config.addDefault("Color", "%GRAY%");
		config.addDefault("MaxLevel", 4);
		config.addDefault("OverrideLanguagesystem", false);

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", true);

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", "DID");
		config.addDefault("Recipe.Middle", "IOI");
		config.addDefault("Recipe.Bottom", "DID");

		Map<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("D", Material.DIAMOND.name());
		recipeMaterials.put("I", Material.IRON_INGOT.name());
		recipeMaterials.put("O", Material.OBSIDIAN.name());

		config.addDefault("Recipe.Materials", recipeMaterials);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init(Material.OBSIDIAN, true);
	}

	@Override
	public boolean applyMod(Player player, ItemStack tool, boolean isCommand) {
		ItemMeta meta = tool.getItemMeta();

		if (meta != null) {
			meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, modManager.getModLevel(tool, this), true);

			tool.setItemMeta(meta);
		}

		return true;
	}
}

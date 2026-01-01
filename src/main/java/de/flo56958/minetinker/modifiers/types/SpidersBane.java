package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ConfigurationManager;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class SpidersBane extends Modifier {

	private static SpidersBane instance;

	private SpidersBane() {
		super(MineTinker.getPlugin());
		customModelData = 10_038;
	}

	public static SpidersBane instance() {
		synchronized (SpidersBane.class) {
			if (instance == null)
				instance = new SpidersBane();
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Spiders-Bane";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Arrays.asList(ToolType.AXE, ToolType.SWORD, ToolType.MACE, ToolType.SPEAR, ToolType.TRIDENT);
	}

	@Override
	public @NotNull List<Enchantment> getAppliedEnchantments() {
		return Collections.singletonList(Enchantment.BANE_OF_ARTHROPODS);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Color", "%RED%");
		config.addDefault("MaxLevel", 5);
		config.addDefault("SlotCost", 1);
		config.addDefault("ModifierItemMaterial", Material.FERMENTED_SPIDER_EYE.name());

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);
		config.addDefault("MinimumToolLevelRequirement", 1);

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", "ESE");
		config.addDefault("Recipe.Middle", "SFS");
		config.addDefault("Recipe.Bottom", "ESE");

		Map<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("E", Material.SPIDER_EYE.name());
		recipeMaterials.put("S", Material.STRING.name());
		recipeMaterials.put("F", Material.FERMENTED_SPIDER_EYE.name());

		config.addDefault("Recipe.Materials", recipeMaterials);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init();
	}

	@Override
	public boolean applyMod(Player player, ItemStack tool, boolean isCommand) {
		ItemMeta meta = tool.getItemMeta();

		if (meta != null) {
			meta.addEnchant(Enchantment.BANE_OF_ARTHROPODS, modManager.getModLevel(tool, this), true);
			tool.setItemMeta(meta);
		} else return false;

		return true;
	}
}

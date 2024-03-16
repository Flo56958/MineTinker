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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SwiftSneaking extends Modifier {

	private static SwiftSneaking instance;

	private SwiftSneaking() {
		super(MineTinker.getPlugin());
		customModelData = 10_062;
	}

	public static SwiftSneaking instance() {
		synchronized (SwiftSneaking.class) {
			if (instance == null)
				instance = new SwiftSneaking();
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Swift-Sneaking";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Collections.singletonList(ToolType.LEGGINGS);
	}

	@Override
	public @NotNull List<Enchantment> getAppliedEnchantments() {
		return Collections.singletonList(Enchantment.SWIFT_SNEAK);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Color", "%GRAY%");
		config.addDefault("MaxLevel", 3);
		config.addDefault("SlotCost", 1);
		config.addDefault("ModifierItemMaterial", Material.FEATHER.name());

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);
		config.addDefault("MinimumToolLevelRequirement", 1);

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", "BSB");
		config.addDefault("Recipe.Middle", "DFD");
		config.addDefault("Recipe.Bottom", "BSB");

		Map<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("B", Material.BLUE_ICE.name());
		recipeMaterials.put("S", Material.SOUL_SAND.name());
		recipeMaterials.put("D", Material.DIAMOND.name());
		recipeMaterials.put("F", Material.FEATHER.name());

		config.addDefault("Recipe.Materials", recipeMaterials);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init();
	}

	@Override
	public boolean applyMod(Player player, @NotNull ItemStack tool, boolean isCommand) {
		ItemMeta meta = tool.getItemMeta();

		if (meta != null) {
			meta.addEnchant(Enchantment.SWIFT_SNEAK, modManager.getModLevel(tool, this), true);
			tool.setItemMeta(meta);
		}

		return true;
	}
}

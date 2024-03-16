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

public class SoulSpeed extends Modifier {

	private static SoulSpeed instance;

	private SoulSpeed() {
		super(MineTinker.getPlugin());
		customModelData = 10_054;
	}

	public static SoulSpeed instance() {
		synchronized (SoulSpeed.class) {
			if (instance == null)
				instance = new SoulSpeed();
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Soul-Speed";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Collections.singletonList(ToolType.BOOTS);
	}

	@Override
	public @NotNull List<Enchantment> getAppliedEnchantments() {
		return Collections.singletonList(Enchantment.SOUL_SPEED);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Color", "%GRAY%");
		config.addDefault("MaxLevel", 3);
		config.addDefault("SlotCost", 1);
		config.addDefault("ModifierItemMaterial", Material.SOUL_SAND.name());

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);
		config.addDefault("MinimumToolLevelRequirement", 1);

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", "GSG");
		config.addDefault("Recipe.Middle", "SAS");
		config.addDefault("Recipe.Bottom", "GSG");

		Map<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("S", Material.SOUL_SAND.name());
		recipeMaterials.put("G", Material.GOLD_BLOCK.name());
		recipeMaterials.put("A", Material.ANCIENT_DEBRIS.name());

		config.addDefault("Recipe.Materials", recipeMaterials);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init();
	}

	@Override
	public boolean applyMod(Player player, ItemStack tool, boolean isCommand) {
		ItemMeta meta = tool.getItemMeta();

		if (meta != null) {
			meta.addEnchant(Enchantment.SOUL_SPEED, modManager.getModLevel(tool, this), true);
			tool.setItemMeta(meta);
		}

		return true;
	}
}

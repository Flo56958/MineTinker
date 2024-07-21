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
import java.util.List;

public class Dense extends Modifier {

	private static Dense instance;

	private Dense() {
		super(MineTinker.getPlugin());
		customModelData = 10_070;
	}

	public static Dense instance() {
		synchronized (Dense.class) {
			if (instance == null)
				instance = new Dense();
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Dense";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Collections.singletonList(ToolType.MACE);
	}

	@Override
	public @NotNull List<Enchantment> getAppliedEnchantments() {
		return Collections.singletonList(Enchantment.DENSITY);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Color", "%DARK_GRAY%");
		config.addDefault("MaxLevel", 5);
		config.addDefault("SlotCost", 1);
		config.addDefault("ModifierItemMaterial", Material.DEEPSLATE.name());

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", true);
		config.addDefault("MinimumToolLevelRequirement", 1);

		config.addDefault("Recipe.Enabled", false);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init();
	}

	@Override
	public boolean applyMod(Player player, ItemStack tool, boolean isCommand) {
		ItemMeta meta = tool.getItemMeta();

		if (meta != null) {
			meta.addEnchant(Enchantment.DENSITY, modManager.getModLevel(tool, this), true);
			tool.setItemMeta(meta);
		} else return false;

		return true;
	}
}

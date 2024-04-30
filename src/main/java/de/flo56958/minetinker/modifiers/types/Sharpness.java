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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sharpness extends Modifier {

	private static Sharpness instance;

	private boolean allowAxes;

	private Sharpness() {
		super(MineTinker.getPlugin());
		customModelData = 10_032;
	}

	public static Sharpness instance() {
		synchronized (Sharpness.class) {
			if (instance == null)
				instance = new Sharpness();
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Sharpness";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		if (allowAxes)
			return Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.CROSSBOW, ToolType.SWORD, ToolType.TRIDENT);
		return Arrays.asList(ToolType.BOW, ToolType.CROSSBOW, ToolType.SWORD, ToolType.TRIDENT);
	}

	@Override
	public @NotNull List<Enchantment> getAppliedEnchantments() {
		return Arrays.asList(Enchantment.SHARPNESS, Enchantment.POWER, Enchantment.IMPALING);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Color", "%WHITE%");
		config.addDefault("MaxLevel", 5);
		config.addDefault("SlotCost", 1);
		config.addDefault("ModifierItemMaterial", Material.QUARTZ_BLOCK.name());
		config.addDefault("AllowAxes", true);

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);
		config.addDefault("MinimumToolLevelRequirement", 1);

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", "QQQ");
		config.addDefault("Recipe.Middle", "QOQ");
		config.addDefault("Recipe.Bottom", "QQQ");

		Map<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("Q", Material.QUARTZ_BLOCK.name());
		recipeMaterials.put("O", Material.NETHER_QUARTZ_ORE.name());

		config.addDefault("Recipe.Materials", recipeMaterials);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init();

		this.allowAxes = config.getBoolean("AllowAxes", true);
	}

	@Override
	public boolean applyMod(Player player, ItemStack tool, boolean isCommand) {
		ItemMeta meta = tool.getItemMeta();

		if (meta != null) {
			if (ToolType.AXE.contains(tool.getType()) || ToolType.SWORD.contains(tool.getType())) {
				meta.addEnchant(Enchantment.SHARPNESS, modManager.getModLevel(tool, this), true);
			} else if (ToolType.BOW.contains(tool.getType()) || ToolType.CROSSBOW.contains(tool.getType())) {
				meta.addEnchant(Enchantment.POWER, modManager.getModLevel(tool, this), true);
			} else if (ToolType.TRIDENT.contains(tool.getType())) {
				meta.addEnchant(Enchantment.SHARPNESS, modManager.getModLevel(tool, this), true);
				meta.addEnchant(Enchantment.IMPALING, modManager.getModLevel(tool, this), true);
			}

			tool.setItemMeta(meta);
		}

		return true;
	}
}

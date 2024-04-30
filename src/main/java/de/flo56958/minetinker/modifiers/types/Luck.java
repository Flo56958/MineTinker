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

public class Luck extends Modifier {

	private static final EnumMap<ToolType, List<Enchantment>> applicableEnchants = new EnumMap<>(ToolType.class);

	private static Luck instance;

	static {
		applicableEnchants.put(ToolType.AXE, Arrays.asList(Enchantment.FORTUNE, Enchantment.LOOTING));
		applicableEnchants.put(ToolType.BOW, Collections.singletonList(Enchantment.LOOTING));
		applicableEnchants.put(ToolType.CROSSBOW, Collections.singletonList(Enchantment.LOOTING));
		applicableEnchants.put(ToolType.HOE, Collections.singletonList(Enchantment.FORTUNE));
		applicableEnchants.put(ToolType.TRIDENT, Collections.singletonList(Enchantment.LOOTING));
		applicableEnchants.put(ToolType.PICKAXE, Collections.singletonList(Enchantment.FORTUNE));
		applicableEnchants.put(ToolType.SHOVEL, Collections.singletonList(Enchantment.FORTUNE));
		applicableEnchants.put(ToolType.SWORD, Collections.singletonList(Enchantment.LOOTING));
		applicableEnchants.put(ToolType.SHEARS, Collections.singletonList(Enchantment.FORTUNE));
		applicableEnchants.put(ToolType.FISHINGROD, Collections.singletonList(Enchantment.LUCK_OF_THE_SEA));
	}

	private Luck() {
		super(MineTinker.getPlugin());
		customModelData = 10_020;
	}

	public static Luck instance() {
		synchronized (Luck.class) {
			if (instance == null)
				instance = new Luck();
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Luck";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.CROSSBOW, ToolType.HOE, ToolType.PICKAXE, ToolType.SHEARS,
				ToolType.FISHINGROD, ToolType.SHOVEL, ToolType.SWORD, ToolType.TRIDENT);
	}

	@Override
	public @NotNull List<Enchantment> getAppliedEnchantments() {
		return Arrays.asList(Enchantment.FORTUNE, Enchantment.LOOTING, Enchantment.LUCK_OF_THE_SEA);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Color", "%BLUE%");
		config.addDefault("MaxLevel", 3);
		config.addDefault("SlotCost", 1);
		config.addDefault("ModifierItemMaterial", Material.LAPIS_BLOCK.name());

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);
		config.addDefault("MinimumToolLevelRequirement", 1);

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", "GLG");
		config.addDefault("Recipe.Middle", "LLL");
		config.addDefault("Recipe.Bottom", "GLG");

		Map<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("L", Material.LAPIS_BLOCK.name());
		recipeMaterials.put("G", Material.GOLD_BLOCK.name());

		config.addDefault("Recipe.Materials", recipeMaterials);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init();
	}

	@Override
	public boolean applyMod(Player player, ItemStack tool, boolean isCommand) {
		ItemMeta meta = tool.getItemMeta();

		if (meta != null) {
			for (final Enchantment enchantment : applicableEnchants.get(ToolType.get(tool.getType())))
				meta.addEnchant(enchantment, modManager.getModLevel(tool, this), true);

			tool.setItemMeta(meta);
		}

		return true;
	}
}

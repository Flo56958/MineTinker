package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ConfigurationManager;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Channeling extends Modifier {

	private static Channeling instance;

	private Channeling() {
		super(MineTinker.getPlugin());
		customModelData = 10_007;
	}

	public static Channeling instance() {
		synchronized (Channeling.class) {
			if (instance == null) {
				instance = new Channeling();
			}
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Channeling";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Collections.singletonList(ToolType.TRIDENT);
	}

	@Override
	public List<Enchantment> getAppliedEnchantments() {
		return Collections.singletonList(Enchantment.CHANNELING);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Name", "Channeling");
		config.addDefault("ModifierItemName", "Lightning Infused Shard");
		config.addDefault("Description", "Summons lightning when weapon is thrown at mobs!");
		config.addDefault("DescriptionModifierItem", "%GRAY%Modifier-Item for the Channeling-Modifier");
		config.addDefault("Color", "%GRAY%");
		config.addDefault("MaxLevel", 1);
		config.addDefault("SlotCost", 1);
		config.addDefault("OverrideLanguagesystem", false);

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", "SPS");
		config.addDefault("Recipe.Middle", "PCP");
		config.addDefault("Recipe.Bottom", "SPS");

		Map<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("S", Material.SEA_LANTERN.name());
		recipeMaterials.put("P", Material.PRISMARINE_SHARD.name());
		recipeMaterials.put("C", Material.CREEPER_HEAD.name());

		config.addDefault("Recipe.Materials", recipeMaterials);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init(Material.PRISMARINE_SHARD);
	}

	@Override
	public boolean applyMod(Player player, ItemStack tool, boolean isCommand) {
		ItemMeta meta = tool.getItemMeta();

		if (meta != null) {
			if (ToolType.TRIDENT.contains(tool.getType())) {
				meta.addEnchant(Enchantment.CHANNELING, modManager.getModLevel(tool, this), true);
			}

			tool.setItemMeta(meta);
		}

		return true;
	}
}

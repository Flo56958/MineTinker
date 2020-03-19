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

public class Aquaphilic extends Modifier {

	private static Aquaphilic instance;

	private Aquaphilic() {
		super(Main.getPlugin());
		customModelData = 10_003;
	}

	public static Aquaphilic instance() {
		synchronized (Aquaphilic.class) {
			if (instance == null) {
				instance = new Aquaphilic();
			}
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Aquaphilic";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return new ArrayList<>(Arrays.asList(ToolType.BOOTS, ToolType.HELMET));
	}

	@Override
	public List<Enchantment> getAppliedEnchantments() {
		return Arrays.asList(Enchantment.DEPTH_STRIDER, Enchantment.OXYGEN, Enchantment.WATER_WORKER);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Name", "Aquaphilic");
		config.addDefault("ModifierItemName", "Pearl of the ocean");
		config.addDefault("Description", "Make the water your friend");
		config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Aquaphilic-Modifier");
		config.addDefault("Color", "%AQUA%");
		config.addDefault("MaxLevel", 3); //higher will have no effect on depth strider
		config.addDefault("OverrideLanguagesystem", false);

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", "PNP");
		config.addDefault("Recipe.Middle", "NHN");
		config.addDefault("Recipe.Bottom", "PNP");

		Map<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("H", Material.HEART_OF_THE_SEA.name());
		recipeMaterials.put("N", Material.NAUTILUS_SHELL.name());
		recipeMaterials.put("P", Material.PRISMARINE_SHARD.name());

		config.addDefault("Recipe.Materials", recipeMaterials);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init(Material.HEART_OF_THE_SEA, true);
	}

	@Override
	public boolean applyMod(Player player, ItemStack tool, boolean isCommand) {
		ItemMeta meta = tool.getItemMeta();

		if (meta != null) {
			if (ToolType.BOOTS.contains(tool.getType())) {
				meta.addEnchant(Enchantment.DEPTH_STRIDER, modManager.getModLevel(tool, this), true);
			} else if (ToolType.HELMET.contains(tool.getType())) {
				meta.addEnchant(Enchantment.OXYGEN, modManager.getModLevel(tool, this), true);
				meta.addEnchant(Enchantment.WATER_WORKER, modManager.getModLevel(tool, this), true);
			}

			tool.setItemMeta(meta);
		}


		return true;
	}
}

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

public class Insulating extends Modifier {

	private static Insulating instance;

	private Insulating() {
		super(Main.getPlugin());
	}

	public static Insulating instance() {
		synchronized (Insulating.class) {
			if (instance == null) {
				instance = new Insulating();
			}
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Insulating";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Arrays.asList(ToolType.BOOTS, ToolType.LEGGINGS, ToolType.CHESTPLATE, ToolType.HELMET);
	}

	@Override
	public List<Enchantment> getAppliedEnchantments() {
		return Collections.singletonList(Enchantment.PROTECTION_FIRE);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Name", "Insulating");
		config.addDefault("ModifierItemName", "Heat Resistant Alloy");
		config.addDefault("Description", "Armor mitigates heat damage!");
		config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Anti-Fire-Plating-Modifier");
		config.addDefault("Color", "%WHITE%");
		config.addDefault("MaxLevel", 5);
		config.addDefault("OverrideLanguagesystem", false);

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", "IMI");
		config.addDefault("Recipe.Middle", "MDM");
		config.addDefault("Recipe.Bottom", "IMI");

		Map<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("I", Material.IRON_BLOCK.name());
		recipeMaterials.put("M", Material.MAGMA_BLOCK.name());
		recipeMaterials.put("D", Material.DIAMOND.name());

		config.addDefault("Recipe.Materials", recipeMaterials);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init(Material.MAGMA_CREAM, true);
	}

	@Override
	public boolean applyMod(Player p, ItemStack tool, boolean isCommand) {
		ItemMeta meta = tool.getItemMeta();

		if (meta != null) {
			if (ToolType.HELMET.contains(tool.getType()) || ToolType.CHESTPLATE.contains(tool.getType())
					|| ToolType.LEGGINGS.contains(tool.getType()) || ToolType.BOOTS.contains(tool.getType())) {

				meta.addEnchant(Enchantment.PROTECTION_FIRE, modManager.getModLevel(tool, this), true);
			}

			tool.setItemMeta(meta);
		}

		return true;
	}
}

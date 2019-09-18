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
import java.util.Arrays;
import java.util.List;

public class Fiery extends Modifier {

	//TODO: Add Particle effect
	private static Fiery instance;

	private Fiery() {
		super(Main.getPlugin());
	}

	public static Fiery instance() {
		synchronized (Fiery.class) {
			if (instance == null) {
				instance = new Fiery();
			}
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Fiery";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.SWORD);
	}

	@Override
	public List<Enchantment> getAppliedEnchantments() {
		return Arrays.asList(Enchantment.ARROW_FIRE, Enchantment.FIRE_ASPECT);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Name", "Fiery");
		config.addDefault("ModifierItemName", "Enchanted Blaze-Rod");
		config.addDefault("Description", "Inflames enemies!");
		config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Fiery-Modifier");
		config.addDefault("Color", "%YELLOW%");
		config.addDefault("MaxLevel", 2);

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", true);

		config.addDefault("Recipe.Enabled", false);
		config.addDefault("OverrideLanguagesystem", false);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init(Material.BLAZE_ROD, true);
	}

	@Override
	public boolean applyMod(Player p, ItemStack tool, boolean isCommand) {
		ItemMeta meta = tool.getItemMeta();

		if (meta != null) {
			if (ToolType.BOW.contains(tool.getType()) || ToolType.CROSSBOW.contains(tool.getType())) {
				meta.addEnchant(Enchantment.ARROW_FIRE, modManager.getModLevel(tool, this), true);
			} else if (ToolType.SWORD.contains(tool.getType())) {
				meta.addEnchant(Enchantment.FIRE_ASPECT, modManager.getModLevel(tool, this), true);
			}

			tool.setItemMeta(meta);
		}

		return true;
	}
}

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

public class Knockback extends Modifier {

	private static Knockback instance;

	private boolean worksOnShields = false;

	private Knockback() {
		super(Main.getPlugin());
		customModelData = 10_017;
	}

	public static Knockback instance() {
		synchronized (Knockback.class) {
			if (instance == null) {
				instance = new Knockback();
			}
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Knockback";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		if (worksOnShields) {
			return Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.SWORD, ToolType.SHIELD, ToolType.TRIDENT);
		} else {
			return Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.SWORD, ToolType.TRIDENT);
		}
	}

	@Override
	public List<Enchantment> getAppliedEnchantments() {
		return Arrays.asList(Enchantment.KNOCKBACK, Enchantment.ARROW_KNOCKBACK);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Name", "Knockback");
		config.addDefault("ModifierItemName", "Enchanted TNT");
		config.addDefault("Description", "Knock back Enemies further!");
		config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Knockback-Modifier");
		config.addDefault("Color", "%GRAY%");
		config.addDefault("MaxLevel", 5);

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", true);

		config.addDefault("Recipe.Enabled", false);
		config.addDefault("OverrideLanguagesystem", false);

		config.addDefault("WorksOnShields", false);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init(Material.TNT, true);

		this.worksOnShields = config.getBoolean("WorksOnShields");
	}

	@Override
	public boolean applyMod(Player player, ItemStack tool, boolean isCommand) {
		ItemMeta meta = tool.getItemMeta();

		if (meta != null) {
			if (ToolType.AXE.contains(tool.getType()) || ToolType.SWORD.contains(tool.getType()) || ToolType.TRIDENT.contains(tool.getType())) {
				meta.addEnchant(Enchantment.KNOCKBACK, modManager.getModLevel(tool, this), true);
			} else if (ToolType.SHIELD.contains(tool.getType()) && worksOnShields) {
				meta.addEnchant(Enchantment.KNOCKBACK, modManager.getModLevel(tool, this), true);
			} else if (ToolType.BOW.contains(tool.getType()) || ToolType.CROSSBOW.contains(tool.getType())) {
				meta.addEnchant(Enchantment.ARROW_KNOCKBACK, modManager.getModLevel(tool, this), true);
			}

			tool.setItemMeta(meta);
		}

		return true;
	}
}

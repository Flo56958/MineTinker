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
import java.util.Collections;
import java.util.List;

public class Sweeping extends Modifier {

	//TODO: Make active right-click ability to push entities away
	//has cooldown
	private static Sweeping instance;

	private Sweeping() {
		super(Main.getPlugin());
	}

	public static Sweeping instance() {
		synchronized (Sweeping.class) {
			if (instance == null) {
				instance = new Sweeping();
			}
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Sweeping";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Collections.singletonList(ToolType.SWORD);
	}

	@Override
	public List<Enchantment> getAppliedEnchantments() {
		return Collections.singletonList(Enchantment.SWEEPING_EDGE);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Name", "Sweeping");
		config.addDefault("ModifierItemName", "Enchanted Iron Ingot");
		config.addDefault("Description", "More damage over a greater area!");
		config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Sweeping-Modifier");
		config.addDefault("Color", "%RED%");
		config.addDefault("MaxLevel", 5);

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", true);

		config.addDefault("Recipe.Enabled", false);
		config.addDefault("OverrideLanguagesystem", false);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init(Material.IRON_INGOT, true);
	}

	@Override
	public boolean applyMod(Player p, ItemStack tool, boolean isCommand) {
		ItemMeta meta = tool.getItemMeta();

		if (meta != null) {
			meta.addEnchant(Enchantment.SWEEPING_EDGE, modManager.getModLevel(tool, this), true);

			tool.setItemMeta(meta);
		}

		return true;
	}
}

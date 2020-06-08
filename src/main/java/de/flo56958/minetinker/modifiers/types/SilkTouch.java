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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SilkTouch extends Modifier {

	private static SilkTouch instance;

	private SilkTouch() {
		super(MineTinker.getPlugin());
		customModelData = 10_034;
	}

	public static SilkTouch instance() {
		synchronized (SilkTouch.class) {
			if (instance == null) {
				instance = new SilkTouch();
			}
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Silk-Touch";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Arrays.asList(ToolType.AXE, ToolType.HOE, ToolType.PICKAXE, ToolType.SHOVEL, ToolType.SHEARS);
	}

	@Override
	public List<Enchantment> getAppliedEnchantments() {
		return Collections.singletonList(Enchantment.SILK_TOUCH);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Name", "Silk-Touch");
		config.addDefault("ModifierItemName", "Enhanced Cobweb");
		config.addDefault("Description", "Applies Silk-Touch!");
		config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Silk-Touch-Modifier");
		config.addDefault("Color", "%WHITE%");
		config.addDefault("MaxLevel", 1); //IF 2: Epic Spawners work with MT-SilkTouch
		config.addDefault("SlotCost", 1);

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", true);

		config.addDefault("Recipe.Enabled", false);
		config.addDefault("OverrideLanguagesystem", false);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init(Material.COBWEB);
	}

	@Override
	public boolean applyMod(Player player, ItemStack tool, boolean isCommand) {
		ItemMeta meta = tool.getItemMeta();

		if (meta != null) {
			meta.addEnchant(Enchantment.SILK_TOUCH, modManager.getModLevel(tool, this), true);

			tool.setItemMeta(meta);
		}

		return true;
	}
}

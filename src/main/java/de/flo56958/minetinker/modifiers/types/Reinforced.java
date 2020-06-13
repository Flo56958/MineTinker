package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ConfigurationManager;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Reinforced extends Modifier {

	private static Reinforced instance;
	private boolean applyUnbreakableOnMaxLevel;
	private boolean hideUnbreakableFlag;

	private Reinforced() {
		super(MineTinker.getPlugin());
		customModelData = 10_030;
	}

	public static Reinforced instance() {
		synchronized (Reinforced.class) {
			if (instance == null) {
				instance = new Reinforced();
			}
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Reinforced";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Collections.singletonList(ToolType.ALL);
	}

	@Override
	public List<Enchantment> getAppliedEnchantments() {
		return Collections.singletonList(Enchantment.DURABILITY);

	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Name", "Reinforced");
		config.addDefault("ModifierItemName", "Compressed Obsidian");
		config.addDefault("Description", "Chance to not use durability when using the tool/armor!");
		config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Reinforced-Modifier");
		config.addDefault("Color", "%DARK_GRAY%");
		config.addDefault("MaxLevel", 3);
		config.addDefault("SlotCost", 1);
		config.addDefault("ApplyUnbreakableOnMaxLevel", false);
		config.addDefault("HideUnbreakableFlag", true);
		config.addDefault("OverrideLanguagesystem", false);

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", "IOI");
		config.addDefault("Recipe.Middle", "ODO");
		config.addDefault("Recipe.Bottom", "IOI");

		Map<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("O", Material.OBSIDIAN.name());
		recipeMaterials.put("I", Material.IRON_INGOT.name());
		recipeMaterials.put("D", Material.DIAMOND.name());

		config.addDefault("Recipe.Materials", recipeMaterials);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		this.applyUnbreakableOnMaxLevel = config.getBoolean("ApplyUnbreakableOnMaxLevel", false);
		this.hideUnbreakableFlag = config.getBoolean("HideUnbreakableFlag", true);

		init(Material.OBSIDIAN);
	}

	@Override
	public boolean applyMod(Player player, ItemStack tool, boolean isCommand) {
		ItemMeta meta = tool.getItemMeta();

		if (meta != null) {
			meta.addEnchant(Enchantment.DURABILITY, modManager.getModLevel(tool, this), true);

			if (modManager.getModLevel(tool, this) == this.getMaxLvl() && this.applyUnbreakableOnMaxLevel) {
				meta.setUnbreakable(true);
				if (hideUnbreakableFlag) {
					meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
				}
			}

			tool.setItemMeta(meta);
		}

		return true;
	}

	@Override
	public void removeMod(ItemStack tool) {
		ItemMeta meta = tool.getItemMeta();

		if (meta != null) {
			meta.removeEnchant(Enchantment.DURABILITY);
			if (this.applyUnbreakableOnMaxLevel) {
				meta.setUnbreakable(false);
				meta.removeItemFlags(ItemFlag.HIDE_UNBREAKABLE);
			}

			tool.setItemMeta(meta);
		}
	}
}

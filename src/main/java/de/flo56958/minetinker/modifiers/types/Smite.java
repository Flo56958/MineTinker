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

import java.io.File;
import java.util.*;

public class Smite extends Modifier {

	private static Smite instance;

	private Smite() {
		super(MineTinker.getPlugin());
		customModelData = 10_035;
	}

	public static Smite instance() {
		synchronized (Smite.class) {
			if (instance == null) {
				instance = new Smite();
			}
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Smite";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Arrays.asList(ToolType.SWORD, ToolType.AXE);
	}

	@Override
	public List<Enchantment> getAppliedEnchantments() {
		return Collections.singletonList(Enchantment.DAMAGE_UNDEAD);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Color", "%YELLOW%");
		config.addDefault("MaxLevel", 5);
		config.addDefault("SlotCost", 1);

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", "BMB");
		config.addDefault("Recipe.Middle", "MIM");
		config.addDefault("Recipe.Bottom", "BMB");

		Map<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("B", Material.BONE.name());
		recipeMaterials.put("M", Material.BONE_MEAL.name());
		recipeMaterials.put("I", Material.IRON_INGOT.name());

		config.addDefault("Recipe.Materials", recipeMaterials);

		ConfigurationManager.getInstance().saveConfig(config);
		ConfigurationManager.getInstance().loadConfig("Modifiers" + File.separator, getFileName());

		init(Material.BONE);
	}

	@Override
	public boolean applyMod(Player player, ItemStack tool, boolean isCommand) {
		ItemMeta meta = tool.getItemMeta();

		if (meta != null) {
			if (ToolType.AXE.contains(tool.getType()) || ToolType.SWORD.contains(tool.getType())) {
				meta.addEnchant(Enchantment.DAMAGE_UNDEAD, modManager.getModLevel(tool, this), true);
			}

			tool.setItemMeta(meta);
		}

		return true;
	}
}

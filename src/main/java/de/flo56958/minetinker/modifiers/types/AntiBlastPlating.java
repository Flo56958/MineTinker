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

public class AntiBlastPlating extends Modifier {

	private static AntiBlastPlating instance;

	private AntiBlastPlating() {
		super(MineTinker.getPlugin());
		customModelData = 10_002;
	}

	public static AntiBlastPlating instance() {
		synchronized (AntiBlastPlating.class) {
			if (instance == null)
				instance = new AntiBlastPlating();
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Anti-Blast-Plating";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Arrays.asList(ToolType.BOOTS, ToolType.LEGGINGS, ToolType.CHESTPLATE, ToolType.HELMET);
	}

	@Override
	public @NotNull List<Enchantment> getAppliedEnchantments() {
		return Collections.singletonList(Enchantment.PROTECTION_EXPLOSIONS);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("MaxLevel", 4);
		config.addDefault("SlotCost", 1);
		config.addDefault("Color", "%WHITE%");
		config.addDefault("ModifierItemMaterial", Material.IRON_BLOCK.name());

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);
		config.addDefault("MinimumToolLevelRequirement", 1);

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", "IMI");
		config.addDefault("Recipe.Middle", "MDM");
		config.addDefault("Recipe.Bottom", "IMI");

		Map<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("I", Material.IRON_BLOCK.name());
		recipeMaterials.put("M", Material.TNT.name());
		recipeMaterials.put("D", Material.DIAMOND.name());

		config.addDefault("Recipe.Materials", recipeMaterials);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init();
	}

	@Override
	public boolean applyMod(Player player, ItemStack tool, boolean isCommand) {
		ItemMeta meta = tool.getItemMeta();

		if (meta != null) {
			if (ToolType.ARMOR.contains(tool.getType()))
				meta.addEnchant(Enchantment.PROTECTION_EXPLOSIONS, modManager.getModLevel(tool, this), true);

			tool.setItemMeta(meta);
		}

		return true;
	}
}

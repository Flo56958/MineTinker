package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ConfigurationManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class Hardened extends Modifier implements Listener {

	private static Hardened instance;

	private double armorPerLevel;
	private double toughnessPerLevel;

	private Hardened() {
		super(MineTinker.getPlugin());
		customModelData = 10_046;
	}

	public static Hardened instance() {
		synchronized (Hardened.class) {
			if (instance == null)
				instance = new Hardened();
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Hardened";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Arrays.asList(ToolType.HELMET, ToolType.CHESTPLATE, ToolType.ELYTRA, ToolType.LEGGINGS, ToolType.BOOTS);
	}

	@Override
	public @NotNull List<Attribute> getAppliedAttributes() {
		return Arrays.asList(Attribute.GENERIC_ARMOR, Attribute.GENERIC_ARMOR_TOUGHNESS);
	}

	@Override
	public boolean applyMod(Player player, ItemStack tool, boolean isCommand) {
		int level = modManager.getModLevel(tool, this);
		this.removeMod(tool); // Remove old attributes
		ItemMeta meta = tool.getItemMeta();
		if (meta == null) return false;

		final ToolType toolType = ToolType.get(tool.getType());

		{
			final double amount = armorPerLevel * level;

			if (amount > 0) {
				final NamespacedKey nkArmor = new NamespacedKey(MineTinker.getPlugin(), this.sArmor + toolType.name());
				final AttributeModifier armorAM = switch (toolType) {
					case BOOTS -> new AttributeModifier(nkArmor, amount,
							AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.FEET);
					case CHESTPLATE, ELYTRA -> new AttributeModifier(nkArmor, amount,
							AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.CHEST);
					case HELMET -> new AttributeModifier(nkArmor, amount,
							AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HEAD);
					case LEGGINGS -> new AttributeModifier(nkArmor, amount,
							AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.LEGS);
					default -> null;
				};
				assert armorAM != null;
				meta.addAttributeModifier(Attribute.GENERIC_ARMOR, armorAM);
			}
		}

		{
			final double amount = toughnessPerLevel * level;

			if (amount > 0) {
				final NamespacedKey nkArmorToughness = new NamespacedKey(MineTinker.getPlugin(), this.sArmorToughness + toolType.name());
				AttributeModifier toughnessAM = switch (toolType) {
					case BOOTS -> new AttributeModifier(nkArmorToughness, amount,
							AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.FEET);
					case CHESTPLATE, ELYTRA -> new AttributeModifier(nkArmorToughness, amount,
							AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.CHEST);
					case HELMET -> new AttributeModifier(nkArmorToughness, amount,
							AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HEAD);
					case LEGGINGS -> new AttributeModifier(nkArmorToughness, amount,
							AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.LEGS);
					default -> null;
				};
				assert toughnessAM != null;
				meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, toughnessAM);
			}
		}
		tool.setItemMeta(meta);
		return true;
	}

	private final String sArmor = this.getKey() + ".armor_";
	private final String sArmorToughness = this.getKey() + ".armor_toughness_";

	@Override
	public void removeMod(ItemStack tool) {
		final ItemMeta meta = tool.getItemMeta();
		if (meta == null) return;

		final ToolType toolType = ToolType.get(tool.getType());

		final NamespacedKey nkArmor = new NamespacedKey(MineTinker.getPlugin(), this.sArmor + toolType.name());
		Collection<AttributeModifier> list = meta.getAttributeModifiers(Attribute.GENERIC_ARMOR);
		if (list != null) {
			list = new ArrayList<>(list); // Collection is immutable
			list.removeIf(am -> !nkArmor.getNamespace().equals(am.getKey().getNamespace()));
			list.removeIf(am -> !nkArmor.getKey().contains(am.getKey().getKey()));
			list.forEach(am -> meta.removeAttributeModifier(Attribute.GENERIC_ARMOR, am));
		}

		final NamespacedKey nkArmorToughness = new NamespacedKey(MineTinker.getPlugin(), this.sArmorToughness + toolType.name());
		list = meta.getAttributeModifiers(Attribute.GENERIC_ARMOR_TOUGHNESS);
		if (list != null) {
			list = new ArrayList<>(list); // Collection is immutable
			list.removeIf(am -> !nkArmorToughness.getNamespace().equals(am.getKey().getNamespace()));
			list.removeIf(am -> !nkArmorToughness.getKey().contains(am.getKey().getKey()));
			list.forEach(am -> meta.removeAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, am));
		}
		tool.setItemMeta(meta);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Color", "%DARK_GRAY%");
		config.addDefault("MaxLevel", 5);
		config.addDefault("SlotCost", 1);
		config.addDefault("ModifierItemMaterial", Material.IRON_BLOCK.name());
		config.addDefault("ArmorPerLevel", 1.0);
		config.addDefault("ToughnessPerLevel", 0.5);

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);
		config.addDefault("MinimumToolLevelRequirement", 1);

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", "D D");
		config.addDefault("Recipe.Middle", "III");
		config.addDefault("Recipe.Bottom", "D D");

		Map<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("D", Material.DIAMOND.name());
		recipeMaterials.put("I", Material.IRON_BLOCK.name());

		config.addDefault("Recipe.Materials", recipeMaterials);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		this.armorPerLevel = config.getDouble("ArmorPerLevel", 1.0);
		this.toughnessPerLevel = config.getDouble("ToughnessPerLevel", 0.5);

		init();

		this.description = this.description.replaceAll("%aamount", String.valueOf(this.armorPerLevel))
				.replaceAll("%tamount", String.valueOf(this.toughnessPerLevel));
	}
}

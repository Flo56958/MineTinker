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
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class Speedy extends Modifier {

	private static Speedy instance;

	private double speedPerLevel;

	private Speedy() {
		super(MineTinker.getPlugin());
		customModelData = 10_037;
	}

	public static Speedy instance() {
		synchronized (Speedy.class) {
			if (instance == null)
				instance = new Speedy();
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Speedy";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Arrays.asList(ToolType.BOOTS, ToolType.LEGGINGS);
	}

	@Override
	public @NotNull List<Attribute> getAppliedAttributes() {
		return Collections.singletonList(Attribute.MOVEMENT_SPEED);
	}

	private final String sMovementSpeed = this.getKey() + ".movement_speed_";

	@Override
	public void removeMod(ItemStack tool) {
		ItemMeta meta = tool.getItemMeta();
		if (meta == null) return;

		final ToolType toolType = ToolType.get(tool.getType());
		final NamespacedKey nkMovementSpeed = new NamespacedKey(MineTinker.getPlugin(), sMovementSpeed + toolType.name());

		Collection<AttributeModifier> list = meta.getAttributeModifiers(Attribute.MOVEMENT_SPEED);
		if (list != null) {
			list = new ArrayList<>(list); // Collection is immutable
			list.removeIf(am -> !nkMovementSpeed.getNamespace().equals(am.getKey().getNamespace()));
			list.removeIf(am -> !nkMovementSpeed.getKey().contains(am.getKey().getKey()));
			list.forEach(am -> meta.removeAttributeModifier(Attribute.MOVEMENT_SPEED, am));
		}

		tool.setItemMeta(meta);
	}

	@Override
	public boolean applyMod(Player player, ItemStack tool, boolean isCommand) {
		this.removeMod(tool); // Remove old attributes

		ItemMeta meta = tool.getItemMeta();
		if (meta == null) return false;

		final int level = modManager.getModLevel(tool, this);
		final ToolType toolType = ToolType.get(tool.getType());
		final NamespacedKey nkMovementSpeed = new NamespacedKey(MineTinker.getPlugin(), sMovementSpeed + toolType.name());

		if (ToolType.LEGGINGS.contains(tool.getType()))
			meta.addAttributeModifier(Attribute.MOVEMENT_SPEED,
				new AttributeModifier(nkMovementSpeed, level * this.speedPerLevel,
						AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.LEGS));
		else
			meta.addAttributeModifier(Attribute.MOVEMENT_SPEED,
				new AttributeModifier(nkMovementSpeed, level * this.speedPerLevel,
						AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.FEET));

		tool.setItemMeta(meta);
		return true;
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Color", "%BLUE%");
		config.addDefault("MaxLevel", 5);
		config.addDefault("SlotCost", 1);
		config.addDefault("ModifierItemMaterial", Material.RABBIT_HIDE.name());
		config.addDefault("SpeedPerLevel", 0.01);

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);
		config.addDefault("MinimumToolLevelRequirement", 1);

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", "R R");
		config.addDefault("Recipe.Middle", " H ");
		config.addDefault("Recipe.Bottom", "R R");

		Map<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("H", Material.RABBIT_HIDE.name());
		recipeMaterials.put("R", Material.RABBIT_FOOT.name());

		config.addDefault("Recipe.Materials", recipeMaterials);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		this.speedPerLevel = config.getDouble("SpeedPerLevel");

		init();

		this.description = this.description.replace("%amount", String.valueOf(this.speedPerLevel * 100));
	}
}

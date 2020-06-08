package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ConfigurationManager;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
			if (instance == null) {
				instance = new Speedy();
			}
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
	public List<Attribute> getAppliedAttributes() {
		return Collections.singletonList(Attribute.GENERIC_MOVEMENT_SPEED);
	}

	@Override
	public boolean applyMod(Player player, ItemStack tool, boolean isCommand) {
		ItemMeta meta = tool.getItemMeta();

		if (meta == null) {
			return false;
		}

		//To check if armor modifiers are on the armor
		Collection<AttributeModifier> attributeModifiers = meta.getAttributeModifiers(Attribute.GENERIC_ARMOR);

		if (attributeModifiers == null || attributeModifiers.isEmpty()) {
			modManager.addArmorAttributes(tool);
			meta = tool.getItemMeta();
		}

		Collection<AttributeModifier> speedModifiers = meta.getAttributeModifiers(Attribute.GENERIC_MOVEMENT_SPEED);
		double speedOnItem = 0.0D;

		if (!(speedModifiers == null || speedModifiers.isEmpty())) {
			HashSet<String> names = new HashSet<>();

			for (AttributeModifier am : speedModifiers) {
				if (names.add(am.getName())) speedOnItem += am.getAmount();
			}
		}

		meta.removeAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED);
		meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED,
				new AttributeModifier(UUID.randomUUID(), "generic.movementSpeed", speedOnItem + this.speedPerLevel, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS));
		meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED,
				new AttributeModifier(UUID.randomUUID(), "generic.movementSpeed", speedOnItem + this.speedPerLevel, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET));

		tool.setItemMeta(meta);
		return true;
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Name", "Speedy");
		config.addDefault("ModifierItemName", "Enhanced Rabbithide");
		config.addDefault("Description", "Gotta go fast!");
		config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Speedy-Modifier");
		config.addDefault("Color", "%BLUE%");
		config.addDefault("MaxLevel", 5);
		config.addDefault("SlotCost", 1);
		config.addDefault("SpeedPerLevel", 0.01);
		config.addDefault("OverrideLanguagesystem", false);

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);

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

		init(Material.RABBIT_HIDE);

		this.description = this.description.replace("%amount", String.valueOf(this.speedPerLevel * 100));
	}

	@Override
	public List<Enchantment> getAppliedEnchantments() {
		return new ArrayList<>();
	}
}

package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.ConfigurationManager;
import de.flo56958.minetinker.utils.data.DataHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.*;

public class Tanky extends Modifier implements Listener {

	private static Tanky instance;

	private int healthPerLevel;

	private Tanky() {
		super(MineTinker.getPlugin());
		customModelData = 10_040;
	}

	public static Tanky instance() {
		synchronized (Tanky.class) {
			if (instance == null) {
				instance = new Tanky();
			}
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Tanky";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Arrays.asList(ToolType.CHESTPLATE, ToolType.LEGGINGS);
	}

	@Override
	public List<Attribute> getAppliedAttributes() {
		return Collections.singletonList(Attribute.GENERIC_MAX_HEALTH);
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

		Collection<AttributeModifier> healthModifiers = meta.getAttributeModifiers(Attribute.GENERIC_MAX_HEALTH);

		double healthOnItem = 0.0D;
		if (!(healthModifiers == null || healthModifiers.isEmpty())) {
			HashSet<String> names = new HashSet<>();
			for (AttributeModifier am : healthModifiers) {
				if (names.add(am.getName())) healthOnItem += am.getAmount();
			}
		}
		meta.removeAttributeModifier(Attribute.GENERIC_MAX_HEALTH);
		modManager.addArmorAttributes(tool);
		meta.addAttributeModifier(Attribute.GENERIC_MAX_HEALTH, new AttributeModifier(UUID.randomUUID(), "generic.maxHealth", healthOnItem + this.healthPerLevel, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST));
		meta.addAttributeModifier(Attribute.GENERIC_MAX_HEALTH, new AttributeModifier(UUID.randomUUID(), "generic.maxHealth", healthOnItem + this.healthPerLevel, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS));

		tool.setItemMeta(meta);
		return true;
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Color", "%DARK_GRAY%");
		config.addDefault("MaxLevel", 5);
		config.addDefault("SlotCost", 1);
		config.addDefault("HealthPerLevel", 3);

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", "RBR");
		config.addDefault("Recipe.Middle", "BOB");
		config.addDefault("Recipe.Bottom", "RBR");

		Map<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("B", Material.BONE.name());
		recipeMaterials.put("O", Material.OBSIDIAN.name());
		recipeMaterials.put("R", Material.ROTTEN_FLESH.name());

		config.addDefault("Recipe.Materials", recipeMaterials);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		this.healthPerLevel = config.getInt("HealthPerLevel", 3);

		init(Material.OBSIDIAN);

		this.description = this.description.replace("%amount", String.valueOf(this.healthPerLevel / 2.0));
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Double health;
		ItemStack chest = event.getPlayer().getInventory().getChestplate();
		if (modManager.isArmorViable(chest) && modManager.hasMod(chest, this)) {
			health = DataHandler.getTag(chest, "modifier_berserk_health_save", PersistentDataType.DOUBLE, false);
			if (health != null) {
				Bukkit.getScheduler().runTaskLater(MineTinker.getPlugin(), () -> event.getPlayer().setHealth(health), 10L);
				DataHandler.removeTag(chest, "modifier_berserk_health_save", false);
			} else {
				return;
			}
		} else {
			chest = event.getPlayer().getInventory().getLeggings();
			if (modManager.isArmorViable(chest) && modManager.hasMod(chest, this)) {
				health = DataHandler.getTag(chest, "modifier_berserk_health_save", PersistentDataType.DOUBLE, false);
				if (health != null) {
					Bukkit.getScheduler().runTaskLater(MineTinker.getPlugin(), () -> event.getPlayer().setHealth(health), 10L);
					DataHandler.removeTag(chest, "modifier_berserk_health_save", false);
				} else {
					return;
				}
			} else {
				return;
			}
		}
		ChatWriter.logModifier(event.getPlayer(), event, this, chest, String.format("ApplyHealth(%.2f -> %.2f)", event.getPlayer().getHealth(), health));
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		if (event.getPlayer().getHealth() >= 20.0) { //has Tanky and enough health
			ItemStack chest = event.getPlayer().getInventory().getChestplate();
			if (modManager.isArmorViable(chest) && modManager.hasMod(chest, this)) {
				DataHandler.setTag(chest, "modifier_berserk_health_save", event.getPlayer().getHealth(),
						PersistentDataType.DOUBLE, false);
				return;
			}
			chest = event.getPlayer().getInventory().getLeggings();
			if (modManager.isArmorViable(chest) && modManager.hasMod(chest, this)) {
				DataHandler.setTag(chest, "modifier_berserk_health_save", event.getPlayer().getHealth(),
						PersistentDataType.DOUBLE, false);
			}
		}
	}
}

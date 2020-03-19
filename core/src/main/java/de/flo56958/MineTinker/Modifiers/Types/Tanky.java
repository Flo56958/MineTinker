package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
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

import java.io.File;
import java.util.*;

public class Tanky extends Modifier implements Listener {

	private static Tanky instance;

	private HashMap<UUID, Double> playerHealth = new HashMap<>(); //used to track Player health when quiting and rejoining
	//TODO: Save this data to a more suitable location, this one gets deleted on reload
	//Maybe a database

	private int healthPerLevel;

	private Tanky() {
		super(Main.getPlugin());
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
		config.addDefault("Name", "Tanky");
		config.addDefault("ModifierItemName", "Bloodinfused Obsidian");
		config.addDefault("Description", "Makes you extra tanky!");
		config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Tanky-Modifier");
		config.addDefault("Color", "%DARK_GRAY%");
		config.addDefault("MaxLevel", 5);
		config.addDefault("HealthPerLevel", 3);

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", "RBR");
		config.addDefault("Recipe.Middle", "BOB");
		config.addDefault("Recipe.Bottom", "RBR");
		config.addDefault("OverrideLanguagesystem", false);

		Map<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("B", Material.BONE.name());
		recipeMaterials.put("O", Material.OBSIDIAN.name());
		recipeMaterials.put("R", Material.ROTTEN_FLESH.name());

		config.addDefault("Recipe.Materials", recipeMaterials);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		this.healthPerLevel = config.getInt("HealthPerLevel", 3);

		init(Material.OBSIDIAN, true);

		this.description = this.description.replace("%amount", "" + this.healthPerLevel / 2.0);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Double health = playerHealth.get(event.getPlayer().getUniqueId());
		if (health != null) {
			Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> event.getPlayer().setHealth(health), 10L);
		}
		playerHealth.remove(event.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		if (event.getPlayer().getHealth() >= 20.0) { //has Tanky and enough health
			playerHealth.put(event.getPlayer().getUniqueId(), event.getPlayer().getHealth());
		}
	}
}

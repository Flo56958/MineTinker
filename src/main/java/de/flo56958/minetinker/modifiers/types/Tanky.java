package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.ConfigurationManager;
import de.flo56958.minetinker.utils.data.DataHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class Tanky extends Modifier implements Listener {

	private static Tanky instance;

	private int healthPerLevel;
	private boolean allowElytra;

	private Tanky() {
		super(MineTinker.getPlugin());
		customModelData = 10_040;
	}

	public static Tanky instance() {
		synchronized (Tanky.class) {
			if (instance == null)
				instance = new Tanky();
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Tanky";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		if (allowElytra)
			return Arrays.asList(ToolType.CHESTPLATE, ToolType.LEGGINGS, ToolType.ELYTRA);
		return Arrays.asList(ToolType.CHESTPLATE, ToolType.LEGGINGS);
	}

	@Override
	public @NotNull List<Attribute> getAppliedAttributes() {
		return Collections.singletonList(Attribute.MAX_HEALTH);
	}

	private final String sHealth = this.getKey() + ".max_health_";

	@Override
	public void removeMod(ItemStack tool) {
		ItemMeta meta = tool.getItemMeta();
		if (meta == null) return;

		final ToolType toolType = ToolType.get(tool.getType());
		final NamespacedKey nkHealth = new NamespacedKey(MineTinker.getPlugin(), sHealth + toolType.name());

		Collection<AttributeModifier> list = meta.getAttributeModifiers(Attribute.MAX_HEALTH);
		if (list != null) {
			list = new ArrayList<>(list); // Collection is immutable
			list.removeIf(am -> !nkHealth.getNamespace().equals(am.getKey().getNamespace()));
			list.removeIf(am -> !nkHealth.getKey().contains(am.getKey().getKey()));
			list.forEach(am -> meta.removeAttributeModifier(Attribute.MAX_HEALTH, am));
		}

		tool.setItemMeta(meta);
	}

	@Override
	public boolean applyMod(Player player, ItemStack tool, boolean isCommand) {
		this.removeMod(tool); // remove old attributes

		ItemMeta meta = tool.getItemMeta();
		if (meta == null) return false;

		final int level = modManager.getModLevel(tool, this);

		final ToolType toolType = ToolType.get(tool.getType());
		final NamespacedKey nkHealth = new NamespacedKey(MineTinker.getPlugin(), sHealth + toolType.name());

		if (ToolType.LEGGINGS.contains(tool.getType()))
			meta.addAttributeModifier(Attribute.MAX_HEALTH, new AttributeModifier(nkHealth, level * this.healthPerLevel,
					AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.LEGS));
		else //Chestplate and Elytra
			meta.addAttributeModifier(Attribute.MAX_HEALTH, new AttributeModifier(nkHealth, level * this.healthPerLevel,
					AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.CHEST));

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
		config.addDefault("ModifierItemMaterial", Material.OBSIDIAN.name());
		config.addDefault("HealthPerLevel", 3);

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);
		config.addDefault("MinimumToolLevelRequirement", 1);
		config.addDefault("AllowElytra", false);

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
		this.allowElytra = config.getBoolean("AllowElytra", false);

		init();

		this.description = this.description.replace("%amount", String.valueOf(this.healthPerLevel / 2.0));
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Double health;
		ItemStack chest = event.getPlayer().getInventory().getChestplate();
		if (modManager.isArmorViable(chest) && modManager.hasMod(chest, this)) {
			health = DataHandler.getTag(chest, "modifier_tanky_health_save", PersistentDataType.DOUBLE);
			if (health != null && health > 0) {
				Bukkit.getScheduler().runTaskLater(this.getSource(), () -> {
					try {
						event.getPlayer().setHealth(health);
					} catch (IllegalArgumentException ignored) {}
				}, 10L);
				DataHandler.removeTag(chest, "modifier_tanky_health_save");
			} else {
				return;
			}
		} else {
			chest = event.getPlayer().getInventory().getLeggings();
			if (modManager.isArmorViable(chest) && modManager.hasMod(chest, this)) {
				health = DataHandler.getTag(chest, "modifier_tanky_health_save", PersistentDataType.DOUBLE);
				if (health != null) {
					Bukkit.getScheduler().runTaskLater(this.getSource(), () -> event.getPlayer().setHealth(health), 10L);
					DataHandler.removeTag(chest, "modifier_tanky_health_save");
				} else return;
			} else return;
		}
		ChatWriter.logModifier(event.getPlayer(), event, this, chest, String.format("ApplyHealth(%.2f -> %.2f)", event.getPlayer().getHealth(), health));
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		if (event.getPlayer().getHealth() >= 20.0) { //has Tanky and enough health
			ItemStack chest = event.getPlayer().getInventory().getChestplate();
			if (modManager.isArmorViable(chest) && modManager.hasMod(chest, this)) {
				DataHandler.setTag(chest, "modifier_tanky_health_save", event.getPlayer().getHealth(),
						PersistentDataType.DOUBLE);
				return;
			}
			chest = event.getPlayer().getInventory().getLeggings();
			if (modManager.isArmorViable(chest) && modManager.hasMod(chest, this)) {
				DataHandler.setTag(chest, "modifier_tanky_health_save", event.getPlayer().getHealth(),
						PersistentDataType.DOUBLE);
			}
		}
	}
}

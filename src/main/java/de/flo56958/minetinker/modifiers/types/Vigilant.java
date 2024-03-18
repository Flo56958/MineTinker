package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.api.events.MTEntityDamageByEntityEvent;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ConfigurationManager;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Vigilant extends Modifier implements Listener {

	private static Vigilant instance;

	private double percentile;

	private Vigilant() {
		super(MineTinker.getPlugin());
		customModelData = 10_061;
	}

	public static Vigilant instance() {
		synchronized (Vigilant.class) {
			if (instance == null)
				instance = new Vigilant();
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Vigilant";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Collections.singletonList(ToolType.CHESTPLATE);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Color", "%GRAY%");
		config.addDefault("MaxLevel", 3);
		config.addDefault("SlotCost", 1);
		config.addDefault("ModifierItemMaterial", Material.GOLDEN_APPLE.name());

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", true);
		config.addDefault("MinimumToolLevelRequirement", 1);
		config.addDefault("AmountDamageAsShield", 0.33);

		config.addDefault("Recipe.Enabled", false);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init();

		this.percentile = config.getDouble("AmountDamageAsShield", 0.33);

		this.description = this.description.replaceAll("%amount", Math.round(this.percentile * 100) + "%");
	}

	private void effect(@NotNull final Player player, final double damage) {
		if (!player.hasPermission(getUsePermission())) return;
		if (player.getAbsorptionAmount() >= 0.5) return;

		final ItemStack chestplate = player.getInventory().getChestplate();
		if (!modManager.isArmorViable(chestplate)) return;
		if (!modManager.hasMod(chestplate, this)) return;

		final int level = modManager.getModLevel(chestplate, this);

		final double absorption = damage * this.percentile * level;

		// Make sure the player can receive the absorption
		final ItemMeta meta = chestplate.getItemMeta();
		if (meta == null) return;
		meta.removeAttributeModifier(Attribute.GENERIC_MAX_ABSORPTION); // Overwrite the old values
		meta.addAttributeModifier(Attribute.GENERIC_MAX_ABSORPTION, new AttributeModifier(UUID.randomUUID(),
				"generic.max_absorption", absorption,
				AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST));
		chestplate.setItemMeta(meta);

		// Add absorption
		player.setAbsorptionAmount(absorption);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onDamage(@NotNull MTEntityDamageByEntityEvent event) {
		// Player was damaged
		if (event.getPlayer().equals(event.getEvent().getEntity()))
			effect(event.getPlayer(), Math.max(0.0d, event.getEvent().getFinalDamage()));
	}
}
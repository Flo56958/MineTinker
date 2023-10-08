package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.api.events.MTEntityDamageByEntityEvent;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ConfigurationManager;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class Vigilant extends Modifier implements Listener {

	private static Vigilant instance;

	private double percentile;

	private Vigilant() {
		super(MineTinker.getPlugin());
		customModelData = 10_061;
	}

	public static Vigilant instance() {
		synchronized (Vigilant.class) {
			if (instance == null) {
				instance = new Vigilant();
			}
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

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", true);
		config.addDefault("MinimumToolLevelRequirement", 1);
		config.addDefault("AmountDamageAsShield", 0.33);

		config.addDefault("Recipe.Enabled", false);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init(Material.GOLDEN_APPLE);

		this.percentile = config.getDouble("AmountDamageAsShield", 0.33);

		this.description = this.description.replaceAll("%amount", Math.round(this.percentile * 100) + "%");
	}

	private void effect(@NotNull final Player player, final double damage) {
		if (!player.hasPermission("minetinker.modifiers.vigilant.use")) return;
		if (player.getAbsorptionAmount() >= 0.5) return;

		final ItemStack chestplate = player.getInventory().getChestplate();
		if (!modManager.isArmorViable(chestplate)) return;
		if (!modManager.hasMod(chestplate, this)) return;

		final int level = modManager.getModLevel(chestplate, this);

		final double absorption = damage * this.percentile * level;
		//Add absorption
		player.setAbsorptionAmount(absorption);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onDamage(@NotNull MTEntityDamageByEntityEvent event) {
		// Player was damaged
		if (event.getPlayer().equals(event.getEvent().getEntity())) {
			effect(event.getPlayer(), Math.max(0.0d, event.getEvent().getFinalDamage()));
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onEat(@NotNull PlayerItemConsumeEvent event) {
		ItemStack item = event.getItem().clone();
		if (item.hasItemMeta() && item.getItemMeta().hasCustomModelData() && item.getItemMeta().getCustomModelData() == this.customModelData) {
			event.setCancelled(true);
		}
	}
}
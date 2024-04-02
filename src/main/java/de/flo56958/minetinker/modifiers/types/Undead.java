package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ConfigurationManager;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class Undead extends Modifier implements Listener {

	private static Undead instance;
	private boolean disableAttacks;

	private Undead() {
		super(MineTinker.getPlugin());
		customModelData = 10_060;
	}

	public static Undead instance() {
		synchronized (Undead.class) {
			if (instance == null)
				instance = new Undead();
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Undead";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Collections.singletonList(ToolType.HELMET);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Color", "%YELLOW%");
		config.addDefault("MaxLevel", 1);
		config.addDefault("SlotCost", 5);
		config.addDefault("ModifierItemMaterial", Material.LIME_DYE.name());

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", true);
		config.addDefault("MinimumToolLevelRequirement", 1);
		config.addDefault("DisableAttacks", true);

		config.addDefault("Recipe.Enabled", false);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init();

		this.disableAttacks = config.getBoolean("DisableAttacks", true);
	}

	@EventHandler(ignoreCancelled = true)
	public void onTarget(@NotNull EntityTargetLivingEntityEvent event) {
		if (!(event.getTarget() instanceof Player player)) return;
		if (!player.hasPermission(getUsePermission())) return;

		ItemStack helmet = player.getInventory().getHelmet();
		if (!modManager.isArmorViable(helmet)) return;
		if (!modManager.hasMod(helmet, this)) return;

		event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
	public void onAttack(@NotNull EntityDamageByEntityEvent event) {
		if (!this.disableAttacks) return;
		Entity damager = event.getDamager();
		if (damager instanceof Projectile projectile) {
			if (projectile.getShooter() instanceof Entity shooter) {
				damager = shooter;
			}
		}

		if (!(damager instanceof Player player)) return;
		if (event.getEntity() instanceof Player) return;
		if (!player.hasPermission(getUsePermission())) return;

		final ItemStack helmet = player.getInventory().getHelmet();
		if (!modManager.isArmorViable(helmet)) return;
		if (!modManager.hasMod(helmet, this)) return;

		event.setCancelled(true);

		if (event.getDamager() instanceof Projectile projectile) {
			projectile.remove();
		}
	}
}

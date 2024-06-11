package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.api.events.MTEntityDamageByEntityEvent;
import de.flo56958.minetinker.api.events.MTProjectileLaunchEvent;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.ConfigurationManager;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Magical extends Modifier implements Listener {

	private static Magical instance;
	private double multiplierArrowSpeed;
	private double multiplierDamagePerLevel;
	private int experienceCost;
	private boolean hasKnockback;

	private Magical() {
		super(MineTinker.getPlugin());
		customModelData = 10_021;
	}

	public static Magical instance() {
		synchronized (Magical.class) {
			if (instance == null)
				instance = new Magical();
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Magical";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Arrays.asList(ToolType.BOW, ToolType.CROSSBOW);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Color", "%DARK_PURPLE%");
		config.addDefault("MaxLevel", 5);
		config.addDefault("SlotCost", 1);
		config.addDefault("ModifierItemMaterial", Material.ARROW.name());

		config.addDefault("MultiplierArrowSpeed", 0.5);
		config.addDefault("MultiplierArrowDamagePerLevel", 1.25);
		config.addDefault("ExperienceCost", 3);
		config.addDefault("HasKnockback", true);

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);
		config.addDefault("MinimumToolLevelRequirement", 1);

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", "BPB");
		config.addDefault("Recipe.Middle", "PAP");
		config.addDefault("Recipe.Bottom", "BPB");

		Map<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("B", Material.DRAGON_BREATH.name());
		recipeMaterials.put("A", Material.ARROW.name());
		recipeMaterials.put("P", Material.BLAZE_POWDER.name());

		config.addDefault("Recipe.Materials", recipeMaterials);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init();

		this.multiplierArrowSpeed = config.getDouble("MultiplierArrowSpeed", 0.3);
		this.multiplierDamagePerLevel = config.getDouble("MultiplierArrowDamagePerLevel", 1.25);
		this.experienceCost = config.getInt("ExperienceCost", 10);
		this.hasKnockback = config.getBoolean("HasKnockback", true);

		this.description = this.description.replace("%amount", String.valueOf(Math.round(this.multiplierArrowSpeed * 100)))
				.replace("%min", String.valueOf(Math.round((this.multiplierDamagePerLevel - 1.0) * 100)))
				.replace("%max", String.valueOf(Math.round((Math.pow(this.multiplierDamagePerLevel, this.getMaxLvl()) - 1.0) * 100)))
				.replace("%xp", String.valueOf(this.experienceCost));
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onShoot(final MTProjectileLaunchEvent event) {
		final Projectile projectile = event.getEvent().getEntity();
		if (!(projectile instanceof Arrow arrow)) return;

		final Player player = event.getPlayer();
		if (!player.hasPermission(getUsePermission())) return;

		final ItemStack tool = event.getTool();
		if (!modManager.isToolViable(tool)) return;
		final int modLevel = modManager.getModLevel(tool, this);
		if (modLevel <= 0) return;

		if (player.getGameMode() != GameMode.CREATIVE) {
			if (player.getTotalExperience() <= this.experienceCost) {
				if (arrow.getPickupStatus() != AbstractArrow.PickupStatus.CREATIVE_ONLY) {
					// return arrow to player
					player.getInventory().addItem(arrow.getItem());
				}
				event.setCancelled(true);
				return;
			}

			player.giveExp(-this.experienceCost);
		}

		arrow.setColor(Color.PURPLE);
		arrow.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
		arrow.setGravity(false);
		//arrow.setGlowing(true);

		final Vector velocity = arrow.getVelocity().multiply(this.multiplierArrowSpeed);
		arrow.setVelocity(velocity.clone());
		arrow.setDamage(arrow.getDamage() * Math.pow(this.multiplierDamagePerLevel, modLevel));

		ChatWriter.logModifier(player, event, this, tool, "Cost(" + this.experienceCost + ")");

		for (int i = 1; i < 30; i++) {
			Bukkit.getScheduler().runTaskLater(this.getSource(), () -> arrow.setVelocity(velocity.clone()), i * 20L);
		}

		Bukkit.getScheduler().runTaskLater(this.getSource(), () -> arrow.setGravity(true), 30 * 20L);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityHit(MTEntityDamageByEntityEvent event) {
		if (!(event.getEvent().getDamager() instanceof Arrow arrow)) return;
		if (arrow.hasGravity()) return;
		if (!(arrow.getShooter() instanceof Player player)) return;
		if (!player.hasPermission(getUsePermission())) return;
		if (!modManager.hasMod(event.getTool(), this)) return;

		int modLevel = modManager.getModLevel(event.getTool(), this);
		double oldDamage = event.getEvent().getDamage() / this.multiplierArrowSpeed;
		double newDamage = oldDamage * Math.pow(this.multiplierDamagePerLevel, modLevel);
		event.getEvent().setDamage(newDamage);
		ChatWriter.logModifier(event.getPlayer(), event, this, event.getTool(),
				String.format("Damage(%.2f -> %.2f [%.4f])", oldDamage, newDamage, newDamage / oldDamage));
		if (this.hasKnockback) event.getEntity().setVelocity(arrow.getVelocity().normalize().multiply(modLevel));
	}
}

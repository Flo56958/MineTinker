package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.api.events.MTProjectileLaunchEvent;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ConfigurationManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.*;

public class Homing extends Modifier implements Listener {

	private static Homing instance;
	private int radius;
	private double accuracy;
	private int homingSeconds;
	private boolean worksOnPlayers;

	public static Homing instance() {
		synchronized (Homing.class) {
			if (instance == null)
				instance = new Homing();
		}

		return instance;
	}

	protected Homing() {
		super(MineTinker.getPlugin());
		customModelData = 10_058;
	}

	@Override
	public String getKey() {
		return "Homing";
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
		config.addDefault("Color", "%GOLD%");
		config.addDefault("MaxLevel", 5);
		config.addDefault("SlotCost", 5);
		config.addDefault("ModifierItemMaterial", Material.OBSERVER.name());

		config.addDefault("EnchantCost", 50);
		config.addDefault("Enchantable", false);
		config.addDefault("MinimumToolLevelRequirement", 5);
		config.addDefault("HomingRadius", 10);
		config.addDefault("AccuracyPerLevel", 0.025);
		config.addDefault("MaximumHomingSeconds", 10);
		config.addDefault("WorksOnPlayers", true);

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", "ESE");
		config.addDefault("Recipe.Middle", "NON");
		config.addDefault("Recipe.Bottom", "ESE");

		Map<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("N", Material.NETHER_STAR.name());
		recipeMaterials.put("O", Material.OBSERVER.name());
		recipeMaterials.put("E", Material.ENDER_EYE.name());
		recipeMaterials.put("S", Material.SHULKER_SHELL.name());

		config.addDefault("Recipe.Materials", recipeMaterials);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init();

		this.radius = config.getInt("HomingRadius", 10);
		this.accuracy = config.getDouble("AccuracyPerLevel", 0.1);
		this.homingSeconds = config.getInt("MaximumHomingSeconds", 10);
		this.worksOnPlayers = config.getBoolean("WorksOnPlayers", true);

		this.description = this.description.replaceAll("%radius", String.valueOf(radius));
	}

	@EventHandler(ignoreCancelled = true)
	public void onShoot(final MTProjectileLaunchEvent event) {
		final Projectile projectile = event.getEvent().getEntity();
		if (!(projectile instanceof final Arrow arrow)) return;

		final Player player = event.getPlayer();
		if (!player.hasPermission(getUsePermission())) return;

		final ItemStack tool = event.getTool();
		if (!modManager.isToolViable(tool)) return;

		final int modLevel = modManager.getModLevel(tool, this);
		if (modLevel <= 0) return;

		final long start = System.currentTimeMillis();
		final double accuracy = modLevel * this.accuracy;
		final Runnable runnable = new Runnable() {
			@Override
			public void run() {
				if (System.currentTimeMillis() - start > Homing.this.homingSeconds * 1000L) return;
				if (arrow.isInBlock()) return;
				if (arrow.isDead()) return;
				if (arrow.getVelocity().length() <= 0.1) return;
				if (arrow.getLastDamageCause() != null) return;

				final List<Entity> entities = arrow.getNearbyEntities(Homing.this.radius, Homing.this.radius, Homing.this.radius);
				entities.sort(Comparator.comparing(e -> e.getLocation().distance(arrow.getLocation())));
				for (final Entity e : entities) {
					if (e.equals(arrow.getShooter())) continue;
					if (e instanceof Arrow) continue;
					if (e instanceof Item) continue;
					if (e instanceof ItemFrame) continue;
					if (e instanceof Minecart) continue;
					if (e instanceof Boat) continue;
					if (e instanceof Painting) continue;
					if (e instanceof Player && !Homing.this.worksOnPlayers) continue;
					if (e instanceof LivingEntity liv && !liv.hasLineOfSight(arrow)) continue;

					final double velocity = arrow.getVelocity().length();
					final Vector vel = arrow.getVelocity().normalize();
					final Vector newVel = e.getLocation().toVector().subtract(arrow.getLocation().toVector()).normalize();
					if (vel.dot(newVel) <= 0.0) continue; // is behind the arrow

					arrow.setVelocity(newVel.multiply(accuracy).add(vel.multiply(1 - accuracy)).normalize().multiply(velocity));
					break;
				}

				Bukkit.getServer().getScheduler().runTaskLater(getSource(), this, 1);
			}
		};

		Bukkit.getServer().getScheduler().runTaskLater(this.getSource(), runnable, 3);
	}
}

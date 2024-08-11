package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.api.events.MTProjectileHitEvent;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ConfigurationManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Phasing extends Modifier implements Listener {

	private static Phasing instance;

	private Phasing() {
		super(MineTinker.getPlugin());
		customModelData = 10_065;
	}

	public static Phasing instance() {
		synchronized (Phasing.class) {
			if (instance == null)
				instance = new Phasing();
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Phasing";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Arrays.asList(ToolType.BOW, ToolType.CROSSBOW);
	}

	@Override
	public @NotNull List<Enchantment> getAppliedEnchantments() {
		return new ArrayList<>();
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Color", "%RED%");
		config.addDefault("MaxLevel", 5);
		config.addDefault("SlotCost", 1);
		config.addDefault("ModifierItemMaterial", Material.CHORUS_FRUIT.name());

		config.addDefault("EnchantCost", 15);
		config.addDefault("Enchantable", true);
		config.addDefault("MinimumToolLevelRequirement", 1);

		config.addDefault("Recipe.Enabled", false);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init();
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
	public void onProjectileHit(final MTProjectileHitEvent event) {
		final Player p = event.getPlayer();
		final ItemStack tool = event.getTool();
		final Projectile projectile = event.getEvent().getEntity();
		if (projectile.hasMetadata(this.getKey())) return;
		projectile.setMetadata(this.getKey(), new FixedMetadataValue(MineTinker.getPlugin(), true));
		if (!modManager.hasMod(tool, this)) return;

		final int level = modManager.getModLevel(tool, this);

		if (!p.hasPermission(getUsePermission())) return;
		if (!(projectile instanceof final Arrow arrow)) return;

		final Vector stepsize = new Vector(arrow.getBoundingBox().getWidthX(),
				arrow.getBoundingBox().getHeight(), arrow.getBoundingBox().getWidthZ()).multiply(0.5);
		final Vector direction = arrow.getVelocity().clone().normalize().multiply(stepsize);
		Vector vector = arrow.getBoundingBox().getCenter();
		// TODO: Implement DDA for better traversal
		loop:
		for (BoundingBox dist = arrow.getBoundingBox().clone();
		     arrow.getLocation().distance(vector.toLocation(arrow.getWorld())) <= level * 1.5;
		     dist.shift(direction)) {
			vector = dist.getCenter();
			final Block block = arrow.getLocation().getWorld().getBlockAt(vector.toLocation(arrow.getWorld()));
			if (block.isPassable()) {
				// construct air bounding box
				// TODO: make more efficient, we do not need 28 block checks
				final BoundingBox box = block.getBoundingBox();
				for (int i = -1; i <= 1; i++) {
					for (int j = -1; j <= 1; j++) {
						for (int k = -1; k <= 1; k++) {
							final Block b = block.getRelative(i, j, k);
							if (b.isPassable()) {
								box.union(b.getBoundingBox());
								if (box.contains(dist)) break loop;
							}
						}
					}
				}
				if (box.contains(dist)) break;
			}
		}

		final Location location = vector.toLocation(arrow.getWorld());

		// Arrow can't phase through the wall as it is too thick
		if (!arrow.getLocation().getWorld().getBlockAt(location).isPassable()) return;

		// Arrow has hit a block, but it should phase through it
		event.setCancelled(true);

		// spawn new arrow and copy all data
		final Arrow phaser = arrow.getLocation().getWorld().spawnArrow(location, arrow.getVelocity(),
				(float) arrow.getVelocity().length(), 0.0f);
		phaser.setPierceLevel(arrow.getPierceLevel());
		phaser.setPickupStatus(arrow.getPickupStatus());
		phaser.setCritical(arrow.isCritical());
		phaser.setDamage(arrow.getDamage());
		phaser.setShooter(arrow.getShooter());
		phaser.setWeapon(tool);
		phaser.setFireTicks(arrow.getFireTicks());

		phaser.setMetadata(this.getKey(), new FixedMetadataValue(MineTinker.getPlugin(), true));
		// add item reference to arrow
		phaser.setMetadata(MineTinker.getPlugin().getName() + "item",
				new FixedMetadataValue(MineTinker.getPlugin(), tool));

		// reenable ender if the arrow was an ender arrow
		if (arrow.hasMetadata(Ender.instance().getKey()))
			phaser.setMetadata(Ender.instance().getKey(), new FixedMetadataValue(MineTinker.getPlugin(), 0));

		// other modifier should not trigger again on ProjectileLaunchEvent,
		// so we do not trigger any events

		// Remove old arrow
		arrow.remove();
	}
}

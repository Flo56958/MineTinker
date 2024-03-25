package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.api.events.MTProjectileLaunchEvent;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.ConfigurationManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class MultiShot extends Modifier implements Listener {

	private static MultiShot instance;
	private double spread;
	private boolean needsArrows;
	private boolean allowMultipleHits;

	private MultiShot() {
		super(MineTinker.getPlugin());
		customModelData = 10_023;
	}

	public static MultiShot instance() {
		synchronized (MultiShot.class) {
			if (instance == null)
				instance = new MultiShot();
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Multishot";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Arrays.asList(ToolType.CROSSBOW, ToolType.BOW);
	}

	@Override
	public @NotNull List<Enchantment> getAppliedEnchantments() {
		return Collections.singletonList(Enchantment.MULTISHOT);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Color", "%YELLOW%");
		config.addDefault("MaxLevel", 5);
		config.addDefault("SlotCost", 1);
		config.addDefault("ModifierItemMaterial", Material.ARROW.name());
		config.addDefault("ArrowSpread", 5.0);
		config.addDefault("NeedsArrows", true);
		config.addDefault("UseEnchantOnCrossbow", false);
		config.addDefault("AllowMultipleHits", true);

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);
		config.addDefault("MinimumToolLevelRequirement", 1);

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", "QQQ");
		config.addDefault("Recipe.Middle", "AAA");
		config.addDefault("Recipe.Bottom", "QQQ");

		Map<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("Q", Material.QUARTZ_BLOCK.name());
		recipeMaterials.put("A", Material.ARROW.name());

		config.addDefault("Recipe.Materials", recipeMaterials);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init();

		this.spread = config.getDouble("ArrowSpread", 5.0);
		this.needsArrows = config.getBoolean("NeedsArrows", true);
		this.allowMultipleHits = config.getBoolean("AllowMultipleHits", true);
	}

	@Override
	public boolean applyMod(Player player, ItemStack tool, boolean isCommand) {
		ItemMeta meta = tool.getItemMeta();

		if (meta != null) {
			if (ToolType.CROSSBOW.contains(tool.getType())) {
				if (getConfig().getBoolean("UseEnchantOnCrossbow"))
					meta.addEnchant(Enchantment.MULTISHOT, modManager.getModLevel(tool, this), true);
				else
					meta.removeEnchant(Enchantment.MULTISHOT);
			}

			tool.setItemMeta(meta);
		}

		return true;
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onHit(final EntityDamageByEntityEvent event) {
		if (!this.allowMultipleHits) return;
		if (!(event.getDamager() instanceof Arrow arrow)) return;
		if (!arrow.hasMetadata(this.getKey())) return;
		if (!(event.getEntity() instanceof LivingEntity entity)) return;

		// Fix Arrows reflecting of entity
		entity.setNoDamageTicks(0);
		entity.setMaximumNoDamageTicks(0);
		event.setCancelled(false);

		Bukkit.getScheduler().runTaskLater(this.getSource(), () -> entity.setMaximumNoDamageTicks(20), 20);
	}

	@EventHandler(ignoreCancelled = true)
	public void onShoot(final MTProjectileLaunchEvent event) {
		Projectile projectile = event.getEvent().getEntity();
		if (projectile.hasMetadata(this.getKey())) return;
		if (!(projectile instanceof Arrow arrow)) return;

		final Player player = event.getPlayer();
		if (!player.hasPermission(getUsePermission())) return;

		final ItemStack tool = event.getTool();

		if (ToolType.CROSSBOW.contains(tool.getType()) && getConfig().getBoolean("UseEnchantOnCrossbow")) return;
		if (!modManager.isToolViable(tool)) return;

		final int modLevel = modManager.getModLevel(tool, this);
		if (modLevel <= 0) return;

		final Vector vel = arrow.getVelocity();
		final Location loc = arrow.getLocation();

		ChatWriter.logModifier(player, event, this, tool);

		final int amount = (ToolType.CROSSBOW.contains(tool.getType())) ? 1 : 2;

		for (int i = 1; i <= modLevel; i++) {
			if (!player.getGameMode().equals(GameMode.CREATIVE) && arrow.getPickupStatus() != AbstractArrow.PickupStatus.CREATIVE_ONLY) {
				boolean hasArrow = true;
				if (needsArrows) {
					hasArrow = false;

					ItemStack offhand = player.getInventory().getItemInOffHand();

					if (!player.getInventory().contains(Material.ARROW)
							&& offhand != null && offhand.getType() != Material.ARROW) break;

					if (!modManager.isModifierItem(offhand)
							&& offhand.getType() == Material.ARROW && offhand.getAmount() >= amount) {
						// 2 as the main arrow is detracted later
						offhand.setAmount(offhand.getAmount() - 1);
						hasArrow = true;
					} else {
						for (ItemStack item : player.getInventory().getContents()) {
							if (item == null || modManager.isModifierItem(item)) continue;

							if (item.getType() == Material.ARROW && item.getAmount() >= amount) {
								item.setAmount(item.getAmount() - 1);
								hasArrow = true;
								break;
							}
						}
					}
				}

				if (!hasArrow) break;
			}

			Bukkit.getScheduler().runTaskLater(this.getSource(), () -> {
				final Arrow arr = loc.getWorld().spawnArrow(loc, vel, (float) vel.length(), (float) spread);
				arr.setShooter(player);
				arr.setShotFromCrossbow(arrow.isShotFromCrossbow());

				if (player.getGameMode().equals(GameMode.CREATIVE) || arrow.getPickupStatus() == AbstractArrow.PickupStatus.CREATIVE_ONLY)
					arr.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);

				arr.setCritical(arrow.isCritical());
				arr.setDamage(arrow.getDamage());
				// no recursive actions
				arr.setMetadata(this.getKey(), new FixedMetadataValue(this.getSource(), null));

				EntityShootBowEvent bowEvent =
						new EntityShootBowEvent(player, tool, new ItemStack(Material.ARROW, 1), arr,
								EquipmentSlot.HAND, (float) vel.length(), false);
				Bukkit.getPluginManager().callEvent(bowEvent);
				if (bowEvent.isCancelled()) {
					// return arrow
					returnArrow(player, arr);
					arr.remove();
					return;
				}
				ProjectileLaunchEvent launchEvent = new ProjectileLaunchEvent(arr);
				Bukkit.getPluginManager().callEvent(launchEvent);
				if (launchEvent.isCancelled()) {
					// return arrow
					returnArrow(player, arr);
					arr.remove();
				}
			}, 0);
		}

		arrow.setMetadata(this.getKey(), new FixedMetadataValue(this.getSource(), null));
	}

	private void returnArrow(Player player, Arrow arr) {
		if (needsArrows && (arr.getPickupStatus() != AbstractArrow.PickupStatus.CREATIVE_ONLY)) {
			if (!player.getInventory().addItem(new ItemStack(Material.ARROW, 1)).isEmpty()) { //adds items to (full) inventory
				player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.ARROW, 1)); //drops item when inventory is full
			} // no else as it gets added in if
		}
	}
}

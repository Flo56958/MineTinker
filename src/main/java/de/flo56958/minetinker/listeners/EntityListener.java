package de.flo56958.minetinker.listeners;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.api.events.MTEntityDamageByEntityEvent;
import de.flo56958.minetinker.api.events.MTEntityDeathEvent;
import de.flo56958.minetinker.api.events.MTProjectileHitEvent;
import de.flo56958.minetinker.api.events.MTProjectileLaunchEvent;
import de.flo56958.minetinker.data.Lists;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.modifiers.ModManager;
import de.flo56958.minetinker.modifiers.Modifier;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class EntityListener implements Listener {

	private static final ModManager modManager = ModManager.instance();

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onDamage(@NotNull final EntityDamageByEntityEvent event) {
		if (Lists.WORLDS.contains(event.getDamager().getWorld().getName())) return;

		if (event.getCause().equals(EntityDamageEvent.DamageCause.SUICIDE)
				|| event.getCause().equals(EntityDamageEvent.DamageCause.VOID)) return;

		Player player = getPlayer(event);

		if (player == null) return;

		ItemStack tool = player.getInventory().getItemInMainHand();
		if (event.getDamager() instanceof Arrow || event.getDamager() instanceof Trident) {
			List<MetadataValue> tools = event.getDamager().getMetadata(MineTinker.getPlugin().getName() + "item");
			if (tools.isEmpty()) return;
			FixedMetadataValue obj = (FixedMetadataValue) tools.get(0);
			if (obj == null || !(obj.value() instanceof ItemStack t)) return;
			tool = t;
		}

//        if (e.getEntity() instanceof Player) {
//            if (((Player) e.getEntity()).isBlocking()) {
//                return;
//            }
//        }

		if (!modManager.isToolViable(tool)) return;

		if (!modManager.durabilityCheck(event, player, tool)) return;

		Bukkit.getPluginManager().callEvent(new MTEntityDamageByEntityEvent(player, tool, event.getEntity(), event));

		modManager.addExp(player, tool,
				MineTinker.getPlugin().getConfig().getInt("ExtraExpPerEntityHit."
						+ event.getEntity().getType(), 0), true);

		if (event.getDamager() instanceof Trident trident) trident.setItem(tool);
	}

	@Nullable
	private static Player getPlayer(@NotNull EntityDamageByEntityEvent event) {
		Player player = null;

		if (event.getDamager() instanceof final Arrow arrow && !(event.getDamager() instanceof Trident)) {
			final ProjectileSource source = arrow.getShooter();
			if (source instanceof Player) player = (Player) source;
		} else if (event.getDamager() instanceof final Trident trident) {
			final ProjectileSource source = trident.getShooter();
			if (source instanceof Player) player = (Player) source;
		} else if (event.getDamager() instanceof Player)
			player = (Player) event.getDamager();
		return player;
	}

	@EventHandler
	public void onDeath(@NotNull final EntityDeathEvent event) {
		final LivingEntity mob = event.getEntity();
		final Player player = mob.getKiller();

		if (Lists.WORLDS.contains(mob.getWorld().getName())) return;

		final FileConfiguration config = MineTinker.getPlugin().getConfig();

		// Trigger mobdrops even if the player is null
		if (!(mob instanceof Player) && (!event.getDrops().isEmpty() || event.getDroppedExp() != 0)) {
			if (config.getBoolean("ConvertLoot.MobDrops", true))
				event.getDrops().forEach(item -> modManager.convertLoot(item, player));

			if (config.getBoolean("MobDropModifierItems.Enabled", true)) {
				if (config.getBoolean("MobDropModifierItems.ConsiderIncludedMobs") ==
						config.getStringList("MobDropModifierItems.IncludedMobs").contains(mob.getType().name())) {
					Random rand = new Random();
					if (rand.nextInt(100) < config.getInt("MobDropModifierItems.Chance", 50)) {
						int amount = rand.nextInt(config.getInt("MobDropModifierItems.MaximumAmount", 2) + 1);
						final List<Modifier> mods = modManager.getAllowedMods();
						for (int i = 0; i < amount; i++) {
							final int index = rand.nextInt(mods.size());
							final Modifier mod = mods.get(index);
							if (!config.getStringList("MobDropModifierItems.ExcludeModifiers").contains(mod.getKey()))
								event.getDrops().add(mod.getModItem().clone());
						}
					}
				}
			}
		}

		if (player == null) return;

		ItemStack tool = player.getInventory().getItemInMainHand();
		if (mob.getLastDamageCause() instanceof EntityDamageByEntityEvent lastevent) {
			if (lastevent.getDamager() instanceof Arrow || lastevent.getDamager() instanceof Trident) {
				List<MetadataValue> tools = lastevent.getDamager().getMetadata(MineTinker.getPlugin().getName() + "item");
				if (tools.isEmpty()) return;
				FixedMetadataValue obj = (FixedMetadataValue) tools.get(0);
				if (obj == null || !(obj.value() instanceof ItemStack t)) return;
				tool = t;
			}
		}

		if (!modManager.isToolViable(tool)) return;

		Bukkit.getPluginManager().callEvent(new MTEntityDeathEvent(player, tool, event));

		modManager.addExp(player, tool, MineTinker.getPlugin().getConfig()
				.getInt("ExtraExpPerEntityDeath." + event.getEntity().getType(), 0), true);

		if (mob.getLastDamageCause() instanceof EntityDamageByEntityEvent lasteven) {
			if (lasteven.getDamager() instanceof Trident trident) trident.setItem(tool);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onArrowHit(@NotNull final ProjectileHitEvent event) {
		if (!(event.getEntity().getShooter() instanceof final Player player)) return;

		ItemStack tool = player.getInventory().getItemInMainHand();

		if (event.getHitBlock() == null && !ToolType.FISHINGROD.contains(tool.getType())) return;

		if (event.getEntity() instanceof Trident || event.getEntity() instanceof Arrow) {
			List<MetadataValue> tools = event.getEntity().getMetadata(MineTinker.getPlugin().getName() + "item");
			if (tools.isEmpty()) return;
			FixedMetadataValue obj = (FixedMetadataValue) tools.get(0);
			if (obj == null || !(obj.value() instanceof ItemStack t)) return;
			tool = t;
		}

		if (!modManager.isToolViable(tool)) return;

		Bukkit.getPluginManager().callEvent(new MTProjectileHitEvent(player, tool, event));

		if (event.getEntity() instanceof Trident trident) trident.setItem(tool);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
	public void onProjectileLaunch(@NotNull final ProjectileLaunchEvent event) {
		if (!(event.getEntity().getShooter() instanceof final Player player)) return;

		ItemStack tool = player.getInventory().getItemInMainHand();
		if (event.getEntity() instanceof Trident trident) tool = trident.getItem();
		if (event.getEntity() instanceof FishHook && (tool == null || !ToolType.FISHINGROD.contains(tool.getType())))
			tool = player.getInventory().getItemInOffHand(); // Fishing rods can also be thrown in offhand

		// get reference from bow shoot
		if(event.getEntity().hasMetadata(MineTinker.getPlugin().getName() + "item")) {
			List<MetadataValue> tools = event.getEntity().getMetadata(MineTinker.getPlugin().getName() + "item");
			if (tools.isEmpty()) return;
			FixedMetadataValue obj = (FixedMetadataValue) tools.get(0);
			if (obj == null || !(obj.value() instanceof ItemStack t)) return;
			tool = t;
		}

		// This isn't the best detection, if the player has a non modifier in one hand and
		// one in the other, this won't know which was actually thrown.
		// TODO: Maybe improve this before release.
		// It works as a safeguard in general though.
		if (modManager.isModifierItem(tool)) {
			event.setCancelled(true);
			player.setCooldown(tool.getType(), 10);
			return;
		} else if (modManager.isModifierItem(player.getInventory().getItemInOffHand())) {
			event.setCancelled(true);
			player.setCooldown(player.getInventory().getItemInOffHand().getType(), 10);
			return;
		}

		if (!modManager.isToolViable(tool)) return;

		if (!modManager.durabilityCheck(event, player, tool)) return;

		modManager.addExp(player, tool, MineTinker.getPlugin().getConfig().getInt("ExpPerArrowShot"), true);

		// add item reference to arrow
		if(!event.getEntity().hasMetadata(MineTinker.getPlugin().getName() + "item")) {
			event.getEntity().setMetadata(MineTinker.getPlugin().getName() + "item",
					new FixedMetadataValue(MineTinker.getPlugin(), tool));
		}

		Bukkit.getPluginManager().callEvent(new MTProjectileLaunchEvent(player, tool, event));

		if (event.getEntity() instanceof Trident trident) trident.setItem(tool);

        /*
        Self-Repair and Experienced will no longer trigger on bowfire
         */
	}

	@EventHandler(ignoreCancelled = true)
	public void onBowShoot(@NotNull final EntityShootBowEvent event) {
		if (!(event.getEntity() instanceof final Player player)) return;

		final ItemStack offHand = player.getInventory().getItemInOffHand();

		final ItemStack bow = event.getBow();
		if (bow != null) {
			// add item reference to arrow
			event.getProjectile().setMetadata(MineTinker.getPlugin().getName() + "item",
					new FixedMetadataValue(MineTinker.getPlugin(), bow));
		}

		if (offHand.getType() == Material.ARROW && playSound(event, player, offHand)) return;

		for (final ItemStack item : player.getInventory().getContents()) {
			if (item == null) continue; // Extremely consistently null

			if (item.getType() == Material.ARROW) {
				playSound(event, player, item);
				return;
			}
		}
	}

	private boolean playSound(final EntityShootBowEvent event, final Player player, final ItemStack offHand) {
		final Modifier mod = modManager.getModifierFromItem(offHand);

		if (mod != null && mod.getModItem().getType() == Material.ARROW) {
			event.setCancelled(true);
			player.playSound(player.getLocation(), Sound.ITEM_CROSSBOW_LOADING_END, 1.0f, 1.0f);
			return true;
		}
		return false;
	}
}
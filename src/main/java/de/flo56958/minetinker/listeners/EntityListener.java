package de.flo56958.minetinker.listeners;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.data.Lists;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.events.MTEntityDamageByEntityEvent;
import de.flo56958.minetinker.events.MTEntityDeathEvent;
import de.flo56958.minetinker.events.MTProjectileHitEvent;
import de.flo56958.minetinker.modifiers.ModManager;
import de.flo56958.minetinker.modifiers.Modifier;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class EntityListener implements Listener {

	private static final ModManager modManager = ModManager.instance();

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onDamage(@NotNull final EntityDamageByEntityEvent event) {
		if (Lists.WORLDS.contains(event.getDamager().getWorld().getName())) {
			return;
		}

		if (event.getCause().equals(EntityDamageEvent.DamageCause.SUICIDE)
				|| event.getCause().equals(EntityDamageEvent.DamageCause.VOID)) {
			return;
		}

		Player player;

		if (event.getDamager() instanceof Arrow && !(event.getDamager() instanceof Trident)) {
			final Arrow arrow = (Arrow) event.getDamager();
			final ProjectileSource source = arrow.getShooter();

			if (source instanceof Player) {
				player = (Player) source;
			} else {
				return;
			}

		} else if (event.getDamager() instanceof Trident) {
			final Trident trident = (Trident) event.getDamager();
			final ProjectileSource source = trident.getShooter();

			if (source instanceof Player) {
				player = (Player) source;
			} else {
				return;
			}

		} else if (event.getDamager() instanceof Player) {
			player = (Player) event.getDamager();
		} else {
			return;
		}


//        if (e.getEntity() instanceof Player) {
//            if (((Player) e.getEntity()).isBlocking()) {
//                return;
//            }
//        }

		ItemStack tool = player.getInventory().getItemInMainHand();

		if (event.getDamager() instanceof Trident) {
			final Trident trident = (Trident) event.getDamager();
			tool = TridentListener.TridentToItemStack.remove(trident);

			if (tool == null) {
				return;
			}
		}

		if (!modManager.isToolViable(tool)) {
			return;
		}

		if (!modManager.durabilityCheck(event, player, tool)) {
			return;
		}

		Bukkit.getPluginManager().callEvent(new MTEntityDamageByEntityEvent(player, tool, event.getEntity(), event));

		modManager.addExp(player, tool,
				MineTinker.getPlugin().getConfig().getInt("ExtraExpPerEntityHit."
						+ event.getEntity().getType().toString(), 0));
	}

	@EventHandler
	public void onDeath(@NotNull final EntityDeathEvent event) {
		final LivingEntity mob = event.getEntity();
		final Player player = mob.getKiller();

		if (player == null) {
			return;
		}

		if (Lists.WORLDS.contains(player.getWorld().getName())) {
			return;
		}

		final ItemStack tool = player.getInventory().getItemInMainHand();

		if (!modManager.isToolViable(tool)) {
			return;
		}

		final FileConfiguration config = MineTinker.getPlugin().getConfig();

		if (config.getBoolean("ConvertMobDrops.Enabled", true)) {
			for (ItemStack item : event.getDrops()) {
				Random rand = new Random();
				if (rand.nextInt(100) < config.getInt("ConvertMobDrops.Chance", 100)) {
					if (!modManager.convertItemStack(item, null)) continue;

					if (config.getBoolean("ConvertMobDrops.ApplyExp", true)) {
						final int exp = rand.nextInt(config.getInt("ConvertMobDrops.MaximumNumberOfExp", 650));
						final int divider = config.getInt("LevelStep", 100);
						for (int i = 0; i < (exp - 17) / divider; i++) { //to get possible LevelUps
							modManager.addExp(null, item, divider);
						}
						final long difference = exp - modManager.getExp(item);
						modManager.addExp(null, item, difference);
					}

					if (config.getBoolean("ConvertMobDrops.ApplyModifiers", true)) {
						List<Modifier> mods = modManager.getAllowedMods();
						for (int i = 0; i < rand.nextInt(config.getInt("ConvertMobDrops.MaximumNumberOfModifiers", 4) + 1); i++) {
							if (config.getBoolean("ConvertMobDrops.AppliedModifiersConsiderSlots", true)
									&& modManager.getFreeSlots(item) == 0) {
								break;
							}
							for (int j = 0; j < 2; j++) { //to give an extra chance
								final int index = rand.nextInt(mods.size());
								final Modifier mod = mods.get(index);
								if (modManager.addMod(player, item, mod, true, true, true)) {
									if (config.getBoolean("ConvertMobDrops.AppliedModifiersConsiderSlots", true)) {
										modManager.setFreeSlots(item, modManager.getFreeSlots(item) - 1);
									}
									break;
								}
							}
						}
					}
				}
			}
		}

		if (config.getBoolean("MobDropModifierItems.Enabled", true)) {
			if (config.getBoolean("MobDropModifierItems.ConsiderIncludedMobs") ==
					config.getStringList("MobDropModifierItems.IncludedMobs").contains(mob.getType().name())) {
				Random rand = new Random();
				if (rand.nextInt(100) < config.getInt("MobDropModifierItems.Chance", 2)) {
					int amount = rand.nextInt(config.getInt("MobDropModifierItems.MaximumAmount") + 1);
					final List<Modifier> mods = modManager.getAllowedMods();
					for (int i = 0; i < amount; i++) {
						final int index = rand.nextInt(mods.size());
						final Modifier mod = mods.get(index);
						if (!config.getStringList("MobDropModifierItems.ExcludeModifiers").contains(mod.getKey())) {
							event.getDrops().add(mod.getModItem().clone());
						}
					}
				}
			}
		}

		Bukkit.getPluginManager().callEvent(new MTEntityDeathEvent(player, tool, event));

		modManager.addExp(player, tool, MineTinker.getPlugin().getConfig().getInt("ExtraExpPerEntityDeath."
				+ event.getEntity().getType().toString(), 0));
	}

	@EventHandler(ignoreCancelled = true)
	public void onArrowHit(@NotNull final ProjectileHitEvent event) {
		if (!(event.getEntity().getShooter() instanceof Player)) {
			return;
		}

		final Player player = (Player) event.getEntity().getShooter();
		ItemStack tool = player.getInventory().getItemInMainHand();

		if (event.getHitBlock() == null && !ToolType.FISHINGROD.contains(tool.getType())) {
			return;
		}

		if (event.getEntity() instanceof Trident) {
			final Trident trident = (Trident) event.getEntity();
			// Intellij gets confused if this isn't assigned to a variable

			tool = TridentListener.TridentToItemStack.get(trident);
			TridentListener.TridentToItemStack.remove(trident);

			if (tool == null) {
				return;
			}
		}

		if (!modManager.isToolViable(tool)) {
			return;
		}

		Bukkit.getPluginManager().callEvent(new MTProjectileHitEvent(player, tool, event));
	}

	@EventHandler(ignoreCancelled = true)
	public void onProjectileLaunch(@NotNull final ProjectileLaunchEvent event) {
		if (!(event.getEntity().getShooter() instanceof Player)) {
			return;
		}

		final Player player = (Player) event.getEntity().getShooter();
		final ItemStack tool = player.getInventory().getItemInMainHand();

		// This isn't the best detection, if the player has a non modifier in one hand and
		// one in the other, this won't know which was actually thrown.
		// TODO: Maybe improve this before release.
		// It works as a safeguard in general though.
		if (modManager.isModifierItem(tool)) {
			event.setCancelled(true);
			player.updateInventory();
			player.setCooldown(tool.getType(), 10);
			return;
		} else if (modManager.isModifierItem(player.getInventory().getItemInOffHand())) {
			event.setCancelled(true);
			player.updateInventory();
			player.setCooldown(player.getInventory().getItemInOffHand().getType(), 10);
			return;
		}

		if (!modManager.isToolViable(tool)) {
			return;
		}

		if (!modManager.durabilityCheck(event, player, tool)) {
			return;
		}

		modManager.addExp(player, tool, MineTinker.getPlugin().getConfig().getInt("ExpPerArrowShot"));

        /*
        Self-Repair and Experienced will no longer trigger on bowfire
         */
	}

	@EventHandler(ignoreCancelled = true)
	public void onBowShoot(EntityShootBowEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}

		final Player player = (Player) event.getEntity();
		final ItemStack offHand = player.getInventory().getItemInOffHand();

		if (offHand.getType() == Material.ARROW) {
			final Modifier mod = modManager.getModifierFromItem(offHand);

			if (mod != null && mod.getModItem().getType() == Material.ARROW) {
				event.setCancelled(true);

				player.updateInventory();
				player.playSound(player.getLocation(), Sound.ITEM_CROSSBOW_LOADING_END, 1.0f, 1.0f);

				return;
			}
		}

		for (ItemStack item : player.getInventory().getContents()) {
			if (item == null) continue; // Extremely consistently null

			if (item.getType() == Material.ARROW) {
				final Modifier mod = modManager.getModifierFromItem(item);
				if (mod != null && mod.getModItem().getType() == Material.ARROW) {
					event.setCancelled(true);
					player.updateInventory();
					player.playSound(player.getLocation(), Sound.ITEM_CROSSBOW_LOADING_END, 1.0f, 1.0f);
					return;
				}
				return;
			}
		}
	}
}
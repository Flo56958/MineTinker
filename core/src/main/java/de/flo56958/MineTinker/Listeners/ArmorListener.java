package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.MTEntityDamageByEntityEvent;
import de.flo56958.MineTinker.Events.MTEntityDamageEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class ArmorListener implements Listener {

	private static final ModManager modManager = ModManager.instance();
	private static final ArrayList<EntityDamageEvent.DamageCause> blacklistedCauses = new ArrayList<>();

	static {
		//List to disable XP-Farming on Armor with Suicide-Commands
		blacklistedCauses.add(EntityDamageEvent.DamageCause.SUICIDE); //vanilla Suicide command
		blacklistedCauses.add(EntityDamageEvent.DamageCause.VOID); //other Suicide commands
		blacklistedCauses.add(EntityDamageEvent.DamageCause.CUSTOM); //Essentials Suicide
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerDamage(EntityDamageEvent event) {
		if (event.getDamage() <= 0) {
			return;
		}

		if (Lists.WORLDS.contains(event.getEntity().getWorld().getName())) {
			return;
		}

		if (!(event.getEntity() instanceof Player)) {
			return;
		}

		if (blacklistedCauses.contains(event.getCause())) {
			return;
		}

		Player player = (Player) event.getEntity();

		Entity entity = null;
		EntityDamageByEntityEvent byEntityEvent = null;

		if (event instanceof EntityDamageByEntityEvent) {
			byEntityEvent = (EntityDamageByEntityEvent)event;
			entity = byEntityEvent.getDamager();

			if (entity instanceof Arrow) {
				Arrow arrow = (Arrow) entity;
				ProjectileSource source = arrow.getShooter();

				if (source instanceof Entity) {
					entity = (Entity) source;
				} else {
					return;
				}
			}

		}

		ArrayList<ItemStack> armor = new ArrayList<>(Arrays.asList(player.getInventory().getArmorContents()));
		if (ToolType.SHIELD.contains(player.getInventory().getItemInMainHand().getType())) armor.add(player.getInventory().getItemInMainHand());
		else if (ToolType.SHIELD.contains(player.getInventory().getItemInOffHand().getType())) armor.add(player.getInventory().getItemInOffHand());

		for (ItemStack piece : armor) {
			if (!modManager.isArmorViable(piece)) {
				continue;
			}

			if (byEntityEvent != null) {
				Bukkit.getPluginManager().callEvent(new MTEntityDamageByEntityEvent(player, piece, entity, byEntityEvent));
			} else {
				Bukkit.getPluginManager().callEvent(new MTEntityDamageEvent(player, piece, event));
			}
		}
	}

	//Handle exp calculation for both armor and weapons
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void expCalculation(MTEntityDamageEvent event) {
		FileConfiguration config = Main.getPlugin().getConfig();

		int amount = config.getInt("ExpPerEntityHit");

		if (config.getBoolean("EnableDamageExp")) {
			amount = (int) event.getEvent().getDamage() / 2;
		}

		if (config.getBoolean("DisableExpFromFalldamage", false)
				&& event.getEvent().getCause() == EntityDamageEvent.DamageCause.FALL) {
			return;
		}

		modManager.addExp(event.getPlayer(), event.getTool(), amount);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void expCalculation(MTEntityDamageByEntityEvent event) {
		FileConfiguration config = Main.getPlugin().getConfig();

		int amount = config.getInt("ExpPerEntityHit");

		if (config.getBoolean("EnableDamageExp")) {
			amount = (int) event.getEvent().getDamage() / 2;
		}

		if (config.getBoolean("DisableExpFromFalldamage", false)
				&& event.getEvent().getCause() == EntityDamageEvent.DamageCause.FALL) {
			return;
		}

		modManager.addExp(event.getPlayer(), event.getTool(), amount);
	}

	@EventHandler(ignoreCancelled = true)
	public void onElytraDamage(PlayerItemDamageEvent event) {
		if (!event.getPlayer().isGliding()) {
			return;
		}

		if (event.getItem().getType() != Material.ELYTRA) {
			return;
		}

		if (!modManager.isArmorViable(event.getItem())) {
			return;
		}

		Random rand = new Random();
		int chance = rand.nextInt(100);

		if (chance < ConfigurationManager.getConfig("Elytra.yml").getInt("ExpChanceWhileFlying")) {
			modManager.addExp(event.getPlayer(), event.getItem(), Main.getPlugin().getConfig().getInt("ExpPerEntityHit"));
		}
	}
}

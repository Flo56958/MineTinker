package de.flo56958.minetinker.listeners;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.data.Lists;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.events.MTEntityDamageByEntityEvent;
import de.flo56958.minetinker.events.MTEntityDamageEvent;
import de.flo56958.minetinker.modifiers.ModManager;
import de.flo56958.minetinker.utils.ConfigurationManager;
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
import org.jetbrains.annotations.NotNull;

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

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerDamage(@NotNull final EntityDamageEvent event) {
		if (event.getDamage() <= 0) {
			return;
		}

		if (Lists.WORLDS.contains(event.getEntity().getWorld().getName())) {
			return;
		}

		if (!(event.getEntity() instanceof final Player player)) {
			return;
		}

		if (blacklistedCauses.contains(event.getCause())) {
			return;
		}

		Entity entity = null;
		EntityDamageByEntityEvent byEntityEvent = null;

		if (event instanceof EntityDamageByEntityEvent) {
			byEntityEvent = (EntityDamageByEntityEvent)event;
			entity = byEntityEvent.getDamager();

			if (entity instanceof final Arrow arrow) {
				final ProjectileSource source = arrow.getShooter();

				if (source instanceof Entity) {
					entity = (Entity) source;
				} else {
					return;
				}
			}
		}

		final ArrayList<ItemStack> armor = new ArrayList<>(Arrays.asList(player.getInventory().getArmorContents()));
		if (ToolType.SHIELD.contains(player.getInventory().getItemInMainHand().getType()))
			armor.add(player.getInventory().getItemInMainHand());
		else if (ToolType.SHIELD.contains(player.getInventory().getItemInOffHand().getType()))
			armor.add(player.getInventory().getItemInOffHand());

		final boolean isBlocking = player.isBlocking() && event.getFinalDamage() == 0.0d;
		for (ItemStack piece : armor) {
			if (!modManager.isArmorViable(piece)) {
				continue;
			}

			if (byEntityEvent != null) {
				Bukkit.getPluginManager().callEvent(new MTEntityDamageByEntityEvent(player, piece, entity, byEntityEvent, isBlocking));
			} else {
				Bukkit.getPluginManager().callEvent(new MTEntityDamageEvent(player, piece, event, isBlocking));
			}
		}
	}

	//Handle exp calculation for both armor and weapons
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void expCalculation(@NotNull final MTEntityDamageEvent event) {
		expCalculation(event.isBlocking(), event.getTool(), event.getEvent(), event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void expCalculation(@NotNull final MTEntityDamageByEntityEvent event) {
		expCalculation(event.isBlocking(), event.getTool(), event.getEvent(), event.getPlayer());
	}

	private void expCalculation(final boolean isBlocking, @NotNull final ItemStack tool, @NotNull final EntityDamageEvent event, @NotNull final Player player) {
		//Armor should not get Exp when successfully blocking
		if(isBlocking && !ToolType.SHIELD.contains(tool.getType()) && ToolType.ARMOR.contains(tool.getType())) return;
		//Shield should not get Exp when not successfully blocking when getting attacked
		if(!isBlocking && player.equals(event.getEntity()) && ToolType.SHIELD.contains(tool.getType())) return;
		FileConfiguration config = MineTinker.getPlugin().getConfig();
		int amount = config.getInt("ExpPerEntityHit", 1);

		if (config.getBoolean("EnableDamageExp", true)) {
			amount = Math.max(amount, (int) Math.round(event.getFinalDamage()));
			//Max because the lowest exp amount you should get is ExpPerEntityHit
		}

		if (config.getBoolean("DisableExpFromFalldamage", false)
				&& event.getCause() == EntityDamageEvent.DamageCause.FALL) {
			return;
		}

		modManager.addExp(player, tool, amount);
	}

	@EventHandler(ignoreCancelled = true)
	public void onElytraDamage(@NotNull final PlayerItemDamageEvent event) {
		if (!event.getPlayer().isGliding()) {
			return;
		}

		if (event.getItem().getType() != Material.ELYTRA) {
			return;
		}

		if (!modManager.isArmorViable(event.getItem())) {
			return;
		}

		final int chance = new Random().nextInt(100);
		if (chance < ConfigurationManager.getConfig("Elytra.yml").getInt("ExpChanceWhileFlying")) {
			modManager.addExp(event.getPlayer(), event.getItem(), MineTinker.getPlugin().getConfig().getInt("ExpPerEntityHit"));
		}
	}
}

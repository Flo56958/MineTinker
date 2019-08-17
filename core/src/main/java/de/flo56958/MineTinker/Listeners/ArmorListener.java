package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Events.MTEntityDamageByEntityEvent;
import de.flo56958.MineTinker.Events.MTEntityDamageEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Types.SelfRepair;
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

import java.util.Random;

public class ArmorListener implements Listener {

    private static final ModManager modManager = ModManager.instance();
    private static final FileConfiguration config = Main.getPlugin().getConfig();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamage() <= 0) {
            return;
        }

        if (Lists.WORLDS.contains(event.getEntity().getWorld().getName())) {
            return;
        }

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (event.getCause().equals(EntityDamageEvent.DamageCause.SUICIDE) || event.getCause().equals(EntityDamageEvent.DamageCause.VOID)) {
            return;
        }

        Player player = (Player) event.getEntity();

        Entity entity = event.getDamager();

        if (entity instanceof Arrow) {
            Arrow arrow = (Arrow) entity;
            ProjectileSource source = arrow.getShooter();

            if (source instanceof Entity) {
                entity = (Entity) source;
            } else {
                return;
            }
        }

        ItemStack[] armor = player.getInventory().getArmorContents();

        for (ItemStack piece : armor) {
            if (!modManager.isArmorViable(piece)) {
                continue;
            }

            MTEntityDamageByEntityEvent damageByEntityEvent = new MTEntityDamageByEntityEvent(player, piece, entity, event);
            Bukkit.getPluginManager().callEvent(damageByEntityEvent);

            int amount = config.getInt("ExpPerEntityHit");

            if (config.getBoolean("EnableDamageExp")) {
                amount = (int) event.getDamage();
            }

            modManager.addExp(player, piece, amount);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageEvent event) {
        if (Lists.WORLDS.contains(event.getEntity().getWorld().getName())) {
            return;
        }

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (event.getCause().equals(EntityDamageEvent.DamageCause.SUICIDE) || event.getCause().equals(EntityDamageEvent.DamageCause.VOID)) {
            return;
        }

        Player p = (Player) event.getEntity();

        ItemStack[] armor = p.getInventory().getArmorContents();

        for (ItemStack piece : armor) {
            if (!modManager.isArmorViable(piece)) {
                continue;
            }

            Bukkit.getPluginManager().callEvent(new MTEntityDamageEvent(p, piece, event));

            int amount = config.getInt("ExpPerEntityHit") / 2;

            if (config.getBoolean("EnableDamageExp")) {
                amount = (int) event.getDamage() / 2;
            }

            modManager.addExp(p, piece, amount);
        }
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

        if (chance < ConfigurationManager.getConfig("Elytra.yml").getInt("Elytra.ExpChanceWhileFlying")) {
            modManager.addExp(event.getPlayer(), event.getItem(), config.getInt("ExpPerEntityHit"));
        }

        SelfRepair.instance().effectElytra(event.getPlayer(), event.getItem());
    }
}

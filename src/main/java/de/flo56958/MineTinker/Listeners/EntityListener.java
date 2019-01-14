package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Events.MTEntityDamageByEntityEvent;
import de.flo56958.MineTinker.Events.MTEntityDeathEvent;
import de.flo56958.MineTinker.Events.MTProjectileHitEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.ModManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

public class EntityListener implements Listener {

    private static final ModManager modManager = ModManager.instance();
    private static final FileConfiguration config = Main.getPlugin().getConfig();

    @SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.isCancelled()) { return; }
        if (Lists.WORLDS.contains(e.getDamager().getWorld().getName())) { return; }

        Player p;

        if (e.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow) e.getDamager();
            ProjectileSource source = arrow.getShooter();
            if (source instanceof Player) {
                p = (Player) source;
            } else { return; }
        } else if (e.getDamager() instanceof Player) {
            p = (Player) e.getDamager();
        } else { return; }

        ItemStack tool = p.getInventory().getItemInMainHand();
        if (!modManager.isToolViable(tool)) { return; }
        if (!modManager.durabilityCheck(e, p, tool)) { return; }

        int amount = config.getInt("ExpPerEntityHit");

        Bukkit.getPluginManager().callEvent(new MTEntityDamageByEntityEvent(p, tool, e));

        if (config.getBoolean("EnableDamageExp")) { //at bottom because of Melting
            amount = (int) e.getDamage();
        }
        amount += config.getInt("ExtraExpPerEntityHit." + e.getEntity().getType().toString()); //adds 0 if not in found in config (negative values are also fine)
        modManager.addExp(p, tool, amount);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        LivingEntity mob = e.getEntity();
        Player p = mob.getKiller();
        if (p == null) { return; }
        if (Lists.WORLDS.contains(p.getWorld().getName())) { return; }
        ItemStack tool = p.getInventory().getItemInMainHand();

        if (!modManager.isToolViable(tool)) { return; }

        Bukkit.getPluginManager().callEvent(new MTEntityDeathEvent(p, tool, e));

        modManager.addExp(p, tool, config.getInt("ExtraExpPerEntityDeath." + e.getEntity().getType().toString())); //adds 0 if not in found in config (negative values are also fine)
    }

    @EventHandler
    public void onArrowHit(ProjectileHitEvent e) {
        if (!(e.getEntity().getShooter() instanceof Player)) { return; }
        Player p = (Player) e.getEntity().getShooter();
        ItemStack tool = p.getInventory().getItemInMainHand();
        if (e.getHitBlock() == null) { return; }
        if (!modManager.isToolViable(tool)) { return; }

        Bukkit.getPluginManager().callEvent(new MTProjectileHitEvent(p, tool, e));
    }

	@EventHandler
    public void onBowFire(ProjectileLaunchEvent e) {
        if (e.isCancelled()) { return; }
        if (!(e.getEntity().getShooter() instanceof Player)) { return; }

        Player p = (Player) e.getEntity().getShooter();
        ItemStack tool = p.getInventory().getItemInMainHand();

        if (!modManager.isToolViable(tool)) { return; }
        if (!modManager.durabilityCheck(e, p, tool)) { return; }


        modManager.addExp(p, tool, config.getInt("ExpPerArrowShot"));

        /*
        Self-Repair and Experienced will no longer trigger on bowfire
         */
    }
}
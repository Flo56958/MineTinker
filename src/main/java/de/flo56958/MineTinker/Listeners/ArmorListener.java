package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Events.MTEntityDamageByEntityEvent;
import de.flo56958.MineTinker.Events.MTEntityDamageEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Types.ModifierType;
import de.flo56958.MineTinker.Modifiers.Types.SelfRepair;
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

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (e.isCancelled()) { return; }
        if (Lists.WORLDS.contains(e.getEntity().getWorld().getName())) { return; }

        if (!(e.getEntity() instanceof Player)) { return; }

        Player p = (Player) e.getEntity();

        Entity ent = e.getDamager();

        if (ent instanceof Arrow) {
            Arrow arrow = (Arrow) ent;
            ProjectileSource source = arrow.getShooter();
            if (source instanceof Entity) {
                ent = (Entity) source;
            } else {
                return;
            }
        }

        ItemStack[] armor = p.getInventory().getArmorContents();

        for (ItemStack piece : armor) {
            if (!modManager.isArmorViable(piece)) { continue; }

            Bukkit.getPluginManager().callEvent(new MTEntityDamageByEntityEvent(p, piece, ent, e));

            int amount = config.getInt("ExpPerEntityHit");
            if (config.getBoolean("EnableDamageExp")) {
                amount = (int) e.getDamage();
            }
            modManager.addExp(p, piece, amount);
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        if (e.isCancelled()) { return; }
        if (Lists.WORLDS.contains(e.getEntity().getWorld().getName())) { return; }

        if (!(e.getEntity() instanceof Player)) { return; }

        Player p = (Player) e.getEntity();

        ItemStack[] armor = p.getInventory().getArmorContents();

        for (ItemStack piece : armor) {
            if (!modManager.isArmorViable(piece)) { continue; }

            Bukkit.getPluginManager().callEvent(new MTEntityDamageEvent(p, piece, e));

            int amount = config.getInt("ExpPerEntityHit") / 2;
            if (config.getBoolean("EnableDamageExp")) {
                amount = (int) e.getDamage() / 2;
            }
            modManager.addExp(p, piece, amount);
        }
    }

    @EventHandler
    public void onElytraDamage(PlayerItemDamageEvent e) {
        if (e.isCancelled()) { return; }

        if (!e.getItem().getType().equals(Material.ELYTRA)) { return; }
        if (!modManager.isArmorViable(e.getItem())) { return; }

        Random rand = new Random();
        int chance = rand.nextInt(100);
        if (chance < 25) {
            modManager.addExp(e.getPlayer(), e.getItem(), config.getInt("ExpPerEntityHit"));
        }
        ((SelfRepair) modManager.getAdmin(ModifierType.SELF_REPAIR)).effectElytra(e.getPlayer(), e.getItem());
    }
}

package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Types.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public class ArmorListener implements Listener {

    private static final ModManager modManager = Main.getModManager();
    private static final FileConfiguration config = Main.getPlugin().getConfig();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (e.isCancelled()) { return; }
        if (!Lists.WORLDS.contains(e.getEntity().getWorld().getName())) { return; }

        if (!(e.getEntity() instanceof Player)) { return; }

        Player p = (Player) e.getEntity();

        Entity damager = e.getDamager();

        if (e.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow) e.getDamager();
            if (arrow.getShooter() instanceof Entity) {
                damager = (Entity) arrow.getShooter();
            }
        }

        ItemStack[] armor = p.getInventory().getArmorContents();

        for (ItemStack piece : armor) {
            if (!modManager.isArmorViable(piece)) { continue; }

            if (modManager.get(ModifierType.POISONOUS) != null) {
                ((Poisonous) modManager.get(ModifierType.POISONOUS)).effect(p, piece, damager);
            }

            if (modManager.get(ModifierType.SHULKING) != null) {
                ((Shulking) modManager.get(ModifierType.SHULKING)).effect(p, piece, damager);
            }

            if (modManager.get(ModifierType.WEBBED) != null) {
                ((Webbed) modManager.get(ModifierType.WEBBED)).effect(p, piece, damager);
            }

            if (modManager.get(ModifierType.MELTING) != null) {
                ((Melting) modManager.get(ModifierType.MELTING)).effect_armor(p, piece, e);
            }

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
        if (!Lists.WORLDS.contains(e.getEntity().getWorld().getName())) { return; }

        if (!(e.getEntity() instanceof Player)) { return; }

        Player p = (Player) e.getEntity();

        ItemStack[] armor = p.getInventory().getArmorContents();

        for (ItemStack piece : armor) {
            if (!modManager.isArmorViable(piece)) { continue; }

            if (modManager.get(ModifierType.SELF_REPAIR) != null) {
                ((SelfRepair) modManager.get(ModifierType.SELF_REPAIR)).effect(p, piece);
            }

            if (modManager.get(ModifierType.EXPERIENCED) != null) {
                ((Experienced) modManager.get(ModifierType.EXPERIENCED)).effect(p, piece);
            }

            if (modManager.get(ModifierType.MELTING) != null) {
                ((Melting) modManager.get(ModifierType.MELTING)).effect_armor(p, piece);
            }

            int amount = config.getInt("ExpPerEntityHit") / 2;
            if (config.getBoolean("EnableDamageExp")) {
                amount = (int) e.getDamage() / 2;
            }
            modManager.addExp(p, piece, amount);
        }
    }
}

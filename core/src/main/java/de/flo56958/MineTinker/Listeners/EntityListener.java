package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.MTEntityDamageByEntityEvent;
import de.flo56958.MineTinker.Events.MTEntityDeathEvent;
import de.flo56958.MineTinker.Events.MTProjectileHitEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.nms.NBTUtils;
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

public class EntityListener implements Listener {

    private static final ModManager modManager = ModManager.instance();
    private static final FileConfiguration config = Main.getPlugin().getConfig();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent e) {
        if (Lists.WORLDS.contains(e.getDamager().getWorld().getName())) return;

        Player p;

        if (e.getDamager() instanceof Arrow && !(e.getDamager() instanceof Trident)) {
            Arrow arrow = (Arrow) e.getDamager();
            ProjectileSource source = arrow.getShooter();

            if (source instanceof Player) p = (Player) source;
            else return;

        } else if (e.getDamager() instanceof Trident) {
            Trident trident = (Trident) e.getDamager();
            ProjectileSource source = trident.getShooter();

            if (source instanceof Player)
                p = (Player) source;
            else return;

        } else if (e.getDamager() instanceof Player)
            p = (Player) e.getDamager();
        else return;

        /*
        if (e.getEntity() instanceof Player) {
            if (((Player) e.getEntity()).isBlocking()) return;
        } */

        ItemStack tool = p.getInventory().getItemInMainHand();

        if (e.getDamager() instanceof Trident) {
            tool = TridentListener.TridentToItemStack.get(e.getDamager());

            Trident trident = (Trident)e.getDamager();
            TridentListener.TridentToItemStack.remove(trident);

            if (tool == null) {
                return;
            }
        }

        if (!modManager.isToolViable(tool)) return;
        if (!modManager.durabilityCheck(e, p, tool)) return;

        int amount = config.getInt("ExpPerEntityHit");

        Bukkit.getPluginManager().callEvent(new MTEntityDamageByEntityEvent(p, tool, e.getEntity(), e));

        if (config.getBoolean("EnableDamageExp")) //at bottom because of Melting
            amount = (int) e.getDamage();

        amount += config.getInt("ExtraExpPerEntityHit." + e.getEntity().getType().toString()); //adds 0 if not in found in config (negative values are also fine)
        modManager.addExp(p, tool, amount);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        LivingEntity mob = e.getEntity();
        Player p = mob.getKiller();

        if (p == null) return;
        if (Lists.WORLDS.contains(p.getWorld().getName())) return;

        ItemStack tool = p.getInventory().getItemInMainHand();

        if (!modManager.isToolViable(tool)) return;

        Bukkit.getPluginManager().callEvent(new MTEntityDeathEvent(p, tool, e));

        modManager.addExp(p, tool, config.getInt("ExtraExpPerEntityDeath." + e.getEntity().getType().toString())); //adds 0 if not in found in config (negative values are also fine)
    }

    @EventHandler
    public void onArrowHit(ProjectileHitEvent e) {
        if (!(e.getEntity().getShooter() instanceof Player)) return;
        Player p = (Player) e.getEntity().getShooter();
        ItemStack tool = p.getInventory().getItemInMainHand();

        if (e.getHitBlock() == null && !ToolType.FISHINGROD.getMaterials().contains(tool.getType())) return;

        if (e.getEntity() instanceof Trident) {
            Trident trident = (Trident)e.getEntity(); // Intellij gets confused if this isn't assigned to a variable

            tool = TridentListener.TridentToItemStack.get(trident);
            TridentListener.TridentToItemStack.remove(trident);

            if (tool == null) return;
        }

        if (!modManager.isToolViable(tool)) return;

        Bukkit.getPluginManager().callEvent(new MTProjectileHitEvent(p, tool, e));
    }

	@EventHandler(ignoreCancelled = true)
    public void onProjectileLaunch(ProjectileLaunchEvent e) {
        if (!(e.getEntity().getShooter() instanceof Player)) return;

        Player p = (Player) e.getEntity().getShooter();
        ItemStack tool = p.getInventory().getItemInMainHand();

        // In the check below this, experience bottles are not throwable by default
        // This is because they're Experienced Modifier Items
        if (tool.getType() == Material.EXPERIENCE_BOTTLE) {
            return;
        }

        // This isn't the best detection, if the player has a non modifier in one hand and
        // one in the other, this won't know which was actually thrown.
        // Maybe improve this before release.
        // It works as a safeguard in general though.
        if (modManager.isModifierItem(tool) || modManager.isModifierItem(p.getInventory().getItemInOffHand())) {
            e.setCancelled(true);
            p.updateInventory();
            p.setCooldown(Material.ENDER_PEARL, 10);
        }

        if (!modManager.isToolViable(tool)) return;
        if (!modManager.durabilityCheck(e, p, tool)) return;

        modManager.addExp(p, tool, config.getInt("ExpPerArrowShot"));

        /*
        Self-Repair and Experienced will no longer trigger on bowfire
         */
    }

    @EventHandler(ignoreCancelled = true)
    public void onBowShoot(EntityShootBowEvent e) {
        if (!(e.getEntity() instanceof Player)) return;

        Player player = (Player) e.getEntity();
        ItemStack offHand = player.getInventory().getItemInOffHand();

        if (offHand.getType() == Material.ARROW) {
            Modifier mod = modManager.getModifierFromItem(offHand);

            if (mod != null && mod.getModItem().getType() == Material.ARROW) {
                e.setCancelled(true);
                player.updateInventory();

                if (NBTUtils.isOneFourteenCompatible()) player.playSound(player.getLocation(), Sound.ITEM_CROSSBOW_LOADING_END, 1.0f, 1.0f);

                return;
            }
        }

        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) continue; // Extremely consistently null

            if (item.getType() == Material.ARROW) {
                Modifier mod = modManager.getModifierFromItem(item);

                if (mod != null && mod.getModItem().getType() == Material.ARROW) {
                    e.setCancelled(true);

                    player.updateInventory();

                    if (NBTUtils.isOneFourteenCompatible()) player.playSound(player.getLocation(), Sound.ITEM_CROSSBOW_LOADING_END, 1.0f, 1.0f);

                    return;
                }

                return;
            }
        }
    }
}
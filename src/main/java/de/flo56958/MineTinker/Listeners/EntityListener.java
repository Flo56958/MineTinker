package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Types.*;
import org.bukkit.Material;
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

        //-------------------------------------------MODIFIERS---------------------------------------------
        if (!ToolType.BOW.getMaterials().contains(tool.getType())) {
            if (modManager.get(ModifierType.SELF_REPAIR) != null) {
                ((SelfRepair) modManager.get(ModifierType.SELF_REPAIR)).effect(p, tool);
            }
            if (modManager.get(ModifierType.EXPERIENCED) != null) {
                ((Experienced) modManager.get(ModifierType.EXPERIENCED)).effect(p, tool);
            }
        }

        if (modManager.get(ModifierType.GLOWING) != null) {
            ((Glowing) modManager.get(ModifierType.GLOWING)).effect(p, tool, e);
        }

        if (modManager.get(ModifierType.POISONOUS) != null) {
            ((Poisonous) modManager.get(ModifierType.POISONOUS)).effect(p, tool, e.getEntity());
        }

        if (modManager.get(ModifierType.SHULKING) != null) {
            ((Shulking) modManager.get(ModifierType.SHULKING)).effect(p, tool, e.getEntity());
        }

        if (modManager.get(ModifierType.WEBBED) != null) {
            ((Webbed) modManager.get(ModifierType.WEBBED)).effect(p, tool, e.getEntity());
        }

        if (modManager.get(ModifierType.MELTING) != null) {
            ((Melting) modManager.get(ModifierType.MELTING)).effect(p, tool, e);
        }

        if (modManager.get(ModifierType.ENDER) != null) {
            ((Ender) modManager.get(ModifierType.ENDER)).effect(p, tool, e);
        }
        //-------------------------------------------MODIFIERS---------------------------------------------

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

        ItemStack loot = new ItemStack(Material.AIR, 1);

        //-------------------------------------------MODIFIERS---------------------------------------------
        if (modManager.get(ModifierType.BEHEADING) != null) {
            loot = ((Beheading) modManager.get(ModifierType.BEHEADING)).effect(p, tool, mob);
        }

        if (modManager.get(ModifierType.DIRECTING) != null) {
            ((Directing) modManager.get(ModifierType.DIRECTING)).effect(p, tool, loot, e);
        } else {
            if (!loot.equals(new ItemStack(Material.AIR, 1))) {
                p.getWorld().dropItemNaturally(e.getEntity().getLocation(), loot);
            }
        }

        modManager.addExp(p, tool, config.getInt("ExtraExpPerEntityHit." + e.getEntity().getType().toString())); //adds 0 if not in found in config (negative values are also fine)
    }

    @EventHandler
    public void onArrowHit(ProjectileHitEvent e) {
        if (!(e.getEntity().getShooter() instanceof Player)) { return; }
        Player p = (Player) e.getEntity().getShooter();
        ItemStack tool = p.getInventory().getItemInMainHand();
        if (e.getHitBlock() == null) { return; }
        if (!modManager.isToolViable(tool)) { return; }

        //-------------------------------------------MODIFIERS---------------------------------------------
        if (modManager.get(ModifierType.ENDER) != null) {
            ((Ender) modManager.get(ModifierType.ENDER)).effect(p, tool, e);
        }
    }

    @SuppressWarnings("deprecation")
	@EventHandler
    public void onBowFire(ProjectileLaunchEvent e) {
        if (e.isCancelled()) { return; }
        if (!(e.getEntity().getShooter() instanceof Player)) { return; }

        Player p = (Player) e.getEntity().getShooter();
        ItemStack tool = p.getInventory().getItemInMainHand();

        if (!modManager.isToolViable(tool)) { return; }
        if (!modManager.durabilityCheck(e, p, tool)) { return; }


        modManager.addExp(p, tool, config.getInt("ExpPerArrowShot"));

        //-------------------------------------------MODIFIERS---------------------------------------------
        if (modManager.get(ModifierType.SELF_REPAIR) != null) {
            ((SelfRepair) modManager.get(ModifierType.SELF_REPAIR)).effect(p, tool);
        }

        if (modManager.get(ModifierType.EXPERIENCED) != null) {
            ((Experienced) modManager.get(ModifierType.EXPERIENCED)).effect(p, tool);
        }

    }
}

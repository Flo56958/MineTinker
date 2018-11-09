package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Types.*;
import de.flo56958.MineTinker.Utilities.PlayerInfo;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.projectiles.ProjectileSource;

import java.util.List;

public class EntityListener implements Listener {

    private static final ModManager modManager = Main.getModManager();
    private static final FileConfiguration config = Main.getPlugin().getConfig();

    @EventHandler
    public void onDamage (EntityDamageByEntityEvent e) {
        if (e.isCancelled()) { return; }
        if (!Lists.WORLDS.contains(e.getDamager().getWorld().getName())) { return; }

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
        if (!PlayerInfo.isToolViable(tool)) { return; }

        ItemMeta meta = tool.getItemMeta();
        List<String> lore = meta.getLore();

        if (tool.getType().getMaxDurability() - tool.getDurability() <= 1) {
            e.setCancelled(true);
            if (config.getBoolean("Sound.OnBreaking")) {
                p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.5F, 0.5F);
            }
            return;
        }
        int amount = config.getInt("ExpPerEntityHit"); //AddExp at bottom because of Melting

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
            ((Poisonous) modManager.get(ModifierType.POISONOUS)).effect(p, tool, e);
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

        if (config.getBoolean("EnableDamageExp")) {
            amount = (int) e.getDamage();
        }
        modManager.addExp(p, tool, amount); //at bottom because of Melting
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        LivingEntity mob = e.getEntity();
        Player p = mob.getKiller();
        if (p == null) { return; }
        if (!Lists.WORLDS.contains(p.getWorld().getName())) { return; }
        ItemStack tool = p.getInventory().getItemInMainHand();

        if (!PlayerInfo.isToolViable(tool)) { return; }

        ItemMeta meta = tool.getItemMeta();
        List<String> lore = meta.getLore();

        ItemStack loot = new ItemStack(Material.AIR, 1);
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
    }

    @EventHandler
    public void onArrowHit(ProjectileHitEvent e) {
        if (!(e.getEntity().getShooter() instanceof Player)) { return; }
        Player p = (Player) e.getEntity().getShooter();
        ItemStack tool = p.getInventory().getItemInMainHand();
        if (e.getHitBlock() != null) {
            if (!PlayerInfo.isToolViable(tool)) { return; }

            if (modManager.get(ModifierType.ENDER) != null) {
                ((Ender) modManager.get(ModifierType.ENDER)).effect(p, tool, e);
            }
        }
    }

    @EventHandler
    public void onBowFire(ProjectileLaunchEvent e) {
        if (e.isCancelled()) { return; }
        if (!(e.getEntity().getShooter() instanceof Player)) { return; }

        Player p = (Player) e.getEntity().getShooter();
        ItemStack tool = p.getInventory().getItemInMainHand();

        if (!PlayerInfo.isToolViable(tool)) { return; }

        ItemMeta meta = tool.getItemMeta();
        List<String> lore = meta.getLore();

        if (tool.getType().getMaxDurability() - tool.getDurability() <= 1) {
            e.setCancelled(true);
            if (config.getBoolean("Sound.OnBreaking")) {
                p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.5F, 0.5F);
            }
            tool.setDurability((short) (tool.getDurability() - 1));
            return;
        }

        modManager.addExp(p, tool, config.getInt("ExpPerArrowShot"));

        if (modManager.get(ModifierType.SELF_REPAIR) != null) {
            ((SelfRepair) modManager.get(ModifierType.SELF_REPAIR)).effect(p, tool);
        }

        if (modManager.get(ModifierType.EXPERIENCED) != null) {
            ((Experienced) modManager.get(ModifierType.EXPERIENCED)).effect(p, tool);
        }

    }
}

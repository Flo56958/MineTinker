package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Data.Strings;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Utilities.*;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
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
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import java.util.List;
import java.util.Random;

public class EntityListener implements Listener {

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
        if (config.getBoolean("EnableDamageExp")) {
            amount = (int) e.getDamage();
        }

        ModifierEffect.selfRepair(p, tool);

        ModifierEffect.xp(p, tool);

        if (config.getBoolean("Modifiers.Glowing.allowed") && p.hasPermission("minetinker.modifiers.glowing.use")) {
            if (lore.contains(Strings.GLOWING)) {
                if (!e.getEntity().isDead()) {
                    try {
                        LivingEntity ent = (LivingEntity) e.getEntity();
                        ent.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, config.getInt("Modifiers.Glowing.Duration"), 0, false, false));
                    } catch (Exception ignored) {
                    }
                }
            }
        }

        if (config.getBoolean("Modifiers.Shulking.allowed") && p.hasPermission("minetinker.modifiers.shulking.use")) {
            for (int i = 1; i <= config.getInt("Modifiers.Shulking.MaxLevel"); i++) {
                if (lore.contains(Strings.SHULKING + i)) {
                    if (!e.getEntity().isDead()) {
                        try {
                            LivingEntity ent = (LivingEntity) e.getEntity();
                            ent.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, config.getInt("Modifiers.Shulking.Duration"), config.getInt("Modifiers.Shulking.EffectMultiplier") * (i - 1), false, false));
                        } catch (Exception ignored) {
                        }
                    }
                    break;
                }
            }
        }

        if (config.getBoolean("Modifiers.Poisonous.allowed") && p.hasPermission("minetinker.modifiers.poisonous.use")) {
            for (int i = 1; i <= config.getInt("Modifiers.Poisonous.MaxLevel"); i++) {
                if (lore.contains(Strings.POISONOUS + i)) {
                    if (!e.getEntity().isDead()) {
                        try {
                            LivingEntity ent = (LivingEntity) e.getEntity();
                            int duration = (int) (config.getInt("Modifiers.Poisonous.Duration") * Math.pow(config.getDouble("Modifiers.Poisonous.DurationMultiplier"), (i - 1)));
                            int amplifier = config.getInt("Modifiers.Poisonous.EffectMultiplier") * (i - 1);
                            ent.addPotionEffect(new PotionEffect(PotionEffectType.POISON, duration, amplifier, false, false));
                        } catch (Exception ignored) {
                        }
                    }
                    break;
                }
            }
        }

        if (config.getBoolean("Modifiers.Webbed.allowed") && p.hasPermission("minetinker.modifiers.webbed.use")) {
            for (int i = 1; i <= config.getInt("Modifiers.Webbed.MaxLevel"); i++) {
                if (lore.contains(Strings.WEBBED + i)) {
                    if (!e.getEntity().isDead()) {
                        try {
                            LivingEntity ent = (LivingEntity) e.getEntity();
                            int duration = (int) (config.getInt("Modifiers.Webbed.Duration") * Math.pow(config.getDouble("Modifiers.Webbed.DurationMultiplier"), (i - 1)));
                            int amplifier = config.getInt("Modifiers.Webbed.EffectMultiplier") * (i - 1) / 2;
                            ent.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration, amplifier, false, false));
                        } catch (Exception ignored) {
                        }
                    }
                    break;
                }
            }
        }

        if (config.getBoolean("Modifiers.Melting.allowed") && p.hasPermission("minetinker.modifiers.melting.use")) {
            for (int i = 1; i <= config.getInt("Modifiers.Melting.MaxLevel"); i++) {
                if (lore.contains(Strings.MELTING + i)) {
                    if (!e.getEntity().isDead()) {
                        try {
                            LivingEntity ent = (LivingEntity) e.getEntity();
                            if (ent.getFireTicks() != 0) {
                                double damage = e.getDamage();
                                damage = damage * (1 + config.getDouble("Modifiers.Melting.BonusMultiplier") * i);
                                e.setDamage(damage);
                                amount = (int) damage;
                            }
                        } catch (Exception ignored) {
                        }
                    }
                    break;
                }
            }
        }

        if (config.getBoolean("Modifiers.Ender.allowed") && p.hasPermission("minetinker.modifiers.ender.use")) {
            if (lore.contains(Strings.ENDER)) {
                if (p.isSneaking()) {
                    Location loc = e.getEntity().getLocation().clone();
                    e.getEntity().teleport(p.getLocation());
                    p.teleport(new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ()));
                    if (config.getBoolean("Modifiers.Ender.Sound")) {
                        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.3F);
                        p.getWorld().playSound(e.getEntity().getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.3F);
                    }
                }
            }
        }

        LevelCalculator.addExp(p, tool, amount); //at bottom because of Melting
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

        if (config.getBoolean("Modifiers.Beheading.allowed") && p.hasPermission("minetinker.modifiers.beheading.use")) {
            for (int i = 1; i <= config.getInt("Modifiers.Beheading.MaxLevel"); i++) {
                if (lore.contains(Strings.BEHEADING + i)) {
                    Random rand = new Random();
                    int n = rand.nextInt(100);
                    if (n <= config.getInt("Modifiers.Beheading.PercentagePerLevel") * i) {
                        if (mob.getType().equals(EntityType.CREEPER)) {
                            loot = new ItemStack(Material.CREEPER_HEAD, 1);
                        } else if (mob.getType().equals(EntityType.SKELETON)) {
                            loot = new ItemStack(Material.SKELETON_SKULL, 1);
                        } else if (mob.getType().equals(EntityType.WITHER_SKELETON)) {
                            loot = new ItemStack(Material.WITHER_SKELETON_SKULL, 1);
                        } else if (mob.getType().equals(EntityType.ZOMBIE)) {
                            loot = new ItemStack(Material.ZOMBIE_HEAD, 1);
                        } else if (mob.getType().equals(EntityType.ZOMBIE_VILLAGER)) {
                            loot = new ItemStack(Material.ZOMBIE_HEAD, 1);
                        } else if (mob.getType().equals(EntityType.PLAYER)) {
                            ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
                            SkullMeta headMeta = (SkullMeta) head.getItemMeta();
                            headMeta.setOwningPlayer((OfflinePlayer) mob);
                            head.setItemMeta(headMeta);
                            loot = head;
                        } else {
                            break;
                        }
                        ChatWriter.log(false, p.getDisplayName() + " triggered Beheading on " + ItemGenerator.getDisplayName(tool) + ChatColor.WHITE + " (" + tool.getType().toString() + ")!");
                    }
                    break;
                }
            }
        }
        if (config.getBoolean("Modifiers.Directing.allowed") && p.hasPermission("minetinker.modifiers.directing.use")) {
            if (lore.contains(Strings.DIRECTING)) {
                List<ItemStack> drops = e.getDrops();
                if (!loot.equals(new ItemStack(Material.AIR, 1))) {
                    drops.add(loot);
                }
                for (ItemStack current : drops) {
                    if(p.getInventory().addItem(current).size() != 0) { //adds items to (full) inventory
                        p.getWorld().dropItem(p.getLocation(), current);
                    } // no else as it gets added in if-clause
                }
                drops.clear();
            } else {
                if (!loot.equals(new ItemStack(Material.AIR, 1))) {
                    p.getWorld().dropItemNaturally(mob.getLocation(), loot);
                }
            }
        } else {
            if (!loot.equals(new ItemStack(Material.AIR, 1))) {
                p.getWorld().dropItemNaturally(mob.getLocation(), loot);
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

            ItemMeta meta = tool.getItemMeta();
            List<String> lore = meta.getLore();

            if (config.getBoolean("Modifiers.Ender.allowed") && p.hasPermission("minetinker.modifiers.ender.use")) {
                if (lore.contains(Strings.ENDER)) {
                    if (p.isSneaking()) {
                        Location loc = e.getEntity().getLocation().clone();
                        p.teleport(new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), p.getLocation().getYaw(), p.getLocation().getPitch()).add(0, 1, 0));
                        if (config.getBoolean("Modifiers.Ender.Sound")) {
                            p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.3F);
                        }
                    }
                }
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

        LevelCalculator.addExp(p, tool, config.getInt("ExpPerArrowShot"));

        ModifierEffect.selfRepair(p, tool);

        ModifierEffect.xp(p, tool);

    }
}

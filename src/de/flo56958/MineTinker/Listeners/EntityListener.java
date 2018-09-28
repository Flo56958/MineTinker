package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Data.Strings;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import de.flo56958.MineTinker.Utilities.LevelCalculator;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.*;
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

import java.util.ArrayList;
import java.util.Random;

public class EntityListener implements Listener {

    @EventHandler
    public void onDamage (EntityDamageByEntityEvent e) {
        if (!e.isCancelled()) {
            if (!Lists.WORLDS.contains(e.getDamager().getWorld().getName())) { return; }
            if (e.getDamager() instanceof CraftPlayer) {
                Player p = (Player) e.getDamager();
                ItemStack tool = p.getInventory().getItemInMainHand();
                if (tool.hasItemMeta()) {
                    ItemMeta meta = tool.getItemMeta();
                    if (meta.hasLore()) {
                        ArrayList<String> lore = (ArrayList<String>) meta.getLore();
                        if (lore.contains(Strings.IDENTIFIER)) {
                            if (tool.getType().getMaxDurability() - tool.getDurability() <= 1) {
                                e.setCancelled(true);
                                if (Main.getPlugin().getConfig().getBoolean("Sound.OnBreaking")) {
                                    p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.5F, 0.5F);
                                }
                                return;
                            }
                            int amount = Main.getPlugin().getConfig().getInt("ExpPerSwordSwing");
                            if (Main.getPlugin().getConfig().getBoolean("EnableDamageExp")) {
                                amount = (int) e.getDamage();
                            }
                            LevelCalculator.addExp(p, tool, amount);

                            if (Main.getPlugin().getConfig().getBoolean("Modifiers.Self-Repair.allowed") && p.hasPermission("minetinker.modifiers.selfrepair.use")) {
                                for (int i = 1; i <= Main.getPlugin().getConfig().getInt("Modifiers.Self-Repair.MaxLevel"); i++) {
                                    if (lore.contains(Strings.SELFREPAIR + i)) {
                                        //self-repair
                                        Random rand = new Random();
                                        int n = rand.nextInt(100);
                                        if (n <= Main.getPlugin().getConfig().getInt("Modifiers.Self-Repair.PercentagePerLevel") * i) {
                                            int heal = Main.getPlugin().getConfig().getInt("Modifiers.Self-Repair.HealthRepair");
                                            short dura = (short) (tool.getDurability() - heal);
                                            if (dura < 0) {
                                                dura = 0;
                                            }
                                            p.getInventory().getItemInMainHand().setDurability(dura);
                                            ChatWriter.log(false, p.getDisplayName() + " triggered Self-Repair on " + ItemGenerator.getDisplayName(tool) + ChatColor.WHITE + " (" + tool.getType().toString() + ")!");
                                        }
                                        break;
                                    }
                                }
                            }

                            if (Main.getPlugin().getConfig().getBoolean("Modifiers.XP.allowed") && p.hasPermission("minetinker.modifiers.xp.use")) {
                                for (int i = 1; i <= Main.getPlugin().getConfig().getInt("Modifiers.XP.MaxLevel"); i++) {
                                    if (lore.contains(Strings.XP + i)) {
                                        Random rand = new Random();
                                        int n = rand.nextInt(100);
                                        if (n <= Main.getPlugin().getConfig().getInt("Modifiers.XP.PercentagePerLevel") * i) {
                                            ExperienceOrb orb = p.getWorld().spawn(p.getLocation(), ExperienceOrb.class);
                                            orb.setExperience(Main.getPlugin().getConfig().getInt("Modifiers.XP.XPAmount"));
                                            ChatWriter.log(false, p.getDisplayName() + " triggered XP on " + ItemGenerator.getDisplayName(tool) + ChatColor.WHITE + " (" + tool.getType().toString() + ")!");
                                        }
                                        break;
                                    }
                                }
                            }

                            if (Main.getPlugin().getConfig().getBoolean("Modifiers.Glowing.allowed") && p.hasPermission("minetinker.modifiers.glowing.use")) {
                                if (lore.contains(Strings.GLOWING)) {
                                    if (!e.getEntity().isDead()) {
                                        try {
                                            LivingEntity ent = (LivingEntity) e.getEntity();
                                            ent.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Main.getPlugin().getConfig().getInt("Modifiers.Glowing.Duration"), 0, false, false));
                                        } catch (Exception ignored) {
                                        }
                                    }
                                }
                            }

                            if (Main.getPlugin().getConfig().getBoolean("Modifiers.Shulking.allowed") && p.hasPermission("minetinker.modifiers.shulking.use")) {
                                for (int i = 1; i <= Main.getPlugin().getConfig().getInt("Modifiers.Shulking.MaxLevel"); i++) {
                                    if (lore.contains(Strings.SHULKING + i)) {
                                        if (!e.getEntity().isDead()) {
                                            try {
                                                LivingEntity ent = (LivingEntity) e.getEntity();
                                                ent.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, Main.getPlugin().getConfig().getInt("Modifiers.Shulking.Duration"), Main.getPlugin().getConfig().getInt("Modifiers.Shulking.EffectMultiplier") * (i - 1), false, false));
                                            } catch (Exception ignored) {
                                            }
                                        }
                                        break;
                                    }
                                }
                            }

                            if (Main.getPlugin().getConfig().getBoolean("Modifiers.Poisonous.allowed") && p.hasPermission("minetinker.modifiers.poisonous.use")) {
                                for (int i = 1; i <= Main.getPlugin().getConfig().getInt("Modifiers.Poisonous.MaxLevel"); i++) {
                                    if (lore.contains(Strings.POISONOUS + i)) {
                                        if (!e.getEntity().isDead()) {
                                            try {
                                                LivingEntity ent = (LivingEntity) e.getEntity();
                                                int duration = (int) (Main.getPlugin().getConfig().getInt("Modifiers.Poisonous.Duration") * Math.pow(Main.getPlugin().getConfig().getDouble("Modifiers.Poisonous.DurationMultiplier"), (i - 1)));
                                                int amplifier = Main.getPlugin().getConfig().getInt("Modifiers.Poisonous.EffectMultiplier") * (i - 1);
                                                ent.addPotionEffect(new PotionEffect(PotionEffectType.POISON, duration, amplifier, false, false));
                                            } catch (Exception ignored) {
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        LivingEntity mob = e.getEntity();
        Player p = mob.getKiller();
        if (p == null) { return; }
        if (!Lists.WORLDS.contains(p.getWorld().getName())) { return; }
        ItemStack tool = p.getInventory().getItemInMainHand();
        if (tool.hasItemMeta()) {
            ItemMeta meta = tool.getItemMeta();
            if (meta.hasLore()) {
                ArrayList<String> lore = (ArrayList<String>) meta.getLore();
                if (lore.contains(Strings.IDENTIFIER)) {
                    if (Main.getPlugin().getConfig().getBoolean("Modifiers.Beheading.allowed") && p.hasPermission("minetinker.modifiers.beheading.use")) {
                        for (int i = 1; i <= Main.getPlugin().getConfig().getInt("Modifiers.Beheading.MaxLevel"); i++) {
                            if (lore.contains(Strings.BEHEADING + i)) {
                                Random rand = new Random();
                                int n = rand.nextInt(100);
                                if (n <= Main.getPlugin().getConfig().getInt("Modifiers.Beheading.PercentagePerLevel") * i) {
                                    if (mob.getType().equals(EntityType.CREEPER)) {
                                        p.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.CREEPER_HEAD, 1));
                                    } else if (mob.getType().equals(EntityType.SKELETON)) {
                                        p.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.SKELETON_SKULL, 1));
                                    } else if (mob.getType().equals(EntityType.WITHER_SKELETON)) {
                                        p.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.WITHER_SKELETON_SKULL, 1));
                                    } else if (mob.getType().equals(EntityType.ZOMBIE)) {
                                        p.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.ZOMBIE_HEAD, 1));
                                    } else if (mob.getType().equals(EntityType.ZOMBIE_VILLAGER)) {
                                        p.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.ZOMBIE_HEAD, 1));
                                    } else if (mob.getType().equals(EntityType.PLAYER)) {
                                        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
                                        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
                                        headMeta.setOwningPlayer((OfflinePlayer) mob);
                                        head.setItemMeta(headMeta);
                                        p.getWorld().dropItemNaturally(mob.getLocation(), head);
                                    } else {
                                        break;
                                    }
                                    ChatWriter.log(false, p.getDisplayName() + " triggered Beheading on " + ItemGenerator.getDisplayName(tool) + ChatColor.WHITE + " (" + tool.getType().toString() + ")!");
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onArrowHit(ProjectileHitEvent e) {
        if (e.getEntity().getShooter() instanceof Player) {
            Player p = (Player) e.getEntity().getShooter();
            ItemStack tool = p.getInventory().getItemInMainHand();
            if (e.getHitEntity() != null) {
                Entity target = e.getHitEntity();
                if (tool.hasItemMeta()) {
                    ItemMeta meta = tool.getItemMeta();
                    if (meta.hasLore()) {
                        ArrayList<String> lore = (ArrayList<String>) meta.getLore();
                        if (lore.contains(Strings.IDENTIFIER)) {
                            LevelCalculator.addExp(p, tool, Main.getPlugin().getConfig().getInt("ExpPerArrowHit"));

                            if (target != null) {
                                if (Main.getPlugin().getConfig().getBoolean("Modifiers.Shulking.allowed") && p.hasPermission("minetinker.modifiers.shulking.use")) {
                                    for (int i = 1; i <= Main.getPlugin().getConfig().getInt("Modifiers.Shulking.MaxLevel"); i++) {
                                        if (lore.contains(Strings.SHULKING + i)) {
                                            if (!e.getEntity().isDead()) {
                                                try {
                                                    LivingEntity ent = (LivingEntity) target;
                                                    ent.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, Main.getPlugin().getConfig().getInt("Modifiers.Shulking.Duration"), Main.getPlugin().getConfig().getInt("Modifiers.Shulking.EffectMultiplier") * (i - 1), false, false));
                                                } catch (Exception ignored) {
                                                }
                                            }
                                            break;
                                        }
                                    }
                                }

                                if (Main.getPlugin().getConfig().getBoolean("Modifiers.Glowing.allowed") && p.hasPermission("minetinker.modifiers.glowing.use")) {
                                    if (lore.contains(Strings.GLOWING)) {
                                        if (!e.getEntity().isDead()) {
                                            try {
                                                LivingEntity ent = (LivingEntity) target;
                                                ent.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Main.getPlugin().getConfig().getInt("Modifiers.Glowing.Duration"), 0, false, false));
                                            } catch (Exception ignored) {
                                            }
                                        }
                                    }
                                }

                                if (Main.getPlugin().getConfig().getBoolean("Modifiers.Poisonous.allowed") && p.hasPermission("minetinker.modifiers.poisonous.use")) {
                                    for (int i = 1; i <= Main.getPlugin().getConfig().getInt("Modifiers.Poisonous.MaxLevel"); i++) {
                                        if (lore.contains(Strings.POISONOUS + i)) {
                                            if (!e.getEntity().isDead()) {
                                                try {
                                                    LivingEntity ent = (LivingEntity) target;
                                                    int duration = (int) (Main.getPlugin().getConfig().getInt("Modifiers.Poisonous.Duration") * Math.pow(Main.getPlugin().getConfig().getDouble("Modifiers.Poisonous.DurationMultiplier"), (i - 1)));
                                                    int amplifier = Main.getPlugin().getConfig().getInt("Modifiers.Poisonous.EffectMultiplier") * (i - 1);
                                                    ent.addPotionEffect(new PotionEffect(PotionEffectType.POISON, duration, amplifier, false, false));
                                                } catch (Exception ignored) {
                                                }
                                            }
                                        }
                                    }
                                }

                                if (Main.getPlugin().getConfig().getBoolean("Modifiers.Ender.allowed") && p.hasPermission("minetinker.modifiers.ender.use")) {
                                    if (lore.contains(Strings.ENDER)) {
                                        if (p.isSneaking()) {
                                            Location loc = e.getHitEntity().getLocation().clone();
                                            e.getHitEntity().teleport(p.getLocation());
                                            p.teleport(new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ()));
                                            if (Main.getPlugin().getConfig().getBoolean("Modifiers.Ender.Sound")) {
                                                p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.3F);
                                                p.getWorld().playSound(e.getHitEntity().getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.3F);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (e.getHitBlock() != null) {
                if (tool.hasItemMeta()) {
                    ItemMeta meta = tool.getItemMeta();
                    if (tool.getItemMeta().hasLore()) {
                        ArrayList<String> lore = (ArrayList<String>) meta.getLore();
                        if (lore.contains(Strings.IDENTIFIER)) {
                            if (Main.getPlugin().getConfig().getBoolean("Modifiers.Ender.allowed") && p.hasPermission("minetinker.modifiers.ender.use")) {
                                if (lore.contains(Strings.ENDER)) {
                                    if (p.isSneaking()) {
                                        Location loc = e.getEntity().getLocation().clone();
                                        p.teleport(new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), p.getLocation().getYaw(), p.getLocation().getPitch()).add(0, 1, 0));
                                        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Ender.Sound")) {
                                            p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.3F);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBowFire (ProjectileLaunchEvent e) {
        if (!e.isCancelled()) {
            if (e.getEntity().getShooter() instanceof Player) {
                Player p = (Player) e.getEntity().getShooter();
                ItemStack tool = p.getInventory().getItemInMainHand();
                if (tool.hasItemMeta()) {
                    ItemMeta meta = tool.getItemMeta();
                    if (meta.hasLore()) {
                        ArrayList<String> lore = (ArrayList<String>) meta.getLore();
                        if (lore.contains(Strings.IDENTIFIER)) {
                            if (tool.getType().getMaxDurability() - tool.getDurability() <= 1) {
                                e.setCancelled(true);
                                if (Main.getPlugin().getConfig().getBoolean("Sound.OnBreaking")) {
                                    p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.5F, 0.5F);
                                }
                                tool.setDurability((short) (tool.getDurability() - 1));
                                return;
                            }

                            LevelCalculator.addExp(p, tool, Main.getPlugin().getConfig().getInt("ExpPerArrowShot"));

                            if (Main.getPlugin().getConfig().getBoolean("Modifiers.Self-Repair.allowed") && p.hasPermission("minetinker.modifiers.selfrepair.use")) {
                                for (int i = 1; i <= Main.getPlugin().getConfig().getInt("Modifiers.Self-Repair.MaxLevel"); i++) {
                                    if (lore.contains(Strings.SELFREPAIR + i)) {
                                        //self-repair
                                        Random rand = new Random();
                                        int n = rand.nextInt(100);
                                        if (n <= Main.getPlugin().getConfig().getInt("Modifiers.Self-Repair.PercentagePerLevel") * i) {
                                            int heal = Main.getPlugin().getConfig().getInt("Modifiers.Self-Repair.HealthRepair");
                                            short dura = (short) (tool.getDurability() - heal);
                                            if (dura < 0) {
                                                dura = 0;
                                            }
                                            p.getInventory().getItemInMainHand().setDurability(dura);
                                            ChatWriter.log(false, p.getDisplayName() + " triggered Self-Repair on " + ItemGenerator.getDisplayName(tool) + ChatColor.WHITE + " (" + tool.getType().toString() + ")!");
                                        }
                                        break;
                                    }
                                }
                            }

                            if (Main.getPlugin().getConfig().getBoolean("Modifiers.XP.allowed") && p.hasPermission("minetinker.modifiers.xp.use")) {
                                for (int i = 1; i <= Main.getPlugin().getConfig().getInt("Modifiers.XP.MaxLevel"); i++) {
                                    if (lore.contains(Strings.XP + i)) {
                                        //xp
                                        Random rand = new Random();
                                        int n = rand.nextInt(100);
                                        if (n <= Main.getPlugin().getConfig().getInt("Modifiers.XP.PercentagePerLevel") * i) {
                                            ExperienceOrb orb = p.getWorld().spawn(p.getLocation(), ExperienceOrb.class);
                                            orb.setExperience(Main.getPlugin().getConfig().getInt("Modifiers.XP.XPAmount"));
                                            ChatWriter.log(false, p.getDisplayName() + " triggered XP on " + ItemGenerator.getDisplayName(tool) + ChatColor.WHITE + " (" + tool.getType().toString() + ")!");
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

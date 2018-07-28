package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Data.Strings;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import de.flo56958.MineTinker.Utilities.LevelCalculator;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
                        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Self-Repair.allowed")) {
                            //<editor-fold desc="self-repair check">
                            searchloop:
                            for (int i = 0; i <= Main.getPlugin().getConfig().getInt("Modifiers.Self-Repair.MaxLevel"); i++) {
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
                                        ChatWriter.log(false, p.getDisplayName() + " triggered Self-Repair on " + ItemGenerator.getDisplayName(tool) + " (" + tool.getType().toString() + ")!");
                                    }
                                    break searchloop;
                                }
                            }
                            //</editor-fold>
                        }
                        if (Main.getPlugin().getConfig().getBoolean("Modifiers.XP.allowed")) {
                            //<editor-fold desc="xp check">
                            searchloop:
                            for (int i = 0; i <= Main.getPlugin().getConfig().getInt("Modifiers.XP.MaxLevel"); i++) {
                                if (lore.contains(Strings.XP + i)) {
                                    //xp
                                    Random rand = new Random();
                                    int n = rand.nextInt(100);
                                    if (n <= Main.getPlugin().getConfig().getInt("Modifiers.XP.PercentagePerLevel") * i) {
                                        ExperienceOrb orb = p.getWorld().spawn(p.getLocation(), ExperienceOrb.class);
                                        orb.setExperience(Main.getPlugin().getConfig().getInt("Modifiers.XP.XPAmount"));
                                        ChatWriter.log(false, p.getDisplayName() + " triggered XP on " + ItemGenerator.getDisplayName(tool) + " (" + tool.getType().toString() + ")!");
                                    }
                                    break searchloop;
                                }
                            }
                            //</editor-fold>
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
            ArrayList<String> lore = (ArrayList<String>) meta.getLore();
            if (lore.contains(Strings.IDENTIFIER)) {
                for (int i = 0; i <= Main.getPlugin().getConfig().getInt("Modifiers.Beheading.MaxLevel"); i++) {
                    if (lore.contains(Strings.BEHEADING + i)) {
                        //beheading
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
                            } else {
                                break;
                            }
                            ChatWriter.log(false, p.getDisplayName() + " triggered Beheading on " + ItemGenerator.getDisplayName(tool) + " (" + tool.getType().toString() + ")!");
                        }
                        break;
                    }
                }
            }
        }
    }
}

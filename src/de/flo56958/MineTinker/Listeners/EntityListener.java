package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Data.Strings;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Utilities.LevelCalculator;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class EntityListener implements Listener {

    @EventHandler
    public void onDamage (EntityDamageByEntityEvent e) {
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
                }
            }
        }
    }
}

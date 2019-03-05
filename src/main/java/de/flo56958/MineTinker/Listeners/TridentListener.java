package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Modifiers.ModManager;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class TridentListener implements Listener {

    public static HashMap<Trident, ItemStack> TridentToItemStack = new HashMap<>();

    @EventHandler
    public void onTridentLaunch(ProjectileLaunchEvent e) {
        System.out.println(TridentToItemStack.size());
        if (e.isCancelled()) { return; }
        if (!(e.getEntity().getShooter() instanceof Player)) { return; }

        Player p = (Player) e.getEntity().getShooter();
        ItemStack trident = p.getInventory().getItemInMainHand().clone();
        ModManager.instance().addExp(p, trident, -20000);
        TridentToItemStack.put((Trident) e.getEntity(), trident);
    }
}

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

    static final HashMap<Trident, ItemStack> TridentToItemStack = new HashMap<>();

    @EventHandler(ignoreCancelled = true)
    public void onTridentLaunch(ProjectileLaunchEvent e) {
        if (!(e.getEntity().getShooter() instanceof Player)) return;
        if (!(e.getEntity() instanceof Trident)) return;

        Player p = (Player) e.getEntity().getShooter();
        ItemStack trident = p.getInventory().getItemInMainHand().clone();

        if (!ModManager.instance().isToolViable(trident)) return;

        ModManager.instance().addExp(p, trident, -20000);
        TridentToItemStack.put((Trident) e.getEntity(), trident);
    }
}

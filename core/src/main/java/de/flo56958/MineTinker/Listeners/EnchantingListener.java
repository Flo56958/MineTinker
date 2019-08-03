package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Modifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;

import java.util.Map;

public class EnchantingListener implements Listener {

    ModManager modManager = ModManager.instance();

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onTableEnchant(EnchantItemEvent event) {
        Map<Enchantment, Integer> enchants = event.getEnchantsToAdd();

        for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
            Modifier modifier = modManager.getModifierFromEnchantment(entry.getKey());

            // The modifier may be disabled
            if (modifier != null) {
                for (int i = 0; i < entry.getValue(); i++) {
                    boolean success = modifier.applyMod(event.getEnchanter(), event.getItem(), false);

                    if (success) {
                        int newLevel = enchants.get(entry.getKey()) - 1;

                        // If the target level is 0 then just remove from the map instead of setting to 0
                        // Not quite sure what happens if it tries to set an enchant with a level of 0
                        // It may remove it? Which would be adverse.
                        if (newLevel == 0) {
                            enchants.remove(entry.getKey());
                        } else {
                            enchants.put(entry.getKey(), enchants.get(entry.getKey()) - 1);
                        }
                    }
                }
            }
        }

        // The enchants should be added when calling applyMod
        event.getEnchantsToAdd().clear();
    }

}

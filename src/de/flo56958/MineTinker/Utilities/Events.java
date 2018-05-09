package de.flo56958.MineTinker.Utilities;

import de.flo56958.MineTinker.Main;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Events {

    public static void LevelUp(Player p, ItemStack tool) {
        if (Main.getPlugin().getConfig().getBoolean("playSoundOnLevelUp")) {
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 0.5F);
        }
        String name = tool.getItemMeta().getDisplayName();
        if (tool.getItemMeta().getDisplayName() == null) {
            name = tool.getType().toString();
        }
        ChatWriter.sendMessage(p, ChatColor.GOLD, name + " just got a Level-Up!");
    }
}

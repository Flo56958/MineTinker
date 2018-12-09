package de.flo56958.MineTinker.Utilities;

import de.flo56958.MineTinker.Main;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Events {
    private static final FileConfiguration config = Main.getPlugin().getConfig();

    static void Upgrade_Fail(Player p, ItemStack tool, String level) {
        if (config.getBoolean("Sound.OnUpgrade")) {
            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1.0F, 0.5F);
        }
        ChatWriter.sendActionBar(p, ItemGenerator.getDisplayName(tool) + ChatColor.RED + " is already " + level + "!");
    }

    static void Upgrade_Prohibited(Player p, ItemStack tool) {
        if (config.getBoolean("Sound.OnUpgrade")) {
            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1.0F, 0.5F);
        }
        ChatWriter.sendActionBar(p, ItemGenerator.getDisplayName(tool) + ChatColor.RED + " can not be upgraded!");
        ChatWriter.log(false,  p.getDisplayName() + " tried to upgrade " + ItemGenerator.getDisplayName(tool) + ChatColor.WHITE + " (" + tool.getType().toString() + ") but was not allowed!");
    }

    static void Upgrade_Success(Player p, ItemStack tool, String level) {
        if (config.getBoolean("Sound.OnUpgrade")) {
            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0F, 0.5F);
        }
        ChatWriter.sendActionBar(p, ItemGenerator.getDisplayName(tool) + " is now " + level + "!");
        ChatWriter.log(false, p.getDisplayName() + " upgraded " + ItemGenerator.getDisplayName(tool) + ChatColor.WHITE + " (" + tool.getType().toString() + ") to " + level + "!");
    }
}

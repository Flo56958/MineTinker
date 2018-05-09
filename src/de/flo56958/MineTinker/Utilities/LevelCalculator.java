package de.flo56958.MineTinker.Utilities;

import de.flo56958.MineTinker.Main;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class LevelCalculator {

    public static int getNextLevelReq(int level) {
        return (int) (Main.getPlugin().getConfig().getInt("LevelStep") * Math.pow(2, (double) (level - 1)));
    }

    public static void addExp(Player p, ItemStack tool, int amount) {
        ItemMeta meta = tool.getItemMeta();
        ArrayList<String> lore = (ArrayList<String>) meta.getLore();
        String levelLine = lore.get(1);
        String expLine = lore.get(2);

        String[] levelS = levelLine.split(" ");
        String[] expS = expLine.split(" ");

        int level = Integer.parseInt(levelS[1]);
        int exp = Integer.parseInt(expS[1]);

        exp = exp + amount;
        if (exp >= LevelCalculator.getNextLevelReq(level)) {
            exp = 0;
            level++;
            lore.set(1, ChatColor.GOLD + "Level:" + ChatColor.WHITE + " " + level);
            Events.LevelUp(p, tool);
        }

        lore.set(2, ChatColor.GOLD + "Exp:" + ChatColor.WHITE + " " + exp + " / " + LevelCalculator.getNextLevelReq(level));

        meta.setLore(lore);
        tool.setItemMeta(meta);
    }
}

package de.flo56958.MineTinker.Utilities;

import de.flo56958.MineTinker.Main;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class LevelCalculator {

    static long getNextLevelReq(int level) {
        return (long) (Main.getPlugin().getConfig().getInt("LevelStep") * Math.pow(Main.getPlugin().getConfig().getDouble("LevelFactor"), (double) (level - 1)));
    }

    public static void addExp(Player p, ItemStack tool, int amount) {
        boolean LevelUp = false;

        ItemMeta meta = tool.getItemMeta();
        List<String> lore = meta.getLore();

        String[] levelS = lore.get(1).split(" ");
        String[] expS = lore.get(2).split(" ");

        int level = Integer.parseInt(levelS[1]);
        long exp = Long.parseLong(expS[1]);
        if (exp == Long.MAX_VALUE || exp < 0 || level < 0) {
            if (Main.getPlugin().getConfig().getBoolean("ResetAtIntOverflow")) {
                level = 1;
                lore.set(1, ChatColor.GOLD + "Level:" + ChatColor.WHITE + " " + level);
                exp = 0;
                LevelUp = true;
            } else {
                return;
            }
        }

        exp = exp + amount;
        if (exp >= getNextLevelReq(level)) {
            level++;
            lore.set(1, ChatColor.GOLD + "Level:" + ChatColor.WHITE + " " + level);
            LevelUp = true;
        }

        lore.set(2, ChatColor.GOLD + "Exp:" + ChatColor.WHITE + " " + exp + " / " + getNextLevelReq(level));

        meta.setLore(lore);
        tool.setItemMeta(meta);
        if (LevelUp) {
            Events.LevelUp(p, tool);
            Events.LevelUpChance(p, tool);
        }
    }
}

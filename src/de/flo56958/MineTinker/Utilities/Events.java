package de.flo56958.MineTinker.Utilities;

import de.flo56958.MineTinker.Main;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class Events {

    public static void LevelUp(Player p, ItemStack tool) {
        if (Main.getPlugin().getConfig().getBoolean("Sound.OnLevelUp")) {
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 0.5F);
        }
        String name = tool.getItemMeta().getDisplayName();
        if (tool.getItemMeta().getDisplayName() == null) {
            name = tool.getType().toString();
        }
        ChatWriter.sendMessage(p, ChatColor.GOLD, name + " just got a Level-Up!");

        if (Main.getPlugin().getConfig().getInt("AddModifierSlotsPerLevel") > 0) {
            ItemMeta meta = tool.getItemMeta();
            ArrayList<String> lore = (ArrayList<String>) meta.getLore();
            String[] slotsS = lore.get(3).split(" ");

            int slots = Integer.parseInt(slotsS[3]);
            int newSlots = slots + Main.getPlugin().getConfig().getInt("AddModifierSlotsPerLevel");
            lore.set(3, ChatColor.WHITE + "Free Modifier Slots: " + newSlots);

            ChatWriter.sendMessage(p, ChatColor.GOLD, name + " has now " + ChatColor.WHITE + newSlots + ChatColor.GOLD + " free Modifier-Slots!");

            meta.setLore(lore);
            tool.setItemMeta(meta);
        }
    }

    public static void Mod_MaxLevel(Player p, ItemStack tool, String mod) {
        if (Main.getPlugin().getConfig().getBoolean("Sound.OnModding")) {
            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1.0F, 0.5F);
        }
        String name = tool.getItemMeta().getDisplayName();
        if (tool.getItemMeta().getDisplayName() == null) {
            name = tool.getType().toString();
        }
        ChatWriter.sendMessage(p, ChatColor.RED, name + " is already max level on: " + mod);
    }

    public static void Mod_AddMod(Player p, ItemStack tool, String s, int slotsRemaining) {
        if (Main.getPlugin().getConfig().getBoolean("Sound.OnModding")) {
            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0F, 0.5F);
        }
        String name = tool.getItemMeta().getDisplayName();
        if (tool.getItemMeta().getDisplayName() == null) {
            name = tool.getType().toString();
        }
        ChatWriter.sendMessage(p, ChatColor.WHITE, name + " has now " + s);
        ChatWriter.sendMessage(p, ChatColor.WHITE, name + " has now " + slotsRemaining + " free Slots remaining!");
    }

    public static void Mod_NoSlots(Player p, ItemStack tool, String s) {
        if (Main.getPlugin().getConfig().getBoolean("Sound.OnModding")) {
            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1.0F, 0.5F);
        }
        String name = tool.getItemMeta().getDisplayName();
        if (tool.getItemMeta().getDisplayName() == null) {
            name = tool.getType().toString();
        }
        ChatWriter.sendMessage(p, ChatColor.RED, name + " has not enough Modifier-Slots for " + s + "!");
    }

    public static void Upgrade_Fail(Player p, ItemStack tool, String level) {
        if (Main.getPlugin().getConfig().getBoolean("Sound.OnUpgrade")) {
            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1.0F, 0.5F);
        }
        String name = tool.getItemMeta().getDisplayName();
        if (tool.getItemMeta().getDisplayName() == null) {
            name = tool.getType().toString();
        }
        ChatWriter.sendMessage(p, ChatColor.RED, name + " is already " + level + "!");
    }

    public static void Upgrade_Prohibited(Player p, ItemStack tool) {
        if (Main.getPlugin().getConfig().getBoolean("Sound.OnUpgrade")) {
            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1.0F, 0.5F);
        }
        String name = tool.getItemMeta().getDisplayName();
        if (tool.getItemMeta().getDisplayName() == null) {
            name = tool.getType().toString();
        }
        ChatWriter.sendMessage(p, ChatColor.RED, name + " can not be upgraded!");
    }

    public static void Upgrade_Success(Player p, ItemStack tool, String level) {
        if (Main.getPlugin().getConfig().getBoolean("Sound.OnUpgrade")) {
            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0F, 0.5F);
        }
        String name = tool.getItemMeta().getDisplayName();
        if (tool.getItemMeta().getDisplayName() == null) {
            name = tool.getType().toString();
        }
        ChatWriter.sendMessage(p, ChatColor.WHITE, name + " is now " + level + "!");
    }
}

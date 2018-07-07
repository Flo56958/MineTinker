package de.flo56958.MineTinker.Utilities;

import de.flo56958.MineTinker.Main;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

class Events {

    public static void LevelUp(Player p, ItemStack tool) {
        if (Main.getPlugin().getConfig().getBoolean("Sound.OnLevelUp")) {
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 0.5F);
        }
        ChatWriter.sendMessage(p, ChatColor.GOLD, ItemGenerator.getDisplayName(tool) + " just got a Level-Up!");

        if (Main.getPlugin().getConfig().getInt("AddModifierSlotsPerLevel") > 0) {
            ItemMeta meta = tool.getItemMeta();
            ArrayList<String> lore = (ArrayList<String>) meta.getLore();
            String[] slotsS = lore.get(3).split(" ");

            int slots = Integer.parseInt(slotsS[3]);
            int newSlots = slots + Main.getPlugin().getConfig().getInt("AddModifierSlotsPerLevel");
            lore.set(3, ChatColor.WHITE + "Free Modifier Slots: " + newSlots);

            ChatWriter.sendMessage(p, ChatColor.GOLD, ItemGenerator.getDisplayName(tool) + " has now " + ChatColor.WHITE + newSlots + ChatColor.GOLD + " free Modifier-Slots!");

            meta.setLore(lore);
            tool.setItemMeta(meta);
        }
        ChatWriter.log(false, p.getDisplayName() + " leveled up " + ItemGenerator.getDisplayName(tool) + " (" + tool.getType().toString() + ")!");
    }

    public static void Mod_MaxLevel(Player p, ItemStack tool, String mod) {
        if (Main.getPlugin().getConfig().getBoolean("Sound.OnModding")) {
            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1.0F, 0.5F);
        }
        ChatWriter.sendMessage(p, ChatColor.RED, ItemGenerator.getDisplayName(tool) + " is already max level on: " + mod);
    }

    public static void Mod_AddMod(Player p, ItemStack tool, String s, int slotsRemaining) {
        if (Main.getPlugin().getConfig().getBoolean("Sound.OnModding")) {
            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0F, 0.5F);
        }
        ChatWriter.sendMessage(p, ChatColor.WHITE, ItemGenerator.getDisplayName(tool) + " has now " + s);
        ChatWriter.sendMessage(p, ChatColor.WHITE, ItemGenerator.getDisplayName(tool) + " has now " + slotsRemaining + " free Slots remaining!");
        ChatWriter.log(false, p.getDisplayName() + " modded " + ItemGenerator.getDisplayName(tool) + " (" + tool.getType().toString() + ") with " + s + "!");
    }

    public static void Mod_NoSlots(Player p, ItemStack tool, String s) {
        if (Main.getPlugin().getConfig().getBoolean("Sound.OnModding")) {
            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1.0F, 0.5F);
        }
        ChatWriter.sendMessage(p, ChatColor.RED, ItemGenerator.getDisplayName(tool) + " has not enough Modifier-Slots for " + s + "!");
    }

    public static void Upgrade_Fail(Player p, ItemStack tool, String level) {
        if (Main.getPlugin().getConfig().getBoolean("Sound.OnUpgrade")) {
            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1.0F, 0.5F);
        }
        ChatWriter.sendMessage(p, ChatColor.RED, ItemGenerator.getDisplayName(tool) + " is already " + level + "!");
    }

    public static void Upgrade_Prohibited(Player p, ItemStack tool) {
        if (Main.getPlugin().getConfig().getBoolean("Sound.OnUpgrade")) {
            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1.0F, 0.5F);
        }
        ChatWriter.sendMessage(p, ChatColor.RED, ItemGenerator.getDisplayName(tool) + " can not be upgraded!");
        ChatWriter.log(false,  ChatColor.RED + p.getDisplayName() + " tried to upgrade " + ItemGenerator.getDisplayName(tool) + " (" + tool.getType().toString() + ") but was not allowed!");
    }

    public static void Upgrade_Success(Player p, ItemStack tool, String level) {
        if (Main.getPlugin().getConfig().getBoolean("Sound.OnUpgrade")) {
            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0F, 0.5F);
        }
        ChatWriter.sendMessage(p, ChatColor.WHITE, ItemGenerator.getDisplayName(tool) + " is now " + level + "!");
        ChatWriter.log(false, p.getDisplayName() + " upgraded " + ItemGenerator.getDisplayName(tool) + " (" + tool.getType().toString() + ") to " + level + "!");
    }
}

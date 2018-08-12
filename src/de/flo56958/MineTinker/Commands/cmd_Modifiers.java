package de.flo56958.MineTinker.Commands;

import de.flo56958.MineTinker.Data.Strings;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

class cmd_Modifiers {

    static void modifierList(Player p) {
        ChatWriter.sendMessage(p, ChatColor.GOLD, "Possible Modifiers:");
        int index = 1;
        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Auto-Smelt.allowed")) {
            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". " + Strings.AUTOSMELT + ChatColor.WHITE + "[Enhanced Furnace] Chance to smelt ore when mined! (P)");
            index++;
        }
        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Beheading.allowed")) {
            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". " + Strings.BEHEADING + ChatColor.WHITE + "[Enchanted Wither-Skull] Chance to drop the head of the mob! (B/SW)");
            index++;
        }
        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Ender.allowed")) {
            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". " + Strings.ENDER + ": " + ChatColor.WHITE + "[Special Endereye] Teleports you while sneaking to the arrow location! (B)");
            index++;
        }
        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Extra-Modifier.allowed")) {
            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". " + ChatColor.GRAY + "Extra-Modifier-Slot: " + ChatColor.WHITE + "[Netherstar] Adds a additional Modifier-Slot to the tool!");
            index++;
        }
        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Fiery.allowed")) {
            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". " + Strings.FIERY_SWORD + ChatColor.WHITE + "[Blaze-Rod] Inflames enemies! (B/SW)");
            index++;
        }
        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Glowing.allowed")) {
            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". " + Strings.GLOWING + ": " + ChatColor.WHITE + "[Glowstone Eye of Ender] Makes Enemies glow! (B/SW)");
            index++;
        }
        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Haste.allowed")) {
            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". " + Strings.HASTE + ChatColor.WHITE + "[Redstone] Tool can destroy blocks faster! (P/A/S)");
            index++;
        }
        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Infinity.allowed")) {
            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". " + Strings.INFINITY + ChatColor.WHITE + "[Enchanted Arrow] You only need one Arrow to shoot a bow! (B)");
            index++;
        }
        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Luck.allowed")) {
            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". " + Strings.LUCK + ChatColor.WHITE + "[Compressed Lapis-Block] Lets you get more drops from blocks or mobs!");
            index++;
        }
        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Poisonous.allowed")) {
            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". " + Strings.POWER + ChatColor.WHITE + "[Enchanted Rotten Flesh] Poisons enemies! (B/SW)");
            index++;
        }
        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Power.allowed")) {
            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". " + Strings.POWER + ChatColor.WHITE + "[Emerald] Tool can destroy more blocks per swing! (P/A/S)");
            index++;
        }
        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Reinforced.allowed")) {
            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". " + Strings.REINFORCED + ChatColor.WHITE + "[Compressed Obsidian] Chance to not use durability when using the tool!");
            index++;
        }
        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Self-Repair.allowed")) {
            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". " + Strings.SELFREPAIR + ChatColor.WHITE + "[Enchanted Mossy Cobblestone] Chance to repair the tool while using it!");
            index++;
        }
        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Sharpness.allowed")) {
            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". " + Strings.SHARPNESS + ChatColor.WHITE + "[Compressed Quartzblock] Tool does additional damage! (B/SW)");
            index++;
        }
        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Shulking.allowed")) {
            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". " + Strings.SHULKING + ChatColor.WHITE + "[Special Shulkershell] Makes enemies levitate! (B/SW)");
            index++;
        }
        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Silk-Touch.allowed")) {
            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". " + Strings.SILKTOUCH + ChatColor.WHITE + "[Enchanted Cobweb] Applies Silk-Touch! (P/A/S)");
            index++;
        }
        if (Main.getPlugin().getConfig().getBoolean("Modifiers.XP.allowed")) {
            ChatWriter.sendMessage(p, ChatColor.WHITE, index + ". " + Strings.XP + ChatColor.WHITE + "[XP-Bottle] Tool has the chance to drop XP while using it!");
        }
    }
}

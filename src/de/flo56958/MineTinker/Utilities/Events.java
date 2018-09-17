package de.flo56958.MineTinker.Utilities;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Random;

class Events {

    static void LevelUp(Player p, ItemStack tool) {
        if (Main.getPlugin().getConfig().getBoolean("Sound.OnLevelUp")) {
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 0.5F);
        }
        ChatWriter.sendMessage(p, ChatColor.GOLD, ItemGenerator.getDisplayName(tool) + ChatColor.GOLD + " just got a Level-Up!");

        if (Main.getPlugin().getConfig().getInt("AddModifierSlotsPerLevel") > 0) {
            ItemMeta meta = tool.getItemMeta();
            ArrayList<String> lore = (ArrayList<String>) meta.getLore();
            String[] slotsS = lore.get(3).split(" ");

            int slots = Integer.parseInt(slotsS[3]);
            int newSlots = slots + Main.getPlugin().getConfig().getInt("AddModifierSlotsPerLevel");
            lore.set(3, ChatColor.WHITE + "Free Modifier Slots: " + newSlots);

            ChatWriter.sendMessage(p, ChatColor.GOLD, ItemGenerator.getDisplayName(tool) + ChatColor.GOLD + " has now " + ChatColor.WHITE + newSlots + ChatColor.GOLD + " free Modifier-Slots!");

            meta.setLore(lore);
            tool.setItemMeta(meta);
        }
        ChatWriter.log(false, p.getDisplayName() + " leveled up " + ItemGenerator.getDisplayName(tool) + ChatColor.WHITE + " (" + tool.getType().toString() + ")!");
    }

    static void LevelUpChance(Player p, ItemStack tool) {
        if (Main.getPlugin().getConfig().getBoolean("LevelUpEvents.enabled")) {
            Random rand = new Random();
            if (Main.getPlugin().getConfig().getBoolean("LevelUpEvents.DurabilityRepair.enabled")) {
                int n = rand.nextInt(100);
                if (n <= Main.getPlugin().getConfig().getInt("LevelUpEvents.DurabilityRepair.percentage")) {
                    tool.setDurability((short) -1);
                }
            }
            if (Main.getPlugin().getConfig().getBoolean("LevelUpEvents.DropLoot.enabled")) {
                int n = rand.nextInt(100);
                if (n <= Main.getPlugin().getConfig().getInt("LevelUpEvents.DropLoot.percentage")) {
                    int index = rand.nextInt(Lists.DROPLOOT.size());
                    Material m = Material.getMaterial(Lists.DROPLOOT.get(index));
                    int amount = rand.nextInt(Main.getPlugin().getConfig().getInt("LevelUpEvents.DropLoot.maximumDrop") - Main.getPlugin().getConfig().getInt("LevelUpEvents.DropLoot.minimumDrop"));
                    amount = amount + Main.getPlugin().getConfig().getInt("LevelUpEvents.DropLoot.minimumDrop");
                    ItemStack drops = new ItemStack(m, amount);
                    p.getWorld().dropItemNaturally(p.getLocation(), drops);
                }
            }
            if (Main.getPlugin().getConfig().getBoolean("LevelUpEvents.RandomModifier.enabled")) {
                int n = rand.nextInt(100);
                if (n <= Main.getPlugin().getConfig().getInt("LevelUpEvents.RandomModifier.percentage")) {
                    p.getInventory().setItemInMainHand(LevelUpEvent_RandomModifier_apply(tool, p));
                }
            }
            if (Main.getPlugin().getConfig().getBoolean("LevelUpEvents.DropXP.enabled")) {
                int n = rand.nextInt(100);
                if (n <= Main.getPlugin().getConfig().getInt("LevelUpEvents.DropXP.percentage")) {
                    ExperienceOrb orb = p.getWorld().spawn(p.getLocation(), ExperienceOrb.class);
                    orb.setExperience(Main.getPlugin().getConfig().getInt("LevelUpEvents.DropXP.amount"));
                }
            }
        }
    }

    private static ItemStack LevelUpEvent_RandomModifier_apply(ItemStack tool, Player p) {
        int index = new Random().nextInt(Lists.getAllowedModifiers().size());
        ItemStack safety = tool.clone();
        ItemStack newTool = ItemGenerator.ToolModifier(tool, Lists.getAllowedModifiers().get(index), p, true);
        if (newTool != null) {
            return newTool;
        } else {
            return LevelUpEvent_RandomModifier_apply(safety, p); //infinite loop when no modifier can be applied (if extra-modifier is disabled)
        }
    }

    static void Mod_MaxLevel(Player p, ItemStack tool, String mod) {
        if (Main.getPlugin().getConfig().getBoolean("Sound.OnModding")) {
            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1.0F, 0.5F);
        }
        ChatWriter.sendMessage(p, ChatColor.RED, ItemGenerator.getDisplayName(tool) + ChatColor.WHITE + " is already max level on: " + mod);
    }

    static void Mod_AddMod(Player p, ItemStack tool, String s, int slotsRemaining, boolean event) {
        if (Main.getPlugin().getConfig().getBoolean("Sound.OnModding") && !event) {
            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0F, 0.5F);
        }
        ChatWriter.sendMessage(p, ChatColor.WHITE, ItemGenerator.getDisplayName(tool) + ChatColor.WHITE + " has now " + s);
        if (!event) {
            ChatWriter.sendMessage(p, ChatColor.WHITE, ItemGenerator.getDisplayName(tool) + ChatColor.WHITE + " has now " + slotsRemaining + " free Slots remaining!");
        }
        ChatWriter.log(false, p.getDisplayName() + " modded " + ItemGenerator.getDisplayName(tool) +  ChatColor.WHITE + " (" + tool.getType().toString() + ") with " + s + ChatColor.GRAY + "!");
    }

    static void Mod_NoSlots(Player p, ItemStack tool, String s) {
        if (Main.getPlugin().getConfig().getBoolean("Sound.OnModding")) {
            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1.0F, 0.5F);
        }
        ChatWriter.sendMessage(p, ChatColor.RED, ItemGenerator.getDisplayName(tool) + ChatColor.RED + " has not enough Modifier-Slots for " + s + "!");
    }

    static void Upgrade_Fail(Player p, ItemStack tool, String level) {
        if (Main.getPlugin().getConfig().getBoolean("Sound.OnUpgrade")) {
            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1.0F, 0.5F);
        }
        ChatWriter.sendMessage(p, ChatColor.RED, ItemGenerator.getDisplayName(tool) + ChatColor.RED + " is already " + level + "!");
    }

    static void Upgrade_Prohibited(Player p, ItemStack tool) {
        if (Main.getPlugin().getConfig().getBoolean("Sound.OnUpgrade")) {
            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1.0F, 0.5F);
        }
        ChatWriter.sendMessage(p, ChatColor.RED, ItemGenerator.getDisplayName(tool) + ChatColor.RED + " can not be upgraded!");
        ChatWriter.log(false,  p.getDisplayName() + " tried to upgrade " + ItemGenerator.getDisplayName(tool) + ChatColor.WHITE + " (" + tool.getType().toString() + ") but was not allowed!");
    }

    static void Upgrade_Success(Player p, ItemStack tool, String level) {
        if (Main.getPlugin().getConfig().getBoolean("Sound.OnUpgrade")) {
            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0F, 0.5F);
        }
        ChatWriter.sendMessage(p, ChatColor.WHITE, ItemGenerator.getDisplayName(tool) + " is now " + level + "!");
        ChatWriter.log(false, p.getDisplayName() + " upgraded " + ItemGenerator.getDisplayName(tool) + ChatColor.WHITE + " (" + tool.getType().toString() + ") to " + level + "!");
    }

    static void ModAndSilk(Player p, String mod) {
        if (Main.getPlugin().getConfig().getBoolean("Sound.OnModding")) {
            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1.0F, 0.5F);
        }
        ChatWriter.sendMessage(p, ChatColor.RED, mod + " and Silk-Touch can't be combined!");
        ChatWriter.log(false, p.getDisplayName() + " tried to combine " + mod + " and Silk-Touch on one tool!");
    }
}

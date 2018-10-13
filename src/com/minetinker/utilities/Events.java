package com.minetinker.utilities;

import com.minetinker.Main;
import com.minetinker.data.Lists;
import com.minetinker.data.Strings;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Random;

class Events {
    private static final FileConfiguration config = Main.getPlugin().getConfig();

    static void LevelUp(Player p, ItemStack tool) {
        if (config.getBoolean("Sound.OnLevelUp")) {
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 0.5F);
        }

        if (config.getInt("AddModifierSlotsPerLevel") > 0) {
            ItemMeta meta = tool.getItemMeta();
            ArrayList<String> lore = (ArrayList<String>) meta.getLore();
            String[] slotsS = lore.get(3).split(" ");

            int slots = Integer.parseInt(slotsS[3]);
            int newSlots = slots;
            if (!(slots == Integer.MAX_VALUE || slots < 0)) {
                newSlots += config.getInt("AddModifierSlotsPerLevel");
            } else {
                newSlots = Integer.MAX_VALUE;
            }
            lore.set(3, ChatColor.WHITE + Strings.FREEMODIFIERSLOTS + newSlots);

            meta.setLore(lore);
            tool.setItemMeta(meta);
        }

        ChatWriter.sendActionBar(p, ItemGenerator.getDisplayName(tool) + ChatColor.GOLD + " just got a Level-Up!");

        ChatWriter.log(false, p.getDisplayName() + " leveled up " + ItemGenerator.getDisplayName(tool) + ChatColor.WHITE + " (" + tool.getType().toString() + ")!");
    }

    static void LevelUpChance(Player p, ItemStack tool) {
        if (config.getBoolean("LevelUpEvents.enabled")) {
            Random rand = new Random();
            if (config.getBoolean("LevelUpEvents.DurabilityRepair.enabled")) {
                int n = rand.nextInt(100);
                if (n <= config.getInt("LevelUpEvents.DurabilityRepair.percentage")) {
                    tool.setDurability((short) -1);
                }
            }
            if (config.getBoolean("LevelUpEvents.DropLoot.enabled")) {
                int n = rand.nextInt(100);
                if (n <= config.getInt("LevelUpEvents.DropLoot.percentage")) {
                    int index = rand.nextInt(Lists.DROPLOOT.size());
                    Material m = Material.getMaterial(Lists.DROPLOOT.get(index));
                    int amount = rand.nextInt(config.getInt("LevelUpEvents.DropLoot.maximumDrop") - config.getInt("LevelUpEvents.DropLoot.minimumDrop"));
                    amount = amount + config.getInt("LevelUpEvents.DropLoot.minimumDrop");
                    ItemStack drop = new ItemStack(m, amount);
                    if(p.getInventory().addItem(drop).size() != 0) { //adds items to (full) inventory
                        p.getWorld().dropItem(p.getLocation(), drop);
                    } // no else as it gets added in if
                }
            }
            if (config.getBoolean("LevelUpEvents.RandomModifier.enabled")) {
                int n = rand.nextInt(100);
                if (n <= config.getInt("LevelUpEvents.RandomModifier.percentage")) {
                    p.getInventory().setItemInMainHand(LevelUpEvent_RandomModifier_apply(tool, p));
                }
            }
            if (config.getBoolean("LevelUpEvents.DropXP.enabled")) {
                int n = rand.nextInt(100);
                if (n <= config.getInt("LevelUpEvents.DropXP.percentage")) {
                    ExperienceOrb orb = p.getWorld().spawn(p.getLocation(), ExperienceOrb.class);
                    orb.setExperience(config.getInt("LevelUpEvents.DropXP.amount"));
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
            return LevelUpEvent_RandomModifier_apply(safety, p); //infinite loop when no modifier can be applied (if extra-modifier is disabled) //TODO: Disable possible loop
        }
    }

    static void Mod_MaxLevel(Player p, ItemStack tool, String mod) {
        if (config.getBoolean("Sound.OnModding")) {
            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1.0F, 0.5F);
        }
        ChatWriter.sendActionBar(p, ChatColor.RED + ItemGenerator.getDisplayName(tool) + " is already max level on: " + mod);
    }

    static void Mod_AddMod(Player p, ItemStack tool, String s, int slotsRemaining, boolean event) {
        if (config.getBoolean("Sound.OnModding") && !event) {
            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0F, 0.5F);
        }
        if (!event) {
            ChatWriter.sendActionBar(p, ItemGenerator.getDisplayName(tool) + ChatColor.WHITE + " has now " + s + " and " + slotsRemaining + " free Slots remaining!");
        } else {
            ChatWriter.sendActionBar(p, ItemGenerator.getDisplayName(tool) + ChatColor.WHITE + " has now " + s);
        }
        ChatWriter.log(false, p.getDisplayName() + " modded " + ItemGenerator.getDisplayName(tool) +  ChatColor.WHITE + " (" + tool.getType().toString() + ") with " + s + ChatColor.GRAY + "!");
    }

    static void Mod_NoSlots(Player p, ItemStack tool, String s) {
        if (config.getBoolean("Sound.OnModding")) {
            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1.0F, 0.5F);
        }
        ChatWriter.sendActionBar(p, ItemGenerator.getDisplayName(tool) + ChatColor.RED + " has not enough Modifiers-Slots for " + s + "!");
    }

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

    static void IncompatibleMods(Player p, String mod1, String mod2) {
        if (config.getBoolean("Sound.OnModding")) {
            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1.0F, 0.5F);
        }
        ChatWriter.sendActionBar(p, mod1 + ChatColor.WHITE + " and " + mod2 + ChatColor.WHITE + " can't be combined!");
        ChatWriter.log(false, p.getDisplayName() + " tried to combine " + mod1 + " and " + mod2 + " on one tool!");
    }
}

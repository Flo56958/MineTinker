package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Data.Strings;
import de.flo56958.MineTinker.Events.ModifierApplyEvent;
import de.flo56958.MineTinker.Events.ModifierFailEvent;
import de.flo56958.MineTinker.Events.ToolLevelUpEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Random;

public class TinkerListener implements Listener {

    private static final ModManager modManager = Main.getModManager();
    private static final FileConfiguration config = Main.getPlugin().getConfig();

    @EventHandler
    public void onModifierApply(ModifierApplyEvent e) {
        Player p = e.getPlayer();
        ItemStack tool = e.getTool();
        Modifier mod = e.getMod();

        if (config.getBoolean("Sound.OnModding")) {
            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0F, 0.5F);
        }
        ChatWriter.sendActionBar(p, ItemGenerator.getDisplayName(tool) + ChatColor.WHITE + " has now " + mod.getColor() + mod.getName() + ChatColor.WHITE + " and " + e.getSlotsRemaining() + " free Slots remaining!");
        ChatWriter.log(false, p.getDisplayName() + " modded " + ItemGenerator.getDisplayName(tool) +  ChatColor.GRAY + " (" + tool.getType().toString() + ") with " + mod.getColor() + mod.getName() + " " + modManager.getModLevel(tool, mod) + ChatColor.GRAY + "!");
    }

    @EventHandler
    public void onModifierFail(ModifierFailEvent e) {
        Player p = e.getPlayer();
        ItemStack tool = e.getTool();
        Modifier mod = e.getMod();

        if (config.getBoolean("Sound.OnFail")) {
            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1.0F, 0.5F);
        }
        ChatWriter.sendActionBar(p, "Failed to apply " + mod.getName() + " on " + ItemGenerator.getDisplayName(tool) + ChatColor.WHITE + " (" + e.getFailCause().toString() + ")");
        ChatWriter.log(false, p.getDisplayName() + " failed to apply " + mod.getColor() + mod.getName() + " " + (modManager.getModLevel(tool, mod) + 1) + " on " + ItemGenerator.getDisplayName(tool) + ChatColor.GRAY + " (" + tool.getType().toString() + ") (" + e.getFailCause().toString() + ")");
    }

    @EventHandler
    public void onToolLevelUp(ToolLevelUpEvent e) {
        Player p = e.getPlayer();
        ItemStack tool = e.getTool();

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

        //------------------------------------------------------------------------------------------------------------------------

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
                    p.getInventory().setItemInMainHand(LevelUpEvent_RandomModifier_apply(tool, p, 1));
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

    private static ItemStack LevelUpEvent_RandomModifier_apply(ItemStack tool, Player p, int insurance) {
        if (insurance == 20) { return tool; }

        int index = new Random().nextInt(modManager.getAllMods().size());
        ItemStack safety = tool.clone();
        ItemStack newTool = modManager.getAllMods().get(index).applyMod(p, tool, false);
        if (newTool != null) {
            return newTool;
        } else {
            insurance++;
            return LevelUpEvent_RandomModifier_apply(safety, p, insurance);
        }
    }
}

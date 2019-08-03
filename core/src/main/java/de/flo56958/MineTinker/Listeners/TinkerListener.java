package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Events.ModifierApplyEvent;
import de.flo56958.MineTinker.Events.ModifierFailEvent;
import de.flo56958.MineTinker.Events.ToolLevelUpEvent;
import de.flo56958.MineTinker.Events.ToolUpgradeEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Modifiers.Types.ExtraModifier;
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
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TinkerListener implements Listener {

    private static final ModManager modManager = ModManager.instance();
    private static final FileConfiguration config = Main.getPlugin().getConfig();

    @EventHandler
    public void onToolUpgrade(ToolUpgradeEvent event) {
        Player player = event.getPlayer();
        ItemStack tool = event.getTool();

        if (event.isSuccessful()) {
            if (config.getBoolean("Sound.OnUpgrade")) {
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0F, 0.5F);
            }

            ChatWriter.sendActionBar(player, ItemGenerator.getDisplayName(tool) + " is now " + tool.getType().toString().split("_")[0] + "!");
            ChatWriter.log(false, player.getDisplayName() + " upgraded " + ItemGenerator.getDisplayName(tool) + ChatColor.WHITE + " (" + tool.getType().toString() + ") to " + tool.getType().toString() + "!");
        } else {
            if (config.getBoolean("Sound.OnUpgrade")) {
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1.0F, 0.5F);
            }
        }
    }

    @EventHandler
    public void onModifierApply(ModifierApplyEvent event) {
        Player player = event.getPlayer();
        ItemStack tool = event.getTool();
        Modifier mod = event.getMod();

        if (config.getBoolean("Sound.OnModding")) {
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0F, 0.5F);
        }

        ChatWriter.sendActionBar(player, ItemGenerator.getDisplayName(tool) + ChatColor.WHITE + " has now " + mod.getColor() + mod.getName() + ChatColor.WHITE + " and " + event.getSlotsRemaining() + " free Slots remaining!");
        ChatWriter.log(false, player.getDisplayName() + " modded " + ItemGenerator.getDisplayName(tool) +  ChatColor.GRAY + " (" + tool.getType().toString() + ") with " + mod.getColor() + mod.getName() + ChatColor.GRAY + " " + modManager.getModLevel(tool, mod) + "!");
    }

    @EventHandler
    public void onModifierFail(ModifierFailEvent event) {
        Player player = event.getPlayer();
        ItemStack tool = event.getTool();
        Modifier mod = event.getMod();

        if (config.getBoolean("Sound.OnFail")) {
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1.0F, 0.5F);
        }

        if (!event.isCommand()) {
            ChatWriter.sendActionBar(player, "Failed to apply " + mod.getColor() + mod.getName() + ChatColor.WHITE + " on " + ItemGenerator.getDisplayName(tool) + ChatColor.WHITE + " (" + event.getFailCause().toString() + ")");
            ChatWriter.log(false, player.getDisplayName() + " failed to apply " + mod.getColor() + mod.getName() + ChatColor.GRAY + " " + (modManager.getModLevel(tool, mod) + 1) + " on " + ItemGenerator.getDisplayName(tool) + ChatColor.GRAY + " (" + tool.getType().toString() + ") (" + event.getFailCause().toString() + ")");
        }
    }

	@EventHandler
    public void onToolLevelUp(ToolLevelUpEvent event) {
        Player player = event.getPlayer();
        ItemStack tool = event.getTool();

        boolean appliedRandomMod = false;

        if (config.getBoolean("LevelUpEvents.enabled")) {
            Random rand = new Random();

            if (config.getBoolean("LevelUpEvents.DurabilityRepair.enabled")) {
                int n = rand.nextInt(100);

                if (n <= config.getInt("LevelUpEvents.DurabilityRepair.percentage")) {
                    Damageable dam = (Damageable) tool.getItemMeta();

                    if (dam != null) {
                        dam.setDamage(0);
                        tool.setItemMeta((ItemMeta) dam);
                    }
                }
            }

            if (config.getBoolean("LevelUpEvents.DropLoot.enabled")) {
                int n = rand.nextInt(100);

                if (n <= config.getInt("LevelUpEvents.DropLoot.percentage")) {
                    int index = rand.nextInt(Lists.DROPLOOT.size());
                    Material m = Material.getMaterial(Lists.DROPLOOT.get(index));

                    if (m != null) {
                        int amount = rand.nextInt(config.getInt("LevelUpEvents.DropLoot.maximumDrop") - config.getInt("LevelUpEvents.DropLoot.minimumDrop"));
                        amount = amount + config.getInt("LevelUpEvents.DropLoot.minimumDrop");

                        ItemStack drop = new ItemStack(m, amount);

                        if (player.getInventory().addItem(drop).size() != 0) { //adds items to (full) inventory
                            player.getWorld().dropItem(player.getLocation(), drop); //drops item when inventory is full
                        } // no else as it gets added in if
                    }
                }
            }

            if (config.getBoolean("LevelUpEvents.RandomModifier.enabled")) {
                int n = rand.nextInt(100);

                if (n <= config.getInt("LevelUpEvents.RandomModifier.percentage")) {
                    for (int i = 0; i < player.getInventory().getSize(); i++) { //getting the inventory slot of the tool
                        if (player.getInventory().getItem(i) != null && player.getInventory().getItem(i).equals(tool)) {  //Can be NULL!
                            for (int j = 0; j < config.getInt("LevelUpEvents.RandomModifier.AmountOfModifiers"); j++) {

                                List<Modifier> mods = new ArrayList<>(modManager.getAllowedMods()); //necessary as the failed modifiers get removed from the list (so a copy is in order)

                                if (!config.getBoolean("LevelUpEvents.RandomModifier.AllowExtraModifier")) {
                                    mods.remove(ExtraModifier.instance());
                                }

                                int index;

                                do {
                                    index = new Random().nextInt(mods.size());
                                    appliedRandomMod = mods.get(index).applyMod(player, tool, true);

                                    if (!appliedRandomMod) {
                                        mods.remove(index); //Remove the failed modifier from the the list of the possibles
                                    }

                                    if (mods.isEmpty()) {
                                        break;
                                    } //Secures that the while will terminate after some time (if all modifiers were removed)
                                } while (!appliedRandomMod);
                            }
                            break;
                        }
                    }
                }
            }

            if (config.getBoolean("LevelUpEvents.DropXP.enabled")) {
                int n = rand.nextInt(100);

                if (n <= config.getInt("LevelUpEvents.DropXP.percentage")) {
                    ExperienceOrb orb = player.getWorld().spawn(player.getLocation(), ExperienceOrb.class);
                    orb.setExperience(config.getInt("LevelUpEvents.DropXP.amount"));
                }
            }
        }

        //------------------------------------------------------------------------------------------------------------------------

        if (config.getBoolean("Sound.OnLevelUp")) {
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 0.5F);
        }

        if (config.getInt("AddModifierSlotsPerLevel") > 0 && !(config.getBoolean("LevelUpEvents.RandomModifier.DisableAddingNewSlots") && appliedRandomMod)) {
            int slots = modManager.getFreeSlots(tool);

            if (!(slots == Integer.MAX_VALUE || slots < 0)) {
                slots += config.getInt("AddModifierSlotsPerLevel");
            } else {
                slots = Integer.MAX_VALUE;
            }

            modManager.setFreeSlots(tool, slots);
        }

        ChatWriter.sendActionBar(player, ItemGenerator.getDisplayName(tool) + ChatColor.GOLD + " just got a Level-Up!");
        ChatWriter.log(false, player.getDisplayName() + " leveled up " + ItemGenerator.getDisplayName(tool) + ChatColor.WHITE + " (" + tool.getType().toString() + ")!");
    }
}

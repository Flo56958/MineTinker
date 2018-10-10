package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Data.Modifiers;
import de.flo56958.MineTinker.Data.Strings;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class AnvilListener implements Listener {

    private static final FileConfiguration config = Main.getPlugin().getConfig();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.isCancelled()) { return; }
            if (!Lists.WORLDS.contains(e.getWhoClicked().getWorld().getName())) { return; }
            Inventory i = e.getClickedInventory();
            if (!(i instanceof AnvilInventory)) { return; }
        int s = e.getRawSlot();
        if (s != 1) { return; }      //Mod-Slot
        ItemStack tool = i.getItem(0);
        if (tool == null) { return; }
        if (!tool.getItemMeta().hasLore()) { return; }
        if (!tool.getItemMeta().getLore().contains(Strings.IDENTIFIER)) { return; }

        ItemStack newTool = null;

        if (e.getWhoClicked().getItemOnCursor().getAmount() == 1) {
            boolean isModifier = true;
            ItemStack modifier = e.getWhoClicked().getItemOnCursor();
            if (modifier.equals(Modifiers.SELFREPAIR_MODIFIER)) {
                if (Lists.getAllowedModifiers().contains(config.getString("Modifiers.Self-Repair.name").toLowerCase())) {
                    newTool = ItemGenerator.ToolModifier(tool, config.getString("Modifiers.Self-Repair.name").toLowerCase(), (Player) e.getWhoClicked(), false);
                }
            } else if (modifier.getType().equals(Material.NETHER_STAR)) {
                if (Lists.getAllowedModifiers().contains(config.getString("Modifiers.Extra-Modifier.name").toLowerCase())) {
                    newTool = ItemGenerator.ToolModifier(tool, config.getString("Modifiers.Extra-Modifier.name").toLowerCase(), (Player) e.getWhoClicked(), false);
                }
            } else if (modifier.equals(Modifiers.HASTE_MODIFIER)) {
                if (Lists.getAllowedModifiers().contains(config.getString("Modifiers.Haste.name").toLowerCase())) {
                    newTool = ItemGenerator.ToolModifier(tool, config.getString("Modifiers.Haste.name").toLowerCase(), (Player) e.getWhoClicked(), false);
                }
            } else if (modifier.equals(Modifiers.REINFORCED_MODIFIER)) {
                if (Lists.getAllowedModifiers().contains(config.getString("Modifiers.Reinforced.name").toLowerCase())) {
                    newTool = ItemGenerator.ToolModifier(tool, config.getString("Modifiers.Reinforced.name").toLowerCase(), (Player) e.getWhoClicked(), false);
                }
            } else if (modifier.equals(Modifiers.SHARPNESS_MODIFIER)) {
                if (Lists.getAllowedModifiers().contains(config.getString("Modifiers.Sharpness.name").toLowerCase())) {
                    newTool = ItemGenerator.ToolModifier(tool, config.getString("Modifiers.Sharpness.name").toLowerCase(), (Player) e.getWhoClicked(), false);
                }
            } else if (modifier.getType().equals(Material.EXPERIENCE_BOTTLE)) {
                if (Lists.getAllowedModifiers().contains(config.getString("Modifiers.XP.name").toLowerCase())) {
                    newTool = ItemGenerator.ToolModifier(tool, config.getString("Modifiers.XP.name").toLowerCase(), (Player) e.getWhoClicked(), false);
                }
            } else if (modifier.equals(Modifiers.LUCK_MODIFIER)) {
                if (Lists.getAllowedModifiers().contains(config.getString("Modifiers.Luck.name").toLowerCase())) {
                    newTool = ItemGenerator.ToolModifier(tool, config.getString("Modifiers.Luck.name").toLowerCase(), (Player) e.getWhoClicked(), false);
                }
            } else if (modifier.equals(Modifiers.SILKTOUCH_MODIFIER)) {
                if (Lists.getAllowedModifiers().contains(config.getString("Modifiers.Silk-Touch.name").toLowerCase())) {
                    newTool = ItemGenerator.ToolModifier(tool, config.getString("Modifiers.Silk-Touch.name").toLowerCase(), (Player) e.getWhoClicked(), false);
                }
            } else if (modifier.equals(Modifiers.FIERY_MODIFIER)) {
                if (Lists.getAllowedModifiers().contains(config.getString("Modifiers.Fiery.name").toLowerCase())) {
                    newTool = ItemGenerator.ToolModifier(tool, config.getString("Modifiers.Fiery.name").toLowerCase(), (Player) e.getWhoClicked(), false);
                }
            } else if (modifier.equals(Modifiers.AUTOSMELT_MODIFIER)) {
                if (Lists.getAllowedModifiers().contains(config.getString("Modifiers.Auto-Smelt.name").toLowerCase())) {
                    newTool = ItemGenerator.ToolModifier(tool, config.getString("Modifiers.Auto-Smelt.name").toLowerCase(), (Player) e.getWhoClicked(), false);
                }
            } else if (modifier.equals(Modifiers.POWER_MODIFIER)) {
                if (Lists.getAllowedModifiers().contains(config.getString("Modifiers.Power.name").toLowerCase())) {
                    newTool = ItemGenerator.ToolModifier(tool, config.getString("Modifiers.Power.name").toLowerCase(), (Player) e.getWhoClicked(), false);
                }
            } else if (modifier.equals(Modifiers.BEHEADING_MODIFIER)) {
                if (Lists.getAllowedModifiers().contains(config.getString("Modifiers.Beheading.name").toLowerCase())) {
                    newTool = ItemGenerator.ToolModifier(tool, config.getString("Modifiers.Beheading.name").toLowerCase(), (Player) e.getWhoClicked(), false);
                }
            } else if (modifier.equals(Modifiers.INFINITY_MODIFIER)) {
                if (Lists.getAllowedModifiers().contains(config.getString("Modifiers.Infinity.name").toLowerCase())) {
                    newTool = ItemGenerator.ToolModifier(tool, config.getString("Modifiers.Infinity.name").toLowerCase(), (Player) e.getWhoClicked(), false);
                }
            } else if (modifier.equals(Modifiers.SHULKING_MODIFIER)) {
                if (Lists.getAllowedModifiers().contains(config.getString("Modifiers.Shulking.name").toLowerCase())) {
                    newTool = ItemGenerator.ToolModifier(tool, config.getString("Modifiers.Shulking.name").toLowerCase(), (Player) e.getWhoClicked(), false);
                }
            } else if (modifier.equals(Modifiers.ENDER_MODIFIER)) {
                if (Lists.getAllowedModifiers().contains(config.getString("Modifiers.Ender.name").toLowerCase())) {
                    newTool = ItemGenerator.ToolModifier(tool, config.getString("Modifiers.Ender.name").toLowerCase(), (Player) e.getWhoClicked(), false);
                }
            } else if (modifier.equals(Modifiers.POISONOUS_MODIFIER)) {
                if (Lists.getAllowedModifiers().contains(config.getString("Modifiers.Poisonous.name").toLowerCase())) {
                    newTool = ItemGenerator.ToolModifier(tool, config.getString("Modifiers.Poisonous.name").toLowerCase(), (Player) e.getWhoClicked(), false);
                }
            } else if (modifier.equals(Modifiers.GLOWING_MODIFIER)) {
                if (Lists.getAllowedModifiers().contains(config.getString("Modifiers.Glowing.name").toLowerCase())) {
                    newTool = ItemGenerator.ToolModifier(tool, config.getString("Modifiers.Glowing.name").toLowerCase(), (Player) e.getWhoClicked(), false);
                }
            } else if (modifier.equals(Modifiers.SWEEPING_MODIFIER)) {
                if (Lists.getAllowedModifiers().contains(config.getString("Modifiers.Sweeping.name").toLowerCase())) {
                    newTool = ItemGenerator.ToolModifier(tool, config.getString("Modifiers.Sweeping.name").toLowerCase(), (Player) e.getWhoClicked(), false);
                }
            } else if (modifier.equals(Modifiers.KNOCKBACK_MODIFIER)) {
                if (Lists.getAllowedModifiers().contains(config.getString("Modifiers.Knockback.name").toLowerCase())) {
                    newTool = ItemGenerator.ToolModifier(tool, config.getString("Modifiers.Knockback.name").toLowerCase(), (Player) e.getWhoClicked(), false);
                }
            } else if (modifier.equals(Modifiers.TIMBER_MODIFIER)) {
                if (Lists.getAllowedModifiers().contains(config.getString("Modifiers.Timber.name").toLowerCase())) {
                    newTool = ItemGenerator.ToolModifier(tool, config.getString("Modifiers.Timber.name").toLowerCase(), (Player) e.getWhoClicked(), false);
                }
            } else if (modifier.equals(Modifiers.WEBBED_MODIFIER)) {
                if (Lists.getAllowedModifiers().contains(config.getString("Modifiers.Webbed.name").toLowerCase())) {
                    newTool = ItemGenerator.ToolModifier(tool, config.getString("Modifiers.Webbed.name").toLowerCase(), (Player) e.getWhoClicked(), false);
                }
            } else if (modifier.equals(Modifiers.MELTING_MODIFIER)) {
                if (Lists.getAllowedModifiers().contains(config.getString("Modifiers.Melting.name").toLowerCase())) {
                    newTool = ItemGenerator.ToolModifier(tool, config.getString("Modifiers.Melting.name").toLowerCase(), (Player) e.getWhoClicked(), false);
                }
            } else if (modifier.equals(Modifiers.DIRECTING_MODIFIER)) {
                if (Lists.getAllowedModifiers().contains(config.getString("Modifiers.Directing.name").toLowerCase())) {
                    newTool = ItemGenerator.ToolModifier(tool, config.getString("Modifiers.Directing.name").toLowerCase(), (Player) e.getWhoClicked(), false);
                }
            } else {
                isModifier = false;
            }
            if (isModifier) {
                if (newTool == null) {
                    return;
                }
                int newAmount = e.getWhoClicked().getItemOnCursor().getAmount() - 1;
                e.getWhoClicked().getItemOnCursor().setAmount(newAmount);
                e.setCancelled(true);
                return;
            }
        }

        if (config.getBoolean("Upgradeable") && e.getWhoClicked().hasPermission("minetinker.tool.upgrade")) {
            if (e.getWhoClicked().getItemOnCursor().getAmount() == 1) { //Shovel
                if (Lists.SHOVELS.contains(tool.getType().toString())) {
                    newTool = ItemGenerator.itemUpgrader(tool, e.getWhoClicked().getItemOnCursor(), (Player) e.getWhoClicked());
                }
            } else if (e.getWhoClicked().getItemOnCursor().getAmount() == 2) { //Sword / Hoe
                if (Lists.SWORDS.contains(tool.getType().toString()) || Lists.HOES.contains(tool.getType().toString())) {
                    newTool = ItemGenerator.itemUpgrader(tool, e.getWhoClicked().getItemOnCursor(), (Player) e.getWhoClicked());
                }
            } else if (e.getWhoClicked().getItemOnCursor().getAmount() == 3) { //Axe / Pickaxe
                if (Lists.AXES.contains(tool.getType().toString()) || Lists.PICKAXES.contains(tool.getType().toString())) {
                    newTool = ItemGenerator.itemUpgrader(tool, e.getWhoClicked().getItemOnCursor(), (Player) e.getWhoClicked());
                }
            }
            if (newTool != null) {
                e.getWhoClicked().getItemOnCursor().setAmount(0);
                e.setCancelled(true);
            }
        }
    }
}
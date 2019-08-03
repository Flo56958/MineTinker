package de.flo56958.MineTinker.Modifiers;

import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

//TODO: Remove Interface and integrate into the Modifier-Class, so every modifier can be enchanted
public interface Enchantable {
    void enchantItem(Player p, ItemStack item);

    default void _createModifierItem(FileConfiguration config, Player p, Modifier mod, String modifier) {
        if (config.getBoolean(modifier + ".Recipe.Enabled")) {
            return;
        }

        Location location = p.getLocation();
        World world = location.getWorld();
        PlayerInventory inventory = p.getInventory();

        if (world == null) {
            return;
        }

        if (p.getGameMode() == GameMode.CREATIVE) {
            world.dropItemNaturally(location, mod.getModItem());

            if (Main.getPlugin().getConfig().getBoolean("Sound.OnEnchanting")) {
                p.playSound(location, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 0.5F);
            }

            ChatWriter.log(false, p.getDisplayName() + " created a " + mod.getName() + "-Modifiers in Creative!");
        } else if (p.getLevel() >= config.getInt(modifier + ".EnchantCost")) {
            int amount = inventory.getItemInMainHand().getAmount();
            int newLevel = p.getLevel() - config.getInt(modifier + ".EnchantCost");

            p.setLevel(newLevel);
            inventory.getItemInMainHand().setAmount(amount - 1);

            if (inventory.addItem(mod.getModItem()).size() != 0) { //adds items to (full) inventory
                world.dropItem(location, mod.getModItem());
            } // no else as it gets added in if

            if (Main.getPlugin().getConfig().getBoolean("Sound.OnEnchanting")) {
                p.playSound(location, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 0.5F);
            }

            ChatWriter.log(false, p.getDisplayName() + " created a " + mod.getName() + "-Modifiers!");
        } else {
            ChatWriter.sendActionBar(p, ChatColor.RED + "" + config.getInt(modifier + ".EnchantCost") + " levels required!");
            ChatWriter.log(false, p.getDisplayName() + " tried to create a " + mod.getName() + "-Modifiers but had not enough levels!");
        }

    }
}

package de.flo56958.minetinker.modifiers;

import de.flo56958.minetinker.Main;
import de.flo56958.minetinker.utilities.ChatWriter;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

//TODO: Remove Interface and integrate into the Modifier-Class, so every modifier can be enchanted
public interface Enchantable {
    void enchantItem(Player p, ItemStack item);

    default void _createModifierItem(FileConfiguration config, Player p, Modifier mod, String modifier) {
        if (config.getBoolean(modifier + ".Recipe.Enabled")) return;

        if (p.getGameMode().equals(GameMode.CREATIVE) && p.getLocation().getWorld() != null) {
            p.getLocation().getWorld().dropItemNaturally(p.getLocation(), mod.getModItem());

            if (Main.getPlugin().getConfig().getBoolean("Sound.OnEnchanting")) {
                p.playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 0.5F);
            }

            ChatWriter.log(false, p.getDisplayName() + " created a " + mod.getName() + "-modifiers in Creative!");
        } else if (p.getLevel() >= config.getInt(modifier + ".EnchantCost")) {
            int amount = p.getInventory().getItemInMainHand().getAmount();
            int newLevel = p.getLevel() - config.getInt(modifier + ".EnchantCost");

            p.setLevel(newLevel);
            p.getInventory().getItemInMainHand().setAmount(amount - 1);

            if (p.getInventory().addItem(mod.getModItem()).size() != 0) { //adds items to (full) inventory
                p.getWorld().dropItem(p.getLocation(), mod.getModItem());
            } // no else as it gets added in if

            if (Main.getPlugin().getConfig().getBoolean("Sound.OnEnchanting")) {
                p.playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 0.5F);
            }

            ChatWriter.log(false, p.getDisplayName() + " created a " + mod.getName() + "-modifiers!");
        } else {
            ChatWriter.sendActionBar(p, ChatColor.RED + "" + config.getInt(modifier + ".EnchantCost") + " levels required!");
            ChatWriter.log(false, p.getDisplayName() + " tried to create a " + mod.getName() + "-modifiers but had not enough levels!");
        }

    }
}

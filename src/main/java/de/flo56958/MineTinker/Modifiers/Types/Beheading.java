package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Craftable;
import de.flo56958.MineTinker.Modifiers.Enchantable;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import de.flo56958.MineTinker.Utilities.modifiers_Config;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Beheading extends Modifier implements Enchantable, Craftable {

    private int percentagePerLevel;

    public Beheading() {
        super(ModifierType.BEHEADING,
                ChatColor.DARK_GRAY,
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.SWORD)),
                Main.getPlugin());
    }
    
    public void reload() {
    	FileConfiguration config = getConfig();
    	config.options().copyDefaults(true);
    	
    	String key = "Beheading";
    	config.addDefault(key + ".allowed", true);
    	config.addDefault(key + ".name", key);
    	config.addDefault(key + ".name_modifier", "Enchanted Wither-Skull");
    	config.addDefault(key + ".description", "Chance to drop the head of the mob!");
    	config.addDefault(key + ".MaxLevel", 10);
    	config.addDefault(key + ".PercentagePerLevel", "10 #= 100% at Level 10");
    	config.addDefault(key + ".EnchantCost", 10);
    	config.addDefault(key + ".Recipe.Enabled", false);
    	
    	init(config.getString("Beheading.name"),
                "[" + config.getString("Beheading.name_modifier") + "] " + config.getString("Beheading.description"),
                config.getInt("Beheading.MaxLevel"),
                ItemGenerator.itemEnchanter(Material.WITHER_SKELETON_SKULL, ChatColor.DARK_GRAY + config.getString("Beheading.name_modifier"), 1, Enchantment.LOOT_BONUS_MOBS, 1));
        
        this.percentagePerLevel = config.getInt("Beheading.PercentagePerLevel");
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        return checkAndAdd(p, tool, this, "beheading", isCommand);
    }

    public ItemStack effect(Player p, ItemStack tool, Entity mob) {
        ItemStack loot = new ItemStack(Material.AIR, 1);
        if (p.hasPermission("minetinker.Beheading.use")) {
            if (modManager.hasMod(tool, this)) {
                Random rand = new Random();
                int n = rand.nextInt(100);
                if (n <= this.percentagePerLevel * modManager.getModLevel(tool, this)) {
                    if (mob.getType().equals(EntityType.CREEPER)) {
                        loot = new ItemStack(Material.CREEPER_HEAD, 1);
                    } else if (mob.getType().equals(EntityType.SKELETON)) {
                        loot = new ItemStack(Material.SKELETON_SKULL, 1);
                    } else if (mob.getType().equals(EntityType.WITHER_SKELETON)) {
                        loot = new ItemStack(Material.WITHER_SKELETON_SKULL, 1);
                    } else if (mob.getType().equals(EntityType.ZOMBIE)) {
                        loot = new ItemStack(Material.ZOMBIE_HEAD, 1);
                    } else if (mob.getType().equals(EntityType.ZOMBIE_VILLAGER)) {
                        loot = new ItemStack(Material.ZOMBIE_HEAD, 1);
                    } else if (mob.getType().equals(EntityType.PLAYER)) {
                        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
                        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
                        headMeta.setOwningPlayer((OfflinePlayer) mob);
                        head.setItemMeta(headMeta);
                        loot = head;
                    }
                    if (loot.getType() != Material.AIR) {
                        ChatWriter.log(false, p.getDisplayName() + " triggered Beheading on " + ItemGenerator.getDisplayName(tool) + ChatColor.WHITE + " (" + tool.getType().toString() + ")!");
                    }
                }
            }
        }
        return loot;
    }

    @Override
    public void enchantItem(Player p, ItemStack item) {
        if (!p.hasPermission("minetinker.Beheading.craft")) { return; }
        _createModifierItem(getConfig(), p, this, "Beheading");
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Beheading", "Modifier_Beheading");
    }
    
    private static FileConfiguration getConfig() {
    	return Main.getConfigurations().getConfig(modifiers_Config.Beheading);
    }
    
    public boolean isAllowed() {
    	return getConfig().isBoolean("Beheading.allowed");
    }
}

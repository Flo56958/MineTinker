package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.events.MTEntityDeathEvent;
import de.flo56958.minetinker.Main;
import de.flo56958.minetinker.modifiers.Craftable;
import de.flo56958.minetinker.modifiers.Enchantable;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utilities.ChatWriter;
import de.flo56958.minetinker.utilities.ConfigurationManager;
import de.flo56958.minetinker.utilities.ItemGenerator;
import de.flo56958.minetinker.utilities.Modifiers_Config;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Beheading extends Modifier implements Enchantable, Craftable, Listener {

    private int percentagePerLevel;

    private static Beheading instance;

    public static Beheading instance() {
        synchronized (Beheading.class) {
            if (instance == null) instance = new Beheading();
        }
        return instance;
    }

    private Beheading() {
        super(ModifierType.BEHEADING,
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.SWORD, ToolType.TRIDENT)),
                Main.getPlugin());
        Bukkit.getPluginManager().registerEvents(this, Main.getPlugin());
    }

    @Override
    public List<Enchantment> getAppliedEnchantments() {
        return new ArrayList<>();
    }

    @Override
    public void reload() {
    	FileConfiguration config = getConfig();
    	config.options().copyDefaults(true);
    	
    	String key = "Beheading";
    	config.addDefault(key + ".allowed", true);
    	config.addDefault(key + ".name", key);
    	config.addDefault(key + ".name_modifier", "Enchanted Wither-Skull");
        config.addDefault(key + ".modifier_item", "WITHER_SKELETON_SKULL"); //Needs to be a viable Material-Type
        config.addDefault(key + ".description", "Chance to drop the head of the mob!");
        config.addDefault(key + ".description_modifier", "%WHITE%Modifier-Item for the Beheading-Modifier");
    	config.addDefault(key + ".Color", "%DARK_GRAY%");
    	config.addDefault(key + ".MaxLevel", 10);
    	config.addDefault(key + ".PercentagePerLevel", 10);  //= 100% at Level 10
    	config.addDefault(key + ".EnchantCost", 10);
    	config.addDefault(key + ".Recipe.Enabled", false);
    	
    	ConfigurationManager.saveConfig(config);

        String name = config.getString(key + ".name");
        String modName = config.getString(key + ".name_modifier");
        String description = config.getString(key + ".description");
        String color = config.getString(key + ".Color");
        int maxLevel = config.getInt(key + ".MaxLevel");
        String modItem = config.getString(key + ".modifier_item");
        String modDescription = config.getString(key + ".description_modifier");

        if (name == null || modName == null || description == null || color == null || modItem == null || modDescription == null) return;

        init(name, "[" + modName + "] " + description, ChatWriter.getColor(color), maxLevel,
                modManager.createModifierItem(Material.getMaterial(modItem), ChatWriter.getColor(color) + modName,
                        ChatWriter.addColors(modDescription), this));

        this.percentagePerLevel = config.getInt("Beheading.PercentagePerLevel");
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        return checkAndAdd(p, tool, this, "beheading", isCommand);
    }

    @Override
    public void removeMod(ItemStack tool) { }

    /**
     * Effect for getting the mob heads
     */
    @EventHandler(priority = EventPriority.LOW) //For Directing
    public void effect(MTEntityDeathEvent event) {
        if (!this.isAllowed()) return;

        Player p = event.getPlayer();
        ItemStack tool = event.getTool();
        LivingEntity mob = event.getEvent().getEntity();
        ItemStack loot = new ItemStack(Material.AIR, 1);

        if (p.hasPermission("minetinker.modifiers.beheading.use")) {
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

                        if (head.getItemMeta() != null) {
                            SkullMeta headMeta = (SkullMeta) head.getItemMeta();
                            headMeta.setOwningPlayer((OfflinePlayer) mob);
                            head.setItemMeta(headMeta);
                        }

                        loot = head;
                    }

                    if (loot.getType() != Material.AIR) {
                        event.getEvent().getDrops().add(loot);
                        ChatWriter.log(false, p.getDisplayName() + " triggered Beheading on " + ItemGenerator.getDisplayName(tool) + ChatColor.WHITE + " (" + tool.getType().toString() + ")!");
                    }
                }
            }
        }
    }

    @Override
    public void enchantItem(Player p, ItemStack item) {
        if (!p.hasPermission("minetinker.modifiers.beheading.craft")) return;
        _createModifierItem(getConfig(), p, this, "Beheading");
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Beheading", "Modifier_Beheading");
    }
    
    private static FileConfiguration getConfig() {
    	return ConfigurationManager.getConfig(Modifiers_Config.Beheading);
    }

    @Override
    public boolean isAllowed() {
    	return getConfig().getBoolean("Beheading.allowed");
    }
}

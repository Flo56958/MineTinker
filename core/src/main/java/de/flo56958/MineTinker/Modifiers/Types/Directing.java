package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.MTEntityDeathEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

public class Directing extends Modifier implements Listener {

    private static Directing instance;

    private boolean workInPVP;
    private boolean workOnXP;
    private int minimumLevelForXP;

    public static Directing instance() {
        synchronized (Directing.class) {
            if (instance == null) instance = new Directing();
        }
        return instance;
    }

    private Directing() {
        super("Directing", "Directing.yml",
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.CROSSBOW, ToolType.SWORD, ToolType.TRIDENT)),
                Main.getPlugin());
        Bukkit.getPluginManager().registerEvents(this, Main.getPlugin());
    }

    @Override
    public void reload() {
    	FileConfiguration config = getConfig();
    	config.options().copyDefaults(true);
    	
    	String key = "Directing";
    	config.addDefault(key + ".allowed", true);
    	config.addDefault(key + ".name", key);
    	config.addDefault(key + ".name_modifier", "Enhanced Compass");
        config.addDefault(key + ".modifier_item", "COMPASS"); //Needs to be a viable Material-Type
        config.addDefault(key + ".description", "Loot goes directly into Inventory!");
        config.addDefault(key + ".description_modifier", "%WHITE%Modifier-Item for the Directing-Modifier");
        config.addDefault(key + ".MaxLevel", 1);
        config.addDefault(key + ".worksOnXP", true);
        config.addDefault(key + ".minimumLevelToGetXP", 1); //Modifier-Level to give Player XP
        config.addDefault(key + ".workinpvp", true);
        config.addDefault(key + ".Color", "%GRAY%");

        config.addDefault(key + ".Recipe.Enabled", true);
    	config.addDefault(key + ".Recipe.Top", "ECE");
    	config.addDefault(key + ".Recipe.Middle", "CIC");
    	config.addDefault(key + ".Recipe.Bottom", "ECE");

        Map<String, String> recipeMaterials = new HashMap<>();
        recipeMaterials.put("C", "COMPASS");
        recipeMaterials.put("E", "ENDER_PEARL");
        recipeMaterials.put("I", "IRON_BLOCK");

        config.addDefault(key + ".Recipe.Materials", recipeMaterials);
    	
    	ConfigurationManager.saveConfig(config);
        ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

        init(config.getString(key + ".name"),
                "[" + config.getString(key + ".name_modifier") + "] \u200B" + config.getString(key + ".description"),
                ChatWriter.getColor(config.getString(key + ".Color")),
                config.getInt(key + ".MaxLevel"),
                modManager.createModifierItem(Material.getMaterial(config.getString(key + ".modifier_item")), ChatWriter.getColor(config.getString(key + ".Color")) + config.getString(key + ".name_modifier"), ChatWriter.addColors(config.getString(key + ".description_modifier")), this));

        this.workInPVP = config.getBoolean(key + ".workinpvp", true);
        this.workOnXP = config.getBoolean(key + ".workOnXP", true);
        this.minimumLevelForXP = config.getInt(key + ".minimumLevelToGetXP", 1);
    }

    @Override
    public boolean applyMod(Player p, ItemStack tool, boolean isCommand) {
        return Modifier.checkAndAdd(p, tool, this, "directing", isCommand);
    }

    @EventHandler
    public void effect(MTEntityDeathEvent event) {
        if (!this.isAllowed()) {
            return;
        }

        if (!this.workInPVP && event.getEvent().getEntity() instanceof Player) {
            return;
        }

        Player p = event.getPlayer();
        ItemStack tool = event.getTool();

        if (!p.hasPermission("minetinker.modifiers.directing.use")) {
            return;
        }

        if (!modManager.hasMod(tool, this)) {
            return;
        }

        List<ItemStack> drops = event.getEvent().getDrops();

        for (ItemStack current : drops) {
            if (p.getInventory().addItem(current).size() != 0) { //adds items to (full) inventory
                p.getWorld().dropItem(p.getLocation(), current);
            } // no else as it gets added in if-clause
        }

        drops.clear();

        if (this.workOnXP && modManager.getModLevel(tool, this) >= this.minimumLevelForXP) {
            p.giveExp(event.getEvent().getDroppedExp());
            event.getEvent().setDroppedExp(0);
        }
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Directing", "Modifier_Directing");
    }

    @Override
    public boolean isAllowed() {
    	return getConfig().getBoolean("Directing.allowed");
    }
}

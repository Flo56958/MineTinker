package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.MTEntityDeathEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Modifier;
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
    public List<Enchantment> getAppliedEnchantments() {
        return new ArrayList<>();
    }

    @Override
    public void reload() {
    	FileConfiguration config = getConfig();
    	config.options().copyDefaults(true);

    	config.addDefault("Allowed", true);
    	config.addDefault("Name", "Directing");
    	config.addDefault("ModifierItemName", "Enhanced Compass");
        config.addDefault("Description", "Loot goes directly into Inventory!");
        config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Directing-Modifier");
        config.addDefault("MaxLevel", 1);
        config.addDefault("WorksOnXP", true);
        config.addDefault("MinimumLevelToGetXP", 1); //Modifier-Level to give Player XP
        config.addDefault("WorkInPVP", true);
        config.addDefault("Color", "%GRAY%");

        config.addDefault("Recipe.Enabled", true);
    	config.addDefault("Recipe.Top", "ECE");
    	config.addDefault("Recipe.Middle", "CIC");
    	config.addDefault("Recipe.Bottom", "ECE");

        Map<String, String> recipeMaterials = new HashMap<>();
        recipeMaterials.put("C", "COMPASS");
        recipeMaterials.put("E", "ENDER_PEARL");
        recipeMaterials.put("I", "IRON_BLOCK");

        config.addDefault("Recipe.Materials", recipeMaterials);
    	
    	ConfigurationManager.saveConfig(config);
        ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

        init(Material.COMPASS, true);

        this.workInPVP = config.getBoolean("WorkInPVP", true);
        this.workOnXP = config.getBoolean("WorksOnXP", true);
        this.minimumLevelForXP = config.getInt("MinimumLevelToGetXP", 1);
    }

    @Override
    public boolean applyMod(Player p, ItemStack tool, boolean isCommand) {
        return Modifier.checkAndAdd(p, tool, this, "directing", isCommand);
    }

    @Override
    public void removeMod(ItemStack tool) { }

    @EventHandler
    public void effect(MTEntityDeathEvent event) {
        if (!this.isAllowed()) return;
        if (!this.workInPVP && event.getEvent().getEntity() instanceof Player) return;
        Player p = event.getPlayer();
        ItemStack tool = event.getTool();

        if (!p.hasPermission("minetinker.modifiers.directing.use")) return;
        if (!modManager.hasMod(tool, this)) return;

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
}

package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.MTBlockBreakEvent;
import de.flo56958.MineTinker.Events.MTEntityDamageByEntityEvent;
import de.flo56958.MineTinker.Events.MTEntityDamageEvent;
import de.flo56958.MineTinker.Events.MTPlayerInteractEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Experienced extends Modifier implements Listener {

    private int percentagePerLevel;
    private int amount;

    private static Experienced instance;

    public static Experienced instance() {
        synchronized (Experienced.class) {
            if (instance == null) {
                instance = new Experienced();
            }
        }

        return instance;
    }

    @Override
    public String getKey() {
        return "Experienced";
    }

    @Override
    public boolean hasRecipe() {
        return false;
    }

    @Override
    public List<ToolType> getAllowedTools() {
        return Arrays.asList(ToolType.values());
    }

    private Experienced() {
        super(Main.getPlugin());

        Bukkit.getPluginManager().registerEvents(this, Main.getPlugin());
    }

    @Override
    public void reload() {
    	FileConfiguration config = getConfig();
    	config.options().copyDefaults(true);

        String key = getKey();

        config.addDefault(key + ".allowed", true);
    	config.addDefault(key + ".name", key);
    	config.addDefault(key + ".description", "Tool has the chance to drop XP while using it!");
        config.addDefault(key + ".Color", "%GREEN%");
        config.addDefault(key + ".MaxLevel", 10);
    	config.addDefault(key + ".PercentagePerLevel", 2); //= 20% at Level 10 -> every 5th hit / block will trigger Experienced
    	config.addDefault(key + ".Amount", 1); //How much XP should be dropped when triggered
    	config.addDefault(key + ".Recipe.Enabled", false);
    	
    	ConfigurationManager.saveConfig(config);
        ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());
    	
        init("[Bottle o' Experience] \u200B" + config.getString(key + ".description"),
                ChatWriter.getColor(config.getString(key + ".Color")),
                config.getInt(key + ".MaxLevel"),
                new ItemStack(Material.EXPERIENCE_BOTTLE, 1));
        
        this.percentagePerLevel = config.getInt(key + ".PercentagePerLevel");
        this.amount = config.getInt(key + ".Amount");
    }

    @Override
    public boolean applyMod(Player p, ItemStack tool, boolean isCommand) {
        return Modifier.checkAndAdd(p, tool, this, "experienced", isCommand);
    }

    //----------------------------------------------------------

    @EventHandler(ignoreCancelled = true)
    public void effect(MTBlockBreakEvent event) {
        if (!this.isAllowed()) {
            return;
        }

        effect(event.getPlayer(), event.getTool());
    }

    @EventHandler(ignoreCancelled = true)
    public void effect(MTEntityDamageByEntityEvent event) {
        if (!this.isAllowed()) {
            return;
        }

        if (ToolType.BOOTS.contains(event.getTool().getType())
            || ToolType.LEGGINGS.contains(event.getTool().getType())
            || ToolType.CHESTPLATE.contains(event.getTool().getType())
            || ToolType.HELMET.contains(event.getTool().getType())) {

            return; //Makes sure that armor does not get the double effect as it also gets the effect in EntityDamageEvent
        }

        effect(event.getPlayer(), event.getTool());
    }

    @EventHandler(ignoreCancelled = true)
    public void effect(MTEntityDamageEvent event) {
        if (!this.isAllowed()) {
            return;
        }

        effect(event.getPlayer(), event.getTool());
    }

    @EventHandler(ignoreCancelled = true)
    public void effect(MTPlayerInteractEvent event) {
        if (!this.isAllowed()) {
            return;
        }

        effect(event.getPlayer(), event.getTool());
    }

    /**
     * The Effect of the modifier
     * @param p the Player
     * @param tool the Tool
     */
    private void effect(Player p, ItemStack tool) {
        if (!p.hasPermission("minetinker.modifiers.experienced.use")) {
            return;
        }

        if (!modManager.hasMod(tool, this)) {
            return;
        }

        int level = modManager.getModLevel(tool, this);

        Random rand = new Random();
        int n = rand.nextInt(100);

        if (n <= this.percentagePerLevel * level) {
            p.giveExp(this.amount);
            ChatWriter.log(false, p.getDisplayName() + " triggered Experienced on " + ItemGenerator.getDisplayName(tool) + ChatColor.WHITE + " (" + tool.getType().toString() + ")!");
        }
    }
}

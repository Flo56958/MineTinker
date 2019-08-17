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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Experienced extends Modifier implements Listener {

    private int percentagePerLevel;
    private int amount;

    private static Experienced instance;

    public static Experienced instance() {
        synchronized (Experienced.class) {
            if (instance == null) instance = new Experienced();
        }
        return instance;
    }

    private Experienced() {
        super("Experienced", "Experienced.yml",
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.CROSSBOW, ToolType.HOE, ToolType.PICKAXE, ToolType.SHEARS, ToolType.SHOVEL,
                                                ToolType.SWORD, ToolType.TRIDENT, ToolType.FISHINGROD,
                                                ToolType.HELMET, ToolType.CHESTPLATE, ToolType.LEGGINGS, ToolType.BOOTS, ToolType.ELYTRA)),
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
    	config.addDefault("Name", "Experienced");
    	config.addDefault("Description", "Tool has the chance to drop XP while using it!");
        config.addDefault("Color", "%GREEN%");
        config.addDefault("MaxLevel", 10);
    	config.addDefault("PercentagePerLevel", 2); //= 20% at Level 10 -> every 5th hit / block will trigger Experienced
    	config.addDefault("Amount", 1); //How much XP should be dropped when triggered
    	config.addDefault("Recipe.Enabled", false);
        config.addDefault("OverrideLanguagesystem", false);

        ConfigurationManager.saveConfig(config);
        ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());
    	
        init(Material.EXPERIENCE_BOTTLE, false);
        
        this.percentagePerLevel = config.getInt("PercentagePerLevel", 2);
        this.amount = config.getInt("Amount", 1);
    }

    @Override
    public boolean applyMod(Player p, ItemStack tool, boolean isCommand) {
        return Modifier.checkAndAdd(p, tool, this, "experienced", isCommand);
    }

    @Override
    public void removeMod(ItemStack tool) { }

    //----------------------------------------------------------

    @EventHandler
    public void effect(MTBlockBreakEvent event) {
        if (event.isCancelled() || !this.isAllowed()) return;
        effect(event.getPlayer(), event.getTool());
    }

    @EventHandler
    public void effect(MTEntityDamageByEntityEvent event) {
        if (event.isCancelled() || !this.isAllowed()) return;

        if (ToolType.BOOTS.contains(event.getTool().getType())
            || ToolType.LEGGINGS.contains(event.getTool().getType())
            || ToolType.CHESTPLATE.contains(event.getTool().getType())
            || ToolType.HELMET.contains(event.getTool().getType())) return; //Makes sure that armor does not get the double effect as it also gets the effect in EntityDamageEvent

        effect(event.getPlayer(), event.getTool());
    }

    @EventHandler
    public void effect(MTEntityDamageEvent event) {
        if (event.isCancelled() || !this.isAllowed()) return;

        effect(event.getPlayer(), event.getTool());
    }

    @EventHandler
    public void effect(MTPlayerInteractEvent event) {
        if (event.isCancelled() || !this.isAllowed()) return;

        effect(event.getPlayer(), event.getTool());
    }

    /**
     * The Effect of the modifier
     * @param p the Player
     * @param tool the Tool
     */
    private void effect(Player p, ItemStack tool) {
        if (!p.hasPermission("minetinker.modifiers.experienced.use")) return;
        if (!modManager.hasMod(tool, this)) return;

        int level = modManager.getModLevel(tool, this);

        Random rand = new Random();
        int n = rand.nextInt(100);

        if (n <= this.percentagePerLevel * level) {
            p.giveExp(this.amount);
            ChatWriter.log(false, p.getDisplayName() + " triggered Experienced on " + ItemGenerator.getDisplayName(tool) + ChatColor.WHITE + " (" + tool.getType().toString() + ")!");
        }
    }
}

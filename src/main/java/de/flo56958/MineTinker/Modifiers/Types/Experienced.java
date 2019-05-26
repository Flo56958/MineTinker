package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.MTBlockBreakEvent;
import de.flo56958.MineTinker.Events.MTEntityDamageByEntityEvent;
import de.flo56958.MineTinker.Events.MTEntityDamageEvent;
import de.flo56958.MineTinker.Events.MTPlayerInteractEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Craftable;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import de.flo56958.MineTinker.Utilities.Modifiers_Config;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Experienced extends Modifier implements Craftable, Listener {

    private int percentagePerLevel;
    private int amount;

    private static Experienced instance;

    public static Experienced instance() {
        if (instance == null) instance = new Experienced();
        return instance;
    }

    private Experienced() {
        super(ModifierType.EXPERIENCED,
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.HOE, ToolType.PICKAXE, ToolType.SHEARS, ToolType.SHOVEL,
                                                ToolType.SWORD, ToolType.TRIDENT, ToolType.FISHINGROD,
                                                ToolType.HELMET, ToolType.CHESTPLATE, ToolType.LEGGINGS, ToolType.BOOTS, ToolType.ELYTRA)),
                Main.getPlugin());
        Bukkit.getPluginManager().registerEvents(this, Main.getPlugin());
    }

    @Override
    public void reload() {
    	FileConfiguration config = getConfig();
    	config.options().copyDefaults(true);
    	
    	String key = "Experienced";
    	config.addDefault(key + ".allowed", true);
    	config.addDefault(key + ".name", key);
    	config.addDefault(key + ".description", "Tool has the chance to drop XP while using it!");
        config.addDefault(key + ".Color", "%GREEN%");
        config.addDefault(key + ".MaxLevel", 10);
    	config.addDefault(key + ".PercentagePerLevel", 2); //= 20% at Level 10 -> every 5th hit / block will trigger Experienced
    	config.addDefault(key + ".Amount", 1); //How much XP should be dropped when triggered
    	config.addDefault(key + ".Recipe.Enabled", false);
    	
    	ConfigurationManager.saveConfig(config);
    	
        init(config.getString(key + ".name"),
                "[Bottle o' Experience] " + config.getString(key + ".description"),
                ChatWriter.getColor(config.getString(key + ".Color")),
                config.getInt(key + ".MaxLevel"),
                new ItemStack(Material.EXPERIENCE_BOTTLE, 1));
        
        this.percentagePerLevel = config.getInt(key + ".PercentagePerLevel");
        this.amount = config.getInt(key + ".Amount");
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
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
        if (ToolType.BOOTS.getMaterials().contains(event.getTool().getType())
            || ToolType.LEGGINGS.getMaterials().contains(event.getTool().getType())
            || ToolType.CHESTPLATE.getMaterials().contains(event.getTool().getType())
            || ToolType.HELMET.getMaterials().contains(event.getTool().getType())) return; //Makes sure that armor does not get the double effect as it also gets the effect in EntityDamageEvent
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
            ExperienceOrb orb = p.getWorld().spawn(p.getLocation(), ExperienceOrb.class);
            orb.setExperience(this.amount);
            ChatWriter.log(false, p.getDisplayName() + " triggered Experienced on " + ItemGenerator.getDisplayName(tool) + ChatColor.WHITE + " (" + tool.getType().toString() + ")!");
        }
    }

    //-------------------------------------------------------------

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Experienced", "Modifier_Experienced");
    }
    
    private static FileConfiguration getConfig() {
    	return ConfigurationManager.getConfig(Modifiers_Config.Experienced);
    }

    @Override
    public boolean isAllowed() {
    	return getConfig().getBoolean("Experienced.allowed");
    }
}

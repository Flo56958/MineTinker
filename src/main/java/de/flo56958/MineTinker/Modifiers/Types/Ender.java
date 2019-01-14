package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ModifierFailCause;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.MTEntityDamageByEntityEvent;
import de.flo56958.MineTinker.Events.MTProjectileHitEvent;
import de.flo56958.MineTinker.Events.ModifierFailEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Craftable;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import de.flo56958.MineTinker.Utilities.Modifiers_Config;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;

public class Ender extends Modifier implements Craftable, Listener {

    private boolean compatibleWithInfinity;
    private boolean hasSound;

    public Ender() {
        super(ModifierType.ENDER,
                new ArrayList<>(Collections.singletonList(ToolType.BOW)),
                Main.getPlugin());
        Bukkit.getPluginManager().registerEvents(this, Main.getPlugin());
    }
    
    public void reload() {
    	FileConfiguration config = getConfig();
    	config.options().copyDefaults(true);
    	
    	String key = "Ender";
    	config.addDefault(key + ".allowed", true);
    	config.addDefault(key + ".name", key);
    	config.addDefault(key + ".name_modifier", "Special Endereye");
    	config.addDefault(key + ".description", "Teleports you while sneaking to the arrow location!");
    	config.addDefault(key + ".description_modifier", "%WHITE%Modifier-Item for the Ender-Modifier");
        config.addDefault(key + ".Color", "%DARK_GREEN%");
        config.addDefault(key + ".MaxLevel", 2);
        config.addDefault(key + ".Sound", true); //#Enderman-Teleport-Sound
        config.addDefault(key + ".CompatibleWithInfinity", true);
    	config.addDefault(key + ".Recipe.Enabled", true);
    	config.addDefault(key + ".Recipe.Top", "PPP");
    	config.addDefault(key + ".Recipe.Middle", "PEP");
    	config.addDefault(key + ".Recipe.Bottom", "PPP");
    	config.addDefault(key + ".Recipe.Materials.P", "ENDER_PEARL");
    	config.addDefault(key + ".Recipe.Materials.E", "ENDER_EYE");
    	
    	ConfigurationManager.saveConfig(config);
        
        init(config.getString(key + ".name"),
                "[" + config.getString(key + ".name_modifier") + "] " + config.getString(key + ".description"),
                ChatWriter.getColor(config.getString(key + ".Color")),
                config.getInt(key + ".MaxLevel"),
                modManager.createModifierItem(Material.ENDER_EYE, ChatWriter.getColor(config.getString(key + ".Color")) + config.getString(key + ".name_modifier"), ChatWriter.addColors(config.getString(key + ".description_modifier")), this));
        
        this.hasSound = config.getBoolean("Ender.Sound");
        this.compatibleWithInfinity = config.getBoolean("Ender.CompatibleWithInfinity");
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (!this.compatibleWithInfinity) {
            if (modManager.get(ModifierType.INFINITY) != null) {
                if (modManager.hasMod(tool, modManager.get(ModifierType.INFINITY))) {
                    pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
                    return null;
                }
            }
        }
        return Modifier.checkAndAdd(p, tool, this, "ender", isCommand);
    }

    @Override
    public void removeMod(ItemStack tool) { }

    /**
     * The Effect for the ProjectileHitEvent
     * @param event the Event
     */
    @EventHandler
    public void effect(MTProjectileHitEvent event) {
        if (!this.isAllowed()) { return; }
        Player p = event.getPlayer();
        ItemStack tool = event.getTool();
        if (!p.hasPermission("minetinker.modifiers.ender.use")) { return; }
        if (!modManager.hasMod(tool, this)) { return; }
        if (!p.isSneaking()) { return; }

        Location loc = event.getEvent().getEntity().getLocation().clone(); //Location of the Arrow
        p.teleport(new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), p.getLocation().getYaw(), p.getLocation().getPitch()).add(0, 1, 0));
        if (this.hasSound) {
            p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.3F);
        }
        ChatWriter.log(false, p.getDisplayName() + " triggered Ender on " + ItemGenerator.getDisplayName(tool) + ChatColor.GRAY + " (" + tool.getType().toString() + ")!");
    }

    /**
     * The Effect for the EntityDamageByEntityEvent
     * @param event
     */
    @EventHandler
    public void effect(MTEntityDamageByEntityEvent event) {
        if (event.isCancelled() || !this.isAllowed()) { return; }
        Player p = event.getPlayer();
        if (p.equals(event.getEvent().getEntity())) { return; }
        ItemStack tool = event.getTool();
        if (!p.hasPermission("minetinker.modifiers.ender.use")) { return; }
        if (!modManager.hasMod(tool, this)) { return; } //No check needed, as Ender can only be applied on the Bow
        if (modManager.getModLevel(tool, this) < 2) { return; }
        if (!p.isSneaking()) { return; }

        Location loc = event.getEvent().getEntity().getLocation().clone();
        event.getEvent().getEntity().teleport(p.getLocation());
        p.teleport(new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ()));
        if (this.hasSound) {
            p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.3F); //Sound at Players position
            p.getWorld().playSound(event.getEvent().getEntity().getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.3F); //Sound at Entity's position
        }
        ChatWriter.log(false, p.getDisplayName() + " triggered Ender on " + ItemGenerator.getDisplayName(tool) + ChatColor.GRAY + " (" + tool.getType().toString() + ")!");
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Ender", "Modifier_Ender");
    }
    
    private static FileConfiguration getConfig() {
    	return ConfigurationManager.getConfig(Modifiers_Config.Ender);
    }
    
    public boolean isAllowed() {
    	return getConfig().getBoolean("Ender.allowed");
    }
}

package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ModifierFailCause;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.MTEntityDamageByEntityEvent;
import de.flo56958.MineTinker.Events.MTProjectileHitEvent;
import de.flo56958.MineTinker.Events.ModifierFailEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.*;

public class Ender extends Modifier implements Listener {

    private boolean compatibleWithInfinity;
    private boolean hasSound;
    private boolean hasParticles;
    private boolean giveNauseaOnUse;
    private int nauseaDuration;
    private boolean giveBlindnessOnUse;
    private int blindnessDuration;

    private static Ender instance;

    public static Ender instance() {
        synchronized (Ender.class) {
            if (instance == null) {
                instance = new Ender();
            }
        }

        return instance;
    }

    @Override
    public String getKey() {
        return "Ender";
    }

    @Override
    public List<ToolType> getAllowedTools() {
        return Arrays.asList(ToolType.BOW, ToolType.CROSSBOW, ToolType.TRIDENT);
    }

    private Ender() {
        super(Main.getPlugin());

        Bukkit.getPluginManager().registerEvents(this, Main.getPlugin());
    }

    @Override
    public void reload() {
    	FileConfiguration config = getConfig();
    	config.options().copyDefaults(true);

    	config.addDefault("Allowed", true);
    	config.addDefault("Name", "Ender");
    	config.addDefault("ModifierItemName", "Special Endereye");
        config.addDefault("Description", "Teleports you while sneaking to the arrow location!");
    	config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Ender-Modifier");
        config.addDefault("Color", "%DARK_GREEN%");
        config.addDefault("MaxLevel", 2);
        config.addDefault("Sound", true); //#Enderman-Teleport-Sound
        config.addDefault("Particles", true);
        config.addDefault("GiveNauseaOnUse", true);
        config.addDefault("NauseaDuration", 5); //seconds
        config.addDefault("GiveBlindnessOnUse", true);
        config.addDefault("BlindnessDuration", 3); //seconds
        config.addDefault("OverrideLanguagesystem", false);

        config.addDefault("CompatibleWithInfinity", true);

    	config.addDefault("Recipe.Enabled", true);
    	config.addDefault("Recipe.Top", "PPP");
    	config.addDefault("Recipe.Middle", "PEP");
    	config.addDefault("Recipe.Bottom", "PPP");

        Map<String, String> recipeMaterials = new HashMap<>();
        recipeMaterials.put("P", "ENDER_PEARL");
        recipeMaterials.put("E", "ENDER_EYE");

        config.addDefault("Recipe.Materials", recipeMaterials);

    	ConfigurationManager.saveConfig(config);
        ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

        init(Material.ENDER_EYE, true);
        
        this.hasSound = config.getBoolean("Sound", true);
        this.hasParticles = config.getBoolean("Particles", true);
        this.compatibleWithInfinity = config.getBoolean("CompatibleWithInfinity", true);
        this.giveNauseaOnUse = config.getBoolean("GiveNauseaOnUse", true);
        this.nauseaDuration = config.getInt("NauseaDuration", 5) * 20;
        this.giveBlindnessOnUse = config.getBoolean("GiveBlindnessOnUse", true);
        this.blindnessDuration = config.getInt("BlindnessDuration", 3) * 20;
    }

    @Override
    public boolean applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (!this.compatibleWithInfinity) {
            if (modManager.hasMod(tool, Infinity.instance())) {
                pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
                return false;
            }
        }

        return true;
    }

    /**
     * The Effect for the ProjectileHitEvent
     * @param event the Event
     */
    @EventHandler
    public void effect(MTProjectileHitEvent event) {
        if (!this.isAllowed()) {
            return;
        }

        Player p = event.getPlayer();
        ItemStack tool = event.getTool();

        if (!p.hasPermission("minetinker.modifiers.ender.use")) {
            return;
        }

        if (!modManager.hasMod(tool, this)) {
            return;
        }

        if (!p.isSneaking()) {
            return;
        }

        Location loc = event.getEvent().getEntity().getLocation().clone(); //Location of the Arrow
        Location oldLoc = p.getLocation();

        p.teleport(new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), p.getLocation().getYaw(), p.getLocation().getPitch()).add(0, 1, 0));

        if (this.hasSound) {
            p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.3F);
        }

        spawnParticles(p, oldLoc);

        if (this.giveNauseaOnUse) {
            p.removePotionEffect(PotionEffectType.CONFUSION);
            p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, this.nauseaDuration, 0, false, false));
        }
        if (this.giveBlindnessOnUse) {
            p.removePotionEffect(PotionEffectType.BLINDNESS);
            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, this.blindnessDuration, 0, false, false));
        }
        ChatWriter.log(false, p.getDisplayName() + " triggered Ender on " + ItemGenerator.getDisplayName(tool) + ChatColor.GRAY + " (" + tool.getType().toString() + ")!");
    }

    /**
     * The Effect for the EntityDamageByEntityEvent
     * @param event
     */
    @EventHandler(ignoreCancelled = true)
    public void effect(MTEntityDamageByEntityEvent event) {
        if (!this.isAllowed()) {
            return;
        }

        Player player = event.getPlayer();
        Entity entity = event.getEvent().getEntity();

        if (!player.isSneaking()) {
            return;
        }

        if (player.equals(event.getEvent().getEntity())) {
            return;
        }

        if (!player.hasPermission("minetinker.modifiers.ender.use")) {
            return;
        }

        ItemStack tool = event.getTool();

        if (!modManager.hasMod(tool, this)) {
            return; //No check needed, as Ender can only be applied on the Bow
        }

        if (modManager.getModLevel(tool, this) < 2) {
            return;
        }

        // e
        Location loc = entity.getLocation().clone();
        entity.teleport(player.getLocation());

        spawnParticles(player, loc);

        player.teleport(loc);

        if (this.hasSound) {
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.3F); //Sound at Players position
            player.getWorld().playSound(event.getEvent().getEntity().getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.3F); //Sound at Entity's position
        }

        if (this.giveNauseaOnUse) {
            player.removePotionEffect(PotionEffectType.CONFUSION);
            player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, this.nauseaDuration, 0, false, false));

            if (entity instanceof LivingEntity) {
                ((LivingEntity) entity).removePotionEffect(PotionEffectType.CONFUSION);
                ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, this.nauseaDuration, 0, false, false));
            }
        }

        if (this.giveBlindnessOnUse) {
            player.removePotionEffect(PotionEffectType.BLINDNESS);
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, this.blindnessDuration, 0, false, false));

            if (entity instanceof LivingEntity) {
                ((LivingEntity) entity).removePotionEffect(PotionEffectType.BLINDNESS);
                ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, this.blindnessDuration, 0, false, false));
            }
        }

        ChatWriter.log(false, player.getDisplayName() + " triggered Ender on " + ItemGenerator.getDisplayName(tool) + ChatColor.GRAY + " (" + tool.getType().toString() + ")!");
    }

    private void spawnParticles(Player p, Location oldLoc) {
        if (this.hasParticles) {
            AreaEffectCloud cloud = (AreaEffectCloud) p.getWorld().spawnEntity(p.getLocation(), EntityType.AREA_EFFECT_CLOUD);
            cloud.setVelocity(new Vector(0, 1, 0));
            cloud.setRadius(0.5f);
            cloud.setDuration(5);
            cloud.setColor(Color.GREEN);
            cloud.getLocation().setYaw(90);

            AreaEffectCloud cloud2 = (AreaEffectCloud) p.getWorld().spawnEntity(oldLoc, EntityType.AREA_EFFECT_CLOUD);
            cloud2.setVelocity(new Vector(0, 1, 0));
            cloud2.setRadius(0.5f);
            cloud2.setDuration(5);
            cloud2.setColor(Color.GREEN);
            cloud2.getLocation().setPitch(90);
        }
    }
}

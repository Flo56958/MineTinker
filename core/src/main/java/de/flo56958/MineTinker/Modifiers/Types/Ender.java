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
import org.bukkit.enchantments.Enchantment;
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
            if (instance == null) instance = new Ender();
        }
        return instance;
    }

    private Ender() {
        super("Ender", "Ender.yml",
                new ArrayList<>(Arrays.asList(ToolType.BOW, ToolType.CROSSBOW, ToolType.TRIDENT)),
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
    	
    	String key = "Ender";
    	config.addDefault(key + ".allowed", true);
    	config.addDefault(key + ".name", key);
    	config.addDefault(key + ".name_modifier", "Special Endereye");
        config.addDefault(key + ".modifier_item", "ENDER_EYE"); //Needs to be a viable Material-Type
        config.addDefault(key + ".description", "Teleports you while sneaking to the arrow location!");
    	config.addDefault(key + ".description_modifier", "%WHITE%Modifier-Item for the Ender-Modifier");
        config.addDefault(key + ".Color", "%DARK_GREEN%");
        config.addDefault(key + ".MaxLevel", 2);
        config.addDefault(key + ".Sound", true); //#Enderman-Teleport-Sound
        config.addDefault(key + ".Particles", true);
        config.addDefault(key + ".giveNauseaOnUse", true);
        config.addDefault(key + ".nauseaDuration", 5); //seconds
        config.addDefault(key + ".giveBlindnessOnUse", true);
        config.addDefault(key + ".blindnessDuration", 3); //seconds

        config.addDefault(key + ".CompatibleWithInfinity", true);

    	config.addDefault(key + ".Recipe.Enabled", true);
    	config.addDefault(key + ".Recipe.Top", "PPP");
    	config.addDefault(key + ".Recipe.Middle", "PEP");
    	config.addDefault(key + ".Recipe.Bottom", "PPP");

        Map<String, String> recipeMaterials = new HashMap<>();
        recipeMaterials.put("P", "ENDER_PEARL");
        recipeMaterials.put("E", "ENDER_EYE");

        config.addDefault(key + ".Recipe.Materials", recipeMaterials);

    	ConfigurationManager.saveConfig(config);
        ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

        init(config.getString(key + ".name"),
                "[" + config.getString(key + ".name_modifier") + "] \u200B" + config.getString(key + ".description"),
                ChatWriter.getColor(config.getString(key + ".Color")), config.getInt(key + ".MaxLevel"),
                modManager.createModifierItem(Material.getMaterial(config.getString(key + ".modifier_item")),
                ChatWriter.getColor(config.getString(key + ".Color")) + config.getString(key + ".name_modifier"),
                ChatWriter.addColors(config.getString(key + ".description_modifier")), this));
        
        this.hasSound = config.getBoolean(key + ".Sound", true);
        this.hasParticles = config.getBoolean(key + ".Particles", true);
        this.compatibleWithInfinity = config.getBoolean(key + ".CompatibleWithInfinity", true);
        this.giveNauseaOnUse = config.getBoolean(key + ".giveNauseaOnUse", true);
        this.nauseaDuration = config.getInt(key + ".nauseaDuration", 5) * 20;
        this.giveBlindnessOnUse = config.getBoolean(key + ".giveBlindnessOnUse", true);
        this.blindnessDuration = config.getInt(key + ".blindnessDuration", 3) * 20;
    }

    @Override
    public boolean applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (!this.compatibleWithInfinity) {
            if (modManager.hasMod(tool, Infinity.instance())) {
                pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
                return false;
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
        if (!this.isAllowed()) return;

        Player p = event.getPlayer();
        ItemStack tool = event.getTool();

        if (!p.hasPermission("minetinker.modifiers.ender.use")) return;
        if (!modManager.hasMod(tool, this)) return;
        if (!p.isSneaking()) return;

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
    @EventHandler
    public void effect(MTEntityDamageByEntityEvent event) {
        if (event.isCancelled() || !this.isAllowed()) return;

        Player p = event.getPlayer();
        Entity e = event.getEvent().getEntity();
        if (!p.isSneaking()) return;
        if (p.equals(event.getEvent().getEntity())) return;
        if (!p.hasPermission("minetinker.modifiers.ender.use")) return;

        ItemStack tool = event.getTool();
        if (!modManager.hasMod(tool, this)) return; //No check needed, as Ender can only be applied on the Bow
        if (modManager.getModLevel(tool, this) < 2) return;

        Location loc = e.getLocation().clone();
        e.teleport(p.getLocation());

        spawnParticles(p, loc);

        p.teleport(loc);

        if (this.hasSound) {
            p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.3F); //Sound at Players position
            p.getWorld().playSound(event.getEvent().getEntity().getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.3F); //Sound at Entity's position
        }

        if (this.giveNauseaOnUse) {
            p.removePotionEffect(PotionEffectType.CONFUSION);
            p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, this.nauseaDuration, 0, false, false));
            if (e instanceof LivingEntity) {
                ((LivingEntity) e).removePotionEffect(PotionEffectType.CONFUSION);
                ((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, this.nauseaDuration, 0, false, false));
            }
        }

        if (this.giveBlindnessOnUse) {
            p.removePotionEffect(PotionEffectType.BLINDNESS);
            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, this.blindnessDuration, 0, false, false));
            if (e instanceof LivingEntity) {
                ((LivingEntity) e).removePotionEffect(PotionEffectType.BLINDNESS);
                ((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, this.blindnessDuration, 0, false, false));
            }
        }

        ChatWriter.log(false, p.getDisplayName() + " triggered Ender on " + ItemGenerator.getDisplayName(tool) + ChatColor.GRAY + " (" + tool.getType().toString() + ")!");
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

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Ender", "Modifier_Ender");
    }

    @Override
    public boolean isAllowed() {
    	return getConfig().getBoolean("Ender.allowed");
    }
}

package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.MTEntityDamageByEntityEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Enchantable;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Poisonous extends Modifier implements Enchantable, Listener {
	
    private int duration;
    private double durationMultiplier;
    private int effectAmplifier;
    private boolean dropPoisonedMeat;

    private static Poisonous instance;

    public static Poisonous instance() {
        synchronized (Poisonous.class) {
            if (instance == null) instance = new Poisonous();
        }
        return instance;
    }

    private Poisonous() {
        super("Poisonous", "Poisonous.yml",
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.CROSSBOW, ToolType.SWORD, ToolType.TRIDENT,
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
    	
    	String key = "Poisonous";
    	config.addDefault(key + ".allowed", true);
    	config.addDefault(key + ".name", key);
    	config.addDefault(key + ".name_modifier", "Enhanced Rotten Flesh");
        config.addDefault(key + ".modifier_item", "ROTTEN_FLESH"); //Needs to be a viable Material-Type
        config.addDefault(key + ".description", "Poisons enemies!");
        config.addDefault(key + ".description_modifier", "%WHITE%Modifier-Item for the Poisonous-Modifier");
        config.addDefault(key + ".Color", "%DARK_GREEN%");
        config.addDefault(key + ".MaxLevel", 5);
    	config.addDefault(key + ".EnchantCost", 10);
    	config.addDefault(key + ".Duration", 120); //ticks INTEGER (20 ticks ~ 1 sec)
    	config.addDefault(key + ".DurationMultiplier", 1.1); //Duration * (Multiplier^Level) DOUBLE
    	config.addDefault(key + ".EffectAmplifier", 2); //per Level (Level 1 = 0, Level 2 = 2, Level 3 = 4, ...) INTEGER
        config.addDefault(key + ".DropRottenMeatIfPoisoned", false);
    	config.addDefault(key + ".Recipe.Enabled", false);
    	
    	ConfigurationManager.saveConfig(config);
        ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());
        
        init(config.getString(key + ".name"),
                "[" + config.getString(key + ".name_modifier") + "] \u200B" + config.getString(key + ".description"),
                ChatWriter.getColor(config.getString(key + ".Color")),
                config.getInt(key + ".MaxLevel"),
                modManager.createModifierItem(Material.getMaterial(config.getString(key + ".modifier_item")), ChatWriter.getColor(config.getString(key + ".Color")) + config.getString(key + ".name_modifier"), ChatWriter.addColors(config.getString(key + ".description_modifier")), this));
        
        this.duration = config.getInt(key + ".Duration");
        this.durationMultiplier = config.getDouble(key + ".DurationMultiplier");
        this.effectAmplifier = config.getInt(key + ".EffectAmplifier");
        this.dropPoisonedMeat = config.getBoolean(key + ".DropRottenMeatIfPoisoned");
    }

    @Override
    public boolean applyMod(Player p, ItemStack tool, boolean isCommand) {
        return checkAndAdd(p, tool, this, "poisonous", isCommand);
    }

    @Override
    public void removeMod(ItemStack tool) { }

    @EventHandler(ignoreCancelled = true)
    public void effect(MTEntityDamageByEntityEvent event) {
        if (!this.isAllowed()) return;
        if (!(event.getEntity() instanceof LivingEntity)) return;

        Player p = event.getPlayer();
        ItemStack tool = event.getTool();

        if (!p.hasPermission("minetinker.modifiers.poisonous.use")) return;
        if (!modManager.hasMod(tool, this)) return;

        int level = modManager.getModLevel(tool, this);

        int duration = (int) (this.duration * Math.pow(this.durationMultiplier, (level - 1)));
        int amplifier = this.effectAmplifier * (level - 1);
        ((LivingEntity) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.POISON, duration, amplifier, false, false));
        ChatWriter.log(false, p.getDisplayName() + " triggered Poisonous on " + ItemGenerator.getDisplayName(tool) + ChatColor.GRAY + " (" + tool.getType().toString() + ")!");
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!dropPoisonedMeat) return;

        LivingEntity mob = event.getEntity();
        Player p = mob.getKiller();

        if (p == null) return;
        if (Lists.WORLDS.contains(p.getWorld().getName())) return;

        boolean isPoisoned = false;

        for (PotionEffect potionEffect : mob.getActivePotionEffects()) {
            if (potionEffect.getType() == PotionEffectType.POISON) {
                isPoisoned = true;
                break;
            }
        }

        if (!isPoisoned) return;

        int numberOfMeat = 0;
        int numberOfPotatoes = 0;

        Iterator<ItemStack> iterator = event.getDrops().iterator();

        while (iterator.hasNext()) {
            ItemStack drop = iterator.next();
            if (isMeat(drop)) {
                iterator.remove();
                numberOfMeat++;
            } else if (drop.getType() == Material.POTATO) {
                iterator.remove();
                numberOfPotatoes++;
            }
        }

        if (numberOfMeat > 0) event.getDrops().add(new ItemStack(Material.ROTTEN_FLESH, numberOfMeat));
        if (numberOfPotatoes > 0) event.getDrops().add(new ItemStack(Material.POISONOUS_POTATO, numberOfPotatoes));
    }

    private boolean isMeat(ItemStack item) {
        switch (item.getType()) {
            case BEEF:
            case PORKCHOP:
            case COD:
            case SALMON:
            case TROPICAL_FISH:
            case PUFFERFISH:
            case CHICKEN:
            case RABBIT:
            case MUTTON:
                return true;
            default:
                return false;
        }
    }

    @Override
    public void enchantItem(Player p, ItemStack item) {
        if (!p.hasPermission("minetinker.modifiers.poisonous.craft")) return;
        _createModifierItem(getConfig(), p, this, "Poisonous");
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Poisonous", "Modifier_Poisonous");
    }

    @Override
    public boolean isAllowed() {
    	return getConfig().getBoolean("Poisonous.allowed");
    }
}

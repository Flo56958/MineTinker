package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.MTEntityDamageByEntityEvent;
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
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.*;

public class Glowing extends Modifier implements Listener {

    private int duration;
    private double durationMultiplier;

    private static Glowing instance;

    public static Glowing instance() {
        synchronized (Glowing.class) {
            if (instance == null) instance = new Glowing();
        }
        return instance;
    }

    private Glowing() {
        super("Glowing", "Glowing.yml",
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
    	config.addDefault("Name", "Glowing");
    	config.addDefault("ModifierItemName", "Ender-Glowstone");
        config.addDefault("Description", "Makes Enemies glow!");
        config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Glowing-Modifier");
        config.addDefault("Color", "%YELLOW%");
        config.addDefault("MaxLevel", 3);
    	config.addDefault("Duration", 200); //ticks INTEGER (20 ticks ~ 1 sec)
    	config.addDefault("DurationMultiplier", 1.1); //Duration * (Multiplier^Level) DOUBLE
        config.addDefault("OverrideLanguagesystem", false);

    	config.addDefault("Recipe.Enabled", true);
    	config.addDefault("Recipe.Top", "GGG");
    	config.addDefault("Recipe.Middle", "GEG");
    	config.addDefault("Recipe.Bottom", "GGG");

        Map<String, String> recipeMaterials = new HashMap<>();
        recipeMaterials.put("G", "GLOWSTONE_DUST");
        recipeMaterials.put("E", "ENDER_EYE");

        config.addDefault("Recipe.Materials", recipeMaterials);
        
    	ConfigurationManager.saveConfig(config);
        ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

        init(Material.GLOWSTONE, true);

        this.duration = config.getInt("Duration", 200);
        this.durationMultiplier = config.getDouble("DurationMultiplier", 1.1);
    }

    @Override
    public boolean applyMod(Player p, ItemStack tool, boolean isCommand) {
        return Modifier.checkAndAdd(p, tool, this, "glowing", isCommand);
    }

    @Override
    public void removeMod(ItemStack tool) { }

    @EventHandler
    public void effect(MTEntityDamageByEntityEvent event) {
        if (event.isCancelled() || !this.isAllowed()) return;
        if (!(event.getEntity() instanceof LivingEntity)) return;

        Player p = event.getPlayer();
        ItemStack tool = event.getTool();
        LivingEntity e = (LivingEntity) event.getEntity();

        if (!p.hasPermission("minetinker.modifiers.glowing.use")) return;
        if (!modManager.hasMod(tool, this)) return;

        int duration = (int) (this.duration * Math.pow(this.durationMultiplier, (modManager.getModLevel(tool, this) - 1)));
        e.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, duration, 0, false, false));

        ChatWriter.log(false, p.getDisplayName() + " triggered Glowing on " + ItemGenerator.getDisplayName(tool) + ChatColor.GRAY + " (" + tool.getType().toString() + ")!");
    }
}

package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.events.MTEntityDamageByEntityEvent;
import de.flo56958.minetinker.Main;
import de.flo56958.minetinker.modifiers.Craftable;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utilities.ChatWriter;
import de.flo56958.minetinker.utilities.ConfigurationManager;
import de.flo56958.minetinker.utilities.ItemGenerator;
import de.flo56958.minetinker.utilities.Modifiers_Config;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Glowing extends Modifier implements Craftable, Listener {

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
        super(ModifierType.GLOWING,
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.SWORD, ToolType.TRIDENT)),
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
    	
    	String key = "Glowing";
    	config.addDefault(key + ".allowed", true);
    	config.addDefault(key + ".name", key);
    	config.addDefault(key + ".name_modifier", "Ender-Glowstone");
        config.addDefault(key + ".modifier_item", "GLOWSTONE"); //Needs to be a viable Material-Type
        config.addDefault(key + ".description", "Makes Enemies glow!");
        config.addDefault(key + ".description_modifier", "%WHITE%Modifier-Item for the Glowing-Modifier");
        config.addDefault(key + ".Color", "%YELLOW%");
        config.addDefault(key + ".MaxLevel", 3);
    	config.addDefault(key + ".Duration", 200); //ticks INTEGER (20 ticks ~ 1 sec)
    	config.addDefault(key + ".DurationMultiplier", 1.1); //Duration * (Multiplier^Level) DOUBLE
    	config.addDefault(key + ".Recipe.Enabled", true);
    	config.addDefault(key + ".Recipe.Top", "GGG");
    	config.addDefault(key + ".Recipe.Middle", "GEG");
    	config.addDefault(key + ".Recipe.Bottom", "GGG");
    	config.addDefault(key + ".Recipe.Materials.G", "GLOWSTONE_DUST");
    	config.addDefault(key + ".Recipe.Materials.E", "ENDER_EYE");
        
    	ConfigurationManager.saveConfig(config);

        String name = config.getString(key + ".name");
        String modName = config.getString(key + ".name_modifier");
        String description = config.getString(key + ".description");
        String color = config.getString(key + ".Color");
        int maxLevel = config.getInt(key + ".MaxLevel");
        String modItem = config.getString(key + ".modifier_item");
        String modDescription = config.getString(key + ".description_modifier");

        if (name == null || modName == null || description == null || color == null || modItem == null || modDescription == null) return;

        init(name, "[" + modName + "] " + description, ChatWriter.getColor(color), maxLevel,
                modManager.createModifierItem(Material.getMaterial(modItem), ChatWriter.getColor(color) + modName,
                        ChatWriter.addColors(modDescription), this));

        this.duration = config.getInt(key + ".Duration");
        this.durationMultiplier = config.getDouble(key + ".DurationMultiplier");
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
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

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Glowing", "Modifier_Glowing");
    }
    
    private static FileConfiguration getConfig() {
    	return ConfigurationManager.getConfig(Modifiers_Config.Glowing);
    }

    @Override
    public boolean isAllowed() {
    	return getConfig().getBoolean("Glowing.allowed");
    }
}

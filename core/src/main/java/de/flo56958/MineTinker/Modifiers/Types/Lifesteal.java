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
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

public class Lifesteal extends Modifier implements Listener {

    private int percentPerLevel;
    private int percentToTrigger;

    private static Lifesteal instance;

    public static Lifesteal instance() {
        synchronized (Lifesteal.class) {
            if (instance == null) instance = new Lifesteal();
        }
        return instance;
    }

    private Lifesteal() {
        super("Lifesteal", "Lifesteal.yml",
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
        config.addDefault("Name", "Lifesteal");
        config.addDefault("ModifierItemName", "Bloodinfused Netherrack");
        config.addDefault("Description", "Get HP when hitting enemies!");
        config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Lifesteal-Modifier");
        config.addDefault("Color", "%DARK_RED%");
        config.addDefault("MaxLevel", 3);
        config.addDefault("PercentToTrigger", 50);
        config.addDefault("PercentOfDamagePerLevel", 10);
        config.addDefault("OverrideLanguagesystem", false);

        config.addDefault("Recipe.Enabled", true);
        config.addDefault("Recipe.Top", "SRS");
        config.addDefault("Recipe.Middle", "RNR");
        config.addDefault("Recipe.Bottom", "SRS");

        Map<String, String> recipeMaterials = new HashMap<>();
        recipeMaterials.put("N", "NETHERRACK");
        recipeMaterials.put("R", "ROTTEN_FLESH");
        recipeMaterials.put("S", "SOUL_SAND");

        config.addDefault("Recipe.Materials", recipeMaterials);

        ConfigurationManager.saveConfig(config);
        ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

        init(Material.NETHERRACK, true);

        this.percentPerLevel = config.getInt("PercentOfDamagePerLevel", 10);
        this.percentToTrigger = config.getInt("PercentToTrigger", 50);
    }

    @EventHandler(priority = EventPriority.HIGH) //because of Melting
    public void effect(MTEntityDamageByEntityEvent event) {
        if (event.isCancelled() || !this.isAllowed()) return;
        if (event.getPlayer().equals(event.getEvent().getEntity())) return; //when event was triggered by the armor

        Player p = event.getPlayer();
        ItemStack tool = event.getTool();

        if (!p.hasPermission("minetinker.modifiers.lifesteal.use")) return;
        if (!modManager.hasMod(tool, this)) return;

        Random rand = new Random();
        if (rand.nextInt(100) > this.percentToTrigger) return;

        int level = modManager.getModLevel(tool, this);
        double damage = event.getEvent().getDamage();
        double recovery = damage * ((percentPerLevel * level) / 100.0);
        double health = p.getHealth() + recovery;

        AttributeInstance attribute = p.getAttribute(Attribute.GENERIC_MAX_HEALTH);

        if (attribute != null) {
            // for IllegalArgumentExeption if Health is biggen than MaxHealth
            if (health > attribute.getValue()) {
                health = attribute.getValue();
            }

            p.setHealth(health);
        }

        ChatWriter.log(false, p.getDisplayName() + " triggered Lifesteal on " + ItemGenerator.getDisplayName(tool) + ChatColor.GRAY + " (" + tool.getType().toString() + ") and got " + recovery + " health back!");
    }

    @Override
    public boolean applyMod(Player p, ItemStack tool, boolean isCommand) {
        return Modifier.checkAndAdd(p, tool, this, "lifesteal", isCommand);
    }

    @Override
    public void removeMod(ItemStack tool) { }
}

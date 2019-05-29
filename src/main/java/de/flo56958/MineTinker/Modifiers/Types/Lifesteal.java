package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.MTEntityDamageByEntityEvent;
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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Lifesteal extends Modifier implements Craftable, Listener {

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
        super(ModifierType.LIFESTEAL,
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.SWORD, ToolType.TRIDENT)),
                Main.getPlugin());
        Bukkit.getPluginManager().registerEvents(this, Main.getPlugin());
    }

    @Override
    public List<Enchantment> getAppliedEnchantments() {
        List<Enchantment> enchantments = new ArrayList<>();

        return enchantments;
    }

    @Override
    public void reload() {
        FileConfiguration config = getConfig();
        config.options().copyDefaults(true);

        String key = "Lifesteal";
        config.addDefault(key + ".allowed", true);
        config.addDefault(key + ".name", key);
        config.addDefault(key + ".name_modifier", "Bloodinfused Netherrack");
        config.addDefault(key + ".modifier_item", "NETHERRACK"); //Needs to be a viable Material-Type
        config.addDefault(key + ".description", "Get HP when hitting enemies!");
        config.addDefault(key + ".description_modifier", "%WHITE%Modifier-Item for the Lifesteal-Modifier");
        config.addDefault(key + ".Color", "%DARK_RED%");
        config.addDefault(key + ".MaxLevel", 3);
        config.addDefault(key + ".PercentToTrigger", 50);
        config.addDefault(key + ".PercentOfDamagePerLevel", 10);
        config.addDefault(key + ".Recipe.Enabled", true);
        config.addDefault(key + ".Recipe.Top", "SRS");
        config.addDefault(key + ".Recipe.Middle", "RNR");
        config.addDefault(key + ".Recipe.Bottom", "SRS");
        config.addDefault(key + ".Recipe.Materials.N", "NETHERRACK");
        config.addDefault(key + ".Recipe.Materials.R", "ROTTEN_FLESH");
        config.addDefault(key + ".Recipe.Materials.S", "SOUL_SAND");

        ConfigurationManager.saveConfig(config);

        init(config.getString(key + ".name"),
                "[" + config.getString(key + ".name_modifier") + "] " + config.getString(key + ".description"),
                ChatWriter.getColor(config.getString(key + ".Color")),
                config.getInt(key + ".MaxLevel"),
                modManager.createModifierItem(Material.getMaterial(config.getString(key + ".modifier_item")), ChatWriter.getColor(config.getString(key + ".Color")) + config.getString(key + ".name_modifier"), ChatWriter.addColors(config.getString(key + ".description_modifier")), this));

        this.percentPerLevel = config.getInt(key + ".PercentOfDamagePerLevel");
        this.percentToTrigger = config.getInt(key + ".PercentToTrigger");
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

        // TODO: don't call getMaxHealth
        if (health > p.getMaxHealth()) { health = p.getMaxHealth(); } // for IllegalArgumentExeption if Health is biggen than MaxHealth
        p.setHealth(health);

        ChatWriter.log(false, p.getDisplayName() + " triggered Lifesteal on " + ItemGenerator.getDisplayName(tool) + ChatColor.GRAY + " (" + tool.getType().toString() + ") and got " + recovery + " health back!");
    }

    private FileConfiguration getConfig() {
        return ConfigurationManager.getConfig(Modifiers_Config.Lifesteal);
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Lifesteal", "Modifier_Lifesteal");
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        return Modifier.checkAndAdd(p, tool, this, "lifesteal", isCommand);
    }

    @Override
    public void removeMod(ItemStack tool) { }

    @Override
    public boolean isAllowed() {
        return getConfig().getBoolean("Lifesteal.allowed");
    }
}

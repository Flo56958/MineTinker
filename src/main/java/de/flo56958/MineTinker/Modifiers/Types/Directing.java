package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.MTEntityDeathEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Craftable;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import de.flo56958.MineTinker.Utilities.Modifiers_Config;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Directing extends Modifier implements Craftable, Listener {

    private static Directing instance;

    private boolean workInPVP;

    public static Directing instance() {
        if (instance == null) instance = new Directing();
        return instance;
    }

    private Directing() {
        super(ModifierType.DIRECTING,
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
    	
    	String key = "Directing";
    	config.addDefault(key + ".allowed", true);
    	config.addDefault(key + ".name", key);
    	config.addDefault(key + ".name_modifier", "Enhanced Compass");
        config.addDefault(key + ".modifier_item", "COMPASS"); //Needs to be a viable Material-Type
        config.addDefault(key + ".description", "Loot goes directly into Inventory!");
        config.addDefault(key + ".description_modifier", "%WHITE%Modifier-Item for the Directing-Modifier");
        config.addDefault(key + ".workinpvp", true);
        config.addDefault(key + ".Color", "%GRAY%");
        config.addDefault(key + ".Recipe.Enabled", true);
    	config.addDefault(key + ".Recipe.Top", "ECE");
    	config.addDefault(key + ".Recipe.Middle", "CIC");
    	config.addDefault(key + ".Recipe.Bottom", "ECE");
    	config.addDefault(key + ".Recipe.Materials.C", "COMPASS");
    	config.addDefault(key + ".Recipe.Materials.E", "ENDER_PEARL");
    	config.addDefault(key + ".Recipe.Materials.I", "IRON_BLOCK");
    	
    	ConfigurationManager.saveConfig(config);
    	
        init(config.getString(key + ".name"),
                "[" + config.getString(key + ".name_modifier") + "] " + config.getString(key + ".description"),
                ChatWriter.getColor(config.getString(key + ".Color")),
                1,
                modManager.createModifierItem(Material.getMaterial(config.getString(key + ".modifier_item")), ChatWriter.getColor(config.getString(key + ".Color")) + config.getString(key + ".name_modifier"), ChatWriter.addColors(config.getString(key + ".description_modifier")), this));

        this.workInPVP = config.getBoolean(key + ".workinpvp");
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        return Modifier.checkAndAdd(p, tool, this, "directing", isCommand);
    }

    @Override
    public void removeMod(ItemStack tool) { }

    @EventHandler
    public void effect(MTEntityDeathEvent event) {
        if (!this.isAllowed()) return;
        if (!this.workInPVP && event.getEvent().getEntity() instanceof Player) return;

        Player p = event.getPlayer();
        ItemStack tool = event.getTool();

        if (!p.hasPermission("minetinker.modifiers.directing.use")) return;
        if (!modManager.hasMod(tool, this)) return;

        List<ItemStack> drops = event.getEvent().getDrops();

        for (ItemStack current : drops) {
            if (p.getInventory().addItem(current).size() != 0) { //adds items to (full) inventory
                p.getWorld().dropItem(p.getLocation(), current);
            } // no else as it gets added in if-clause
        }

        drops.clear();
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Directing", "Modifier_Directing");
    }
    
    private static FileConfiguration getConfig() {
    	return ConfigurationManager.getConfig(Modifiers_Config.Directing);
    }

    @Override
    public boolean isAllowed() {
    	return getConfig().getBoolean("Directing.allowed");
    }
}

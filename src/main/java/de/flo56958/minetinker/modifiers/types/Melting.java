package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.events.MTEntityDamageByEntityEvent;
import de.flo56958.minetinker.events.MTEntityDamageEvent;
import de.flo56958.minetinker.Main;
import de.flo56958.minetinker.modifiers.Craftable;
import de.flo56958.minetinker.modifiers.Enchantable;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Melting extends Modifier implements Enchantable, Craftable, Listener {

    private double bonusMultiplier;
    private boolean cancelBurning;

    private static Melting instance;

    public static Melting instance() {
        synchronized (Melting.class) {
            if (instance == null) instance = new Melting();
        }
        return instance;
    }

    private Melting() {
        super(ModifierType.MELTING,
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.SWORD,
                                                ToolType.CHESTPLATE, ToolType.LEGGINGS)),
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
    	
    	String key = "Melting";
    	config.addDefault(key + ".allowed", true);
    	config.addDefault(key + ".name", key);
    	config.addDefault(key + ".name_modifier", "Enchanted Magma block");
        config.addDefault(key + ".modifier_item", "MAGMA_BLOCK"); //Needs to be a viable Material-Type
        config.addDefault(key + ".description", "Extra damage against burning enemies and less damage taken while on fire!");
        config.addDefault(key + ".description_modifier", "%WHITE%Modifier-Item for the Melting-Modifier");
        config.addDefault(key + ".Color", "%GOLD%");
        config.addDefault(key + ".MaxLevel", 3);
    	config.addDefault(key + ".EnchantCost", 10);
    	config.addDefault(key + ".BonusMultiplier", 0.1); //Percent of Bonus-damage per Level or Damage-reduction on Armor
        config.addDefault(key + ".CancelBurningOnArmor", true);
    	config.addDefault(key + ".Recipe.Enabled", false);
        
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

        this.bonusMultiplier = config.getDouble(key + ".BonusMultiplier");
        this.cancelBurning = config.getBoolean(key + ".CancelBurningOnArmor");
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        return checkAndAdd(p, tool, this, "melting", isCommand);
    }

    @Override
    public void removeMod(ItemStack tool) { }

    @EventHandler
    public void effect(MTEntityDamageByEntityEvent event) {
        if (event.isCancelled() || !this.isAllowed()) return;

        Player p = event.getPlayer();
        ItemStack tool = event.getTool();

        if (!p.hasPermission("minetinker.modifiers.melting.use")) return;
        if (!modManager.hasMod(tool, this)) return;

        if (event.getPlayer().equals(event.getEvent().getEntity())) {
            /*
            The melting effect if the Player gets damaged. getTool = Armor piece
             */
            int level = modManager.getModLevel(tool, this);
            if (p.getFireTicks() <= 0) return;

            if (p.getFireTicks() > 0 && cancelBurning) {
                p.setFireTicks(0);
            }

            double damage = event.getEvent().getDamage();
            damage = damage * (1 - this.bonusMultiplier * level);

            event.getEvent().setDamage(damage);

            ChatWriter.log(false, p.getDisplayName() + " triggered Melting on " + ItemGenerator.getDisplayName(tool) + ChatColor.GRAY + " (" + tool.getType().toString() + ")!");
        } else {
            /*
            The melting effect, if the Player is the Damager
             */
            if (event.getEvent().getEntity() instanceof LivingEntity) {
                LivingEntity e = (LivingEntity) event.getEvent().getEntity();

                if (e.isDead()) return;
                int level = modManager.getModLevel(tool, this);
                if (e.getFireTicks() == 0) return;

                double damage = event.getEvent().getDamage();
                damage = damage * (1 + this.bonusMultiplier * level);

                event.getEvent().setDamage(damage);

                ChatWriter.log(false, p.getDisplayName() + " triggered Melting on " + ItemGenerator.getDisplayName(tool) + ChatColor.GRAY + " (" + tool.getType().toString() + ")!");
            }
        }
    }

    @EventHandler
    public void effect(MTEntityDamageEvent event) {
        if (event.isCancelled() || !this.isAllowed()) return;

        Player p = event.getPlayer();
        ItemStack tool = event.getTool();

        if (!p.hasPermission("minetinker.modifiers.melting.use")) return;
        if (!modManager.hasMod(tool, this)) return;

        if (p.getFireTicks() > 0 && cancelBurning) {
            p.setFireTicks(0);
        }

        ChatWriter.log(false, p.getDisplayName() + " triggered Melting on " + ItemGenerator.getDisplayName(tool) + ChatColor.GRAY + " (" + tool.getType().toString() + ")!");
    }

    @Override
    public void enchantItem(Player p, ItemStack item) {
        if (!p.hasPermission("minetinker.modifiers.melting.craft")) return;
        _createModifierItem(getConfig(), p, this, "Melting");
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Melting", "Modifier_Melting");
    }
    
    private static FileConfiguration getConfig() {
    	return ConfigurationManager.getConfig(Modifiers_Config.Melting);
    }

    @Override
    public boolean isAllowed() {
    	return getConfig().getBoolean("Melting.allowed");
    }
}

package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.MTBlockBreakEvent;
import de.flo56958.MineTinker.Events.MTEntityDamageByEntityEvent;
import de.flo56958.MineTinker.Events.MTEntityDamageEvent;
import de.flo56958.MineTinker.Events.MTPlayerInteractEvent;
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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SelfRepair extends Modifier implements Enchantable, Listener {

    private int percentagePerLevel;
    private int healthRepair;

    private boolean useMending;

    private static SelfRepair instance;

    public static SelfRepair instance() {
        synchronized (SelfRepair.class) {
            if (instance == null) instance = new SelfRepair();
        }
        return instance;
    }

    private SelfRepair() {
        super("Self-Repair", "Self-Repair.yml",
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.CROSSBOW, ToolType.HOE, ToolType.PICKAXE, ToolType.SHEARS, ToolType.SHOVEL, ToolType.SWORD,
                                                ToolType.TRIDENT, ToolType.FISHINGROD,
                                                ToolType.HELMET, ToolType.CHESTPLATE, ToolType.LEGGINGS, ToolType.BOOTS, ToolType.ELYTRA)),
                Main.getPlugin());
        Bukkit.getPluginManager().registerEvents(this, Main.getPlugin());
    }

    @Override
    public List<Enchantment> getAppliedEnchantments() {
        List<Enchantment> enchantments = new ArrayList<>();
        enchantments.add(Enchantment.MENDING);

        return enchantments;
    }

    @Override
    public void reload() {
    	FileConfiguration config = getConfig();
    	config.options().copyDefaults(true);
    	
    	String key = "Self-Repair";
    	config.addDefault(key + ".allowed", true);
    	config.addDefault(key + ".name", key);
    	config.addDefault(key + ".name_modifier", "Enchanted mossy Cobblestone");
        config.addDefault(key + ".modifier_item", "MOSSY_COBBLESTONE"); //Needs to be a viable Material-Type
        config.addDefault(key + ".description", "Chance to repair the tool / armor while using it!");
        config.addDefault(key + ".description_modifier", "%WHITE%Modifier-Item for the Self-Repair-Modifier");
        config.addDefault(key + ".Color", "%GREEN%");
        config.addDefault(key + ".MaxLevel", 10);
    	config.addDefault(key + ".EnchantCost", 10);
    	config.addDefault(key + ".PercentagePerLevel", 10); //100% at Level 10 (not necessary for unbreakable tool in most cases)
    	config.addDefault(key + ".HealthRepair", 2); //How much durability should be repaired per trigger
        config.addDefault(key + ".UseMending", false); //Disables the plugins own system and instead uses the vanilla Mending enchantment
    	config.addDefault(key + ".Recipe.Enabled", false);
    	
    	ConfigurationManager.saveConfig(config);
        ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());
        
        init(config.getString(key + ".name"),
                "[" + config.getString(key + ".name_modifier") + "] \u200B" + config.getString(key + ".description"),
                ChatWriter.getColor(config.getString(key + ".Color")),
                config.getInt(key + ".MaxLevel"),
                modManager.createModifierItem(Material.getMaterial(config.getString(key + ".modifier_item")), ChatWriter.getColor(config.getString(key + ".Color")) + config.getString(key + ".name_modifier"), ChatWriter.addColors(config.getString(key + ".description_modifier")), this));
        
        this.percentagePerLevel = config.getInt(key + ".PercentagePerLevel");
        this.healthRepair = config.getInt(key + ".HealthRepair");
        this.useMending = config.getBoolean(key + ".UseMending");
    }

    @Override
    public boolean applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (!Modifier.checkAndAdd(p, tool, this, "selfrepair", isCommand)) return false;

        if (useMending) {
            ItemMeta meta = tool.getItemMeta();

            if (meta != null) {
                meta.addEnchant(Enchantment.MENDING, modManager.getModLevel(tool, this), true);

                if (Main.getPlugin().getConfig().getBoolean("HideEnchants")) {
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                } else {
                    meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
                }

                tool.setItemMeta(meta);
            }
        }
        return true;
    }

    @Override
    public void removeMod(ItemStack tool) {
        ItemMeta meta = tool.getItemMeta();

        if (meta != null) {
            meta.removeEnchant(Enchantment.MENDING);
            tool.setItemMeta(meta);
        }
    }

    //------------------------------------------------------

    @EventHandler
    public void effect(MTBlockBreakEvent event) {
        if (event.isCancelled() || !this.isAllowed()) return;
        effect(event.getPlayer(), event.getTool());
    }

    @EventHandler
    public void effect(MTEntityDamageByEntityEvent event) {
        if (event.isCancelled() || !this.isAllowed()) return;

        if (ToolType.BOOTS.getMaterials().contains(event.getTool().getType())
                || ToolType.LEGGINGS.getMaterials().contains(event.getTool().getType())
                || ToolType.CHESTPLATE.getMaterials().contains(event.getTool().getType())
                || ToolType.HELMET.getMaterials().contains(event.getTool().getType())) return; //Makes sure that armor does not get the double effect as it also gets the effect in EntityDamageEvent

        effect(event.getPlayer(), event.getTool());
    }

    @EventHandler
    public void effect(MTEntityDamageEvent event) {
        if (event.isCancelled() || !this.isAllowed()) return;
        effect(event.getPlayer(), event.getTool());
    }

    @EventHandler
    public void effect(MTPlayerInteractEvent event) {
        if (event.isCancelled() || !this.isAllowed()) return;
        effect(event.getPlayer(), event.getTool());
    }

    @EventHandler
    public void onShear(PlayerShearEntityEvent event) {
        if (event.isCancelled() || !this.isAllowed()) return;

        ItemStack tool = event.getPlayer().getInventory().getItemInMainHand();

        if (!(modManager.isToolViable(tool) && ToolType.SHEARS.getMaterials().contains(tool.getType()))) return;

        effect(event.getPlayer(), tool);
    }

    public void effectElytra(Player p, ItemStack elytra) {
        if (!this.isAllowed()) return;
        effect(p, elytra);
    }

    /**
     * The Effect that is used if Mending is disabled
     * @param p the Player
     * @param tool the Tool
     */
	private void effect(Player p, ItemStack tool) {
        if (useMending) return;
        if (!p.hasPermission("minetinker.modifiers.selfrepair.use")) return;
        if (!modManager.hasMod(tool, this)) return;

        int level = modManager.getModLevel(tool, this);
        Random rand = new Random();
        int n = rand.nextInt(100);

        if (n <= this.percentagePerLevel * level) {
            if (tool instanceof Damageable) {
                Damageable damageable = (Damageable) tool;
                short dura = (short) (damageable.getDamage() - this.healthRepair);

                if (dura < 0) {
                    dura = 0;
                }

                damageable.setDamage(dura);

                ChatWriter.log(false, p.getDisplayName() + " triggered Self-Repair on " + ItemGenerator.getDisplayName(tool) + ChatColor.GRAY + " (" + tool.getType().toString() + ")!");
            }
        }
    }

    //----------------------------------------------------------

    @Override
    public void enchantItem(Player p, ItemStack item) {
        if (!p.hasPermission("minetinker.modifiers.selfrepair.craft")) return;
        _createModifierItem(getConfig(), p, this, "Self-Repair");
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Self-Repair", "Modifier_SelfRepair");
    }

    @Override
    public boolean isAllowed() {
    	return getConfig().getBoolean("Self-Repair.allowed");
    }
}

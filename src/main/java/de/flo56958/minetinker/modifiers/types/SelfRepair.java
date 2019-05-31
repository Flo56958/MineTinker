package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.events.MTBlockBreakEvent;
import de.flo56958.minetinker.events.MTEntityDamageByEntityEvent;
import de.flo56958.minetinker.events.MTEntityDamageEvent;
import de.flo56958.minetinker.events.MTPlayerInteractEvent;
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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SelfRepair extends Modifier implements Enchantable, Craftable, Listener {

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
        super(ModifierType.SELF_REPAIR,
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.HOE, ToolType.PICKAXE, ToolType.SHEARS, ToolType.SHOVEL, ToolType.SWORD,
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

        this.percentagePerLevel = config.getInt(key + ".PercentagePerLevel");
        this.healthRepair = config.getInt(key + ".HealthRepair");
        this.useMending = config.getBoolean(key + ".UseMending");
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (Modifier.checkAndAdd(p, tool, this, "selfrepair", isCommand) == null) {
            return null;
        }

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
        return tool;
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
    //TODO: Implement with Damagable
    @SuppressWarnings("deprecation")
	private void effect(Player p, ItemStack tool) {
        if (useMending) return;
        if (!p.hasPermission("minetinker.modifiers.selfrepair.use")) return;
        if (!modManager.hasMod(tool, this)) return;

        int level = modManager.getModLevel(tool, this);
        Random rand = new Random();
        int n = rand.nextInt(100);

        if (n <= this.percentagePerLevel * level) {
            short dura = (short) (tool.getDurability() - this.healthRepair);

            if (dura < 0) dura = 0;

            tool.setDurability(dura);

            ChatWriter.log(false, p.getDisplayName() + " triggered Self-Repair on " + ItemGenerator.getDisplayName(tool) + ChatColor.GRAY + " (" + tool.getType().toString() + ")!");
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
    
    private static FileConfiguration getConfig() {
    	return ConfigurationManager.getConfig(Modifiers_Config.Self_Repair);
    }

    @Override
    public boolean isAllowed() {
    	return getConfig().getBoolean("Self-Repair.allowed");
    }
}
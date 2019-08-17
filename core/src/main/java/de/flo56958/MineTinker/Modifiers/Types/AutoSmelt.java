package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ModifierFailCause;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.MTBlockBreakEvent;
import de.flo56958.MineTinker.Events.ModifierFailEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

public class AutoSmelt extends Modifier implements Listener {

    private int percentagePerLevel;
    private boolean hasSound;
    private boolean hasParticles;
    private boolean worksUnderWater;
    private boolean smeltStone;
    private boolean smeltTerracotta;
    private boolean burnCoal;

    private static AutoSmelt instance;

    public static AutoSmelt instance() {
        synchronized (AutoSmelt.class) {
            if (instance == null) instance = new AutoSmelt();
        }
        return instance;
    }

    private AutoSmelt() {
        super("Auto-Smelt", "Auto-Smelt.yml",
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.PICKAXE, ToolType.SHOVEL, ToolType.SHEARS)),
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

        config.addDefault("allowed", true);
    	config.addDefault("Name", "Auto-Smelt");
    	config.addDefault("ModifierItemName", "Enhanced Furnace");
        config.addDefault("Description", "Chance to smelt ore when mined!");
        config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Auto-Smelt-Modifier");
    	config.addDefault("Color", "%YELLOW%");
    	config.addDefault("MaxLevel", 5);
    	config.addDefault("PercentagePerLevel", 20);
    	config.addDefault("Sound", true); //Auto-Smelt makes a sound
    	config.addDefault("Particles", true); //Auto-Smelt will create a particle effect when triggered
    	config.addDefault("SmeltStone", false);
    	config.addDefault("SmeltTerracotta", false);
    	config.addDefault("BurnCoal", true);
    	config.addDefault("WorksUnderWater", true);
        config.addDefault("OverrideLanguagesystem", false);

    	config.addDefault("Recipe.Enabled", true);
    	config.addDefault("Recipe.Top", "CCC");
    	config.addDefault("Recipe.Middle", "CFC");
    	config.addDefault("Recipe.Bottom", "CCC");

        Map<String, String> recipeMaterials = new HashMap<>();
        recipeMaterials.put("C", "FURNACE");
        recipeMaterials.put("F", "BLAZE_ROD");

        config.addDefault("Recipe.Materials", recipeMaterials);

    	ConfigurationManager.saveConfig(config);
        ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

    	init(Material.FURNACE, true);
        
        this.percentagePerLevel = config.getInt("PercentagePerLevel", 20);
        this.hasSound = config.getBoolean("Sound", true);
        this.hasParticles = config.getBoolean("Particles", true);
        this.worksUnderWater = config.getBoolean("WorksUnderWater", true);
        this.smeltStone = config.getBoolean("SmeltStone", false);
        this.burnCoal = config.getBoolean("BurnCoal", true);
        this.smeltTerracotta = config.getBoolean("SmeltTerracotta", false);
    }
    
    @Override
    public boolean applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (modManager.hasMod(tool, SilkTouch.instance())) {
            pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
            return false;
        }

        return Modifier.checkAndAdd(p, tool, this, "autosmelt", isCommand);
    }

    @Override
    public void removeMod(ItemStack tool) { }

    /**
     * The Effect for the BlockBreak-Listener
     * @param event the Event
     */
    @EventHandler
    public void effect(MTBlockBreakEvent event) {
        if (event.isCancelled() || !this.isAllowed()) return;

        Player p = event.getPlayer();
        ItemStack tool = event.getTool();
        Block b = event.getBlock();
        BlockBreakEvent e = event.getEvent();

    	//FileConfiguration config = getConfig();
    	
        if (!p.hasPermission("minetinker.modifiers.autosmelt.use")) return; //TODO: Think about more blocks for Auto-Smelt
        if (!modManager.hasMod(tool, this)) return;

        if (!worksUnderWater) {
            if (p.isSwimming() || p.getWorld().getBlockAt(p.getLocation()).getType().equals(Material.WATER)) return;
        }

        boolean allowLuck = false;
        int amount = 1;

        Material loot;

        //TODO: CHANGE TO CHECK CONFIG FOR WHAT OUTPUT A BLOCK HAS INSTEAD OF SWITCH CASE
        switch (b.getType()) {
            case STONE:
                if (!smeltStone) return;
            case COBBLESTONE:
                loot = Material.STONE;
                break;

            case SAND:
                loot = Material.GLASS;
                break;

            case SNOW:
            case SNOW_BLOCK:
                loot = Material.AIR;
                break;

            case RED_SAND:
                loot = Material.RED_STAINED_GLASS;
                break;

            case WHITE_TERRACOTTA:
                if (!smeltTerracotta) return;
                loot = Material.WHITE_GLAZED_TERRACOTTA;
                break;
            case ORANGE_TERRACOTTA:
                if (!smeltTerracotta) return;
                loot = Material.ORANGE_GLAZED_TERRACOTTA;
                break;
            case MAGENTA_TERRACOTTA:
                if (!smeltTerracotta) return;
                loot = Material.MAGENTA_GLAZED_TERRACOTTA;
                break;
            case LIGHT_BLUE_TERRACOTTA:
                if (!smeltTerracotta) return;
                loot = Material.LIGHT_BLUE_GLAZED_TERRACOTTA;
                break;
            case YELLOW_TERRACOTTA:
                if (!smeltTerracotta) return;
                loot = Material.YELLOW_GLAZED_TERRACOTTA;
                break;
            case LIME_TERRACOTTA:
                if (!smeltTerracotta) return;
                loot = Material.LIME_GLAZED_TERRACOTTA;
                break;
            case PINK_TERRACOTTA:
                if (!smeltTerracotta) return;
                loot = Material.PINK_GLAZED_TERRACOTTA;
                break;
            case GRAY_TERRACOTTA:
                if (!smeltTerracotta) return;
                loot = Material.GRAY_GLAZED_TERRACOTTA;
                break;
            case LIGHT_GRAY_TERRACOTTA:
                if (!smeltTerracotta) return;
                loot = Material.LIGHT_GRAY_GLAZED_TERRACOTTA;
                break;
            case CYAN_TERRACOTTA:
                if (!smeltTerracotta) return;
                loot = Material.CYAN_GLAZED_TERRACOTTA;
                break;
            case PURPLE_TERRACOTTA:
                if (!smeltTerracotta) return;
                loot = Material.PURPLE_GLAZED_TERRACOTTA;
                break;
            case BLUE_TERRACOTTA:
                if (!smeltTerracotta) return;
                loot = Material.BLUE_GLAZED_TERRACOTTA;
                break;
            case BROWN_TERRACOTTA:
                if (!smeltTerracotta) return;
                loot = Material.BROWN_GLAZED_TERRACOTTA;
                break;
            case GREEN_TERRACOTTA:
                if (!smeltTerracotta) return;
                loot = Material.GREEN_GLAZED_TERRACOTTA;
                break;
            case RED_TERRACOTTA:
                if (!smeltTerracotta) return;
                loot = Material.RED_GLAZED_TERRACOTTA;
                break;
            case BLACK_TERRACOTTA:
                if (!smeltTerracotta) return;
                loot = Material.BLACK_GLAZED_TERRACOTTA;
                break;

            case ACACIA_LOG:
            case BIRCH_LOG:
            case DARK_OAK_LOG:
            case JUNGLE_LOG:
            case OAK_LOG:
            case SPRUCE_LOG:

            case STRIPPED_ACACIA_LOG:
            case STRIPPED_BIRCH_LOG:
            case STRIPPED_DARK_OAK_LOG:
            case STRIPPED_JUNGLE_LOG:
            case STRIPPED_OAK_LOG:
            case STRIPPED_SPRUCE_LOG:

            case ACACIA_WOOD:
            case BIRCH_WOOD:
            case DARK_OAK_WOOD:
            case JUNGLE_WOOD:
            case OAK_WOOD:
            case SPRUCE_WOOD:

            case STRIPPED_ACACIA_WOOD:
            case STRIPPED_BIRCH_WOOD:
            case STRIPPED_DARK_OAK_WOOD:
            case STRIPPED_JUNGLE_WOOD:
            case STRIPPED_OAK_WOOD:
            case STRIPPED_SPRUCE_WOOD:
                allowLuck = true;
                loot = Material.CHARCOAL;
                break;

            case ACACIA_LEAVES:
            case BIRCH_LEAVES:
            case DARK_OAK_LEAVES:
            case JUNGLE_LEAVES:
            case OAK_LEAVES:
            case SPRUCE_LEAVES:
                loot = Material.STICK;
                break;

            case IRON_ORE:
                allowLuck = true;
                loot = Material.IRON_INGOT;
                break;

            case GOLD_ORE:
                allowLuck = true;
                loot = Material.GOLD_INGOT;
                break;

            case NETHERRACK:
                allowLuck = true;
                loot = Material.NETHER_BRICK;
                break;

            case KELP_PLANT:
                loot = Material.DRIED_KELP;
                break;

            case WET_SPONGE:
                loot = Material.SPONGE;
                break;

            case COAL_ORE:
            case COAL_BLOCK:
                if (!burnCoal) return;
                loot = Material.AIR;
                break;

            case CLAY:
                loot = Material.BRICK;
                amount = 4;
                break;

            default:
                return;
        }

        Random rand = new Random();
        int n = rand.nextInt(100);
        if (n <= this.percentagePerLevel * modManager.getModLevel(tool, this) && b.getLocation().getWorld() != null) {
            if (allowLuck) {
                int level = modManager.getModLevel(tool, Luck.instance());
                if (level > 0) {
                    amount = amount + rand.nextInt(level) * amount; //Times amount is for clay as it drops 4 per block
                }
            }

            if (!loot.equals(Material.AIR)) {
                ItemStack items = new ItemStack(loot, amount);
                b.getLocation().getWorld().dropItemNaturally(b.getLocation(), items);
            }
            e.setDropItems(false);

            if (this.hasParticles) { b.getLocation().getWorld().spawnParticle(Particle.FLAME, b.getLocation(), 5); }
            if (this.hasSound) { b.getLocation().getWorld().playSound(b.getLocation(), Sound.ENTITY_GENERIC_BURN, 0.2F, 0.5F); }

            ChatWriter.log(false, p.getDisplayName() + " triggered Auto-Smelt on " + ItemGenerator.getDisplayName(tool) + ChatColor.GRAY +
                    " (" + tool.getType().toString() + ") while mining " + e.getBlock().getType().toString() + "!");
        }
    }
}

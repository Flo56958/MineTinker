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
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AutoSmelt extends Modifier implements Listener {

    private EnumMap<Material, Material> conversions = new EnumMap<>(Material.class);
    private List<Material> luckMaterials = new ArrayList<>();

    private int percentagePerLevel;
    private boolean hasSound;
    private boolean hasParticles;
    private boolean worksUnderWater;

    private static AutoSmelt instance;

    public static AutoSmelt instance() {
        synchronized (AutoSmelt.class) {
            if (instance == null) {
                instance = new AutoSmelt();
            }
        }

        return instance;
    }

    @Override
    public String getKey() {
        return "Auto-Smelt";
    }

    @Override
    public List<ToolType> getAllowedTools() {
        return Arrays.asList(ToolType.AXE, ToolType.PICKAXE, ToolType.SHOVEL, ToolType.SHEARS);
    }

    private AutoSmelt() {
        super(Main.getPlugin());

        Bukkit.getPluginManager().registerEvents(this, Main.getPlugin());
    }

    @Override
    public void reload() {
    	FileConfiguration config = getConfig();
    	config.options().copyDefaults(true);

        String key = getKey();

        config.addDefault(key + ".allowed", true);
    	config.addDefault(key + ".name", key);
    	config.addDefault(key + ".name_modifier", "Enhanced Furnace");
        config.addDefault(key + ".modifier_item", "FURNACE"); //Needs to be a viable Material-Type
        config.addDefault(key + ".description", "Chance to smelt ore when mined!");
        config.addDefault(key + ".description_modifier", "%WHITE%Modifier-Item for the " + key + "-Modifier");
    	config.addDefault(key + ".Color", "%YELLOW%");
        config.addDefault(key + ".EnchantCost", 10);
    	config.addDefault(key + ".MaxLevel", 5);
    	config.addDefault(key + ".PercentagePerLevel", 20);
    	config.addDefault(key + ".Sound", true); //Auto-Smelt makes a sound
    	config.addDefault(key + ".Particles", true); //Auto-Smelt will create a particle effect when triggered
    	config.addDefault(key + ".works_under_water", true);

    	config.addDefault(key + ".Recipe.Enabled", true);
    	config.addDefault(key + ".Recipe.Top", "CCC");
    	config.addDefault(key + ".Recipe.Middle", "CFC");
    	config.addDefault(key + ".Recipe.Bottom", "CCC");

        Map<String, String> recipeMaterials = new HashMap<>();
        recipeMaterials.put("C", "FURNACE");
        recipeMaterials.put("F", "BLAZE_ROD");

        config.addDefault(key + ".Recipe.Materials", recipeMaterials);

        conversions.put(Material.STONE, Material.STONE);
        conversions.put(Material.COBBLESTONE, Material.STONE);
        conversions.put(Material.SAND, Material.GLASS);
        conversions.put(Material.SNOW, Material.AIR);
        conversions.put(Material.SNOW_BLOCK, Material.COBBLESTONE);
        conversions.put(Material.RED_SAND, Material.RED_STAINED_GLASS);
        conversions.put(Material.WHITE_TERRACOTTA, Material.WHITE_GLAZED_TERRACOTTA);
        conversions.put(Material.ORANGE_TERRACOTTA, Material.ORANGE_GLAZED_TERRACOTTA);
        conversions.put(Material.MAGENTA_TERRACOTTA, Material.MAGENTA_GLAZED_TERRACOTTA);
        conversions.put(Material.LIGHT_BLUE_TERRACOTTA, Material.LIGHT_BLUE_GLAZED_TERRACOTTA);
        conversions.put(Material.YELLOW_TERRACOTTA, Material.YELLOW_GLAZED_TERRACOTTA);
        conversions.put(Material.LIME_TERRACOTTA, Material.LIME_GLAZED_TERRACOTTA);
        conversions.put(Material.PINK_TERRACOTTA, Material.PINK_GLAZED_TERRACOTTA);
        conversions.put(Material.GRAY_TERRACOTTA, Material.GRAY_GLAZED_TERRACOTTA);
        conversions.put(Material.LIGHT_GRAY_TERRACOTTA, Material.LIGHT_GRAY_GLAZED_TERRACOTTA);
        conversions.put(Material.CYAN_TERRACOTTA, Material.CYAN_GLAZED_TERRACOTTA);
        conversions.put(Material.PURPLE_TERRACOTTA, Material.PURPLE_GLAZED_TERRACOTTA);
        conversions.put(Material.BLUE_TERRACOTTA, Material.BLUE_GLAZED_TERRACOTTA);
        conversions.put(Material.BROWN_TERRACOTTA, Material.BROWN_GLAZED_TERRACOTTA);
        conversions.put(Material.GREEN_TERRACOTTA, Material.GREEN_GLAZED_TERRACOTTA);
        conversions.put(Material.RED_TERRACOTTA, Material.RED_GLAZED_TERRACOTTA);
        conversions.put(Material.BLACK_TERRACOTTA, Material.BLACK_GLAZED_TERRACOTTA);
        conversions.put(Material.ACACIA_LOG, Material.CHARCOAL);
        conversions.put(Material.BIRCH_LOG, Material.CHARCOAL);
        conversions.put(Material.DARK_OAK_LOG, Material.CHARCOAL);
        conversions.put(Material.JUNGLE_LOG, Material.CHARCOAL);
        conversions.put(Material.OAK_LOG, Material.CHARCOAL);
        conversions.put(Material.SPRUCE_LOG, Material.CHARCOAL);
        conversions.put(Material.STRIPPED_ACACIA_LOG, Material.CHARCOAL);
        conversions.put(Material.STRIPPED_BIRCH_LOG, Material.CHARCOAL);
        conversions.put(Material.STRIPPED_DARK_OAK_LOG, Material.CHARCOAL);
        conversions.put(Material.STRIPPED_JUNGLE_LOG, Material.CHARCOAL);
        conversions.put(Material.STRIPPED_OAK_LOG, Material.CHARCOAL);
        conversions.put(Material.STRIPPED_SPRUCE_LOG, Material.CHARCOAL);
        conversions.put(Material.ACACIA_WOOD, Material.CHARCOAL);
        conversions.put(Material.BIRCH_WOOD, Material.CHARCOAL);
        conversions.put(Material.DARK_OAK_WOOD, Material.CHARCOAL);
        conversions.put(Material.JUNGLE_WOOD, Material.CHARCOAL);
        conversions.put(Material.OAK_WOOD, Material.CHARCOAL);
        conversions.put(Material.SPRUCE_WOOD, Material.CHARCOAL);
        conversions.put(Material.STRIPPED_ACACIA_WOOD, Material.CHARCOAL);
        conversions.put(Material.STRIPPED_BIRCH_WOOD, Material.CHARCOAL);
        conversions.put(Material.STRIPPED_DARK_OAK_WOOD, Material.CHARCOAL);
        conversions.put(Material.STRIPPED_JUNGLE_WOOD, Material.CHARCOAL);
        conversions.put(Material.STRIPPED_OAK_WOOD, Material.CHARCOAL);
        conversions.put(Material.STRIPPED_SPRUCE_WOOD, Material.CHARCOAL);
        conversions.put(Material.ACACIA_LEAVES, Material.STICK);
        conversions.put(Material.BIRCH_LEAVES, Material.STICK);
        conversions.put(Material.DARK_OAK_LEAVES, Material.STICK);
        conversions.put(Material.JUNGLE_LEAVES, Material.STICK);
        conversions.put(Material.OAK_LEAVES, Material.STICK);
        conversions.put(Material.SPRUCE_LEAVES, Material.STICK);

        conversions.put(Material.IRON_ORE, Material.IRON_INGOT);
        conversions.put(Material.GOLD_ORE, Material.GOLD_INGOT);
        conversions.put(Material.NETHERRACK, Material.NETHER_BRICK);
        conversions.put(Material.KELP_PLANT, Material.DRIED_KELP);
        conversions.put(Material.WET_SPONGE, Material.SPONGE);
        conversions.put(Material.COAL_ORE, Material.AIR);
        conversions.put(Material.COAL_BLOCK, Material.AIR);
        conversions.put(Material.CLAY, Material.BRICK);

        config.addDefault(key + ".Conversions", conversions);

        //conversions.clear();

        luckMaterials.add(Material.STRIPPED_ACACIA_WOOD);
        luckMaterials.add(Material.STRIPPED_BIRCH_WOOD);
        luckMaterials.add(Material.STRIPPED_DARK_OAK_WOOD);
        luckMaterials.add(Material.STRIPPED_JUNGLE_WOOD);
        luckMaterials.add(Material.STRIPPED_OAK_WOOD);
        luckMaterials.add(Material.IRON_ORE);
        luckMaterials.add(Material.GOLD_ORE);
        luckMaterials.add(Material.NETHERRACK);

        config.addDefault(key + ".AllowLuck", luckMaterials);

        //luckMaterials.clear();

    	ConfigurationManager.saveConfig(config);
        ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

    	init("[" + config.getString(key + ".name_modifier") + "] \u200B" + config.getString(key + ".description"),
                ChatWriter.getColor(config.getString(key + ".Color")), config.getInt(key + ".MaxLevel"),
                modManager.createModifierItem(Material.getMaterial(config.getString(key + ".modifier_item")),
                ChatWriter.getColor(config.getString(key + ".Color")) + config.getString(key + ".name_modifier"),
                ChatWriter.addColors(config.getString(key + ".description_modifier")), this));
        
        this.percentagePerLevel = config.getInt(key + ".PercentagePerLevel");
        this.hasSound = config.getBoolean(key + ".Sound");
        this.hasParticles = config.getBoolean(key + ".Particles");
        this.worksUnderWater = config.getBoolean(key + ".works_under_water");
//
//        ConfigurationSection conversionConfig = config.getConfigurationSection(key + ".Conversions");
//
//        for (String sectionKey : conversionConfig.getKeys(false)) {
//            conversions.put(Material.valueOf(sectionKey), Material.valueOf(conversionConfig.getString(sectionKey)));
//        }
//
//        for (String luckEntry : config.getStringList(key + ".AllowLuck")) {
//            luckMaterials.add(Material.valueOf(luckEntry));
//        }
    }
    
    @Override
    public boolean applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (modManager.hasMod(tool, SilkTouch.instance())) {
            pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
            return false;
        }

        return Modifier.checkAndAdd(p, tool, this, "autosmelt", isCommand);
    }

    /**
     * The Effect for the BlockBreak-Listener
     * @param event the Event
     */
    @EventHandler(ignoreCancelled = true)
    public void effect(MTBlockBreakEvent event) {
        if (!this.isAllowed()) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack tool = event.getTool();
        Block block = event.getBlock();
        BlockBreakEvent breakEvent = event.getEvent();

    	//FileConfiguration config = getConfig();
    	
        if (!player.hasPermission("minetinker.modifiers.autosmelt.use")) {
            return; //TODO: Think about more blocks for Auto-Smelt
        }

        if (!modManager.hasMod(tool, this)) {
            return;
        }

        if (!worksUnderWater) {
            if (player.isSwimming() || player.getWorld().getBlockAt(player.getLocation()).getType() == Material.WATER) {
                return;
            }
        }

        boolean allowLuck = luckMaterials.contains(block.getType());
        int amount = 1;

        Material loot = conversions.get(block.getType());

        if (loot == null) {
            return;
        }

        Random rand = new Random();
        int n = rand.nextInt(100);

        if (n <= this.percentagePerLevel * modManager.getModLevel(tool, this) && block.getLocation().getWorld() != null) {
            if (allowLuck) {
                int level = modManager.getModLevel(tool, Luck.instance());

                if (level > 0) {
                    amount = amount + rand.nextInt(level) * amount; //Times amount is for clay as it drops 4 per block
                }
            }

            if (loot != Material.AIR) {
                ItemStack items = new ItemStack(loot, amount);
                block.getLocation().getWorld().dropItemNaturally(block.getLocation(), items);
            }

            breakEvent.setDropItems(false);

            if (this.hasParticles) {
                block.getLocation().getWorld().spawnParticle(Particle.FLAME, block.getLocation(), 5);
            }

            if (this.hasSound) {
                block.getLocation().getWorld().playSound(block.getLocation(), Sound.ENTITY_GENERIC_BURN, 0.2F, 0.5F);
            }

            ChatWriter.log(false, player.getDisplayName() + " triggered Auto-Smelt on " + ItemGenerator.getDisplayName(tool) + ChatColor.GRAY +
                    " (" + tool.getType().toString() + ") while mining " + breakEvent.getBlock().getType().toString() + "!");
        }
    }
}

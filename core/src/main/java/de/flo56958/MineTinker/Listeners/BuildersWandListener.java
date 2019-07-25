package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import de.flo56958.MineTinker.Utilities.PlayerInfo;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Slab;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class BuildersWandListener implements Listener {

    private static final ModManager modManager;
    private static FileConfiguration config;
    private static final ArrayList<ItemStack> wands = new ArrayList<>();

    //TODO: MAKE CONFIG-RELOADABLE
    static {
    	modManager = ModManager.instance();
    	config = ConfigurationManager.getConfig("BuildersWand.yml");
    	config.options().copyDefaults(true);
    	
    	String key = "BuildersWand";
    	config.addDefault(key + ".enabled", true);
    	config.addDefault(key + ".description", "%WHITE%MineTinker-Builderswand");
    	config.addDefault(key + ".useDurability", true);
    	config.addDefault(key + ".name_wood", "Wooden Builderswand");
    	config.addDefault(key + ".name_stone", "Stone Builderswand");
    	config.addDefault(key + ".name_iron", "Iron Builderswand");
    	config.addDefault(key + ".name_gold", "Golden Builderswand");
    	config.addDefault(key + ".name_diamond", "Diamond Builderswand");
    	
    	List<String> list = new ArrayList<>();
    	list.add("bannedExample1");
    	list.add("bannedExample2");
    	config.addDefault(key + ".BannedWorlds", list); //#Worlds where MineTinker-Builderswands can't be used
    	
    	String recipe = key + ".Recipes.Wood";
    	config.addDefault(recipe + ".Top", "  W");
    	config.addDefault(recipe + ".Middle", " S ");
    	config.addDefault(recipe + ".Bottom", "S  ");
    	config.addDefault(recipe + ".Materials.S", "STICK");
    	config.addDefault(recipe + ".Materials.W", "LEGACY_WOOD");
    	
    	recipe = key + ".Recipes.Stone";
    	config.addDefault(recipe + ".Top", "  C");
    	config.addDefault(recipe + ".Middle", " S ");
    	config.addDefault(recipe + ".Bottom", "S  ");
    	config.addDefault(recipe + ".Materials.C", "COBBLESTONE");
    	config.addDefault(recipe + ".Materials.S", "STICK");

    	recipe = key + ".Recipes.Iron";
    	config.addDefault(recipe + ".Top", "  I");
    	config.addDefault(recipe + ".Middle", " S ");
    	config.addDefault(recipe + ".Bottom", "S  ");
    	config.addDefault(recipe + ".Materials.I", "IRON_INGOT");
    	config.addDefault(recipe + ".Materials.S", "STICK");
    	
    	recipe = key + ".Recipes.Gold";
    	config.addDefault(recipe + ".Top", "  G");
    	config.addDefault(recipe + ".Middle", " S ");
    	config.addDefault(recipe + ".Bottom", "S  ");
    	config.addDefault(recipe + ".Materials.G", "GOLD_INGOT");
    	config.addDefault(recipe + ".Materials.S", "STICK");
    	
    	recipe = key + ".Recipes.Diamond";
    	config.addDefault(recipe + ".Top", "  D");
    	config.addDefault(recipe + ".Middle", " S ");
    	config.addDefault(recipe + ".Bottom", "S  ");
    	config.addDefault(recipe + ".Materials.D", "DIAMOND");
    	config.addDefault(recipe + ".Materials.S", "STICK");
    	
    	ConfigurationManager.saveConfig(config);
    }

    @SuppressWarnings("EmptyMethod")
    public static void init() {/*class must be called once*/}

    public static void reload() {
        config = ConfigurationManager.getConfig("BuildersWand.yml");

        wands.clear();
        wands.add(buildersWandCreator(Material.WOODEN_SHOVEL, config.getString("BuildersWand.name_wood")));
        wands.add(buildersWandCreator(Material.STONE_SHOVEL, config.getString("BuildersWand.name_stone")));
        wands.add(buildersWandCreator(Material.IRON_SHOVEL, config.getString("BuildersWand.name_iron")));
        wands.add(buildersWandCreator(Material.GOLDEN_SHOVEL, config.getString("BuildersWand.name_gold")));
        wands.add(buildersWandCreator(Material.DIAMOND_SHOVEL, config.getString("BuildersWand.name_diamond")));

        registerBuildersWands();
    }

    private static ItemStack buildersWandCreator(Material m, String name) { //TODO: Modify to implement Modifiers
        ItemStack wand = new ItemStack(m, 1);
        ItemMeta meta = wand.getItemMeta();

        if (meta != null) {
            ArrayList<String> lore = new ArrayList<>();

            lore.add(ChatWriter.addColors(config.getString("BuildersWand.description")));
            meta.setLore(lore);

            meta.setDisplayName(ChatWriter.addColors(name));
            meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
            wand.setItemMeta(meta);
        }

        modManager.getNBTHandler().setStringList(wand, "CanDestroy", "minecraft:air");
        modManager.getNBTHandler().setInt(wand, "IdentifierBuilderswand", 0);

        return wand;
    }

    /**
     * tries to register the Builderswand recipes
     */
    private static void registerBuildersWands() {
        try {
            NamespacedKey nkey = new NamespacedKey(Main.getPlugin(), "Builderswand_Wood");
            ShapedRecipe newRecipe = new ShapedRecipe(nkey, wands.get(0)); //init recipe
            String top = config.getString("BuildersWand.Recipes.Wood.Top");
            String middle = config.getString("BuildersWand.Recipes.Wood.Middle");
            String bottom = config.getString("BuildersWand.Recipes.Wood.Bottom");
            ConfigurationSection materials = config.getConfigurationSection("BuildersWand.Recipes.Wood.Materials");

            // TODO: Make safe
            newRecipe.shape(top, middle, bottom); //makes recipe

            for (String key : materials.getKeys(false)){
                newRecipe.setIngredient(key.charAt(0), Material.getMaterial(materials.getString(key)));
            }

            Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
            ModManager.instance().recipe_Namespaces.add(nkey);
        } catch (Exception e) {
            ChatWriter.logError("Could not register recipe for the Wooden Builderswand!"); //executes if the recipe could not initialize
        }
        try {
            NamespacedKey nkey = new NamespacedKey(Main.getPlugin(), "Builderswand_Stone");
            ShapedRecipe newRecipe = new ShapedRecipe(nkey, wands.get(1)); //init recipe
            String top = config.getString("BuildersWand.Recipes.Stone.Top");
            String middle = config.getString("BuildersWand.Recipes.Stone.Middle");
            String bottom = config.getString("BuildersWand.Recipes.Stone.Bottom");
            ConfigurationSection materials = config.getConfigurationSection("BuildersWand.Recipes.Stone.Materials");

            // TODO: Make safe
            newRecipe.shape(top, middle, bottom); //makes recipe

            for (String key : materials.getKeys(false)) {
                newRecipe.setIngredient(key.charAt(0), Material.getMaterial(materials.getString(key)));
            }

            Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
            ModManager.instance().recipe_Namespaces.add(nkey);
        } catch (Exception e) {
            ChatWriter.logError("Could not register recipe for the Stone Builderswand!"); //executes if the recipe could not initialize
        }
        try {
            NamespacedKey nkey = new NamespacedKey(Main.getPlugin(), "Builderswand_Iron");
            ShapedRecipe newRecipe = new ShapedRecipe(nkey, wands.get(2)); //init recipe
            String top = config.getString("BuildersWand.Recipes.Iron.Top");
            String middle = config.getString("BuildersWand.Recipes.Iron.Middle");
            String bottom = config.getString("BuildersWand.Recipes.Iron.Bottom");
            ConfigurationSection materials = config.getConfigurationSection("BuildersWand.Recipes.Iron.Materials");

            newRecipe.shape(top, middle, bottom); //makes recipe

            for (String key : materials.getKeys(false)) {
                newRecipe.setIngredient(key.charAt(0), Material.getMaterial(materials.getString(key)));
            }

            Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
            ModManager.instance().recipe_Namespaces.add(nkey);
        } catch (Exception e) {
            ChatWriter.logError("Could not register recipe for the Iron Builderswand!"); //executes if the recipe could not initialize
        }
        try {
            NamespacedKey nkey = new NamespacedKey(Main.getPlugin(), "Builderswand_Gold");
            ShapedRecipe newRecipe = new ShapedRecipe(nkey, wands.get(3)); //init recipe
            String top = config.getString("BuildersWand.Recipes.Gold.Top");
            String middle = config.getString("BuildersWand.Recipes.Gold.Middle");
            String bottom = config.getString("BuildersWand.Recipes.Gold.Bottom");
            ConfigurationSection materials = config.getConfigurationSection("BuildersWand.Recipes.Gold.Materials");

            // TODO: Make safe
            newRecipe.shape(top, middle, bottom); //makes recipe

            for (String key : materials.getKeys(false)) {
                newRecipe.setIngredient(key.charAt(0), Material.getMaterial(materials.getString(key)));
            }

            Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
            ModManager.instance().recipe_Namespaces.add(nkey);
        } catch (Exception e) {
            ChatWriter.logError("Could not register recipe for the Golden Builderswand!"); //executes if the recipe could not initialize
        }
        try {
            NamespacedKey nkey = new NamespacedKey(Main.getPlugin(), "Builderswand_Diamond");
            ShapedRecipe newRecipe = new ShapedRecipe(nkey, wands.get(4)); //init recipe
            String top = config.getString("BuildersWand.Recipes.Diamond.Top");
            String middle = config.getString("BuildersWand.Recipes.Diamond.Middle");
            String bottom = config.getString("BuildersWand.Recipes.Diamond.Bottom");
            ConfigurationSection materials = config.getConfigurationSection("BuildersWand.Recipes.Diamond.Materials");

            newRecipe.shape(top, middle, bottom); //makes recipe

            for (String key : materials.getKeys(false)) {
                newRecipe.setIngredient(key.charAt(0), Material.getMaterial(materials.getString(key)));
            }

            Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
        } catch (Exception e) {
            ChatWriter.logError("Could not register recipe for the Diamond Builderswand!"); //executes if the recipe could not initialize
        }
    }

    @SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true)
    public void onClick (PlayerInteractEvent e) {
        if (Lists.WORLDS_BUILDERSWANDS.contains(e.getPlayer().getWorld().getName())) {
            return;
        }

        ItemStack wand = e.getPlayer().getInventory().getItemInMainHand();

        if (!modManager.isWandViable(wand)) {
            return;
        }

        e.setCancelled(true);

        if (!e.getPlayer().hasPermission("minetinker.builderswands.use")) {
            return;
        }

        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        int _u = 0;
        int _w = 0;

        Player p = e.getPlayer();

        if (!p.isSneaking()) {
            switch (wand.getType()) {  //TODO: custom Builderswand sizes
                case STONE_SHOVEL:
                    _w = 1;
                    break;
                case IRON_SHOVEL:
                    _u = 1;
                    _w = 1;
                    break;
                case GOLDEN_SHOVEL:
                    _u = 1;
                    _w = 2;
                    break;
                case DIAMOND_SHOVEL:
                    _u = 2;
                    _w = 2;
                    break;
            }
        }

        Block b = e.getClickedBlock();
        BlockFace bf = e.getBlockFace();
        ItemStack[] inv = p.getInventory().getContents();

        Vector u = new Vector(0, 0, 0);
        Vector v = new Vector(0, 0, 0);
        Vector w = new Vector(0, 0, 0);

        if (bf.equals(BlockFace.UP) || bf.equals(BlockFace.DOWN)) {
            if (bf.equals(BlockFace.UP)) {
                v = new Vector(0, 1, 0);
            } else {
                v = new Vector(0, -1, 0);
            }

            switch (PlayerInfo.getFacingDirection(p)) {
                case "N":
                    w = new Vector(-1, 0, 0);
                    break;
                case "E":
                    w = new Vector(0, 0, -1);
                    break;
                case "S":
                    w = new Vector(1, 0, 0);
                    break;
                case "W":
                    w = new Vector(0, 0, 1);
                    break;
            }

            u = v.getCrossProduct(w);
        } else if (bf.equals(BlockFace.NORTH)) {
            v = new Vector(0, 0, -1);
            w = new Vector(-1, 0, 0);
            u = new Vector(0, -1, 0);
        } else if (bf.equals(BlockFace.EAST)) {
            v = new Vector(1, 0, 0);
            w = new Vector(0, 0, -1);
            u = new Vector(0, 1, 0);
        } else if (bf.equals(BlockFace.SOUTH)) {
            v = new Vector(0, 0, 1);
            w = new Vector(1, 0, 0);
            u = new Vector(0, 1, 0);
        } else if (bf.equals(BlockFace.WEST)) {
            v = new Vector(-1, 0, 0);
            w = new Vector(0, 0, 1);
            u = new Vector(0, -1, 0);
        }
        if ((p.getGameMode().equals(GameMode.SURVIVAL) || p.getGameMode().equals(GameMode.ADVENTURE)) && b != null) {
            for (ItemStack current : inv) {
                if (current == null) {
                    continue;
                }

                if (!current.getType().equals(b.getType())) {
                    continue;
                }

                if (current.hasItemMeta()) {
                    continue;
                }

                loop:
                for (int i = -_w; i <= _w; i++) {
                    for (int j = -_u; j <= _u; j++) {
                        Location l = b.getLocation().clone();

                        l.subtract(w.clone().multiply(i));
                        l.subtract(u.clone().multiply(j));

                        Location loc = l.clone().subtract(v.clone().multiply(-1));

                        if (wand.getType().getMaxDurability() - wand.getDurability() <= 1) {
                            break loop;
                        }

                        if (!placeBlock(b, p, l, loc, current, v)) {
                            continue;
                        }

                        int amountPlaced = 1;
                        BlockData behindData = b.getWorld().getBlockAt(loc.subtract(v)).getBlockData();

                        if (behindData instanceof Slab && ((Slab) behindData).getType().equals(Slab.Type.DOUBLE)) {
                            amountPlaced = 2; //Special case for slabs as you place two slabs at once
                        }

                        current.setAmount(current.getAmount() - amountPlaced);

                        if (config.getBoolean("BuildersWand.useDurability")) {
                            //TODO: Add Modifiers to the Builderwand (Self-Repair, Reinforced, XP)
                            wand.setDurability((short) (wand.getDurability() + 1));
                        }

                        if (current.getAmount() == 0) {
                            //TODO: Add Exp gain for Builderswands
                            break loop;
                        }

                        e.setCancelled(true);
                    }
                }

                break;
            }
        } else if (p.getGameMode().equals(GameMode.CREATIVE) && b != null) {
            for (int i = -_w; i <= _w; i++) {
                for (int j = -_u; j <= _u; j++) {
                    Location l = b.getLocation().clone();

                    l.subtract(w.clone().multiply(i));
                    l.subtract(u.clone().multiply(j));

                    Location loc = l.clone().subtract(v.clone().multiply(-1));

                    placeBlock(b, p, l, loc, new ItemStack(b.getType(), 64), v);
                }
            }
        }
    }

    private boolean placeBlock(Block b, Player p, Location l, Location loc, ItemStack item, Vector v) {
        if (!b.getWorld().getBlockAt(l).getType().equals(b.getType())) {
            return false;
        }

        if (!(b.getWorld().getBlockAt(loc).getType().equals(Material.AIR) ||
                b.getWorld().getBlockAt(loc).getType().equals(Material.CAVE_AIR) ||
                b.getWorld().getBlockAt(loc).getType().equals(Material.WATER) ||
                b.getWorld().getBlockAt(loc).getType().equals(Material.BUBBLE_COLUMN) ||
                b.getWorld().getBlockAt(loc).getType().equals(Material.LAVA) ||
                b.getWorld().getBlockAt(loc).getType().equals(Material.GRASS))) {

            return false;
        }

        //triggers a pseudoevent to find out if the Player can build
        Block block = b.getWorld().getBlockAt(loc);

        BlockPlaceEvent placeEvent = new BlockPlaceEvent(block, block.getState(), b, item, p, true, EquipmentSlot.HAND);
        Bukkit.getPluginManager().callEvent(placeEvent);

        //check the pseudoevent
        if (!placeEvent.canBuild() || placeEvent.isCancelled()) {
            return false;
        }

        Block nb = b.getWorld().getBlockAt(loc);
        Block behind = nb.getWorld().getBlockAt(loc.clone().subtract(v));
        if (behind.getBlockData() instanceof Slab) {
            if (((Slab) behind.getBlockData()).getType().equals(Slab.Type.DOUBLE)) {
                if (item.getAmount() - 2 < 0) {
                    return false;
                }
            }
        }

        nb.setType(item.getType());
        BlockData bd = nb.getBlockData();

        if (bd instanceof Directional) {
            ((Directional) bd).setFacing(((Directional) behind.getBlockData()).getFacing());
        }

        if (bd instanceof Slab) {
            ((Slab) bd).setType(((Slab) behind.getBlockData()).getType());
        }

        nb.setBlockData(bd);

        return true;
    }

    public static ArrayList<ItemStack> getWands() {
        return wands;
    }
}

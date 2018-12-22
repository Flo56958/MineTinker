package de.flo56958.MineTinker.Listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

//import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import de.flo56958.MineTinker.Utilities.PlayerInfo;

public class BuildersWandListener implements Listener {

    private static final ModManager modManager;
    private static final FileConfiguration config;
    
    static {
    	modManager = ModManager.instance();
    	config = ConfigurationManager.getConfig("BuildersWand.yml");
    	config.options().copyDefaults(true);
    	
    	String key = "BuildersWand";
    	config.addDefault(key + ".enabled", true);
    	config.addDefault(key + ".useDurability", true);
    	config.addDefault(key + ".name_wood", "Wooden Builderswand");
    	config.addDefault(key + ".name_stone", "Stone Builderswand");
    	config.addDefault(key + ".name_iron", "Iron Builderswand");
    	config.addDefault(key + ".name_gold", "Golden Builderswand");
    	config.addDefault(key + ".name_diamond", "Diamond Builderswand");
    	
    	List<String> list = new ArrayList<String>();
    	list.add("bannedExample1");
    	list.add("bannedExample2");
    	config.addDefault(key + ".BannedWorlds", list); //#Worlds where MineTinker-Builderswands can't be used
    	
    	String recipe = key + ".Recipe.Wood";
    	config.addDefault(recipe + ".Top", "  W");
    	config.addDefault(recipe + ".Middle", " S ");
    	config.addDefault(recipe + ".Bottom", "S  ");
    	config.addDefault(recipe + ".Materials.S", "STICK");
    	config.addDefault(recipe + ".Materials.W", "LEGACY_WOOD");
    	
    	recipe = key + ".Recipe.Stone";
    	config.addDefault(recipe + ".Top", "  C");
    	config.addDefault(recipe + ".Middle", " S ");
    	config.addDefault(recipe + ".Bottom", "S  ");
    	config.addDefault(recipe + ".Materials.C", "COBBLESTONE");
    	config.addDefault(recipe + ".Materials.S", "STICK");

    	recipe = key + ".Recipe.Iron";
    	config.addDefault(recipe + ".Top", "  I");
    	config.addDefault(recipe + ".Middle", " S ");
    	config.addDefault(recipe + ".Bottom", "S  ");
    	config.addDefault(recipe + ".Materials.I", "IRON_INGOT");
    	config.addDefault(recipe + ".Materials.S", "STICK");
    	
    	recipe = key + ".Recipe.Gold";
    	config.addDefault(recipe + ".Top", "  G");
    	config.addDefault(recipe + ".Middle", " S ");
    	config.addDefault(recipe + ".Bottom", "S  ");
    	config.addDefault(recipe + ".Materials.G", "GOLD_INGOT");
    	config.addDefault(recipe + ".Materials.S", "STICK");
    	
    	recipe = key + ".Recipe.Diamond";
    	config.addDefault(recipe + ".Top", "  D");
    	config.addDefault(recipe + ".Middle", " S ");
    	config.addDefault(recipe + ".Bottom", "S  ");
    	config.addDefault(recipe + ".Materials.D", "DIAMOND");
    	config.addDefault(recipe + ".Materials.S", "STICK");
    	
    	ConfigurationManager.saveConfig(config);
    }
    
    @EventHandler
    public void onBlockBreak (BlockBreakEvent e) {
        if (e.isCancelled()) { return; }
        if (Lists.WORLDS_BUILDERSWANDS.contains(e.getPlayer().getWorld().getName())) { return; }

        ItemStack wand = e.getPlayer().getInventory().getItemInMainHand();

        if (!modManager.isWandViable(wand)) { return; }

        e.setCancelled(true);
    }

    @SuppressWarnings("deprecation")
	@EventHandler
    public void onClick (PlayerInteractEvent e) {
        if (e.isCancelled()) { return; }
        if (Lists.WORLDS_BUILDERSWANDS.contains(e.getPlayer().getWorld().getName())) { return; }

        ItemStack wand = e.getPlayer().getInventory().getItemInMainHand();

        if (!modManager.isWandViable(wand)) { return; }

        e.setCancelled(true);

        if (!e.getPlayer().hasPermission("minetinker.builderswands.use")) { return; }
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) { return; }

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
                default:
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
        for (ItemStack current : inv) {
            if (current != null) {
                if (current.getType().equals(b.getType())) {
                    if (!current.hasItemMeta()) {
                        loop:
                        for (int i = -_w; i <= _w; i++) {
                            for (int j = -_u; j <= _u; j++) {
                                Location l = b.getLocation().clone();
                                l.subtract(w.clone().multiply(i));
                                l.subtract(u.clone().multiply(j));
                                Location loc = l.clone().subtract(v.clone().multiply(-1));
                                if (b.getWorld().getBlockAt(l).getType().equals(b.getType())) {
                                    if (b.getWorld().getBlockAt(loc).getType().equals(Material.AIR) ||
                                            b.getWorld().getBlockAt(loc).getType().equals(Material.CAVE_AIR) ||
                                            b.getWorld().getBlockAt(loc).getType().equals(Material.WATER) ||
                                            b.getWorld().getBlockAt(loc).getType().equals(Material.BUBBLE_COLUMN) ||
                                            b.getWorld().getBlockAt(loc).getType().equals(Material.LAVA) ||
                                            b.getWorld().getBlockAt(loc).getType().equals(Material.GRASS)) {
                                        if (wand.getType().getMaxDurability() - wand.getDurability() <= 1) {
                                            break loop;
                                        }

                                        /*boolean canBuild = true;
                                        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
                                        // WorldGuard may not be loaded
                                        if (plugin instanceof WorldGuardPlugin) {
                                            WorldGuardPlugin wg = (WorldGuardPlugin) plugin;

                                            canBuild = ((WorldGuardPlugin) plugin).canBuild(p, b.getWorld().getBlockAt(loc));
                                        }
                                        if (canBuild) { */

                                        b.getWorld().getBlockAt(loc).setType(current.getType());

                                        //} else { continue; }

                                        current.setAmount(current.getAmount() - 1);
                                        if (config.getBoolean("BuildersWand.useDurability")) { //TODO: Add Modifiers to the Builderwand (Self-Repair, Reinforced, XP)
                                            wand.setDurability((short) (wand.getDurability() + 1));
                                        }
                                        if (current.getAmount() == 0) { //TODO: Add Exp gain for Builderswands
                                            break loop;
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    }
                }
            }
        }
    }
}

package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Data.ModifierFailCause;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.MTBlockBreakEvent;
import de.flo56958.MineTinker.Events.MTPlayerInteractEvent;
import de.flo56958.MineTinker.Events.ModifierFailEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Craftable;
import de.flo56958.MineTinker.Modifiers.Enchantable;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.*;
import net.minecraft.server.v1_13_R2.BlockPosition;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Power extends Modifier implements Enchantable, Craftable, Listener {

    public static final HashMap<Player, Boolean> HASPOWER = new HashMap<>();

    private boolean lv1_vertical;

    private static Power instance;

    public static Power instance() {
        if (instance == null) instance = new Power();
        return instance;
    }

    private Power() {
        super(ModifierType.POWER,
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.HOE, ToolType.PICKAXE, ToolType.SHOVEL, ToolType.SHEARS)),
                Main.getPlugin());
        Bukkit.getPluginManager().registerEvents(this, Main.getPlugin());
    }

    @Override
    public void reload() {
    	FileConfiguration config = getConfig();
    	config.options().copyDefaults(true);
    	
    	String key = "Power";
    	config.addDefault(key + ".allowed", true);
    	config.addDefault(key + ".name", key);
    	config.addDefault(key + ".name_modifier", "Enchanted Emerald");
    	config.addDefault(key + ".description", "Tool can destroy more blocks per swing!");
        config.addDefault(key + ".description_modifier", "%WHITE%Modifier-Item for the Power-Modifier");
        config.addDefault(key + ".Color", "%GREEN%");
        config.addDefault(key + ".lv1_vertical", false); //Should the 3x1 at level 1 be horizontal (false) or vertical (true)
    	config.addDefault(key + ".MaxLevel", 3); //Algorithm for area of effect (except for level 1): (level * 2) - 1 x (level * 2) - 1
    	config.addDefault(key + ".EnchantCost", 10);
    	config.addDefault(key + ".Recipe.Enabled", false);
        
    	ConfigurationManager.saveConfig(config);
    	
        init(config.getString(key + ".name"),
                "[" + config.getString("Power.name_modifier") + "] " + config.getString(key + ".description"),
                ChatWriter.getColor(config.getString(key + ".Color")),
                config.getInt(key + ".MaxLevel"),
                modManager.createModifierItem(Material.EMERALD, ChatWriter.getColor(config.getString(key + ".Color")) + config.getString(key + ".name_modifier"), ChatWriter.addColors(config.getString(key + ".description_modifier")), this));
        
        this.lv1_vertical = config.getBoolean("Power.lv1_vertical");
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (modManager.get(ModifierType.TIMBER) != null) {
            if (modManager.hasMod(tool, modManager.get(ModifierType.TIMBER))) {
                pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
                return null;
            }
        }

        return Modifier.checkAndAdd(p, tool, this, "power", isCommand);
    }

    @Override
    public void removeMod(ItemStack tool) { }

    private boolean checkPower(Player p, ItemStack tool) {
        if (!p.hasPermission("minetinker.modifiers.power.use")) { return false; }
        if (HASPOWER.get(p)) { return false; }
        if (p.isSneaking()) { return false; }

        return modManager.hasMod(tool, this);
    }

    /**
     * The effect when a Block was brocken
     * @param event The Event
     */
    @EventHandler
    public void effect(MTBlockBreakEvent event) {
        if (event.isCancelled() || !this.isAllowed()) { return; }
        Player p = event.getPlayer();
        ItemStack tool = event.getTool();
        Block b = event.getBlock();
        if (!checkPower(p, tool)) { return; }
        if (ToolType.HOE.getMaterials().contains(tool.getType())) { return; }

        ChatWriter.log(false, p.getDisplayName() + " triggered Power on " + ItemGenerator.getDisplayName(tool) + ChatColor.GRAY + " (" + tool.getType().toString() + ")!");

        HASPOWER.replace(p, true); //for the power-triggered BlockBreakEvents (prevents endless "recursion")

        int level = modManager.getModLevel(tool, this);

        if (level == 1) {
            if (lv1_vertical) {
                if (Lists.BLOCKFACE.get(p).equals(BlockFace.DOWN) || Lists.BLOCKFACE.get(p).equals(BlockFace.UP)) {
                    if (PlayerInfo.getFacingDirection(p).equals("N") || PlayerInfo.getFacingDirection(p).equals("S")) {
                        Block b1 = b.getWorld().getBlockAt(b.getLocation().add(0, 0, 1));
                        Block b2 = b.getWorld().getBlockAt(b.getLocation().add(0, 0, -1));
                        powerBlockBreak(b1, (CraftPlayer) p);
                        powerBlockBreak(b2, (CraftPlayer) p);
                    } else if (PlayerInfo.getFacingDirection(p).equals("W") || PlayerInfo.getFacingDirection(p).equals("E")) {
                        Block b1 = b.getWorld().getBlockAt(b.getLocation().add(1, 0, 0));
                        Block b2 = b.getWorld().getBlockAt(b.getLocation().add(-1, 0, 0));
                        powerBlockBreak(b1, (CraftPlayer) p);
                        powerBlockBreak(b2, (CraftPlayer) p);
                    }
                } else {
                    Block b1 = b.getWorld().getBlockAt(b.getLocation().add(0, 1, 0));
                    Block b2 = b.getWorld().getBlockAt(b.getLocation().add(0, -1, 0));
                    powerBlockBreak(b1, (CraftPlayer) p);
                    powerBlockBreak(b2, (CraftPlayer) p);
                }
            } else if (Lists.BLOCKFACE.get(p).equals(BlockFace.DOWN) || Lists.BLOCKFACE.get(p).equals(BlockFace.UP)) {
                if (PlayerInfo.getFacingDirection(p).equals("N") || PlayerInfo.getFacingDirection(p).equals("S")) {
                    Block b1 = b.getWorld().getBlockAt(b.getLocation().add(1, 0, 0));
                    Block b2 = b.getWorld().getBlockAt(b.getLocation().add(-1, 0, 0));
                    powerBlockBreak(b1, (CraftPlayer) p);
                    powerBlockBreak(b2, (CraftPlayer) p);
                } else if (PlayerInfo.getFacingDirection(p).equals("W") || PlayerInfo.getFacingDirection(p).equals("E")) {
                    Block b1 = b.getWorld().getBlockAt(b.getLocation().add(0, 0, 1));
                    Block b2 = b.getWorld().getBlockAt(b.getLocation().add(0, 0, -1));
                    powerBlockBreak(b1, (CraftPlayer) p);
                    powerBlockBreak(b2, (CraftPlayer) p);
                }
            } else if (Lists.BLOCKFACE.get(p).equals(BlockFace.NORTH) || Lists.BLOCKFACE.get(p).equals(BlockFace.SOUTH)) {
                Block b1 = b.getWorld().getBlockAt(b.getLocation().add(1, 0, 0));
                Block b2 = b.getWorld().getBlockAt(b.getLocation().add(-1, 0, 0));
                powerBlockBreak(b1, (CraftPlayer) p);
                powerBlockBreak(b2, (CraftPlayer) p);
            } else if (Lists.BLOCKFACE.get(p).equals(BlockFace.WEST) || Lists.BLOCKFACE.get(p).equals(BlockFace.EAST)) {
                Block b1 = b.getWorld().getBlockAt(b.getLocation().add(0, 0, 1));
                Block b2 = b.getWorld().getBlockAt(b.getLocation().add(0, 0, -1));
                powerBlockBreak(b1, (CraftPlayer) p);
                powerBlockBreak(b2, (CraftPlayer) p);
            }
        } else {
            HASPOWER.replace(p, true);
            if (Lists.BLOCKFACE.get(p).equals(BlockFace.DOWN) || Lists.BLOCKFACE.get(p).equals(BlockFace.UP)) {
                for (int x = -(level - 1); x <= (level - 1); x++) {
                    for (int z = -(level - 1); z <= (level - 1); z++) {
                        if (!(x == 0 && z == 0)) {
                            Block b1 = b.getWorld().getBlockAt(b.getLocation().add(x, 0, z));
                            powerBlockBreak(b1, (CraftPlayer) p);
                        }
                    }
                }
            } else if (Lists.BLOCKFACE.get(p).equals(BlockFace.NORTH) || Lists.BLOCKFACE.get(p).equals(BlockFace.SOUTH)) {
                for (int x = -(level - 1); x <= (level - 1); x++) {
                    for (int y = -(level - 1); y <= (level - 1); y++) {
                        if (!(x == 0 && y == 0)) {
                            Block b1 = b.getWorld().getBlockAt(b.getLocation().add(x, y, 0));
                            powerBlockBreak(b1, (CraftPlayer) p);
                        }
                    }
                }
            } else if (Lists.BLOCKFACE.get(p).equals(BlockFace.EAST) || Lists.BLOCKFACE.get(p).equals(BlockFace.WEST)) {
                for (int z = -(level - 1); z <= (level - 1); z++) {
                    for (int y = -(level - 1); y <= (level - 1); y++) {
                        if (!(z == 0 && y == 0)) {
                            Block b1 = b.getWorld().getBlockAt(b.getLocation().add(0, y, z));
                            powerBlockBreak(b1, (CraftPlayer) p);
                        }
                    }
                }
            }
        }

        HASPOWER.replace(p, false); //so the effect of power is not disabled for the Player
    }

    /**
     * Effect for the PlayerInteractEvent for the Hoe
     */
    @EventHandler
    public void effect(MTPlayerInteractEvent event) {
        if (!event.isCancelled() || !this.isAllowed()) { return; }

        Player p = event.getPlayer();
        ItemStack tool = event.getTool();

        if (!ToolType.HOE.getMaterials().contains(tool.getType())) { return; }

        PlayerInteractEvent e = event.getEvent();
        if (!checkPower(p, tool)) { return; }

        ChatWriter.log(false, p.getDisplayName() + " triggered Power on " + ItemGenerator.getDisplayName(tool) + ChatColor.GRAY + " (" + tool.getType().toString() + ")!");

        HASPOWER.replace(p, true);

        int level = modManager.getModLevel(tool, this);

        Block b = e.getClickedBlock();

        if (level == 1) {
            if (Lists.BLOCKFACE.get(p).equals(BlockFace.DOWN) || Lists.BLOCKFACE.get(p).equals(BlockFace.UP)) {
                Block b1;
                Block b2;
                
                FileConfiguration config = getConfig();
                
                if (PlayerInfo.getFacingDirection(p).equals("N") || PlayerInfo.getFacingDirection(p).equals("S")) {
                    if (this.lv1_vertical) {
                        b1 = b.getWorld().getBlockAt(b.getLocation().add(0, 0, 1));
                        b2 = b.getWorld().getBlockAt(b.getLocation().add(0, 0, -1));
                    } else {
                        b1 = b.getWorld().getBlockAt(b.getLocation().add(1, 0, 0));
                        b2 = b.getWorld().getBlockAt(b.getLocation().add(-1, 0, 0));
                    }
                } else if (PlayerInfo.getFacingDirection(p).equals("W") || PlayerInfo.getFacingDirection(p).equals("E")) {
                    if (this.lv1_vertical) {
                        b1 = b.getWorld().getBlockAt(b.getLocation().add(1, 0, 0));
                        b2 = b.getWorld().getBlockAt(b.getLocation().add(-1, 0, 0));
                    } else {
                        b1 = b.getWorld().getBlockAt(b.getLocation().add(0, 0, 1));
                        b2 = b.getWorld().getBlockAt(b.getLocation().add(0, 0, -1));
                    }
                } else {
                    b1 = b;
                    b2 = b;
                }
                powerCreateFarmland(p, tool, b1);
                powerCreateFarmland(p, tool, b2);
            }
        } else {
            if (Lists.BLOCKFACE.get(p).equals(BlockFace.DOWN) || Lists.BLOCKFACE.get(p).equals(BlockFace.UP)) {
                for (int x = -(level - 1); x <= (level - 1); x++) {
                    for (int z = -(level - 1); z <= (level - 1); z++) {
                        if (!(x == 0 && z == 0)) {
                            Block b_ = p.getWorld().getBlockAt(b.getLocation().add(x, 0, z));
                            powerCreateFarmland(p, tool, b_);
                        }
                    }
                }
            }
        }

        HASPOWER.replace(p, false);
    }

    private void powerBlockBreak(Block b, CraftPlayer p) {
        if (!b.getType().equals(Material.AIR) && !b.getType().equals(Material.CAVE_AIR) && !b.getType().equals(Material.BEDROCK)
                && !b.getType().equals(Material.WATER) && !b.getType().equals(Material.BUBBLE_COLUMN) && !b.getType().equals(Material.LAVA)
                && !b.getType().equals(Material.END_PORTAL) && !b.getType().equals(Material.END_CRYSTAL) && !b.getType().equals(Material.END_PORTAL_FRAME)
                && !b.getType().equals(Material.NETHER_PORTAL)) {
            p.getHandle().playerInteractManager.breakBlock(new BlockPosition(b.getX(), b.getY(), b.getZ()));
        }
    }

    @SuppressWarnings("deprecation")
	private static void powerCreateFarmland(Player p, ItemStack tool, Block b) {
        if (b.getType().equals(Material.GRASS_BLOCK) || b.getType().equals(Material.DIRT)) {
            if (b.getWorld().getBlockAt(b.getLocation().add(0, 1, 0)).getType().equals(Material.AIR)) {
                tool.setDurability((short) (tool.getDurability() + 1));
                Bukkit.getPluginManager().callEvent(new PlayerInteractEvent(p, Action.RIGHT_CLICK_BLOCK, tool, b, BlockFace.UP));
                b.setType(Material.FARMLAND); //Event only does Plugin event (no vanilla conversion to Farmland and Tool-Damage)
            }
        }
    }

    @Override
    public void enchantItem(Player p, ItemStack item) {
        if (!p.hasPermission("minetinker.modifiers.power.craft")) { return; }
        _createModifierItem(getConfig(), p, this, "Power");
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Power", "Modifier_Power");
    }
    
    private static FileConfiguration getConfig() {
    	return ConfigurationManager.getConfig(Modifiers_Config.Power);
    }

    @Override
    public boolean isAllowed() {
    	return getConfig().getBoolean("Power.allowed");
    }
}

package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

public class Soulbound extends Modifier implements Listener {

    private final HashMap<Player, ArrayList<ItemStack>> storedItemStacks = new HashMap<>(); //saves ItemStacks untill reload (if the player does not respawn instantly)
    private boolean toolDropable;
    private boolean decrementModLevelOnUse;
    private int percentagePerLevel;

    private static Soulbound instance;

    public static Soulbound instance() {
        synchronized (Soulbound.class) {
            if (instance == null) instance = new Soulbound();
        }
        return instance;
    }

    private Soulbound() {
        super("Soulbound", "Soulbound.yml",
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.CROSSBOW, ToolType.HOE, ToolType.PICKAXE, ToolType.SHEARS, ToolType.SHOVEL,
                                                ToolType.SWORD, ToolType.TRIDENT, ToolType.FISHINGROD,
                                                ToolType.HELMET, ToolType.CHESTPLATE, ToolType.LEGGINGS, ToolType.BOOTS, ToolType.ELYTRA)),
                Main.getPlugin());
        Bukkit.getPluginManager().registerEvents(this, Main.getPlugin());
    }

    @Override
    public List<Enchantment> getAppliedEnchantments() {
        return new ArrayList<>();
    }

    @Override
    public boolean applyMod(Player p, ItemStack tool, boolean isCommand) {
        return checkAndAdd(p, tool, this, "soulbound", isCommand);
    }

    @Override
    public void removeMod(ItemStack tool) { }

    @Override
    public void reload() {
        FileConfiguration config = getConfig();
        config.options().copyDefaults(true);

        config.addDefault("Allowed", true);
        config.addDefault("Name", "Soulbound");
        config.addDefault("ModifierItemName", "Powerinfused Beacon");
        config.addDefault("Description", "Do not lose the tool when dying.");
        config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Soulbound-Modifier");
        config.addDefault("Color", "%GRAY%");
        config.addDefault("MaxLevel", 1);
        config.addDefault("PercentagePerLevel", 100);
        config.addDefault("DecrementModLevelOnUse", false);
        config.addDefault("ToolDropable", true);

        config.addDefault("Recipe.Enabled", true);
        config.addDefault("Recipe.Top", "BLB");
        config.addDefault("Recipe.Middle", "LNL");
        config.addDefault("Recipe.Bottom", "BLB");

        Map<String, String> recipeMaterials = new HashMap<>();
        recipeMaterials.put("B", "BLAZE_ROD");
        recipeMaterials.put("L", "LAVA_BUCKET");
        recipeMaterials.put("N", "NETHER_STAR");

        config.addDefault("Recipe.Materials", recipeMaterials);

        ConfigurationManager.saveConfig(config);
        ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

        init(Material.BEACON, true);

        this.toolDropable = config.getBoolean("ToolDropable", true);
        this.decrementModLevelOnUse = config.getBoolean("DecrementModLevelOnUse", false);
        this.percentagePerLevel = config.getInt("PercentagePerLevel", 100);
    }

    public boolean getDropable(ItemStack is) {
        if (!modManager.hasMod(is, this)) { return true; }
        return toolDropable;
    }

    /**
     * Effect when a player dies
     * @param p the Player
     * @param is the ItemStack to keep
     * @return true if soulbound has success
     */
    public boolean effect(Player p, ItemStack is) {
        if (!p.hasPermission("minetinker.modifiers.soulbound.use")) { return false; }
        if (!modManager.hasMod(is, this)) { return false; }

        Random rand = new Random();
        if (rand.nextInt(100) > modManager.getModLevel(is, this) * percentagePerLevel) { return false; }

        storedItemStacks.computeIfAbsent(p, k -> new ArrayList<>());

        ArrayList<ItemStack> stored = storedItemStacks.get(p);

        ChatWriter.log(false, p.getDisplayName() + " triggered Soulbound on " + ItemGenerator.getDisplayName(is) + ChatColor.GRAY + " (" + is.getType().toString() + ")!");
        if (stored.contains(is)) { return true; }

        if (decrementModLevelOnUse) {
            int newLevel = modManager.getModLevel(is, this) - 1;
            if (newLevel == 0) { modManager.removeMod(is, this); }
            else { modManager.getNBTHandler().setInt(is, this.getNBTKey(), modManager.getModLevel(is, this) - 1); }
        }

        stored.add(is.clone());
        return true;
    }

    /**
     * Effect if a player respawns
     */
    @EventHandler
    public void effect(PlayerRespawnEvent e) {
        Player p = e.getPlayer();

        if (!p.hasPermission("minetinker.modifiers.soulbound.use")) return;
        if (!storedItemStacks.containsKey(p)) return;

        ArrayList<ItemStack> stored = storedItemStacks.get(p);

        for (ItemStack is : stored) {
            if (p.getInventory().addItem(is).size() != 0) { //adds items to (full) inventory
                p.getWorld().dropItem(p.getLocation(), is);
            } // no else as it gets added in if
        }

        storedItemStacks.remove(p);
    }

    /**
     * Effect if a player drops an item
     * @param e
     */
    @EventHandler(ignoreCancelled = true)
    public void effect(PlayerDropItemEvent e) {
        Item item = e.getItemDrop();
        ItemStack is = item.getItemStack();

        if (!(modManager.isArmorViable(is) || modManager.isToolViable(is) || modManager.isWandViable(is))) return;
        if (!modManager.hasMod(is, this)) return;
        if (toolDropable) return;

        e.setCancelled(true);
    }
}

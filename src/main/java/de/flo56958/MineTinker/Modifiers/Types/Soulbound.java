package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Craftable;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import de.flo56958.MineTinker.Utilities.Modifiers_Config;
import net.minecraft.server.v1_14_R1.NBTTagInt;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class Soulbound extends Modifier implements Craftable, Listener {

    private final HashMap<Player, ArrayList<ItemStack>> storedItemStacks = new HashMap<>(); //saves ItemStacks untill reload (if the player does not respawn instantly)
    private boolean toolDropable;
    private boolean decrementModLevelOnUse;
    private int percentagePerLevel;

    private static Soulbound instance;

    public static Soulbound instance() {
        if (instance == null) instance = new Soulbound();
        return instance;
    }

    private Soulbound() {
        super(ModifierType.SOULBOUND,
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.HOE, ToolType.PICKAXE, ToolType.SHEARS, ToolType.SHOVEL,
                                                ToolType.SWORD, ToolType.TRIDENT, ToolType.FISHINGROD,
                                                ToolType.HELMET, ToolType.CHESTPLATE, ToolType.LEGGINGS, ToolType.BOOTS, ToolType.ELYTRA)),
                Main.getPlugin());
        Bukkit.getPluginManager().registerEvents(this, Main.getPlugin());
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Soulbound", "Modifier_Soulbound");
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        return checkAndAdd(p, tool, this, "soulbound", isCommand);
    }

    @Override
    public void removeMod(ItemStack tool) { }

    @Override
    public void reload() {
        FileConfiguration config = getConfig();
        config.options().copyDefaults(true);

        String key = "Soulbound";
        config.addDefault(key + ".allowed", true);
        config.addDefault(key + ".name", key);
        config.addDefault(key + ".name_modifier", "Powerinfused Beacon");
        config.addDefault(key + ".modifier_item", "BEACON"); //Needs to be a viable Material-Type
        config.addDefault(key + ".description", "Do not lose the tool when dying.");
        config.addDefault(key + ".description_modifier", "%WHITE%Modifier-Item for the Soulbound-Modifier");
        config.addDefault(key + ".Color", "%GRAY%");
        config.addDefault(key + ".MaxLevel", 1);
        config.addDefault(key + ".PercentagePerLevel", 100);
        config.addDefault(key + ".DecrementModLevelOnUse", false);
        config.addDefault(key + ".ToolDropable", true);
        config.addDefault(key + ".Recipe.Enabled", true);
        config.addDefault(key + ".Recipe.Top", "BLB");
        config.addDefault(key + ".Recipe.Middle", "LNL");
        config.addDefault(key + ".Recipe.Bottom", "BLB");
        config.addDefault(key + ".Recipe.Materials.B", "BLAZE_ROD");
        config.addDefault(key + ".Recipe.Materials.L", "LAVA_BUCKET");
        config.addDefault(key + ".Recipe.Materials.N", "NETHER_STAR");

        ConfigurationManager.saveConfig(config);

        init(config.getString(key + ".name"),
                "[" + config.getString(key + ".name_modifier") + "] " + config.getString(key + ".description"),
                ChatWriter.getColor(config.getString(key + ".Color")),
                getConfig().getInt(key + ".MaxLevel"),
                modManager.createModifierItem(Material.getMaterial(config.getString(key + ".modifier_item")), ChatWriter.getColor(config.getString(key + ".Color")) + config.getString(key + ".name_modifier"), ChatWriter.addColors(config.getString(key + ".description_modifier")), this));

        this.toolDropable = config.getBoolean(key + ".ToolDropable");
        this.decrementModLevelOnUse = config.getBoolean(key + ".DecrementModLevelOnUse");
        this.percentagePerLevel = config.getInt(key + ".PercentagePerLevel");
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
            else { modManager.setNBTTag(is, this.getType().getNBTKey(), new NBTTagInt(modManager.getModLevel(is, this) - 1)); }
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
        if (!p.hasPermission("minetinker.modifiers.soulbound.use")) { return; }

        if (!storedItemStacks.containsKey(p)) { return; }

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
    @EventHandler
    public void effect(PlayerDropItemEvent e) {
        if (e.isCancelled()) { return; }

        Item item = e.getItemDrop();
        ItemStack is = item.getItemStack();
        if (!(modManager.isArmorViable(is) || modManager.isToolViable(is) || modManager.isWandViable(is))) { return; }

        if (!modManager.hasMod(is, this)) { return; }
        if (toolDropable) { return; }

        e.setCancelled(true);
    }

    private static FileConfiguration getConfig() {
        return ConfigurationManager.getConfig(Modifiers_Config.Soulbound);
    }

    @Override
    public boolean isAllowed() {
        return getConfig().getBoolean("Soulbound.allowed");
    }
}

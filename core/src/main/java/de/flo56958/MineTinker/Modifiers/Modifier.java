package de.flo56958.MineTinker.Modifiers;

import de.flo56958.MineTinker.Data.ModifierFailCause;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.ModifierApplyEvent;
import de.flo56958.MineTinker.Events.ModifierFailEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Types.ExtraModifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Modifier {
    protected static final ModManager modManager = ModManager.instance();
    protected static final PluginManager pluginManager = Bukkit.getPluginManager();

    private String description;
    private ChatColor color;
    private int maxLvl;
    private ItemStack modItem;
    private final Plugin source;

    public abstract String getKey();

    public abstract List<ToolType> getAllowedTools();

    public String getRecipeKey() {
        StringBuilder key = new StringBuilder("Modifier_");

        for (String segment : getKey().toLowerCase().replace("'", "").split("-")) {
            key.append(segment.substring(0, 1).toUpperCase()).append(segment.substring(1));
        }

        return key.toString();
    }

    public String getDescription() {
        return description;
    }

    public ChatColor getColor() {
        return color;
    }

    public int getMaxLvl() {
        return maxLvl;
    }

    public ItemStack getModItem() {
        return modItem;
    }

    public boolean hasRecipe() {
        return true;
    }

    private final String fileName;

    Plugin getSource() {
        return source;
    } //for other Plugins/Addons that register Modifiers

    public String getName() {
        return getConfig().getString(getKey() + ".name");
    }

    /**
     * Class constructor
     * @param source The Plugin that registered the Modifier
     */
    protected Modifier(Plugin source) {
        this.fileName = getKey().replace("'", "") + ".yml";
        this.source = source;
    }

    /**
     * changes the core settings of the Modifier (like a secondary constructor)
     * @param description Description of the Modifier
     * @param color Color of the Modifier
     * @param maxLvl Maximum Level cap of the Modifier
     * @param modItem ItemStack that is required to craft the Modifier
     */
    protected void init(String description, ChatColor color, int maxLvl, ItemStack modItem) {
        this.description = ChatWriter.addColors(description);
        this.color = color;
        this.maxLvl = maxLvl;
        this.modItem = modItem;
    }

    /**
     * applies the Modifier to the tool
     * @param p the Player
     * @param tool the Tool to modify
     * @return  true if successful
     *          false if failure
     */
    public abstract boolean applyMod(Player p, ItemStack tool, boolean isCommand);

    /**
     * what should be done to the Tool if the Modifier gets removed
     * @param tool the Tool
     */
    public void removeMod(ItemStack tool) {
        ItemMeta meta = tool.getItemMeta();

        if (meta != null) {
            for (Enchantment enchantment : getAppliedEnchantments()) {
                meta.removeEnchant(enchantment);
            }

            for (Attribute attribute : getAppliedAttributes()) {
                meta.removeAttributeModifier(attribute);
            }

            tool.setItemMeta(meta);
        }
    }

    /**
     * reloads the settings of the Modifier
     */
    public abstract void reload();

    /**
     * @return is the modifier allowed
     */
    public boolean isAllowed() {
        return getConfig().getBoolean(getKey() + ".allowed");
    }

    public String getFileName() {
        return fileName;
    }

    /**
     * @return a list of enchantments that may be applied when the modifier is applied
     */
    public List<Enchantment> getAppliedEnchantments() {
        return Collections.emptyList();
    }

    /**
     * @return a list of attributes that may be applied when the modifier is applied
     */
    public List<Attribute> getAppliedAttributes() {
        return Collections.emptyList();
    }

    public static boolean checkAndAdd(Player p, ItemStack tool, Modifier mod, String permission, boolean isCommand) {
        if ((modManager.getFreeSlots(tool) < 1 && !mod.equals(ExtraModifier.instance())) && !isCommand) {
            pluginManager.callEvent(new ModifierFailEvent(p, tool, mod, ModifierFailCause.NO_FREE_SLOTS, isCommand));
            return false;
        }

        if (!p.hasPermission("minetinker.modifiers." + permission + ".apply")) {
            pluginManager.callEvent(new ModifierFailEvent(p, tool, mod, ModifierFailCause.NO_PERMISSION, isCommand));
            return false;
        }

        if (!mod.getAllowedTools().contains(ToolType.get(tool.getType()))) {
            pluginManager.callEvent(new ModifierFailEvent(p, tool, mod, ModifierFailCause.INVALID_TOOLTYPE, isCommand));
            return false;
        }

        if (modManager.getModLevel(tool, mod) >= mod.getMaxLvl()) {
            pluginManager.callEvent(new ModifierFailEvent(p, tool, mod, ModifierFailCause.MOD_MAXLEVEL, isCommand));
            return false;
        }

        modManager.addMod(tool, mod);

        int freeSlots = modManager.getFreeSlots(tool);

        if (!isCommand) {
            modManager.setFreeSlots(tool, --freeSlots);
        } else {
            ModifierApplyEvent event = new ModifierApplyEvent(p, tool, mod, freeSlots, true);
            Bukkit.getPluginManager().callEvent(event);
        }

        return true;
    }

    protected FileConfiguration getConfig() {
        return ConfigurationManager.getConfig(this.getFileName());
    }

    protected void registerCraftingRecipe() {
        if (!hasRecipe()) {
            return;
        }

        Modifier mod = this;
        FileConfiguration config = getConfig();
        String keyName = getRecipeKey();
        String name = getKey().replace("'", "");

        if (config.getBoolean(name + ".Recipe.Enabled")) {
            try {
                NamespacedKey nkey = new NamespacedKey(Main.getPlugin(), keyName);
                ShapedRecipe newRecipe = new ShapedRecipe(nkey, mod.getModItem()); //reload recipe
                String top = config.getString(name + ".Recipe.Top");
                String middle = config.getString(name + ".Recipe.Middle");
                String bottom = config.getString(name + ".Recipe.Bottom");
                ConfigurationSection materials = config.getConfigurationSection(name + ".Recipe.Materials");

                newRecipe.shape(top, middle, bottom); //makes recipe

                if (materials != null) {
                    for (String key : materials.getKeys(false)) {
                        String materialName = materials.getString(key);

                        if (materialName == null) {
                            ChatWriter.logInfo("Material entry not found! Aborting recipe registration for this modifier.");
                            return;
                        }

                        Material material = Material.getMaterial(materialName);

                        if (material == null) {
                            ChatWriter.log(false, "Material [" + materialName + "] is null for mod [" + getName() + "]");
                            return;
                        } else {
                            newRecipe.setIngredient(key.charAt(0), material);
                        }
                    }
                } else {
                    ChatWriter.logError("Could not register recipe for the " + name + "-Modifier!"); //executes if the recipe could not initialize
                    ChatWriter.logError("Cause: Malformed recipe config.");

                    return;
                }

                Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
                ChatWriter.log(false, "Registered recipe for the " + name + "-Modifier!");
                ModManager.instance().recipe_Namespaces.add(nkey);
            } catch (Exception e) {
                ChatWriter.logError("Could not register recipe for the " + name + "-Modifier!"); //executes if the recipe could not initialize
                e.printStackTrace();
            }
        }
    }

    // ---------------------- Enchantable Stuff ----------------------

    public void enchantItem(Player p, ItemStack item) {
        if (!p.hasPermission("minetinker.modifiers." + getName().replace("-", "").toLowerCase() + ".craft")) {
            return;
        }

        _createModifierItem(getConfig(), p, this, getName());
    }

    public void _createModifierItem(FileConfiguration config, Player p, Modifier mod, String modifier) {
        if (config.getBoolean(modifier + ".Recipe.Enabled")) {
            return;
        }

        Location location = p.getLocation();
        World world = location.getWorld();
        PlayerInventory inventory = p.getInventory();

        if (world == null) {
            return;
        }

        if (p.getGameMode() == GameMode.CREATIVE) {
            world.dropItemNaturally(location, mod.getModItem());

            if (Main.getPlugin().getConfig().getBoolean("Sound.OnEnchanting")) {
                p.playSound(location, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 0.5F);
            }

            ChatWriter.log(false, p.getDisplayName() + " created a " + mod.getName() + "-Modifiers in Creative!");
        } else if (p.getLevel() >= config.getInt(modifier + ".EnchantCost")) {
            int amount = inventory.getItemInMainHand().getAmount();
            int newLevel = p.getLevel() - config.getInt(modifier + ".EnchantCost");

            p.setLevel(newLevel);
            inventory.getItemInMainHand().setAmount(amount - 1);

            if (inventory.addItem(mod.getModItem()).size() != 0) { //adds items to (full) inventory
                world.dropItem(location, mod.getModItem());
            } // no else as it gets added in if

            if (Main.getPlugin().getConfig().getBoolean("Sound.OnEnchanting")) {
                p.playSound(location, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 0.5F);
            }

            ChatWriter.log(false, p.getDisplayName() + " created a " + mod.getName() + "-Modifiers!");
        } else {
            ChatWriter.sendActionBar(p, ChatColor.RED + "" + config.getInt(modifier + ".EnchantCost") + " levels required!");
            ChatWriter.log(false, p.getDisplayName() + " tried to create a " + mod.getName() + "-Modifiers but had not enough levels!");
        }

    }
}

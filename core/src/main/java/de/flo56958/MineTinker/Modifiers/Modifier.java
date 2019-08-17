package de.flo56958.MineTinker.Modifiers;

import de.flo56958.MineTinker.Data.ModifierFailCause;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.ModifierApplyEvent;
import de.flo56958.MineTinker.Events.ModifierFailEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Types.ExtraModifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import de.flo56958.MineTinker.Utilities.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.List;

public abstract class Modifier {
    protected static final ModManager modManager = ModManager.instance();
    protected static final PluginManager pluginManager = Bukkit.getPluginManager();

    private String name;
    private String description;
    private ChatColor color;
    private int maxLvl;
    private ItemStack modItem;
    private final ArrayList<ToolType> allowedTools;
    private final Plugin source;

    private final ArrayList<Attribute> emptyArrayList = new ArrayList<>();

    public String getName() {
        return name;
    }

    public String getDescription() { return description; }

    public ChatColor getColor() {
        return color;
    }

    public int getMaxLvl() { return maxLvl; }

    public ItemStack getModItem() {
        return modItem;
    }

    protected ArrayList<ToolType> getAllowedTools(){
        return allowedTools;
    }

    private final String nbtTag;
    private final String fileName;

    Plugin getSource() { return source; } //for other Plugins/Addons that register Modifiers

    /**
     * Class constructor
     * @param allowedTools Lists of ToolTypes where the Modifier is allowed on
     * @param source The Plugin that registered the Modifier
     */
    protected Modifier(String nbtTag, String fileName, ArrayList<ToolType> allowedTools, Plugin source) {
        this.nbtTag = nbtTag;
        this.fileName = fileName;
        this.allowedTools = allowedTools;
        this.source = source;
    }

    /**
     * changes the core settings of the Modifier (like a secondary constructor)
     */
    protected void init(Material m, boolean customItem) {
        FileConfiguration config = getConfig();
        this.color = ChatWriter.getColor(config.getString("Color", "%WHITE%"));
        this.maxLvl = config.getInt("MaxLevel");
        if (config.getBoolean("OverrideLanguagesytem", false)) { //use the config values instead
            this.name = config.getString("Name", "");
            this.description = ChatWriter.addColors(config.getString("Description", ""));
            if (customItem) {
                this.modItem = modManager.createModifierItem(m, this.color + config.getString("ModifierItemName", ""),
                        ChatWriter.addColors(config.getString("DescriptionModifierItem", "")), this);
            } else {
                this.modItem = new ItemStack(m, 1);
            }
        } else { //normal Languagesystem-Integration
            String langStart = "Modifier." + getNBTKey();
            this.name = LanguageManager.getString(langStart + ".Name");
            this.description = LanguageManager.getString(langStart + ".Description");
            if (customItem) {
                this.modItem = modManager.createModifierItem(m, this.color + LanguageManager.getString(langStart + ".ModifierItemName"),
                        ChatColor.WHITE + LanguageManager.getString(langStart + ".DescriptionModifierItem"), this);
            } else {
                this.modItem = new ItemStack(m, 1);
            }
        }
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
    public abstract void removeMod(ItemStack tool);

    /**
     * reloads the settings of the Modifier
     */
    public abstract void reload();

    /**
     * @return is the modifier allowed
     */
    public boolean isAllowed() {
        return getConfig().getBoolean("Allowed", true);
    }

    public String getNBTKey() {
        return nbtTag;
    }

    public String getFileName() {
        return fileName;
    }

    /**
     * @return a list of enchantments that may be applied when the modifier is applied
     */
    public abstract List<Enchantment> getAppliedEnchantments();

    /**
     * @return a list of attributes that may be applied when the modifier is applied
     */
    public List<Attribute> getAppliedAttributes() {
        return emptyArrayList;
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

    public void registerCraftingRecipe() {
        FileConfiguration config = getConfig();
        if (config.getBoolean("Recipe.Enabled")) {
            try {
                NamespacedKey nkey = new NamespacedKey(Main.getPlugin(), "Modifier_" + this.nbtTag.replace('\'', '-')); //for Spider'sBane //TODO: Change Spiders Bane NBT-Tag
                ShapedRecipe newRecipe = new ShapedRecipe(nkey, this.getModItem()); //reload recipe
                String top = config.getString("Recipe.Top");
                String middle = config.getString("Recipe.Middle");
                String bottom = config.getString("Recipe.Bottom");
                ConfigurationSection materials = config.getConfigurationSection("Recipe.Materials");

                newRecipe.shape(top, middle, bottom); //makes recipe

                if (materials != null) {
                    for (String key : materials.getKeys(false)) {
                        String materialName = materials.getString(key);

                        if (materialName == null) {
                            ChatWriter.logInfo(LanguageManager.getString("Modifier.MaterialEntryNotFound"));
                            return;
                        }

                        Material material = Material.getMaterial(materialName);

                        if (material == null) {
                            ChatWriter.log(false, "Material [" + materialName + "] is null for mod [" + this.name + "]");
                            return;
                        } else {
                            newRecipe.setIngredient(key.charAt(0), material);
                        }
                    }
                } else {
                    ChatWriter.logError("Could not register recipe for the " + this.name + "-Modifier!"); //executes if the recipe could not initialize
                    ChatWriter.logError("Cause: Malformed recipe config.");

                    return;
                }

                Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
                ChatWriter.log(false, "Registered recipe for the " + this.name + "-Modifier!");
                ModManager.instance().recipe_Namespaces.add(nkey);
            } catch (Exception e) {
                ChatWriter.logError("Could not register recipe for the " + this.name + "-Modifier!"); //executes if the recipe could not initialize
                e.printStackTrace();
            }
        }
    }

    protected FileConfiguration getConfig() {
        return ConfigurationManager.getConfig(this.getFileName());
    }
}

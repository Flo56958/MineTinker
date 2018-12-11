package de.flo56958.MineTinker.Modifiers;

import de.flo56958.MineTinker.Events.ToolLevelUpEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Types.*;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.List;

public class ModManager {

    private static final FileConfiguration config = Main.getPlugin().getConfig();
    private static final PluginManager pluginManager = Bukkit.getPluginManager();

    /**
     * stores the list of allowed modifiers
     */
    private final ArrayList<Modifier> mods = new ArrayList<>();
    /**
     * sublist of mods which contains all modifiers that can be crafted (if enabled)
     */
    private final ArrayList<Modifier> craftableMods = new ArrayList<>();
    /**
     * sublist of mods which contains all modifiers that are crafted through the bookshelf
     */
    private final ArrayList<Modifier> enchantableMods = new ArrayList<>();

    private static ModManager instance;

    public final String IDENTIFIER_ARMOR;
    public final String IDENTIFIER_BUILDERSWAND;
    public final String IDENTIFIER_TOOL;

    public final String LEVELLINE;
    public final String EXPLINE;
    public final String FREEMODIFIERSLOTS;
    public final String MODIFIERSTART;

    /**
     * Class constructor (no parameters)
     */
    private ModManager() {
        this.IDENTIFIER_ARMOR = ChatColor.WHITE + config.getString("Language.Identifier_Armor");
        this.IDENTIFIER_BUILDERSWAND = ChatColor.WHITE + config.getString("Language.Identifier_Builderswand");
        this.IDENTIFIER_TOOL = ChatColor.WHITE + config.getString("Language.Identifier_Tool");

        this.LEVELLINE = ChatColor.GOLD + config.getString("Language.LevelLine") + ":" + ChatColor.WHITE + " ";
        this.EXPLINE = ChatColor.GOLD + config.getString("Language.ExpLine") + ":" + ChatColor.WHITE + " ";
        this.FREEMODIFIERSLOTS = ChatColor.WHITE + config.getString("Language.FreeModifierSlotsLine") + ": ";
        this.MODIFIERSTART = ChatColor.WHITE + config.getString("Language.ModifiersLine") + ":";
    }

    /**
     * get the instance that contains the modifier list (VERY IMPORTANT)
     *
     * @return the instance
     */
    public static ModManager instance() {
        if(instance == null) {
            instance = new ModManager();
            instance.init();
        }
        return instance;
    }

    /**
     * checks and loads all modifiers with configurations settings into memory
     */
    private void init() {
        ConfigurationManager configs = Main.getMain().getConfigurations();
        if (configs.getConfig("Auto-Smelt.yml").getBoolean("Auto-Smelt.allowed")) {
            register(new AutoSmelt());
        }
        if (configs.getConfig("Beheading.yml").getBoolean("Beheading.allowed")) {
            register(new Beheading());
        }
        if (configs.getConfig("Directing.yml").getBoolean("Directing.allowed")) {
            register(new Directing());
        }
        if (configs.getConfig("Ender.yml").getBoolean("Ender.allowed")) {
            register(new Ender());
        }
        if (configs.getConfig("Experienced.yml").getBoolean("Experienced.allowed")) {
            register(new Experienced());
        }
        if (configs.getConfig("Extra-Modifier.yml").getBoolean("Extra-Modifier.allowed")) {
            register(new ExtraModifier());
        }
        if (configs.getConfig("Fiery.yml").getBoolean("Fiery.allowed")) {
            register(new Fiery());
        }
        if (configs.getConfig("Glowing.yml").getBoolean("Glowing.allowed")) {
            register(new Glowing());
        }
        if (configs.getConfig("Haste.yml").getBoolean("Haste.allowed")) {
            register(new Haste());
        }
        if (configs.getConfig("Infinity.yml").getBoolean("Infinity.allowed")) {
            register(new Infinity());
        }
        if (configs.getConfig("Knockback.yml").getBoolean("Knockback.allowed")) {
            register(new Knockback());
        }
        if (configs.getConfig("Light-Weight.yml").getBoolean("Light-Weight.allowed")) {
            register(new LightWeight());
        }
        if (configs.getConfig("Luck.yml").getBoolean("Luck.allowed")) {
            register(new Luck());
        }
        if (configs.getConfig("Melting.yml").getBoolean("Melting.allowed")) {
            register(new Melting());
        }
        if (configs.getConfig("Poisonous.yml").getBoolean("Poisonous.allowed")) {
            register(new Poisonous());
        }
        if (configs.getConfig("Power.yml").getBoolean("Power.allowed")) {
            register(new Power());
        }
        if (configs.getConfig("Protecting.yml").getBoolean("Protecting.allowed")) {
            register(new Protecting());
        }
        if (configs.getConfig("Reinforced.yml").getBoolean("Reinforced.allowed")) {
            register(new Reinforced());
        }
        if (configs.getConfig("Self-Repair.yml").getBoolean("Self-Repair.allowed")) {
            register(new SelfRepair());
        }
        if (configs.getConfig("Sharpness.yml").getBoolean("Sharpness.allowed")) {
            register(new Sharpness());
        }
        if (configs.getConfig("Shulking.yml").getBoolean("Shulking.allowed")) {
            register(new Shulking());
        }
        if (configs.getConfig("Silk-Touch.yml").getBoolean("Silk-Touch.allowed")) {
            register(new SilkTouch());
        }
        if (configs.getConfig("Sweeping.yml").getBoolean("Sweeping.allowed")) {
            register(new Sweeping());
        }
        if (configs.getConfig("Timber.yml").getBoolean("Timber.allowed")) {
            register(new Timber());
        }
        if (configs.getConfig("Webbed.yml").getBoolean("Webbed.allowed")) {
            register(new Webbed());
        }

        for (Modifier m : this.mods) {
            if (m instanceof Craftable) {
                this.craftableMods.add(m);
            }
            if (m instanceof Enchantable) {
                this.enchantableMods.add(m);
            }
        }

        for (Modifier m : this.craftableMods) {
            ((Craftable) m).registerCraftingRecipe();
        }
    }

    /**
     * register a new modifier to the list
     *
     * @param mod the modifier instance
     */
    private void register(Modifier mod) {
        mods.add(mod);
        ChatWriter.log(false, ChatColor.GREEN + "Registered the " + mod.getColor() + mod.getName() + ChatColor.GREEN + " modifier from " + mod.getSource().getName() + ".");
    }

    /**
     * get all the modifiers in the list
     *
     * @return the modifier list
     */
    public List<Modifier> getAllMods() {
        return this.mods;
    }

    /**
     * get all the craftable modifiers in the list
     *
     * @return the craftable modifier list
     */
    public List<Modifier> getCraftableMods() { return this.craftableMods; }

    /**
     * get all the enchantable modifiers in the list
     *
     * @return the enchantable modifier list
     */
    public List<Modifier> getEnchantableMods() { return this.enchantableMods; }

    /**
     * get a specific modifier instance
     *
     * @param type the modifiertype
     * @return the modifier instance, null if invalid modifier name
     */
    public Modifier get(ModifierType type) {
        for(Modifier m : mods) {
            if(m.getType().equals(type)) {
                return m;
            }
        }
        return null;
    }

    /**
     * add a specified modifier to a tool
     *
     * @param is the item to add the modifier to
     * @param mod the modifier to add
     */
    void addMod(ItemStack is, Modifier mod) {
        if(isToolViable(is) || isArmorViable(is)) {
            ItemMeta meta = is.getItemMeta();
            List<String> lore = meta.getLore();
            int level = getModLevel(is, mod);
            if (level != 0) {
                lore.set(getModIndex(is, mod), mod.getColor() + mod.getName() + ": " + ++level);
            } else {
                lore.add(mod.getColor() + mod.getName() + ": " + ++level);
            }
            meta.setLore(lore);
            is.setItemMeta(meta);
        }
    }

    /**
     * get the level of a specified modifier on a tool
     *
     * @param is the item
     * @param mod the modifier
     */
    public int getModLevel(ItemStack is, Modifier mod) {
        if(isToolViable(is) || isArmorViable(is)) {
            List<String> lore = is.getItemMeta().getLore();
            for (String s : lore) {
                if (s.startsWith(mod.getColor() + mod.getName())) {
                    String[] st = s.split(": ");
                    return Integer.parseInt(st[1]);
                }
            }
        }
        return 0;
    }

    /**
     * get the lore index of a specified modifier on a tool
     *
     * @param is the item
     * @param mod the modifier
     */
    private int getModIndex(ItemStack is, Modifier mod) {
        List<String> lore = is.getItemMeta().getLore();
        for (String s : lore) {
            if (s.startsWith(mod.getColor() + mod.getName())) {
                return lore.indexOf(s);
            }
        }
        return -1;
    }

    /**
     * remove a modifier from a tool
     *
     * @param is the item to remove the modifier from
     * @param mod the modifier to remove
     */
    public void removeMod(ItemStack is, Modifier mod) {
		/*if((PlayerInfo.isToolViable(is)) && (hasMod(is, mod))) {
			List<String> lore = getModInfo(is);
			for(String s: new ArrayList<>(lore)) {
				if(s.startsWith(mod.getName())) {
					lore.remove(s);
				}
			}
			setModInfo(is, lore);
		}*/
    }

    /**
     * gets how many free slots the tool has
     *
     * @param is the item to get the information from
     */
    public int getFreeSlots(ItemStack is) {
        ItemMeta im = is.getItemMeta();
        List<String> lore = im.getLore();
        for(String s : lore) {
            if(s.startsWith(this.FREEMODIFIERSLOTS)) {
                return Integer.parseInt(s.substring(this.FREEMODIFIERSLOTS.length()));
            }
        }
        return 0;
    }

    /**
     * sets how many free slots the tool has
     *
     * @param is the item to set the information to
     */
    public void setFreeSlots(ItemStack is, int freeSlots) {
        ItemMeta meta = is.getItemMeta();
        List<String> lore = meta.getLore();
        for(String s : lore) {
            if(s.startsWith(this.FREEMODIFIERSLOTS)) {
                lore.set(lore.indexOf(s), this.FREEMODIFIERSLOTS + freeSlots);
                meta.setLore(lore);
                is.setItemMeta(meta);
            }
        }
    }

    /**
     * gets what level the tool has
     *
     * @param is the item to get the information from
     */
    public int getLevel(ItemStack is) {
        ItemMeta meta = is.getItemMeta();
        List<String> lore = meta.getLore();
        for(String s : lore) {
            if(s.startsWith(this.LEVELLINE)) {
                String s_ = s.substring(this.LEVELLINE.length());
                try {
                    return Integer.parseInt(s_);
                } catch (Exception e) {
                    return -1;
                }
            }
        }
        return -1;
    }

    /**
     * sets the level of the tool
     *
     * @param is the item to get the information from
     */
    public void setLevel(ItemStack is, int level) {
        ItemMeta meta = is.getItemMeta();
        List<String> lore = meta.getLore();
        for(String s : lore) {
            if(s.startsWith(this.LEVELLINE)) {
                lore.set(lore.indexOf(s), this.LEVELLINE + level);
                meta.setLore(lore);
                is.setItemMeta(meta);
            }
        }
    }

    /**
     * gets the amount of exp the tool has
     *
     * @param is the item to get the information from
     */
    public long getExp(ItemStack is) {
        ItemMeta meta = is.getItemMeta();
        List<String> lore = meta.getLore();
        for(String s : lore) {
            if(s.startsWith(this.EXPLINE)) {
                String s_ = s.substring(this.EXPLINE.length());
                String[] s__ = s_.split(" / ");
                try {
                    return Long.parseLong(s__[0]);
                } catch (Exception e) {
                    return -1;
                }
            }
        }
        return -1;
    }

    /**
     * sets the exp amount of the tool
     *
     * @param is the item to get the information from
     */
    public void setExp(ItemStack is, long exp) {
        ItemMeta meta = is.getItemMeta();
        List<String> lore = meta.getLore();
        for(String s : lore) {
            if(s.startsWith(this.EXPLINE)) {
                lore.set(lore.indexOf(s), this.EXPLINE + exp + " / " + getNextLevelReq(getLevel(is)));
                meta.setLore(lore);
                is.setItemMeta(meta);
            }
        }
    }

    /**
     * @param tool The Tool that is checked
     * @param mod The modifier that is checked in tool
     * @return if the tool has the mod
     */
    public boolean hasMod(ItemStack tool, Modifier mod) {
        return getModIndex(tool, mod) != -1;
    }

    /**
     * calculates the required exp for the given level
     * @param level
     * @return long value of the exp required
     */
    public long getNextLevelReq(int level) {
        return (long) (Main.getPlugin().getConfig().getInt("LevelStep") * Math.pow(Main.getPlugin().getConfig().getDouble("LevelFactor"), (double) (level - 1)));
    }

    /**
     *
     * @param p Player that uses the tool
     * @param tool tool that needs to get exp
     * @param amount how much exp should the tool get
     */
    public void addExp(Player p, ItemStack tool, int amount) {
        boolean LevelUp = false;

        int level = this.getLevel(tool);
        long exp = this.getExp(tool);

        if (level == -1 || exp == -1) { return; }

        if (exp == Long.MAX_VALUE || exp + 1 < 0 || level + 1 < 0) {
            if (Main.getPlugin().getConfig().getBoolean("ResetAtIntOverflow")) {
                level = 1;
                setLevel(tool, level);
                exp = 0;
                LevelUp = true;
            } else {
                return;
            }
        }

        exp = exp + amount;
        if (exp >= getNextLevelReq(level)) {
            level++;
            setLevel(tool, level);
            LevelUp = true;
        }

        setExp(tool, exp);

        if (LevelUp) {
            pluginManager.callEvent(new ToolLevelUpEvent(p, tool));
        }
    }

    /**
     * checks if the ItemStack is viable is viable
     * @param armor
     * @return
     */
    public boolean isArmorViable(ItemStack armor) {
        if (armor == null) { return false; }
        if (!armor.hasItemMeta()) { return false; }
        ItemMeta meta = armor.getItemMeta();

        if (!meta.hasLore()) { return false; }
        List<String> lore = meta.getLore();
        return lore.contains(this.IDENTIFIER_ARMOR);
    }

    public boolean isToolViable(ItemStack tool) {
        if (tool == null) { return false; }
        if (!tool.hasItemMeta()) { return false; }
        ItemMeta meta = tool.getItemMeta();

        if (!meta.hasLore()) { return false; }
        List<String> lore = meta.getLore();
        return lore.contains(this.IDENTIFIER_TOOL);
    }

    public boolean isWandViable(ItemStack wand) {
        if (wand == null) { return false; }
        if (!wand.hasItemMeta()) { return false; }
        ItemMeta meta = wand.getItemMeta();

        if (!meta.hasLore()) { return false; }
        List<String> lore = meta.getLore();
        return lore.contains(this.IDENTIFIER_BUILDERSWAND);
    }
}

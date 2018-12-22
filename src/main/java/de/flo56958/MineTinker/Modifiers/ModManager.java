package de.flo56958.MineTinker.Modifiers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;

import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Events.ToolLevelUpEvent;
import de.flo56958.MineTinker.Modifiers.Types.AutoSmelt;
import de.flo56958.MineTinker.Modifiers.Types.Beheading;
import de.flo56958.MineTinker.Modifiers.Types.Directing;
import de.flo56958.MineTinker.Modifiers.Types.Ender;
import de.flo56958.MineTinker.Modifiers.Types.Experienced;
import de.flo56958.MineTinker.Modifiers.Types.ExtraModifier;
import de.flo56958.MineTinker.Modifiers.Types.Fiery;
import de.flo56958.MineTinker.Modifiers.Types.Glowing;
import de.flo56958.MineTinker.Modifiers.Types.Haste;
import de.flo56958.MineTinker.Modifiers.Types.Infinity;
import de.flo56958.MineTinker.Modifiers.Types.Knockback;
import de.flo56958.MineTinker.Modifiers.Types.LightWeight;
import de.flo56958.MineTinker.Modifiers.Types.Luck;
import de.flo56958.MineTinker.Modifiers.Types.Melting;
import de.flo56958.MineTinker.Modifiers.Types.ModifierType;
import de.flo56958.MineTinker.Modifiers.Types.Poisonous;
import de.flo56958.MineTinker.Modifiers.Types.Power;
import de.flo56958.MineTinker.Modifiers.Types.Protecting;
import de.flo56958.MineTinker.Modifiers.Types.Reinforced;
import de.flo56958.MineTinker.Modifiers.Types.SelfRepair;
import de.flo56958.MineTinker.Modifiers.Types.Sharpness;
import de.flo56958.MineTinker.Modifiers.Types.Shulking;
import de.flo56958.MineTinker.Modifiers.Types.SilkTouch;
import de.flo56958.MineTinker.Modifiers.Types.Sweeping;
import de.flo56958.MineTinker.Modifiers.Types.Timber;
import de.flo56958.MineTinker.Modifiers.Types.Webbed;
import de.flo56958.MineTinker.Utilities.ChatWriter;

public class ModManager {

    private static final FileConfiguration config = Main.getPlugin().getConfig();
    private static final PluginManager pluginManager = Bukkit.getPluginManager();

    /**
     * stores the list of all modifiers
     */
    private final List<Modifier> allMods = new ArrayList<>();
    /**
     * stores the list of allowed modifiers
     */
    private final List<Modifier> mods = new ArrayList<>();
    /**
     * sublist of mods which contains all modifiers that can be crafted (if enabled)
     */
    private final List<Modifier> craftableMods = new ArrayList<>();
    /**
     * sublist of mods which contains all modifiers that are crafted through the bookshelf
     */
    private final List<Modifier> enchantableMods = new ArrayList<>();

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
    public synchronized static ModManager instance() {
        if(instance == null) {
            instance = new ModManager();
            instance.init();
        }
        return instance;
    }

    public void reload() {
    	for(Modifier m : allMods) {
    		m.reload();
    		
    		if(m.isAllowed())
    			register(m);
    		else
    			unregister(m);
    	}
    	
    	this.craftableMods.clear();
        this.enchantableMods.clear();
    	
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
     * checks and loads all modifiers with configurations settings into memory
     */
    private void init() {
    	allMods.add(new AutoSmelt());
    	allMods.add(new Beheading());
    	allMods.add(new Directing());
    	allMods.add(new Ender());
    	allMods.add(new Experienced());
    	allMods.add(new ExtraModifier());
    	allMods.add(new Fiery());
    	allMods.add(new Glowing());
    	allMods.add(new Haste());
    	allMods.add(new Infinity());
    	allMods.add(new Knockback());
    	allMods.add(new LightWeight());
        allMods.add(new Luck());
        allMods.add(new Melting());
        allMods.add(new Poisonous());
        allMods.add(new Power());
        allMods.add(new Protecting());
        allMods.add(new Reinforced());
        allMods.add(new SelfRepair());
        allMods.add(new Sharpness());
        allMods.add(new Shulking());
        allMods.add(new SilkTouch());
        allMods.add(new Sweeping());
        allMods.add(new Timber());
        allMods.add(new Webbed());
        
        reload();
    }

    /**
     * register a new modifier to the list
     *
     * @param mod the modifier instance
     */
    private void register(Modifier mod) {
    	if(!mods.contains(mod)) {
	        mods.add(mod);
	        ChatWriter.logColor(ChatColor.GREEN + "Registered the " + mod.getColor() + mod.getName() + ChatColor.GREEN + " modifier from " + mod.getSource().getName() + ".");
    	}
    }
    
    private void unregister(Modifier mod) {
    	 mods.remove(mod);
         ChatWriter.logColor(ChatColor.GREEN + "Unregistered the " + mod.getColor() + mod.getName() + ChatColor.GREEN + " modifier from " + mod.getSource().getName() + ".");
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
    private int getLevel(ItemStack is) {
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
    private void setLevel(ItemStack is, int level) {
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
    private long getExp(ItemStack is) {
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
    private void setExp(ItemStack is, long exp) {
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

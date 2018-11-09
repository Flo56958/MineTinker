package de.flo56958.MineTinker.Modifiers;

import de.flo56958.MineTinker.Data.CraftingRecipes;
import de.flo56958.MineTinker.Data.Strings;
import de.flo56958.MineTinker.Events.ToolLevelUpEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Types.*;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.PlayerInfo;
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
	
	/*
	 * all instances of modifier
	 */
	private final ArrayList<Modifier> mods = new ArrayList<>();
	
	private ModManager instance;
	
	/**
	 * get the instance that contains the modifier list (VERY IMPORTANT)
	 * 
	 * @return the instance
	 */
	public ModManager instance() {
		if(instance == null) {
			instance = new ModManager();
		}
		return instance;
	}

	public void init() {
		if (config.getBoolean("Modifiers.Auto-Smelt.allowed")) {
			register(new AutoSmelt());
			CraftingRecipes.registerAutoSmeltModifier();
		}
		if (config.getBoolean("Modifiers.Beheading.allowed")) {
			register(new Beheading());
		}
        if (config.getBoolean("Modifiers.Directing.allowed")) {
            register(new Directing());
            CraftingRecipes.registerDirectingModifier();
        }
        if (config.getBoolean("Modifiers.Ender.allowed")) {
            register(new Ender());
            CraftingRecipes.registerEnderModifier();
        }
        if (config.getBoolean("Modifiers.Experienced.allowed")) {
            register(new Experienced());
        }
		if (config.getBoolean("Modifiers.Extra-Modifier.allowed")) {
			register(new ExtraModifier());
		}
		if (config.getBoolean("Modifiers.Fiery.allowed")) {
			register(new Fiery());
		}
		if (config.getBoolean("Modifiers.Glowing.allowed")) {
			register(new Glowing());
			CraftingRecipes.registerGlowingModifier();
		}
		if (config.getBoolean("Modifiers.Haste.allowed")) {
			register(new Haste());
			CraftingRecipes.registerHasteModifier();
		}
		if (config.getBoolean("Modifiers.Infinity.allowed")) {
			register(new Infinity());
		}
		if (config.getBoolean("Modifiers.Knockback.allowed")) {
			register(new Knockback());
		}
		if (config.getBoolean("Modifiers.Luck.allowed")) {
			register(new Luck());
			CraftingRecipes.registerLuckModifier();
		}
		if (config.getBoolean("Modifiers.Melting.allowed")) {
			register(new Melting());
		}
		if (config.getBoolean("Modifiers.Poisonous.allowed")) {
			register(new Poisonous());
		}
		if (config.getBoolean("Modifiers.Power.allowed")) {
			register(new Power());
		}
		if (config.getBoolean("Modifiers.Reinforced.allowed")) {
			register(new Reinforced());
			CraftingRecipes.registerReinforcedModifier();
		}
		if (config.getBoolean("Modifiers.Self-Repair.allowed")) {
			register(new SelfRepair());
		}
        if (config.getBoolean("Modifiers.Sharpness.allowed")) {
            register(new Sharpness());
            CraftingRecipes.registerSharpnessModifier();
        }
        if (config.getBoolean("Modifiers.Shulking.allowed")) {
            register(new Shulking());
            CraftingRecipes.registerShulkingModifier();
        }
        if (config.getBoolean("Modifiers.Silk-Touch.allowed")) {
            register(new SilkTouch());
        }
        if (config.getBoolean("Modifiers.Sweeping.allowed")) {
            register(new Sweeping());
        }
        if (config.getBoolean("Modifiers.Timber.allowed")) {
            register(new Timber());
        }
        if (config.getBoolean("Modifiers.Webbed.allowed")) {
            register(new Webbed());
            CraftingRecipes.registerWebbedModifier();
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
		return mods;
	}
	
	/**
	 * get a specific modifier instance
	 * 
	 * @param name the modifier's name
	 * @return the modifier instance, null if invalid modifier name
	 */
    private Modifier get(String name) {
		for(Modifier m : mods) {
			if(m.getName().equalsIgnoreCase(name)) {
				return m;
			}
		}
		return null;
	}

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
	 * get all instances of all modifiers on a tool
	 * 
	 * @param is the item to check
	 * @return list of instances
	 */
	public ArrayList<Modifier> getMods(ItemStack is){
		ArrayList<Modifier> modz = new ArrayList<>();
		if(PlayerInfo.isToolViable(is)) {
			List<String> l = getModInfo(is);
			for(String mod : l) {
				String[] s = mod.split(" ");
				if(get(s[0]) != null) modz.add(get(s[0]));
			}
		}
		return modz;
	}
	
	/**
	 * get the string list of modifiers and their level from a tool
	 * 
	 * @param is the item to check
	 * @return list of strings
	 */
    private ArrayList<String> getModInfo(ItemStack is){
		ArrayList<String> list = new ArrayList<>();
		if(PlayerInfo.isToolViable(is)) {
			ItemMeta im = is.getItemMeta();
			List<String> lore = im.getLore();
			List<String> l = lore.subList(lore.indexOf(Strings.IDENTIFIER), lore.size());
			list.addAll(l);
		}
		return list;
	}

	/**
	 * add a specified modifier to a tool
	 * 
	 * @param is the item to add the modifier to
	 * @param mod the modifier to add
	 */
	public void addMod(ItemStack is, Modifier mod) {
		if(PlayerInfo.isToolViable(is)) {
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
		if(PlayerInfo.isToolViable(is)) {
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
			if(s.startsWith(Strings.FREEMODIFIERSLOTS)) {
				String number = s.substring(Strings.FREEMODIFIERSLOTS.length());
				return Integer.parseInt(number);
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
			if(s.startsWith(Strings.FREEMODIFIERSLOTS)) {
				lore.set(lore.indexOf(s), Strings.FREEMODIFIERSLOTS + freeSlots);
				meta.setLore(lore);
				is.setItemMeta(meta);
			}
		}
	}

	public boolean hasMod(ItemStack is, Modifier mod) {
		return getModIndex(is, mod) != -1;
	}

    public long getNextLevelReq(int level) {
        return (long) (Main.getPlugin().getConfig().getInt("LevelStep") * Math.pow(Main.getPlugin().getConfig().getDouble("LevelFactor"), (double) (level - 1)));
    }

    public void addExp(Player p, ItemStack tool, int amount) {
        boolean LevelUp = false;

        ItemMeta meta = tool.getItemMeta();
        List<String> lore = meta.getLore();

        String[] levelS = lore.get(1).split(" ");
        String[] expS = lore.get(2).split(" ");

        int level = Integer.parseInt(levelS[1]);
        long exp = Long.parseLong(expS[1]);
        if (exp == Long.MAX_VALUE || exp < 0 || level < 0) {
            if (Main.getPlugin().getConfig().getBoolean("ResetAtIntOverflow")) {
                level = 1;
                lore.set(1, ChatColor.GOLD + "Level:" + ChatColor.WHITE + " " + level);
                exp = 0;
                LevelUp = true;
            } else {
                return;
            }
        }

        exp = exp + amount;
        if (exp >= getNextLevelReq(level)) {
            level++;
            lore.set(1, ChatColor.GOLD + "Level:" + ChatColor.WHITE + " " + level);
            LevelUp = true;
        }

        lore.set(2, ChatColor.GOLD + "Exp:" + ChatColor.WHITE + " " + exp + " / " + getNextLevelReq(level));

        meta.setLore(lore);
        tool.setItemMeta(meta);
        if (LevelUp) {
            pluginManager.callEvent(new ToolLevelUpEvent(p, tool));
        }
    }
}

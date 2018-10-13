package com.minetinker.modifiers;

import com.minetinker.data.Strings;
import com.minetinker.utilities.ChatWriter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class ModManager {
	
	/*
	 * all instances of modifier
	 */
	List<Modifier> mods = new ArrayList<>();
	
	private ModManager instance;
	
	/**
	 * get the instance that contains the mod list (VERY IMPORTANT)
	 * 
	 * @return the instance
	 */
	public ModManager instance() {
		if(instance == null) {
			instance = new ModManager();
		}
		return instance;
	}
	
	/**
	 * register a new modifier to the list
	 * 
	 * @param mod the modifier instance
	 * @param source the source plugin
	 */
	public void register(Modifier mod, Plugin source) {
		mods.add(mod);
		ChatWriter.log(false, ChatColor.GREEN + "Registered the " + mod.getColor() + mod.getName() + " modifier from " + source.getName() + ".");
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
	public Modifier get(String name) {
		for(Modifier m: mods) {
			if(m.getName().equalsIgnoreCase(name)) {
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
	public List<Modifier> getMods(ItemStack is){
		List<Modifier> modz = new ArrayList<>();
		if(isTool(is)) {
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
	public List<String> getModInfo(ItemStack is){
		List<String> list = new ArrayList<>();
		if(isTool(is)) {
			ItemMeta im = is.getItemMeta();
			List<String> lore = im.getLore();
			List<String> l = lore.subList(lore.indexOf(Strings.IDENTIFIER), lore.size());
			list.addAll(l);
		}
		return list;
	}
	
	/**
	 * set the string list of modifiers and their level on a tool
	 * 
	 * @param is the item to check
	 * @param lore the string list to set
	 */
	public void setModInfo(ItemStack is, List<String> lore) {
		ItemMeta im = is.getItemMeta();
		List<String> l = im.getLore();
		l.subList(l.indexOf(Strings.MODIFIERSTART) + 1, l.size()).clear();
		l.addAll(lore);
		im.setLore(l);
		is.setItemMeta(im);
	}
	
	/**
	 * check if the item specified is a valid tool
	 * 
	 * @param is the item to check
	 * @return true or false
	 */
	public boolean isTool(ItemStack is) {
		ItemMeta im = is.getItemMeta();
		return im.hasLore() && im.getLore().contains(Strings.IDENTIFIER);
	}
	
	/**
	 * get the level of a specific modifier from a tool
	 * 
	 * @param is the item to check
	 * @param mod the modifier to look for
	 * @return level of the modifier, 0 if not present
	 */
	public int getModLevel(ItemStack is, Modifier mod) {
		if((isTool(is)) && (getMods(is).contains(mod))) {
			List<String> list = getModInfo(is);
			for(String s : list) {
				if(s.startsWith(mod.getName())) {
					String[] st = s.split(" ");
					return Integer.parseInt(st[1]);
				}
			}
		}
		return 0;
	}
	
	/**
	 * check if a tool has a specific modifier
	 * 
	 * @param is the item to check
	 * @param mod the modifier to look for
	 * @return true or false
	 */
	public boolean hasMod(ItemStack is, Modifier mod) {
		return getMods(is).contains(mod);
	}
	
	/**
	 * add a specified modifier to a tool
	 * 
	 * @param is the item to add the modifier to
	 * @param mod the modifier to add
	 * @param lvl the level to set the modifier at
	 */
	public void addMod(ItemStack is, Modifier mod, int lvl) {
		if(isTool(is)) {
			List<Modifier> modz = getMods(is);
			if(modz.contains(mod)) {
				int i = getModLevel(is, mod);
				if(i > lvl) lvl = i;
				if(i == lvl) lvl++;
			}
			List<String> newInfo = getModInfo(is);
			newInfo.add(mod.getName() + Integer.toString(lvl));
			setModInfo(is, newInfo);
		}
	}
	
	/**
	 * remove a modifier from a tool
	 * 
	 * @param is the item to remove the modifier from
	 * @param mod the modifier to remove
	 */
	public void removeMod(ItemStack is, Modifier mod) {
		if((isTool(is)) && (hasMod(is, mod))) {
			List<String> lore = getModInfo(is);
			for(String s: new ArrayList<>(lore)) {
				if(s.startsWith(mod.getName())) {
					lore.remove(s);
				}
			}
			setModInfo(is, lore);
		}
	}
	
}

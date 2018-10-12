package com.minetinker.modifiers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import de.flo56958.MineTinker.Data.ToolType;

public abstract class Modifier {

	protected String name;
	protected ChatColor color;
	protected int maxLvl;
	protected HashMap<String, Object> options;
	protected ItemStack item;
	protected List<ToolType> allowedTools;
	protected Plugin source;
	
	public String getName() {
		return name;
	}
	
	public ChatColor getColor() {
		return color;
	}
	
	public int getMaxLvl() {
		return maxLvl;
	}
	
	public Map<String, Object> getOptions(){
		return options;
	}
	
	public ItemStack getItem() {
		return item;
	}
	
	public List<ToolType> getAllowedTools(){
		return allowedTools;
	}
	
	public void load() {
		
	}
	
}

package de.flo56958.MineTinker.Modifiers;

import de.flo56958.MineTinker.Data.ModifierFailCause;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.ModifierApplyEvent;
import de.flo56958.MineTinker.Events.ModifierFailEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Types.ModifierType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class Modifier {

	private static final ModManager modManager = Main.getModManager();
	private static final PluginManager pluginManager = Bukkit.getPluginManager();
	private static FileConfiguration config = Main.getPlugin().getConfig();

	private final String name;
	private final ModifierType type;
	private final String description;
	private final ChatColor color;
	private final int maxLvl;
	private HashMap<String, Object> options;
	private final ItemStack modItem;
	private final ArrayList<ToolType> allowedTools;
	private final Plugin source;
	
	public String getName() {
		return name;
	}

	public String getDescription() { return description; }

	public ModifierType getType() { return type; }
	
	public ChatColor getColor() {
		return color;
	}
	
	public int getMaxLvl() { return maxLvl; }
	
	public Map<String, Object> getOptions(){
		return options;
	}
	
	public ItemStack getModItem() {
		return modItem;
	}
	
	public ArrayList<ToolType> getAllowedTools(){
		return allowedTools;
	}

	public Plugin getSource() { return source; }

	/**
	 * register a new modifier to the list
	 *
	 * @param p the Player
	 * @param tool the to modify Tool
	 */
	public abstract ItemStack applyMod(Player p, ItemStack tool, boolean isCommand);

	public Modifier(String name, String description, ModifierType type, ChatColor color, int maxLvl, ItemStack modItem, ArrayList<ToolType> allowedTools, Plugin source) {
		this.name = name;
		this.description = description;
		this.type = type;
		this.color = color;
		this.maxLvl = maxLvl;
		this.modItem = modItem;
		this.allowedTools = allowedTools;
		this.source = source;
	}

	public static ItemStack checkAndAdd(Player p, ItemStack tool, Modifier mod, String permission,  boolean isCommand) {
		if (!p.hasPermission("minetinker.modifiers." + permission + ".apply")) {
			pluginManager.callEvent(new ModifierFailEvent(p, tool, mod, ModifierFailCause.NO_PERMISSION, isCommand));
			return null;
		}
		if (!mod.getAllowedTools().contains(ToolType.get(tool.getType()))) {
			pluginManager.callEvent(new ModifierFailEvent(p, tool, mod, ModifierFailCause.INVALID_TOOLTYPE, isCommand));
			return null;
		}
		if (modManager.getModLevel(tool, mod) >= mod.getMaxLvl()) {
			pluginManager.callEvent(new ModifierFailEvent(p, tool, mod, ModifierFailCause.MOD_MAXLEVEL, isCommand));
			return null;
		}

		modManager.addMod(tool, mod);

		int freeSlots = modManager.getFreeSlots(tool);

		if (!isCommand) {
			modManager.setFreeSlots(tool, --freeSlots);
		}

		pluginManager.callEvent(new ModifierApplyEvent(p, tool, mod, freeSlots, isCommand));
		return tool;
	}
	
}

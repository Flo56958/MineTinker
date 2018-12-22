package de.flo56958.MineTinker.Modifiers;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import de.flo56958.MineTinker.Data.ModifierFailCause;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.ModifierApplyEvent;
import de.flo56958.MineTinker.Events.ModifierFailEvent;
import de.flo56958.MineTinker.Modifiers.Types.ModifierType;

public abstract class Modifier {

	protected static final ModManager modManager = ModManager.instance();
	protected static final PluginManager pluginManager = Bukkit.getPluginManager();

	private String name;
	private final ModifierType type;
	private String description;
	private final ChatColor color;
	private int maxLvl;
	private ItemStack modItem;
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
	
	private int getMaxLvl() { return maxLvl; }

	public ItemStack getModItem() {
		return modItem;
	}
	
	protected ArrayList<ToolType> getAllowedTools(){
		return allowedTools;
	}

	Plugin getSource() { return source; }

	/**
	 * register a new modifier to the list
	 *
	 * @param p the Player
	 * @param tool the to modify Tool
	 */
	public abstract ItemStack applyMod(Player p, ItemStack tool, boolean isCommand);

	/**
	 * Class constructor
	 * @param name Name of the Modifier
	 * @param description
	 * @param type ModifierType of the Modifier
	 * @param color Color of the Modifier
	 * @param maxLvl Maximum Level cap of the Modifier
	 * @param modItem ItemStack that is required to craft the Modifier
	 * @param allowedTools Lists of ToolTypes where the Modifier is allowed on
	 * @param source The Plugin that registered the Modifier
	 */
	protected Modifier(ModifierType type, ChatColor color, ArrayList<ToolType> allowedTools, Plugin source) {
		this.type = type;
		this.color = color;
		this.allowedTools = allowedTools;
		this.source = source;
		init("", "", 1, new ItemStack(Material.BEDROCK, 1)); //init, maybe someone forget it
		reload();
	}
	
	/**
	 * @param name Name of the Modifier
	 * @param description
	 * @param type ModifierType of the Modifier
	 * @param color Color of the Modifier
	 * @param maxLvl Maximum Level cap of the Modifier
	 * @param modItem ItemStack that is required to craft the Modifier
	 * @param allowedTools Lists of ToolTypes where the Modifier is allowed on
	 * @param source The Plugin that registered the Modifier
	 */
	/*
	 * can be changed at any time
	 */
	protected void init(String name, String description, int maxLvl, ItemStack modItem) {
		this.name = name;
		this.description = description;
		this.maxLvl = maxLvl;
		this.modItem = modItem;
	}
	
	public abstract void reload();

	public abstract boolean isAllowed();
	
	public static ItemStack checkAndAdd(Player p, ItemStack tool, Modifier mod, String permission, boolean isCommand) {
		if (modManager.getFreeSlots(tool) < 1 && !mod.getType().equals(ModifierType.EXTRA_MODIFIER)) {
			pluginManager.callEvent(new ModifierFailEvent(p, tool, mod, ModifierFailCause.NO_FREE_SLOTS, isCommand));
			return null;
		}
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
		} else {
			Bukkit.getPluginManager().callEvent(new ModifierApplyEvent(p, tool, mod, freeSlots, true));
		}

		return tool;
	}
	
}

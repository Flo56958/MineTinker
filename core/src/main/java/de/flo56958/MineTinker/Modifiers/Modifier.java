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
import de.flo56958.MineTinker.Utilities.nms.NBTUtils;
import org.bukkit.*;
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

import java.util.Collections;
import java.util.List;
import java.util.Set;

public abstract class Modifier {
	protected static final ModManager modManager = ModManager.instance();
	protected static final PluginManager pluginManager = Bukkit.getPluginManager();
	private final Plugin source;
	private final String fileName;
	protected String description;
	private String name;
	private ChatColor color;
	private int maxLvl;
	private ItemStack modItem;

	/**
	 * Class constructor
	 *
	 * @param source The Plugin that registered the Modifier
	 */
	protected Modifier(Plugin source) {
		this.fileName = getKey().replace("'", "") + ".yml";
		this.source = source;
	}

	static boolean checkAndAdd(Player p, ItemStack tool, Modifier mod, String permission, boolean isCommand, boolean fromRandom, boolean silent) {
		if ((modManager.getFreeSlots(tool) < 1 && !mod.equals(ExtraModifier.instance())) && !isCommand) {
			if (!silent) pluginManager.callEvent(new ModifierFailEvent(p, tool, mod, ModifierFailCause.NO_FREE_SLOTS, isCommand));
			return false;
		}

		if (!p.hasPermission("minetinker.modifiers." + permission + ".apply")) {
			if (!silent) pluginManager.callEvent(new ModifierFailEvent(p, tool, mod, ModifierFailCause.NO_PERMISSION, isCommand));
			return false;
		}

		FileConfiguration modifiersconfig = ConfigurationManager.getConfig("Modifiers.yml");
		if (!(modifiersconfig.getBoolean("CommandIgnoresToolTypes") && isCommand && !fromRandom) && !mod.isMaterialCompatible(tool.getType())) {
			if (!silent) pluginManager.callEvent(new ModifierFailEvent(p, tool, mod, ModifierFailCause.INVALID_TOOLTYPE, isCommand));
			return false;
		}

		if (!(modifiersconfig.getBoolean("CommandIgnoresMaxLevel") && isCommand && !fromRandom) && modManager.getModLevel(tool, mod) >= mod.getMaxLvl()) {
			if (!silent) pluginManager.callEvent(new ModifierFailEvent(p, tool, mod, ModifierFailCause.MOD_MAXLEVEL, isCommand));
			return false;
		}

		if (!(modManager.hasMod(tool, mod) && modifiersconfig.getBoolean("IgnoreIncompatibilityIfModifierAlreadyApplied"))) {
			if (fromRandom || !(modifiersconfig.getBoolean("CommandIgnoresIncompatibilities") && isCommand)) {
				Set<Modifier> incompatibility = modManager.getIncompatibilities(mod);

				for (Modifier m : incompatibility) {
					if (modManager.hasMod(tool, m)) {
						if (!silent) pluginManager.callEvent(new ModifierFailEvent(p, tool, mod, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
						return false;
					}
					if (modifiersconfig.getBoolean("IncompatibilitiesConsiderEnchants")) {
						for (Enchantment e : m.getAppliedEnchantments()) {
							if (!tool.hasItemMeta()) return false;
							if (tool.getItemMeta().hasEnchant(e)) {
								if (!silent) pluginManager.callEvent(new ModifierFailEvent(p, tool, mod, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
								return false;
							}
						}
					}
				}
			}
		}

		modManager.addMod(tool, mod);

		int freeSlots = modManager.getFreeSlots(tool);

		if (!isCommand) {
			modManager.setFreeSlots(tool, --freeSlots);
		} else {
			if (!silent) {
				ModifierApplyEvent event = new ModifierApplyEvent(p, tool, mod, freeSlots, true);
				Bukkit.getPluginManager().callEvent(event);
			}
		}

		return true;
	}

	public abstract String getKey();

	public abstract List<ToolType> getAllowedTools();

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

	Plugin getSource() {
		return source;
	} //for other Plugins/Addons that register Modifiers

	public String getName() {
		return this.name;
	}

	public int getEnchantCost() {
		return getConfig().getInt("EnchantCost");
	}

	public boolean isEnchantable() {
		return getConfig().getBoolean("Enchantable");
	}

	/**
	 * changes the core settings of the Modifier (like a secondary constructor)
	 */

	protected void init(Material m, boolean customItem) {
		FileConfiguration config = getConfig();

		this.color = ChatWriter.getColor(config.getString("Color", "%WHITE%"));
		this.maxLvl = config.getInt("MaxLevel");

		if (config.getBoolean("OverrideLanguagesystem", false)) { //use the config values instead
			this.name = config.getString("Name", "");
			this.description = ChatWriter.addColors(config.getString("Description", ""));

			if (customItem) {
				this.modItem = modManager.createModifierItem(m, this.color + config.getString("ModifierItemName", ""),
						ChatWriter.addColors(config.getString("DescriptionModifierItem", "")), this);
			} else {
				this.modItem = new ItemStack(m, 1);
			}
		} else { //normal Languagesystem-Integration
			String langStart = "Modifier." + getKey();

			this.name = LanguageManager.getString(langStart + ".Name");
			this.description = LanguageManager.getString(langStart + ".Description");

			if (customItem) {
				this.modItem = modManager.createModifierItem(m, this.color + LanguageManager.getString(langStart + ".ModifierItemName"),
						ChatColor.WHITE + LanguageManager.getString(langStart + ".DescriptionModifierItem"), this);
			} else {
				this.modItem = new ItemStack(m, 1);
			}
		}

		if (ConfigurationManager.getConfig("Modifiers.yml").getBoolean("UseCustomModelData", false)) {
			this.modItem.setType(Material.STICK);
			NBTUtils.getHandler().setInt(this.modItem, "CustomModelData", Math.abs(this.getKey().hashCode() % 10_000_000));
		}
	}

	/**
	 * applies the Modifier to the tool
	 *
	 * @param p    the Player
	 * @param tool the Tool to modify
	 * @return true if successful
	 * false if failure
	 */
	public boolean applyMod(Player p, ItemStack tool, boolean isCommand) {
		return true;
	}

	/**
	 * what should be done to the Tool if the Modifier gets removed
	 *
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
		return getConfig().getBoolean("Allowed", true);
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

	protected boolean isMaterialCompatible(Material material) {
		for (ToolType toolType : getAllowedTools()) {
			if (toolType.contains(material)) {
				return true;
			}
		}

		return false;
	}

	protected FileConfiguration getConfig() {
		return ConfigurationManager.getConfig(this.getFileName());
	}

	protected void registerCraftingRecipe() {
		if (!hasRecipe()) {
			return;
		}

		FileConfiguration config = getConfig();
		if (config.getBoolean("Recipe.Enabled")) {
			try {
				NamespacedKey nkey = new NamespacedKey(Main.getPlugin(), "Modifier_" + getKey());
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

	// ---------------------- Enchantable Stuff ----------------------

	public void enchantItem(Player p) {
		if (!p.hasPermission("minetinker.modifiers." + getKey().replace("-", "").toLowerCase() + ".craft")) {
			return;
		}

		if (getConfig().getBoolean("Recipe.Enabled")) {
			return;
		}

		Location location = p.getLocation();
		World world = location.getWorld();
		PlayerInventory inventory = p.getInventory();

		if (world == null) {
			return;
		}

		if (p.getGameMode() == GameMode.CREATIVE) {
			if (inventory.addItem(getModItem()).size() != 0) { //adds items to (full) inventory
				world.dropItem(location, getModItem());
			} // no else as it gets added in if

			if (Main.getPlugin().getConfig().getBoolean("Sound.OnEnchanting")) {
				p.playSound(location, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 0.5F);
			}

			ChatWriter.log(false, p.getDisplayName() + " created a " + getName() + "-Modifiers in Creative!");
		} else if (p.getLevel() >= getEnchantCost()) {
			int amount = inventory.getItemInMainHand().getAmount();
			int newLevel = p.getLevel() - getEnchantCost();

			p.setLevel(newLevel);
			inventory.getItemInMainHand().setAmount(amount - 1);

			if (inventory.addItem(getModItem()).size() != 0) { //adds items to (full) inventory
				world.dropItem(location, getModItem());
			} // no else as it gets added in if

			if (Main.getPlugin().getConfig().getBoolean("Sound.OnEnchanting")) {
				p.playSound(location, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 0.5F);
			}

			ChatWriter.log(false, p.getDisplayName() + " created a " + getName() + "-Modifiers!");
		} else {
			ChatWriter.sendActionBar(p, ChatColor.RED + LanguageManager.getString("Modifier.Enchantable.LevelsRequired", p).replace("%amount", "" + getEnchantCost()));
			ChatWriter.log(false, p.getDisplayName() + " tried to create a " + getName() + "-Modifiers but had not enough levels!");
		}

	}
}

package de.flo56958.minetinker.modifiers;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.data.ModifierFailCause;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.events.ModifierApplyEvent;
import de.flo56958.minetinker.events.ModifierFailEvent;
import de.flo56958.minetinker.modifiers.types.ExtraModifier;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.ConfigurationManager;
import de.flo56958.minetinker.utils.LanguageManager;
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

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
	private ItemStack modItem = new ItemStack(Material.BEDROCK);
	protected int slotCost;

	protected int customModelData = -1;

	/**
	 * Class constructor
	 *
	 * @param source The Plugin that registered the Modifier
	 */
	protected Modifier(Plugin source) {
		this.fileName = getKey().replace("'", "") + ".yml";
		ConfigurationManager.loadConfig("Modifiers" + File.separator, this.fileName);
		this.source = source;
	}

	boolean checkAndAdd(Player player, ItemStack tool, String permission, boolean isCommand, boolean fromRandom, boolean silent) {
		if ((modManager.getFreeSlots(tool) < this.getSlotCost() && !this.equals(ExtraModifier.instance())) && !isCommand) {
			if (!silent)
				pluginManager.callEvent(new ModifierFailEvent(player, tool, this, ModifierFailCause.NO_FREE_SLOTS, false));
			return false;
		}

		if (!player.hasPermission("minetinker.modifiers." + permission + ".apply")) {
			if (!silent)
				pluginManager.callEvent(new ModifierFailEvent(player, tool, this, ModifierFailCause.NO_PERMISSION, isCommand));
			return false;
		}

		FileConfiguration modifiersconfig = ConfigurationManager.getConfig("Modifiers.yml");
		if (!(modifiersconfig.getBoolean("CommandIgnoresToolTypes") && isCommand && !fromRandom) && !this.isMaterialCompatible(tool.getType())) {
			if (!silent)
				pluginManager.callEvent(new ModifierFailEvent(player, tool, this, ModifierFailCause.INVALID_TOOLTYPE, isCommand));
			return false;
		}

		if (!(modifiersconfig.getBoolean("CommandIgnoresMaxLevel") && isCommand && !fromRandom) && modManager.getModLevel(tool, this) >= this.getMaxLvl()) {
			if (!silent)
				pluginManager.callEvent(new ModifierFailEvent(player, tool, this, ModifierFailCause.MOD_MAXLEVEL, isCommand));
			return false;
		}

		if (!(modManager.hasMod(tool, this) && modifiersconfig.getBoolean("IgnoreIncompatibilityIfModifierAlreadyApplied"))) {
			if (fromRandom || !(modifiersconfig.getBoolean("CommandIgnoresIncompatibilities") && isCommand)) {
				final Set<Modifier> incompatibility = modManager.getIncompatibilities(this);

				if (incompatibility != null) {
					for (final Modifier m : incompatibility) {
						if (modManager.hasMod(tool, m)) {
							if (!silent)
								pluginManager.callEvent(new ModifierFailEvent(player, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
							return false;
						}
						if (modifiersconfig.getBoolean("IncompatibilitiesConsiderEnchants")) {
							for (final Enchantment e : m.getAppliedEnchantments()) {
								if (!tool.hasItemMeta()) return false;
								if (Objects.requireNonNull(tool.getItemMeta(), "Tool has no ItemMeta").hasEnchant(e)) {
									if (!silent)
										pluginManager.callEvent(new ModifierFailEvent(player, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
									return false;
								}
							}
						}
					}
				}
			}
		}

		modManager.addMod(tool, this);

		if (!isCommand) {
			modManager.setFreeSlots(tool, modManager.getFreeSlots(tool) - this.getSlotCost());
		} else {
			if (!silent) {
				ModifierApplyEvent event = new ModifierApplyEvent(player, tool, this, modManager.getFreeSlots(tool), true);
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

	public int getSlotCost() {
		return slotCost;
	}

	public ItemStack getModItem() {
		return modItem;
	}

	public boolean hasRecipe() {
		return getConfig().getBoolean("Recipe.Enabled", false);
	}

	Plugin getSource() {
		return source;
	} //for other Plugins/Addons that register Modifiers

	public String getName() {
		return this.name;
	}

	public int getEnchantCost() {
		return getConfig().getInt("EnchantCost", 10);
	}

	public boolean isEnchantable() {
		return getConfig().getBoolean("Enchantable", false);
	}

	/**
	 * changes the core settings of the Modifier (like a secondary constructor)
	 */
	protected void init(Material m) {
		FileConfiguration config = getConfig();

		try {
			this.color = ChatWriter.getColor(Objects.requireNonNull(config.getString("Color", "%WHITE%"), "Config has no Color-Value!"));
		} catch (IllegalArgumentException | ArrayIndexOutOfBoundsException ignored) {
			this.color = ChatColor.WHITE;
			ChatWriter.logError("Illegal Color detected for Modifier " + this.getKey());
		}

		this.maxLvl = config.getInt("MaxLevel");
		this.slotCost = config.getInt("SlotCost", 1);

		if (source.equals(MineTinker.getPlugin())) { //normal Languagesystem-Integration
			String langStart = "Modifier." + getKey();

			this.name = LanguageManager.getString(langStart + ".Name");
			this.description = LanguageManager.getString(langStart + ".Description");

			this.modItem = modManager.createModifierItem(m, this.color + LanguageManager.getString(langStart + ".ModifierItemName"),
					ChatColor.WHITE + LanguageManager.getString(langStart + ".DescriptionModifierItem"), this);
		} else { //use the config values instead
			this.name = config.getString("Name", "");
			this.description = ChatWriter.addColors(Objects.requireNonNull(config.getString("Description", ""), "Config has no Description-Value!"));

			this.modItem = modManager.createModifierItem(m, this.color + config.getString("ModifierItemName", ""),
					ChatWriter.addColors(Objects.requireNonNull(config.getString("DescriptionModifierItem", ""), "Config has no DescriptionModifierItem-Value!")), this);
		}

		ItemMeta itemMeta = this.modItem.getItemMeta();
		if (itemMeta != null) itemMeta.setCustomModelData(this.customModelData);
		this.modItem.setItemMeta(itemMeta);
		if (ConfigurationManager.getConfig("Modifiers.yml").getBoolean("UseCustomModelData", false)) {
			this.modItem.setType(Material.STICK);
		}
	}

	/**
	 * applies the Modifier to the tool
	 *
	 * @param player the Player
	 * @param tool   the Tool to modify
	 * @return true if successful
	 * false if failure
	 */
	public boolean applyMod(Player player, ItemStack tool, boolean isCommand) {
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

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	protected boolean isMaterialCompatible(Material material) {
		for (ToolType toolType : getAllowedTools()) {
			if (toolType.contains(material)) {
				return true;
			}
		}

		return false;
	}

	protected FileConfiguration getConfig() {
		return ConfigurationManager.getConfig(this);
	}

	protected void registerCraftingRecipe() {
		if (!hasRecipe()) {
			return;
		}

		FileConfiguration config = getConfig();
		try {
			NamespacedKey nkey = new NamespacedKey(MineTinker.getPlugin(), "Modifier_" + getKey());
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

			MineTinker.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
			ChatWriter.log(false, "Registered recipe for the " + this.name + "-Modifier!");
			ModManager.instance().recipe_Namespaces.add(nkey);
		} catch (Exception e) {
			ChatWriter.logError("Could not register recipe for the " + this.name + "-Modifier!"); //executes if the recipe could not initialize
		}
	}

	public boolean equals(Object o) {
		if (o instanceof Modifier) {
			return ((Modifier) o).getKey().equals(this.getKey());
		}
		return false;
	}

	// ---------------------- Enchantable Stuff ----------------------

	public void enchantItem(Player player) {
		if (!player.hasPermission("minetinker.modifiers." + getKey().replace("-", "").toLowerCase() + ".craft")) {
			return;
		}

		if (getConfig().getBoolean("Recipe.Enabled")) {
			return;
		}

		Location location = player.getLocation();
		World world = location.getWorld();
		PlayerInventory inventory = player.getInventory();

		if (world == null) {
			return;
		}

		if (player.getGameMode() == GameMode.CREATIVE) {
			if (inventory.addItem(getModItem()).size() != 0) { //adds items to (full) inventory
				world.dropItem(location, getModItem());
			} // no else as it gets added in if

			if (MineTinker.getPlugin().getConfig().getBoolean("Sound.OnEnchanting")) {
				player.playSound(location, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 0.5F);
			}

			ChatWriter.log(false, player.getDisplayName() + " created a " + getName() + "-Modifiers in Creative!");
		} else if (player.getLevel() >= getEnchantCost()) {
			int amount = inventory.getItemInMainHand().getAmount();
			int newLevel = player.getLevel() - getEnchantCost();

			player.setLevel(newLevel);
			inventory.getItemInMainHand().setAmount(amount - 1);

			if (inventory.addItem(getModItem()).size() != 0) { //adds items to (full) inventory
				world.dropItem(location, getModItem());
			} // no else as it gets added in if

			if (MineTinker.getPlugin().getConfig().getBoolean("Sound.OnEnchanting")) {
				player.playSound(location, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 0.5F);
			}

			ChatWriter.log(false, player.getDisplayName() + " created a " + getName() + "-Modifiers!");
		} else {
			ChatWriter.sendActionBar(player, ChatColor.RED
					+ LanguageManager.getString("Modifier.Enchantable.LevelsRequired", player)
					.replace("%amount", String.valueOf(getEnchantCost())));
			ChatWriter.log(false, player.getDisplayName() + " tried to create a "
					+ getName() + "-Modifiers but had not enough levels!");
		}
	}
}

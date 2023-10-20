package de.flo56958.minetinker.modifiers;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.api.events.ModifierApplyEvent;
import de.flo56958.minetinker.api.events.ModifierFailEvent;
import de.flo56958.minetinker.data.ModifierFailCause;
import de.flo56958.minetinker.data.ToolType;
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
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

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

	private int minimumLevelRequirement = 1;

	private NamespacedKey namespaceKey;

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

	final boolean checkAndAdd(final Player player, final ItemStack tool, final boolean isCommand,
							  final boolean fromRandom, final boolean silent, final boolean modifySlotCount) {
		if (modifySlotCount) {
			//Check for free Slots
			if ((modManager.getFreeSlots(tool) < this.getSlotCost() && !this.equals(ExtraModifier.instance())) && !isCommand) {
				if (!silent)
					pluginManager.callEvent(new ModifierFailEvent(player, tool, this, ModifierFailCause.NO_FREE_SLOTS, false));
				return false;
			}
		}

		//Check for Permission
		if (player != null) {
			if (!player.hasPermission(getApplyPermission())) {
				if (!silent)
					pluginManager.callEvent(new ModifierFailEvent(player, tool, this, ModifierFailCause.NO_PERMISSION, isCommand));
				return false;
			}
		}

		//Check for ToolType
		FileConfiguration modifiersconfig = ConfigurationManager.getConfig("Modifiers.yml");
		if (!(modifiersconfig.getBoolean("CommandIgnoresToolTypes") && isCommand && !fromRandom) && !this.isMaterialCompatible(tool.getType())) {
			if (!silent)
				pluginManager.callEvent(new ModifierFailEvent(player, tool, this, ModifierFailCause.INVALID_TOOLTYPE, isCommand));
			return false;
		}

		//Check for Tool Level
		if(modManager.getLevel(tool) < this.minimumLevelRequirement) {
			if (!silent)
				pluginManager.callEvent(new ModifierFailEvent(player, tool, this, ModifierFailCause.TOOL_LEVEL_TO_LOW, isCommand));
			return false;
		}

		//Check for Max Level
		if (!(modifiersconfig.getBoolean("CommandIgnoresMaxLevel") && isCommand && !fromRandom) && modManager.getModLevel(tool, this) >= this.getMaxLvl()) {
			if (!silent)
				pluginManager.callEvent(new ModifierFailEvent(player, tool, this, ModifierFailCause.MOD_MAXLEVEL, isCommand));
			return false;
		}

		//Check for Incompatibilities
		if (!(modManager.hasMod(tool, this) && modifiersconfig.getBoolean("IgnoreIncompatibilityIfModifierAlreadyApplied"))) {
			if (fromRandom || !(modifiersconfig.getBoolean("CommandIgnoresIncompatibilities") && isCommand)) {
				final Set<Modifier> incompatibility = modManager.getIncompatibilities(this);

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

		modManager.addMod(tool, this);

		if (modifySlotCount) {
			//Reduce Slotamount
			modManager.setFreeSlots(tool, modManager.getFreeSlots(tool) - this.getSlotCost());
		}

		if (!silent) {
			ModifierApplyEvent event = new ModifierApplyEvent(player, tool, this, modManager.getFreeSlots(tool), true);
			Bukkit.getPluginManager().callEvent(event);
		}

		return true;
	}

	public abstract String getKey();

	public abstract List<ToolType> getAllowedTools();

	public final String getDescription() {
		return description;
	}

	public final ChatColor getColor() {
		return color;
	}

	public final int getMaxLvl() {
		return maxLvl;
	}

	public final int getSlotCost() {
		return slotCost;
	}

	public final ItemStack getModItem() {
		return modItem;
	}

	public final int getMinimumLevelRequirement() {
		return minimumLevelRequirement;
	}

	public final boolean hasRecipe() {
		return getConfig().getBoolean("Recipe.Enabled", false);
	}

	public final Plugin getSource() {
		return source;
	} //for other Plugins/Addons that register Modifiers

	public final String getName() {
		return this.name;
	}

	public final int getEnchantCost() {
		return getConfig().getInt("EnchantCost", 10);
	}

	public final boolean isEnchantable() {
		return getConfig().getBoolean("Enchantable", false);
	}

	public final NamespacedKey getNamespaceKey() { return namespaceKey; }

	/**
	 * changes the core settings of the Modifier (like a secondary constructor)
	 */
	protected final void init(@NotNull final Material m) {
		FileConfiguration config = getConfig();

		try {
			this.color = ChatWriter.getColor(Objects.requireNonNull(config.getString("Color", "%WHITE%"),
					"Config has no Color-Value!"));
		} catch (IllegalArgumentException | ArrayIndexOutOfBoundsException ignored) {
			this.color = ChatColor.WHITE;
			ChatWriter.logError("Illegal Color detected for Modifier " + this.getKey());
		}

		this.maxLvl = config.getInt("MaxLevel");
		this.slotCost = config.getInt("SlotCost", 1);
		this.minimumLevelRequirement = config.getInt("MinimumToolLevelRequirement", 1);

		if (source.equals(MineTinker.getPlugin())) { //normal Languagesystem-Integration
			String langStart = "Modifier." + getKey();

			this.name = LanguageManager.getString(langStart + ".Name");
			this.description = LanguageManager.getString(langStart + ".Description");

			this.modItem = modManager.createModifierItem(m, this.color + LanguageManager.getString(langStart + ".ModifierItemName"),
					ChatColor.WHITE + LanguageManager.getString(langStart + ".DescriptionModifierItem"), this);
		} else { //use the config values instead
			this.name = config.getString("Name", "");
			this.description = ChatWriter.addColors(Objects.requireNonNull(config.getString("Description", ""),
					"Config has no Description-Value!"));

			this.modItem = modManager.createModifierItem(m, this.color + config.getString("ModifierItemName", ""),
					ChatWriter.addColors(Objects.requireNonNull(config.getString("DescriptionModifierItem", ""),
							"Config has no DescriptionModifierItem-Value!")), this);
		}

		final ItemMeta itemMeta = this.modItem.getItemMeta();
		if (itemMeta != null) itemMeta.setCustomModelData(this.customModelData);
		this.modItem.setItemMeta(itemMeta);
	}

	/**
	 * applies the Modifier to the tool
	 *
	 * @param player the Player
	 * @param tool   the Tool to modify
	 * @return true if successful
	 * false if failure
	 */
	public boolean applyMod(final Player player, final ItemStack tool, final boolean isCommand) {
		return true;
	}

	/**
	 * what should be done to the Tool if the Modifier gets removed
	 *
	 * @param tool the Tool
	 */
	public void removeMod(final ItemStack tool) {
		ItemMeta meta = tool.getItemMeta();

		if (meta != null) {
			for (Enchantment enchantment : getAppliedEnchantments())
				meta.removeEnchant(enchantment);

			for (Attribute attribute : getAppliedAttributes())
				meta.removeAttributeModifier(attribute);

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
	public final boolean isAllowed() {
		return getConfig().getBoolean("Allowed", true);
	}

	public final String getFileName() {
		return fileName;
	}

	/**
	 * @return a list of enchantments that may be applied when the modifier is applied
	 */
	public @NotNull List<Enchantment> getAppliedEnchantments() {
		return Collections.emptyList();
	}

	/**
	 * @return a list of attributes that may be applied when the modifier is applied
	 */
	public @NotNull List<Attribute> getAppliedAttributes() {
		return Collections.emptyList();
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	protected final boolean isMaterialCompatible(@NotNull final Material material) {
		for (final ToolType toolType : getAllowedTools()) {
			if (toolType.contains(material))
				return true;
		}

		return false;
	}

	protected final FileConfiguration getConfig() {
		return ConfigurationManager.getConfig(this);
	}

	protected final void registerCraftingRecipe() {
		if (!hasRecipe()) {
			return;
		}

		final FileConfiguration config = getConfig();
		try {
			final NamespacedKey nkey = new NamespacedKey(MineTinker.getPlugin(), "Modifier_" + getKey());
			final ShapedRecipe newRecipe = new ShapedRecipe(nkey, this.getModItem()); //reload recipe
			final String top = config.getString("Recipe.Top");
			final String middle = config.getString("Recipe.Middle");
			final String bottom = config.getString("Recipe.Bottom");
			final ConfigurationSection materials = config.getConfigurationSection("Recipe.Materials");

			newRecipe.shape(top, middle, bottom); //makes recipe

			if (materials != null) {
				for (final String key : materials.getKeys(false)) {
					final String materialName = materials.getString(key);

					if (materialName == null) {
						ChatWriter.logError(LanguageManager.getString("Modifier.MaterialEntryNotFound"));
						return;
					}

					final HashSet<Material> mats = new HashSet<>();
					for (final String mat : materialName.split(",")) {
						if (mat.isEmpty()) continue;
						final Material m = Material.getMaterial(mat);
						if (m == null) continue;
						mats.add(m);
					}

					if (mats.isEmpty()) {
						ChatWriter.log(false, "Material [" + materialName + "] is null for mod [" + this.name + "]");
						return;
					} else {
						newRecipe.setIngredient(key.charAt(0), new RecipeChoice.MaterialChoice(mats.stream().toList()));
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
			this.namespaceKey = nkey;
		} catch (Exception e) {
			e.printStackTrace();
			ChatWriter.logError("Could not register recipe for the " + this.name + "-Modifier!"); //executes if the recipe could not initialize
		}
	}

	@Contract(value = "null -> false", pure = true)
	public final boolean equals(@Nullable Object o) {
		if (o instanceof Modifier mod)
			return mod.getKey().equals(this.getKey());
		return false;
	}

	// ---------------------- Enchantable Stuff ----------------------

	/**
	 * @param player The player that enchants
	 * @param item The item that will be enchanted
	 */
	public final void enchantItem(@NotNull Player player, @NotNull ItemStack item) {
		if (!player.hasPermission(getCraftPermission())) return;

		final Location location = player.getLocation();
		final World world = location.getWorld();
		final PlayerInventory inventory = player.getInventory();

		if (world == null) return;

		if (player.getGameMode() == GameMode.CREATIVE) {
			if (!inventory.addItem(getModItem()).isEmpty()) { //adds items to (full) inventory
				world.dropItem(location, getModItem());
			} // no else as it gets added in if

			if (MineTinker.getPlugin().getConfig().getBoolean("Sound.OnEnchanting")) {
				player.playSound(location, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 0.5F);
			}

			ChatWriter.log(false, player.getDisplayName() + " created a " + getName() + "-Modifiers in Creative!");
		} else if (player.getLevel() >= getEnchantCost()) {
			int amount = item.getAmount();
			int newLevel = player.getLevel() - getEnchantCost();

			player.setLevel(newLevel);
			item.setAmount(amount - 1);

			if (!inventory.addItem(getModItem()).isEmpty()) { //adds items to (full) inventory
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

	public final String getCraftPermission() {
		return "minetinker.modifiers." + getKey().replace("-", "").toLowerCase() + ".craft";
	}

	public final String getUsePermission() {
		return "minetinker.modifiers." + getKey().replace("-", "").toLowerCase() + ".use";
	}

	public final String getApplyPermission() {
		return "minetinker.modifiers." + getKey().replace("-", "").toLowerCase() + ".apply";
	}

	public List<String> getStatistics(ItemStack item) {
		return null;
	}
}

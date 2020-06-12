package de.flo56958.minetinker.modifiers;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.data.GUIs;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.events.ToolLevelUpEvent;
import de.flo56958.minetinker.listeners.ActionBarListener;
import de.flo56958.minetinker.modifiers.types.*;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.ConfigurationManager;
import de.flo56958.minetinker.utils.LanguageManager;
import de.flo56958.minetinker.utils.data.DataHandler;
import de.flo56958.minetinker.utils.data.UUIDTagType;
import de.flo56958.minetinker.utils.datatypes.Pair;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ModManager {

	private static FileConfiguration config;
	private static FileConfiguration layout;
	private static ModManager instance;

	static {
		config = MineTinker.getPlugin().getConfig();

		layout = ConfigurationManager.getConfig("layout.yml");
		layout.options().copyDefaults(true);
		layout.addDefault("UseRomans.Level", true);
		layout.addDefault("UseRomans.Exp", false);
		layout.addDefault("UseRomans.FreeSlots", false);
		layout.addDefault("UseRomans.ModifierLevels", true);
		layout.addDefault("OverrideLanguagesystem", false);
		layout.addDefault("UsePatternMatcher", false); //for plugin compatibility

		ArrayList<String> loreLayout = new ArrayList<>();
		loreLayout.add("%GOLD%Level %WHITE%%LEVEL%");
		loreLayout.add("%GOLD%Exp: %WHITE%%EXP% / %NEXT_LEVEL_EXP%");
		loreLayout.add("%WHITE%Free Modifier Slots: %FREE_SLOTS%");
		loreLayout.add("%WHITE%Modifiers:");
		loreLayout.add("%MODIFIERS%");
		layout.addDefault("LoreLayout", loreLayout);

		layout.addDefault("ModifierLayout", "%MODIFIER% %WHITE%%MODLEVEL%");

		ConfigurationManager.saveConfig(layout);

		FileConfiguration modifierconfig = ConfigurationManager.getConfig("Modifiers.yml");
		modifierconfig.options().copyDefaults(true);

		List<String> incompatibilityList = new ArrayList<>();
		incompatibilityList.add(SelfRepair.instance().getKey() + ":" + Infinity.instance().getKey());
		incompatibilityList.add(Ender.instance().getKey() + ":" + Infinity.instance().getKey());
		incompatibilityList.add(Ender.instance().getKey() + ":" + MultiShot.instance().getKey());
		incompatibilityList.add(AutoSmelt.instance().getKey() + ":" + SilkTouch.instance().getKey());
		incompatibilityList.add(Luck.instance().getKey() + ":" + SilkTouch.instance().getKey());
		incompatibilityList.add(Protecting.instance().getKey() + ":" + AntiArrowPlating.instance().getKey());
		incompatibilityList.add(Protecting.instance().getKey() + ":" + AntiBlastPlating.instance().getKey());
		incompatibilityList.add(Protecting.instance().getKey() + ":" + Insulating.instance().getKey());
		incompatibilityList.add(AntiBlastPlating.instance().getKey() + ":" + AntiArrowPlating.instance().getKey());
		incompatibilityList.add(Protecting.instance().getKey() + ":" + KineticPlating.instance().getKey());
		incompatibilityList.add(AntiBlastPlating.instance().getKey() + ":" + KineticPlating.instance().getKey());
		incompatibilityList.add(AntiBlastPlating.instance().getKey() + ":" + Insulating.instance().getKey());
		incompatibilityList.add(AntiArrowPlating.instance().getKey() + ":" + Insulating.instance().getKey());
		incompatibilityList.add(KineticPlating.instance().getKey() + ":" + Insulating.instance().getKey());
		incompatibilityList.add(AntiArrowPlating.instance().getKey() + ":" + KineticPlating.instance().getKey());
		incompatibilityList.add(Sharpness.instance().getKey() + ":" + Smite.instance().getKey());
		incompatibilityList.add(Sharpness.instance().getKey() + ":" + SpidersBane.instance().getKey());
		incompatibilityList.add(SpidersBane.instance().getKey() + ":" + Smite.instance().getKey());
		incompatibilityList.add(Aquaphilic.instance().getKey() + ":" + Freezing.instance().getKey());
		incompatibilityList.add(Infinity.instance().getKey() + ":" + Propelling.instance().getKey());
		incompatibilityList.add(MultiShot.instance().getKey() + ":" + Piercing.instance().getKey());
		incompatibilityList.add(Power.instance().getKey() + ":" + Timber.instance().getKey());
		incompatibilityList.add(Drilling.instance().getKey() + ":" + Timber.instance().getKey());
		incompatibilityList.add(SelfRepair.instance().getKey() + ":" + Photosynthesis.instance().getKey());
		incompatibilityList.add(MultiShot.instance().getKey() + ":" + Magical.instance().getKey());
		incompatibilityList.add(Magical.instance().getKey() + ":" + Ender.instance().getKey());
		incompatibilityList.add(Sunblazer.instance().getKey() + ":" + Nightseeker.instance().getKey());
		incompatibilityList.add(Sunblazer.instance().getKey() + ":" + SelfRepair.instance().getKey());
		incompatibilityList.add(Sunblazer.instance().getKey() + ":" + Scotopic.instance().getKey());
		incompatibilityList.add(Sunblazer.instance().getKey() + ":" + ShadowDive.instance().getKey());
		incompatibilityList.add(Photosynthesis.instance().getKey() + ":" + Nightseeker.instance().getKey());
		incompatibilityList.add(Glowing.instance().getKey() + ":" + Nightseeker.instance().getKey());
		incompatibilityList.add(Poisonous.instance().getKey() + ":" + Glowing.instance().getKey());
		incompatibilityList.add(Poisonous.instance().getKey() + ":" + Webbed.instance().getKey());
		incompatibilityList.add(Withered.instance().getKey() + ":" + Poisonous.instance().getKey());
		incompatibilityList.add(Withered.instance().getKey() + ":" + Glowing.instance().getKey());
		incompatibilityList.add(Withered.instance().getKey() + ":" + Webbed.instance().getKey());
		incompatibilityList.add(Withered.instance().getKey() + ":" + Beheading.instance().getKey());
		incompatibilityList.add(Webbed.instance().getKey() + ":" + Glowing.instance().getKey());
		incompatibilityList.add(Shulking.instance().getKey() + ":" + Glowing.instance().getKey());
		incompatibilityList.add(Shulking.instance().getKey() + ":" + Webbed.instance().getKey());
		incompatibilityList.add(Shulking.instance().getKey() + ":" + Poisonous.instance().getKey());
		incompatibilityList.add(Shulking.instance().getKey() + ":" + Withered.instance().getKey());
		incompatibilityList.add(Scotopic.instance().getKey() + ":" + Webbed.instance().getKey());
		incompatibilityList.add(Scotopic.instance().getKey() + ":" + Poisonous.instance().getKey());
		incompatibilityList.add(Scotopic.instance().getKey() + ":" + Withered.instance().getKey());
		incompatibilityList.add(Scotopic.instance().getKey() + ":" + Shulking.instance().getKey());
		incompatibilityList.add(Shrouded.instance().getKey() + ":" + Ender.instance().getKey());
		incompatibilityList.add(Shrouded.instance().getKey() + ":" + MultiShot.instance().getKey());

		incompatibilityList.sort(String::compareToIgnoreCase);

		modifierconfig.addDefault("Incompatibilities", incompatibilityList);
		modifierconfig.addDefault("IncompatibilitiesConsiderEnchants", true);
		modifierconfig.addDefault("CommandIgnoresIncompatibilities", true);
		modifierconfig.addDefault("CommandIgnoresToolTypes", false);
		modifierconfig.addDefault("CommandIgnoresMaxLevel", false);
		modifierconfig.addDefault("IgnoreIncompatibilityIfModifierAlreadyApplied", true);
		modifierconfig.addDefault("UseCustomModelData", false);
		ConfigurationManager.saveConfig(modifierconfig);
	}

	//TODO: AUTO-DISCOVER RECIPES
	public final ArrayList<NamespacedKey> recipe_Namespaces = new ArrayList<>();
	private final HashMap<Modifier, Set<Modifier>> incompatibilities = new HashMap<>();
	/**
	 * stores the list of all MineTinker modifiers
	 */
	private final HashSet<Modifier> allMods = new HashSet<>();
	/**
	 * stores the list of allowed modifiers
	 */
	private final ArrayList<Modifier> mods = new ArrayList<>();
	private String ToolIdentifier;
	private String ArmorIdentifier;
	private List<String> loreScheme;
	private String modifierLayout;

	private boolean allowBookConvert;

	/**
	 * Class constructor (no parameters)
	 */
	private ModManager() {
	}

	/**
	 * get the instance that contains the modifier list (VERY IMPORTANT)
	 *
	 * @return the instance
	 */
	public static @NotNull ModManager instance() {
		synchronized (ModManager.class) {
			if (instance == null) {
				instance = new ModManager();
				instance.init();
			}
		}

		return instance;
	}

	public static @Nullable Pair<@Nullable Material, @NotNull Integer> itemUpgrader(@NotNull Material tool, Material material) {
		String name = tool.name().split("_")[0].toLowerCase();

		if (name.equals("wooden") && material.name().contains("PLANKS")
				|| name.equals("stone") && material == Material.COBBLESTONE
				|| name.equals("iron") && material == Material.IRON_INGOT
				|| name.equals("gold") && material == Material.GOLD_INGOT
				|| name.equals("diamond") && material == Material.DIAMOND
				|| name.equals("leather") && material == Material.LEATHER
				|| name.equals("turtle") && material == Material.SCUTE
				|| name.equals("chainmail") && material == Material.IRON_BARS) {
			return null;
		}

		if (ToolType.SWORD.contains(tool)) {
			return new Pair<>(getToolUpgrade(material, "SWORD"), 2);
		} else if (ToolType.PICKAXE.contains(tool)) {
			return new Pair<>(getToolUpgrade(material, "PICKAXE"), 3);
		} else if (ToolType.AXE.contains(tool)) {
			return new Pair<>(getToolUpgrade(material, "AXE"), 3);
		} else if (ToolType.SHOVEL.contains(tool)) {
			return new Pair<>(getToolUpgrade(material, "SHOVEL"), 1);
		} else if (ToolType.HOE.contains(tool)) {
			return new Pair<>(getToolUpgrade(material, "HOE"), 2);
		} else if (ToolType.HELMET.contains(tool)) {
			return new Pair<>(getArmorUpgrade(material, "HELMET"), 5);
		} else if (ToolType.CHESTPLATE.contains(tool)) {
			return new Pair<>(getArmorUpgrade(material, "CHESTPLATE"), 8);
		} else if (ToolType.LEGGINGS.contains(tool)) {
			return new Pair<>(getArmorUpgrade(material, "LEGGINGS"), 7);
		} else if (ToolType.BOOTS.contains(tool)) {
			return new Pair<>(getArmorUpgrade(material, "BOOTS"), 4);
		}

		return null;
	}

	private static @Nullable Material getToolUpgrade(@NotNull Material material, String tool) {
		switch (material) {
			case ACACIA_PLANKS:
			case BIRCH_PLANKS:
			case DARK_OAK_PLANKS:
			case JUNGLE_PLANKS:
			case OAK_PLANKS:
			case SPRUCE_PLANKS:
				return Material.getMaterial("WOODEN_" + tool);
			case COBBLESTONE:
				return Material.getMaterial("STONE_" + tool);
			case IRON_INGOT:
				return Material.getMaterial("IRON_" + tool);
			case GOLD_INGOT:
				return Material.getMaterial("GOLDEN_" + tool);
			case DIAMOND:
				return Material.getMaterial("DIAMOND_" + tool);
			default:
				return null;
		}
	}

	private static @Nullable Material getArmorUpgrade(@NotNull Material material, String tool) {
		switch (material) {
			case LEATHER:
				return Material.getMaterial("LEATHER_" + tool);
			case IRON_INGOT:
				return Material.getMaterial("IRON_" + tool);
			case GOLD_INGOT:
				return Material.getMaterial("GOLDEN_" + tool);
			case DIAMOND:
				return Material.getMaterial("DIAMOND_" + tool);
			case IRON_BARS:
				return Material.getMaterial("CHAINMAIL_" + tool);
			default:
				return null;
		}
	}

	public void reload() {
		config = MineTinker.getPlugin().getConfig();
		layout = ConfigurationManager.getConfig("layout.yml");

		removeRecipes();
		mods.clear();

		for (Modifier m : allMods) {
			m.reload();

			if (m.isAllowed()) {
				mods.add(m);
			} else {
				mods.remove(m);
			}
		}

		mods.sort(Comparator.comparing(Modifier::getName));

		this.ToolIdentifier = config.getString("ToolIdentifier");
		this.ArmorIdentifier = config.getString("ArmorIdentifier");

		removeRecipes();
		for (Modifier m : this.mods) {
			m.registerCraftingRecipe();
		}

		//get Modifier incompatibilities
		reloadIncompatibilities();

		if (layout.getBoolean("OverrideLanguagesystem", false)) {
			this.loreScheme = layout.getStringList("LoreLayout");

			for (int i = 0; i < loreScheme.size(); i++) {
				loreScheme.set(i, ChatWriter.addColors(loreScheme.get(i)));
			}
		} else {
			this.loreScheme = new ArrayList<>();
			this.loreScheme.add(LanguageManager.getString("Commands.ItemStatistics.Level")
					.replace("%level", "%LEVEL%"));
			this.loreScheme.add(LanguageManager.getString("Commands.ItemStatistics.Exp")
					.replace("%current", "%EXP%")
					.replace("%nextlevel", "%NEXT_LEVEL_EXP%"));
			this.loreScheme.add(LanguageManager.getString("Commands.ItemStatistics.FreeSlots")
					.replace("%slots", "%FREE_SLOTS%"));
			this.loreScheme.add(LanguageManager.getString("Commands.ItemStatistics.Modifiers"));
			this.loreScheme.add("%MODIFIERS%");
		}

		this.modifierLayout = ChatWriter.addColors(layout.getString("ModifierLayout"));
		this.allowBookConvert = config.getBoolean("ConvertBookToModifier");
		GUIs.reload();
	}

	private void reloadIncompatibilities() {
		FileConfiguration modifierconfig = ConfigurationManager.getConfig("Modifiers.yml");

		List<String> possibleKeys = new ArrayList<>();
		for (Modifier m : this.allMods) {
			possibleKeys.add(m.getKey());
		}
		possibleKeys.sort(String::compareToIgnoreCase);
		possibleKeys.add(0, "Do not edit this list; just for documentation of what Keys can be used under Incompatibilities");
		modifierconfig.set("PossibleKeys", possibleKeys);
		ConfigurationManager.saveConfig(modifierconfig);
		ConfigurationManager.loadConfig("", "Modifiers.yml");
		incompatibilities.clear();
		for (Modifier m : this.allMods) {
			incompatibilities.putIfAbsent(m, new HashSet<>());
		}
		modifierconfig = ConfigurationManager.getConfig("Modifiers.yml");
		List<String> incompatibilityList = modifierconfig.getStringList("Incompatibilities");
		for (String s : incompatibilityList) {
			String[] splits = s.split(":");
			if (splits.length != 2) continue;
			Modifier mod1 = null;
			Modifier mod2 = null;
			for (Modifier m : this.allMods) {
				if (m.getKey().equals(splits[0])) {
					mod1 = m;
				}
				if (m.getKey().equals(splits[1])) {
					mod2 = m;
				}
			}

			if (mod1 == null || mod2 == null) continue;
			if (mod1.equals(mod2)) continue; //Modifier can not be incompatible with its self

			incompatibilities.get(mod1).add(mod2);
			incompatibilities.get(mod2).add(mod1);
		}
	}

	public @NotNull HashSet<Modifier> getIncompatibilities(Modifier m) {
		return new HashSet<>(incompatibilities.get(m));
	}

	/**
	 * checks and loads all modifiers with configurations settings into memory
	 */
	private void init() {
		reload();
	}

	/**
	 * register a new modifier to the list
	 *
	 * @param mod the modifier instance
	 */
	@Contract("null -> false")
	public boolean register(Modifier mod) {
		if (mod == null) return false;
		if (!allMods.contains(mod)) {
			mod.reload();
			allMods.add(mod);
			if (mod.isAllowed()) {
				mods.add(mod);
				mods.sort(Comparator.comparing(Modifier::getName));
				mod.registerCraftingRecipe();
			}
			if (mod instanceof Listener) { //Enable Events
				Bukkit.getPluginManager().registerEvents((Listener) mod, MineTinker.getPlugin());
			}
			reloadIncompatibilities();
			if (!mod.getSource().equals(MineTinker.getPlugin())) {
				GUIs.reload();
			}
			ChatWriter.logColor(LanguageManager.getString("ModManager.RegisterModifier")
					.replace("%mod", mod.getColor() + mod.getName())
					.replace("%plugin", mod.getSource().getName()));
			return true;
		}
		return false;
	}

	/**
	 * unregisters the Modifier from the list
	 *
	 * @param mod the modifier instance
	 */
	public void unregister(Modifier mod) {
		if (mod == null) return;
		allMods.remove(mod);
		mods.remove(mod);
		incompatibilities.remove(mod);
		if (mod instanceof Listener) { //Disable Events
			HandlerList.unregisterAll((Listener) mod);
		}
		ChatWriter.logColor(LanguageManager.getString("ModManager.UnregisterModifier")
				.replace("%mod", mod.getColor() + mod.getName())
				.replace("%plugin", MineTinker.getPlugin().getName()));
	}

	/**
	 * should we let the player convert enchanted books to modifier items
	 *
	 * @return
	 */
	@Contract(pure = true)
	public boolean allowBookToModifier() {
		return this.allowBookConvert;
	}

	/**
	 * get all the modifiers in the list
	 *
	 * @return the modifier list
	 */
	public List<Modifier> getAllowedMods() {
		return new ArrayList<>(this.mods);
	}

	public HashSet<Modifier> getAllMods() {
		return new HashSet<>(this.allMods);
	}

	/**
	 * add a specified modifier to a tool
	 *
	 * @param is  the item to add the modifier to
	 * @param mod the modifier to add
	 */
	void addMod(ItemStack is, @NotNull Modifier mod) {
		DataHandler.setTag(is, mod.getKey(), getModLevel(is, mod) + 1, PersistentDataType.INTEGER, false);
		rewriteLore(is);
	}

	public boolean addMod(Player player, ItemStack item, @NotNull Modifier modifier, boolean fromCommand, boolean fromRandom, boolean silent) {
		if (!modifier.getKey().equals(ExtraModifier.instance().getKey())) {
			if (!modifier.checkAndAdd(player, item,
					modifier.getKey().toLowerCase().replace("-", ""), fromCommand, fromRandom, silent)) {
				return false;
			}
		}

		boolean success = modifier.applyMod(player, item, fromCommand);

		if (success) {
			ItemMeta meta = item.getItemMeta();

			if (meta != null) {
				if (MineTinker.getPlugin().getConfig().getBoolean("HideEnchants", true)) {
					meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				} else {
					meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
				}

				if (MineTinker.getPlugin().getConfig().getBoolean("HideAttributes", true)) {
					meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
				} else {
					meta.removeItemFlags(ItemFlag.HIDE_ATTRIBUTES);
				}

				item.setItemMeta(meta);
			}
		}

		return success;
	}

	/**
	 * get the level of a specified modifier on a tool
	 *
	 * @param is  the item
	 * @param mod the modifier
	 */
	public int getModLevel(ItemStack is, @NotNull Modifier mod) {
		if (!mod.isAllowed()) return 0;
		Integer tag = DataHandler.getTag(is, mod.getKey(), PersistentDataType.INTEGER, false);
		if (tag == null) return 0;
		return tag;
	}

	/**
	 * remove a modifier from a tool
	 *
	 * @param is  the item to remove the modifier from
	 * @param mod the modifier to remove
	 */
	public void removeMod(ItemStack is, Modifier mod) {
		if (hasMod(is, mod)) {
			DataHandler.removeTag(is, mod.getKey(), false);
			mod.removeMod(is);
			rewriteLore(is);
		}
	}

	/**
	 * gets how many free slots the tool has
	 *
	 * @param is the item to get the information from
	 */
	public int getFreeSlots(ItemStack is) {
		Integer freeSlots = DataHandler.getTag(is, "FreeSlots", PersistentDataType.INTEGER, false);
		if (freeSlots == null) return 0;
		return freeSlots;
	}

	/**
	 * sets how many free slots the tool has
	 *
	 * @param is the item to set the information to
	 */
	public void setFreeSlots(ItemStack is, int freeSlots) {
		DataHandler.setTag(is, "FreeSlots", freeSlots, PersistentDataType.INTEGER, false);
		rewriteLore(is);
	}

	/**
	 * gets what level the tool has
	 *
	 * @param is the item to get the information from
	 */
	public int getLevel(ItemStack is) {
		Integer level = DataHandler.getTag(is, "Level", PersistentDataType.INTEGER, false);
		if (level == null) return 0;
		return level;
	}

	/**
	 * sets the level of the tool
	 *
	 * @param is the item to get the information from
	 */
	private void setLevel(ItemStack is, int level) {
		DataHandler.setTag(is, "Level", level, PersistentDataType.INTEGER, false);
	}

	/**
	 * gets the amount of exp the tool has
	 *
	 * @param is the item to get the information from
	 */
	public long getExp(ItemStack is) {
		Long exp = DataHandler.getTag(is, "Exp", PersistentDataType.LONG, false);
		if (exp == null) return 0;
		return exp;
	}

	/**
	 * sets the exp amount of the tool
	 *
	 * @param is the item to get the information from
	 */
	private void setExp(ItemStack is, long exp) {
		DataHandler.setTag(is, "Exp", exp, PersistentDataType.LONG, false);
	}

	/**
	 * @param tool The Tool that is checked
	 * @param mod  The modifier that is checked in tool
	 * @return if the tool has the mod
	 */
	public boolean hasMod(ItemStack tool, @NotNull Modifier mod) {
		return mod.isAllowed() && DataHandler.hasTag(tool, mod.getKey(), PersistentDataType.INTEGER, false);
	}

	/**
	 * calculates the required exp for the given level
	 *
	 * @param level
	 * @return long value of the exp required
	 */
	public long getNextLevelReq(int level) {
		if (config.getBoolean("ProgressionIsLinear")) {
			return Math.round(MineTinker.getPlugin().getConfig().getInt("LevelStep")
					* MineTinker.getPlugin().getConfig().getDouble("LevelFactor") * level);
		} else {
			return Math.round(MineTinker.getPlugin().getConfig().getInt("LevelStep")
					* Math.pow(MineTinker.getPlugin().getConfig().getDouble("LevelFactor"), level - 1));
		}
	}

	/**
	 * @param player Player that uses the tool
	 * @param tool   tool that needs to get exp
	 * @param amount how much exp should the tool get
	 */
	public void addExp(Player player, ItemStack tool, long amount) {
		if (amount == 0) {
			return;
		}

		boolean LevelUp = false;

		int level = this.getLevel(tool);
		long exp = this.getExp(tool);

		if (level == -1 || exp == -1) {
			return;
		}

		if (exp + 1 < 0 || level + 1 < 0) {
			if (MineTinker.getPlugin().getConfig().getBoolean("ResetAtIntOverflow")) { //secures a "good" exp-system if the Values get to big
				level = 1;
				setLevel(tool, level);
				exp = 0;
				LevelUp = true;
			} else {
				return;
			}
		}

		exp = exp + amount;
		if (exp >= getNextLevelReq(level)) { //tests for a level up
			level++;
			setLevel(tool, level);
			LevelUp = true;
		}

		if (config.getBoolean("actionbar-on-exp-gain")) {
			ActionBarListener.addXP(player, (int) amount);
		}

		setExp(tool, exp);
		rewriteLore(tool);

		if (LevelUp) {
			Bukkit.getPluginManager().callEvent(new ToolLevelUpEvent(player, tool));
		}
	}

	/**
	 * @param armor the ItemStack
	 * @return if the ItemStack is viable as MineTinker-Armor
	 */
	@Contract("null -> false")
	public boolean isArmorViable(ItemStack armor) {
		return armor != null && DataHandler.hasTag(armor, this.ArmorIdentifier, PersistentDataType.INTEGER, false);
	}

	/**
	 * @param tool the ItemStack
	 * @return if the ItemStack is viable as MineTinker-Tool
	 */
	@Contract("null -> false")
	public boolean isToolViable(ItemStack tool) {
		return tool != null && DataHandler.hasTag(tool, this.ToolIdentifier, PersistentDataType.INTEGER, false);
	}

	/**
	 * @param wand the ItemStack
	 * @return if the ItemStack is viable as MineTinker-Builderswand
	 */
	@Contract("null -> false")
	public boolean isWandViable(ItemStack wand) {
		return wand != null && DataHandler.hasTag(wand, "identifier_builderswand", PersistentDataType.INTEGER, false);
	}

	/**
	 * Updates the lore of the Item as everything is stored in the NBT-Data
	 *
	 * @param is The Itemstack to rewrite the Lore
	 */
	private void rewriteLore(ItemStack is) {
		if (!MineTinker.getPlugin().getConfig().getBoolean("EnableLore")) {
			return;
		}

		ArrayList<String> lore = new ArrayList<>(this.loreScheme);

		long exp = getExp(is);
		int level = getLevel(is);
		long nextLevelReq = getNextLevelReq(level);
		int freeSlots = getFreeSlots(is);

		String exp_ = layout.getBoolean("UseRomans.Exp") ? ChatWriter.toRomanNumerals((int) exp) : String.valueOf(exp);
		String level_ = layout.getBoolean("UseRomans.Level") ? ChatWriter.toRomanNumerals(level) : String.valueOf(level);
		String nextLevelReq_ = layout.getBoolean("UseRomans.Exp") ? ChatWriter.toRomanNumerals((int) nextLevelReq) : String.valueOf(nextLevelReq);
		String freeSlots_ = layout.getBoolean("UseRomans.FreeSlots") ? ChatWriter.toRomanNumerals(freeSlots) : String.valueOf(freeSlots);

		for (int i = 0; i < lore.size(); i++) {
			String s = lore.get(i);
			s = s.replaceAll("%EXP%", String.valueOf(exp_));
			s = s.replaceAll("%LEVEL%", String.valueOf(level_));
			s = s.replaceAll("%NEXT_LEVEL_EXP%", String.valueOf(nextLevelReq_));
			s = s.replaceAll("%FREE_SLOTS%", String.valueOf(freeSlots_));

			s = ChatColor.WHITE + s;
			lore.set(i, s);
		}

		int index = -1;
		for (int i = 0; i < lore.size(); i++) {
			String s = lore.get(i);

			if (s.contains("%MODIFIERS%")) {
				index = i;
				break;
			}
		}

		if (index == -1) {
			return;
		}

		lore.remove(index);

		for (Modifier m : this.mods) {
			if (DataHandler.hasTag(is, m.getKey(), PersistentDataType.INTEGER, false)) {
				int modLevel = getModLevel(is, m);
				String modLevel_ = layout.getBoolean("UseRomans.ModifierLevels")
						? ChatWriter.toRomanNumerals(modLevel) : String.valueOf(modLevel);

				String s = this.modifierLayout;
				s = s.replaceAll("%MODIFIER%", m.getColor() + m.getName());
				s = s.replaceAll("%MODLEVEL%", modLevel_);

				lore.add(index++, s);
			}
		}

		ItemMeta meta = is.getItemMeta();

		if (meta != null) {
			if (layout.getBoolean("UsePatternMatcher", false)) {
				List<String> oldLore = meta.getLore();
				if (oldLore != null) {
					//clean up lore from old MineTinker-Lore
					ArrayList<String> toRemove = new ArrayList<>();
					for (String s : oldLore) {
						boolean removed = false;
						for (String m : this.loreScheme) {
							if (s.matches("[§f]{0,2}" +
									m.replace("%LEVEL%", "[a-zA-Z0-9&§]+?")
											.replace("%EXP%", "[a-zA-Z0-9&§]+?")
											.replace("%FREE_SLOTS%", "[a-zA-Z0-9&§]+?")
											.replace("%NEXT_LEVEL_EXP%", "[a-zA-Z0-9&§]+?"))) {
								toRemove.add(s);
								removed = true;
								break;
							}
						}
						if (removed) continue;
						for (Modifier m : this.mods) {
							if (s.matches("[§f]{0,2}" +
									this.modifierLayout.replace("%MODIFIER%",
											"[a-zA-Z0-9&§]*" + m.getName())
											.replace("%MODLEVEL%", "[a-zA-Z0-9&§]+?"))) {
								toRemove.add(s);
								break;
							}
						}
					}

					if (toRemove.size() > 0) {
						int startIndex = oldLore.indexOf(toRemove.get(0));
						//add Lore that was before MineTinker in front of it again
						for (int i = 0; i < startIndex; i++) {
							lore.add(i, oldLore.get(0));
							oldLore.remove(0);
						}
						oldLore.removeAll(toRemove);
					}

					//add not MineTinker-Lore
					lore.addAll(oldLore);
				}
			}

			meta.setLore(lore);
			is.setItemMeta(meta);
		}
	}

	/**
	 * converts a given ItemStack into its MineTinker equivalent
	 *
	 * @param is The {@link ItemStack} to convert.
	 * @return If the conversion was successful. Also returns false if the item is already MT compatible.
	 */
	@Contract("null, _ -> false")
	public boolean convertItemStack(ItemStack is, @Nullable Entity entity) {
		if (is == null) return false;

		Material m = is.getType();
		int damage = 0;

		// Don't convert already converted items
		if (isArmorViable(is) || isToolViable(is) || isWandViable(is)) {
			return false;
		}

		if (is.getItemMeta() instanceof Damageable) {
			damage = ((Damageable) is.getItemMeta()).getDamage();
		}

		if (!ToolType.ALL.contains(m)) {
			return false;
		}

		if (!MineTinker.getPlugin().getConfig().getBoolean("ConvertEnchantsAndAttributes")) {
			ItemMeta meta = new ItemStack(is.getType(), is.getAmount()).getItemMeta();

			if (meta instanceof Damageable) {
				((Damageable) meta).setDamage(damage);
			}

			is.setItemMeta(meta);
		}

		boolean eligible = false;
		if ((ToolType.AXE.contains(m)
				|| ToolType.BOW.contains(m)
				|| ToolType.CROSSBOW.contains(m)
				|| ToolType.HOE.contains(m)
				|| ToolType.PICKAXE.contains(m)
				|| ToolType.SHOVEL.contains(m)
				|| ToolType.SWORD.contains(m)
				|| ToolType.TRIDENT.contains(m)
				|| ToolType.SHEARS.contains(m)
				|| ToolType.SHIELD.contains(m)
				|| ToolType.FISHINGROD.contains(m)) && !isWandViable(is)) {
			DataHandler.setTag(is, this.ToolIdentifier, 56958, PersistentDataType.INTEGER, false);
			eligible = true;
		}
		if (ToolType.BOOTS.contains(m)
				|| ToolType.CHESTPLATE.contains(m)
				|| ToolType.HELMET.contains(m)
				|| ToolType.LEGGINGS.contains(m)
				|| ToolType.ELYTRA.contains(m)
				|| ToolType.SHIELD.contains(m)) {
			DataHandler.setTag(is, this.ArmorIdentifier, 56958, PersistentDataType.INTEGER, false);
			eligible = true;
		}

		if (!eligible) return false;

		if (entity != null) setCreator(is, entity);
		DataHandler.setTag(is, "creation_date", System.currentTimeMillis(), PersistentDataType.LONG, false); //Set creation date
		setExp(is, 0);
		setLevel(is, 1);
		setFreeSlots(is, config.getInt("StartingModifierSlots"));
		rewriteLore(is);

		addArmorAttributes(is);

		ItemMeta meta = is.getItemMeta();

		if (meta != null) {
			if (MineTinker.getPlugin().getConfig().getBoolean("ConvertEnchantsAndAttributes")) {

				for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
					Modifier modifier = getModifierFromEnchantment(entry.getKey());

					if (modifier == null) {
						continue;
					}

					meta.removeEnchant(entry.getKey());

					for (int i = 0; i < entry.getValue(); i++) { //possible to go over MaxLevel of the mod
						addMod(is, modifier);
						modifier.applyMod(null, is, true); //Player is only required with Extra-Modifier (not possible here)
					}
				}

				if (meta.getAttributeModifiers() == null) {
					return true;
				}

				for (Map.Entry<Attribute, Collection<AttributeModifier>> entry : meta.getAttributeModifiers().asMap().entrySet()) {
					Modifier modifier = getModifierFromAttribute(entry.getKey());

					if (modifier == null || modifier == Hardened.instance()) {
						continue;
					}

					meta.removeAttributeModifier(entry.getKey());

					addMod(is, modifier);
				}
			}
		}
		return true;
	}

	private void setCreator(ItemStack is, Entity entity) {
		if (is == null || entity == null) return;
		DataHandler.setTag(is, "creator", entity.getUniqueId(), UUIDTagType.instance, false);
	}

	public OfflinePlayer getCreator(ItemStack is) {
		if (is == null) return null;
		UUID creator = DataHandler.getTag(is, "creator", UUIDTagType.instance, false);
		if (creator == null) return null;
		return Bukkit.getOfflinePlayer(creator);
	}

	public void addArmorAttributes(@NotNull ItemStack is) {
		double armor = 0.0d;
		double toughness = 0.0d;

		switch (is.getType()) {
			case LEATHER_BOOTS:
			case CHAINMAIL_BOOTS:
			case GOLDEN_BOOTS:
			case LEATHER_HELMET:
				armor = 1.0d;
				break;
			case IRON_BOOTS:
			case CHAINMAIL_HELMET:
			case IRON_HELMET:
			case GOLDEN_HELMET:
			case TURTLE_HELMET:
			case LEATHER_LEGGINGS:
				armor = 2.0d;
				break;
			case DIAMOND_BOOTS:
			case DIAMOND_HELMET:
				armor = 3.0d;
				toughness = 2.0d;
				break;
			case LEATHER_CHESTPLATE:
			case GOLDEN_LEGGINGS:
				armor = 3.0d;
				break;
			case CHAINMAIL_CHESTPLATE:
			case GOLDEN_CHESTPLATE:
			case IRON_LEGGINGS:
				armor = 5.0d;
				break;
			case IRON_CHESTPLATE:
				armor = 6.0d;
				break;
			case DIAMOND_CHESTPLATE:
				armor = 8.0d;
				toughness = 2.0d;
				break;
			case CHAINMAIL_LEGGINGS:
				armor = 4.0d;
				break;
			case DIAMOND_LEGGINGS:
				armor = 6.0d;
				toughness = 2.0d;
				break;
		}

		ItemMeta meta = is.getItemMeta();

		if (meta != null) {
			AttributeModifier armorAM;
			AttributeModifier toughnessAM;

			if (ToolType.BOOTS.contains(is.getType())) {
				armorAM = new AttributeModifier(UUID.randomUUID(), "generic.armor", armor, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET);
				toughnessAM = new AttributeModifier(UUID.randomUUID(), "generic.armorToughness", toughness, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET);
			} else if (ToolType.CHESTPLATE.contains(is.getType())) {
				armorAM = new AttributeModifier(UUID.randomUUID(), "generic.armor", armor, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST);
				toughnessAM = new AttributeModifier(UUID.randomUUID(), "generic.armorToughness", toughness, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST);
			} else if (ToolType.HELMET.contains(is.getType())) {
				armorAM = new AttributeModifier(UUID.randomUUID(), "generic.armor", armor, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HEAD);
				toughnessAM = new AttributeModifier(UUID.randomUUID(), "generic.armorToughness", toughness, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HEAD);
			} else if (ToolType.LEGGINGS.contains(is.getType())) {
				armorAM = new AttributeModifier(UUID.randomUUID(), "generic.armor", armor, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS);
				toughnessAM = new AttributeModifier(UUID.randomUUID(), "generic.armorToughness", toughness, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS);
			} else if (ToolType.ELYTRA.contains(is.getType())) {
				armorAM = null;
				toughnessAM = null;
			} else return;

			if (armor > 0.0d) {
				meta.removeAttributeModifier(Attribute.GENERIC_ARMOR);
				meta.addAttributeModifier(Attribute.GENERIC_ARMOR, armorAM);
			}

			if (toughness > 0.0d) {
				meta.removeAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS);
				meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, toughnessAM);
			}

			if (MineTinker.getPlugin().getConfig().getBoolean("HideAttributes")) {
				meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			} else {
				meta.removeItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			}

			is.setItemMeta(meta);

			Hardened.instance().reapplyAttributes(is);
		}
	}

	public @NotNull ItemStack createModifierItem(@NotNull Material m, @NotNull String name, @NotNull String description, @NotNull Modifier mod) {
		ItemStack is = new ItemStack(m, 1);
		ItemMeta meta = is.getItemMeta();

		if (meta != null) {
			meta.setDisplayName(name);

			ArrayList<String> lore = new ArrayList<>();
			lore.addAll(ChatWriter.splitString(description, 40));
			meta.setLore(lore);

			meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);

			is.setItemMeta(meta);
		}

		DataHandler.setTag(is, "modifier_item", mod.getKey(), PersistentDataType.STRING, false);
		//TODO: DataHandler.setStringList(is, "CanPlaceOn", true, "minecraft:air");

		return is;
	}

	/**
	 * @param item the ItemStack
	 * @return if the ItemStack is viable as MineTinker-Modifier-Item
	 */
	@Contract("null -> false")
	public boolean isModifierItem(ItemStack item) {
		if (item == null) return false;
		return DataHandler.hasTag(item, "modifier_item", PersistentDataType.STRING, false);
	}

	/**
	 * @param item the ItemStack
	 * @return the Modifier of the Modifier-Item (NULL if not found)
	 */
	@Nullable
	public Modifier getModifierFromItem(ItemStack item) {
		if (!isModifierItem(item)) {
			return null;
		}

		if (!DataHandler.hasTag(item, "modifier_item", PersistentDataType.STRING, false)) {
			return null;
		}

		String name = DataHandler.getTag(item, "modifier_item", PersistentDataType.STRING, false);

		if (name == null) {
			return null;
		}

		for (Modifier m : mods) {
			if (m.getKey().equals(name)) {
				return m;
			}
		}

		return null;
	}

	/**
	 * Gets the first found modifier that applies the supplied enchantment.
	 *
	 * @param enchantment The enchantment to get the Modifier for
	 * @return the Modifier or null
	 */
	@Nullable
	public Modifier getModifierFromEnchantment(@NotNull Enchantment enchantment) {
		for (Modifier modifier : getAllMods()) {
			if (modifier.getAppliedEnchantments().contains(enchantment)) {
				return modifier;
			}
		}

		return null;
	}

	/**
	 * Gets the first found modifier that applies the supplied attribute.
	 *
	 * @param attribute
	 * @return the Modifier or null
	 */
	@Nullable
	public Modifier getModifierFromAttribute(@NotNull Attribute attribute) {
		for (Modifier modifier : getAllMods()) {
			if (modifier.getAppliedAttributes().contains(attribute)) {
				return modifier;
			}
		}

		return null;
	}

	/**
	 * Checks the durability of the Tool
	 *
	 * @param cancellable the Event (implements Cancelable)
	 * @param player      the Player
	 * @param tool        the Tool
	 * @return false: if broken; true: if enough durability
	 */
	public boolean durabilityCheck(@NotNull Cancellable cancellable, @NotNull Player player, @NotNull ItemStack tool) {
		ItemMeta meta = tool.getItemMeta();

		if (meta instanceof Damageable) {
			if (config.getBoolean("UnbreakableTools", true)
					&& tool.getType().getMaxDurability() - ((Damageable) meta).getDamage() <= 2) {
				cancellable.setCancelled(true);

				if (config.getBoolean("Sound.OnBreaking", true)) {
					player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.5F, 0.5F);
				}
				return false;
			}
		}
		return true;
	}

	public void removeRecipes() {
		Iterator<Recipe> it = Bukkit.getServer().recipeIterator();
		//TODO: Find a different way to remove recipes! Bukkit is bugged atm

		while (it.hasNext()) {
			ItemStack result = it.next().getResult();

			//Modifieritems
			if (ModManager.instance().isModifierItem(result)) {
				it.remove();
				continue;
			}

			//Builderswands
			if (ModManager.instance().isWandViable(result)) {
				it.remove();
			}

		}

		ModManager.instance().recipe_Namespaces.clear();
	}
}
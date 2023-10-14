package de.flo56958.minetinker.modifiers;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.api.events.ToolLevelUpEvent;
import de.flo56958.minetinker.data.GUIs;
import de.flo56958.minetinker.data.ToolType;
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
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ModManager {

	private static FileConfiguration config;
	public static FileConfiguration layout;
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

		final ArrayList<String> loreLayout = new ArrayList<>();
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
		//incompatibilityList.add(Drilling.instance().getKey() + ":" + Power.instance().getKey());
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
		incompatibilityList.add(Homing.instance().getKey() + ":" + Ender.instance().getKey());
		incompatibilityList.add(Homing.instance().getKey() + ":" + MultiShot.instance().getKey());
		incompatibilityList.add(Shulking.instance().getKey() + ":" + Undead.instance().getKey());
		incompatibilityList.add(Poisonous.instance().getKey() + ":" + Undead.instance().getKey());
		incompatibilityList.add(Thorned.instance().getKey() + ":" + Undead.instance().getKey());
		incompatibilityList.add(Webbed.instance().getKey() + ":" + Undead.instance().getKey());
		incompatibilityList.add(Withered.instance().getKey() + ":" + Undead.instance().getKey());
		incompatibilityList.add(MultiShot.instance().getKey() + ":" + Explosive.instance().getKey());
		incompatibilityList.add(Ender.instance().getKey() + ":" + Explosive.instance().getKey());
		incompatibilityList.add(Sharpness.instance().getKey() + ":" + Explosive.instance().getKey());
		incompatibilityList.add(Magical.instance().getKey() + ":" + Explosive.instance().getKey());
		incompatibilityList.add(Piercing.instance().getKey() + ":" + Explosive.instance().getKey());
		incompatibilityList.add(Channeling.instance().getKey() + ":" + Explosive.instance().getKey());

		Plugin plugin = Bukkit.getPluginManager().getPlugin("ProtocolLib");
		if (plugin != null && plugin.isEnabled())
			incompatibilityList.add(Echoing.instance().getKey() + ":" + Undead.instance().getKey());

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

	public final ArrayList<NamespacedKey> recipe_Namespaces = new ArrayList<>();
	private final HashMap<Modifier, Set<Modifier>> incompatibilities = new HashMap<>();
	/**
	 * stores the list of all MineTinker modifiers
	 */
	private final HashSet<Modifier> allMods = new HashSet<>();
	private final HashMap<String, Modifier> modKeys = new HashMap<>();
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
	private ModManager() {}

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

	public static @Nullable Pair<@Nullable Material, @NotNull Integer> itemUpgrader(@NotNull final Material tool, @NotNull final Material material) {
		String name = tool.name().split("_")[0].toLowerCase();

		if (name.equals("wooden") && material.name().contains("PLANKS")
				|| name.equals("stone") && material == Material.COBBLESTONE
				|| name.equals("iron") && material == Material.IRON_INGOT
				|| name.equals("gold") && material == Material.GOLD_INGOT
				|| name.equals("diamond") && material == Material.DIAMOND
				|| name.equals("leather") && material == Material.LEATHER
				|| name.equals("turtle") && material == Material.SCUTE
				|| name.equals("chainmail") && material == Material.IRON_BARS
				|| name.equals("netherite") && material == Material.NETHERITE_INGOT)
			return null;

		// upgrading from diamond to netherrite should only require one material
		final boolean reduceToOne = name.equals("diamond") && material == Material.NETHERITE_INGOT;
		return switch(ToolType.get(tool)) {
			case AXE -> new Pair<>(getToolUpgrade(material, "AXE"), reduceToOne ? 1 : 3);
			case BOOTS -> new Pair<>(getArmorUpgrade(material, "BOOTS"), reduceToOne ? 1 : 4);
			case CHESTPLATE -> new Pair<>(getArmorUpgrade(material, "CHESTPLATE"), reduceToOne ? 1 : 8);
			case HELMET -> new Pair<>(getArmorUpgrade(material, "HELMET"), reduceToOne ? 1 : 5);
			case HOE -> new Pair<>(getToolUpgrade(material, "HOE"), reduceToOne ? 1 : 2);
			case LEGGINGS -> new Pair<>(getArmorUpgrade(material, "LEGGINGS"), reduceToOne ? 1 : 7);
			case PICKAXE -> new Pair<>(getToolUpgrade(material, "PICKAXE"), reduceToOne ? 1 : 3);
			case SHOVEL -> new Pair<>(getToolUpgrade(material, "SHOVEL"), 1);
			case SWORD -> new Pair<>(getToolUpgrade(material, "SWORD"), reduceToOne ? 1 : 2);
			default -> null;
		};
	}

	private static @Nullable Material getToolUpgrade(@NotNull final Material material, @NotNull final String tool) {
		return switch (material) {
			case ACACIA_PLANKS, BIRCH_PLANKS, DARK_OAK_PLANKS, JUNGLE_PLANKS, OAK_PLANKS, SPRUCE_PLANKS
					-> Material.getMaterial("WOODEN_" + tool);
			case COBBLESTONE -> Material.getMaterial("STONE_" + tool);
			case DIAMOND -> Material.getMaterial("DIAMOND_" + tool);
			case GOLD_INGOT -> Material.getMaterial("GOLDEN_" + tool);
			case IRON_INGOT -> Material.getMaterial("IRON_" + tool);
			case NETHERITE_INGOT -> Material.getMaterial("NETHERITE_" + tool);
			default -> null;
		};
	}

	private static @Nullable Material getArmorUpgrade(@NotNull final Material material, @NotNull final String tool) {
		return switch (material) {
			case DIAMOND -> Material.getMaterial("DIAMOND_" + tool);
			case GOLD_INGOT -> Material.getMaterial("GOLDEN_" + tool);
			case IRON_BARS -> Material.getMaterial("CHAINMAIL_" + tool);
			case IRON_INGOT -> Material.getMaterial("IRON_" + tool);
			case LEATHER -> Material.getMaterial("LEATHER_" + tool);
			case NETHERITE_INGOT -> Material.getMaterial("NETHERITE_" + tool);
			default -> null;
		};
	}

	public List<Modifier> getToolMods(ItemStack tool) {
		ArrayList<Modifier> mods = new ArrayList<>(this.mods);
		mods.removeIf(mod -> !this.hasMod(tool, mod));
		return mods;
	}

	public void reload() {
		config = MineTinker.getPlugin().getConfig();
		layout = ConfigurationManager.getConfig("layout.yml");

		removeRecipes();
		mods.clear();
		mods.addAll(allMods);
		mods.removeIf(mod -> !mod.isAllowed());

		mods.sort(Comparator.comparing(Modifier::getName));

		this.ToolIdentifier = config.getString("ToolIdentifier");
		this.ArmorIdentifier = config.getString("ArmorIdentifier");

		removeRecipes();
		this.mods.forEach(Modifier::registerCraftingRecipe);

		//get Modifier incompatibilities
		this.reloadIncompatibilities();

		if (layout.getBoolean("OverrideLanguagesystem", false)) {
			this.loreScheme = layout.getStringList("LoreLayout");

			loreScheme.replaceAll(ChatWriter::addColors);
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

		this.modifierLayout = ChatWriter.addColors(Objects.requireNonNull(layout.getString("ModifierLayout"), "ModifierLayout is null!"));
		this.allowBookConvert = config.getBoolean("ConvertBookToModifier");
		GUIs.reload();
	}

	private void reloadIncompatibilities() {
		FileConfiguration modifierconfig = ConfigurationManager.getConfig("Modifiers.yml");

		final List<String> possibleKeys = new ArrayList<>();
		this.allMods.forEach(m -> possibleKeys.add(m.getKey()));
		possibleKeys.sort(String::compareToIgnoreCase);
		possibleKeys.add(0, "Do not edit this list; just for documentation of what Keys can be used under Incompatibilities");
		modifierconfig.set("PossibleKeys", possibleKeys);
		ConfigurationManager.saveConfig(modifierconfig);
		ConfigurationManager.loadConfig("", "Modifiers.yml");
		incompatibilities.clear();
		this.allMods.forEach(m -> incompatibilities.putIfAbsent(m, new HashSet<>()));
		modifierconfig = ConfigurationManager.getConfig("Modifiers.yml");
		final List<String> incompatibilityList = modifierconfig.getStringList("Incompatibilities");
		incompatibilityList.forEach(s -> {
			final String[] splits = s.split(":");
			if (splits.length != 2) return;
			final Modifier mod1 = this.allMods.stream()
					.filter(m -> m.getKey().equals(splits[0]))
					.findFirst()
					.orElse(null);

			final Modifier mod2 = this.allMods.stream()
					.filter(m -> m.getKey().equals(splits[1]))
					.findFirst()
					.orElse(null);

			if (mod1 == null || mod2 == null) return;
			if (mod1.equals(mod2)) return; //Modifier can not be incompatible with itself
			if (!mod1.isAllowed() || !mod2.isAllowed()) return; //not enabled Modifiers should not be listed

			//Cross-link incompatibilities
			incompatibilities.get(mod1).add(mod2);
			incompatibilities.get(mod2).add(mod1);
		});

		//Make the incompatibilities unmodifiable
		incompatibilities.replaceAll((m, set) -> Collections.unmodifiableSet(set));
	}

	/**
	 * This Method returns the original Set.
	 * @param m The modifier to get the Incompatibilities for
	 * @return The incompatibilities
	 */
	public @NotNull Set<Modifier> getIncompatibilities(final Modifier m) {
		return incompatibilities.getOrDefault(m, new HashSet<>());
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
	 * @throws IllegalArgumentException if the modifier is already registered
	 * 									or if a modifier with the same CustomModelData is already registered
	 * 									or if a modifier with the same Key is already registered
	 */
	@SuppressWarnings("UnusedReturnValue")
	@Contract("null -> false")
	public boolean register(@Nullable final Modifier mod) {
		if (mod == null) return false;
		if (allMods.contains(mod)) throw new IllegalArgumentException("Modifier " + mod.getKey() + " already registered!");
		if (allMods.stream().filter(m -> m.customModelData == mod.customModelData).findFirst().orElse(null) != null)
			throw new IllegalArgumentException("Modifier " + mod.getKey() + " with same CustomModelData " + mod.customModelData + " already registered!");
		if (modKeys.containsKey(mod.getKey())) throw new IllegalArgumentException("Modifier with Key " + mod.getKey() + " already registered!");

		modKeys.put(mod.getKey(), mod);
		mod.reload();
		allMods.add(mod);
		if (mod.isAllowed()) {
			mods.add(mod);
			mods.sort(Comparator.comparing(Modifier::getName));
			mod.registerCraftingRecipe();
			if (mod instanceof Listener listener) //Enable Events
				Bukkit.getPluginManager().registerEvents(listener, MineTinker.getPlugin());
		}
		reloadIncompatibilities();
		if (!mod.getSource().equals(MineTinker.getPlugin())) GUIs.reload();

		ChatWriter.logColor(LanguageManager.getString("ModManager.RegisterModifier")
				.replace("%mod", mod.getColor() + mod.getName())
				.replace("%plugin", mod.getSource().getName()));
		return true;
	}

	/**
	 * unregisters the Modifier from the list
	 *
	 * @param mod the modifier instance
	 * @throws IllegalArgumentException if the modifier is not registered
	 */
	public void unregister(@NotNull final Modifier mod) {
		if (!allMods.contains(mod)) throw new IllegalArgumentException("Modifier not registered!");
		allMods.remove(mod);
		mods.remove(mod);
		incompatibilities.remove(mod);
		modKeys.remove(mod.getKey());
		if (mod instanceof Listener listener) //Disable Events
			HandlerList.unregisterAll(listener);

		ChatWriter.logColor(LanguageManager.getString("ModManager.UnregisterModifier")
				.replace("%mod", mod.getColor() + mod.getName())
				.replace("%plugin", MineTinker.getPlugin().getName()));
	}

	/**
	 * should we let the player convert enchanted books to modifier items
	 *
	 * @return the value
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
	public @NotNull List<Modifier> getAllowedMods() {
		return new ArrayList<>(this.mods);
	}

	public @NotNull HashSet<Modifier> getAllMods() {
		return new HashSet<>(this.allMods);
	}

	/**
	 * add a specified modifier to a tool
	 *
	 * @param is  the item to add the modifier to
	 * @param mod the modifier to add
	 */
	void addMod(@NotNull final ItemStack is, @NotNull final Modifier mod) {
		DataHandler.setTag(is, mod.getKey(), getModLevel(is, mod) + 1, PersistentDataType.INTEGER, false);
		rewriteLore(is);
	}

	public boolean addMod(final Player player, @NotNull final ItemStack item, @NotNull final Modifier modifier, final boolean fromCommand, final boolean fromRandom, final boolean silent, final boolean modifySlotCount) {
		if (!modifier.getKey().equals(ExtraModifier.instance().getKey())
				&& !modifier.checkAndAdd(player, item, modifier.getKey().toLowerCase().replace("-", ""),
											fromCommand, fromRandom, silent, modifySlotCount))
				return false;

		// apply modifier
		if (!modifier.applyMod(player, item, fromCommand)) return false;

		ItemMeta meta = item.getItemMeta();

		if (meta == null) return true;

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
		return true;
	}

	/**
	 * get the level of a specified modifier on a tool. 0 on failure
	 *
	 * @param is  the item
	 * @param mod the modifier
	 */
	public int getModLevel(@NotNull final ItemStack is, @NotNull final Modifier mod) {
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
	public void removeMod(@NotNull final ItemStack is, @NotNull final Modifier mod) {
		if (!hasMod(is, mod)) return;

		DataHandler.removeTag(is, mod.getKey(), false);
		mod.removeMod(is);
		rewriteLore(is);
	}

	/**
	 * gets how many free slots the tool has
	 *
	 * @param is the item to get the information from
	 */
	public int getFreeSlots(@NotNull final ItemStack is) {
		Integer freeSlots = DataHandler.getTag(is, "FreeSlots", PersistentDataType.INTEGER, false);
		if (freeSlots == null) return 0;
		return freeSlots;
	}

	/**
	 * sets how many free slots the tool has
	 *
	 * @param is the item to set the information to
	 */
	public void setFreeSlots(@NotNull final ItemStack is, final int freeSlots) {
		DataHandler.setTag(is, "FreeSlots", freeSlots, PersistentDataType.INTEGER, false);
		rewriteLore(is);
	}

	/**
	 * gets what level the tool has
	 *
	 * @param is the item to get the information from
	 */
	public int getLevel(@NotNull final ItemStack is) {
		Integer level = DataHandler.getTag(is, "Level", PersistentDataType.INTEGER, false);
		if (level == null) return -1;
		return level;
	}

	/**
	 * sets the level of the tool
	 *
	 * @param is the item to get the information from
	 */
	private void setLevel(@NotNull final ItemStack is, final int level) {
		DataHandler.setTag(is, "Level", level, PersistentDataType.INTEGER, false);
	}

	/**
	 * gets the amount of exp the tool has
	 *
	 * @param is the item to get the information from
	 */
	public long getExp(@NotNull final ItemStack is) {
		Long exp = DataHandler.getTag(is, "Exp", PersistentDataType.LONG, false);
		if (exp == null) return -1;
		return exp;
	}

	/**
	 * sets the exp amount of the tool
	 *
	 * @param is the item to get the information from
	 */
	private void setExp(@Nullable final ItemStack is, final long exp) {
		if (is == null) return;
		DataHandler.setTag(is, "Exp", exp, PersistentDataType.LONG, false);
	}

	/**
	 * @param tool The Tool that is checked
	 * @param mod  The modifier that is checked in tool
	 * @return if the tool has the mod
	 */
	@Contract("null, _ -> false")
	public boolean hasMod(@Nullable final ItemStack tool, @NotNull final Modifier mod) {
		if (tool == null) return false;
		return mod.isAllowed() && DataHandler.hasTag(tool, mod.getKey(), PersistentDataType.INTEGER, false);
	}

	/**
	 * calculates the required exp for the given level
	 *
	 * @param level the level to get
	 * @return long value of the exp required
	 */
	public long getNextLevelReq(final int level) {
		if (config.getBoolean("ProgressionIsLinear"))
			return Math.round(MineTinker.getPlugin().getConfig().getInt("LevelStep")
					* MineTinker.getPlugin().getConfig().getDouble("LevelFactor") * level);

		return Math.round(MineTinker.getPlugin().getConfig().getInt("LevelStep")
					* Math.pow(MineTinker.getPlugin().getConfig().getDouble("LevelFactor"), level - 1));
	}

	/**
	 * @param player Player that uses the tool
	 * @param tool   tool that needs to get exp
	 * @param amount how much exp should the tool get
	 */
	public void addExp(@Nullable final Player player, @NotNull final ItemStack tool, final long amount, final boolean callLevelUpEvent) {
		if (amount == 0) return;

		int level = this.getLevel(tool);
		long exp = this.getExp(tool);

		if (level == -1 || exp == -1) return;

		if (exp + 1 < 0 || level + 1 < 0) {
			// secures a "good" exp-system if the Values get to big
			if (!MineTinker.getPlugin().getConfig().getBoolean("ResetAtIntOverflow")) return;

			level = 1;
			setLevel(tool, level);
			exp = 0;
		}

		exp = exp + amount;
		while (exp >= getNextLevelReq(level)) { // tests for a level up
			level++;
			setLevel(tool, level);
			if (callLevelUpEvent)
				Bukkit.getPluginManager().callEvent(new ToolLevelUpEvent(player, tool));
		}

		if (config.getBoolean("actionbar-on-exp-gain"))
			ActionBarListener.addXP(player, (int) amount);

		setExp(tool, exp);
		rewriteLore(tool);
	}

	/**
	 * @param armor the ItemStack
	 * @return if the ItemStack is viable as MineTinker-Armor
	 */
	@Contract("null -> false")
	public boolean isArmorViable(@Nullable final ItemStack armor) {
		return armor != null && DataHandler.hasTag(armor, this.ArmorIdentifier,
										PersistentDataType.INTEGER, false);
	}

	/**
	 * @param tool the ItemStack
	 * @return if the ItemStack is viable as MineTinker-Tool
	 */
	@Contract("null -> false")
	public boolean isToolViable(@Nullable final ItemStack tool) {
		return tool != null && DataHandler.hasTag(tool, this.ToolIdentifier,
										PersistentDataType.INTEGER, false);
	}

	/**
	 * @param wand the ItemStack
	 * @return if the ItemStack is viable as MineTinker-Builderswand
	 */
	@Contract("null -> false")
	public boolean isWandViable(@Nullable final ItemStack wand) {
		return wand != null && DataHandler.hasTag(wand, "identifier_builderswand",
										PersistentDataType.INTEGER, false);
	}

	/**
	 * Updates the lore of the Item as everything is stored in the NBT-Data
	 *
	 * @param is The Itemstack to rewrite the Lore
	 */
	private void rewriteLore(@NotNull final ItemStack is) {
		if (!MineTinker.getPlugin().getConfig().getBoolean("EnableLore")) return;

		final ArrayList<String> lore = new ArrayList<>(this.loreScheme);

		final long exp = getExp(is);
		final int level = getLevel(is);
		final long nextLevelReq = getNextLevelReq(level);
		final int freeSlots = getFreeSlots(is);

		final String exp_ = layout.getBoolean("UseRomans.Exp")
				? ChatWriter.toRomanNumerals((int) exp) : String.valueOf(exp);
		final String level_ = layout.getBoolean("UseRomans.Level")
				? ChatWriter.toRomanNumerals(level) : String.valueOf(level);
		final String nextLevelReq_ = layout.getBoolean("UseRomans.Exp")
				? ChatWriter.toRomanNumerals((int) nextLevelReq) : String.valueOf(nextLevelReq);
		final String freeSlots_ = layout.getBoolean("UseRomans.FreeSlots")
				? ChatWriter.toRomanNumerals(freeSlots) : String.valueOf(freeSlots);

		final OfflinePlayer creator = getCreator(is);
		final String creator_ = (creator != null) ? creator.getName() : "null";

		lore.replaceAll(s -> ChatColor.WHITE + s
				.replaceAll("%EXP%", exp_)
				.replaceAll("%LEVEL%", level_)
				.replaceAll("%NEXT_LEVEL_EXP%", nextLevelReq_)
				.replaceAll("%FREE_SLOTS%", freeSlots_)
				.replaceAll("%CREATOR%", creator_ != null ? creator_ : "null"));

		int index = -1;
		for (int i = 0; i < lore.size(); i++) {
			final String s = lore.get(i);

			if (s.contains("%MODIFIERS%")) {
				index = i;
				break;
			}
		}

		if (index == -1) return;

		lore.remove(index);

		for (final Modifier m : this.mods) {
			if (!DataHandler.hasTag(is, m.getKey(), PersistentDataType.INTEGER, false)) continue;

			final int modLevel = getModLevel(is, m);
			final String modLevel_ = layout.getBoolean("UseRomans.ModifierLevels")
					? ChatWriter.toRomanNumerals(modLevel) : String.valueOf(modLevel);

			String s = this.modifierLayout;
			s = s.replaceAll("%MODIFIER%", m.getColor() + m.getName());
			s = s.replaceAll("%MODLEVEL%", modLevel_);

			lore.add(index++, s);
		}

		final ItemMeta meta = is.getItemMeta();

		if (meta == null) return;
		if (layout.getBoolean("UsePatternMatcher", false) && meta.hasLore()) {
			// clean up lore from old MineTinker-Lore
			final List<String> oldLore = meta.getLore();
			final ArrayList<String> toRemove = new ArrayList<>();
			final String mod = "\\Q" + this.modifierLayout + "\\E";
			for (String s : oldLore) {
				boolean removed = false;
				for (String m : this.loreScheme) {
					m = "\\Q" + m + "\\E";
					if (s.matches("[§f]{0,2}" +
							m.replace("%LEVEL%", "\\E[a-zA-Z0-9&§]+?\\Q")
									.replace("%EXP%", "\\E[a-zA-Z0-9&§]+?\\Q")
									.replace("%FREE_SLOTS%", "\\E[a-zA-Z0-9&§]+?\\Q")
									.replace("%NEXT_LEVEL_EXP%", "\\E[a-zA-Z0-9&§]+?\\Q")
									.replace("%CREATOR%", "\\E[a-zA-Z0-9&§]+?\\Q"))) {
						toRemove.add(s);
						removed = true;
						break;
					}
				}
				if (removed) continue;
				for (Modifier m : this.getToolMods(is)) {
					if (s.matches("[§f]{0,2}" +
							mod.replace("%MODIFIER%", "\\E.+\\Q" + m.getName())
									.replace("%MODLEVEL%", "\\E[a-zA-Z0-9&§]+?\\Q"))) {
						toRemove.add(s);
						break;
					}
				}
			}

			if (!toRemove.isEmpty()) {
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

		meta.setLore(lore);
		is.setItemMeta(meta);

	}

	/**
	 * converts a given ItemStack into its MineTinker equivalent
	 *
	 * @param is The {@link ItemStack} to convert.
	 * @return If the conversion was successful. Also returns false if the item is already MT compatible.
	 */
	@Contract("null, _ -> false")
	public boolean convertItemStack(final ItemStack is, @Nullable final Entity entity) {
		if (is == null) return false;

		final Material m = is.getType();
		int damage = 0;

		// Don't convert already converted items
		if (isArmorViable(is) || isToolViable(is) || isWandViable(is)) return false;

		if (is.getItemMeta() instanceof Damageable)
			damage = ((Damageable) is.getItemMeta()).getDamage();

		if (!ToolType.ALL.contains(m)) return false;

		if (!MineTinker.getPlugin().getConfig().getBoolean("ConvertEnchantsAndAttributes")) {
			final ItemMeta meta = new ItemStack(is.getType(), is.getAmount()).getItemMeta();

			if (meta instanceof Damageable damagable) {
				damagable.setDamage(damage);
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

		final ItemMeta meta = is.getItemMeta();

		if (meta == null) return true;
		if (!MineTinker.getPlugin().getConfig().getBoolean("ConvertEnchantsAndAttributes"))  return true;

		for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
			final Modifier modifier = getModifierFromEnchantment(entry.getKey());

			if (modifier == null) continue;

			meta.removeEnchant(entry.getKey());

			for (int i = 0; i < entry.getValue(); i++) { //possible to go over MaxLevel of the mod
				addMod(is, modifier);
				modifier.applyMod(null, is, true); //Player is only required with Extra-Modifier (not possible here)
			}
		}

		if (meta.getAttributeModifiers() == null) return true;

		for (Map.Entry<Attribute, Collection<AttributeModifier>> entry : meta.getAttributeModifiers().asMap().entrySet()) {
			final Modifier modifier = getModifierFromAttribute(entry.getKey());

			if (modifier == null || modifier.equals(Hardened.instance())) {
				continue;
			}

			meta.removeAttributeModifier(entry.getKey());
			addMod(is, modifier);
		}

		return true;
	}

	private void setCreator(@Nullable final ItemStack is, @Nullable final Entity entity) {
		if (is == null || entity == null) return;
		DataHandler.setTag(is, "creator", entity.getUniqueId(), UUIDTagType.instance, false);
	}

	public @Nullable OfflinePlayer getCreator(@Nullable final ItemStack is) {
		if (is == null) return null;
		UUID creator = DataHandler.getTag(is, "creator", UUIDTagType.instance, false);
		if (creator == null) return null;
		return Bukkit.getOfflinePlayer(creator);
	}

	public void addArmorAttributes(@NotNull final ItemStack is) {
		ItemMeta meta = is.getItemMeta();
		if (meta == null) return;

		double armor = 0.0d;
		double toughness = 0.0d;
		double knockback_res = 0.0d;

		switch (is.getType()) {
			case LEATHER_BOOTS, CHAINMAIL_BOOTS, GOLDEN_BOOTS, LEATHER_HELMET -> armor = 1.0d;
			case IRON_BOOTS, CHAINMAIL_HELMET, IRON_HELMET, GOLDEN_HELMET, TURTLE_HELMET, LEATHER_LEGGINGS -> armor = 2.0d;
			case DIAMOND_BOOTS, DIAMOND_HELMET -> {
				armor = 3.0d;
				toughness = 2.0d;
			}
			case LEATHER_CHESTPLATE, GOLDEN_LEGGINGS -> armor = 3.0d;
			case CHAINMAIL_CHESTPLATE, GOLDEN_CHESTPLATE, IRON_LEGGINGS -> armor = 5.0d;
			case IRON_CHESTPLATE -> armor = 6.0d;
			case DIAMOND_CHESTPLATE -> {
				armor = 8.0d;
				toughness = 2.0d;
			}
			case CHAINMAIL_LEGGINGS -> armor = 4.0d;
			case DIAMOND_LEGGINGS -> {
				armor = 6.0d;
				toughness = 2.0d;
			}
			case NETHERITE_HELMET, NETHERITE_BOOTS -> {
				armor = 3.0d;
				toughness = 3.0d;
				knockback_res = 0.1d; //Knockback Resistance of 1.0d would be Resistance 10
			}
			case NETHERITE_CHESTPLATE -> {
				armor = 8.0d;
				toughness = 3.0d;
				knockback_res = 0.1d; //Knockback Resistance of 1.0d would be Resistance 10
			}
			case NETHERITE_LEGGINGS -> {
				armor = 6.0d;
				toughness = 3.0d;
				knockback_res = 0.1d; //Knockback Resistance of 1.0d would be Resistance 10
			}
		}

		AttributeModifier armorAM;
		AttributeModifier toughnessAM;
		AttributeModifier knockbackResAM;

		if (ToolType.BOOTS.contains(is.getType())) {
			armorAM = new AttributeModifier(UUID.randomUUID(), "generic.armor", armor,
					AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET);
			toughnessAM = new AttributeModifier(UUID.randomUUID(), "generic.armor_toughness", toughness,
					AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET);
			knockbackResAM = new AttributeModifier(UUID.randomUUID(), "generic.knockback_resistance", knockback_res,
					AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET);
		} else if (ToolType.CHESTPLATE.contains(is.getType())) {
			armorAM = new AttributeModifier(UUID.randomUUID(), "generic.armor", armor,
					AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST);
			toughnessAM = new AttributeModifier(UUID.randomUUID(), "generic.armor_toughness", toughness,
					AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST);
			knockbackResAM = new AttributeModifier(UUID.randomUUID(), "generic.knockback_resistance", knockback_res,
					AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST);
		} else if (ToolType.HELMET.contains(is.getType())) {
			armorAM = new AttributeModifier(UUID.randomUUID(), "generic.armor", armor,
					AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HEAD);
			toughnessAM = new AttributeModifier(UUID.randomUUID(), "generic.armor_toughness", toughness,
					AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HEAD);
			knockbackResAM = new AttributeModifier(UUID.randomUUID(), "generic.knockback_resistance", knockback_res,
					AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HEAD);
		} else if (ToolType.LEGGINGS.contains(is.getType())) {
			armorAM = new AttributeModifier(UUID.randomUUID(), "generic.armor", armor,
					AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS);
			toughnessAM = new AttributeModifier(UUID.randomUUID(), "generic.armor_toughness", toughness,
					AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS);
			knockbackResAM = new AttributeModifier(UUID.randomUUID(), "generic.knockback_resistance", knockback_res,
					AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS);
		} else return;

		if (armor > 0.0d) {
			meta.removeAttributeModifier(Attribute.GENERIC_ARMOR);
			meta.addAttributeModifier(Attribute.GENERIC_ARMOR, armorAM);
		}

		if (toughness > 0.0d) {
			meta.removeAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS);
			meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, toughnessAM);
		}

		if (knockback_res > 0.0d) {
			meta.removeAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
			meta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, knockbackResAM);
		}

		if (MineTinker.getPlugin().getConfig().getBoolean("HideAttributes")) {
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		} else {
			meta.removeItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		}

		is.setItemMeta(meta);

		Hardened.instance().reapplyAttributes(is);
	}

	public @NotNull ItemStack createModifierItem(@NotNull final Material m, @NotNull final String name,
												 @NotNull final String description, @NotNull final Modifier mod) {
		final ItemStack is = new ItemStack(m, 1);
		final ItemMeta meta = is.getItemMeta();

		if (meta == null) return is;
		meta.setDisplayName(name);

		final List<String> lore = ChatWriter.splitString(description, 40);
		//Tool Level Requirement
		if (mod.getMinimumLevelRequirement() >= 1) {
			lore.add(ChatColor.WHITE + LanguageManager.getString("GUIs.Modifiers.MinimumToolLevel")
					.replaceFirst("%level",
							layout.getBoolean("UseRomans.Level")
									? ChatWriter.toRomanNumerals(mod.getMinimumLevelRequirement())
									: String.valueOf(mod.getMinimumLevelRequirement())));
		}
		//Slot cost
		if (mod.getSlotCost() >= 0) {
			lore.add(ChatColor.WHITE + LanguageManager.getString("GUIs.Modifiers.SlotCost")
					.replaceFirst("%amount",
							layout.getBoolean("UseRomans.FreeSlots")
									? ChatWriter.toRomanNumerals(mod.getSlotCost())
									: String.valueOf(mod.getSlotCost())));
		}
		meta.setLore(lore);

		meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);

		is.setItemMeta(meta);
		DataHandler.setTag(is, "modifier_item", mod.getKey(), PersistentDataType.STRING, false);
		//TODO: DataHandler.setStringList(is, "CanPlaceOn", true, "minecraft:air");
		return is;
	}

	/**
	 * @param item the ItemStack
	 * @return if the ItemStack is viable as MineTinker-Modifier-Item
	 */
	@Contract("null -> false")
	public boolean isModifierItem(@Nullable final ItemStack item) {
		if (item == null) return false;
		return DataHandler.hasTag(item, "modifier_item", PersistentDataType.STRING, false);
	}

	/**
	 * @param item the ItemStack
	 * @return the Modifier of the Modifier-Item (NULL if not found)
	 */
	@Nullable
	public Modifier getModifierFromItem(@Nullable final ItemStack item) {
		if (!isModifierItem(item)) return null;

		final String name = DataHandler.getTag(item, "modifier_item", PersistentDataType.STRING, false);
		if (name == null) return null;

		return getModifierFromKey(name);
	}

	/**
	 * @param key the ItemStack
	 */
	@Nullable
	public Modifier getModifierFromKey(@Nullable final String key) {
		if (key == null || key.isEmpty() || key.isBlank()) return null;
		final Modifier mod = modKeys.get(key);
		if (mod == null || !mod.isAllowed()) return null;
		return mod;
	}

	/**
	 * Gets the first found modifier that applies the supplied enchantment.
	 *
	 * @param enchantment The enchantment to get the Modifier for
	 * @return the Modifier or null
	 */
	@Nullable
	public Modifier getModifierFromEnchantment(@NotNull final Enchantment enchantment) {
		return this.getAllowedMods().stream()
				.filter(modifier -> modifier.getAppliedEnchantments().contains(enchantment))
				.findFirst()
				.orElse(null);
	}

	/**
	 * Gets the first found modifier that applies the supplied attribute.
	 *
	 * @param attribute the attribute
	 * @return the Modifier or null
	 */
	@Nullable
	public Modifier getModifierFromAttribute(@NotNull final Attribute attribute) {
		return this.getAllowedMods().stream()
				.filter(modifier -> modifier.getAppliedAttributes().contains(attribute))
				.findFirst()
				.orElse(null);
	}

	public void convertLoot(@Nullable ItemStack item, @Nullable Player player) {
		Random rand = new Random();
		if (rand.nextInt(100) >= config.getInt("ConvertLoot.Chance", 100)) return;
		if (!convertItemStack(item, null)) return;
		//Item is now MT
		//continue if already was MT or not right material

		if (config.getBoolean("ConvertLoot.ApplyExp", true)) {
			final int exp = rand.nextInt(config.getInt("ConvertLoot.MaximumNumberOfExp", 650));
			addExp(null, item, exp, false);
			int level = getLevel(item);
			setFreeSlots(item, getFreeSlots(item) + level * config.getInt("AddModifierSlotsPerLevel"));
		}

		if (config.getBoolean("ConvertLoot.ApplyModifiers", true)) {
			//Remove all enchants if modifiers will get added
			allMods.forEach(mod -> removeMod(item, mod));
			final List<Modifier> mods = getAllowedMods();
			mods.remove(ExtraModifier.instance());
			int amount = rand.nextInt(config.getInt("ConvertLoot.MaximumNumberOfModifiers") + 1);
			for (int i = 0; i < amount; i++) {
				while (!mods.isEmpty()) {
					final int index = rand.nextInt(mods.size());
					final Modifier mod = mods.get(index);
					if (addMod(player, item, mod, false, true, true,
							config.getBoolean("ConvertLoot.AppliedModifiersConsiderSlots", true))) {
						break;
					}

					mods.remove(mod);
				}

				if (mods.isEmpty()) break;
			}
		}
	}

	/**
	 * Checks the durability of the Tool
	 *
	 * @param cancellable the Event (implements Cancelable)
	 * @param player      the Player
	 * @param tool        the Tool
	 * @return false: if broken; true: if enough durability
	 */
	public boolean durabilityCheck(@NotNull final Cancellable cancellable, @NotNull final Player player, @NotNull final ItemStack tool) {
		if (!config.getBoolean("UnbreakableTools", true)) return true;

		final ItemMeta meta = tool.getItemMeta();

		if (meta instanceof Damageable damageable && tool.getType().getMaxDurability() - damageable.getDamage() <= 2) {
			cancellable.setCancelled(true);

			if (config.getBoolean("Sound.OnBreaking", true)) {
				player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.5F, 0.5F);
			}
			return false;
		}
		return true;
	}

	public void removeRecipes() {
		this.recipe_Namespaces.forEach(key -> Bukkit.getServer().removeRecipe(key));
		this.recipe_Namespaces.clear();
	}
}
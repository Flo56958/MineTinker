package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.api.events.ToolLevelUpEvent;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ConfigurationManager;
import de.flo56958.minetinker.utils.LanguageManager;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class Mutating extends Modifier implements Listener {

	private static Mutating instance;
	private int chancePerLevel;
	private int modifierAmount;
	private boolean allowExtraModifier;
	private boolean dropModifierAsItem;
	private boolean allowLevelZero;
	private boolean disableAddingNewSlots;
	private final Random rand = new Random();
	private boolean ignoreSlots;

	private Mutating() {
		super(MineTinker.getPlugin());
		customModelData = 10_068;
	}

	public static Mutating instance() {
		synchronized (Mutating.class) {
			if (instance == null)
				instance = new Mutating();
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Mutating";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Collections.singletonList(ToolType.ALL);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Color", "%DARK_GRAY%");
		config.addDefault("MaxLevel", 5);
		config.addDefault("SlotCost", 1);
		config.addDefault("ModifierItemMaterial", Material.NETHER_STAR.name());

		config.addDefault("EnchantCost", 25);
		config.addDefault("Enchantable", false);
		config.addDefault("MinimumToolLevelRequirement", 1);

		config.addDefault("ChancePerLevel", 10);
		config.addDefault("ModifierAmount", 1); // How many (different) Modifiers should be applied in this event
		config.addDefault("AllowExtraModifier", true); // Should the Extra Modifier be part of the random roll
		config.addDefault("DropModifierAsItem", false);
		config.addDefault("AllowLevelZero", true);
		config.addDefault("DisableAddingNewSlots", false); // Should the Event replace the normal free Slots on Level up
		config.addDefault("IgnoreSlots", true); // Should the Modifiers be applied to the Tool, even if there are not enough Slots

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", "PBP");
		config.addDefault("Recipe.Middle", "BNB");
		config.addDefault("Recipe.Bottom", "PBP");

		Map<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("B", Material.BLAZE_ROD.name());
		recipeMaterials.put("P", Material.BLAZE_POWDER.name());
		recipeMaterials.put("N", Material.NETHER_STAR.name());

		config.addDefault("Recipe.Materials", recipeMaterials);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init();

		this.chancePerLevel = config.getInt("ChancePerLevel", 10);
		this.modifierAmount = config.getInt("ModifierAmount", 1);
		this.allowExtraModifier = config.getBoolean("AllowExtraModifier", true);
		this.dropModifierAsItem = config.getBoolean("DropModifierAsItem", false);
		this.allowLevelZero = config.getBoolean("AllowLevelZero", true);
		this.disableAddingNewSlots = config.getBoolean("DisableAddingNewSlots", false);
		this.ignoreSlots = config.getBoolean("IgnoreSlots", true);

		this.description = this.description.replace("%chance", String.valueOf(chancePerLevel))
											.replace("%amount", String.valueOf(modifierAmount));
		if (allowLevelZero) this.description += " " + LanguageManager.getString("Modifier.Mutating.Description_Level0");
	}

	@EventHandler(ignoreCancelled = true)
	private void onLevelUp(@NotNull final ToolLevelUpEvent event) {
		final Player player = event.getPlayer();
		if (!player.hasPermission(getUsePermission())) return;

		final ItemStack tool = event.getTool();

		int modlevel = modManager.getModLevel(tool, this);
		if (modlevel == 0 && !allowLevelZero) return;

		if (allowLevelZero) modlevel++; //if level 0 is allowed, the level is increased by 1

		if (rand.nextInt(100) > chancePerLevel * modlevel) return;

		for (int i = 0; i < modifierAmount; i++) {
			final List<Modifier> mods = modManager.getAllowedMods();
			// necessary as the failed modifiers get removed from the list (so a copy is in order)
			// e.g. not enough slots previously but an extra modifier was applied, so removed mods require retesting

			if (!allowExtraModifier) mods.remove(ExtraModifier.instance());

			while (!mods.isEmpty()) {
				final Modifier mod = mods.get(rand.nextInt(mods.size()));
				if (dropModifierAsItem) {
					if (!player.getInventory().addItem(mod.getModItem()).isEmpty()) { //adds items to (full) inventory
						player.getWorld().dropItem(player.getLocation(), mod.getModItem()); //drops item when inventory is full
					} // no else as it gets added in if
					break;
				}

				if (modManager.addMod(player, tool, mod, false, true, false, !ignoreSlots))
					break;

				mods.remove(mod); //Remove the failed modifier from the list of the possibles
			}

			if (mods.isEmpty()) return; // if list is empty no modifier was applied and won't be applied in later tries
		}

		// successful application of at least one modifier
		if (disableAddingNewSlots) event.setNewSlots(0);
	}
}

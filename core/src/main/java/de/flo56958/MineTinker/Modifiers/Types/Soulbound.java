package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

public class Soulbound extends Modifier implements Listener {

	private static Soulbound instance;
	//Can't you the Player-Object here as it gets newly created if the player leaves without respawning
	private final HashMap<UUID, ArrayList<ItemStack>> storedItemStacks = new HashMap<>(); //saves ItemStacks until reload (if the player does not respawn instantly)
	private boolean toolDropable;
	private boolean decrementModLevelOnUse;
	private int percentagePerLevel;

	private Soulbound() {
		super(Main.getPlugin());
		customModelData = 10_036;
	}

	public static Soulbound instance() {
		synchronized (Soulbound.class) {
			if (instance == null) {
				instance = new Soulbound();
			}
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Soulbound";
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
		config.addDefault("Name", "Soulbound");
		config.addDefault("ModifierItemName", "Powerinfused Beacon");
		config.addDefault("Description", "Do not lose the tool when dying.");
		config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Soulbound-Modifier");
		config.addDefault("Color", "%GRAY%");
		config.addDefault("MaxLevel", 1);
		config.addDefault("PercentagePerLevel", 100);
		config.addDefault("DecrementModLevelOnUse", false);
		config.addDefault("ToolDropable", true);
		config.addDefault("OverrideLanguagesystem", false);

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", "BLB");
		config.addDefault("Recipe.Middle", "LNL");
		config.addDefault("Recipe.Bottom", "BLB");

		Map<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("B", Material.BLAZE_ROD.name());
		recipeMaterials.put("L", Material.LAVA_BUCKET.name());
		recipeMaterials.put("N", Material.NETHER_STAR.name());

		config.addDefault("Recipe.Materials", recipeMaterials);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init(Material.BEACON, true);

		this.toolDropable = config.getBoolean("ToolDropable", true);
		this.decrementModLevelOnUse = config.getBoolean("DecrementModLevelOnUse", false);
		this.percentagePerLevel = config.getInt("PercentagePerLevel", 100);
	}

	/**
	 * Effect when a player dies
	 *
	 * @param player  the Player
	 * @param itemStack the ItemStack to keep
	 * @return true if soulbound has success
	 */
	public boolean effect(Player player, ItemStack itemStack) {
		if (!player.hasPermission("minetinker.modifiers.soulbound.use")) {
			return false;
		}

		if (!modManager.hasMod(itemStack, this)) {
			return false;
		}

		Random rand = new Random();
		if (rand.nextInt(100) > modManager.getModLevel(itemStack, this) * percentagePerLevel) {
			return false;
		}

		storedItemStacks.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>()); // ?

		ArrayList<ItemStack> stored = storedItemStacks.get(player.getUniqueId());

		ChatWriter.log(false, player.getDisplayName() + " triggered Soulbound on " + ItemGenerator.getDisplayName(itemStack) + ChatColor.GRAY + " (" + itemStack.getType().toString() + ")!");

		if (stored.contains(itemStack)) {
			return true;
		}

		if (decrementModLevelOnUse) {
			int newLevel = modManager.getModLevel(itemStack, this) - 1;

			if (newLevel == 0) {
				modManager.removeMod(itemStack, this);
			} else {
				modManager.getNBTHandler().setInt(itemStack, getKey(), modManager.getModLevel(itemStack, this) - 1);
			}
		}

		stored.add(itemStack.clone());
		return true;
	}

	/**
	 * Effect if a player respawns
	 */
	@EventHandler
	public void effect(PlayerRespawnEvent event) {
		Player player = event.getPlayer();

		if (!player.hasPermission("minetinker.modifiers.soulbound.use")) {
			return;
		}

		if (!storedItemStacks.containsKey(player.getUniqueId())) {
			return;
		}

		ArrayList<ItemStack> stored = storedItemStacks.get(player.getUniqueId());

		for (ItemStack is : stored) {
			if (player.getInventory().addItem(is).size() != 0) { //adds items to (full) inventory
				player.getWorld().dropItem(player.getLocation(), is);
			} // no else as it gets added in if
		}

		storedItemStacks.remove(player.getUniqueId());
	}

	/**
	 * Effect if a player drops an item
	 *
	 * @param event the event
	 */
	@EventHandler(ignoreCancelled = true)
	public void effect(PlayerDropItemEvent event) {
		Item item = event.getItemDrop();
		ItemStack tool = item.getItemStack();

		if (!(modManager.isArmorViable(tool) || modManager.isToolViable(tool) || modManager.isWandViable(tool))) {
			return;
		}

		if (!modManager.hasMod(tool, this)) {
			return;
		}

		if (toolDropable) {
			return;
		}

		event.setCancelled(true);
	}
}

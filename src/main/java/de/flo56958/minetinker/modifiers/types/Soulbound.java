package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.ConfigurationManager;
import de.flo56958.minetinker.utils.data.DataHandler;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.*;

public class Soulbound extends Modifier implements Listener {

	private static Soulbound instance;
	//Must be UUID as if the Player reconnects the Player-Object gets recreated and is not the same anymore
 	private final HashMap<UUID, ArrayList<ItemStack>> storedItemStacks = new HashMap<>();		//saves ItemStacks until respawn
	private final HashMap<UUID, ArrayList<Integer>> storedItemStacksLocation = new HashMap<>();	//saves ItemStack slot until respawn
	private boolean toolDroppable;
	private boolean decrementModLevelOnUse;
	private int percentagePerLevel;

	private Soulbound() {
		super(MineTinker.getPlugin());
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
		config.addDefault("Color", "%GRAY%");
		config.addDefault("MaxLevel", 1);
		config.addDefault("SlotCost", 2);
		config.addDefault("PercentagePerLevel", 100);
		config.addDefault("DecrementModLevelOnUse", false);
		config.addDefault("ToolDropable", true);

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

		init(Material.BEACON);

		this.toolDroppable = config.getBoolean("ToolDropable", true);
		this.decrementModLevelOnUse = config.getBoolean("DecrementModLevelOnUse", false);
		this.percentagePerLevel = config.getInt("PercentagePerLevel", 100);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void effect(PlayerDeathEvent event) {
		if (event.getKeepInventory()) {
			return;
		}

		Player player = event.getEntity();
		Inventory inventory = player.getInventory();
		for (int index = 0; index < inventory.getContents().length; index++) {
			ItemStack itemStack = inventory.getItem(index);
			if (itemStack == null) {
				continue; // More consistent nullability in NotNull fields
			}

			if (modManager.isArmorViable(itemStack) || modManager.isToolViable(itemStack) || modManager.isWandViable(itemStack)) {
				if (!player.hasPermission("minetinker.modifiers.soulbound.use")) {
					continue;
				}

				if (!modManager.hasMod(itemStack, this)) {
					continue;
				}

				Random rand = new Random();
				int n = rand.nextInt(100);
				int c = modManager.getModLevel(itemStack, this) * percentagePerLevel;
				ChatWriter.logModifier(player, event, this, itemStack, String.format("Chance(%d/%d)", n, c));
				if (n > c) {
					continue;
				}

				storedItemStacks.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>()); // ?
				storedItemStacksLocation.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>());

				ArrayList<ItemStack> stored = storedItemStacks.get(player.getUniqueId());
				ArrayList<Integer> storedLocation = storedItemStacksLocation.get(player.getUniqueId());

				if (stored.contains(itemStack)) {
					continue;
				}

				if (decrementModLevelOnUse) {
					int newLevel = modManager.getModLevel(itemStack, this) - 1;

					if (newLevel == 0) {
						modManager.removeMod(itemStack, this);
					} else {
						DataHandler.setTag(itemStack, getKey(), modManager.getModLevel(itemStack, this) - 1, PersistentDataType.INTEGER, false);
					}
				}

				stored.add(itemStack.clone());
				storedLocation.add(index);
				itemStack.setAmount(0);
			}
		}
	}

	/**
	 * Effect if a player respawns
	 */
	@EventHandler
	public void effect(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		PlayerInventory inventory = player.getInventory();

		if (!player.hasPermission("minetinker.modifiers.soulbound.use")) {
			return;
		}

		if (!storedItemStacks.containsKey(player.getUniqueId()) || !storedItemStacksLocation.containsKey(player.getUniqueId())) {
			return;
		}

		ArrayList<ItemStack> stored = storedItemStacks.get(player.getUniqueId());
		ArrayList<Integer> storedLocation = storedItemStacksLocation.get(player.getUniqueId());

		for (int i = 0; i < stored.size(); i++) {
			inventory.setItem(storedLocation.get(i), stored.get(i));
			ChatWriter.logModifier(player, event, this, stored.get(i));
		}

		storedItemStacks.remove(player.getUniqueId());
		storedItemStacksLocation.remove(player.getUniqueId());
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

		if (toolDroppable) {
			return;
		}

		ChatWriter.logModifier(event.getPlayer(), event, this, tool, "Tool not droppable");

		event.setCancelled(true);
	}
}

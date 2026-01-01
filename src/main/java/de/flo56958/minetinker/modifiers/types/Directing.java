package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.api.events.MTBlockBreakEvent;
import de.flo56958.minetinker.api.events.MTEntityDeathEvent;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.ConfigurationManager;
import de.flo56958.minetinker.utils.LanguageManager;
import de.flo56958.minetinker.utils.data.DataHandler;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.*;

public class Directing extends Modifier implements Listener {

	private static Directing instance;

	private boolean workInPVP;
	private boolean workOnXP;
	private int minimumLevelForXP;

	private Directing() {
		super(MineTinker.getPlugin());
		customModelData = 10_008;
	}

	public static Directing instance() {
		synchronized (Directing.class) {
			if (instance == null)
				instance = new Directing();
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Directing";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Arrays.asList(ToolType.TOOLS);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("MaxLevel", 1);
		config.addDefault("SlotCost", 1);
		config.addDefault("WorksOnXP", true);
		config.addDefault("MinimumLevelToGetXP", 1); //Modifier-Level to give Player XP
		config.addDefault("WorkInPVP", true);
		config.addDefault("Color", "%GRAY%");
		config.addDefault("ModifierItemMaterial", Material.COMPASS.name());

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);
		config.addDefault("MinimumToolLevelRequirement", 1);

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", "ECE");
		config.addDefault("Recipe.Middle", "CIC");
		config.addDefault("Recipe.Bottom", "ECE");

		Map<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("C", Material.COMPASS.name());
		recipeMaterials.put("E", Material.ENDER_PEARL.name());
		recipeMaterials.put("I", Material.IRON_BLOCK.name());

		config.addDefault("Recipe.Materials", recipeMaterials);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init();

		this.workInPVP = config.getBoolean("WorkInPVP", true);
		this.workOnXP = config.getBoolean("WorksOnXP", true);
		this.minimumLevelForXP = config.getInt("MinimumLevelToGetXP", 1);
	}

	//used for exp teleportation
	@EventHandler(ignoreCancelled = true)
	public void effect(final MTBlockBreakEvent event) {
		final Player player = event.getPlayer();
		final ItemStack tool = event.getTool();
		if (!player.hasPermission(getUsePermission())) return;
		if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) return;
		if (!modManager.isToolViable(tool) || !modManager.hasMod(tool, this)) return;

		if (this.workOnXP && modManager.getModLevel(tool, this) >= this.minimumLevelForXP) {
			//Spawn Experience Orb as adding it directly to the player would prevent Mending from working
			if (event.getEvent().getExpToDrop() <= 0) return;
			ExperienceOrb orb = (ExperienceOrb) player.getWorld().spawnEntity(player.getLocation(), EntityType.EXPERIENCE_ORB);
			orb.setExperience(event.getEvent().getExpToDrop());
			event.getEvent().setExpToDrop(0);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void effect(final BlockDropItemEvent event) {
		final Player player = event.getPlayer();
		final ItemStack tool = player.getInventory().getItemInMainHand();

		if (!player.hasPermission(getUsePermission())) return;
		if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) return;
		if (!modManager.isToolViable(tool) || !modManager.hasMod(tool, this)) return;

		final Iterator<Item> itemIterator = event.getItems().iterator();

		//Track stats
		int stat = DataHandler.getTagOrDefault(tool, getKey() + "_stat_used", PersistentDataType.INTEGER, 0);

		while (itemIterator.hasNext()) {
			final Item item = itemIterator.next();
			stat += item.getItemStack().getAmount();

			final HashMap<Integer, ItemStack> refusedItems = player.getInventory().addItem(item.getItemStack());

			if (!refusedItems.isEmpty()) {
				for (ItemStack itemStack : refusedItems.values()) {
					player.getWorld().dropItem(player.getLocation(), itemStack);
				}
			}

			itemIterator.remove();
		}

		DataHandler.setTag(tool, getKey() + "_stat_used", stat, PersistentDataType.INTEGER);

		Location loc = event.getBlock().getLocation();
		ChatWriter.logModifier(player, event, this, tool,
				String.format("Block(%d/%d/%d)", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void effect(MTEntityDeathEvent event) {
		if (event.getPlayer().equals(event.getEvent().getEntity())) return;
		if (!this.workInPVP && event.getEvent().getEntity() instanceof Player) return;

		// Disable Directing when KeepInventory is on
		if (event.getEvent() instanceof PlayerDeathEvent devent && devent.getKeepInventory()) return;

		final Player player = event.getPlayer();
		final ItemStack tool = event.getTool();
		if (!player.hasPermission(getUsePermission())) return;
		if (!modManager.hasMod(tool, this)) return;

		//Track stats
		int stat = DataHandler.getTagOrDefault(tool, getKey() + "_stat_used", PersistentDataType.INTEGER, 0);

		for (ItemStack current : new ArrayList<>(event.getEvent().getDrops())) {
			if (modManager.hasMod(current, Soulbound.instance()) && event.getEvent().getEntity() instanceof Player)
				continue;

			stat += current.getAmount();

			if (!player.getInventory().addItem(current).isEmpty()) { //adds items to (full) inventory
				player.getWorld().dropItem(player.getLocation(), current);
			} // no else as it gets added in if-clause
			event.getEvent().getDrops().remove(current);
		}

		DataHandler.setTag(tool, getKey() + "_stat_used", stat, PersistentDataType.INTEGER);

		ChatWriter.logModifier(player, event, this, tool,
				"Entity(" + event.getEvent().getEntity().getType() + ")");

		if (this.workOnXP && modManager.getModLevel(tool, this) >= this.minimumLevelForXP) {
			// Spawn Experience Orb as adding it directly to the player would prevent Mending from working
			if (event.getEvent().getDroppedExp() <= 0) return;
			ExperienceOrb orb = (ExperienceOrb) player.getWorld().spawnEntity(player.getLocation(), EntityType.EXPERIENCE_ORB);
			orb.setExperience(orb.getExperience() + event.getEvent().getDroppedExp());
			event.getEvent().setDroppedExp(0);
		}
	}

	private static HashMap<Sheep, Player> sheepSheared = new HashMap<>();

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void sheepShear(PlayerShearEntityEvent event) {
		final Player player = event.getPlayer();
		final ItemStack tool = event.getItem();
		if (!player.hasPermission(getUsePermission())) return;
		if (!modManager.isToolViable(tool) || !modManager.hasMod(tool, this)) return;

		if (!(event.getEntity() instanceof Sheep sheep)) return;
		sheepSheared.put(sheep, player); // Store the player who sheared the sheep

		// there is no item access here
	}

	@EventHandler(ignoreCancelled = true)
	public void afterSheepShear(EntityDropItemEvent event) {
		if (!(event.getEntity() instanceof Sheep sheep)) return;
		if (!sheepSheared.containsKey(sheep)) return; // Only process if the sheep was sheared by a player with Directing

		Player player = sheepSheared.get(sheep);
		// wait 5 ticks as multiple items could drop
		MineTinker.getPlugin().getServer().getScheduler().runTaskLater(MineTinker.getPlugin(), () -> {
			sheepSheared.remove(sheep); // Remove the entry after processing
		}, 5L);

		ItemStack tool = player.getInventory().getItemInMainHand();
		if (!player.hasPermission(getUsePermission())) return;
		if (!modManager.isToolViable(tool) || !modManager.hasMod(tool, this)) return;

		event.setCancelled(true); // Prevent default drop

		// Add wool to player's inventory
		ItemStack wool = event.getItemDrop().getItemStack();
		if (!player.getInventory().addItem(wool).isEmpty()) {
			player.getWorld().dropItem(player.getLocation(), wool);
		}

		//Track stats
		int stat = DataHandler.getTagOrDefault(tool, getKey() + "_stat_used", PersistentDataType.INTEGER, 0);
		stat += wool.getAmount(); //1 for each sheared entity
		DataHandler.setTag(tool, getKey() + "_stat_used", stat, PersistentDataType.INTEGER);

		ChatWriter.logModifier(player, event, this, tool,
				String.format("Entity(%s)", event.getEntity().getType()), String.format("Item(%s)", wool.getType()));
	}

	@Override
	public List<String> getStatistics(ItemStack item) {
		// Get stats
		final List<String> lore = new ArrayList<>();
		final int stat = DataHandler.getTagOrDefault(item, getKey() + "_stat_used", PersistentDataType.INTEGER, 0);
		lore.add(ChatColor.WHITE + LanguageManager.getString("Modifier.Directing.Statistic_Used")
				.replaceAll("%amount", String.valueOf(stat)));
		return lore;
	}
}

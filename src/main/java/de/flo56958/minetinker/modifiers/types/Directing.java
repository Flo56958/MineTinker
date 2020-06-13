package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.events.MTEntityDeathEvent;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.ConfigurationManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;

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
			if (instance == null) {
				instance = new Directing();
			}
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Directing";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.CROSSBOW, ToolType.SWORD, ToolType.TRIDENT, ToolType.PICKAXE, ToolType.SHOVEL, ToolType.HOE, ToolType.SHEARS);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Name", "Directing");
		config.addDefault("ModifierItemName", "Enhanced Compass");
		config.addDefault("Description", "Loot goes directly into Inventory!");
		config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Directing-Modifier");
		config.addDefault("MaxLevel", 1);
		config.addDefault("SlotCost", 1);
		config.addDefault("WorksOnXP", true);
		config.addDefault("MinimumLevelToGetXP", 1); //Modifier-Level to give Player XP
		config.addDefault("WorkInPVP", true);
		config.addDefault("Color", "%GRAY%");
		config.addDefault("OverrideLanguagesystem", false);

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);

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

		init(Material.COMPASS);

		this.workInPVP = config.getBoolean("WorkInPVP", true);
		this.workOnXP = config.getBoolean("WorksOnXP", true);
		this.minimumLevelForXP = config.getInt("MinimumLevelToGetXP", 1);
	}

	@EventHandler
	public void effect(BlockDropItemEvent event) {
		Player player = event.getPlayer();
		ItemStack tool = player.getInventory().getItemInMainHand();

		if (!player.hasPermission("minetinker.modifiers.directing.use")) {
			return;
		}

		if (!modManager.isToolViable(tool) || !modManager.hasMod(tool, this)) {
			return;
		}

		Iterator<Item> itemIterator = event.getItems().iterator();

		while (itemIterator.hasNext()) {
			Item item = itemIterator.next();

			HashMap<Integer, ItemStack> refusedItems = player.getInventory().addItem(item.getItemStack());

			if (!refusedItems.isEmpty()) {
				for (ItemStack itemStack : refusedItems.values()) {
					player.getWorld().dropItem(player.getLocation(), itemStack);
				}
			}

			itemIterator.remove();
		}
		Location loc = event.getBlock().getLocation();
		ChatWriter.logModifier(player, event, this, tool,
				String.format("Block(%d/%d/%d)", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
	}

	@EventHandler
	public void effect(MTEntityDeathEvent event) {
		if (event.getPlayer().equals(event.getEvent().getEntity())) {
			return;
		}
		if (!this.workInPVP && event.getEvent().getEntity() instanceof Player) {
			return;
		}

		Player player = event.getPlayer();
		ItemStack tool = event.getTool();

		if (!player.hasPermission("minetinker.modifiers.directing.use")) {
			return;
		}

		if (!modManager.hasMod(tool, this)) {
			return;
		}

		List<ItemStack> drops = event.getEvent().getDrops();
		List<ItemStack> toremove = new ArrayList<>();

		for (ItemStack current : drops) {
			if (modManager.hasMod(current, Soulbound.instance())) {
				continue;
			}

			if (player.getInventory().addItem(current).size() != 0) { //adds items to (full) inventory
				player.getWorld().dropItem(player.getLocation(), current);
			} // no else as it gets added in if-clause
			toremove.add(current);
		}

		drops.removeAll(toremove);

		if (this.workOnXP && modManager.getModLevel(tool, this) >= this.minimumLevelForXP) {
			player.giveExp(event.getEvent().getDroppedExp());
			event.getEvent().setDroppedExp(0);
		}

		ChatWriter.logModifier(player, event, this, tool,
				"Entity(" + event.getEvent().getEntity().getType().toString() + ")");
	}
}

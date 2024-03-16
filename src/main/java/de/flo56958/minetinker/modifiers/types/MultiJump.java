package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ConfigurationManager;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiJump extends Modifier implements Listener {

	private static MultiJump instance;

	//The Integer tells the Plugin how much Level of MultiJump have been used up by the Player
	private final ConcurrentHashMap<Player, AtomicInteger> jumpcharge = new ConcurrentHashMap<>();

	private final ConcurrentHashMap<Player, Long> allowFlight = new ConcurrentHashMap<>();

	private MultiJump() {
		super(MineTinker.getPlugin());
		customModelData = 10_057;
	}

	public static MultiJump instance() {
		synchronized (MultiJump.class) {
			if (instance == null)
				instance = new MultiJump();
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Multijump";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Collections.singletonList(ToolType.BOOTS);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true); //incompatible with fly plugins
		config.addDefault("Color", "%DARK_GREEN%");
		config.addDefault("MaxLevel", 3);
		config.addDefault("SlotCost", 3);
		config.addDefault("ModifierItemMaterial", Material.RABBIT_FOOT.name());

		config.addDefault("EnchantCost", 50);
		config.addDefault("Enchantable", false);
		config.addDefault("MinimumToolLevelRequirement", 1);

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", "RFR");
		config.addDefault("Recipe.Middle", "FPF");
		config.addDefault("Recipe.Bottom", "RFR");

		Map<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("R", Material.RABBIT_FOOT.name());
		recipeMaterials.put("P", Material.PISTON.name());
		recipeMaterials.put("F", Material.FEATHER.name());

		config.addDefault("Recipe.Materials", recipeMaterials);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init();

		//Reset the used Datastructures
		//This can enable the player to jump more often than they are eligible to but reload() should not be called
		//often
		jumpcharge.clear();
		allowFlight.keySet().forEach(p -> p.setAllowFlight(false));
		allowFlight.clear();
	}

	//These two methods should improve compatibility with flight plugins as with them MT will only disallow flight
	//if MT previously enabled it for that player.
	//With them the player should use flight plugins without problems unless he has Multijump boots equipped.
	private void enableFlight(@NotNull final Player p) {
		if (p.getAllowFlight()) return;
		allowFlight.put(p, System.currentTimeMillis());
		p.setAllowFlight(true);
	}

	private void disableFlight(@NotNull final Player p) {
		if (allowFlight.remove(p) != null)
			p.setAllowFlight(false);
	}

	@EventHandler
	public void onQuit(@NotNull final PlayerQuitEvent e) {
		jumpcharge.remove(e.getPlayer());
		disableFlight(e.getPlayer());
	}

	@EventHandler(ignoreCancelled = true)
	public void onMove(@NotNull final PlayerMoveEvent e) {
		final Player p = e.getPlayer();
		if (!p.hasPermission(getUsePermission())) return;

		if (p.getGameMode() == GameMode.CREATIVE || p.getGameMode() == GameMode.SPECTATOR) return;
		// Player can normally fly -> Multijump not needed
		if (p.getAllowFlight() && !allowFlight.containsKey(p)) return;
		if (p.isFlying()) return;

		if (p.isSwimming() || p.isSleeping() || p.isGliding() || p.isDead()) {
			disableFlight(p);
			return;
		}

		// check if something got through and the player can still fly
		long time = System.currentTimeMillis();
		if (allowFlight.getOrDefault(p, time) - time <= -1000L) disableFlight(p);

		final ItemStack boots = p.getInventory().getBoots();
		if (!modManager.isArmorViable(boots)) return;
		final int level = modManager.getModLevel(boots, this);
		if (level == 0) return;

		AtomicInteger jumpcharge = this.jumpcharge.get(e.getPlayer());
		if (jumpcharge == null) {
			jumpcharge = new AtomicInteger(0);
			this.jumpcharge.put(e.getPlayer(), jumpcharge);
		}

		//Check if the player is on the ground for a recharge
		final Block below = p.getLocation().getWorld().getBlockAt(p.getLocation().add(0, -0.1, 0));
		if (!below.isPassable()) {
			if (jumpcharge.get() > 0) {
				//Only decrement one at a time to have at least a little cooldown
				jumpcharge.decrementAndGet();
			}
			disableFlight(p);
		}

		//"Enable" multijump as a ToggleFlight hack
		//This will surely get flagged by anti cheat plugins
		//FIXME: Find a better solution for MultiJump so it does not trigger AntiCheat or can easily be exploited
		else if (below.getType().isAir()) {
			if(jumpcharge.get() < level)
				enableFlight(p);
			else
				disableFlight(p);
		}
		//Disable when swimming
		else if (below.isLiquid()) {
			disableFlight(p);
		}
	}

	//This event only triggers when the Player has AllowFlight set to true.
	//This method of jumping is not clean.
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onFly(@NotNull final PlayerToggleFlightEvent e) {
		final Player p = e.getPlayer();
		if (p.getGameMode() == GameMode.CREATIVE || p.getGameMode() == GameMode.SPECTATOR) return;

		// Player can normally fly -> Multijump not needed
		if (p.getAllowFlight() && !allowFlight.containsKey(p)) return;

		//Do not check for p.isGliding() here as you should be able to jump mid-glide and not have the AllowFlight(true)
		//exploitable while flying with elytra.
		if (p.isFlying() || p.isSwimming() || p.isSleeping() /*|| p.isGliding()*/ || p.isDead()) return;

		final ItemStack boots = p.getInventory().getBoots();
		if (!modManager.isArmorViable(boots)) return;

		final int level = modManager.getModLevel(boots, this);
		if (level == 0) return;

		if (p.hasPermission(getUsePermission())) {
			AtomicInteger jumpcharge = this.jumpcharge.get(e.getPlayer());
			if (jumpcharge == null) {
				jumpcharge = new AtomicInteger(0);
				this.jumpcharge.put(e.getPlayer(), jumpcharge);
			}

			//check if the player has enough jumps remaining
			if (jumpcharge.get() < level) {
				e.setCancelled(true);
				jumpcharge.incrementAndGet();
				//Reset the upwards Motion as if it was a real "new" jump
				p.setVelocity(p.getVelocity().setY(0.42F));
			}

			//This will remove flight if the player uses a fly command
			disableFlight(p);
		}
	}

	//To avoid that you can have flight without the boots
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onArmorWear(@NotNull InventoryClickEvent e) {
		if (!e.getSlotType().equals(InventoryType.SlotType.ARMOR)) return;
		ItemStack item = e.getCurrentItem();
		if (!modManager.isArmorViable(item)) return;
		if (!modManager.hasMod(item, this)) return;
		if (!(e.getWhoClicked() instanceof Player p)) return;
		if (p.getGameMode() == GameMode.ADVENTURE || p.getGameMode() == GameMode.SURVIVAL)
			disableFlight(p);
	}
}

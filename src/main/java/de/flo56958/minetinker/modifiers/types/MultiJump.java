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
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/*
	The idea of allowing the Player to fly and then cancel the ToggleFlightEvent comes from the Plugin
		"Double Jump Advanced" by HotShot - https://www.spigotmc.org/resources/9441/
	But the used algorithm is greatly changed to fit MT. The On-Ground-Check was created from the ground up.
 */
public class MultiJump extends Modifier implements Listener {

	private static MultiJump instance;

	//The Integer tells the Plugin how much Level of MultiJump have been used up by the Player
	private final ConcurrentHashMap<Player, AtomicInteger> jumpcharge = new ConcurrentHashMap<>();

	private MultiJump() {
		super(MineTinker.getPlugin());
		customModelData = 10_057;
	}

	public static MultiJump instance() {
		synchronized (MultiJump.class) {
			if (instance == null) {
				instance = new MultiJump();
			}
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

		config.addDefault("Allowed", true);
		config.addDefault("Color", "%DARK_GREEN%");
		config.addDefault("MaxLevel", 3);
		config.addDefault("SlotCost", 3);

		config.addDefault("EnchantCost", 50);
		config.addDefault("Enchantable", false);
		config.addDefault("MinimumToolLevelRequirement", 1);

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", "RFR");
		config.addDefault("Recipe.Middle", "FDF");
		config.addDefault("Recipe.Bottom", "RFR");

		Map<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("R", Material.RABBIT_FOOT.name());
		recipeMaterials.put("D", Material.DIAMOND_BLOCK.name());
		recipeMaterials.put("F", Material.FEATHER.name());

		config.addDefault("Recipe.Materials", recipeMaterials);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init(Material.RABBIT_FOOT);

		jumpcharge.clear();
	}

	@EventHandler
	public void onQuit(@NotNull final PlayerQuitEvent e) {
		jumpcharge.remove(e.getPlayer());
	}

	@EventHandler
	public void onMove(@NotNull final PlayerMoveEvent e) {
		final Player p = e.getPlayer();
		if (p.getGameMode() == GameMode.CREATIVE) {
			return;
		}

		if (p.isFlying() || p.isSwimming() || p.isSleeping() || p.isGliding() || p.isDead()) {
			return;
		}

		final ItemStack boots = p.getInventory().getBoots();
		if (!modManager.isArmorViable(boots)) {
			return;
		}
		final int level = modManager.getModLevel(boots, this);
		if (level == 0) {
			return;
		}

		if (p.hasPermission("minetinker.modifiers.doublejump.use")) {
			AtomicInteger jumpcharge = this.jumpcharge.get(e.getPlayer());
			if (jumpcharge == null) {
				jumpcharge = new AtomicInteger(0);
				this.jumpcharge.put(e.getPlayer(), jumpcharge);
			}

			//Check if the player is on the ground for a recharge
			final Block below = Objects.requireNonNull(p.getLocation().getWorld(), "Players world is null!")
					.getBlockAt(p.getLocation().add(0, -0.1, 0));
			if (!(below.getType() == Material.CAVE_AIR || below.getType() == Material.AIR
					|| below.getType() == Material.VOID_AIR
					|| below.getType() == Material.WATER || below.getType() == Material.BUBBLE_COLUMN
					|| below.getType() == Material.LAVA)) {

				if (jumpcharge.get() > 0) {
					//Only decrement one at a time to have at least a little cooldown
					jumpcharge.decrementAndGet();
				}
			}

			//"Enable" multijump as a ToggleFlight hack
			//This will surely get flagged by anti cheat plugins
			//FIXME: Find a better solution for MultiJump so it does not trigger AntiCheat
			else if (below.getType() == Material.CAVE_AIR || below.getType() == Material.AIR
					|| below.getType() == Material.VOID_AIR) {
				//This can and will remove flight if the player uses a fly command
				p.setAllowFlight(jumpcharge.get() < level);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onFly(@NotNull final PlayerToggleFlightEvent e) {
		final Player p = e.getPlayer();
		if (p.getGameMode() == GameMode.CREATIVE) {
			return;
		}

		if (p.isFlying() || p.isSwimming() || p.isSleeping() || p.isGliding() || p.isDead()) {
			return;
		}

		final ItemStack boots = p.getInventory().getBoots();
		if (!modManager.isArmorViable(boots)) {
			return;
		}
		final int level = modManager.getModLevel(boots, this);
		if (level == 0) {
			return;
		}

		if (p.hasPermission("minetinker.modifiers.doublejump.use")) {
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
			p.setAllowFlight(false);
		}
	}
}

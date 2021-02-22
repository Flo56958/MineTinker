package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.events.MTEntityDamageByEntityEvent;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.ConfigurationManager;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

public class Evasive extends Modifier implements Listener {

	private static Evasive instance;
	private int percentPerLevel;
	private int cooldownInSeconds;
	private double sneakMultiplier;
	private double sprintMultiplier;
	private double pvpMultiplier;

	private final HashMap<String, Long> cooldownTracker = new HashMap<>();

	private Evasive() {
		super(MineTinker.getPlugin());
		customModelData = 10_044;
	}

	public static Evasive instance() {
		synchronized (Evasive.class) {
			if (instance == null) {
				instance = new Evasive();
			}
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Evasive";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Arrays.asList(ToolType.BOOTS, ToolType.LEGGINGS);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Color", "%GRAY%");
		config.addDefault("MaxLevel", 5);
		config.addDefault("SlotCost", 3);
		config.addDefault("PercentPerLevel", 10);
		config.addDefault("SneakMultiplier", 0.5);
		config.addDefault("SprintMultiplier", 2.0);
		config.addDefault("PvPMultiplier", 0.5);
		config.addDefault("CooldownInSeconds", 5);
		config.addDefault("Sound", true);

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);
		config.addDefault("MinimumToolLevelRequirement", 1);

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", "D D");
		config.addDefault("Recipe.Middle", " F ");
		config.addDefault("Recipe.Bottom", "D D");

		Map<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("D", Material.DIAMOND.name());
		recipeMaterials.put("F", Material.FEATHER.name());

		config.addDefault("Recipe.Materials", recipeMaterials);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init(Material.FEATHER);

		this.percentPerLevel = config.getInt("PercentPerLevel", 5);
		this.sneakMultiplier = config.getDouble("SneakMultiplier", 0.5);
		this.sprintMultiplier = config.getDouble("SprintMultiplier", 2.0);
		this.cooldownInSeconds = config.getInt("CooldownInSeconds", 5);
		this.pvpMultiplier = config.getDouble("PvPMultiplier", 1.0);

		this.description = this.description.replaceAll("%chance", String.valueOf(this.percentPerLevel))
				.replaceAll("%sneak", String.valueOf(this.sneakMultiplier))
				.replaceAll("%sprint", String.valueOf(this.sprintMultiplier))
				.replaceAll("%pvp", String.valueOf(this.pvpMultiplier));
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void effect(MTEntityDamageByEntityEvent event) {
		if (!event.getPlayer().equals(event.getEvent().getEntity())) {
			return; //when event was not triggered by the armor
		}

		Player player = event.getPlayer();
		ItemStack tool = event.getTool();

		if (!player.hasPermission("minetinker.modifiers.evasive.use")) {
			return;
		}

		if (!modManager.hasMod(tool, this)) {
			return;
		}

		//cooldown checker
		Long time = System.currentTimeMillis();
		if (this.cooldownInSeconds > 0) {
			Long cd = cooldownTracker.get(player.getUniqueId().toString());
			if (cd != null) { //was on cooldown
				if (time - cd > this.cooldownInSeconds * 1000L || player.getGameMode() == GameMode.CREATIVE) {
					cooldownTracker.remove(player.getUniqueId().toString());
				} else {
					ChatWriter.logModifier(player, event, this, tool, "Cooldown");
					return; //still on cooldown
				}
			}
		}

		//calculating chance
		Random rand = new Random();
		int level = modManager.getModLevel(tool, this);

		double chance = this.percentPerLevel * level;
		String chanceCalculation = "Chance(" + chance;
		if (player.isSneaking()) {
			chance *= this.sneakMultiplier;
			chanceCalculation += " ->(Sneak) " + chance;
		}
		if (player.isSprinting()) {
			chance *= this.sprintMultiplier;
			chanceCalculation += " ->(Sprint) " + chance;
		}
		if (event.getEvent().getDamager() instanceof Player) {
			chance *= this.pvpMultiplier;
			chanceCalculation += " ->(PvP) " + chance;
		} else if (event.getEvent().getDamager() instanceof Arrow) {
			if (((Arrow) event.getEvent().getDamager()).getShooter() instanceof Player) {
				chance *= this.pvpMultiplier;
				chanceCalculation += " ->(PvP) " + chance;
			}
		}

		chanceCalculation += ")";

		int c = rand.nextInt(100);

		if (c > chance) {
			ChatWriter.logModifier(player, event, this, tool, chanceCalculation, "Failed(" + c + "/" + chance + ")");
			return;
		}


		if (this.cooldownInSeconds > 0)
			cooldownTracker.put(player.getUniqueId().toString(), time);

		event.setCancelled(true);
		if (getConfig().getBoolean("Sound", true))
			player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 1.0f, 1.0f);

		ChatWriter.logModifier(player, event, this, tool, chanceCalculation, "Success(" + c + "/" + chance + ")");
	}
}

package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import de.flo56958.MineTinker.Utilities.LanguageManager;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoidNetting extends Modifier implements Listener {

	private static VoidNetting instance;
	private int radiusPerLevel;
	private int cooldownInSeconds;
	private double cooldownReductionPerLevel;
	private boolean particles;
	private boolean sound;

	private final HashMap<String, Long> cooldownTracker = new HashMap<>();

	private VoidNetting() {
		super(Main.getPlugin());
		customModelData = 10_049;
	}

	public static VoidNetting instance() {
		synchronized (VoidNetting.class) {
			if (instance == null) {
				instance = new VoidNetting();
			}
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Void-Netting";
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
		config.addDefault("Name", "Void Netting");
		config.addDefault("ModifierItemName", "Net of the Void");
		config.addDefault("Description", "Teleports you to a safe place in a radius of %radius blocks per level when you fall into the void. Cooldown: %cmax to %cmin seconds (depends on level)");
		config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Void-Netting-Modifier");
		config.addDefault("Color", "%DARK_GRAY%");
		config.addDefault("MaxLevel", 5);
		config.addDefault("SlotCost", 1);
		config.addDefault("RadiusPerLevel", 10);
		config.addDefault("CooldownInSeconds", 300);
		config.addDefault("CooldownReductionPerLevel", 0.2);
		config.addDefault("Particles", true);
		config.addDefault("Sound", true);
		config.addDefault("OverrideLanguagesystem", false);

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", "SES");
		config.addDefault("Recipe.Middle", "ETE");
		config.addDefault("Recipe.Bottom", "SES");

		Map<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("E", Material.ENDER_EYE.name());
		recipeMaterials.put("T", Material.TOTEM_OF_UNDYING.name());
		recipeMaterials.put("S", Material.SHULKER_SHELL.name());

		config.addDefault("Recipe.Materials", recipeMaterials);

		// Save Config
		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		// Initialize modifier
		init(Material.COBWEB);

		this.radiusPerLevel = config.getInt("RadiusPerLevel", 5);
		this.cooldownInSeconds = config.getInt("CooldownInSeconds", 300);
		this.cooldownReductionPerLevel = config.getDouble("CooldownReductionPerLevel", 0.2);
		this.particles = config.getBoolean("Particles", true);
		this.sound = config.getBoolean("Sound", true);

		this.description = this.description.replaceAll("%radius", String.valueOf(this.radiusPerLevel))
				.replaceAll("%cmax", String.valueOf(this.cooldownInSeconds))
				.replaceAll("%cmin", String.valueOf(Math.round(this.cooldownInSeconds * Math.pow(1.0 - this.cooldownReductionPerLevel, this.getMaxLvl() - 1))));
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onDamage(EntityDamageEvent event) {
		if (!this.isAllowed()) return;
		if (event.getCause() != EntityDamageEvent.DamageCause.VOID) return;
		if (!(event.getEntity() instanceof Player)) return;

		Player player = (Player) event.getEntity();

		if (!player.hasPermission("minetinker.void-netting.use")) return;

		ItemStack armor = player.getInventory().getBoots();
		if (!modManager.isArmorViable(armor)) return;

		if (!modManager.hasMod(armor, this)) return;

		int level = modManager.getModLevel(armor, this);

		//cooldown checker
		Long time = System.currentTimeMillis();
		long cooldownTime = (long) (this.cooldownInSeconds * 1000 * Math.pow(1.0 - this.cooldownReductionPerLevel, level - 1));
		if (this.cooldownInSeconds > 0) {
			Long cd = cooldownTracker.get(player.getUniqueId().toString());
			if (cd != null) { //was on cooldown
				if (time - cd > cooldownTime || player.getGameMode() == GameMode.CREATIVE) {
					cooldownTracker.remove(player.getUniqueId().toString());
				} else {
					ChatWriter.logModifier(player, event, this, armor, "Cooldown");
					ChatWriter.sendActionBar(player, this.getName() + ": " + LanguageManager.getString("Alert.OnCooldown", player));
					return; //still on cooldown
				}
			}
		}

		Location loc = player.getLocation();
		for (int i = 0; i < level * radiusPerLevel; i++) {
			for (int d = -i; d <= i; d++) {
				int y = loc.getWorld().getHighestBlockYAt(loc.getBlockX() + d, loc.getBlockZ() + i - Math.abs(d));
				if (y > 1) {
					loc = new Location(loc.getWorld(), loc.getBlockX() + d, y + 2, loc.getBlockZ() + i - Math.abs(d));
					break;
				}
			}
			for (int d = -i + 1; d < i; d++) {
				int y = loc.getWorld().getHighestBlockYAt(loc.getBlockX() + d, loc.getBlockZ() + Math.abs(d) - i);
				if (y > 1) {
					loc = new Location(loc.getWorld(), loc.getBlockX() + d, y + 2,loc.getBlockZ() + Math.abs(d) - i);
					break;
				}
			}
		}

		if (loc.equals(player.getLocation())) {
			//No suitable place found
			ChatWriter.logModifier(player, event, this, armor, "Could not find suitable Block to teleport!");
			ChatWriter.sendActionBar(player, this.getName() + ": " + LanguageManager.getString("Modifier.Void-Netting.CouldNotFindBlock", player));
			return;
		}

		Location oldLoc = player.getLocation().clone();
		player.teleport(loc);
		player.setVelocity(new Vector(0, 0.1, 0)); //Slow the fall
		cooldownTracker.put(player.getUniqueId().toString(), time);
		ChatWriter.logModifier(player, event, this, armor,
				String.format("Location(%d/%d/%d -> %d/%d/%d)",
						oldLoc.getBlockX(), oldLoc.getBlockY(), oldLoc.getBlockZ(),
						loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()), String.format("Cooldown(%ds)", cooldownTime));

		if (this.particles) {
			loc.getWorld().spawnParticle(Particle.PORTAL, loc, 20);
			loc.getWorld().spawnParticle(Particle.PORTAL, oldLoc, 20);
		}
		if (this.sound) {
			player.getWorld().playSound(loc, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
			player.getWorld().playSound(oldLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
		}
	}
}

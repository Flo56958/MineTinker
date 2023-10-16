package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.modifiers.CooldownModifier;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.ConfigurationManager;
import de.flo56958.minetinker.utils.LanguageManager;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoidNetting extends CooldownModifier implements Listener {

	private static VoidNetting instance;
	private int radiusPerLevel;
	private boolean particles;
	private boolean sound;

	private VoidNetting() {
		super(MineTinker.getPlugin());
		customModelData = 10_049;
	}

	public static VoidNetting instance() {
		synchronized (VoidNetting.class) {
			if (instance == null)
				instance = new VoidNetting();
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
		config.addDefault("Color", "%DARK_GRAY%");
		config.addDefault("MaxLevel", 5);
		config.addDefault("SlotCost", 1);
		config.addDefault("RadiusPerLevel", 10);
		config.addDefault("CooldownInSeconds", 3600.0);
		config.addDefault("CooldownReductionPerLevel", 0.4);
		config.addDefault("Particles", true);
		config.addDefault("Sound", true);

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);
		config.addDefault("MinimumToolLevelRequirement", 1);

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
		this.cooldownInSeconds = config.getDouble("CooldownInSeconds", 3600.0);
		this.cooldownReductionPerLevel = config.getDouble("CooldownReductionPerLevel", 0.4);
		this.particles = config.getBoolean("Particles", true);
		this.sound = config.getBoolean("Sound", true);

		this.description = this.description.replaceAll("%radius", String.valueOf(this.radiusPerLevel))
				.replaceAll("%cmax", String.valueOf(this.cooldownInSeconds))
				.replaceAll("%cmin", String.valueOf(this.getCooldown(this.getMaxLvl()) / 1000.0));
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onDamage(EntityDamageEvent event) {
		if (event.getCause() != EntityDamageEvent.DamageCause.VOID) return;
		if (!(event.getEntity() instanceof Player player)) return;
		if (!player.hasPermission(getUsePermission())) return;

		ItemStack armor = player.getInventory().getBoots();
		if (!modManager.isArmorViable(armor)) return;
		if (!modManager.hasMod(armor, this)) return;
		int level = modManager.getModLevel(armor, this);

		if (onCooldown(player, armor, true, event)) return;

		//Add small cooldown to improve server performance
		setCooldown(armor, Math.round(this.cooldownInSeconds * 0.05));

		Bukkit.getScheduler().runTaskAsynchronously(MineTinker.getPlugin(), () -> {
			//run effect async as it does not need to stop all action on server if search takes to long
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
				ChatWriter.sendActionBar(player, this.getName() + ": "
						+ LanguageManager.getString("Modifier.Void-Netting.CouldNotFindBlock", player));
				return;
			}
			Location oldLoc = player.getLocation().clone();
			ChatWriter.logModifier(player, event, this, armor,
					String.format("Location(%d/%d/%d -> %d/%d/%d)",
							oldLoc.getBlockX(), oldLoc.getBlockY(), oldLoc.getBlockZ(),
							loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()), String.format("Cooldown(%ds)",
							this.getCooldown(level) / 1000));

			Location finalLoc = loc;
			Bukkit.getScheduler().runTask(MineTinker.getPlugin(), () -> { //Teleport needs to be in sync
				//Slow the fall
				player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 20, 0, false));
				player.teleport(finalLoc);
				player.setVelocity(new Vector(0, 0, 0));

				setCooldown(armor);

				if (this.particles) {
					finalLoc.getWorld().spawnParticle(Particle.PORTAL, finalLoc, 20);
					finalLoc.getWorld().spawnParticle(Particle.PORTAL, oldLoc, 20);
				}
				if (this.sound) {
					player.getWorld().playSound(finalLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
					player.getWorld().playSound(oldLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
				}
			});
		});
	}
}

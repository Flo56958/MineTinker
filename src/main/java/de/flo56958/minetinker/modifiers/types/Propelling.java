package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.modifiers.CooldownModifier;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.ConfigurationManager;
import de.flo56958.minetinker.utils.LanguageManager;
import de.flo56958.minetinker.utils.data.DataHandler;
import de.flo56958.minetinker.utils.playerconfig.PlayerConfigurationManager;
import de.flo56958.minetinker.utils.playerconfig.PlayerConfigurationOption;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Propelling extends CooldownModifier implements Listener {

	private static Propelling instance;
	private int durabilityLoss;
	private double speedPerLevel;

	private Propelling() {
		super(MineTinker.getPlugin());
		customModelData = 10_028;
	}

	public static Propelling instance() {
		synchronized (Propelling.class) {
			if (instance == null)
				instance = new Propelling();
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Propelling";
	}

	@Override
	public List<ToolType> getAllowedTools() {
	return Arrays.asList(ToolType.ELYTRA, ToolType.TRIDENT, ToolType.MACE, ToolType.SWORD, ToolType.AXE);
	}

	@Override
	public @NotNull List<Enchantment> getAppliedEnchantments() {
		return Arrays.asList(Enchantment.RIPTIDE, Enchantment.WIND_BURST);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Color", "%GOLD%");
		config.addDefault("MaxLevel", 3);
		config.addDefault("SlotCost", 1);
		config.addDefault("ModifierItemMaterial", Material.FIREWORK_STAR.name());
		config.addDefault("CooldownInSeconds", 5.0);
		config.addDefault("Elytra.DurabilityLoss", 10);
		config.addDefault("Elytra.SpeedPerLevel", 0.15);

		config.addDefault("EnchantCost", 25);
		config.addDefault("Enchantable", true);
		config.addDefault("MinimumToolLevelRequirement", 1);

		config.addDefault("Recipe.Enabled", false);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init();

		this.durabilityLoss = config.getInt("Elytra.DurabilityLoss", 10);
		this.speedPerLevel = config.getDouble("Elytra.SpeedPerLevel", 0.05);
		this.cooldownInSeconds = config.getDouble("CooldownInSeconds", 5.0);
	}

	@Override
	public boolean applyMod(Player player, ItemStack tool, boolean isCommand) {
		ItemMeta meta = tool.getItemMeta();

		if (meta != null) {
			if (ToolType.TRIDENT.contains(tool.getType()))
				meta.addEnchant(Enchantment.RIPTIDE, modManager.getModLevel(tool, this), true);
			else if (!ToolType.ELYTRA.contains(tool.getType()))
				meta.addEnchant(Enchantment.WIND_BURST, modManager.getModLevel(tool, this), true);
			//Elytra does not get an enchantment

			tool.setItemMeta(meta);
		}

		return true;
	}

	@EventHandler(ignoreCancelled = true)
	public void onElytraSneak(final PlayerToggleSneakEvent event) {
		final Player player = event.getPlayer();

		if (event.isSneaking()) return;
		if (!player.isGliding()) return;
		if (!player.hasPermission(getUsePermission())) return;

		final ItemStack elytra = player.getInventory().getChestplate();
		if (elytra == null) return;
		if (!(modManager.isArmorViable(elytra) && ToolType.ELYTRA.contains(elytra.getType()))) return;
		if (!modManager.hasMod(elytra, this)) return;
		if (onCooldown(player, elytra, true, event)) return;

		if (!DataHandler.triggerItemDamage(player, elytra, this.durabilityLoss)) {
			player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.5F, 0.5F);
			return;
		}

		final int level = modManager.getModLevel(elytra, this);
		final Location loc = player.getLocation();
		final Vector dir = loc.getDirection().normalize().multiply(1 + speedPerLevel * level);

		if (dir.dot(player.getVelocity()) <= 0) {
			player.setVelocity(dir);
		} else {
			player.setVelocity(player.getVelocity().add(dir));
		}

		if (loc.getWorld() != null) {
			if (PlayerConfigurationManager.getInstance().getBoolean(player, PARTICLES))
				loc.getWorld().spawnParticle(Particle.CLOUD, loc, 30, 0.5F, 0.5F, 0.5F, 0.0F);

			if (PlayerConfigurationManager.getInstance().getBoolean(player, SOUND))
				loc.getWorld().playSound(loc, Sound.ENTITY_ENDER_DRAGON_FLAP, 0.5F, 0.5F);
		}

		setCooldown(player, elytra);
		ChatWriter.logModifier(player, event, this, elytra);
	}

	private final PlayerConfigurationOption PARTICLES =
			new PlayerConfigurationOption(this,"particles", PlayerConfigurationOption.Type.BOOLEAN,
					LanguageManager.getString("Modifier.Propelling.PCO_particle"), true);

	private final PlayerConfigurationOption SOUND =
			new PlayerConfigurationOption(this,"sound", PlayerConfigurationOption.Type.BOOLEAN,
					LanguageManager.getString("Modifier.Propelling.PCO_sound"), true);

	@Override
	public List<PlayerConfigurationOption> getPCIOptions() {
		List<PlayerConfigurationOption> playerConfigurationOptions = super.getPCIOptions();
		playerConfigurationOptions.add(PARTICLES);
		playerConfigurationOptions.add(SOUND);

		playerConfigurationOptions.sort(Comparator.comparing(PlayerConfigurationOption::displayName));
		return playerConfigurationOptions;
	}
}

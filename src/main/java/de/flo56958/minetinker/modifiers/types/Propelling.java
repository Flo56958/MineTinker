package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.ConfigurationManager;
import de.flo56958.minetinker.utils.LanguageManager;
import de.flo56958.minetinker.utils.data.DataHandler;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Propelling extends Modifier implements Listener {

	private static Propelling instance;
	private int durabilityLoss;
	private double speedPerLevel;
	private boolean sound;
	private boolean particles;
	private boolean considerReinforced;
	private boolean useLessDurability;

	private double cooldownInSeconds;

	private Propelling() {
		super(MineTinker.getPlugin());
		customModelData = 10_028;
	}

	public static Propelling instance() {
		synchronized (Propelling.class) {
			if (instance == null) {
				instance = new Propelling();
			}
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Propelling";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Arrays.asList(ToolType.ELYTRA, ToolType.TRIDENT);
	}

	@Override
	public @NotNull List<Enchantment> getAppliedEnchantments() {
		return Collections.singletonList(Enchantment.RIPTIDE);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Color", "%GOLD%");
		config.addDefault("MaxLevel", 3);
		config.addDefault("SlotCost", 1);
		config.addDefault("CooldownInSeconds", 5.0);
		config.addDefault("Elytra.DurabilityLoss", 10);
		config.addDefault("Elytra.SpeedPerLevel", 0.15);
		config.addDefault("Elytra.Sound", true);
		config.addDefault("Elytra.Particles", true);
		config.addDefault("ConsiderReinforced", true); //should Reinforced (Unbreaking) be considered
		config.addDefault("ReinforcedUseLessDurability", true); //should Reinforced lessen the durability damage or if false chance to don't use durability at all

		config.addDefault("EnchantCost", 25);
		config.addDefault("Enchantable", true);
		config.addDefault("MinimumToolLevelRequirement", 1);

		config.addDefault("Recipe.Enabled", false);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init(Material.FIREWORK_STAR);

		this.durabilityLoss = config.getInt("Elytra.DurabilityLoss", 10);
		this.speedPerLevel = config.getDouble("Elytra.SpeedPerLevel", 0.05);
		this.considerReinforced = config.getBoolean("ConsiderReinforced", true);
		this.useLessDurability = config.getBoolean("ReinforcedUseLessDurability", true);
		this.cooldownInSeconds = config.getDouble("CooldownInSeconds", 5.0);

		this.sound = config.getBoolean("Elytra.Sound", true);
		this.particles = config.getBoolean("Elytra.Particles", true);
	}

	@Override
	public boolean applyMod(Player player, ItemStack tool, boolean isCommand) {
		ItemMeta meta = tool.getItemMeta();

		if (meta != null) {
			if (ToolType.TRIDENT.contains(tool.getType())) {
				meta.addEnchant(Enchantment.RIPTIDE, modManager.getModLevel(tool, this), true);
			} //Elytra does not get an enchantment

			tool.setItemMeta(meta);
		}

		return true;
	}

	@EventHandler(ignoreCancelled = true)
	public void onElytraSneak(PlayerToggleSneakEvent event) {
		Player player = event.getPlayer();

		if (event.isSneaking()) {
			return;
		}

		if (!player.isGliding()) {
			return;
		}

		if (!player.hasPermission("minetinker.modifiers.propelling.use")) {
			return;
		}

		ItemStack elytra = player.getInventory().getChestplate();

		if (elytra == null) {
			return;
		}

		if (!(modManager.isArmorViable(elytra) && ToolType.ELYTRA.contains(elytra.getType()))) {
			return;
		}

		if (!modManager.hasMod(elytra, this)) {
			return;
		}

		if (cooldownInSeconds > 1 / 20.0) {
			long time = System.currentTimeMillis();
			Long playerTime = DataHandler.getTag(elytra, this.getKey() + "cooldown", PersistentDataType.LONG, false);
			if (playerTime != null) {
				if (time - playerTime < this.cooldownInSeconds * 1000) {
					ChatWriter.logModifier(player, event, this, elytra, "Cooldown");
					ChatWriter.sendActionBar(player, this.getName() + ": " + LanguageManager.getString("Alert.OnCooldown", player));
					return;
				}
			}
		}

		int maxDamage = elytra.getType().getMaxDurability();
		ItemMeta meta = elytra.getItemMeta();

		if (meta instanceof Damageable dam && !meta.isUnbreakable()
				&& (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE)) {

			int loss = durabilityLoss;

			if (considerReinforced) {
				int level = modManager.getModLevel(elytra, Reinforced.instance());
				if (useLessDurability) {
					loss = (int) Math.round(durabilityLoss * (1.0 / (level + 1)));
				} else {
					int durabilityChance = 60 + (40 / (level + 1));
					if (new Random().nextInt(100) > durabilityChance) {
						loss = 0;
					}
				}
			}

			if (maxDamage <= dam.getDamage() + loss + 1) {
				player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.5F, 0.5F);
				return;
			}

			dam.setDamage(dam.getDamage() + loss);
			elytra.setItemMeta(meta);
		}

		int level = modManager.getModLevel(elytra, this);
		Location loc = player.getLocation();
		Vector dir = loc.getDirection().normalize();

		player.setVelocity(dir.multiply(1 + speedPerLevel * level).add(player.getVelocity().multiply(0.1f)));

		if (particles && loc.getWorld() != null) {
			loc.getWorld().spawnParticle(Particle.CLOUD, loc, 30, 0.5F, 0.5F, 0.5F, 0.0F);
		}

		if (sound) player.getWorld().playSound(loc, Sound.ENTITY_ENDER_DRAGON_FLAP, 0.5F, 0.5F);

		if (cooldownInSeconds > 1 / 20.0) {
			DataHandler.setTag(elytra, this.getKey() + "cooldown", System.currentTimeMillis(),
					PersistentDataType.LONG, false);
		}

		ChatWriter.logModifier(player, event, this, elytra);
	}
}

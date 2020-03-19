package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
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
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Propelling extends Modifier implements Listener {

	private static Propelling instance;
	private int durabilityLoss;
	private double speedPerLevel;
	private boolean sound;
	private boolean particles;

	private Propelling() {
		super(Main.getPlugin());
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
	public List<Enchantment> getAppliedEnchantments() {
		return Collections.singletonList(Enchantment.RIPTIDE);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Name", "Propelling");
		config.addDefault("ModifierItemName", "Enchanted Fireworkstar");
		config.addDefault("Description", "Propel yourself through the air.");
		config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Propelling-Modifier");
		config.addDefault("Color", "%GOLD%");
		config.addDefault("MaxLevel", 3);
		config.addDefault("Elytra.DurabilityLoss", 10);
		config.addDefault("Elytra.SpeedPerLevel", 0.05);
		config.addDefault("Elytra.Sound", true);
		config.addDefault("Elytra.Particles", true);

		config.addDefault("EnchantCost", 25);
		config.addDefault("Enchantable", true);

		config.addDefault("Recipe.Enabled", false);
		config.addDefault("OverrideLanguagesystem", false);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init(Material.FIREWORK_STAR, true);

		durabilityLoss = config.getInt("Elytra.DurabilityLoss", 10);
		speedPerLevel = config.getDouble("Elytra.SpeedPerLevel", 0.05);

		sound = config.getBoolean("Elytra.Sound", true);
		particles = config.getBoolean("Elytra.Particles", true);
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

		if (!(modManager.isArmorViable(elytra) && ToolType.ELYTRA.contains(elytra.getType()))) {
			return;
		}

		if (!modManager.hasMod(elytra, this)) {
			return;
		}

		int maxDamage = elytra.getType().getMaxDurability();
		ItemMeta meta = elytra.getItemMeta();

		if (meta instanceof Damageable && !meta.isUnbreakable()) {
			Damageable dam = (Damageable) meta;

			if (maxDamage <= dam.getDamage() + durabilityLoss + 1) {
				player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.5F, 0.5F);
				return;
			}

			dam.setDamage(dam.getDamage() + durabilityLoss);
			elytra.setItemMeta(meta);
		}

		int level = modManager.getModLevel(elytra, this);
		Location loc = player.getLocation();
		Vector dir = loc.getDirection().normalize();

		player.setVelocity(player.getVelocity().add(dir.multiply(1 + speedPerLevel * level)));

		if (sound && loc.getWorld() != null) {
			loc.getWorld().spawnParticle(Particle.CLOUD, loc, 30, 0.5F, 0.5F, 0.5F, 0.0F);
		}

		if (particles) player.playSound(loc, Sound.ENTITY_ENDER_DRAGON_FLAP, 0.5F, 0.5F);
	}
}

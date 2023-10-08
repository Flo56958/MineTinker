package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.api.events.MTEntityDamageByEntityEvent;
import de.flo56958.minetinker.api.events.MTProjectileHitEvent;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ConfigurationManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class Explosive extends Modifier implements Listener {

	private static Explosive instance;
	private float powerPerLevel;
	private boolean setFire;
	private boolean blockBreak;

	private Explosive() {
		super(MineTinker.getPlugin());
		customModelData = 10_063;
	}

	public static Explosive instance() {
		synchronized (Explosive.class) {
			if (instance == null) {
				instance = new Explosive();
			}
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Explosive";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.CROSSBOW, ToolType.SWORD, ToolType.TRIDENT);
	}

	@Override
	public @NotNull List<Enchantment> getAppliedEnchantments() {
		return new ArrayList<>();
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Color", "%RED%");
		config.addDefault("MaxLevel", 5);
		config.addDefault("SlotCost", 1);
		config.addDefault("PowerPerLevel", 0.33);
		config.addDefault("SetFire", false);
		config.addDefault("BlockBreak", false);

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);
		config.addDefault("MinimumToolLevelRequirement", 1);

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", "TTT");
		config.addDefault("Recipe.Middle", "TTT");
		config.addDefault("Recipe.Bottom", "TTT");

		Map<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("T", Material.TNT.name());

		config.addDefault("Recipe.Materials", recipeMaterials);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init(Material.TNT);

		this.powerPerLevel = (float) config.getDouble("PowerPerLevel", 1.0);
		this.setFire = config.getBoolean("SetFire", false);
		this.blockBreak = config.getBoolean("BlockBreak", false);

		this.description = this.description.replace("%power", String.valueOf(this.powerPerLevel));
	}

	@EventHandler(ignoreCancelled = true)
	public void onProjectileHit(final MTProjectileHitEvent event) {
		Player p = event.getPlayer();
		ItemStack tool = event.getTool();
		if (event.getEvent().getEntity().hasMetadata(this.getKey())) return;
		if (!modManager.hasMod(tool, this)) return;

		final int level = modManager.getModLevel(tool, this);

		if (!p.hasPermission("minetinker.modifiers.explosive.use")) return;

		final Location loc = event.getEvent().getEntity().getLocation();

		loc.getWorld().createExplosion(loc, this.powerPerLevel * level, false, false, p);
		event.getEvent().getEntity().setMetadata(this.getKey(), new FixedMetadataValue(MineTinker.getPlugin(), 0));
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityHit(final MTEntityDamageByEntityEvent event) {
		Player p = event.getPlayer();
		ItemStack tool = event.getTool();
		if (event.getEvent().getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION ||
				event.getEvent().getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) return;
		if (!modManager.hasMod(tool, this)) return;

		final int level = modManager.getModLevel(tool, this);

		if (!p.hasPermission("minetinker.modifiers.explosive.use")) return;

		final Location loc = event.getEntity().getLocation().add(0, 0.2, 0);

		loc.getWorld().createExplosion(loc, this.powerPerLevel * level, this.setFire, this.blockBreak, p);
	}
}

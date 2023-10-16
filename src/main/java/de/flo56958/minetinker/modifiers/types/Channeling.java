package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.api.events.MTEntityDamageByEntityEvent;
import de.flo56958.minetinker.api.events.MTProjectileHitEvent;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.ConfigurationManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class Channeling extends Modifier implements Listener {

	private static Channeling instance;

	private boolean worksOnlyInStorms;
	private int chancePerLevel;

	private Channeling() {
		super(MineTinker.getPlugin());
		customModelData = 10_007;
	}

	public static Channeling instance() {
		synchronized (Channeling.class) {
			if (instance == null)
				instance = new Channeling();
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Channeling";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Arrays.asList(ToolType.TRIDENT, ToolType.BOW, ToolType.AXE, ToolType.CROSSBOW, ToolType.SWORD);
	}

	@Override
	public @NotNull List<Enchantment> getAppliedEnchantments() {
		return Collections.singletonList(Enchantment.CHANNELING);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Color", "%DARK_AQUA%");
		config.addDefault("MaxLevel", 1);
		config.addDefault("SlotCost", 1);

		config.addDefault("EnchantCost", 50);
		config.addDefault("Enchantable", false);
		config.addDefault("MinimumToolLevelRequirement", 1);
		config.addDefault("ChancePerLevel", 100);

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", "SPS");
		config.addDefault("Recipe.Middle", "PCP");
		config.addDefault("Recipe.Bottom", "SPS");

		config.addDefault("WorksOnlyInStorms", true);

		Map<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("S", Material.SEA_LANTERN.name());
		recipeMaterials.put("P", Material.PRISMARINE_SHARD.name());
		recipeMaterials.put("C", Material.CREEPER_HEAD.name());

		config.addDefault("Recipe.Materials", recipeMaterials);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init(Material.PRISMARINE_SHARD);

		worksOnlyInStorms = config.getBoolean("WorksOnlyInStorms", true);
		chancePerLevel = config.getInt("ChancePerLevel", 100);

		this.description = this.description.replaceAll("%amount", this.chancePerLevel + "%");
	}

	@Override
	public boolean applyMod(Player player, @NotNull ItemStack tool, final boolean isCommand) {
		ItemMeta meta = tool.getItemMeta();

		if (meta != null) {
			if (ToolType.TRIDENT.contains(tool.getType()))
				meta.addEnchant(Enchantment.CHANNELING, modManager.getModLevel(tool, this), true);

			tool.setItemMeta(meta);
		}

		return true;
	}

	@EventHandler(ignoreCancelled = true)
	private void onProjectileHit(@NotNull final MTProjectileHitEvent event) {
		final ItemStack tool = event.getTool();
		if (!event.getPlayer().hasPermission(getUsePermission())) return;
		if (event.getEvent().getHitEntity() != null) return;
		if (!modManager.hasMod(tool, this)) return;
		if (new Random().nextInt(100) >= modManager.getModLevel(tool, this) * chancePerLevel) return;

		final Location loc = event.getEvent().getEntity().getLocation();
		if (!loc.getWorld().hasStorm() && worksOnlyInStorms) return;
		loc.getWorld().strikeLightning(loc);
		ChatWriter.logModifier(event.getPlayer(), event, this, tool, String.format("Location: %d/%d/%d",
				loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
	}

	@EventHandler(ignoreCancelled = true)
	private void onEntityHit(@NotNull final MTEntityDamageByEntityEvent event) {
		final ItemStack tool = event.getTool();
		if (!event.getPlayer().hasPermission(getUsePermission())) return;
		if (!modManager.hasMod(tool, this)) return;
		if (new Random().nextInt(100) >= modManager.getModLevel(tool, this) * chancePerLevel) return;

		final Location loc = event.getEntity().getLocation();
		if (!loc.getWorld().hasStorm() && worksOnlyInStorms) return;
		loc.getWorld().strikeLightning(loc);
		ChatWriter.logModifier(event.getPlayer(), event, this, tool, String.format("Location: %d/%d/%d",
				loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
	}
}

package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.api.events.MTEntityDamageByEntityEvent;
import de.flo56958.minetinker.api.events.MTEntityDamageEvent;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.ConfigurationManager;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sunblazer extends Modifier implements Listener {

	private static Sunblazer instance;
	private double damageMultiplierPerLevel;
	private Sunblazer() {
		super(MineTinker.getPlugin());
		customModelData = 10_050;
	}

	public static Sunblazer instance() {
		synchronized (Sunblazer.class) {
			if (instance == null)
				instance = new Sunblazer();
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Sunblazer";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Arrays.asList(ToolType.SWORD, ToolType.AXE, ToolType.TRIDENT, ToolType.ELYTRA, ToolType.BOOTS,
				ToolType.CHESTPLATE, ToolType.CROSSBOW, ToolType.BOW, ToolType.HELMET, ToolType.LEGGINGS);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Color", "%YELLOW%");
		config.addDefault("MaxLevel", 3);
		config.addDefault("SlotCost", 1);
		config.addDefault("ModifierItemMaterial", Material.DAYLIGHT_DETECTOR.name());
		config.addDefault("DamageMultiplierPerLevel", 0.1);

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);
		config.addDefault("MinimumToolLevelRequirement", 1);

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", "GIG");
		config.addDefault("Recipe.Middle", "IDI");
		config.addDefault("Recipe.Bottom", "GIG");

		Map<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("G", Material.GOLD_BLOCK.name());
		recipeMaterials.put("D", Material.DAYLIGHT_DETECTOR.name());
		recipeMaterials.put("I", Material.GOLD_INGOT.name());

		config.addDefault("Recipe.Materials", recipeMaterials);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init();

		this.damageMultiplierPerLevel = config.getDouble("DamageMultiplierPerLevel", 0.1);
		this.description = this.description
				.replaceAll("%amountmin", String.format("%.2f", (Math.pow(this.damageMultiplierPerLevel + 1.0, 1) - 1) * 100))
				.replaceAll("%amountmax", String.format("%.2f", (Math.pow(this.damageMultiplierPerLevel + 1.0, this.getMaxLvl()) - 1) * 100));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onHit(MTEntityDamageByEntityEvent event) {
		Player player = event.getPlayer();
		ItemStack tool = event.getTool();
		effect(event, event.getEvent(), tool, player);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onHit(MTEntityDamageEvent event) {
		Player player = event.getPlayer();
		ItemStack tool = event.getTool();
		effect(event, event.getEvent(), tool, player);
	}

	private void effect(Event e, EntityDamageEvent event, ItemStack tool, Player player) {
		if (!player.hasPermission(getUsePermission())) return;

		int level = modManager.getModLevel(tool, this);
		if (level <= 0) return;

		final double damageMultiplier = Math.pow(this.damageMultiplierPerLevel + 1.0, level) - 1.0;
		long worldtime = player.getWorld().getTime() / 1000;
		double daytimeMultiplier;
		if (player.getWorld().getEnvironment() == World.Environment.NORMAL) {
			if (worldtime >= 12) //value range: [-1.0; 1.0]
				daytimeMultiplier = -(6 - Math.abs(18 - worldtime)) / 6.0;
			else
				daytimeMultiplier = (6 - Math.abs(6 - worldtime)) / 6.0;
		} else if (player.getWorld().getEnvironment() == World.Environment.NETHER)
			daytimeMultiplier = 1.0;
		else if (player.getWorld().getEnvironment() == World.Environment.THE_END)
			daytimeMultiplier = -1.0;
		else return;

		final double oldDamage = event.getDamage();

		double multi = (1.0 + damageMultiplier * daytimeMultiplier);
		if (multi < 0.0) multi = 0.0;
		String damagemultiplier = String.format("DamageMultiplier(%.2f * %.2f = %.2f)", damageMultiplier, daytimeMultiplier, damageMultiplier * daytimeMultiplier);
		if (modManager.isToolViable(tool)) { //Player attacked
			event.setDamage(oldDamage * multi);
			ChatWriter.logModifier(player, e, this, tool, damagemultiplier,
					String.format("Damage(%.2f -> %.2f [x%.2f])", oldDamage, event.getDamage(), multi));
		} else if (modManager.isArmorViable(tool)) { //Player got attacked
			if (multi == 0.0) multi = 0.00001;
			event.setDamage(oldDamage / multi);
			ChatWriter.logModifier(player, e, this, tool, damagemultiplier,
					String.format("Damage(%.2f -> %.2f [/%.2f])", oldDamage, event.getDamage(), multi));
		}
	}
}

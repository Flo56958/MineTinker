package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KineticPlating extends Modifier implements Listener {

	private static KineticPlating instance;
	private int amount;

	private KineticPlating() {
		super(Main.getPlugin());
		customModelData = 10_016;
	}

	public static KineticPlating instance() {
		synchronized (KineticPlating.class) {
			if (instance == null) {
				instance = new KineticPlating();
			}
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "KineticPlating";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Collections.singletonList(ToolType.ELYTRA);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Name", "Kinetic Plating");
		config.addDefault("ModifierItemName", "Kinetic Plate");
		config.addDefault("Description", "Reduces the damage taken by crashing with an Elytra by %amount% per Level.");
		config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Kinetic-Plating-Modifier");
		config.addDefault("Color", "%GRAY%");
		config.addDefault("MaxLevel", 5);
		config.addDefault("Amount", 20); //How much XP should be dropped when triggered

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", "PIP");
		config.addDefault("Recipe.Middle", "III");
		config.addDefault("Recipe.Bottom", "PIP");

		Map<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("I", Material.IRON_BLOCK.name());
		recipeMaterials.put("P", Material.PHANTOM_MEMBRANE.name());

		config.addDefault("Recipe.Materials", recipeMaterials);

		config.addDefault("OverrideLanguagesystem", false);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init(Material.IRON_BLOCK, true);

		this.amount = config.getInt("Amount", 1);
		this.description = this.description.replace("%amount", "" + this.amount);
	}

	//----------------------------------------------------------

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void effect(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player)) return;
		if (event.getCause() != EntityDamageEvent.DamageCause.FLY_INTO_WALL) return;

		Player player = (Player) event.getEntity();
		if (!player.hasPermission("minetinker.modifiers.kineticplating.use")) return;
		ItemStack elytra = player.getInventory().getChestplate();

		if (!modManager.hasMod(elytra, this)) return;

		int level = modManager.getModLevel(elytra, this);
		double damageMod = 1.0 - (this.amount * level);
		if (damageMod < 0.0) damageMod = 0.0;
		event.setDamage(event.getDamage() * damageMod);
	}
}

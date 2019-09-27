package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.MTEntityDamageByEntityEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

public class Lifesteal extends Modifier implements Listener {

	private static Lifesteal instance;
	private int percentPerLevel;
	private int percentToTrigger;

	private Lifesteal() {
		super(Main.getPlugin());

	}

	public static Lifesteal instance() {
		synchronized (Lifesteal.class) {
			if (instance == null) {
				instance = new Lifesteal();
			}
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Lifesteal";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.CROSSBOW, ToolType.SWORD, ToolType.TRIDENT);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Name", "Lifesteal");
		config.addDefault("ModifierItemName", "Bloodinfused Netherrack");
		config.addDefault("Description", "Get HP when hitting enemies!");
		config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Lifesteal-Modifier");
		config.addDefault("Color", "%DARK_RED%");
		config.addDefault("MaxLevel", 3);
		config.addDefault("PercentToTrigger", 50);
		config.addDefault("PercentOfDamagePerLevel", 10);
		config.addDefault("OverrideLanguagesystem", false);

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", "SRS");
		config.addDefault("Recipe.Middle", "RNR");
		config.addDefault("Recipe.Bottom", "SRS");

		Map<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("N", Material.NETHERRACK.name());
		recipeMaterials.put("R", Material.ROTTEN_FLESH.name());
		recipeMaterials.put("S", Material.SOUL_SAND.name());

		config.addDefault("Recipe.Materials", recipeMaterials);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init(Material.NETHERRACK, true);

		this.percentPerLevel = config.getInt("PercentOfDamagePerLevel", 10);
		this.percentToTrigger = config.getInt("PercentToTrigger", 50);

		this.description = this.description.replace("%amount", "" + this.percentPerLevel).replace("%chance", "" + this.percentToTrigger);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH) //because of Melting
	public void effect(MTEntityDamageByEntityEvent event) {


		if (event.getPlayer().equals(event.getEvent().getEntity())) {
			return; //when event was triggered by the armor
		}

		Player p = event.getPlayer();
		ItemStack tool = event.getTool();

		if (!p.hasPermission("minetinker.modifiers.lifesteal.use")) {
			return;
		}

		if (!modManager.hasMod(tool, this)) {
			return;
		}

		Random rand = new Random();

		if (rand.nextInt(100) > this.percentToTrigger) {
			return;
		}

		int level = modManager.getModLevel(tool, this);
		double damage = event.getEvent().getDamage();
		double recovery = damage * ((percentPerLevel * level) / 100.0);
		double health = p.getHealth() + recovery;

		AttributeInstance attribute = p.getAttribute(Attribute.GENERIC_MAX_HEALTH);

		if (attribute != null) {
			// for IllegalArgumentExeption if Health is biggen than MaxHealth
			if (health > attribute.getValue()) {
				health = attribute.getValue();
			}

			p.setHealth(health);
		}

		ChatWriter.log(false, p.getDisplayName() + " triggered Lifesteal on " + ItemGenerator.getDisplayName(tool) + ChatColor.GRAY + " (" + tool.getType().toString() + ") and got " + recovery + " health back!");
	}
}

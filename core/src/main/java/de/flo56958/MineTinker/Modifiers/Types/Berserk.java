package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Berserk extends Modifier implements Listener {

	private static Berserk instance;
	private int boostTime;
	private int trigger;

	private Berserk() {
		super(Main.getPlugin());

	}

	public static Berserk instance() {
		synchronized (Berserk.class) {
			if (instance == null) {
				instance = new Berserk();
			}
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Berserk";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Collections.singletonList(ToolType.CHESTPLATE);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Name", "Berserk");
		config.addDefault("ModifierItemName", "Bloody Redstone");
		config.addDefault("Description", "Gain an extra strength boost for %duration seconds when your health drops below %percent%!");
		config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Berserk-Modifier");
		config.addDefault("Color", "%DARK_RED%");
		config.addDefault("MaxLevel", 3);
		config.addDefault("BoostTimeInTicks", 200);
		config.addDefault("TriggerPercent", 20);
		config.addDefault("OverrideLanguagesystem", false);

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", "FRF");
		config.addDefault("Recipe.Middle", "RFR");
		config.addDefault("Recipe.Bottom", "FRF");

		Map<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("F", Material.ROTTEN_FLESH.name());
		recipeMaterials.put("R", Material.REDSTONE.name());

		config.addDefault("Recipe.Materials", recipeMaterials);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		this.boostTime = config.getInt("BoostTimeInTicks");
		this.trigger = config.getInt("TriggerPercent");

		init(Material.REDSTONE, true);

		this.description = this.description
				.replace("%duration", String.valueOf(this.boostTime / 20))
				.replace("%percent", String.valueOf(trigger));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onHit(EntityDamageEvent e) {
		if (!(e.getEntity() instanceof Player)) return;
		Player p = (Player) e.getEntity();

		if (p.getInventory().getChestplate() == null) return;

		int level = modManager.getModLevel(p.getInventory().getChestplate(), this);
		if (level <= 0) return;

		double lifeAfterDamage = p.getHealth() - e.getFinalDamage();

		if (p.getHealth() / p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() > trigger / 100.0
			&& lifeAfterDamage / p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() <= trigger / 100.0) {
			p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, boostTime, level - 1));
		}
	}
}

package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.ConfigurationManager;
import de.flo56958.minetinker.utils.LanguageManager;
import de.flo56958.minetinker.utils.data.DataHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class Berserk extends Modifier implements Listener {

	private static Berserk instance;
	private int boostTime;
	private int trigger;

	private Berserk() {
		super(MineTinker.getPlugin());
		customModelData = 10_006;
	}

	public static Berserk instance() {
		synchronized (Berserk.class) {
			if (instance == null)
				instance = new Berserk();
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
		config.addDefault("Color", "%DARK_RED%");
		config.addDefault("MaxLevel", 3);
		config.addDefault("SlotCost", 1);
		config.addDefault("BoostTimeInTicks", 200);
		config.addDefault("TriggerPercent", 20);

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);
		config.addDefault("MinimumToolLevelRequirement", 1);

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

		init(Material.REDSTONE);

		this.description = this.description
				.replace("%duration", String.valueOf(this.boostTime / 20))
				.replace("%percent", String.valueOf(trigger));
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onHit(@NotNull EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (!player.hasPermission(getUsePermission())) return;

		final ItemStack chest = player.getInventory().getChestplate();
		if (!modManager.isArmorViable(chest)) return;

		int modifierLevel = modManager.getModLevel(chest, this);

		if (modifierLevel <= 0) return;

		final double lifeAfterDamage = player.getHealth() - event.getFinalDamage();
		AttributeInstance healthAttr = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);

		double maxHealth = 20;
		if (healthAttr != null) maxHealth = healthAttr.getValue();

		if (player.getHealth() / maxHealth > trigger / 100.0 && lifeAfterDamage / maxHealth <= trigger / 100.0) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, boostTime,
					modifierLevel - 1));
			ChatWriter.logModifier(player, event, this, chest,
					"Time(" + boostTime + ")", "Amplifier(" + (modifierLevel - 1) + ")");

			// Track stats
			final int stat = DataHandler.getTagOrDefault(chest, getKey() + "_stat_used", PersistentDataType.INTEGER, 0);
			DataHandler.setTag(chest, getKey() + "_stat_used", stat + 1, PersistentDataType.INTEGER);
		}
	}
	@Override
	public List<String> getStatistics(ItemStack item) {
		// Get stats
		final List<String> lore = new ArrayList<>();
		final int stat = DataHandler.getTagOrDefault(item, getKey() + "_stat_used", PersistentDataType.INTEGER, 0);
		lore.add(ChatColor.WHITE + LanguageManager.getString("Modifier.Berserk.Statistic_Used")
				.replaceAll("%amount", String.valueOf(stat)));
		return lore;
	}
}

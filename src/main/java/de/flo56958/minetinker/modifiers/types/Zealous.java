package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.api.events.MTEntityDamageByEntityEvent;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.ConfigurationManager;
import de.flo56958.minetinker.utils.LanguageManager;
import de.flo56958.minetinker.utils.PlayerInfo;
import de.flo56958.minetinker.utils.data.DataHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Zealous extends Modifier implements Listener {

	private static Zealous instance;

	private double percentPerSecond;
	private boolean allowHelmet;

	private Zealous() {
		super(MineTinker.getPlugin());
		customModelData = 10_066;
	}

	public static Zealous instance() {
		synchronized (Zealous.class) {
			if (instance == null)
				instance = new Zealous();
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Zealous";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		if (allowHelmet) // this makes pvp battles infinite again
			return Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.CROSSBOW, ToolType.SWORD, ToolType.TRIDENT, ToolType.HELMET, ToolType.MACE);
		return Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.CROSSBOW, ToolType.SWORD, ToolType.TRIDENT, ToolType.MACE);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Color", "%GOLD%");
		config.addDefault("MaxLevel", 5);
		config.addDefault("SlotCost", 1);
		config.addDefault("ModifierItemMaterial", Material.BLAZE_ROD.name());
		config.addDefault("PercentPerSecond", 1.0D);
		config.addDefault("AllowHelmet", false);

		config.addDefault("EnchantCost", 25);
		config.addDefault("Enchantable", true);
		config.addDefault("MinimumToolLevelRequirement", 1);

		config.addDefault("Recipe.Enabled", false);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init();

		this.percentPerSecond = config.getDouble("PercentPerSecond", 1.0D);
		this.description = this.description.replace("%percent", String.valueOf(this.percentPerSecond));
		this.percentPerSecond /= 100.0D;
		this.allowHelmet = config.getBoolean("AllowHelmet", false);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	private void onDamage(@NotNull final MTEntityDamageByEntityEvent event) {
		final Player player = event.getPlayer();
		if (!player.hasPermission(this.getUsePermission())) return;

		final ItemStack tool = event.getTool();
		final int modLevel = modManager.getModLevel(tool, this);
		if (modLevel <= 0) return;

		final long combatTime = PlayerInfo.getCombatTagTime(player);
		if (combatTime <= 0) return;

		final double damage = event.getEvent().getDamage();
		final double addDamage = this.percentPerSecond * modLevel * damage * combatTime / 1000.0D;
		event.getEvent().setDamage(damage + addDamage);

		ChatWriter.logModifier(player, event, this, tool,
				String.format("Damage(%f + %f = %f)", damage, addDamage, event.getEvent().getDamage()),
				String.format("CombatTime(%d)", combatTime));

		//Track stats
		final double stat_damage = DataHandler.getTagOrDefault(tool, getKey() + "_stat_damage", PersistentDataType.DOUBLE, 0.0D);
		DataHandler.setTag(tool, getKey() + "_stat_damage", stat_damage + addDamage, PersistentDataType.DOUBLE);

		final long stat_time = DataHandler.getTagOrDefault(tool, getKey() + "_stat_time", PersistentDataType.LONG, 0L);
		DataHandler.setTag(tool, getKey() + "_stat_time", Math.max(stat_time, combatTime), PersistentDataType.LONG);
	}

	@Override
	public List<String> getStatistics(ItemStack item) {
		final List<String> lore = new ArrayList<>();
		final double stat_damage = DataHandler.getTagOrDefault(item, getKey() + "_stat_damage", PersistentDataType.DOUBLE, 0.0D);
		final long stat_time = DataHandler.getTagOrDefault(item, getKey() + "_stat_time", PersistentDataType.LONG, 0L);
		lore.add(ChatColor.WHITE + LanguageManager.getString("Modifier.Zealous.Statistic_Time")
				.replaceAll("%amount", String.format("%,.2f", stat_time / 1000.0D)));
		lore.add(ChatColor.WHITE + LanguageManager.getString("Modifier.Zealous.Statistic_Damage")
				.replaceAll("%amount", String.format("%,.2f", stat_damage)));
		return lore;
	}
}

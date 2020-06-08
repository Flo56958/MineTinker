package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.events.MTEntityDamageByEntityEvent;
import de.flo56958.minetinker.Main;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.ConfigurationManager;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Glowing extends Modifier implements Listener {

	private static Glowing instance;
	private int duration;
	private double durationMultiplier;

	private Glowing() {
		super(Main.getPlugin());
		customModelData = 10_012;
	}

	public static Glowing instance() {
		synchronized (Glowing.class) {
			if (instance == null) {
				instance = new Glowing();
			}
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Glowing";
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
		config.addDefault("Name", "Glowing");
		config.addDefault("ModifierItemName", "Ender-Glowstone");
		config.addDefault("Description", "Makes Enemies glow!");
		config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Glowing-Modifier");
		config.addDefault("Color", "%YELLOW%");
		config.addDefault("MaxLevel", 3);
		config.addDefault("SlotCost", 1);
		config.addDefault("Duration", 200); //ticks INTEGER (20 ticks ~ 1 sec)
		config.addDefault("DurationMultiplier", 1.4); //Duration * (Multiplier^Level) DOUBLE
		config.addDefault("OverrideLanguagesystem", false);

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", "GGG");
		config.addDefault("Recipe.Middle", "GEG");
		config.addDefault("Recipe.Bottom", "GGG");

		Map<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("G", Material.GLOWSTONE_DUST.name());
		recipeMaterials.put("E", Material.ENDER_EYE.name());

		config.addDefault("Recipe.Materials", recipeMaterials);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init(Material.GLOWSTONE);

		this.duration = config.getInt("Duration", 200);
		this.durationMultiplier = config.getDouble("DurationMultiplier", 1.4);

		this.description = this.description.replaceAll("%durationmin", String.valueOf(this.duration / 20.0d))
				.replaceAll("%durationmax", String.valueOf(Math.round(this.duration * Math.pow(this.durationMultiplier, this.getMaxLvl() - 1)) / 20.0d));
	}

	@EventHandler(ignoreCancelled = true)
	public void effect(MTEntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof LivingEntity)) {
			return;
		}

		Player player = event.getPlayer();
		ItemStack tool = event.getTool();
		LivingEntity entity = (LivingEntity) event.getEntity();

		if (!player.hasPermission("minetinker.modifiers.glowing.use")) {
			return;
		}

		if (!modManager.hasMod(tool, this)) {
			return;
		}

		if (modManager.hasMod(tool, Shrouded.instance())) { //Should not trigger twice
			return;
		}

		entity.addPotionEffect(getPotionEffect(event, entity, player, tool));
	}

	public PotionEffect getPotionEffect(@Nullable Event event, @Nullable Entity entity, @NotNull Player player, @NotNull ItemStack tool) {
		int level = modManager.getModLevel(tool, this);
		int duration = (int) (this.duration * Math.pow(this.durationMultiplier, (level - 1)));
		if (entity == null) {
			ChatWriter.logModifier(player, event, this, tool, "Duration(" + duration + ")");
		} else {
			ChatWriter.logModifier(player, event, this, tool,
					"Duration(" + duration + ")",
					"Entity(" + entity.getType().toString() + ")");
		}

		return new PotionEffect(PotionEffectType.GLOWING, duration, 0, false, false);
	}

}

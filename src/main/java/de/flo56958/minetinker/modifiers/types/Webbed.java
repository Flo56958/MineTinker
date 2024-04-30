package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.api.events.MTEntityDamageByEntityEvent;
import de.flo56958.minetinker.api.events.MTProjectileHitEvent;
import de.flo56958.minetinker.data.ToolType;
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
import org.bukkit.event.player.PlayerMoveEvent;
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

public class Webbed extends Modifier implements Listener {

	private static Webbed instance;
	private int duration;
	private double durationMultiplier;
	private int effectAmplifier;
	private boolean givesImmunity;

	private Webbed() {
		super(MineTinker.getPlugin());
		customModelData = 10_043;
	}

	public static Webbed instance() {
		synchronized (Webbed.class) {
			if (instance == null)
				instance = new Webbed();
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Webbed";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.CROSSBOW, ToolType.SWORD, ToolType.TRIDENT,
				ToolType.HELMET, ToolType.CHESTPLATE, ToolType.LEGGINGS, ToolType.BOOTS, ToolType.ELYTRA);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Color", "%WHITE%");
		config.addDefault("MaxLevel", 3);
		config.addDefault("SlotCost", 1);
		config.addDefault("ModifierItemMaterial", Material.COBWEB.name());
		config.addDefault("Duration", 60); //ticks (20 ticks ~ 1 sec)
		config.addDefault("DurationMultiplier", 1.2);//Duration * (Multiplier^Level)
		config.addDefault("EffectAmplifier", 2); //per Level (Level 1 = 0, Level 2 = 2, Level 3 = 4, ...)
		config.addDefault("GivesImmunityToEffect", true);

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);
		config.addDefault("MinimumToolLevelRequirement", 1);

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", "WWW");
		config.addDefault("Recipe.Middle", "WBW");
		config.addDefault("Recipe.Bottom", "WWW");

		Map<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("W", Material.COBWEB.name());
		recipeMaterials.put("B", Material.BLUE_ICE.name());

		config.addDefault("Recipe.Materials", recipeMaterials);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init();

		this.duration = config.getInt("Duration", 60);
		this.durationMultiplier = config.getDouble("DurationMultiplier", 1.2);
		this.effectAmplifier = config.getInt("EffectAmplifier", 2);
		this.givesImmunity = config.getBoolean("GivesImmunityToEffect", true);

		this.description = this.description.replace("%duration", String.valueOf(this.duration))
				.replace("%multiplier", String.valueOf(this.durationMultiplier));
	}

	@EventHandler(ignoreCancelled = true)
	public void onMove(PlayerMoveEvent event) {
		if (!this.givesImmunity) return;

		Player player = event.getPlayer();
		if (!player.hasPermission(getUsePermission())) {
			return;
		}

		boolean hasWebbed = false;
		ItemStack armor = null;
		for (ItemStack stack : player.getInventory().getArmorContents()) {
			if (stack == null) continue;
			if (modManager.hasMod(stack, this)) {
				hasWebbed = true;
				armor = stack;
				break;
			}
		}

		if (!hasWebbed) return;
		if (player.hasPotionEffect(PotionEffectType.SLOWNESS)) {
			player.removePotionEffect(PotionEffectType.SLOWNESS);
			ChatWriter.logModifier(player, event, this, armor, "RemoveEffect");
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void effect(MTEntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof LivingEntity)) {
			return;
		}

		effect(event.getPlayer(), event.getTool(), event.getEntity(), event);
	}

	@EventHandler(ignoreCancelled = true)
	public void effect(MTProjectileHitEvent event) {
		if (!(event.getEvent().getHitEntity() instanceof LivingEntity)) {
			return;
		}

		if (!ToolType.FISHINGROD.contains(event.getTool().getType())) {
			return;
		}

		effect(event.getPlayer(), event.getTool(), event.getEvent().getHitEntity(), event);
	}

	private void effect(Player player, ItemStack tool, Entity entity, Event event) {
		if (!player.hasPermission(getUsePermission())) {
			return;
		}

		if (entity.isDead()) {
			return;
		}

		if (!modManager.hasMod(tool, this)) {
			return;
		}

		if (modManager.hasMod(tool, Shrouded.instance())) { //Should not trigger twice
			return;
		}

		((LivingEntity) entity).addPotionEffect(getPotionEffect(event, entity, player, tool));
	}

	public PotionEffect getPotionEffect(@Nullable Event event, @Nullable Entity entity, @NotNull Player player, @NotNull ItemStack tool) {
		int level = modManager.getModLevel(tool, this);
		int duration = (int) (this.duration * Math.pow(this.durationMultiplier, (level - 1)));
		int amplifier = this.effectAmplifier * (level - 1);
		if (entity == null) {
			ChatWriter.logModifier(player, event, this, tool,
					"Duration(" + duration + ")",
					"Amplifier(" + amplifier + ")");
		} else {
			ChatWriter.logModifier(player, event, this, tool,
					"Duration(" + duration + ")",
					"Amplifier(" + amplifier + ")",
					"Entity(" + entity.getType() + ")");
		}

		return new PotionEffect(PotionEffectType.SLOWNESS, duration, amplifier, false, false);
	}
}

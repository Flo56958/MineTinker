package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.api.events.MTEntityDamageByEntityEvent;
import de.flo56958.minetinker.data.Lists;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.ConfigurationManager;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Poisonous extends Modifier implements Listener {

	private static Poisonous instance;
	private int duration;
	private double durationMultiplier;
	private int effectAmplifier;
	private boolean dropPoisonedMeat;
	private boolean effectHealsPlayer;

	private Poisonous() {
		super(MineTinker.getPlugin());
		customModelData = 10_026;
	}

	public static Poisonous instance() {
		synchronized (Poisonous.class) {
			if (instance == null)
				instance = new Poisonous();
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Poisonous";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.CROSSBOW, ToolType.SWORD, ToolType.TRIDENT,
				ToolType.HELMET, ToolType.CHESTPLATE, ToolType.LEGGINGS, ToolType.BOOTS, ToolType.ELYTRA, ToolType.MACE);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Color", "%DARK_GREEN%");
		config.addDefault("MaxLevel", 5);
		config.addDefault("SlotCost", 1);
		config.addDefault("ModifierItemMaterial", Material.ROTTEN_FLESH.name());
		config.addDefault("Duration", 120); //ticks INTEGER (20 ticks ~ 1 sec)
		config.addDefault("DurationMultiplier", 1.1); //Duration * (Multiplier^Level) DOUBLE
		config.addDefault("EffectAmplifier", 1); //per Level (Level 1 = 0, Level 2 = 1, Level 3 = 2, ...) INTEGER
		config.addDefault("DropRottenMeatIfPoisoned", true);
		config.addDefault("EffectHealsPlayer", true);

		config.addDefault("EnchantCost", 20);
		config.addDefault("Enchantable", true);
		config.addDefault("MinimumToolLevelRequirement", 1);

		config.addDefault("Recipe.Enabled", false);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init();

		this.duration = config.getInt("Duration", 120);
		this.durationMultiplier = config.getDouble("DurationMultiplier", 1.1);
		this.effectAmplifier = config.getInt("EffectAmplifier", 2);
		this.dropPoisonedMeat = config.getBoolean("DropRottenMeatIfPoisoned", true);
		this.effectHealsPlayer = config.getBoolean("EffectHealsPlayer", true);

		this.description = this.description.replace("%duration", String.valueOf(this.duration))
				.replace("%multiplier", String.valueOf(this.durationMultiplier));
	}

	@EventHandler(ignoreCancelled = true)
	public void effect(MTEntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof LivingEntity)) return;

		Player player = event.getPlayer();
		ItemStack tool = event.getTool();

		if (!player.hasPermission(getUsePermission())) return;

		if (!modManager.hasMod(tool, this)) return;

		if (modManager.hasMod(tool, Shrouded.instance())) return; //Should not trigger twice

		((LivingEntity) event.getEntity()).addPotionEffect(getPotionEffect(event, event.getEntity(), player, tool));
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onDamage(final EntityDamageEvent event) {
		if (event.getCause() != EntityDamageEvent.DamageCause.POISON) return;
		if (!(event.getEntity() instanceof Player player)) return;
		if (!this.effectHealsPlayer) return;
		if (!player.hasPermission(getUsePermission())) return;

		boolean hasPoisonous = false;
		ItemStack armor = null;
		for (final ItemStack stack : player.getInventory().getArmorContents()) {
			if (stack == null) continue;
			if (modManager.hasMod(stack, this)) {
				hasPoisonous = true;
				armor = stack;
				break;
			}
		}

		if (!hasPoisonous) return;

		double damage = event.getDamage();
		if (damage > 0) {
			event.setDamage(0);
			double health = player.getHealth();
			player.setHealth(Math.min(health + damage, player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
			ChatWriter.logModifier(player, event, this, armor, String.format("Health(%.2f -> %.2f)", health, player.getHealth()));
		}
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

		return new PotionEffect(PotionEffectType.POISON, duration, amplifier, false, false);
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if (!dropPoisonedMeat) return;

		LivingEntity mob = event.getEntity();
		Player player = mob.getKiller();

		if (player == null) return;

		if (Lists.WORLDS.contains(player.getWorld().getName())) return;

		boolean isPoisoned = false;

		for (PotionEffect potionEffect : mob.getActivePotionEffects()) {
			if (potionEffect.getType() == PotionEffectType.POISON) {
				isPoisoned = true;
				break;
			}
		}

		if (!isPoisoned) return;

		int numberOfMeat = 0;
		int numberOfPotatoes = 0;

		Iterator<ItemStack> iterator = event.getDrops().iterator();

		while (iterator.hasNext()) {
			ItemStack drop = iterator.next();
			if (isMeat(drop)) {
				iterator.remove();
				numberOfMeat++;
			} else if (drop.getType() == Material.POTATO) {
				iterator.remove();
				numberOfPotatoes++;
			}
		}

		ChatWriter.logModifier(player, event, this, player.getInventory().getItemInMainHand(),
				"Entity(" + event.getEntity().getType() + ")");

		if (numberOfMeat > 0) event.getDrops().add(new ItemStack(Material.ROTTEN_FLESH, numberOfMeat));
		if (numberOfPotatoes > 0) event.getDrops().add(new ItemStack(Material.POISONOUS_POTATO, numberOfPotatoes));
	}

	private boolean isMeat(@NotNull ItemStack item) {
		return switch (item.getType()) {
			case BEEF, PORKCHOP, COD, SALMON, TROPICAL_FISH, PUFFERFISH, CHICKEN, RABBIT, MUTTON -> true;
			default -> false;
		};
	}
}

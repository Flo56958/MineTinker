package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.MTEntityDamageByEntityEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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

	private Poisonous() {
		super(Main.getPlugin());
		customModelData = 10_026;
	}

	public static Poisonous instance() {
		synchronized (Poisonous.class) {
			if (instance == null) {
				instance = new Poisonous();
			}
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
				ToolType.HELMET, ToolType.CHESTPLATE, ToolType.LEGGINGS, ToolType.BOOTS, ToolType.ELYTRA);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Name", "Poisonous");
		config.addDefault("ModifierItemName", "Enhanced Rotten Flesh");
		config.addDefault("Description", "Poisons enemies!");
		config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Poisonous-Modifier");
		config.addDefault("Color", "%DARK_GREEN%");
		config.addDefault("MaxLevel", 5);
		config.addDefault("SlotCost", 1);
		config.addDefault("Duration", 120); //ticks INTEGER (20 ticks ~ 1 sec)
		config.addDefault("DurationMultiplier", 1.1); //Duration * (Multiplier^Level) DOUBLE
		config.addDefault("EffectAmplifier", 2); //per Level (Level 1 = 0, Level 2 = 2, Level 3 = 4, ...) INTEGER
		config.addDefault("DropRottenMeatIfPoisoned", true);

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", true);

		config.addDefault("Recipe.Enabled", false);
		config.addDefault("OverrideLanguagesystem", false);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init(Material.ROTTEN_FLESH);

		this.duration = config.getInt("Duration", 120);
		this.durationMultiplier = config.getDouble("DurationMultiplier", 1.1);
		this.effectAmplifier = config.getInt("EffectAmplifier", 2);
		this.dropPoisonedMeat = config.getBoolean("DropRottenMeatIfPoisoned", true);

		this.description = this.description.replace("%duration", String.valueOf(this.duration))
				.replace("%multiplier", String.valueOf(this.durationMultiplier));
	}

	@EventHandler(ignoreCancelled = true)
	public void effect(MTEntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof LivingEntity)) {
			return;
		}

		Player player = event.getPlayer();
		ItemStack tool = event.getTool();

		if (!player.hasPermission("minetinker.modifiers.poisonous.use")) {
			return;
		}

		if (!modManager.hasMod(tool, this)) {
			return;
		}

		int level = modManager.getModLevel(tool, this);

		int duration = (int) (this.duration * Math.pow(this.durationMultiplier, (level - 1)));
		int amplifier = this.effectAmplifier * (level - 1);
		((LivingEntity) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.POISON, duration,
				amplifier, false, false));
		ChatWriter.logModifier(player, event, this, tool,
				"Duration(" + duration + ")",
				"Amplifier(" + amplifier + ")",
				"Entity(" + event.getEntity().getType().toString() + ")");
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if (!dropPoisonedMeat) {
			return;
		}

		LivingEntity mob = event.getEntity();
		Player player = mob.getKiller();

		if (player == null) {
			return;
		}

		if (Lists.WORLDS.contains(player.getWorld().getName())) {
			return;
		}

		boolean isPoisoned = false;

		for (PotionEffect potionEffect : mob.getActivePotionEffects()) {
			if (potionEffect.getType() == PotionEffectType.POISON) {
				isPoisoned = true;
				break;
			}
		}

		if (!isPoisoned) {
			return;
		}

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
				"Entity(" + event.getEntity().getType().toString() + ")");

		if (numberOfMeat > 0) event.getDrops().add(new ItemStack(Material.ROTTEN_FLESH, numberOfMeat));
		if (numberOfPotatoes > 0) event.getDrops().add(new ItemStack(Material.POISONOUS_POTATO, numberOfPotatoes));
	}

	private boolean isMeat(ItemStack item) {
		switch (item.getType()) {
			case BEEF:
			case PORKCHOP:
			case COD:
			case SALMON:
			case TROPICAL_FISH:
			case PUFFERFISH:
			case CHICKEN:
			case RABBIT:
			case MUTTON:
				return true;
			default:
				return false;
		}
	}
}

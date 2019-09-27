package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.MTEntityDamageByEntityEvent;
import de.flo56958.MineTinker.Events.MTProjectileHitEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Shulking extends Modifier implements Listener {

	private static Shulking instance;
	private int duration;
	private int effectAmplifier;

	private Shulking() {
		super(Main.getPlugin());

	}

	public static Shulking instance() {
		synchronized (Shulking.class) {
			if (instance == null) {
				instance = new Shulking();
			}
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Shulking";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.CROSSBOW, ToolType.SWORD, ToolType.TRIDENT, ToolType.FISHINGROD,
				ToolType.HELMET, ToolType.CHESTPLATE, ToolType.LEGGINGS, ToolType.BOOTS, ToolType.ELYTRA);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Name", "Shulking"); //wingardium leviosa
		config.addDefault("ModifierItemName", "Enhanced Shulkershell");
		config.addDefault("Description", "Make enemies levitate!");
		config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Shulking-Modifier");
		config.addDefault("Color", "%LIGHT_PURPLE%");
		config.addDefault("MaxLevel", 10);
		config.addDefault("Duration", 20); //ticks (20 ticks ~ 1 sec)
		config.addDefault("EffectAmplifier", 2); //per Level (Level 1 = 0, Level 2 = 2, Level 3 = 4, ...)
		config.addDefault("OverrideLanguagesystem", false);

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", "S");
		config.addDefault("Recipe.Middle", "C");
		config.addDefault("Recipe.Bottom", "S");

		Map<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("S", Material.SHULKER_SHELL.name());
		recipeMaterials.put("C", Material.CHORUS_FRUIT.name());

		config.addDefault("Recipe.Materials", recipeMaterials);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init(Material.SHULKER_SHELL, true);

		this.duration = config.getInt("Duration", 20);
		this.effectAmplifier = config.getInt("EffectAmplifier", 2);

		this.description = this.description.replace("%duration", "" + this.duration);
	}

	@EventHandler(ignoreCancelled = true)
	public void effect(MTEntityDamageByEntityEvent event) {


		if (!(event.getEntity() instanceof LivingEntity)) {
			return;
		}

		Player p = event.getPlayer();
		ItemStack tool = event.getTool();

		effect(p, tool, event.getEntity());
	}

	@EventHandler(ignoreCancelled = true)
	public void effect(MTProjectileHitEvent event) {


		if (!(event.getEvent().getHitEntity() instanceof LivingEntity)) {
			return;
		}

		Player p = event.getPlayer();
		ItemStack tool = event.getTool();

		if (!ToolType.FISHINGROD.contains(tool.getType())) {
			return;
		}

		effect(p, tool, event.getEvent().getHitEntity());
	}

	private void effect(Player p, ItemStack tool, Entity ent) {
		if (!p.hasPermission("minetinker.modifiers.shulking.use")) {
			return;
		}

		if (!modManager.hasMod(tool, this)) {
			return;
		}

		int level = modManager.getModLevel(tool, this);
		int amplifier = this.effectAmplifier * (level - 1);

		((LivingEntity) ent).addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, this.duration, amplifier, false, false));

		ChatWriter.log(false, p.getDisplayName() + " triggered Shulking on " + ItemGenerator.getDisplayName(tool) + ChatColor.GRAY + " (" + tool.getType().toString() + ")!");

	}
}

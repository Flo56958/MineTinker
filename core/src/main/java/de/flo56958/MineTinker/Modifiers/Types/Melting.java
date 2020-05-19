package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.MTEntityDamageByEntityEvent;
import de.flo56958.MineTinker.Events.MTEntityDamageEvent;
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
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class Melting extends Modifier implements Listener {

	private static Melting instance;
	private double bonusMultiplier;
	private boolean cancelBurning;

	private Melting() {
		super(Main.getPlugin());
		customModelData = 10_022;
	}

	public static Melting instance() {
		synchronized (Melting.class) {
			if (instance == null) {
				instance = new Melting();
			}
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Melting";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.CROSSBOW, ToolType.SWORD, ToolType.CHESTPLATE, ToolType.LEGGINGS);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Name", "Melting");
		config.addDefault("ModifierItemName", "Enchanted Magma block");
		config.addDefault("Description", "Extra damage against burning enemies and less damage taken while on fire!");
		config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Melting-Modifier");
		config.addDefault("Color", "%GOLD%");
		config.addDefault("MaxLevel", 3);
		config.addDefault("SlotCost", 1);
		config.addDefault("BonusMultiplier", 0.1); //Percent of Bonus-damage per Level or Damage-reduction on Armor
		config.addDefault("CancelBurningOnArmor", true);

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", true);

		config.addDefault("Recipe.Enabled", false);
		config.addDefault("OverrideLanguagesystem", false);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init(Material.MAGMA_BLOCK);

		this.bonusMultiplier = config.getDouble("BonusMultiplier", 0.1);
		this.cancelBurning = config.getBoolean("CancelBurningOnArmor", true);

		this.description = this.description.replaceAll("%amount", String.valueOf(this.bonusMultiplier * 100));
	}

	@EventHandler(ignoreCancelled = true)
	public void effect(MTEntityDamageByEntityEvent event) {
		Player player = event.getPlayer();
		ItemStack tool = event.getTool();

		if (!player.hasPermission("minetinker.modifiers.melting.use")) {
			return;
		}

		if (!modManager.hasMod(tool, this)) {
			return;
		}

		if (event.getPlayer().equals(event.getEvent().getEntity())) {
            /*
            The melting effect if the Player gets damaged. getTool = Armor piece
             */
			int level = modManager.getModLevel(tool, this);

			if (player.getFireTicks() <= 0) {
				return;
			}

			if (player.getFireTicks() > 0 && cancelBurning) {
				player.setFireTicks(0);
			}

			double oldDamage = event.getEvent().getDamage();
			double newDamage = oldDamage * (1 - this.bonusMultiplier * level);

			event.getEvent().setDamage(newDamage);
			ChatWriter.logModifier(player, event, this, tool, String.format("Damage(%.2f -> %.2f [x%.4f])", oldDamage, newDamage, newDamage/oldDamage));
		} else {
            /*
            The melting effect, if the Player is the Damager
             */
			if (event.getEvent().getEntity() instanceof LivingEntity) {
				LivingEntity entity = (LivingEntity) event.getEvent().getEntity();

				if (entity.isDead()) {
					return;
				}

				int level = modManager.getModLevel(tool, this);

				if (entity.getFireTicks() == 0) {
					return;
				}

				double oldDamage = event.getEvent().getDamage();
				double newDamage = oldDamage * (1 + this.bonusMultiplier * level);

				event.getEvent().setDamage(newDamage);
				ChatWriter.logModifier(player, event, this, tool, String.format("Damage(%.2f -> %.2f [x%.4f])", oldDamage, newDamage, newDamage/oldDamage));
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void effect(MTEntityDamageEvent event) {
		Player player = event.getPlayer();
		ItemStack tool = event.getTool();

		if (!player.hasPermission("minetinker.modifiers.melting.use")) {
			return;
		}

		if (!modManager.hasMod(tool, this)) {
			return;
		}

		int ticks = player.getFireTicks();
		if (ticks > 0 && cancelBurning) {
			player.setFireTicks(0);
			ChatWriter.logModifier(player, event, this, tool, "FireTicks(" + ticks + " -> 0)");
		}
	}
}

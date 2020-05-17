package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.MTEntityDamageByEntityEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import de.flo56958.MineTinker.Utilities.PlayerInfo;
import de.flo56958.MineTinker.Utilities.nms.NBTUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Difficulty;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

public class Magical extends Modifier implements Listener {

	private static Magical instance;
	private double multiplierArrowSpeed;
	private double multiplierDamagePerLevel;
	private int experienceCost;
	private boolean hasKnockback;

	private Magical() {
		super(Main.getPlugin());
		customModelData = 10_021;
	}

	public static Magical instance() {
		synchronized (Magical.class) {
			if (instance == null) {
				instance = new Magical();
			}
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Magical";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Arrays.asList(ToolType.BOW, ToolType.CROSSBOW);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Name", "Magical");
		config.addDefault("ModifierItemName", "Magic Arrow");
		config.addDefault("Description", "Shoot a magic orb that is not affected by gravity but flies %amount% slower! If the orb hits a target it does %min% to %max% more damage (depending on level)!");
		config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Magical-Modifier");
		config.addDefault("Color", "%DARK_PURPLE%");
		config.addDefault("MaxLevel", 5);
		config.addDefault("SlotCost", 1);

		config.addDefault("MultiplierArrowSpeed", 0.3);
		config.addDefault("MultiplierArrowDamagePerLevel", 1.25);
		config.addDefault("ExperienceCost", 3);
		config.addDefault("HasKnockback", true);

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);

		config.addDefault("Recipe.Enabled", true); //TODO: Change
		config.addDefault("Recipe.Top", "BPB");
		config.addDefault("Recipe.Middle", "PAP");
		config.addDefault("Recipe.Bottom", "BPB");
		config.addDefault("OverrideLanguagesystem", false);

		Map<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("B", Material.DRAGON_BREATH.name());
		recipeMaterials.put("A", Material.ARROW.name());
		recipeMaterials.put("P", Material.BLAZE_POWDER.name());

		config.addDefault("Recipe.Materials", recipeMaterials);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init(Material.ARROW, true);

		this.multiplierArrowSpeed = config.getDouble("MultiplierArrowSpeed", 0.3);
		this.multiplierDamagePerLevel = config.getDouble("MultiplierArrowDamagePerLevel", 1.25);
		this.experienceCost = config.getInt("ExperienceCost", 10);
		this.hasKnockback = config.getBoolean("HasKnockback", true);

		this.description = this.description.replace("%amount", String.valueOf(Math.round((1.0 - this.multiplierArrowSpeed) * 100)))
				.replace("%min", String.valueOf(Math.round((this.multiplierDamagePerLevel - 1.0) * 100)))
				.replace("%max", String.valueOf(Math.round((Math.pow(this.multiplierDamagePerLevel, this.getMaxLvl()) - 1.0) * 100)))
				.replace("%xp", String.valueOf(this.experienceCost));
	}

	@EventHandler
	public void onShoot(ProjectileLaunchEvent event) {
		if (!this.isAllowed()) return;

		Projectile arrow = event.getEntity();
		if (!(arrow instanceof Arrow)) return;

		if (!(arrow.getShooter() instanceof Player)) return;

		Player player = (Player) arrow.getShooter();
		if (!player.hasPermission("minetinker.modifiers.magical.use")) return;

		ItemStack tool = player.getInventory().getItemInMainHand();

		if (!modManager.isToolViable(tool)) return;

		int modLevel = modManager.getModLevel(tool, this);
		if (modLevel <= 0) return;

		if (PlayerInfo.getPlayerExp(player) < this.experienceCost) {
			event.setCancelled(true);
			return;
		}

		player.giveExp(- this.experienceCost);

		arrow.setBounce(true);
		((Arrow) arrow).setColor(Color.PURPLE);
		((Arrow) arrow).setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
		arrow.setGravity(false);
		//arrow.setGlowing(true);

		Entity entity = player.getLocation().getWorld().spawnEntity(arrow.getLocation().add(arrow.getVelocity().normalize().multiply(-0.5)), EntityType.ENDERMITE);
		if (entity instanceof LivingEntity) {
			((LivingEntity) entity).setRemoveWhenFarAway(true);
			//((LivingEntity) entity).setAI(false); can not move
			entity.setGravity(false);
			entity.setVelocity(arrow.getVelocity().multiply(this.multiplierArrowSpeed)); //does not work
			entity.setInvulnerable(true);
			entity.setSilent(true);
			((LivingEntity) entity).setCollidable(false);

			for (int i = 5; i < 10 * 20; i = i + 5) {
				Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> {
					entity.teleport(arrow.getLocation().add(arrow.getVelocity().normalize().multiply(-0.4)));
					entity.setVelocity(arrow.getVelocity()); //does not work
				}, i);
			}
		}
		arrow.setCustomName(this.getKey() + ":" + modLevel + ":" + entity.getUniqueId());

		arrow.addAttachment(Main.getPlugin(), this.getKey() + ":" + modLevel, true);

		arrow.setVelocity(arrow.getVelocity().multiply(this.multiplierArrowSpeed));

		((Arrow) arrow).setDamage(((Arrow) arrow).getDamage() * Math.pow(this.multiplierDamagePerLevel, modLevel));

		ChatWriter.logModifier(player, event, this, tool, "Cost(" + this.experienceCost + ")");

		Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> {
			entity.remove();
			arrow.remove();
		}, 10 * 20L);

		if (arrow.getWorld().getDifficulty() != Difficulty.PEACEFUL) {
			Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> NBTUtils.getHandler().removeArrowFromClient((Arrow) arrow), 2);
		}
	}

	@EventHandler
	public void onHit(ProjectileHitEvent event) {
		if (!this.isAllowed()) return;

		Projectile arrow = event.getEntity();
		if (!(arrow instanceof Arrow)) return;

		if (!(arrow.getShooter() instanceof Player)) return;

		Player player = (Player) arrow.getShooter();
		if(!player.hasPermission("minetinker.modifiers.magical.use")) return;

		String customName = arrow.getCustomName();
		if (customName == null) return;

		String[] name = customName.split(":");
		if (name.length != 3) return;
		if (!name[0].equals(this.getKey())) return;

		try {
			//int modLevel = Integer.parseInt(name[1]);

			Entity entity = Bukkit.getServer().getEntity(UUID.fromString(name[2]));
			if (entity != null) {
				entity.remove();
			}

			arrow.remove();
		} catch (NumberFormatException ignored) {}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityHit(MTEntityDamageByEntityEvent event) {
		if (!this.isAllowed()) return;

		if (!(event.getEvent().getDamager() instanceof Arrow)) return;

		Arrow arrow = (Arrow) event.getEvent().getDamager();
		String customName = arrow.getCustomName();
		if (customName == null) return;

		String[] name = customName.split(":");
		if (name.length != 3) return;
		if (!name[0].equals(this.getKey())) return;

		try {
			int modLevel = Integer.parseInt(name[1]);

			double oldDamage = event.getEvent().getDamage();
			double newDamage = oldDamage * Math.pow(this.multiplierDamagePerLevel, modLevel);

			event.getEvent().setDamage(newDamage);
			ChatWriter.logModifier(event.getPlayer(), event, this, event.getTool(), String.format("Damage(%.2f -> %.2f [%.4f])", oldDamage, newDamage, newDamage/oldDamage));

			if (this.hasKnockback) {
				event.getEntity().setVelocity(arrow.getVelocity().normalize().multiply(modLevel));
			}
		} catch (NumberFormatException ignored) {}
	}
}

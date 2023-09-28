package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.ConfigurationManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class MultiShot extends Modifier implements Listener {

	private static MultiShot instance;
	private double spread;
	private boolean needsArrows;

	private MultiShot() {
		super(MineTinker.getPlugin());
		customModelData = 10_023;
	}

	public static MultiShot instance() {
		synchronized (MultiShot.class) {
			if (instance == null) {
				instance = new MultiShot();
			}
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Multishot";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Arrays.asList(ToolType.CROSSBOW, ToolType.BOW);
	}

	@Override
	public @NotNull List<Enchantment> getAppliedEnchantments() {
		return Collections.singletonList(Enchantment.MULTISHOT);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Color", "%YELLOW%");
		config.addDefault("MaxLevel", 5);
		config.addDefault("SlotCost", 1);
		config.addDefault("ArrowSpread", 5.0);
		config.addDefault("NeedsArrows", true);
		config.addDefault("UseEnchantOnCrossbow", false);

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);
		config.addDefault("MinimumToolLevelRequirement", 1);

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", "QQQ");
		config.addDefault("Recipe.Middle", "AAA");
		config.addDefault("Recipe.Bottom", "QQQ");

		Map<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("Q", Material.QUARTZ_BLOCK.name());
		recipeMaterials.put("A", Material.ARROW.name());

		config.addDefault("Recipe.Materials", recipeMaterials);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init(Material.ARROW);

		this.spread = config.getDouble("ArrowSpread");
		this.needsArrows = config.getBoolean("NeedsArrows");
	}

	@Override
	public boolean applyMod(Player player, ItemStack tool, boolean isCommand) {
		ItemMeta meta = tool.getItemMeta();

		if (meta != null) {
			if (ToolType.CROSSBOW.contains(tool.getType())) {
				if (getConfig().getBoolean("UseEnchantOnCrossbow"))
					meta.addEnchant(Enchantment.MULTISHOT, modManager.getModLevel(tool, this), true);
				else
					meta.removeEnchant(Enchantment.MULTISHOT);
			}

			tool.setItemMeta(meta);
		}

		return true;
	}

	@EventHandler(ignoreCancelled = true)
	public void onShoot(ProjectileLaunchEvent event) {
		if (!this.isAllowed()) {
			return;
		}

		Projectile arrow = event.getEntity();

		if (arrow.hasMetadata(this.getKey())) return;

		if (!(arrow instanceof Arrow)) {
			return;
		}

		if (!(arrow.getShooter() instanceof Player player)) {
			return;
		}

		if (!player.hasPermission("minetinker.modifiers.multishot.use")) {
			return;
		}

		List<MetadataValue> tools = arrow.getMetadata(MineTinker.getPlugin().getName() + "item");
		FixedMetadataValue obj = (FixedMetadataValue) tools.get(0);
		if (obj == null || !(obj.value() instanceof ItemStack tool)) return;

		if (ToolType.CROSSBOW.contains(tool.getType()) && getConfig().getBoolean("UseEnchantOnCrossbow")) {
			return;
		}

		if (!modManager.isToolViable(tool)) {
			return;
		}

		int modLevel = modManager.getModLevel(tool, this);

		if (modLevel <= 0) {
			return;
		}

		Vector vel = arrow.getVelocity().clone();
		Location loc = arrow.getLocation().clone();

		final boolean hasInfinity = modManager.hasMod(tool, Infinity.instance());

		final boolean hasFiery = modManager.hasMod(tool, Fiery.instance()) && player.hasPermission("minetinker.modifiers.fiery.use");
		ChatWriter.logModifier(player, event, this, tool,
				Fiery.instance().getKey() + "(" + hasFiery + ")",
				Infinity.instance().getKey() + "(" + hasInfinity + ")");

		for (int i = 1; i <= modLevel; i++) {
			if (!player.getGameMode().equals(GameMode.CREATIVE)) {
				boolean hasArrow = true;
				if (!hasInfinity && needsArrows) {
					hasArrow = false;

					ItemStack offhand = player.getInventory().getItemInOffHand();

					if (!player.getInventory().contains(Material.ARROW) && offhand != null && offhand.getType() != Material.ARROW) {
						break;
					}

					if (offhand.getType() == Material.ARROW && offhand.getAmount() >= 2) { // 2 as the main arrow is detracted later
						offhand.setAmount(offhand.getAmount() - 1);
						hasArrow = true;
					} else {
						for (ItemStack item : player.getInventory().getContents()) {
							if (item == null) {
								continue;
							}

							if (item.getType() == Material.ARROW && item.getAmount() >= 2) {
								item.setAmount(item.getAmount() - 1);
								hasArrow = true;
								break;
							}
						}
					}
				}

				if (!hasArrow) {
					break;
				}
			}

			Bukkit.getScheduler().runTaskLater(MineTinker.getPlugin(), () -> {
				final Arrow arr = loc.getWorld().spawnArrow(loc, vel, (float) vel.length(), (float) spread);
				if(hasFiery) arr.setFireTicks(2000);
				arr.setShooter(player);

				if (hasInfinity || player.getGameMode().equals(GameMode.CREATIVE)) {
					arr.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
				}

				arr.setCritical(((Arrow) arrow).isCritical());
				arr.setDamage(((Arrow) arrow).getDamage());
				arr.setMetadata(this.getKey(), new FixedMetadataValue(this.getSource(), null));

				Bukkit.getPluginManager().callEvent(new ProjectileLaunchEvent(arr));
			}, 0);
		}
	}
}

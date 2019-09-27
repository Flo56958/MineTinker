package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import de.flo56958.MineTinker.Utilities.nms.NBTUtils;
import org.bukkit.Bukkit;
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
import org.bukkit.util.Vector;

import java.io.File;
import java.util.*;

public class MultiShot extends Modifier implements Listener {

	private static MultiShot instance;
	private double spread;

	private MultiShot() {
		super(Main.getPlugin());
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
	public List<Enchantment> getAppliedEnchantments() {
		if (NBTUtils.isOneFourteenCompatible()) {
			return Collections.singletonList(Enchantment.MULTISHOT);
		} else {
			return new ArrayList<>();
		}
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Name", "Multishot");
		config.addDefault("ModifierItemName", "Multi-Arrow");
		config.addDefault("Description", "Shoot more Arrows per shot!");
		config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Multishot-Modifier");
		config.addDefault("Color", "%YELLOW%");
		config.addDefault("MaxLevel", 5);
		config.addDefault("ArrowSpread", 5.0);
		config.addDefault("UseEnchantOnCrossbow", false);

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", "QQQ");
		config.addDefault("Recipe.Middle", "AAA");
		config.addDefault("Recipe.Bottom", "QQQ");
		config.addDefault("OverrideLanguagesystem", false);

		Map<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("Q", Material.QUARTZ_BLOCK.name());
		recipeMaterials.put("A", Material.ARROW.name());

		config.addDefault("Recipe.Materials", recipeMaterials);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init(Material.ARROW, true);

		this.spread = config.getDouble("ArrowSpread");
	}

	@Override
	public boolean applyMod(Player p, ItemStack tool, boolean isCommand) {
		ItemMeta meta = tool.getItemMeta();

		if (meta != null) {
			if (ToolType.CROSSBOW.contains(tool.getType())) {
				if (getConfig().getBoolean("UseEnchantOnCrossbow")) meta.addEnchant(Enchantment.MULTISHOT, modManager.getModLevel(tool, this), true);
			}

			tool.setItemMeta(meta);
		}

		return true;
	}

	@EventHandler
	public void onShoot(ProjectileLaunchEvent e) {
		if (!this.isAllowed()) return;

		Projectile arrow = e.getEntity();
		if (!(arrow instanceof Arrow)) return;

		if (!(arrow.getShooter() instanceof Player)) return;

		Player p = (Player) arrow.getShooter();
		if (!p.hasPermission("minetinker.modifiers.multishot.use")) return;

		ItemStack tool = p.getInventory().getItemInMainHand();

		if (ToolType.CROSSBOW.contains(tool.getType()) && getConfig().getBoolean("UseEnchantOnCrossbow")) return;

		if (!modManager.isToolViable(tool)) return;

		int modLevel = modManager.getModLevel(tool, this);
		if (modLevel <= 0) return;

		Vector vel = arrow.getVelocity().clone();
		Location loc = arrow.getLocation().clone();

		boolean hasInfinity = modManager.hasMod(tool, Infinity.instance());

		for (int i = 1; i <= modLevel; i++) {
			if (!hasInfinity) {
				if (!p.getInventory().contains(Material.ARROW)) break;
				for (ItemStack item : p.getInventory().getContents()) {
					if (item == null) continue;
					if (item.getType() == Material.ARROW) {
						item.setAmount(item.getAmount() - 1);
						break;
					}
				}
			}
			Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> {
				Arrow arr = loc.getWorld().spawnArrow(loc, vel, (float) vel.length(), (float) spread);
				arr.setShooter(p);
				if (hasInfinity) arr.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
				arr.setCritical(((Arrow) arrow).isCritical());
				arr.setDamage(((Arrow) arrow).getDamage());
			}, i);
		}
	}
}

package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.Main;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.ConfigurationManager;
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

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class Infinity extends Modifier implements Listener {

	private static Infinity instance;

	private Infinity() {
		super(Main.getPlugin());
		customModelData = 10_014;
	}

	public static Infinity instance() {
		synchronized (Infinity.class) {
			if (instance == null) {
				instance = new Infinity();
			}
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Infinity";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Arrays.asList(ToolType.BOW, ToolType.TRIDENT, ToolType.CROSSBOW);
	}

	@Override
	public List<Enchantment> getAppliedEnchantments() {
		return Arrays.asList(Enchantment.ARROW_INFINITE, Enchantment.LOYALTY);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Name", "Infinity");
		config.addDefault("ModifierItemName", "Enchanted Arrow");
		config.addDefault("Description", "You only need one Arrow to shoot a bow and the Trident comes back!");
		config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Infinity-Modifier");
		config.addDefault("MaxLevel", 3); //higher values than 1 have no effect on Infinity
		config.addDefault("SlotCost", 2);
		config.addDefault("Color", "%WHITE%");

		config.addDefault("EnchantCost", 15);
		config.addDefault("Enchantable", true);

		config.addDefault("Recipe.Enabled", false);
		config.addDefault("OverrideLanguagesystem", false);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init(Material.ARROW);
	}

	@Override
	public boolean applyMod(Player player, ItemStack tool, boolean isCommand) {
		ItemMeta meta = tool.getItemMeta();

		if (meta != null) {
			if (ToolType.BOW.contains(tool.getType()) || ToolType.CROSSBOW.contains(tool.getType())) {
				meta.addEnchant(Enchantment.ARROW_INFINITE, modManager.getModLevel(tool, this), true);
			} else if (ToolType.TRIDENT.contains(tool.getType())) {
				meta.addEnchant(Enchantment.LOYALTY, modManager.getModLevel(tool, this), true);
			}

			tool.setItemMeta(meta);
		}
		return true;
	}

	@EventHandler
	public void onShoot(ProjectileLaunchEvent event) {
		if (!this.isAllowed()) return;

		Projectile arrow = event.getEntity();
		if (!(arrow instanceof Arrow)) return;

		if (!(arrow.getShooter() instanceof Player)) return;

		Player player = (Player) arrow.getShooter();
		if (!player.hasPermission("minetinker.modifiers.infinity.use")) return;

		ItemStack tool = player.getInventory().getItemInMainHand();

		if (!ToolType.CROSSBOW.contains(tool.getType())) return;

		if (!modManager.isToolViable(tool)) return;

		if (!modManager.hasMod(tool, this)) return;

		if(!((Arrow) arrow).hasCustomEffects()) {
			if (player.getInventory().addItem(new ItemStack(Material.ARROW, 1)).size() != 0) { //adds items to (full) inventory
				player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.ARROW, 1)); //drops item when inventory is full
			} // no else as it gets added in if

			((Arrow) arrow).setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
			ChatWriter.logModifier(player, event, this, tool);
		}
	}
}


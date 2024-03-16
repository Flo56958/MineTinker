package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.api.events.MTProjectileLaunchEvent;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.ConfigurationManager;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class Infinity extends Modifier implements Listener {

	private static Infinity instance;

	private Infinity() {
		super(MineTinker.getPlugin());
		customModelData = 10_014;
	}

	public static Infinity instance() {
		synchronized (Infinity.class) {
			if (instance == null)
				instance = new Infinity();
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
	public @NotNull List<Enchantment> getAppliedEnchantments() {
		return Arrays.asList(Enchantment.ARROW_INFINITE, Enchantment.LOYALTY);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("MaxLevel", 3); //higher values than 1 have no effect on Infinity
		config.addDefault("SlotCost", 2);
		config.addDefault("Color", "%WHITE%");
		config.addDefault("ModifierItemMaterial", Material.ARROW.name());

		config.addDefault("EnchantCost", 15);
		config.addDefault("Enchantable", true);
		config.addDefault("MinimumToolLevelRequirement", 1);

		config.addDefault("Recipe.Enabled", false);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init();
	}

	@Override
	public boolean applyMod(Player player, ItemStack tool, boolean isCommand) {
		ItemMeta meta = tool.getItemMeta();

		if (meta != null) {
			if (ToolType.BOW.contains(tool.getType()) || ToolType.CROSSBOW.contains(tool.getType()))
				meta.addEnchant(Enchantment.ARROW_INFINITE, modManager.getModLevel(tool, this), true);
			else if (ToolType.TRIDENT.contains(tool.getType()))
				meta.addEnchant(Enchantment.LOYALTY, modManager.getModLevel(tool, this), true);

			tool.setItemMeta(meta);
		}
		return true;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onShoot(final MTProjectileLaunchEvent event) {
		Projectile projectile = event.getEvent().getEntity();
		if (!(projectile instanceof Arrow arrow)) return;
		if (arrow.hasCustomEffects()) return;
		if (arrow.getPickupStatus() == AbstractArrow.PickupStatus.CREATIVE_ONLY) return;

		final Player player = event.getPlayer();
		if (!player.hasPermission(getUsePermission())) return;
		if (player.getGameMode() == GameMode.CREATIVE) return;

		final ItemStack tool = event.getTool();
		if (!modManager.hasMod(tool, this)) return;

		if (!player.getInventory().addItem(new ItemStack(Material.ARROW, 1)).isEmpty()) { //adds items to (full) inventory
			player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.ARROW, 1)); //drops item when inventory is full
		} // no else as it gets added in if

		arrow.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
		ChatWriter.logModifier(player, event, this, tool);
	}
}


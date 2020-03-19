package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.MTBlockBreakEvent;
import de.flo56958.MineTinker.Events.MTEntityDamageByEntityEvent;
import de.flo56958.MineTinker.Events.MTEntityDamageEvent;
import de.flo56958.MineTinker.Events.MTPlayerInteractEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SelfRepair extends Modifier implements Listener {

	private static SelfRepair instance;
	private int percentagePerLevel;
	private int healthRepair;
	private boolean useMending;

	private SelfRepair() {
		super(Main.getPlugin());
		customModelData = 10_031;
	}

	public static SelfRepair instance() {
		synchronized (SelfRepair.class) {
			if (instance == null) {
				instance = new SelfRepair();
			}
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Self-Repair";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Collections.singletonList(ToolType.ALL);
	}

	@Override
	public List<Enchantment> getAppliedEnchantments() {
		// This may be an issue if (like by default) Self-Repair doesn't apply mending
		return Collections.singletonList(Enchantment.MENDING);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Name", "Self-Repair");
		config.addDefault("ModifierItemName", "Enchanted mossy Cobblestone");
		config.addDefault("Description", "Chance to repair the tool / armor while using it!");
		config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Self-Repair-Modifier");
		config.addDefault("Color", "%GREEN%");
		config.addDefault("MaxLevel", 25);
		config.addDefault("PercentagePerLevel", 10); //100% at Level 10 (not necessary for unbreakable tool in most cases)
		config.addDefault("HealthRepair", 2); //How much durability should be repaired per trigger
		config.addDefault("UseMending", false); //Disables the plugins own system and instead uses the vanilla Mending enchantment

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", true);

		config.addDefault("Recipe.Enabled", false);
		config.addDefault("OverrideLanguagesystem", false);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init(Material.MOSSY_COBBLESTONE, true);

		this.percentagePerLevel = config.getInt("PercentagePerLevel", 10);
		this.healthRepair = config.getInt("HealthRepair", 2);
		this.useMending = config.getBoolean("UseMending", false);

		this.description = this.description.replace("%amount", "" + this.healthRepair).replace("%chance", "" + this.percentagePerLevel);
	}

	@Override
	public boolean applyMod(Player player, ItemStack tool, boolean isCommand) {
		if (useMending) {
			ItemMeta meta = tool.getItemMeta();

			if (meta != null) {
				meta.addEnchant(Enchantment.MENDING, modManager.getModLevel(tool, this), true);

				tool.setItemMeta(meta);
			}
		}
		return true;
	}

	//------------------------------------------------------

	@EventHandler(ignoreCancelled = true)
	public void effect(MTBlockBreakEvent event) {
		effect(event.getPlayer(), event.getTool());
	}

	@EventHandler(ignoreCancelled = true)
	public void effect(MTEntityDamageByEntityEvent event) {


		if (ToolType.BOOTS.contains(event.getTool().getType())
				|| ToolType.LEGGINGS.contains(event.getTool().getType())
				|| ToolType.CHESTPLATE.contains(event.getTool().getType())
				|| ToolType.HELMET.contains(event.getTool().getType())) {

			return; //Makes sure that armor does not get the double effect as it also gets the effect in EntityDamageEvent
		}

		effect(event.getPlayer(), event.getTool());
	}

	@EventHandler(ignoreCancelled = true)
	public void effect(MTEntityDamageEvent event) {


		effect(event.getPlayer(), event.getTool());
	}

	@EventHandler(ignoreCancelled = true)
	public void effect(MTPlayerInteractEvent event) {


		effect(event.getPlayer(), event.getTool());
	}

	@EventHandler(ignoreCancelled = true)
	public void onShear(PlayerShearEntityEvent event) {
		ItemStack tool = event.getPlayer().getInventory().getItemInMainHand();

		if (!(modManager.isToolViable(tool) && ToolType.SHEARS.contains(tool.getType()))) {
			return;
		}

		effect(event.getPlayer(), tool);
	}

	public void effectElytra(Player player, ItemStack elytra) {
		if (!this.isAllowed()) {
			return;
		}

		effect(player, elytra);
	}

	/**
	 * The Effect that is used if Mending is disabled
	 *
	 * @param player    the Player
	 * @param tool the Tool
	 */
	private void effect(Player player, ItemStack tool) {
		if (useMending) {
			return;
		}

		if (!player.hasPermission("minetinker.modifiers.selfrepair.use")) {
			return;
		}

		if (!modManager.hasMod(tool, this)) {
			return;
		}

		int level = modManager.getModLevel(tool, this);
		Random rand = new Random();
		int n = rand.nextInt(100);

		if (n <= this.percentagePerLevel * level) {
			if (tool.getItemMeta() instanceof Damageable) {
				Damageable damageable = (Damageable) tool.getItemMeta();
				int dura = damageable.getDamage() - this.healthRepair;

				if (dura < 0) {
					dura = 0;
				}

				damageable.setDamage(dura);

				tool.setItemMeta((ItemMeta) damageable);

				ChatWriter.log(false, player.getDisplayName() + " triggered Self-Repair on " + ItemGenerator.getDisplayName(tool) + ChatColor.GRAY + " (" + tool.getType().toString() + ")!");
			}
		}
	}
}

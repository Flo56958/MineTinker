package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.MTEntityDamageByEntityEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
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

public class Fiery extends Modifier implements Listener {

	//TODO: Add Particle effect
	private static Fiery instance;

	private Fiery() {
		super(Main.getPlugin());
		customModelData = 10_010;
	}

	public static Fiery instance() {
		synchronized (Fiery.class) {
			if (instance == null) {
				instance = new Fiery();
			}
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Fiery";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.CROSSBOW, ToolType.SWORD);
	}

	@Override
	public List<Enchantment> getAppliedEnchantments() {
		return Arrays.asList(Enchantment.ARROW_FIRE, Enchantment.FIRE_ASPECT);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Name", "Fiery");
		config.addDefault("ModifierItemName", "Enchanted Blaze-Rod");
		config.addDefault("Description", "Inflames enemies!");
		config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Fiery-Modifier");
		config.addDefault("Color", "%YELLOW%");
		config.addDefault("MaxLevel", 2);
		config.addDefault("SlotCost", 1);

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", true);

		config.addDefault("Recipe.Enabled", false);
		config.addDefault("OverrideLanguagesystem", false);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init(Material.BLAZE_ROD);
	}

	@Override
	public boolean applyMod(Player player, ItemStack tool, boolean isCommand) {
		ItemMeta meta = tool.getItemMeta();

		if (meta != null) {
			if (ToolType.BOW.contains(tool.getType()) || ToolType.CROSSBOW.contains(tool.getType())) {
				meta.addEnchant(Enchantment.ARROW_FIRE, modManager.getModLevel(tool, this), true);
			} else if (ToolType.SWORD.contains(tool.getType()) || ToolType.AXE.contains(tool.getType())) {
				meta.addEnchant(Enchantment.FIRE_ASPECT, modManager.getModLevel(tool, this), true);
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
		if(!player.hasPermission("minetinker.modifiers.fiery.use")) return;

		ItemStack tool = player.getInventory().getItemInMainHand();

		if (!ToolType.CROSSBOW.contains(tool.getType())) return;

		if (!modManager.isToolViable(tool)) return;

		if (!modManager.hasMod(tool, this)) return;

		arrow.setFireTicks(2000);
	}

	@EventHandler(ignoreCancelled = true)
	public void onHit(MTEntityDamageByEntityEvent event) {
		if (!this.isAllowed()) return;
		ItemStack tool = event.getTool();
		if (!ToolType.CROSSBOW.contains(tool.getType()) && !ToolType.BOW.contains(tool.getType())) return; //The enchantment is working for other tools

		if(event.getEvent().getDamager().equals(event.getPlayer())) return; //Melee interaction

		if(!event.getPlayer().hasPermission("minetinker.modifiers.fiery.use")) return;

		int fireticks = event.getEntity().getFireTicks();
		int addedFT = 100 * (modManager.getModLevel(tool, this) - 1); //Flame adds 100 Ticks; Fire aspect multiplies that by the level

		if (addedFT == 0) return;

		event.getEntity().setFireTicks(fireticks + addedFT);
		ChatWriter.logModifier(event.getPlayer(), event, this, tool, String.format("FireTicks(%d -> %d)", fireticks, fireticks + addedFT));
	}
}

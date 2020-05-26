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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class Knockback extends Modifier implements Listener {

	private static Knockback instance;

	private Knockback() {
		super(Main.getPlugin());
		customModelData = 10_017;
	}

	public static Knockback instance() {
		synchronized (Knockback.class) {
			if (instance == null) {
				instance = new Knockback();
			}
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Knockback";
	}

	@Override
	public List<ToolType> getAllowedTools() {
			return Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.SWORD, ToolType.SHIELD, ToolType.TRIDENT);
	}

	@Override
	public List<Enchantment> getAppliedEnchantments() {
		return Arrays.asList(Enchantment.KNOCKBACK, Enchantment.ARROW_KNOCKBACK);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Name", "Knockback");
		config.addDefault("ModifierItemName", "Enchanted TNT");
		config.addDefault("Description", "Knock back Enemies further!");
		config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Knockback-Modifier");
		config.addDefault("Color", "%GRAY%");
		config.addDefault("MaxLevel", 2);
		config.addDefault("SlotCost", 1);

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", true);

		config.addDefault("Recipe.Enabled", false);
		config.addDefault("OverrideLanguagesystem", false);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init(Material.TNT);
	}

	@Override
	public boolean applyMod(Player player, ItemStack tool, boolean isCommand) {
		ItemMeta meta = tool.getItemMeta();

		if (meta != null) {
			if (ToolType.AXE.contains(tool.getType()) || ToolType.SWORD.contains(tool.getType()) || ToolType.TRIDENT.contains(tool.getType())) {
				meta.addEnchant(Enchantment.KNOCKBACK, modManager.getModLevel(tool, this), true);
			} else if (ToolType.BOW.contains(tool.getType()) || ToolType.CROSSBOW.contains(tool.getType())) {
				meta.addEnchant(Enchantment.ARROW_KNOCKBACK, modManager.getModLevel(tool, this), true);
			}
			//Shields do not get the enchant

			tool.setItemMeta(meta);
		}

		return true;
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlock(MTEntityDamageByEntityEvent event) {
		if (event.getPlayer().equals(event.getEvent().getDamager())) return; //Player is attacking
		if (!event.isBlocking()) return;

		if (!event.getPlayer().hasPermission("minetinker.modifiers.knockback.use")) return;

		ItemStack shield = event.getTool();
		if (!ToolType.SHIELD.contains(shield.getType())) return; //Not the shield

		int level = modManager.getModLevel(shield, this);
		if (level <= 0) return;

		//calculate vector
		Vector vector = event.getEvent().getDamager().getLocation().subtract(event.getPlayer().getLocation()).toVector();
		vector = vector.normalize().multiply(new Vector(1, 0, 1)).add(new Vector(0, 0.2, 0));
		vector = vector.multiply(new Vector(level * 0.2, 1, level * 0.2));

		event.getEntity().setVelocity(vector);
		ChatWriter.logModifier(event.getPlayer(), event, this, shield,
				String.format("Vector(%.2f/%.2f/%.2f)", vector.getX(), vector.getY(), vector.getZ()));
	}
}

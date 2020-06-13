package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.events.MTBlockBreakEvent;
import de.flo56958.minetinker.events.MTEntityDamageByEntityEvent;
import de.flo56958.minetinker.events.MTEntityDamageEvent;
import de.flo56958.minetinker.events.MTPlayerInteractEvent;
import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.ConfigurationManager;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

public class Experienced extends Modifier implements Listener {

	private static Experienced instance;
	private int percentagePerLevel;
	private int amount;

	private Experienced() {
		super(MineTinker.getPlugin());
		this.customModelData = 10_048;
	}

	public static Experienced instance() {
		synchronized (Experienced.class) {
			if (instance == null) {
				instance = new Experienced();
			}
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Experienced";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Collections.singletonList(ToolType.ALL);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Name", "Experienced");
		config.addDefault("ModifierItemName", "Essence of Experienced");
		config.addDefault("Description", "Tool has the chance to drop XP while using it!");
		config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Experienced-Modifier");
		config.addDefault("Color", "%GREEN%");
		config.addDefault("MaxLevel", 10);
		config.addDefault("SlotCost", 1);
		config.addDefault("PercentagePerLevel", 2); //= 20% at Level 10 -> every 5th hit / block will trigger Experienced
		config.addDefault("Amount", 1); //How much XP should be dropped when triggered

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);
		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", "   ");
		config.addDefault("Recipe.Middle", " E ");
		config.addDefault("Recipe.Bottom", "   ");

		Map<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("E", Material.EXPERIENCE_BOTTLE.name());

		config.addDefault("Recipe.Materials", recipeMaterials);
		config.addDefault("OverrideLanguagesystem", false);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init(Material.EXPERIENCE_BOTTLE);

		this.percentagePerLevel = config.getInt("PercentagePerLevel", 2);
		this.amount = config.getInt("Amount", 1);
		this.description = this.description.replace("%chance", String.valueOf(this.percentagePerLevel))
				.replace("%amount", String.valueOf(this.amount));
	}

	//----------------------------------------------------------

	@EventHandler(ignoreCancelled = true)
	public void effect(MTBlockBreakEvent event) {
		effect(event.getPlayer(), event.getTool(), event);
	}

	@EventHandler(ignoreCancelled = true)
	public void effect(MTEntityDamageByEntityEvent event) {
		if (ToolType.BOOTS.contains(event.getTool().getType())
				|| ToolType.LEGGINGS.contains(event.getTool().getType())
				|| ToolType.CHESTPLATE.contains(event.getTool().getType())
				|| ToolType.HELMET.contains(event.getTool().getType())) {

			return; //Makes sure that armor does not get the double effect as it also gets the effect in EntityDamageEvent
		}

		effect(event.getPlayer(), event.getTool(), event);
	}

	@EventHandler(ignoreCancelled = true)
	public void effect(MTEntityDamageEvent event) {
		effect(event.getPlayer(), event.getTool(), event);
	}

	@EventHandler(ignoreCancelled = true)
	public void effect(MTPlayerInteractEvent event) {
		effect(event.getPlayer(), event.getTool(), event);
	}

	/**
	 * The Effect of the modifier
	 *
	 * @param player    the Player
	 * @param tool the Tool
	 */
	private void effect(Player player, ItemStack tool, Event event) {
		if (!player.hasPermission("minetinker.modifiers.experienced.use")) {
			return;
		}

		if (!modManager.hasMod(tool, this)) {
			return;
		}

		int level = modManager.getModLevel(tool, this);

		Random rand = new Random();
		int n = rand.nextInt(100);
		int c = this.percentagePerLevel * level;

		if (n <= c) {
			player.giveExp(this.amount);
		}
		ChatWriter.logModifier(player, event, this, tool, String.format("Chance(%d/%d)", n, c));
	}
}

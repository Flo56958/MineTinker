package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.api.events.MTProjectileLaunchEvent;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.ConfigurationManager;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class Piercing extends Modifier implements Listener {

	private static Piercing instance;

	private boolean allowBow = true;

	private Piercing() {
		super(MineTinker.getPlugin());
		customModelData = 10_025;
	}

	public static Piercing instance() {
		synchronized (Piercing.class) {
			if (instance == null)
				instance = new Piercing();
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Piercing";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		if (allowBow) return Arrays.asList(ToolType.CROSSBOW, ToolType.BOW);
		return Collections.singletonList(ToolType.CROSSBOW);
	}

	@Override
	public @NotNull List<Enchantment> getAppliedEnchantments() {
		return Collections.singletonList(Enchantment.PIERCING);
	}
	//The mod does not apply the enchantment anymore

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Color", "%GRAY%");
		config.addDefault("MaxLevel", 4);
		config.addDefault("SlotCost", 1);
		config.addDefault("AllowBow", true);

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);
		config.addDefault("MinimumToolLevelRequirement", 1);

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", "FIF");
		config.addDefault("Recipe.Middle", "OAO");
		config.addDefault("Recipe.Bottom", "FIF");

		Map<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("F", Material.FLINT.name());
		recipeMaterials.put("I", Material.IRON_INGOT.name());
		recipeMaterials.put("O", Material.OAK_PLANKS.name());
		recipeMaterials.put("A", Material.ARROW.name());

		config.addDefault("Recipe.Materials", recipeMaterials);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init(Material.ARROW);

		this.allowBow = config.getBoolean("AllowBow", true);
	}

	@EventHandler
	public void onLaunch(final MTProjectileLaunchEvent event) {
		final Projectile projectile = event.getEvent().getEntity();
		if (!(projectile instanceof final Arrow arrow)) return;

		final Player player = event.getPlayer();
		if (!player.hasPermission(getUsePermission())) return;

		final ItemStack tool = event.getTool();
		if (!modManager.hasMod(tool, this)) return;

		final int level = modManager.getModLevel(tool, this);
		arrow.setPierceLevel(level);

		ChatWriter.logModifier(player, event, this, tool);
	}
}

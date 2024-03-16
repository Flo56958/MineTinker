package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.ConfigurationManager;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

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
		super(MineTinker.getPlugin());
		customModelData = 10_031;
	}

	public static SelfRepair instance() {
		synchronized (SelfRepair.class) {
			if (instance == null)
				instance = new SelfRepair();
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
	public @NotNull List<Enchantment> getAppliedEnchantments() {
		// This may be an issue if (like by default) Self-Repair doesn't apply mending
		return Collections.singletonList(Enchantment.MENDING);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Color", "%GREEN%");
		config.addDefault("MaxLevel", 10);
		config.addDefault("SlotCost", 1);
		config.addDefault("ModifierItemMaterial", Material.MOSSY_COBBLESTONE.name());
		config.addDefault("PercentagePerLevel", 10); //100% at Level 10 (not necessary for unbreakable tool in most cases)
		config.addDefault("HealthRepair", 3); //How much durability should be repaired per trigger
		config.addDefault("UseMending", false); //Disables the plugins own system and instead uses the vanilla Mending enchantment

		config.addDefault("EnchantCost", 25);
		config.addDefault("Enchantable", true);
		config.addDefault("MinimumToolLevelRequirement", 1);

		config.addDefault("Recipe.Enabled", false);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init();

		this.percentagePerLevel = config.getInt("PercentagePerLevel", 10);
		this.healthRepair = config.getInt("HealthRepair", 3);
		this.useMending = config.getBoolean("UseMending", false);

		this.description = this.description.replace("%amount", String.valueOf(this.healthRepair))
				.replace("%chance", String.valueOf(this.percentagePerLevel));
	}

	@Override
	public boolean applyMod(final Player player, final ItemStack tool, final boolean isCommand) {
		final ItemMeta meta = tool.getItemMeta();

		if (meta != null) {
			if (useMending)
				meta.addEnchant(Enchantment.MENDING, modManager.getModLevel(tool, this), true);
			else
				meta.removeEnchant(Enchantment.MENDING);
		}
		tool.setItemMeta(meta);
		return true;
	}

	//------------------------------------------------------

	@EventHandler(ignoreCancelled = true)
	public void effect(@NotNull final PlayerItemDamageEvent event) {
		if (useMending) return;

		final ItemStack tool = event.getItem();
		if (!modManager.isToolViable(tool) && !modManager.isArmorViable(tool)) return;
		if (!event.getPlayer().hasPermission(getUsePermission())) return;
		if (!modManager.hasMod(tool, this)) return;

		final int level = modManager.getModLevel(tool, this);
		final Random rand = new Random();
		final int n = rand.nextInt(100);
		final int c = this.percentagePerLevel * level;

		if (n > c) {
			ChatWriter.logModifier(event.getPlayer(), event, this, tool, String.format("Chance(%d/%d)", n, c));
			return;
		}

		if (tool.getItemMeta() instanceof final Damageable damageable) {
			int dura = damageable.getDamage();
			int newDura = dura - this.healthRepair;

			newDura = Math.max(0, newDura);
			damageable.setDamage(newDura);

			tool.setItemMeta(damageable);
			ChatWriter.logModifier(event.getPlayer(), event, this, tool, String.format("Chance(%d/%d)", n, c),
					String.format("Repair(%d -> %d)", dura, newDura));
		}
	}
}

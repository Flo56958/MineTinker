package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.MineTinker;
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
		config.addDefault("SlotCost", 1);
		config.addDefault("PercentagePerLevel", 10); //100% at Level 10 (not necessary for unbreakable tool in most cases)
		config.addDefault("HealthRepair", 3); //How much durability should be repaired per trigger
		config.addDefault("UseMending", false); //Disables the plugins own system and instead uses the vanilla Mending enchantment

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", true);

		config.addDefault("Recipe.Enabled", false);
		config.addDefault("OverrideLanguagesystem", false);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init(Material.MOSSY_COBBLESTONE);

		this.percentagePerLevel = config.getInt("PercentagePerLevel", 10);
		this.healthRepair = config.getInt("HealthRepair", 3);
		this.useMending = config.getBoolean("UseMending", false);

		this.description = this.description.replace("%amount", String.valueOf(this.healthRepair))
				.replace("%chance", String.valueOf(this.percentagePerLevel));
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
	public void effect(PlayerItemDamageEvent event) {
		if (useMending) {
			return;
		}

		ItemStack tool = event.getItem();
		if (!modManager.isToolViable(tool) && !modManager.isArmorViable(tool)) {
			return;
		}

		if (!event.getPlayer().hasPermission("minetinker.modifiers.selfrepair.use")) {
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
			if (tool.getItemMeta() instanceof Damageable) {
				Damageable damageable = (Damageable) tool.getItemMeta();
				int dura = damageable.getDamage();
				int newDura = dura - this.healthRepair;

				if (newDura < 0) {
					newDura = 0;
				}

				damageable.setDamage(newDura);

				tool.setItemMeta((ItemMeta) damageable);
				ChatWriter.logModifier(event.getPlayer(), event, this, tool, String.format("Chance(%d/%d)", n, c),
						String.format("Repair(%d -> %d)", dura, newDura));
				return;
			}
		}
		ChatWriter.logModifier(event.getPlayer(), event, this, tool, String.format("Chance(%d/%d)", n, c));
	}
}

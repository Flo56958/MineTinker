package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.events.MTEntityDeathEvent;
import de.flo56958.minetinker.Main;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.ConfigurationManager;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Beheading extends Modifier implements Listener {

	private static Beheading instance;
	private int percentagePerLevel;
	private int dropSpawneggChancePerLevel;

	private Beheading() {
		super(Main.getPlugin());
		customModelData = 10_005;
	}

	public static Beheading instance() {
		synchronized (Beheading.class) {
			if (instance == null) {
				instance = new Beheading();
			}
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Beheading";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.CROSSBOW, ToolType.SWORD, ToolType.TRIDENT);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Name", "Beheading");
		config.addDefault("ModifierItemName", "Enchanted Wither-Skull");
		config.addDefault("Description", "Chance to drop the head of the mob!");
		config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Beheading-Modifier");
		config.addDefault("Color", "%DARK_GRAY%");
		config.addDefault("MaxLevel", 10);
		config.addDefault("SlotCost", 2);
		config.addDefault("PercentagePerLevel", 10);  //= 100% at Level 10
		config.addDefault("DropSpawnEggChancePerLevel", 0);

		config.addDefault("EnchantCost", 25);
		config.addDefault("Enchantable", true);

		config.addDefault("Recipe.Enabled", false);
		config.addDefault("OverrideLanguagesystem", false);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init(Material.WITHER_SKELETON_SKULL);

		this.percentagePerLevel = config.getInt("PercentagePerLevel", 10);
		this.dropSpawneggChancePerLevel = config.getInt("DropSpawnEggChancePerLevel", 0);
		this.description = this.description.replace("%chance", String.valueOf(this.percentagePerLevel));
	}

	/**
	 * Effect for getting the mob heads
	 */
	@EventHandler(priority = EventPriority.LOW) //For Directing
	public void effect(MTEntityDeathEvent event) {
		Player player = event.getPlayer();
		ItemStack tool = event.getTool();
		LivingEntity mob = event.getEvent().getEntity();
		ItemStack loot = new ItemStack(Material.AIR, 1);

		if (player.hasPermission("minetinker.modifiers.beheading.use")) {
			if (modManager.hasMod(tool, this)) {
				Random rand = new Random();
				if(this.dropSpawneggChancePerLevel > 0) {
					int n = rand.nextInt(100);
					int i = this.dropSpawneggChancePerLevel * modManager.getModLevel(tool, this);
					if (n <= i) {
							Material mat = Material.getMaterial(mob.getType().toString().toUpperCase() + "_SPAWN_EGG");
							if (mat != null) {
								ItemStack egg = new ItemStack(mat, 1);
								event.getEvent().getDrops().add(egg);
							}
					}
					ChatWriter.logModifier(player, event, this, tool,
							String.format("DropEggChance(%d/%d)", n, i), "Entity(" + mob.getType().toString() + ")");
				}
				int n = rand.nextInt(100);
				int i = this.percentagePerLevel * modManager.getModLevel(tool, this);

				if (n <= i) {
					if (mob.getType() == EntityType.CREEPER) {
						loot = new ItemStack(Material.CREEPER_HEAD, 1);
					} else if (mob.getType() == EntityType.SKELETON) {
						loot = new ItemStack(Material.SKELETON_SKULL, 1);
					} else if (mob.getType() == EntityType.WITHER_SKELETON) {
						loot = new ItemStack(Material.WITHER_SKELETON_SKULL, 1);
					} else if (mob.getType() == EntityType.ZOMBIE) {
						loot = new ItemStack(Material.ZOMBIE_HEAD, 1);
					} else if (mob.getType() == EntityType.ZOMBIE_VILLAGER) {
						loot = new ItemStack(Material.ZOMBIE_HEAD, 1);
					} else if (mob.getType() == EntityType.PLAYER) {
						ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);

						if (head.getItemMeta() != null) {
							SkullMeta headMeta = (SkullMeta) head.getItemMeta();
							headMeta.setOwningPlayer((OfflinePlayer) mob);
							head.setItemMeta(headMeta);
						}

						loot = head;
					}

					if (loot.getType() != Material.AIR) {
						event.getEvent().getDrops().add(loot);
						ChatWriter.logModifier(player, event, this, tool,
								String.format("Chance(%d/%d)", n, i), "Entity(" + mob.getType().toString() + ")");
					}
				}
			}
		}
	}
}

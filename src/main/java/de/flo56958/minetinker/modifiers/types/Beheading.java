package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.api.events.MTEntityDeathEvent;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.ConfigurationManager;
import de.flo56958.minetinker.utils.LanguageManager;
import de.flo56958.minetinker.utils.data.DataHandler;
import org.bukkit.ChatColor;
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
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Beheading extends Modifier implements Listener {

	private static Beheading instance;
	private int percentagePerLevel;
	private int dropSpawneggChancePerLevel;

	private Beheading() {
		super(MineTinker.getPlugin());
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
		config.addDefault("Color", "%DARK_GRAY%");
		config.addDefault("MaxLevel", 10);
		config.addDefault("SlotCost", 2);
		config.addDefault("PercentagePerLevel", 10);  //= 100% at Level 10
		config.addDefault("DropSpawnEggChancePerLevel", 0);

		config.addDefault("EnchantCost", 25);
		config.addDefault("Enchantable", true);
		config.addDefault("MinimumToolLevelRequirement", 1);

		config.addDefault("Recipe.Enabled", false);

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

		if (player.hasPermission(getUsePermission())) {
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
							String.format("DropEggChance(%d/%d)", n, i), "Entity(" + mob.getType() + ")");
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
					} else if (MineTinker.is20compatible && mob.getType() == EntityType.PIGLIN) {
						loot = new ItemStack(Material.PIGLIN_HEAD, 1);
					}

					if (loot.getType() != Material.AIR) {
						event.getEvent().getDrops().add(loot);
						ChatWriter.logModifier(player, event, this, tool,
								String.format("Chance(%d/%d)", n, i), "Entity(" + mob.getType() + ")");
						//Track stats
						int stat = (DataHandler.hasTag(tool, getKey() + "_stat_used", PersistentDataType.INTEGER))
								? DataHandler.getTag(tool, getKey() + "_stat_used", PersistentDataType.INTEGER)
								: 0;
						DataHandler.setTag(tool, getKey() + "_stat_used", stat + 1, PersistentDataType.INTEGER);
					}
				}
			}
		}
	}

	@Override
	public List<String> getStatistics(ItemStack item) {
		//Track stats
		int stat = (DataHandler.hasTag(item, getKey() + "_stat_used", PersistentDataType.INTEGER))
				? DataHandler.getTag(item, getKey() + "_stat_used", PersistentDataType.INTEGER)
				: 0;
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.WHITE + LanguageManager.getString("Modifier.Beheading.Statistic_Used")
				.replaceAll("%amount", String.valueOf(stat)));
		return lore;
	}
}

package de.flo56958.minetinker.modifiers.types;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ConfigurationManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Echoing extends Modifier {

	private static Echoing instance;

	private ProtocolManager protocolManager;

	private int taskID = -1;

	private int radiusPerLevel;

	private int tickTime;

	private final Runnable runnable = () -> {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (!player.hasPermission("minetinker.modifiers.echoing.use")) continue;

			ItemStack helmet = player.getInventory().getHelmet();
			if (!modManager.isArmorViable(helmet)) continue;
			if (!modManager.hasMod(helmet, this)) continue;

			int radius = this.radiusPerLevel * modManager.getModLevel(helmet, this);
			Collection<Entity> entities = player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius);
			Collection<Entity> entities_wide = player.getWorld().getNearbyEntities(player.getLocation(), radius * 2, radius * 2, radius * 2);
			entities_wide.removeAll(entities);

			for (Entity ent : entities) {
				if (ent.isGlowing()) continue;
				if (ent.equals(player)) continue;

				PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
				packet.getIntegers().write(0, ent.getEntityId());
				WrappedDataWatcher watcher = new WrappedDataWatcher();
				WrappedDataWatcher.Serializer serializer = WrappedDataWatcher.Registry.get(Byte.class);
				watcher.setObject(0, serializer, (byte) (0x40));
				packet.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
				try {
					protocolManager.sendServerPacket(player, packet);
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}

			for (Entity ent : entities_wide) {
				if (ent.isGlowing()) continue;
				PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
				packet.getIntegers().write(0, ent.getEntityId());
				WrappedDataWatcher watcher = new WrappedDataWatcher();
				WrappedDataWatcher.Serializer serializer = WrappedDataWatcher.Registry.get(Byte.class);
				watcher.setObject(0, serializer, (byte) 0);
				packet.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
				try {
					protocolManager.sendServerPacket(player, packet);
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
	};

	private Echoing() {
		super(MineTinker.getPlugin());
		customModelData = 10_059;
		protocolManager = ProtocolLibrary.getProtocolManager();
	}

	public static Echoing instance() {
		synchronized (Echoing.class) {
			if (instance == null) {
				instance = new Echoing();
			}
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Echoing";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Collections.singletonList(ToolType.HELMET);
	}

	@Override
	public void reload() {
		if (taskID != -1) {
			Bukkit.getScheduler().cancelTask(taskID);
		}

		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Color", "%YELLOW%");
		config.addDefault("MaxLevel", 3);
		config.addDefault("SlotCost", 2);
		config.addDefault("TickTime", 20); //TickTime in Minecraft ticks

		config.addDefault("EnchantCost", 20);
		config.addDefault("Enchantable", true);
		config.addDefault("MinimumToolLevelRequirement", 1);
		config.addDefault("RadiusPerLevel", 10);

		config.addDefault("Recipe.Enabled", false);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init(Material.ENDER_EYE);

		this.tickTime = config.getInt("TickTime", 20);
		this.radiusPerLevel = config.getInt("RadiusPerLevel", 10);

		if (isAllowed()) {
			this.taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(MineTinker.getPlugin(), this.runnable, 5 * 20L, this.tickTime);
		} else {
			this.taskID = -1;
		}

		this.description = this.description.replaceFirst("%amount", String.valueOf(this.radiusPerLevel));
	}
}

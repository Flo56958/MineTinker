package de.flo56958.minetinker.modifiers.types;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataValue;
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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Echoing extends Modifier implements Listener {

	private static Echoing instance;

	private static final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

	private int taskID = -1;

	private int radiusPerLevel;

	private void sendPacket(@NotNull final Player player, @NotNull final Entity entity, final byte value) {
		PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
		packet.getIntegers().write(0, entity.getEntityId());

		if (!MineTinker.is19compatible) {
			WrappedDataWatcher watcher = new WrappedDataWatcher();
			watcher.setEntity(entity);
			watcher.setObject(0, WrappedDataWatcher.Registry.get(Byte.class), value);
			packet.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
		} else {
			packet.getDataValueCollectionModifier().write(0, List.of(
							new WrappedDataValue(0, WrappedDataWatcher.Registry.get(Byte.class), value)));
		}
		try {
			protocolManager.sendServerPacket(player, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void revert(@NotNull final Player player) {
		final Collection<Entity> entities = this.entities.get(player);
		if (entities == null) return;

		for (final Entity ent : entities) {
			if (!ent.isGlowing()) {
				sendPacket(player, ent, (byte) 0x00);
			}
		}
		this.entities.remove(player);
	}

	private HashMap<Player, Collection<Entity>> entities = new HashMap<>();

	private final Runnable runnable = () -> {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (!player.hasPermission(getUsePermission())) {
				revert(player);
				continue;
			}

			final ItemStack helmet = player.getInventory().getHelmet();
			if (!modManager.isArmorViable(helmet)) {
				revert(player);
				continue;
			}
			if (!modManager.hasMod(helmet, this)) {
				revert(player);
				continue;
			}

			int radius = this.radiusPerLevel * modManager.getModLevel(helmet, this);
			Collection<Entity> entities = player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius);
			Collection<Entity> entities_wide = player.getWorld().getNearbyEntities(player.getLocation(), radius * 2, radius * 2, radius * 2);
			entities_wide.removeAll(entities);

			this.entities.put(player, entities);

			for (final Entity ent : entities) {
				if (ent.isGlowing()) continue;
				if (ent.equals(player)) continue;

				sendPacket(player, ent, (byte) 0x40); // Glowing is 0x40
			}

			for (Entity ent : entities_wide) {
				if (ent.isGlowing()) continue;
				sendPacket(player, ent, (byte) 0x00);
			}
		}
	};

	private Echoing() {
		super(MineTinker.getPlugin());
		customModelData = 10_059;
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

		this.entities.clear();

		int tickTime = config.getInt("TickTime", 20);
		this.radiusPerLevel = config.getInt("RadiusPerLevel", 10);

		if (isAllowed()) {
			this.taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(MineTinker.getPlugin(), this.runnable, 5 * 20L, tickTime);
		} else {
			this.taskID = -1;
		}

		this.description = this.description.replaceFirst("%amount", String.valueOf(this.radiusPerLevel));
	}

	@EventHandler(ignoreCancelled = true)
	private void onPlayerQuit(@NotNull final PlayerQuitEvent event) {
		revert(event.getPlayer());
	}
}

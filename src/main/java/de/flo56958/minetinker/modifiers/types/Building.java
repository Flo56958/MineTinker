package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.api.events.MTPlayerInteractEvent;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.modifiers.PlayerConfigurableModifier;
import de.flo56958.minetinker.utils.ConfigurationManager;
import de.flo56958.minetinker.utils.LanguageManager;
import de.flo56958.minetinker.utils.data.DataHandler;
import de.flo56958.minetinker.utils.playerconfig.PlayerConfigurationManager;
import de.flo56958.minetinker.utils.playerconfig.PlayerConfigurationOption;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Slab;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Building extends PlayerConfigurableModifier implements Listener {

	private static Building instance;
	private boolean useDurability;
	private boolean giveExpOnUse;
	private int expAmount;

	private Building() {
		super(MineTinker.getPlugin());
		customModelData = 10_069;
	}

	public static Building instance() {
		synchronized (Building.class) {
			if (instance == null)
				instance = new Building();
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Building";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Arrays.asList(ToolType.AXE, ToolType.PICKAXE, ToolType.SHOVEL, ToolType.HOE, ToolType.SHEARS);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("MaxLevel", 1);
		config.addDefault("SlotCost", 1);
		config.addDefault("Color", "%GREEN%");
		config.addDefault("ModifierItemMaterial", Material.GRASS_BLOCK.name());
		config.addDefault("UseDurability", true);
		config.addDefault("GiveExpOnUse", true);
		config.addDefault("ExpAmount", 1);

		config.addDefault("EnchantCost", 15);
		config.addDefault("Enchantable", true);
		config.addDefault("MinimumToolLevelRequirement", 1);

		config.addDefault("Recipe.Enabled", false);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init();

		this.useDurability = config.getBoolean("UseDurability", true);
		this.giveExpOnUse = config.getBoolean("GiveExpOnUse", true);
		this.expAmount = config.getInt("ExpAmount", 1);
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlace(@NotNull final MTPlayerInteractEvent event) {
		if (event.getEvent().getAction() != Action.RIGHT_CLICK_BLOCK) return;

		final Player player = event.getPlayer();
		if (!player.hasPermission(getUsePermission())) return;

		final ItemStack tool = event.getTool();
		if (!modManager.hasMod(tool, this)) return;

		if (!PlayerConfigurationManager.getInstance().getBoolean(player, ENABLED)) return;

		final Block block = event.getEvent().getClickedBlock();
		if (block == null) return;
		if (block.isPassable() || block.isLiquid()) return; // do not do passable blocks

		final Block toFill = block.getRelative(event.getEvent().getBlockFace());
		if (!toFill.isPassable() && !toFill.isLiquid() && !toFill.isEmpty()) return;

		final boolean isDoubleSlab = block.getBlockData() instanceof Slab slab && slab.getType() == Slab.Type.DOUBLE;
		ItemStack toPlace = null;
		if (player.getGameMode() == GameMode.CREATIVE) {
			toPlace = new ItemStack(block.getType(), 2); // in case of double slabs
		} else {
			for (final ItemStack stack : event.getPlayer().getInventory().getContents()) {
				if (stack == null) continue;
				if (block.getType() != stack.getType()) continue;
				if (stack.hasItemMeta()) continue; // use only normal blocks
				if (isDoubleSlab && stack.getAmount() < 2) continue;

				toPlace = stack;
				break;
			}
			if (toPlace == null) return;

			// check Tool durability
			if (this.useDurability && !DataHandler.triggerItemDamage(player, tool, 1)) return;
		}

		if (!DataHandler.playerPlaceBlock(player, toPlace, toFill, block, block.getState(), block.getBlockData()))
			return; // TODO: restore durability

		if (this.giveExpOnUse)
			modManager.addExp(player, tool, this.expAmount, true);

		if (PlayerConfigurationManager.getInstance().getBoolean(player, SOLVE_PLAYER_OVERLAP)) {
			// Teleport player one block up if he is inside the block
			final BoundingBox blockBB = toFill.getBoundingBox();
			final BoundingBox playerBB = player.getBoundingBox();
			if (blockBB.overlaps(playerBB)) {
				final BoundingBox intersection = blockBB.intersection(playerBB);

				// Intersection size * block diff = push vector
				final Vector diff = toFill.getLocation().subtract(block.getLocation()).toVector();
				final Vector intersectionSize = intersection.getMax().subtract(intersection.getMin());
				final Vector push = intersectionSize.multiply(diff);
				player.teleport(player.getLocation().add(push));
			}
		}

		event.getEvent().setUseInteractedBlock(Event.Result.DENY); // prevent vanilla behavior by cancelling the event
		toPlace.setAmount(toPlace.getAmount() - (isDoubleSlab ? 2 : 1));
	}

	private final PlayerConfigurationOption ENABLED =
			new PlayerConfigurationOption(this, "enabled", PlayerConfigurationOption.Type.BOOLEAN,
					LanguageManager.getString("Modifier.Homing.PCO_enabled"), true);

	private final PlayerConfigurationOption SOLVE_PLAYER_OVERLAP =
			new PlayerConfigurationOption(this, "solve-player-overlap", PlayerConfigurationOption.Type.BOOLEAN,
					LanguageManager.getString("Modifier.Building.PCO_solve_player_overlap"), true);

	@Override
	public List<PlayerConfigurationOption> getPCIOptions() {
		final ArrayList<PlayerConfigurationOption> playerConfigurationOptions = new ArrayList<>(List.of(ENABLED, SOLVE_PLAYER_OVERLAP));
		playerConfigurationOptions.sort(Comparator.comparing(PlayerConfigurationOption::displayName));
		return playerConfigurationOptions;
	}
}

package de.flo56958.minetinker.listeners;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.data.Lists;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.modifiers.ModManager;
import de.flo56958.minetinker.modifiers.types.Power;
import de.flo56958.minetinker.utils.PlayerInfo;
import de.flo56958.minetinker.utils.data.DataHandler;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class EasyHarvestListener implements Listener {

	private static final ModManager modManager = ModManager.instance();

	private static void harvestCrops(@NotNull final Player player, @NotNull final ItemStack tool, @NotNull final Block block) {
		final Ageable ageable = (Ageable) block.getBlockData();

		if (ageable.getAge() == ageable.getMaximumAge()) {
			breakCrops(player, tool, block);
			playSound(block);
		}
	}

	private static void breakCrops(@NotNull final Player player, @NotNull final ItemStack tool, @NotNull final Block block) {
		if (!player.hasPermission("minetinker.easyharvest.use")) {
			return;
		}

		Power.HAS_POWER.get(player).set(true);
		final Material type = block.getType();

		final PlayerInfo.Direction direction = PlayerInfo.getFacingDirection(player);
		final Location location = block.getLocation();
		final World world = location.getWorld();

		final FileConfiguration config = MineTinker.getPlugin().getConfig();

		if (world == null) {
			return;
		}

		if (!player.isSneaking() && modManager.hasMod(tool, Power.instance())
				&& player.hasPermission("minetinker.modifiers.power.use")) {
			final int level = modManager.getModLevel(tool, Power.instance());

			if (level == 1) {
				Block b1;
				Block b2;

				if (direction == PlayerInfo.Direction.NORTH || direction == PlayerInfo.Direction.SOUTH) {
					if (config.getBoolean("Modifiers.Power.lv1_vertical")) {
						b1 = world.getBlockAt(location.add(0, 0, 1));
						b2 = world.getBlockAt(location.add(0, 0, -1));
					} else {
						b1 = world.getBlockAt(location.add(1, 0, 0));
						b2 = world.getBlockAt(location.add(-1, 0, 0));
					}
				} else if (direction == PlayerInfo.Direction.WEST || direction == PlayerInfo.Direction.EAST) {
					if (config.getBoolean("Modifiers.Power.lv1_vertical")) {
						b1 = world.getBlockAt(location.add(1, 0, 0));
						b2 = world.getBlockAt(location.add(-1, 0, 0));
					} else {
						b1 = world.getBlockAt(location.add(0, 0, 1));
						b2 = world.getBlockAt(location.add(0, 0, -1));
					}
				} else {
					return;
				}
				if (b1.getBlockData() instanceof final Ageable blockOneAgeable) {
					if (b1.getType().equals(block.getType()) && (blockOneAgeable.getAge() == blockOneAgeable.getMaximumAge())) {
						breakBlock(b1, player, tool);
						replantCrops(player, b1, type);
					}
				}

				if (b2.getBlockData() instanceof final Ageable blockTwoAgeable) {
					if (b2.getType().equals(block.getType()) && (blockTwoAgeable.getAge() == blockTwoAgeable.getMaximumAge())) {
						breakBlock(b2, player, tool);
						replantCrops(player, b2, type);
					}
				}
			} else {
				for (int x = -(level - 1); x <= (level - 1); x++) {
					for (int z = -(level - 1); z <= (level - 1); z++) {
						if (!(x == 0 && z == 0)) {
							final Block b1 = block.getWorld().getBlockAt(block.getLocation().add(x, 0, z));

							if (!(b1.getBlockData() instanceof final Ageable blockOneAgeable)) {
								continue;
							}

							if (b1.getType().equals(block.getType()) && (blockOneAgeable.getAge() == blockOneAgeable.getMaximumAge())) {
								breakBlock(b1, player, tool);
								replantCrops(player, b1, type);
							}
						}
					}
				}
			}
		}

		breakBlock(block, player, tool);
		replantCrops(player, block, type);

		Power.HAS_POWER.get(player).set(false);
	}

	private static void replantCrops(@NotNull final Player player, @NotNull final Block block, @NotNull final Material material) {
        if (!MineTinker.getPlugin().getConfig().getBoolean("EasyHarvest.replant")) return;
        if (!player.hasPermission("minetinker.easyharvest.replant")) return;

        if (player.getGameMode() == GameMode.CREATIVE) {
            block.setType(material);
            return;
        }

        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack == null)
                // This is necessary as even though this is annotated @NotNull, it's still null sometimes
                continue;

            if (material == Material.BEETROOTS && itemStack.getType() == Material.BEETROOT_SEEDS) {
                itemStack.setAmount(itemStack.getAmount() - 1);
                block.setType(material);
                break;
            } else if (material == Material.CARROTS && itemStack.getType() == Material.CARROT) {
                itemStack.setAmount(itemStack.getAmount() - 1);
                block.setType(material);
                break;
            } else if (material == Material.POTATOES && itemStack.getType() == Material.POTATO) {
                itemStack.setAmount(itemStack.getAmount() - 1);
                block.setType(material);
                break;
            } else if (material == Material.WHEAT && itemStack.getType() == Material.WHEAT_SEEDS) {
                itemStack.setAmount(itemStack.getAmount() - 1);
                block.setType(material);
                break;
            } else if (material == Material.NETHER_WART && itemStack.getType() == Material.NETHER_WART) {
                itemStack.setAmount(itemStack.getAmount() - 1);
                block.setType(material);
                break;
            }
        }
    }

	private static void playSound(@NotNull final Block block) {
		if (MineTinker.getPlugin().getConfig().getBoolean("EasyHarvest.Sound")) {
			block.getWorld().playSound(block.getLocation(), Sound.ITEM_HOE_TILL, 1.0F, 0.5F);
		}
	}

	private static void breakBlock(@NotNull final Block block, @NotNull final Player player, @NotNull final ItemStack tool) {
		try {
			DataHandler.playerBreakBlock(player, block, tool);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	@EventHandler
	public void onHarvestTry(@NotNull final PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

		final Player player = event.getPlayer();
		if (Lists.WORLDS_EASYHARVEST.contains(player.getWorld().getName())) return;

		final ItemStack tool = player.getInventory().getItemInMainHand();

		if (!ToolType.HOE.contains(tool.getType())) return;
		if (!modManager.isToolViable(tool)) return;

		if (event.getClickedBlock() == null) return;
		if (event.getItem() == null) return;

		final Block block = event.getClickedBlock();
		if (!(block.getBlockData() instanceof Ageable)) return;

		//triggers a pseudoevent to find out if the Player can build
		final BlockPlaceEvent placeEvent = new BlockPlaceEvent(block, block.getState(),
				block, event.getItem(), player, true, EquipmentSlot.HAND);
		Bukkit.getPluginManager().callEvent(placeEvent);

		//check the pseudoevent
		if (!placeEvent.canBuild() || placeEvent.isCancelled()) return;

		harvestCrops(player, tool, block);
	}
}

package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Types.Power;
import de.flo56958.MineTinker.Utilities.PlayerInfo;
import de.flo56958.MineTinker.Utilities.nms.NBTUtils;
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

public class EasyHarvestListener implements Listener {

	private static final ModManager modManager = ModManager.instance();

	private static void harvestCrops(Player player, ItemStack tool, Block block) {
		Ageable ageable = (Ageable) block.getBlockData();

		if (ageable.getAge() == ageable.getMaximumAge()) {
			breakCrops(player, tool, block);
			playSound(block);
		}
	}

	private static void breakCrops(Player player, ItemStack tool, Block block) {
		if (!player.hasPermission("minetinker.easyharvest.use")) {
			return;
		}

		Power.HASPOWER.get(player).set(true);
		Material type = block.getType();

		String direction = PlayerInfo.getFacingDirection(player);
		Location location = block.getLocation();
		World world = location.getWorld();

		FileConfiguration config = Main.getPlugin().getConfig();

		if (world == null) {
			return;
		}

		if (!player.isSneaking() && modManager.hasMod(tool, Power.instance())) {
			if (!player.hasPermission("minetinker.modifiers.power.use")) {
				return;
			}

			int level = modManager.getModLevel(tool, Power.instance());

			if (level == 1) {
				Block b1;
				Block b2;

				if (direction.equals("N") || direction.equals("S")) {
					if (config.getBoolean("Modifiers.Power.lv1_vertical")) {
						b1 = world.getBlockAt(location.add(0, 0, 1));
						b2 = world.getBlockAt(location.add(0, 0, -1));
					} else {
						b1 = world.getBlockAt(location.add(1, 0, 0));
						b2 = world.getBlockAt(location.add(-1, 0, 0));
					}
				} else if (direction.equals("W") || direction.equals("E")) {
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
				if (b1.getBlockData() instanceof Ageable) {
					Ageable blockOneAgeable = (Ageable) b1.getBlockData();
					if (b1.getType().equals(block.getType()) && (blockOneAgeable.getAge() == blockOneAgeable.getMaximumAge())) {
						breakBlock(b1, player);
						replantCrops(player, b1, type);
					}
				}

				if (b2.getBlockData() instanceof Ageable) {
					Ageable blockTwoAgeable = (Ageable) b2.getBlockData();
					if (b2.getType().equals(block.getType()) && (blockTwoAgeable.getAge() == blockTwoAgeable.getMaximumAge())) {
						breakBlock(b2, player);
						replantCrops(player, b2, type);
					}
				}
			} else {
				for (int x = -(level - 1); x <= (level - 1); x++) {
					for (int z = -(level - 1); z <= (level - 1); z++) {
						if (!(x == 0 && z == 0)) {
							Block b1 = block.getWorld().getBlockAt(block.getLocation().add(x, 0, z));

							if (!(b1.getBlockData() instanceof Ageable)) {
								continue;
							}

							Ageable blockOneAgeable = (Ageable) b1.getBlockData();

							if (b1.getType().equals(block.getType()) && (blockOneAgeable.getAge() == blockOneAgeable.getMaximumAge())) {
								breakBlock(b1, player);
								replantCrops(player, b1, type);
							}
						}
					}
				}
			}
		}

		breakBlock(block, player);
		replantCrops(player, block, type);

		Power.HASPOWER.get(player).set(false);
	}

	private static void replantCrops(Player p, Block b, Material m) {
		if (Main.getPlugin().getConfig().getBoolean("EasyHarvest.replant")) {
			if (!p.hasPermission("minetinker.easyharvest.replant")) {
				return;
			}

			for (ItemStack is : p.getInventory().getContents()) {
				if (is == null) {
					// This is necessary as even though this is annotated @NotNull, it's still null sometimes
					continue;
				}

				if (m == Material.BEETROOTS && is.getType() == Material.BEETROOT_SEEDS) {
					is.setAmount(is.getAmount() - 1);
					b.setType(m);
					break;
				} else if (m == Material.CARROTS && is.getType() == Material.CARROT) {
					is.setAmount(is.getAmount() - 1);
					b.setType(m);
					break;
				} else if (m == Material.POTATOES && is.getType() == Material.POTATO) {
					is.setAmount(is.getAmount() - 1);
					b.setType(m);
					break;
				} else if (m == Material.WHEAT && is.getType() == Material.WHEAT_SEEDS) {
					is.setAmount(is.getAmount() - 1);
					b.setType(m);
					break;
				} else if (m == Material.NETHER_WART && is.getType() == Material.NETHER_WART) {
					is.setAmount(is.getAmount() - 1);
					b.setType(m);
					break;
				}
			}
		}
	}

	private static void playSound(Block b) {
		if (Main.getPlugin().getConfig().getBoolean("EasyHarvest.Sound")) {
			b.getWorld().playSound(b.getLocation(), Sound.ITEM_HOE_TILL, 1.0F, 0.5F);
		}
	}

	private static void breakBlock(Block b, Player p) {
		NBTUtils.getHandler().playerBreakBlock(p, b);
	}

	@EventHandler
	public void onHarvestTry(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}

		Player player = event.getPlayer();

		if (Lists.WORLDS_EASYHARVEST.contains(player.getWorld().getName())) {
			return;
		}

		if (!(player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE)) {
			return;
		}

		ItemStack tool = player.getInventory().getItemInMainHand();

		if (!ToolType.HOE.contains(tool.getType())) {
			return;
		}

		if (!modManager.isToolViable(tool)) {
			return;
		}

		if (event.getClickedBlock() == null) {
			return;
		}

		if (event.getItem() == null) {
			return;
		}

		Block block = event.getClickedBlock();

		if (!(block.getBlockData() instanceof Ageable)) {
			return;
		}

		//triggers a pseudoevent to find out if the Player can build
		BlockPlaceEvent placeEvent = new BlockPlaceEvent(block, block.getState(), block, event.getItem(), player, true, EquipmentSlot.HAND);
		Bukkit.getPluginManager().callEvent(placeEvent);

		//check the pseudoevent
		if (!placeEvent.canBuild() || placeEvent.isCancelled()) {
			return;
		}

		harvestCrops(player, tool, block);
	}
}

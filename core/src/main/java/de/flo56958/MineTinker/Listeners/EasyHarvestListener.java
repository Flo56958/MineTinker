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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.block.data.Ageable;
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
    private static final FileConfiguration config = Main.getPlugin().getConfig();

    @EventHandler
    public void onHarvestTry(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        Player p = e.getPlayer();

        if (Lists.WORLDS_EASYHARVEST.contains(p.getWorld().getName())) {
            return;
        }

        if (!(p.getGameMode().equals(GameMode.SURVIVAL) || p.getGameMode().equals(GameMode.ADVENTURE))) {
            return;
        }

        ItemStack tool = p.getInventory().getItemInMainHand();

        if (!ToolType.HOE.contains(tool.getType())) {
            return;
        }

        if (!modManager.isToolViable(tool)) {
            return;
        }

        if (e.getClickedBlock() == null) {
            return;
        }

        if (e.getItem() == null) {
            return;
        }

        Block b = e.getClickedBlock();

        if (!(b.getState().getBlockData() instanceof Ageable)) {
            return;
        }

        //triggers a pseudoevent to find out if the Player can build
        BlockPlaceEvent placeEvent = new BlockPlaceEvent(b, b.getState(), b, e.getItem(), p, true, EquipmentSlot.HAND);
        Bukkit.getPluginManager().callEvent(placeEvent);

        //check the pseudoevent
        if (!placeEvent.canBuild() || placeEvent.isCancelled()) {
            return;
        }

        harvestCrops(p, tool, b);
    }

    private static void harvestCrops(Player p, ItemStack tool, Block b) {
        Ageable ageable = (Ageable)b.getState().getBlockData();

        if (ageable.getAge() == ageable.getMaximumAge()) {
            breakCrops(p, tool, b);
            playSound(b);
        }
    }

    private static void breakCrops(Player p, ItemStack tool, Block b) {
        Power.HASPOWER.get(p).set(true);
        Material m = b.getType();

        String direction = PlayerInfo.getFacingDirection(p);
        Location location = b.getLocation();
        World world = location.getWorld();

        if (world == null) {
            return;
        }

        if (modManager.hasMod(tool, Power.instance()) && !p.isSneaking()) {
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

                Ageable blockOneAgeable = (Ageable)b1.getState().getBlockData();
                if (b1.getType().equals(b.getType()) && (blockOneAgeable.getAge() == blockOneAgeable.getMaximumAge())) {
                    breakBlock(b1, p);
                    replantCrops(p, b1, m);
                }

                Ageable blockTwoAgeable = (Ageable)b1.getState().getBlockData();
                if (b2.getType().equals(b.getType()) && (blockTwoAgeable.getAge() == blockTwoAgeable.getMaximumAge())) {
                    breakBlock(b2, p);
                    replantCrops(p, b2, m);
                }
            } else {
                for (int x = -(level - 1); x <= (level - 1); x++) {
                    for (int z = -(level - 1); z <= (level - 1); z++) {
                        if (!(x == 0 && z == 0)) {
                            Block b1 = b.getWorld().getBlockAt(b.getLocation().add(x, 0, z));
                            Ageable blockOneAgeable = (Ageable)b1.getState().getBlockData();

                            if (b1.getType().equals(b.getType()) && (blockOneAgeable.getAge() == blockOneAgeable.getMaximumAge())) {
                                breakBlock(b1, p);
                                replantCrops(p, b1, m);
                            }
                        }
                    }
                }
            }
        }

        breakBlock(b, p);
        replantCrops(p, b, m);

        Power.HASPOWER.get(p).set(false);
    }

    private static void replantCrops(Player p, Block b, Material m) {
        if (config.getBoolean("EasyHarvest.replant")) {
            for (ItemStack is : p.getInventory().getContents()) {

                if (is == null)  {
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
        if (config.getBoolean("EasyHarvest.Sound")) {
            b.getWorld().playSound(b.getLocation(), Sound.ITEM_HOE_TILL, 1.0F, 0.5F);
        }
    }

    private static void breakBlock(Block b, Player p) {
        NBTUtils.getHandler().playerBreakBlock(p, b);
    }
}

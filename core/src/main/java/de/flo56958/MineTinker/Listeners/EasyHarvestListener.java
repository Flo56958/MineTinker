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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Crops;
import org.bukkit.material.NetherWarts;

public class EasyHarvestListener implements Listener {

    private static final ModManager modManager = ModManager.instance();
    private static final FileConfiguration config = Main.getPlugin().getConfig();

    @EventHandler
    public void onHarvestTry(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        Player p = e.getPlayer();

        if (Lists.WORLDS_EASYHARVEST.contains(p.getWorld().getName())) return;
        if (!(p.getGameMode().equals(GameMode.SURVIVAL) || p.getGameMode().equals(GameMode.ADVENTURE))) return;

        ItemStack tool = p.getInventory().getItemInMainHand();

        if (!ToolType.HOE.getMaterials().contains(tool.getType())) return;
        if (!modManager.isToolViable(tool)) return;
        if (e.getClickedBlock() == null) return;
        if (e.getItem() == null) return;

        Block b = e.getClickedBlock();

        if (b.getState().getData() instanceof Crops) {
            //triggers a pseudoevent to find out if the Player can build
            BlockPlaceEvent placeEvent = new BlockPlaceEvent(b, b.getState(), b, e.getItem(), p, true);
            Bukkit.getPluginManager().callEvent(placeEvent);

            //check the pseudoevent
            if (!placeEvent.canBuild() || placeEvent.isCancelled()) return;

            harvestCrops(p, tool, b);
        }
        
        if (b.getState().getData() instanceof NetherWarts) {
            //triggers a pseudoevent to find out if the Player can build
            BlockPlaceEvent placeEvent = new BlockPlaceEvent(b, b.getState(), b, e.getItem(), p, true);
            Bukkit.getPluginManager().callEvent(placeEvent);

            //check the pseudoevent
            if (!placeEvent.canBuild() || placeEvent.isCancelled()) return;

            harvestWarts(p, tool, b);
        }
    }

    private static void harvestWarts(Player p, ItemStack tool, Block b) {
        NetherWarts w = (NetherWarts) b.getState().getData();

        if (w.getState().equals(NetherWartsState.RIPE)) {
            breakWarts(p, tool, b);
            playSound(b);
        }
    }

    private static void breakWarts(Player p, ItemStack tool, Block b) {
        Power.HASPOWER.get(p).set(true);
        Material m = b.getType();

        if (modManager.hasMod(tool, Power.instance()) && !p.isSneaking()) {

            int level = modManager.getModLevel(tool, Power.instance());

            if (level == 1) {
                Block b1;
                Block b2;

                if (PlayerInfo.getFacingDirection(p).equals("N") || PlayerInfo.getFacingDirection(p).equals("S")) {
                    if (config.getBoolean("Modifiers.Power.lv1_vertical")) {
                        b1 = b.getWorld().getBlockAt(b.getLocation().add(0, 0, 1));
                        b2 = b.getWorld().getBlockAt(b.getLocation().add(0, 0, -1));
                    } else {
                        b1 = b.getWorld().getBlockAt(b.getLocation().add(1, 0, 0));
                        b2 = b.getWorld().getBlockAt(b.getLocation().add(-1, 0, 0));
                    }
                } else if (PlayerInfo.getFacingDirection(p).equals("W") || PlayerInfo.getFacingDirection(p).equals("E")) {
                    if (config.getBoolean("Modifiers.Power.lv1_vertical")) {
                        b1 = b.getWorld().getBlockAt(b.getLocation().add(1, 0, 0));
                        b2 = b.getWorld().getBlockAt(b.getLocation().add(-1, 0, 0));
                    } else {
                        b1 = b.getWorld().getBlockAt(b.getLocation().add(0, 0, 1));
                        b2 = b.getWorld().getBlockAt(b.getLocation().add(0, 0, -1));
                    }
                } else {
                    return;
                }

                if (b1.getType().equals(b.getType()) && ((NetherWarts) b1.getState().getData()).getState().equals(NetherWartsState.RIPE)) {
                    breakBlock(b1, p);
                    replantCrops(p, b1, m);
                }

                if (b2.getType().equals(b.getType()) && ((NetherWarts) b2.getState().getData()).getState().equals(NetherWartsState.RIPE)) {
                    breakBlock(b2, p);
                    replantCrops(p, b2, m);
                }
            } else {
                for (int x = -(level - 1); x <= (level - 1); x++) {
                    for (int z = -(level - 1); z <= (level - 1); z++) {
                        if (!(x == 0 && z == 0)) {
                            Block b1 = b.getWorld().getBlockAt(b.getLocation().add(x, 0, z));

                            if (b1.getType().equals(b.getType()) && ((NetherWarts) b1.getState().getData()).getState().equals(NetherWartsState.RIPE)) {
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

    private static void harvestCrops(Player p, ItemStack tool, Block b) {
        Crops c = (Crops) b.getState().getData();

        if (c.getState().equals(CropState.RIPE)) {
            breakCrops(p, tool, b);
            playSound(b);
        }
    }

    private static void breakCrops(Player p, ItemStack tool, Block b) {
        Power.HASPOWER.get(p).set(true);
        Material m = b.getType();

        if (modManager.hasMod(tool, Power.instance()) && !p.isSneaking()) {
            int level = modManager.getModLevel(tool, Power.instance());
            if (level == 1) {
                Block b1;
                Block b2;

                if (PlayerInfo.getFacingDirection(p).equals("N") || PlayerInfo.getFacingDirection(p).equals("S")) {
                    if (config.getBoolean("Modifiers.Power.lv1_vertical")) {
                        b1 = b.getWorld().getBlockAt(b.getLocation().add(0, 0, 1));
                        b2 = b.getWorld().getBlockAt(b.getLocation().add(0, 0, -1));
                    } else {
                        b1 = b.getWorld().getBlockAt(b.getLocation().add(1, 0, 0));
                        b2 = b.getWorld().getBlockAt(b.getLocation().add(-1, 0, 0));
                    }
                } else if (PlayerInfo.getFacingDirection(p).equals("W") || PlayerInfo.getFacingDirection(p).equals("E")) {
                    if (config.getBoolean("Modifiers.Power.lv1_vertical")) {
                        b1 = b.getWorld().getBlockAt(b.getLocation().add(1, 0, 0));
                        b2 = b.getWorld().getBlockAt(b.getLocation().add(-1, 0, 0));
                    } else {
                        b1 = b.getWorld().getBlockAt(b.getLocation().add(0, 0, 1));
                        b2 = b.getWorld().getBlockAt(b.getLocation().add(0, 0, -1));
                    }
                } else {
                    return;
                }

                if (b1.getType().equals(b.getType()) && ((Crops) b1.getState().getData()).getState().equals(CropState.RIPE)) {
                    breakBlock(b1, p);
                    replantCrops(p, b1, m);
                }

                if (b2.getType().equals(b.getType()) && ((Crops) b2.getState().getData()).getState().equals(CropState.RIPE)) {
                    breakBlock(b2, p);
                    replantCrops(p, b2, m);
                }
            } else {
                for (int x = -(level - 1); x <= (level - 1); x++) {
                    for (int z = -(level - 1); z <= (level - 1); z++) {
                        if (!(x == 0 && z == 0)) {
                            Block b1 = b.getWorld().getBlockAt(b.getLocation().add(x, 0, z));

                            if (b1.getType().equals(b.getType()) && ((Crops) b1.getState().getData()).getState().equals(CropState.RIPE)) {
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
                if (is == null) continue;
                if (m.equals(Material.BEETROOTS) && is.getType().equals(Material.BEETROOT_SEEDS)) {
                    is.setAmount(is.getAmount() - 1);
                    b.setType(m);
                    break;
                } else if (m.equals(Material.CARROTS) && is.getType().equals(Material.CARROT)) {
                    is.setAmount(is.getAmount() - 1);
                    b.setType(m);
                    break;
                } else if (m.equals(Material.POTATOES) && is.getType().equals(Material.POTATO)) {
                    is.setAmount(is.getAmount() - 1);
                    b.setType(m);
                    break;
                } else if (m.equals(Material.WHEAT) && is.getType().equals(Material.WHEAT_SEEDS)) {
                    is.setAmount(is.getAmount() - 1);
                    b.setType(m);
                    break;
                } else if (m.equals(Material.NETHER_WART) && is.getType().equals(Material.NETHER_WART)) {
                    is.setAmount(is.getAmount() - 1);
                    b.setType(m);
                    break;
                }
            }
        }
    }

    private static void playSound(Block b) {
        if (config.getBoolean("EasyHarvest.Sound")) b.getWorld().playSound(b.getLocation(), Sound.ITEM_HOE_TILL, 1.0F, 0.5F);
    }

    private static void breakBlock(Block b, Player p) {
        NBTUtils.getHandler().playerBreakBlock(p, b);
    }
}

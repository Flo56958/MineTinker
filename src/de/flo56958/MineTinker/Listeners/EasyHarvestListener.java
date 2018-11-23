package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Types.ModifierType;
import de.flo56958.MineTinker.Modifiers.Types.Power;
import de.flo56958.MineTinker.Utilities.PlayerInfo;
import net.minecraft.server.v1_13_R2.BlockPosition;
import org.bukkit.CropState;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Crops;

public class EasyHarvestListener implements Listener {

    private static final ModManager modManager = Main.getModManager();
    private static final FileConfiguration config = Main.getPlugin().getConfig();

    @EventHandler
    public void onHarvestTry(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) { return; }
        Player p = e.getPlayer();

        if (!Lists.WORLDS_EASYHARVEST.contains(p.getWorld().getName())) { return; }
        if (!(p.getGameMode().equals(GameMode.SURVIVAL) || p.getGameMode().equals(GameMode.ADVENTURE))) { return; }

        ItemStack tool = p.getInventory().getItemInMainHand();
        if (!ToolType.HOE.getMaterials().contains(tool.getType())) { return; }

        if (!modManager.isToolViable(tool)) { return; }

        Block b = e.getClickedBlock();
        if (b.getState().getData() instanceof Crops) { harvest(p, tool, b); }
    }

    private static void harvest(Player p, ItemStack tool, Block b) {
        Crops c = (Crops) b.getState().getData();

        if (c.getState() == CropState.RIPE) {
            breakCrops(p, tool, b);
            playSound(b);
        }
    }

    private static void breakCrops(Player p, ItemStack tool, Block b) {
        Power.HASPOWER.put(p, true);
        Material m = b.getType();
        if (modManager.hasMod(tool, modManager.get(ModifierType.POWER))) {
            int level = modManager.getModLevel(tool, modManager.get(ModifierType.POWER));
            if (level == 1) {
                Block b1 = null;
                Block b2 = null;
                if (PlayerInfo.getFacingDirection(p).equals("N") || PlayerInfo.getFacingDirection(p).equals("S")) {
                    b1 = b.getWorld().getBlockAt(b.getLocation().add(1, 0, 0));
                    b2 = b.getWorld().getBlockAt(b.getLocation().add(-1, 0, 0));
                } else if (PlayerInfo.getFacingDirection(p).equals("W") || PlayerInfo.getFacingDirection(p).equals("E")) {
                    b1 = b.getWorld().getBlockAt(b.getLocation().add(0, 0, 1));
                    b2 = b.getWorld().getBlockAt(b.getLocation().add(0, 0, -1));
                }
                if (b1.getType().equals(b.getType()) && ((Crops) b1.getState().getData()).getState().equals(CropState.RIPE)) {
                    ((CraftPlayer) p).getHandle().playerInteractManager.breakBlock(new BlockPosition(b1.getX(), b1.getY(), b1.getZ()));
                    replantCrops(p, b1, m);
                }
                if (b2.getType().equals(b.getType()) && ((Crops) b2.getState().getData()).getState().equals(CropState.RIPE)) {
                    ((CraftPlayer) p).getHandle().playerInteractManager.breakBlock(new BlockPosition(b2.getX(), b2.getY(), b2.getZ()));
                    replantCrops(p, b2, m);
                }
            } else {
                for (int x = -(level - 1); x <= (level - 1); x++) {
                    for (int z = -(level - 1); z <= (level - 1); z++) {
                        if (!(x == 0 && z == 0)) {
                            Block b1 = b.getWorld().getBlockAt(b.getLocation().add(x, 0, z));
                            if (b1.getType().equals(b.getType()) && ((Crops) b1.getState().getData()).getState().equals(CropState.RIPE)) {
                                ((CraftPlayer) p).getHandle().playerInteractManager.breakBlock(new BlockPosition(b1.getX(), b1.getY(), b1.getZ()));
                                replantCrops(p, b1, m);
                            }
                        }
                    }
                }
            }
        }

        ((CraftPlayer) p).getHandle().playerInteractManager.breakBlock(new BlockPosition(b.getX(), b.getY(), b.getZ()));
        replantCrops(p, b, m);

        Power.HASPOWER.put(p, false);
    }

    private static void replantCrops(Player p, Block b, Material m) {
        if (config.getBoolean("EasyHarvest.replant")) {
            for (ItemStack is : p.getInventory().getContents()) {
                if (is == null) { continue; }

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
                }
            }
        }
    }

    private static void playSound(Block b) {
        if (config.getBoolean("EasyHarvest.Sound")) {
            b.getWorld().playSound(b.getLocation(), Sound.ITEM_HOE_TILL, 1.0F, 0.5F);

        }
    }
}

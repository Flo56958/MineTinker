package de.flo56958.MineTinker.Utilities;

import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Data.Strings;
import net.minecraft.server.v1_13_R2.BlockPosition;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class ModifierEffect {

    private static final FileConfiguration config = Main.getPlugin().getConfig();

    public static void selfRepair(Player p, ItemStack tool) {
        List<String> lore = tool.getItemMeta().getLore();
        if (config.getBoolean("Modifiers.Self-Repair.allowed") && p.hasPermission("minetinker.modifiers.selfrepair.use")) {
            for (int i = 1; i <= config.getInt("Modifiers.Self-Repair.MaxLevel"); i++) {
                if (lore.contains(Strings.SELFREPAIR + i)) {
                    Random rand = new Random();
                    int n = rand.nextInt(100);
                    if (n <= config.getInt("Modifiers.Self-Repair.PercentagePerLevel") * i) {
                        int heal = config.getInt("Modifiers.Self-Repair.HealthRepair");
                        short dura = (short) (tool.getDurability() - heal);
                        if (dura < 0) {
                            dura = 0;
                        }
                        p.getInventory().getItemInMainHand().setDurability(dura);
                        ChatWriter.log(false, p.getDisplayName() + " triggered Self-Repair on " + ItemGenerator.getDisplayName(tool) + ChatColor.WHITE + " (" + tool.getType().toString() + ")!");
                    }
                    break;
                }
            }
        }
    }

    public static void xp(Player p, ItemStack tool) {
        List<String> lore = tool.getItemMeta().getLore();
        if (config.getBoolean("Modifiers.XP.allowed") && p.hasPermission("minetinker.modifiers.xp.use")) {
            for (int i = 1; i <= config.getInt("Modifiers.XP.MaxLevel"); i++) {
                if (lore.contains(Strings.XP + i)) {
                    Random rand = new Random();
                    int n = rand.nextInt(100);
                    if (n <= config.getInt("Modifiers.XP.PercentagePerLevel") * i) {
                        ExperienceOrb orb = p.getWorld().spawn(p.getLocation(), ExperienceOrb.class);
                        orb.setExperience(config.getInt("Modifiers.XP.XPAmount"));
                        ChatWriter.log(false, p.getDisplayName() + " triggered XP on " + ItemGenerator.getDisplayName(tool) + ChatColor.WHITE + " (" + tool.getType().toString() + ")!");
                    }
                    break;
                }
            }
        }
    }

    public static void powerCreateFarmland(Player p, ItemStack tool, Block b) {
        if (b.getType().equals(Material.GRASS_BLOCK) || b.getType().equals(Material.DIRT)) {
            if (b.getWorld().getBlockAt(b.getLocation().add(0, 1, 0)).getType().equals(Material.AIR)) {
                tool.setDurability((short) (tool.getDurability() + 1));
                Bukkit.getPluginManager().callEvent(new PlayerInteractEvent(p, Action.RIGHT_CLICK_BLOCK, tool, b, BlockFace.UP));
                b.setType(Material.FARMLAND); //Event only does Plugin event (no vanilla conversion to Farmland and Tool-Damage)
            }
        }
    }

    public static void powerBlockBreak(Block b, CraftPlayer p) {
        if (!b.getType().equals(Material.AIR) && !b.getType().equals(Material.CAVE_AIR) && !b.getType().equals(Material.BEDROCK) && !b.getType().equals(Material.WATER) && !b.getType().equals(Material.BUBBLE_COLUMN) && !b.getType().equals(Material.LAVA)) {
            p.getHandle().playerInteractManager.breakBlock(new BlockPosition(b.getX(), b.getY(), b.getZ()));
        }
    }
}

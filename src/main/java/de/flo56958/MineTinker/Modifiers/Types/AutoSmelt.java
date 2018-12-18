package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ModifierFailCause;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.ModifierFailEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Craftable;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class AutoSmelt extends Modifier implements Craftable {

    private static final ModManager modManager = ModManager.instance();
    private static final PluginManager pluginManager = Bukkit.getPluginManager();
    private static final FileConfiguration config = Main.getMain().getConfigurations().getConfig("Auto-Smelt.yml");
    private final int percentagePerLevel;
    private final boolean hasSound;

    public AutoSmelt() {
        super(config.getString("Auto-Smelt.name"),
                "[" + config.getString("Auto-Smelt.name_modifier") + "] " + config.getString("Auto-Smelt.description"),
                ModifierType.AUTO_SMELT,
                ChatColor.YELLOW,
                config.getInt("Auto-Smelt.MaxLevel"),
                ItemGenerator.itemEnchanter(Material.FURNACE, ChatColor.YELLOW + config.getString("Auto-Smelt.name_modifier"), 1, Enchantment.FIRE_ASPECT, 1),
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.PICKAXE, ToolType.SHOVEL)),
                Main.getPlugin());
        this.percentagePerLevel = config.getInt("Auto-Smelt.PercentagePerLevel");
        this.hasSound = config.getBoolean("Auto-Smelt.Sound");
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {

        if (modManager.get(ModifierType.SILK_TOUCH) != null) {
            if (modManager.hasMod(tool, modManager.get(ModifierType.SILK_TOUCH))) {
                pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
                return null;
            }
        }

        return Modifier.checkAndAdd(p, tool, this, "autosmelt", isCommand);
    }

    public void effect(Player p, ItemStack tool, Block b, BlockBreakEvent e) {
        if (!p.hasPermission("minetinker.modifiers.autosmelt.use")) { return; }//TODO: Think about more blocks for Auto-Smelt
        if (!modManager.hasMod(tool, this)) { return; }

        if (!config.getBoolean("Auto-Smelt.works_under_water")) {
            if (p.isSwimming() || p.getWorld().getBlockAt(p.getLocation()).getType().equals(Material.WATER)) { return; }
        }

        boolean allowLuck = false;
        int amount = 1;
        Material loot;
        switch (b.getType()) {
            case STONE:
                if (!config.getBoolean("Auto-Smelt.smelt_stone")) { return; }
            case COBBLESTONE:
                loot = Material.STONE;
                break;

            case SAND:
                loot = Material.GLASS;
                break;

            case RED_SAND:
                loot = Material.RED_STAINED_GLASS;
                break;

            case ACACIA_LOG:
            case BIRCH_LOG:
            case DARK_OAK_LOG:
            case JUNGLE_LOG:
            case OAK_LOG:
            case SPRUCE_LOG:

            case STRIPPED_ACACIA_LOG:
            case STRIPPED_BIRCH_LOG:
            case STRIPPED_DARK_OAK_LOG:
            case STRIPPED_JUNGLE_LOG:
            case STRIPPED_OAK_LOG:
            case STRIPPED_SPRUCE_LOG:

            case ACACIA_WOOD:
            case BIRCH_WOOD:
            case DARK_OAK_WOOD:
            case JUNGLE_WOOD:
            case OAK_WOOD:
            case SPRUCE_WOOD:

            case STRIPPED_ACACIA_WOOD:
            case STRIPPED_BIRCH_WOOD:
            case STRIPPED_DARK_OAK_WOOD:
            case STRIPPED_JUNGLE_WOOD:
            case STRIPPED_OAK_WOOD:
            case STRIPPED_SPRUCE_WOOD:
                allowLuck = true;
                loot = Material.CHARCOAL;
                break;

            case IRON_ORE:
                allowLuck = true;
                loot = Material.IRON_INGOT;
                break;

            case GOLD_ORE:
                allowLuck = true;
                loot = Material.GOLD_INGOT;
                break;

            case NETHERRACK:
                allowLuck = true;
                loot = Material.NETHER_BRICK;
                break;

            case KELP_PLANT:
                loot = Material.DRIED_KELP;
                break;

            case WET_SPONGE:
                loot = Material.SPONGE;
                break;

            case COAL_ORE:
            case COAL_BLOCK:
                if (!config.getBoolean("Auto-Smelt.burn_coal")) { return; }
                loot = Material.AIR;
                break;

            case CLAY:
                loot = Material.BRICK;
                amount = 4;
                break;

            default:
                return;
        }

        Random rand = new Random();
        int n = rand.nextInt(100);
        if (n <= this.percentagePerLevel * modManager.getModLevel(tool, this)) {
            if (allowLuck && modManager.get(ModifierType.LUCK) != null) {
                int level = modManager.getModLevel(tool, modManager.get(ModifierType.LUCK));
                if (level > 0) {
                    amount = amount + rand.nextInt(level) * amount; //Times amount is for clay as it drops 4 per block
                }
            }

            if (!loot.equals(Material.AIR)) {
                ItemStack items = new ItemStack(loot, amount);
                b.getLocation().getWorld().dropItemNaturally(b.getLocation(), items);
            }
            e.setDropItems(false);

            b.getLocation().getWorld().spawnParticle(Particle.FLAME, b.getLocation(), 5);
            if (this.hasSound) {
                b.getLocation().getWorld().playSound(b.getLocation(), Sound.ENTITY_GENERIC_BURN, 0.2F, 0.5F);
            }
            ChatWriter.log(false, p.getDisplayName() + " triggered Auto-Smelt on " + ItemGenerator.getDisplayName(tool) + ChatColor.GRAY + " (" + tool.getType().toString() + ") while mining " + e.getBlock().getType().toString() + "!");
        }
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(config, this, "Auto-Smelt", "Modifier_Autosmelt");
    }
}

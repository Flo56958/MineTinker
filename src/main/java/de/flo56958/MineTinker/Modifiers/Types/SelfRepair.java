package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Craftable;
import de.flo56958.MineTinker.Modifiers.Enchantable;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class SelfRepair extends Modifier implements Enchantable, Craftable {

    private static final FileConfiguration config = Main.getConfigurations().getConfig("Self-Repair.yml");

    private final int percentagePerLevel;
    private final int healthRepair;

    public SelfRepair() {
        super(config.getString("Self-Repair.name"),
                "[" + config.getString("Self-Repair.name_modifier") + "] " + config.getString("Self-Repair.description"),
                ModifierType.SELF_REPAIR,
                ChatColor.GREEN,
                config.getInt("Self-Repair.MaxLevel"),
                ItemGenerator.itemEnchanter(Material.MOSSY_COBBLESTONE, ChatColor.GREEN + config.getString("Self-Repair.name_modifier"), 1, Enchantment.MENDING, 1),
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.HOE, ToolType.PICKAXE, ToolType.SHOVEL, ToolType.SWORD,
                                                ToolType.HELMET, ToolType.CHESTPLATE, ToolType.LEGGINGS, ToolType.BOOTS, ToolType.ELYTRA)),
                Main.getPlugin());
        this.percentagePerLevel = config.getInt("Self-Repair.PercentagePerLevel");
        this.healthRepair = config.getInt("Self-Repair.HealthRepair");
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        return Modifier.checkAndAdd(p, tool, this, "selfrepair", isCommand);
    }

    @SuppressWarnings("deprecation")
	public void effect(Player p, ItemStack tool) {
        if (!p.hasPermission("minetinker.modifiers.selfrepair.use")) { return; }
        if (!modManager.hasMod(tool, this)) { return; }

        int level = modManager.getModLevel(tool, this);

        Random rand = new Random();
        int n = rand.nextInt(100);
        if (n <= this.percentagePerLevel * level) {
            short dura = (short) (tool.getDurability() - this.healthRepair);

            if (dura < 0) {
                dura = 0;
            }

            tool.setDurability(dura);
            ChatWriter.log(false, p.getDisplayName() + " triggered Self-Repair on " + ItemGenerator.getDisplayName(tool) + ChatColor.GRAY + " (" + tool.getType().toString() + ")!");
        }
    }

    @Override
    public void enchantItem(Player p, ItemStack item) {
        if (!p.hasPermission("minetinker.modifiers.selfrepair.craft")) { return; }
        _createModifierItem(config, p, this, "Self-Repair");
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(config, this, "Self-Repair", "Modifier_SelfRepair");
    }
}

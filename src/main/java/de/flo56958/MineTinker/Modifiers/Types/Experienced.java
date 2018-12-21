package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Craftable;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Experienced extends Modifier implements Craftable {

    private static final FileConfiguration config = Main.getMain().getConfigurations().getConfig("Experienced.yml");

    private final int percentagePerLevel;
    private final int amount;

    public Experienced() {
        super(config.getString("Experienced.name"),
                "[Bottle o' Experience] " + config.getString("Experienced.description"),
                ModifierType.EXPERIENCED,
                ChatColor.GREEN,
                config.getInt("Experienced.MaxLevel"),
                new ItemStack(Material.EXPERIENCE_BOTTLE, 1),
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.HOE, ToolType.PICKAXE, ToolType.SHOVEL, ToolType.SWORD,
                                                ToolType.HELMET, ToolType.CHESTPLATE, ToolType.LEGGINGS, ToolType.BOOTS, ToolType.ELYTRA)),
                Main.getPlugin());
        this.percentagePerLevel = config.getInt("Experienced.PercentagePerLevel");
        this.amount = config.getInt("Experienced.Amount");
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        return Modifier.checkAndAdd(p, tool, this, "experienced", isCommand);
    }

    public void effect(Player p, ItemStack tool) {
        if (!p.hasPermission("minetinker.modifiers.experienced.use")) { return; }
        if (!modManager.hasMod(tool, this)) { return; }

        int level = modManager.getModLevel(tool, this);

        Random rand = new Random();
        int n = rand.nextInt(100);
        if (n <= this.percentagePerLevel * level) {
            ExperienceOrb orb = p.getWorld().spawn(p.getLocation(), ExperienceOrb.class);
            orb.setExperience(this.amount);
            ChatWriter.log(false, p.getDisplayName() + " triggered Experienced on " + ItemGenerator.getDisplayName(tool) + ChatColor.WHITE + " (" + tool.getType().toString() + ")!");
        }
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(config, this, "Experienced", "Modifier_Experienced");
    }
}

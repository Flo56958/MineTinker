package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Craftable;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import de.flo56958.MineTinker.Utilities.Modifiers_Config;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Soulbound extends Modifier implements Craftable {

    private HashMap<Player, ItemStack[]> storedItemStacks = new HashMap<>();
    private boolean toolDropable;

    public Soulbound() {
        super(ModifierType.SOULBOUND,
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.HOE, ToolType.PICKAXE, ToolType.SHOVEL, ToolType.SWORD,
                        ToolType.HELMET, ToolType.CHESTPLATE, ToolType.LEGGINGS, ToolType.BOOTS, ToolType.ELYTRA)),
                Main.getPlugin());
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Soulbound", "Modifier_Soulbound");
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        return checkAndAdd(p, tool, this, "soulbound", isCommand);
    }

    @Override
    public void removeMod(ItemStack tool) { }

    @Override
    public void reload() {
        FileConfiguration config = getConfig();
        config.options().copyDefaults(true);

        String key = "Soulbound";
        config.addDefault(key + ".allowed", true);
        config.addDefault(key + ".name", key);
        config.addDefault(key + ".name_modifier", "Powerinfused Netherstar");
        config.addDefault(key + ".description", "Do not lose the tool when dying.");
        config.addDefault(key + ".description_modifier", "%WHITE%Modifier-Item for the Soulbound-Modifier");
        config.addDefault(key + ".Color", "%GRAY%");
        config.addDefault(key + ".MaxLevel", 1);
        config.addDefault(key + ".PercentagePerLevel", 100);
        config.addDefault(key + ".ToolDropable", true);
        config.addDefault(key + ".Recipe.Enabled", true);
        config.addDefault(key + ".Recipe.Top", "BLB");
        config.addDefault(key + ".Recipe.Middle", "LNL");
        config.addDefault(key + ".Recipe.Bottom", "BLB");
        config.addDefault(key + ".Recipe.Materials.B", "BLAZE_ROD");
        config.addDefault(key + ".Recipe.Materials.L", "LAVA_BUCKET");
        config.addDefault(key + ".Recipe.Materials.N", "NETHER_STAR");

        ConfigurationManager.saveConfig(config);

        init(config.getString(key + ".name"),
                "[" + config.getString(key + ".name_modifier") + "] " + config.getString(key + ".description"),
                ChatWriter.getColor(config.getString(key + ".Color")),
                getConfig().getInt(key + ".MaxLevel"),
                modManager.createModifierItem(Material.FURNACE, ChatWriter.getColor(config.getString(key + ".Color")) + config.getString(key + ".name_modifier"), ChatWriter.addColors(config.getString(key + ".description_modifier")), this));

        this.toolDropable = config.getBoolean(key + ".ToolDropable");
    }

    public void effect(Player p) {

    }

    private static FileConfiguration getConfig() {
        return ConfigurationManager.getConfig(Modifiers_Config.Auto_Smelt);
    }

    @Override
    public boolean isAllowed() {
        return getConfig().getBoolean("Soulbound.allowed");
    }
}

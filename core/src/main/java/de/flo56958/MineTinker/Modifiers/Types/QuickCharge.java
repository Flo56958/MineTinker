package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuickCharge extends Modifier {

    private static QuickCharge instance;

    public static QuickCharge instance() {
        synchronized (QuickCharge.class) {
            if (instance == null) instance = new QuickCharge();
        }
        return instance;
    }

    private QuickCharge() {
        super(ModifierType.QUICK_CHARGE,
                new ArrayList<>(Arrays.asList(ToolType.CROSSBOW)),
                Main.getPlugin());
    }

    @Override
    public List<Enchantment> getAppliedEnchantments() {
        List<Enchantment> enchantments = new ArrayList<>();
        enchantments.add(Enchantment.QUICK_CHARGE);

        return enchantments;
    }

    @Override
    public void reload() {
        FileConfiguration config = getConfig();
        config.options().copyDefaults(true);

        String key = "Quick-Charge";
        config.addDefault(key + ".allowed", true);
        config.addDefault(key + ".name", key);
        config.addDefault(key + ".name_modifier", "Spare Crossbow Parts");
        config.addDefault(key + ".modifier_item", "IRON_INGOT"); //Needs to be a viable Material-Type
        config.addDefault(key + ".description", "Reduces crossbow loading time!");
        config.addDefault(key + ".description_modifier", "%GRAY%Modifier-Item for the Quick-Charge-Modifier");
        config.addDefault(key + ".Color", "%GRAY%");
        config.addDefault(key + ".MaxLevel", 3);
        config.addDefault(key + ".Recipe.Enabled", true);
        config.addDefault(key + ".Recipe.Top", "OOO");
        config.addDefault(key + ".Recipe.Middle", "III");
        config.addDefault(key + ".Recipe.Bottom", "OOO");
        config.addDefault(key + ".Recipe.Materials.I", "IRON_INGOT");
        config.addDefault(key + ".Recipe.Materials.O", "OAK_PLANKS");

        ConfigurationManager.saveConfig(config);

        init(config.getString(key + ".name"),
                "[" + config.getString(key + ".name_modifier") + "] " + config.getString(key + ".description"),
                ChatWriter.getColor(config.getString(key + ".Color")),
                config.getInt(key + ".MaxLevel"),
                modManager.createModifierItem(Material.getMaterial(config.getString(key + ".modifier_item")), ChatWriter.getColor(config.getString(key + ".Color")) + config.getString(key + ".name_modifier"), ChatWriter.addColors(config.getString(key + ".description_modifier")), this));
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (Modifier.checkAndAdd(p, tool, this, "quickcharge", isCommand) == null) {
            return null;
        }

        ItemMeta meta = tool.getItemMeta();

        if (meta != null) {
            if (ToolType.CROSSBOW.getMaterials().contains(tool.getType())) {
                meta.addEnchant(Enchantment.QUICK_CHARGE, modManager.getModLevel(tool, this), true);
            }

            if (Main.getPlugin().getConfig().getBoolean("HideEnchants")) {
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            } else {
                meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            tool.setItemMeta(meta);
        }

        return tool;
    }

    @Override
    public void removeMod(ItemStack tool) {
        ItemMeta meta = tool.getItemMeta();

        if (meta != null) {
            meta.removeEnchant(Enchantment.QUICK_CHARGE);
            tool.setItemMeta(meta);
        }
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Quick-Charge", "Modifier_QuickCharge");
    }

    private static FileConfiguration getConfig() {
        return ConfigurationManager.getConfig(ModifierType.QUICK_CHARGE.getFileName());
    }

    @Override
    public boolean isAllowed() {
        return getConfig().getBoolean("Quick-Charge.allowed");
    }
}

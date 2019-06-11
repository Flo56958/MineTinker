package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Craftable;
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

public class Thorned extends Modifier implements Craftable {

    private static Thorned instance;

    public static Thorned instance() {
        synchronized (Thorned.class) {
            if (instance == null) instance = new Thorned();
        }
        return instance;
    }

    private Thorned() {
        super(ModifierType.THORNED,
                new ArrayList<>(Arrays.asList(ToolType.HELMET, ToolType.CHESTPLATE, ToolType.LEGGINGS, ToolType.BOOTS)),
                Main.getPlugin());
    }

    @Override
    public List<Enchantment> getAppliedEnchantments() {
        List<Enchantment> enchantments = new ArrayList<>();
        enchantments.add(Enchantment.THORNS);

        return enchantments;
    }

    @Override
    public void reload() {
        FileConfiguration config = getConfig();
        config.options().copyDefaults(true);

        String key = "Thorned";
        config.addDefault(key + ".allowed", true);
        config.addDefault(key + ".name", key);
        config.addDefault(key + ".name_modifier", "Spiked Plating");
        config.addDefault(key + ".modifier_item", "VINE"); //Needs to be a viable Material-Type
        config.addDefault(key + ".description", "Your armor harms others when they damage you!");
        config.addDefault(key + ".description_modifier", "%WHITE%Modifier-Item for the Thorned-Modifier");
        config.addDefault(key + ".Color", "%DARK_GREEN%");
        config.addDefault(key + ".MaxLevel", 3);
        config.addDefault(key + ".Recipe.Enabled", true);
        config.addDefault(key + ".Recipe.Top", "VAV");
        config.addDefault(key + ".Recipe.Middle", "ASA");
        config.addDefault(key + ".Recipe.Bottom", "VAV");
        config.addDefault(key + ".Recipe.Materials.V", "VINE");
        config.addDefault(key + ".Recipe.Materials.A", "ARROW");
        config.addDefault(key + ".Recipe.Materials.S", "SLIME_BALL");

        ConfigurationManager.saveConfig(config);

        init(config.getString(key + ".name"),
                "[" + config.getString(key + ".name_modifier") + "] " + config.getString(key + ".description"),
                ChatWriter.getColor(config.getString(key + ".Color")), config.getInt(key + ".MaxLevel"),
                modManager.createModifierItem(Material.getMaterial(config.getString(key + ".modifier_item")),
                ChatWriter.getColor(config.getString(key + ".Color")) + config.getString(key + ".name_modifier"),
                ChatWriter.addColors(config.getString(key + ".description_modifier")), this));
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (Modifier.checkAndAdd(p, tool, this, "thorned", isCommand) == null) {
            return null;
        }

        ItemMeta meta = tool.getItemMeta();

        if (meta != null) {
            meta.addEnchant(Enchantment.THORNS, modManager.getModLevel(tool, this), true);

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
            meta.removeEnchant(Enchantment.THORNS);
            tool.setItemMeta(meta);
        }
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Thorned", "Modifier_Thorned");
    }

    private static FileConfiguration getConfig() {
        return ConfigurationManager.getConfig(ModifierType.THORNED.getFileName());
    }

    @Override
    public boolean isAllowed() {
        return getConfig().getBoolean("Thorned.allowed");
    }
}

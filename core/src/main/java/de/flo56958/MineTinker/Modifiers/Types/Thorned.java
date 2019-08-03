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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Thorned extends Modifier {

    private static Thorned instance;

    public static Thorned instance() {
        synchronized (Thorned.class) {
            if (instance == null) {
                instance = new Thorned();
            }
        }

        return instance;
    }

    private Thorned() {
        super("Thorned", "Thorned.yml",
                new ArrayList<>(Arrays.asList(ToolType.HELMET, ToolType.CHESTPLATE, ToolType.LEGGINGS, ToolType.BOOTS)),
                Main.getPlugin());
    }

    @Override
    public List<Enchantment> getAppliedEnchantments() {
        return Collections.singletonList(Enchantment.THORNS);
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
        config.addDefault(key + ".EnchantCost", 10);
        config.addDefault(key + ".MaxLevel", 3);

        config.addDefault(key + ".Recipe.Enabled", true);
        config.addDefault(key + ".Recipe.Top", "VAV");
        config.addDefault(key + ".Recipe.Middle", "ASA");
        config.addDefault(key + ".Recipe.Bottom", "VAV");

        Map<String, String> recipeMaterials = new HashMap<>();
        recipeMaterials.put("V", "VINE");
        recipeMaterials.put("A", "ARROW");
        recipeMaterials.put("S", "SLIME_BALL");

        config.addDefault(key + ".Recipe.Materials", recipeMaterials);

        ConfigurationManager.saveConfig(config);
        ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

        init(config.getString(key + ".name"),
                "[" + config.getString(key + ".name_modifier") + "] \u200B" + config.getString(key + ".description"),
                ChatWriter.getColor(config.getString(key + ".Color")), config.getInt(key + ".MaxLevel"),
                modManager.createModifierItem(Material.getMaterial(config.getString(key + ".modifier_item")),
                ChatWriter.getColor(config.getString(key + ".Color")) + config.getString(key + ".name_modifier"),
                ChatWriter.addColors(config.getString(key + ".description_modifier")), this));
    }

    @Override
    public boolean applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (!Modifier.checkAndAdd(p, tool, this, "thorned", isCommand)) {
            return false;
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

        return true;
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Thorned", "Modifier_Thorned");
    }

    @Override
    public boolean isAllowed() {
        return getConfig().getBoolean("Thorned.allowed");
    }
}

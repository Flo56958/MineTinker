package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ModifierFailCause;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.ModifierFailEvent;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Freezing extends Modifier {

    private static Freezing instance;

    public static Freezing instance() {
        synchronized (Freezing.class) {
            if (instance == null) instance = new Freezing();
        }
        return instance;
    }

    private Freezing() {
        super(ModifierType.FREEZING,
                new ArrayList<>(Collections.singletonList(ToolType.BOOTS)),
                Main.getPlugin());
    }

    @Override
    public List<Enchantment> getAppliedEnchantments() {
        List<Enchantment> enchantments = new ArrayList<>();
        enchantments.add(Enchantment.FROST_WALKER);

        return enchantments;
    }

    @Override
    public void reload() {
        FileConfiguration config = getConfig();
        config.options().copyDefaults(true);

        String key = "Freezing";
        config.addDefault(key + ".allowed", true);
        config.addDefault(key + ".name", key);
        config.addDefault(key + ".name_modifier", "Icy Crystal");
        config.addDefault(key + ".modifier_item", "DIAMOND"); //Needs to be a viable Material-Type
        config.addDefault(key + ".description", "It is freezing around you.");
        config.addDefault(key + ".description_modifier", "%WHITE%Modifier-Item for the Freezing-Modifier");
        config.addDefault(key + ".Color", "%AQUA%");
        config.addDefault(key + ".MaxLevel", 3);

        config.addDefault(key + ".Recipe.Enabled", true);
        config.addDefault(key + ".Recipe.Top", "BBB");
        config.addDefault(key + ".Recipe.Middle", "BDB");
        config.addDefault(key + ".Recipe.Bottom", "BBB");

        Map<String, String> recipeMaterials = new HashMap<>();
        recipeMaterials.put("B", "BLUE_ICE");
        recipeMaterials.put("D", "DIAMOND");

        config.addDefault(key + ".Recipe.Materials", recipeMaterials);

        ConfigurationManager.saveConfig(config);

        init(config.getString(key + ".name"),
                "[" + config.getString(key + ".name_modifier") + "] \u200B" + config.getString(key + ".description"),
                ChatWriter.getColor(config.getString(key + ".Color")),
                config.getInt(key + ".MaxLevel"),
                modManager.createModifierItem(Material.getMaterial(config.getString(key + ".modifier_item")), ChatWriter.getColor(config.getString(key + ".Color")) + config.getString(key + ".name_modifier"), ChatWriter.addColors(config.getString(key + ".description_modifier")), this));

    }

    @Override
    public boolean applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (modManager.hasMod(tool, Aquaphilic.instance())) {
            pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
            return false;
        }

        if (Modifier.checkAndAdd(p, tool, this, "freezing", isCommand)) return false;

        ItemMeta meta = tool.getItemMeta();

        if (meta != null) {
            meta.addEnchant(Enchantment.FROST_WALKER, modManager.getModLevel(tool, this), true);

            if (Main.getPlugin().getConfig().getBoolean("HideEnchants")) {
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            } else {
                meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
        }

        tool.setItemMeta(meta);
        return true;
    }

    @Override
    public void removeMod(ItemStack tool) {
        ItemMeta meta = tool.getItemMeta();

        if (meta != null) {
            meta.removeEnchant(Enchantment.FROST_WALKER);
            tool.setItemMeta(meta);
        }
    }

    private static FileConfiguration getConfig() {
        return ConfigurationManager.getConfig(ModifierType.FREEZING.getFileName());
    }

    @Override
    public boolean isAllowed() {
        return getConfig().getBoolean("Freezing.allowed");
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Freezing", "Modifier_Freezing");
    }
}

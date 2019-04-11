package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ModifierFailCause;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.ModifierFailEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Craftable;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import de.flo56958.MineTinker.Utilities.Modifiers_Config;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;

public class Aquaphilic extends Modifier implements Craftable {

    private static Aquaphilic instance;

    public static Aquaphilic instance() {
        if (instance == null) instance = new Aquaphilic();
        return instance;
    }

    private Aquaphilic() {
        super(ModifierType.AQUAPHILIC,
                new ArrayList<>(Arrays.asList(ToolType.BOOTS, ToolType.HELMET)),
                Main.getPlugin());
    }

    @Override
    public void reload() {
        FileConfiguration config = getConfig();
        config.options().copyDefaults(true);

        String key = "Aquaphilic";
        config.addDefault(key + ".allowed", true);
        config.addDefault(key + ".name", key);
        config.addDefault(key + ".name_modifier", "Pearl of the ocean");
        config.addDefault(key + ".description", "Make the water your friend");
        config.addDefault(key + ".description_modifier", "%WHITE%Modifier-Item for the Aquaphilic-Modifier");
        config.addDefault(key + ".Color", "%AQUA%");
        config.addDefault(key + ".MaxLevel", 3); //higher will have no effect on depth strider
        config.addDefault(key + ".Recipe.Enabled", true);
        config.addDefault(key + ".Recipe.Top", "PNP");
        config.addDefault(key + ".Recipe.Middle", "NHN");
        config.addDefault(key + ".Recipe.Bottom", "PNP");
        config.addDefault(key + ".Recipe.Materials.H", "HEART_OF_THE_SEA");
        config.addDefault(key + ".Recipe.Materials.N", "NAUTILUS_SHELL");
        config.addDefault(key + ".Recipe.Materials.P", "PRISMARINE_SHARD");

        ConfigurationManager.saveConfig(config);

        init(config.getString(key + ".name"),
                "[" + config.getString(key + ".name_modifier") + "] " + config.getString(key + ".description"),
                ChatWriter.getColor(config.getString(key + ".Color")),
                config.getInt(key + ".MaxLevel"),
                modManager.createModifierItem(Material.HEART_OF_THE_SEA, ChatWriter.getColor(config.getString(key + ".Color")) + config.getString(key + ".name_modifier"), ChatWriter.addColors(config.getString(key + ".description_modifier")), this));

    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (modManager.get(ModifierType.FREEZING) != null) {
            if (modManager.hasMod(tool, modManager.get(ModifierType.FREEZING))) {
                pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
                return null;
            }
        }

        if (Modifier.checkAndAdd(p, tool, this, "aquaphilic", isCommand) == null) {
            return null;
        }

        ItemMeta meta = tool.getItemMeta();

        if (ToolType.BOOTS.getMaterials().contains(tool.getType())) {
            meta.addEnchant(Enchantment.DEPTH_STRIDER, modManager.getModLevel(tool, this), true);
        } else if (ToolType.HELMET.getMaterials().contains(tool.getType())) {
            meta.addEnchant(Enchantment.OXYGEN, modManager.getModLevel(tool, this), true);
            meta.addEnchant(Enchantment.WATER_WORKER, modManager.getModLevel(tool, this), true);
        }

        if (Main.getPlugin().getConfig().getBoolean("HideEnchants")) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        } else {
            meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        tool.setItemMeta(meta);

        return tool;
    }

    @Override
    public void removeMod(ItemStack tool) {
        ItemMeta meta = tool.getItemMeta();
        meta.removeEnchant(Enchantment.DEPTH_STRIDER);
        meta.removeEnchant(Enchantment.OXYGEN);
        meta.removeEnchant(Enchantment.WATER_WORKER);
        tool.setItemMeta(meta);
    }

    private static FileConfiguration getConfig() {
        return ConfigurationManager.getConfig(Modifiers_Config.Aquaphilic);
    }

    @Override
    public boolean isAllowed() {
        return getConfig().getBoolean("Aquaphilic.allowed");
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Aquaphilic", "Modifier_Aquaphilic");
    }
}

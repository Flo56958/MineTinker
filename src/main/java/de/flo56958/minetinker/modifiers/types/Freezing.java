package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.data.ModifierFailCause;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.events.ModifierFailEvent;
import de.flo56958.minetinker.Main;
import de.flo56958.minetinker.modifiers.Craftable;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utilities.ChatWriter;
import de.flo56958.minetinker.utilities.ConfigurationManager;
import de.flo56958.minetinker.utilities.Modifiers_Config;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Freezing extends Modifier implements Craftable {

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
        config.addDefault(key + ".Recipe.Materials.B", "BLUE_ICE");
        config.addDefault(key + ".Recipe.Materials.D", "DIAMOND");

        ConfigurationManager.saveConfig(config);

        String name = config.getString(key + ".name");
        String modName = config.getString(key + ".name_modifier");
        String description = config.getString(key + ".description");
        String color = config.getString(key + ".Color");
        int maxLevel = config.getInt(key + ".MaxLevel");
        String modItem = config.getString(key + ".modifier_item");
        String modDescription = config.getString(key + ".description_modifier");

        if (name == null || modName == null || description == null || color == null || modItem == null || modDescription == null) return;

        init(name, "[" + modName + "] " + description, ChatWriter.getColor(color), maxLevel,
                modManager.createModifierItem(Material.getMaterial(modItem), ChatWriter.getColor(color) + modName,
                        ChatWriter.addColors(modDescription), this));
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (modManager.get(ModifierType.AQUAPHILIC) != null) {
            if (modManager.hasMod(tool, modManager.get(ModifierType.AQUAPHILIC))) {
                pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
                return null;
            }
        }

        if (Modifier.checkAndAdd(p, tool, this, "freezing", isCommand) == null) {
            return null;
        }

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
        return tool;
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
        return ConfigurationManager.getConfig(Modifiers_Config.Freezing);
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

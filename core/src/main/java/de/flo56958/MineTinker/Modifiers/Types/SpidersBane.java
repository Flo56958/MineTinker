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
import java.util.List;

public class SpidersBane extends Modifier implements Craftable {

    private boolean compatibleWithSmite;
    private boolean compatibleWithSharpness;

    private static SpidersBane instance;

    public static SpidersBane instance() {
        synchronized (SpidersBane.class) {
            if (instance == null) instance = new SpidersBane();
        }
        return instance;
    }

    private SpidersBane() {
        super(ModifierType.SPIDERSBANE,
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.SWORD)),
                Main.getPlugin());
    }

    @Override
    public List<Enchantment> getAppliedEnchantments() {
        List<Enchantment> enchantments = new ArrayList<>();
        enchantments.add(Enchantment.DAMAGE_ARTHROPODS);

        return enchantments;
    }

    @Override
    public void reload() {
        FileConfiguration config = getConfig();
        config.options().copyDefaults(true);

        String key = "SpidersBane";
        config.addDefault(key + ".allowed", true);
        config.addDefault(key + ".name", key);
        config.addDefault(key + ".name_modifier", "Cleansed Spider Eye");
        config.addDefault(key + ".modifier_item", "FERMENTED_SPIDER_EYE"); //Needs to be a viable Material-Type
        config.addDefault(key + ".description", "Weapon does additional damage to Spiders!");
        config.addDefault(key + ".description_modifier", "%WHITE%Modifier-Item for the Spider's-Bane-Modifier");
        config.addDefault(key + ".Color", "%RED%");
        config.addDefault(key + ".MaxLevel", 5);
        config.addDefault(key + ".CompatibleWithSmite", false);
        config.addDefault(key + ".CompatibleWithSharpness", false);
        config.addDefault(key + ".Recipe.Enabled", true);
        config.addDefault(key + ".Recipe.Top", "ESE");
        config.addDefault(key + ".Recipe.Middle", "SFS");
        config.addDefault(key + ".Recipe.Bottom", "ESE");
        config.addDefault(key + ".Recipe.Materials.E", "SPIDER_EYE");
        config.addDefault(key + ".Recipe.Materials.S", "STRING");
        config.addDefault(key + ".Recipe.Materials.F", "FERMENTED_SPIDER_EYE");

        ConfigurationManager.saveConfig(config);

        init(config.getString(key + ".name"),
                "[" + config.getString(key + ".name_modifier") + "] " + config.getString(key + ".description"),
                ChatWriter.getColor(config.getString(key + ".Color")), config.getInt(key + ".MaxLevel"),
                modManager.createModifierItem(Material.getMaterial(config.getString(key + ".modifier_item")),
                ChatWriter.getColor(config.getString(key + ".Color")) + config.getString(key + ".name_modifier"),
                ChatWriter.addColors(config.getString(key + ".description_modifier")), this));

        this.compatibleWithSmite = config.getBoolean(key + ".CompatibleWithSmite");
        this.compatibleWithSharpness = config.getBoolean(key + ".CompatibleWithSharpness");
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (Modifier.checkAndAdd(p, tool, this, "sharpness", isCommand) == null) {
            return null;
        }

        ItemMeta meta = tool.getItemMeta();

        if (meta != null) {
            if (ToolType.AXE.getMaterials().contains(tool.getType()) || ToolType.SWORD.getMaterials().contains(tool.getType())) {
                if (!this.compatibleWithSmite) {
                    if (modManager.hasMod(tool, Smite.instance()) || meta.hasEnchant(Enchantment.DAMAGE_UNDEAD)) {
                        pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
                        return null;
                    }
                } else if (!this.compatibleWithSharpness) {
                    if (modManager.hasMod(tool, Sharpness.instance()) || meta.hasEnchant(Enchantment.DAMAGE_ALL)) {
                        pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
                        return null;
                    }
                }

                meta.addEnchant(Enchantment.DAMAGE_ARTHROPODS, modManager.getModLevel(tool, this), true);
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
            meta.removeEnchant(Enchantment.DAMAGE_ARTHROPODS);

            tool.setItemMeta(meta);
        }
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "SpidersBane", "Modifier_SpidersBane");
    }

    private static FileConfiguration getConfig() {
        return ConfigurationManager.getConfig(Modifiers_Config.SpidersBane);
    }

    @Override
    public boolean isAllowed() {
        return getConfig().getBoolean("SpidersBane.allowed");
    }
}

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

import java.io.File;
import java.util.*;

public class SpidersBane extends Modifier {

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
        super("Spider's-Bane", "Spiders-Bane.yml",
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.SWORD)),
                Main.getPlugin());
    }

    @Override
    public List<Enchantment> getAppliedEnchantments() {
        return Collections.singletonList(Enchantment.DAMAGE_ARTHROPODS);
    }

    @Override
    public void reload() {
        FileConfiguration config = getConfig();
        config.options().copyDefaults(true);

        String key = "Spiders-Bane";
        config.addDefault(key + ".allowed", true);
        config.addDefault(key + ".name", "Spider's-Bane");
        config.addDefault(key + ".name_modifier", "Cleansed Spider Eye");
        config.addDefault(key + ".modifier_item", "FERMENTED_SPIDER_EYE"); //Needs to be a viable Material-Type
        config.addDefault(key + ".description", "Weapon does additional damage to Spiders!");
        config.addDefault(key + ".description_modifier", "%WHITE%Modifier-Item for the Spider's-Bane-Modifier");
        config.addDefault(key + ".Color", "%RED%");
        config.addDefault(key + ".EnchantCost", 10);
        config.addDefault(key + ".MaxLevel", 5);

        config.addDefault(key + ".CompatibleWithSmite", false);
        config.addDefault(key + ".CompatibleWithSharpness", false);
        config.addDefault(key + ".Recipe.Enabled", true);

        config.addDefault(key + ".Recipe.Top", "ESE");
        config.addDefault(key + ".Recipe.Middle", "SFS");
        config.addDefault(key + ".Recipe.Bottom", "ESE");

        Map<String, String> recipeMaterials = new HashMap<>();
        recipeMaterials.put("E", "SPIDER_EYE");
        recipeMaterials.put("S", "STRING");
        recipeMaterials.put("F", "FERMENTED_SPIDER_EYE");

        config.addDefault(key + ".Recipe.Materials", recipeMaterials);

        ConfigurationManager.saveConfig(config);
        ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

        init(config.getString(key + ".name"),
                "[" + config.getString(key + ".name_modifier") + "] \u200B" + config.getString(key + ".description"),
                ChatWriter.getColor(config.getString(key + ".Color")), config.getInt(key + ".MaxLevel"),
                modManager.createModifierItem(Material.getMaterial(config.getString(key + ".modifier_item")),
                ChatWriter.getColor(config.getString(key + ".Color")) + config.getString(key + ".name_modifier"),
                ChatWriter.addColors(config.getString(key + ".description_modifier")), this));

        this.compatibleWithSmite = config.getBoolean(key + ".CompatibleWithSmite");
        this.compatibleWithSharpness = config.getBoolean(key + ".CompatibleWithSharpness");
    }

    @Override
    public boolean applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (!Modifier.checkAndAdd(p, tool, this, "spidersbane", isCommand)) {
            return false;
        }

        ItemMeta meta = tool.getItemMeta();

        if (meta != null) {
            if (ToolType.AXE.contains(tool.getType()) || ToolType.SWORD.contains(tool.getType())) {
                if (!this.compatibleWithSmite) {
                    if (modManager.hasMod(tool, Smite.instance()) || meta.hasEnchant(Enchantment.DAMAGE_UNDEAD)) {
                        pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
                        return false;
                    }
                }

                if (!this.compatibleWithSharpness) {
                    if (modManager.hasMod(tool, Sharpness.instance()) || meta.hasEnchant(Enchantment.DAMAGE_ALL)) {
                        pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
                        return false;
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
        } else return false;

        return true;
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "SpidersBane", "Modifier_SpidersBane");
    }

    @Override
    public boolean isAllowed() {
        return getConfig().getBoolean("Spiders-Bane.allowed");
    }
}

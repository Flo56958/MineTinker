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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiShot extends Modifier {

    private boolean compatibleWithPiercing;

    private static MultiShot instance;

    public static MultiShot instance() {
        synchronized (MultiShot.class) {
            if (instance == null) instance = new MultiShot();
        }
        return instance;
    }

    private MultiShot() {
        super(ModifierType.MULTISHOT,
                new ArrayList<>(Arrays.asList(ToolType.CROSSBOW)),
                Main.getPlugin());
    }

    @Override
    public List<Enchantment> getAppliedEnchantments() {
        List<Enchantment> enchantments = new ArrayList<>();
        enchantments.add(Enchantment.MULTISHOT);

        return enchantments;
    }

    @Override
    public void reload() {
        FileConfiguration config = getConfig();
        config.options().copyDefaults(true);

        String key = "Multishot";
        config.addDefault(key + ".allowed", true);
        config.addDefault(key + ".name", key);
        config.addDefault(key + ".name_modifier", "Enchanted Blaze-Rod");
        config.addDefault(key + ".modifier_item", "BLAZE_ROD"); //Needs to be a viable Material-Type
        config.addDefault(key + ".description", "Inflames enemies!");
        config.addDefault(key + ".description_modifier", "%WHITE%Modifier-Item for the Multishot-Modifier");
        config.addDefault(key + ".Color", "%YELLOW%");
        config.addDefault(key + ".MaxLevel", 1);

        config.addDefault(key + ".CompatibleWithPiercing", false);

        config.addDefault(key + ".Recipe.Enabled", true);
        config.addDefault(key + ".Recipe.Top", "QQQ");
        config.addDefault(key + ".Recipe.Middle", "AAA");
        config.addDefault(key + ".Recipe.Bottom", "QQQ");

        Map<String, String> recipeMaterials = new HashMap<>();
        recipeMaterials.put("Q", "QUARTZ_BLOCK");
        recipeMaterials.put("A", "ARROW");

        config.addDefault(key + ".Recipe.Materials", recipeMaterials);
        // TODO: Better recipe

        ConfigurationManager.saveConfig(config);

        init(config.getString(key + ".name"),
                "[" + config.getString(key + ".name_modifier") + "] \u200B" + config.getString(key + ".description"),
                ChatWriter.getColor(config.getString(key + ".Color")),
                config.getInt(key + ".MaxLevel"),
                modManager.createModifierItem(Material.getMaterial(config.getString(key + ".modifier_item")), ChatWriter.getColor(config.getString(key + ".Color")) + config.getString(key + ".name_modifier"), ChatWriter.addColors(config.getString(key + ".description_modifier")), this));

        this.compatibleWithPiercing = config.getBoolean(key + ".CompatibleWithPiercing");
    }

    @Override
    public boolean applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (Modifier.checkAndAdd(p, tool, this, "multishot", isCommand)) return false;

        ItemMeta meta = tool.getItemMeta();

        if (meta != null) {
            if (ToolType.CROSSBOW.getMaterials().contains(tool.getType())) {
                if (!this.compatibleWithPiercing) {
                    if (modManager.hasMod(tool, Piercing.instance()) || meta.hasEnchant(Enchantment.PIERCING)) {
                        pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
                        return false;
                    }
                }

                meta.addEnchant(Enchantment.MULTISHOT, modManager.getModLevel(tool, this), true);
            }

            if (Main.getPlugin().getConfig().getBoolean("HideEnchants")) {
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            } else {
                meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            tool.setItemMeta(meta);
        }

        return false;
    }

    @Override
    public void removeMod(ItemStack tool) {
        ItemMeta meta = tool.getItemMeta();

        if (meta != null) {
            meta.removeEnchant(Enchantment.MULTISHOT);
            tool.setItemMeta(meta);
        }
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Multishot", "Modifier_Multishot");
    }

    private static FileConfiguration getConfig() {
        return ConfigurationManager.getConfig(ModifierType.MULTISHOT.getFileName());
    }

    @Override
    public boolean isAllowed() {
        return getConfig().getBoolean("Multishot.allowed");
    }
}

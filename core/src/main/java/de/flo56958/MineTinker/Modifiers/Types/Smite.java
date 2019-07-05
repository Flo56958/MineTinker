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

public class Smite extends Modifier {

    private boolean compatibleWithSharpness;
    private boolean compatibleWithArthropods;

    private static Smite instance;

    public static Smite instance() {
        synchronized (Smite.class) {
            if (instance == null) instance = new Smite();
        }
        return instance;
    }

    private Smite() {
        super("Smite", "Smite.yml",
                new ArrayList<>(Arrays.asList(ToolType.SWORD, ToolType.AXE)),
                Main.getPlugin());
    }

    @Override
    public List<Enchantment> getAppliedEnchantments() {
        List<Enchantment> enchantments = new ArrayList<>();
        enchantments.add(Enchantment.DAMAGE_UNDEAD);

        return enchantments;
    }

    @Override
    public void reload() {
        FileConfiguration config = getConfig();
        config.options().copyDefaults(true);

        String key = "Smite";
        config.addDefault(key + ".allowed", true);
        config.addDefault(key + ".name", key);
        config.addDefault(key + ".name_modifier", "Holy Bone");
        config.addDefault(key + ".modifier_item", "BONE"); //Needs to be a viable Material-Type
        config.addDefault(key + ".description", "Weapon does additional damage towards the Undead!");
        config.addDefault(key + ".description_modifier", "%YELLOW%Modifier-Item for the Smite-Modifier");
        config.addDefault(key + ".Color", "%YELLOW%");
        config.addDefault(key + ".MaxLevel", 5);

        config.addDefault(key + ".CompatibleWithSharpness", false);
        config.addDefault(key + ".CompatibleWithArthropods", false);

        config.addDefault(key + ".Recipe.Enabled", true);
        config.addDefault(key + ".Recipe.Top", "BMB");
        config.addDefault(key + ".Recipe.Middle", "MIM");
        config.addDefault(key + ".Recipe.Bottom", "BMB");

        Map<String, String> recipeMaterials = new HashMap<>();
        recipeMaterials.put("B", "BONE");
        recipeMaterials.put("M", "BONE_MEAL");
        recipeMaterials.put("I", "IRON_INGOT");

        config.addDefault(key + ".Recipe.Materials", recipeMaterials);

        ConfigurationManager.saveConfig(config);

        init(config.getString(key + ".name"),
                "[" + config.getString(key + ".name_modifier") + "] \u200B" + config.getString(key + ".description"),
                ChatWriter.getColor(config.getString(key + ".Color")), config.getInt(key + ".MaxLevel"),
                modManager.createModifierItem(Material.getMaterial(config.getString(key + ".modifier_item")),
                ChatWriter.getColor(config.getString(key + ".Color")) + config.getString(key + ".name_modifier"),
                ChatWriter.addColors(config.getString(key + ".description_modifier")), this));

        this.compatibleWithSharpness = config.getBoolean(key + ".CompatibleWithSharpness");
        this.compatibleWithArthropods = config.getBoolean(key + ".CompatibleWithArthropods");
    }

    @Override
    public boolean applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (!Modifier.checkAndAdd(p, tool, this, "smite", isCommand)) return false;

        ItemMeta meta = tool.getItemMeta();

        if (meta != null) {
            if (!ToolType.AXE.getMaterials().contains(tool.getType()) && !ToolType.SWORD.getMaterials().contains(tool.getType())) {
                if (!this.compatibleWithSharpness) {
                    if (modManager.hasMod(tool, Sharpness.instance()) || meta.hasEnchant(Enchantment.DAMAGE_ALL)) {
                        pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
                        return false;
                    }
                }

                if (!this.compatibleWithArthropods) {
                    if (modManager.hasMod(tool, SpidersBane.instance()) || meta.hasEnchant(Enchantment.DAMAGE_ARTHROPODS)) {
                        pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
                        return false;
                    }
                }

                meta.addEnchant(Enchantment.DAMAGE_UNDEAD, modManager.getModLevel(tool, this), true);
            }

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
    public void removeMod(ItemStack tool) {
        ItemMeta meta = tool.getItemMeta();

        if (meta != null) {
            meta.removeEnchant(Enchantment.DAMAGE_UNDEAD);

            tool.setItemMeta(meta);
        }
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Smite", "Modifier_Smite");
    }

    @Override
    public boolean isAllowed() {
        return getConfig().getBoolean("Smite.allowed");
    }
}

package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ModifierFailCause;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.ModifierFailEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Craftable;
import de.flo56958.MineTinker.Modifiers.Enchantable;
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

public class Piercing extends Modifier implements Craftable {

    private boolean compatibleWithMultishot;

    private static Piercing instance;

    public static Piercing instance() {
        synchronized (Piercing.class) {
            if (instance == null) instance = new Piercing();
        }
        return instance;
    }

    private Piercing() {
        super(ModifierType.PIERCING,
                new ArrayList<>(Arrays.asList(ToolType.CROSSBOW)),
                Main.getPlugin());
    }

    @Override
    public List<Enchantment> getAppliedEnchantments() {
        List<Enchantment> enchantments = new ArrayList<>();
        enchantments.add(Enchantment.PIERCING);

        return enchantments;
    }

    @Override
    public void reload() {
        FileConfiguration config = getConfig();
        config.options().copyDefaults(true);

        String key = "Piercing";
        config.addDefault(key + ".allowed", true);
        config.addDefault(key + ".name", key);
        config.addDefault(key + ".name_modifier", "Bodkin Point");
        config.addDefault(key + ".modifier_item", "ARROW"); //Needs to be a viable Material-Type
        config.addDefault(key + ".description", "Passes through enemies!");
        config.addDefault(key + ".description_modifier", "%GRAY%Modifier-Item for the Piercing-Modifier");
        config.addDefault(key + ".Color", "%GRAY%");
        config.addDefault(key + ".MaxLevel", 4);
        config.addDefault(key + ".CompatibleWithMultishot", false);
        config.addDefault(key + ".Recipe.Enabled", true);
        config.addDefault(key + ".Recipe.Top", "FIF");
        config.addDefault(key + ".Recipe.Middle", "OAO");
        config.addDefault(key + ".Recipe.Bottom", "FIF");
        config.addDefault(key + ".Recipe.Materials.F", "FLINT");
        config.addDefault(key + ".Recipe.Materials.I", "IRON_INGOT");
        config.addDefault(key + ".Recipe.Materials.O", "OAK_PLANKS");
        config.addDefault(key + ".Recipe.Materials.A", "ARROW");

        ConfigurationManager.saveConfig(config);

        init(config.getString(key + ".name"),
                "[" + config.getString(key + ".name_modifier") + "] " + config.getString(key + ".description"),
                ChatWriter.getColor(config.getString(key + ".Color")),
                config.getInt(key + ".MaxLevel"),
                modManager.createModifierItem(Material.getMaterial(config.getString(key + ".modifier_item")), ChatWriter.getColor(config.getString(key + ".Color")) + config.getString(key + ".name_modifier"), ChatWriter.addColors(config.getString(key + ".description_modifier")), this));

        this.compatibleWithMultishot = config.getBoolean(key + ".CompatibleWithMultishot");
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (Modifier.checkAndAdd(p, tool, this, "piercing", isCommand) == null) {
            return null;
        }

        ItemMeta meta = tool.getItemMeta();

        if (meta != null) {
            if (ToolType.CROSSBOW.getMaterials().contains(tool.getType())) {
                if (!this.compatibleWithMultishot) {
                    if (modManager.hasMod(tool, MultiShot.instance()) || meta.hasEnchant(Enchantment.MULTISHOT)) {
                        pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
                        return null;
                    }
                }

                meta.addEnchant(Enchantment.PIERCING, modManager.getModLevel(tool, this), true);
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
            meta.removeEnchant(Enchantment.PIERCING);
            tool.setItemMeta(meta);
        }
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Piercing", "Modifier_Piercing");
    }

    private static FileConfiguration getConfig() {
        return ConfigurationManager.getConfig(Modifiers_Config.Piercing);
    }

    @Override
    public boolean isAllowed() {
        return getConfig().getBoolean("Piercing.allowed");
    }
}

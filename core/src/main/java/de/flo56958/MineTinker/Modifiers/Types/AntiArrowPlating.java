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

public class AntiArrowPlating extends Modifier implements Craftable {

    private boolean compatibleWithProtecting;
    private boolean compatibleWithAntiFire;
    private boolean compatibleWithAntiBlast;

    private static AntiArrowPlating instance;

    public static AntiArrowPlating instance() {
        synchronized (AntiArrowPlating.class) {
            if (instance == null) instance = new AntiArrowPlating();
        }
        return instance;
    }

    private AntiArrowPlating() {
        super(ModifierType.ANTI_ARROW_PLATING,
                new ArrayList<>(Arrays.asList(ToolType.BOOTS, ToolType.LEGGINGS, ToolType.CHESTPLATE, ToolType.HELMET)),
                Main.getPlugin());
    }

    @Override
    public List<Enchantment> getAppliedEnchantments() {
        List<Enchantment> enchantments = new ArrayList<>();
        enchantments.add(Enchantment.PROTECTION_PROJECTILE);

        return enchantments;
    }

    @Override
    public void reload() {
        FileConfiguration config = getConfig();
        config.options().copyDefaults(true);

        String key = "Anti-Arrow-Plating";
        config.addDefault(key + ".allowed", true);
        config.addDefault(key + ".name", key);
        config.addDefault(key + ".name_modifier", "Pierce Resistant Metal");
        config.addDefault(key + ".modifier_item", "IRON_BLOCK"); //Needs to be a viable Material-Type
        config.addDefault(key + ".description", "Armor mitigates projectile damage!");
        config.addDefault(key + ".description_modifier", "%WHITE%Modifier-Item for the Anti-Arrow-Plating-Modifier");
        config.addDefault(key + ".Color", "%WHITE%");
        config.addDefault(key + ".MaxLevel", 5);
        config.addDefault(key + ".CompatibleWithProtecting", false);
        config.addDefault(key + ".CompatibleWithAntiFire", false);
        config.addDefault(key + ".CompatibleWithAntiBlast", false);
        config.addDefault(key + ".Recipe.Enabled", true);
        config.addDefault(key + ".Recipe.Top", "IMI");
        config.addDefault(key + ".Recipe.Middle", "MDM");
        config.addDefault(key + ".Recipe.Bottom", "IMI");
        config.addDefault(key + ".Recipe.Materials.I", "IRON_BLOCK");
        config.addDefault(key + ".Recipe.Materials.A", "ARROW");
        config.addDefault(key + ".Recipe.Materials.D", "DIAMOND");

        ConfigurationManager.saveConfig(config);

        init(config.getString(key + ".name"),
                "[" + config.getString(key + ".name_modifier") + "] " + config.getString(key + ".description"),
                ChatWriter.getColor(config.getString(key + ".Color")), config.getInt(key + ".MaxLevel"),
                modManager.createModifierItem(Material.getMaterial(config.getString(key + ".modifier_item")),
                        ChatWriter.getColor(config.getString(key + ".Color")) + config.getString(key + ".name_modifier"),
                        ChatWriter.addColors(config.getString(key + ".description_modifier")), this));

        this.compatibleWithProtecting = config.getBoolean(key + ".CompatibleWithProtecting");
        this.compatibleWithAntiFire = config.getBoolean(key + ".CompatibleWithAntiFire");
        this.compatibleWithAntiBlast = config.getBoolean(key + ".CompatibleWithAntiBlast");
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (Modifier.checkAndAdd(p, tool, this, "antiarrowplating", isCommand) == null) {
            return null;
        }

        ItemMeta meta = tool.getItemMeta();

        if (meta != null) {
            if (ToolType.HELMET.getMaterials().contains(tool.getType()) || ToolType.CHESTPLATE.getMaterials().contains(tool.getType())
                    || ToolType.LEGGINGS.getMaterials().contains(tool.getType()) || ToolType.BOOTS.getMaterials().contains(tool.getType())) {

                if (!this.compatibleWithProtecting) {
                    if (modManager.hasMod(tool, Protecting.instance()) || meta.hasEnchant(Enchantment.PROTECTION_ENVIRONMENTAL)) {
                        pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
                        return null;
                    }
                }

                if (!this.compatibleWithAntiFire) {
                    if (modManager.hasMod(tool, AntiFirePlating.instance()) || meta.hasEnchant(Enchantment.PROTECTION_FIRE)) {
                        pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
                        return null;
                    }
                }

                if (!this.compatibleWithAntiBlast) {
                    if (modManager.hasMod(tool, AntiBlastPlating.instance()) || meta.hasEnchant(Enchantment.PROTECTION_EXPLOSIONS)) {
                        pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
                        return null;
                    }
                }

                meta.addEnchant(Enchantment.PROTECTION_PROJECTILE, modManager.getModLevel(tool, this), true);
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
            meta.removeEnchant(Enchantment.PROTECTION_PROJECTILE);

            tool.setItemMeta(meta);
        }
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Anti-Arrow-Plating", "Modifier_AntiArrowPlating");
    }

    private static FileConfiguration getConfig() {
        return ConfigurationManager.getConfig(Modifiers_Config.Anti_Arrow_Plating);
    }

    @Override
    public boolean isAllowed() {
        return getConfig().getBoolean("Anti-Arrow-Plating.allowed");
    }
}

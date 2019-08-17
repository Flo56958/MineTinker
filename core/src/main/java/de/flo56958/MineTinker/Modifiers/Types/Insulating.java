package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ModifierFailCause;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.ModifierFailEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Modifier;
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

public class Insulating extends Modifier {

    private boolean compatibleWithProtecting;
    private boolean compatibleWithAntiArrow;
    private boolean compatibleWithAntiBlast;

    private static Insulating instance;

    public static Insulating instance() {
        synchronized (Insulating.class) {
            if (instance == null) {
                instance = new Insulating();
            }
        }

        return instance;
    }

    @Override
    public String getKey() {
        return "Insulating";
    }

    @Override
    public List<ToolType> getAllowedTools() {
        return Arrays.asList(ToolType.BOOTS, ToolType.LEGGINGS, ToolType.CHESTPLATE, ToolType.HELMET);
    }

    private Insulating() {
        super(Main.getPlugin());
    }

    @Override
    public List<Enchantment> getAppliedEnchantments() {
        return Collections.singletonList(Enchantment.PROTECTION_FIRE);
    }

    @Override
    public void reload() {
        FileConfiguration config = getConfig();
        config.options().copyDefaults(true);

        config.addDefault("Allowed", true);
        config.addDefault("Name", "Insulating");
        config.addDefault("ModifierItemName", "Heat Resistant Alloy");
        config.addDefault("Description", "Armor mitigates heat damage!");
        config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Anti-Fire-Plating-Modifier");
        config.addDefault("Color", "%WHITE%");
        config.addDefault("MaxLevel", 5);
        config.addDefault("OverrideLanguagesystem", false);

        config.addDefault("CompatibleWithProtecting", false);
        config.addDefault("CompatibleWithAntiArrow", false);
        config.addDefault("CompatibleWithAntiBlast", false);

        config.addDefault("Recipe.Enabled", true);
        config.addDefault("Recipe.Top", "IMI");
        config.addDefault("Recipe.Middle", "MDM");
        config.addDefault("Recipe.Bottom", "IMI");

        Map<String, String> recipeMaterials = new HashMap<>();
        recipeMaterials.put("I", "IRON_BLOCK");
        recipeMaterials.put("M", "MAGMA_BLOCK");
        recipeMaterials.put("D", "DIAMOND");

        config.addDefault("Recipe.Materials", recipeMaterials);

        ConfigurationManager.saveConfig(config);
        ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

        init(Material.MAGMA_CREAM, true);

        this.compatibleWithProtecting = config.getBoolean("CompatibleWithProtecting", false);
        this.compatibleWithAntiArrow  = config.getBoolean("CompatibleWithAntiArrow", false);
        this.compatibleWithAntiBlast  = config.getBoolean("CompatibleWithAntiBlast", false);
    }

    @Override
    public boolean applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (!Modifier.checkAndAdd(p, tool, this, "insulating", isCommand)) {
            return false;
        }

        ItemMeta meta = tool.getItemMeta();

        if (meta != null) {
            if (ToolType.HELMET.contains(tool.getType()) || ToolType.CHESTPLATE.contains(tool.getType())
                    || ToolType.LEGGINGS.contains(tool.getType()) || ToolType.BOOTS.contains(tool.getType())) {

                if (!this.compatibleWithProtecting) {
                    if (modManager.hasMod(tool, Protecting.instance()) || meta.hasEnchant(Enchantment.PROTECTION_ENVIRONMENTAL)) {
                        pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
                        return false;
                    }
                }

                if (!this.compatibleWithAntiArrow) {
                    if (modManager.hasMod(tool, AntiArrowPlating.instance()) || meta.hasEnchant(Enchantment.PROTECTION_PROJECTILE)) {
                        pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
                        return false;
                    }
                }

                if (!this.compatibleWithAntiBlast) {
                    if (modManager.hasMod(tool, AntiBlastPlating.instance()) || meta.hasEnchant(Enchantment.PROTECTION_EXPLOSIONS)) {
                        pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
                        return false;
                    }
                }

                meta.addEnchant(Enchantment.PROTECTION_FIRE, modManager.getModLevel(tool, this), true);
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
}

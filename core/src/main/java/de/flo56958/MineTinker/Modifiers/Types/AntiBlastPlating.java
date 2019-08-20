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

public class AntiBlastPlating extends Modifier {

    private boolean compatibleWithProtecting;
    private boolean compatibleWithInsulating;
    private boolean compatibleWithAntiArrow;

    private static AntiBlastPlating instance;

    public static AntiBlastPlating instance() {
        synchronized (AntiBlastPlating.class) {
            if (instance == null) {
                instance = new AntiBlastPlating();
            }
        }

        return instance;
    }

    @Override
    public String getKey() {
        return "Anti-Blast-Plating";
    }

    @Override
    public List<ToolType> getAllowedTools() {
        return Arrays.asList(ToolType.BOOTS, ToolType.LEGGINGS, ToolType.CHESTPLATE, ToolType.HELMET);
    }

    private AntiBlastPlating() {
        super(Main.getPlugin());
    }

    @Override
    public List<Enchantment> getAppliedEnchantments() {
        return Collections.singletonList(Enchantment.PROTECTION_EXPLOSIONS);
    }

    @Override
    public void reload() {
        FileConfiguration config = getConfig();
        config.options().copyDefaults(true);

        config.addDefault("Allowed", true);
        config.addDefault("Name", "Anti-Blast-Plating");
        config.addDefault("ModifierItemName", "Blast Resistant Metal");
        config.addDefault("Description", "Armor mitigates explosion damage!");
        config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Anti-Blast-Plating-Modifier");
        config.addDefault("MaxLevel", 5);
        config.addDefault("Color", "%WHITE%");
        config.addDefault("OverrideLanguagesystem", false);

        config.addDefault("CompatibleWithProtecting", false);
        config.addDefault("CompatibleWithInsulating", false);
        config.addDefault("CompatibleWithAntiArrow", false);

        config.addDefault("EnchantCost", 10);
        config.addDefault("Enchantable", false);

        config.addDefault("Recipe.Enabled", true);
        config.addDefault("Recipe.Top", "IMI");
        config.addDefault("Recipe.Middle", "MDM");
        config.addDefault("Recipe.Bottom", "IMI");

        Map<String, String> recipeMaterials = new HashMap<>();
        recipeMaterials.put("I", Material.IRON_BLOCK.name());
        recipeMaterials.put("M", Material.TNT.name());
        recipeMaterials.put("D", Material.DIAMOND.name());

        config.addDefault("Recipe.Materials", recipeMaterials);

        ConfigurationManager.saveConfig(config);
        ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

        init(Material.IRON_BLOCK, true);

        this.compatibleWithProtecting = config.getBoolean("CompatibleWithProtecting", false);
        this.compatibleWithInsulating = config.getBoolean("CompatibleWithInsulating", false);
        this.compatibleWithAntiArrow = config.getBoolean("CompatibleWithAntiArrow", false);
    }

    @Override
    public boolean applyMod(Player p, ItemStack tool, boolean isCommand) {
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

                if (!this.compatibleWithInsulating) {
                    if (modManager.hasMod(tool, Insulating.instance()) || meta.hasEnchant(Enchantment.PROTECTION_FIRE)) {
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

                meta.addEnchant(Enchantment.PROTECTION_EXPLOSIONS, modManager.getModLevel(tool, this), true);
            }

            tool.setItemMeta(meta);
        }

        return true;
    }
}

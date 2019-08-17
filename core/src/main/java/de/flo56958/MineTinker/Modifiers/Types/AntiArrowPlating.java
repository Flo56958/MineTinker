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

public class AntiArrowPlating extends Modifier {

    private boolean compatibleWithProtecting;
    private boolean compatibleWithAntiFire;
    private boolean compatibleWithAntiBlast;

    private static AntiArrowPlating instance;

    public static AntiArrowPlating instance() {
        synchronized (AntiArrowPlating.class) {
            if (instance == null) {
                instance = new AntiArrowPlating();
            }
        }

        return instance;
    }

    @Override
    public String getKey() {
        return "Anti-Arrow-Plating";
    }

    @Override
    public List<ToolType> getAllowedTools() {
        return Arrays.asList(ToolType.BOOTS, ToolType.LEGGINGS, ToolType.CHESTPLATE, ToolType.HELMET);
    }

    private AntiArrowPlating() {
        super(Main.getPlugin());
    }

    @Override
    public List<Enchantment> getAppliedEnchantments() {
        return Collections.singletonList(Enchantment.PROTECTION_ENVIRONMENTAL);
    }

    @Override
    public void reload() {
        FileConfiguration config = getConfig();
        config.options().copyDefaults(true);

        config.addDefault("Allowed", true);
        config.addDefault("Name", "Anti-Arrow-Plating");
        config.addDefault("ModifierItemName", "Pierce Resistant Metal");
        config.addDefault("Description", "Armor mitigates projectile damage!");
        config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Anti-Arrow-Plating-Modifier");
        config.addDefault("Color", "%WHITE%");
        config.addDefault("MaxLevel", 5);
        config.addDefault("OverrideLanguagesystem", false);

        config.addDefault("CompatibleWithProtecting", false);
        config.addDefault("CompatibleWithAntiFire", false);
        config.addDefault("CompatibleWithAntiBlast", false);

        config.addDefault("Recipe.Enabled", true);
        config.addDefault("Recipe.Top", "IAI");
        config.addDefault("Recipe.Middle", "ADA");
        config.addDefault("Recipe.Bottom", "IAI");

        Map<String, String> recipeMaterials = new HashMap<>();
        recipeMaterials.put("I", "IRON_BLOCK");
        recipeMaterials.put("A", "ARROW");
        recipeMaterials.put("D", "DIAMOND");

        config.addDefault("Recipe.Materials", recipeMaterials);

        // Save Config
        ConfigurationManager.saveConfig(config);
        ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

        // Initialize modifier
        init(Material.IRON_BLOCK, true);

        this.compatibleWithProtecting = config.getBoolean("CompatibleWithProtecting", false);
        this.compatibleWithAntiFire = config.getBoolean("CompatibleWithAntiFire", false);
        this.compatibleWithAntiBlast = config.getBoolean("CompatibleWithAntiBlast", false);
    }

    @Override
    public boolean applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (!Modifier.checkAndAdd(p, tool, this, "antiarrowplating", isCommand)) {
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

                if (!this.compatibleWithAntiFire) {
                    if (modManager.hasMod(tool, Insulating.instance()) || meta.hasEnchant(Enchantment.PROTECTION_FIRE)) {
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

                meta.addEnchant(Enchantment.PROTECTION_PROJECTILE, modManager.getModLevel(tool, this), true);
            }

            tool.setItemMeta(meta);
        }

        return true;
    }
}

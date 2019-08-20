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

public class SpidersBane extends Modifier {

    private boolean compatibleWithSmite;
    private boolean compatibleWithSharpness;

    private static SpidersBane instance;

    public static SpidersBane instance() {
        synchronized (SpidersBane.class) {
            if (instance == null) {
                instance = new SpidersBane();
            }
        }

        return instance;
    }

    @Override
    public String getKey() {
        return "Spiders-Bane";
    }

    @Override
    public List<ToolType> getAllowedTools() {
        return Arrays.asList(ToolType.AXE, ToolType.SWORD);
    }

    private SpidersBane() {
        super(Main.getPlugin());
    }

    @Override
    public List<Enchantment> getAppliedEnchantments() {
        return Collections.singletonList(Enchantment.DAMAGE_ARTHROPODS);
    }

    @Override
    public void reload() {
        FileConfiguration config = getConfig();
        config.options().copyDefaults(true);

        config.addDefault("Allowed", true);
        config.addDefault("Name", "Spider's-Bane");
        config.addDefault("ModifierItemName", "Cleansed Spider Eye");
        config.addDefault("Description", "Weapon does additional damage to Spiders!");
        config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Spider's-Bane-Modifier");
        config.addDefault("Color", "%RED%");
        config.addDefault("MaxLevel", 5);
        config.addDefault("OverrideLanguagesystem", false);

        config.addDefault("CompatibleWithSmite", false);
        config.addDefault("CompatibleWithSharpness", false);

        config.addDefault("EnchantCost", 10);
        config.addDefault("Enchantable", false);

        config.addDefault("Recipe.Enabled", true);
        config.addDefault("Recipe.Top", "ESE");
        config.addDefault("Recipe.Middle", "SFS");
        config.addDefault("Recipe.Bottom", "ESE");

        Map<String, String> recipeMaterials = new HashMap<>();
        recipeMaterials.put("E", Material.SPIDER_EYE.name());
        recipeMaterials.put("S", Material.STRING.name());
        recipeMaterials.put("F", Material.FERMENTED_SPIDER_EYE.name());

        config.addDefault("Recipe.Materials", recipeMaterials);

        ConfigurationManager.saveConfig(config);
        ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

        init(Material.FERMENTED_SPIDER_EYE, true);

        this.compatibleWithSmite = config.getBoolean("CompatibleWithSmite", false);
        this.compatibleWithSharpness = config.getBoolean("CompatibleWithSharpness", false);
    }

    @Override
    public boolean applyMod(Player p, ItemStack tool, boolean isCommand) {
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

            tool.setItemMeta(meta);
        } else return false;

        return true;
    }
}

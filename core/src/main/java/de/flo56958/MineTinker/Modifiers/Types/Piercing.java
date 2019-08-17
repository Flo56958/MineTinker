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

public class Piercing extends Modifier {

    private boolean compatibleWithMultishot;

    private static Piercing instance;

    public static Piercing instance() {
        synchronized (Piercing.class) {
            if (instance == null) {
                instance = new Piercing();
            }
        }

        return instance;
    }

    @Override
    public String getKey() {
        return "Piercing";
    }

    @Override
    public List<ToolType> getAllowedTools() {
        return Collections.singletonList(ToolType.CROSSBOW);
    }

    private Piercing() {
        super(Main.getPlugin());
    }

    @Override
    public List<Enchantment> getAppliedEnchantments() {
        return Collections.singletonList(Enchantment.PIERCING);
    }

    @Override
    public void reload() {
        FileConfiguration config = getConfig();
        config.options().copyDefaults(true);

        config.addDefault("Allowed", true);
        config.addDefault("Name", "Piercing");
        config.addDefault("ModifierItemName", "Bodkin Point");
        config.addDefault("Description", "Passes through enemies!");
        config.addDefault("DescriptionModifierItem", "%GRAY%Modifier-Item for the Piercing-Modifier");
        config.addDefault("Color", "%GRAY%");
        config.addDefault("MaxLevel", 4);
        config.addDefault("OverrideLanguagesystem", false);

        config.addDefault("Recipe.Enabled", true);
        config.addDefault("Recipe.Top", "FIF");
        config.addDefault("Recipe.Middle", "OAO");
        config.addDefault("Recipe.Bottom", "FIF");

        Map<String, String> recipeMaterials = new HashMap<>();
        recipeMaterials.put("F", "FLINT");
        recipeMaterials.put("I", "IRON_INGOT");
        recipeMaterials.put("O", "OAK_PLANKS");
        recipeMaterials.put("A", "ARROW");

        config.addDefault("Recipe.Materials", recipeMaterials);

        ConfigurationManager.saveConfig(config);
        ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

        init(Material.ARROW, true);

        this.compatibleWithMultishot = ConfigurationManager.getConfig(Melting.instance()).getBoolean("CompatibleWithPiercing", false);
    }

    @Override
    public boolean applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (!Modifier.checkAndAdd(p, tool, this, "piercing", isCommand)) {
            return false;
        }

        ItemMeta meta = tool.getItemMeta();

        if (meta != null) {
            if (ToolType.CROSSBOW.contains(tool.getType())) {
                if (!this.compatibleWithMultishot) {
                    if (modManager.hasMod(tool, MultiShot.instance()) || meta.hasEnchant(Enchantment.MULTISHOT)) {
                        pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
                        return false;
                    }
                }

                meta.addEnchant(Enchantment.PIERCING, modManager.getModLevel(tool, this), true);
            }

            tool.setItemMeta(meta);
        }

        return true;
    }
}

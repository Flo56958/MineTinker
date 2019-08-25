package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiShot extends Modifier {
    private static MultiShot instance;

    public static MultiShot instance() {
        synchronized (MultiShot.class) {
            if (instance == null) {
                instance = new MultiShot();
            }
        }

        return instance;
    }

    @Override
    public String getKey() {
        return "Multishot";
    }

    @Override
    public List<ToolType> getAllowedTools() {
        return Collections.singletonList(ToolType.CROSSBOW);
    }

    private MultiShot() {
        super(Main.getPlugin());
    }

    @Override
    public List<Enchantment> getAppliedEnchantments() {
        return Collections.singletonList(Enchantment.MULTISHOT);
    }

    @Override
    public void reload() {
        FileConfiguration config = getConfig();
        config.options().copyDefaults(true);

        config.addDefault("Allowed", true);
        config.addDefault("Name", "Multishot");
        config.addDefault("ModifierItemName", "Multi-Arrow");
        config.addDefault("Description", "Shoot more Arrows per shot!");
        config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Multishot-Modifier");
        config.addDefault("Color", "%YELLOW%");
        config.addDefault("MaxLevel", 1);

        config.addDefault("EnchantCost", 10);
        config.addDefault("Enchantable", false);

        config.addDefault("Recipe.Enabled", true);
        config.addDefault("Recipe.Top", "QQQ");
        config.addDefault("Recipe.Middle", "AAA");
        config.addDefault("Recipe.Bottom", "QQQ");
        config.addDefault("OverrideLanguagesystem", false);

        Map<String, String> recipeMaterials = new HashMap<>();
        recipeMaterials.put("Q", Material.QUARTZ_BLOCK.name());
        recipeMaterials.put("A", Material.ARROW.name());

        config.addDefault("Recipe.Materials", recipeMaterials);

        ConfigurationManager.saveConfig(config);
        ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

        init(Material.ARROW, true);
    }

    @Override
    public boolean applyMod(Player p, ItemStack tool, boolean isCommand) {
        ItemMeta meta = tool.getItemMeta();

        if (meta != null) {
            if (ToolType.CROSSBOW.contains(tool.getType())) {
                meta.addEnchant(Enchantment.MULTISHOT, modManager.getModLevel(tool, this), true);
            }

            tool.setItemMeta(meta);
        }

        return false;
    }
}

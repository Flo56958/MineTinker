package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
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

public class Channeling extends Modifier {

    private static Channeling instance;

    public static Channeling instance() {
        synchronized (Channeling.class) {
            if (instance == null) {
                instance = new Channeling();
            }
        }

        return instance;
    }

    @Override
    public String getKey() {
        return "Channeling";
    }

    @Override
    public List<ToolType> getAllowedTools() {
        return Collections.singletonList(ToolType.TRIDENT);
    }

    private Channeling() {
        super(Main.getPlugin());
    }

    @Override
    public List<Enchantment> getAppliedEnchantments() {
        return Collections.singletonList(Enchantment.CHANNELING);
    }

    @Override
    public void reload() {
        FileConfiguration config = getConfig();
        config.options().copyDefaults(true);

        config.addDefault("Allowed", true);
        config.addDefault("Name", "Channeling");
        config.addDefault("ModifierItemName", "Lightning Infused Shard");
        config.addDefault("Description", "Summons lightning when weapon is thrown at mobs!");
        config.addDefault("DescriptionModifierItem", "%GRAY%Modifier-Item for the Channeling-Modifier");
        config.addDefault("Color", "%GRAY%");
        config.addDefault("MaxLevel", 1);
        config.addDefault("OverrideLanguagesystem", false);

        config.addDefault("EnchantCost", 10);
        config.addDefault("Enchantable", false);

        config.addDefault("Recipe.Enabled", true);
        config.addDefault("Recipe.Top", "SPS");
        config.addDefault("Recipe.Middle", "PCP");
        config.addDefault("Recipe.Bottom", "SPS");

        Map<String, String> recipeMaterials = new HashMap<>();
        recipeMaterials.put("S", "SEA_LANTERN");
        recipeMaterials.put("P", "PRISMARINE_SHARD");
        recipeMaterials.put("C", "CREEPER_HEAD");

        config.addDefault("Recipe.Materials", recipeMaterials);

        ConfigurationManager.saveConfig(config);
        ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

        init(Material.PRISMARINE_SHARD, true);
    }

    @Override
    public boolean applyMod(Player p, ItemStack tool, boolean isCommand) {
        ItemMeta meta = tool.getItemMeta();

        if (meta != null) {
            if (ToolType.TRIDENT.contains(tool.getType())) {
                meta.addEnchant(Enchantment.CHANNELING, modManager.getModLevel(tool, this), true);
            }

            tool.setItemMeta(meta);
        }

        return true;
    }
}

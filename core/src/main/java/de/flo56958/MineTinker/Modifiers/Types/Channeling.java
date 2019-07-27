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
            if (instance == null) instance = new Channeling();
        }
        return instance;
    }

    private Channeling() {
        super("Channeling", "Channeling.yml",
                new ArrayList<>(Collections.singletonList(ToolType.TRIDENT)),
                Main.getPlugin());
    }

    @Override
    public List<Enchantment> getAppliedEnchantments() {
        List<Enchantment> enchantments = new ArrayList<>();
        enchantments.add(Enchantment.CHANNELING);

        return enchantments;
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

        config.addDefault("Recipe.Enabled", true);
        config.addDefault("Recipe.Top", "SPS");
        config.addDefault("Recipe.Middle", "PCP");
        config.addDefault("Recipe.Bottom", "SPS");

        Map<String, String> recipeMaterials = new HashMap<>();
        recipeMaterials.put("S", "SEA_LANTERN");
        recipeMaterials.put("P", "PRISMARINE_SHARDS");
        recipeMaterials.put("C", "CREEPER_HEAD");

        config.addDefault("Recipe.Materials", recipeMaterials);

        ConfigurationManager.saveConfig(config);
        ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

        init(Material.PRISMARINE_SHARD);
    }

    @Override
    public boolean applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (!Modifier.checkAndAdd(p, tool, this, "channeling", isCommand)) return false;

        ItemMeta meta = tool.getItemMeta();

        if (meta != null) {
            if (ToolType.TRIDENT.contains(tool.getType())) {
                meta.addEnchant(Enchantment.CHANNELING, modManager.getModLevel(tool, this), true);
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
            meta.removeEnchant(Enchantment.CHANNELING);
            tool.setItemMeta(meta);
        }
    }
}

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
import java.util.*;

public class Luck extends Modifier {

    private static EnumMap<ToolType, List<Enchantment>> applicableEnchants = new EnumMap<>(ToolType.class);

    private static Luck instance;

    static {
        applicableEnchants.put(ToolType.AXE, Arrays.asList(Enchantment.LOOT_BONUS_BLOCKS, Enchantment.LOOT_BONUS_MOBS));
        applicableEnchants.put(ToolType.BOW, Collections.singletonList(Enchantment.LOOT_BONUS_MOBS));
        applicableEnchants.put(ToolType.HOE, Collections.singletonList(Enchantment.LOOT_BONUS_BLOCKS));
        applicableEnchants.put(ToolType.PICKAXE, Collections.singletonList(Enchantment.LOOT_BONUS_BLOCKS));
        applicableEnchants.put(ToolType.SHOVEL, Collections.singletonList(Enchantment.LOOT_BONUS_BLOCKS));
        applicableEnchants.put(ToolType.SWORD, Collections.singletonList(Enchantment.LOOT_BONUS_MOBS));
        applicableEnchants.put(ToolType.SHEARS, Collections.singletonList(Enchantment.LOOT_BONUS_BLOCKS));
        applicableEnchants.put(ToolType.FISHINGROD, Collections.singletonList(Enchantment.LUCK));
    }

    public static Luck instance() {
        synchronized (Luck.class) {
            if (instance == null) {
                instance = new Luck();
            }
        }

        return instance;
    }

    @Override
    public String getKey() {
        return "Luck";
    }

    @Override
    public List<ToolType> getAllowedTools() {
        return Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.CROSSBOW, ToolType.HOE, ToolType.PICKAXE, ToolType.SHEARS,
                ToolType.FISHINGROD, ToolType.SHOVEL, ToolType.SWORD, ToolType.TRIDENT);
    }

    private Luck() {
        super(Main.getPlugin());
    }

    @Override
    public List<Enchantment> getAppliedEnchantments() {
        return Arrays.asList(Enchantment.LOOT_BONUS_BLOCKS, Enchantment.LOOT_BONUS_MOBS, Enchantment.LUCK);
    }

    @Override
    public void reload() {
    	FileConfiguration config = getConfig();
    	config.options().copyDefaults(true);

    	config.addDefault("Allowed", true);
    	config.addDefault("Name", "Luck");
    	config.addDefault("ModifierItemName", "Compressed Lapis-Block");
        config.addDefault("Description", "Get more loot from mobs and blocks!");
        config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Luck-Modifier");
        config.addDefault("Color", "%BLUE%");
        config.addDefault("MaxLevel", 3);
        config.addDefault("OverrideLanguagesystem", false);

        config.addDefault("EnchantCost", 10);
        config.addDefault("Enchantable", true);

    	config.addDefault("Recipe.Enabled", true);
    	config.addDefault("Recipe.Top", "LLL");
    	config.addDefault("Recipe.Middle", "LLL");
    	config.addDefault("Recipe.Bottom", "LLL");

        Map<String, String> recipeMaterials = new HashMap<>();
        recipeMaterials.put("L", Material.LAPIS_BLOCK.name());

        config.addDefault("Recipe.Materials", recipeMaterials);

        ConfigurationManager.saveConfig(config);
        ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

        init(Material.LAPIS_BLOCK, true);
    }

    @Override
    public boolean applyMod(Player p, ItemStack tool, boolean isCommand) {
        ItemMeta meta = tool.getItemMeta();

        if (meta != null) {
            for (Enchantment enchantment : applicableEnchants.get(ToolType.get(tool.getType()))) {
                meta.addEnchant(enchantment, modManager.getModLevel(tool, this), true);
            }

            tool.setItemMeta(meta);
        }

        return true;
    }
}

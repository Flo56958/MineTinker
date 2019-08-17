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

public class Sharpness extends Modifier {

    private boolean compatibleWithSmite;
    private boolean compatibleWithArthropods;

    private static Sharpness instance;

    public static Sharpness instance() {
        synchronized (Sharpness.class) {
            if (instance == null) instance = new Sharpness();
        }
        return instance;
    }

    private Sharpness() {
        super("Sharpness", "Sharpness.yml",
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.CROSSBOW, ToolType.SWORD, ToolType.TRIDENT)),
                Main.getPlugin());
    }

    @Override
    public List<Enchantment> getAppliedEnchantments() {
        List<Enchantment> enchantments = new ArrayList<>();
        enchantments.add(Enchantment.DAMAGE_ALL);
        enchantments.add(Enchantment.ARROW_DAMAGE);
        enchantments.add(Enchantment.IMPALING);

        return enchantments;
    }

    @Override
    public void reload() {
    	FileConfiguration config = getConfig();
    	config.options().copyDefaults(true);
    	
    	config.addDefault("Allowed", true);
    	config.addDefault("Name", "Sharpness");
    	config.addDefault("ModifierItemName", "Compressed Quartzblock");
        config.addDefault("Description", "Weapon does additional damage to everyone!");
        config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Sharpness-Modifier");
        config.addDefault("Color", "%WHITE%");
        config.addDefault("MaxLevel", 5);
        config.addDefault("OverrideLanguagesystem", false);

        config.addDefault("CompatibleWithSmite", false);
        config.addDefault("CompatibleWithArthropods", false);

        config.addDefault("Recipe.Enabled", true);
    	config.addDefault("Recipe.Top", "QQQ");
    	config.addDefault("Recipe.Middle", "QQQ");
    	config.addDefault("Recipe.Bottom", "QQQ");

        Map<String, String> recipeMaterials = new HashMap<>();
        recipeMaterials.put("Q", "QUARTZ_BLOCK");

        config.addDefault("Recipe.Materials", recipeMaterials);

    	ConfigurationManager.saveConfig(config);
        ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());
    	
        init(Material.QUARTZ_BLOCK, true);

        this.compatibleWithSmite = config.getBoolean("CompatibleWithSmite", false);
        this.compatibleWithArthropods = config.getBoolean("CompatibleWithArthropods", false);
    }

    @Override
    public boolean applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (!Modifier.checkAndAdd(p, tool, this, "sharpness", isCommand)) return false;

        ItemMeta meta = tool.getItemMeta();

        if (meta != null) {
            if (ToolType.AXE.contains(tool.getType()) || ToolType.SWORD.contains(tool.getType())) {
                if (!this.compatibleWithSmite) {
                    if (modManager.hasMod(tool, Smite.instance()) || meta.hasEnchant(Enchantment.DAMAGE_UNDEAD)) {
                        pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
                        return false;
                    }
                }

                if (!this.compatibleWithArthropods) {
                    if (modManager.hasMod(tool, SpidersBane.instance()) || meta.hasEnchant(Enchantment.DAMAGE_ARTHROPODS)) {
                        pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
                        return false;
                    }
                }

                meta.addEnchant(Enchantment.DAMAGE_ALL, modManager.getModLevel(tool, this), true);
            } else if (ToolType.BOW.contains(tool.getType()) || ToolType.CROSSBOW.contains(tool.getType())) {
                meta.addEnchant(Enchantment.ARROW_DAMAGE, modManager.getModLevel(tool, this), true);
            } else if (ToolType.TRIDENT.contains(tool.getType())) {
                meta.addEnchant(Enchantment.DAMAGE_ALL, modManager.getModLevel(tool, this), true);
                meta.addEnchant(Enchantment.IMPALING, modManager.getModLevel(tool, this), true);
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
            meta.removeEnchant(Enchantment.DAMAGE_ALL);
            meta.removeEnchant(Enchantment.ARROW_DAMAGE);
            meta.removeEnchant(Enchantment.IMPALING);

            tool.setItemMeta(meta);
        }
    }
}

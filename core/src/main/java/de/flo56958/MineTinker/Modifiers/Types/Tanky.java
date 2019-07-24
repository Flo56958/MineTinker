package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.*;

public class Tanky extends Modifier {

    private static Tanky instance;

    private int healthPerLevel;

    public static Tanky instance() {
        synchronized (Tanky.class) {
            if (instance == null) instance = new Tanky();
        }
        return instance;
    }

    private Tanky() {
        super("Tanky", "Tanky.yml",
                new ArrayList<>(Arrays.asList(ToolType.CHESTPLATE, ToolType.LEGGINGS)),
                Main.getPlugin());
    }

    @Override
    public boolean applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (!Modifier.checkAndAdd(p, tool, this, "tanky", isCommand)) return false;

        ItemMeta meta = tool.getItemMeta();
        if (meta == null) return false;

        if (meta.getAttributeModifiers(Attribute.GENERIC_ARMOR) == null || meta.getAttributeModifiers(Attribute.GENERIC_ARMOR).isEmpty()) modManager.addArmorAttributes(tool);

        Collection<AttributeModifier> healthModifiers = meta.getAttributeModifiers(Attribute.GENERIC_MAX_HEALTH);
        double healthOnItem = 0.0D;
        if (!(healthModifiers == null || healthModifiers.isEmpty())) {
            HashSet<String> names = new HashSet<>();
            for(AttributeModifier am : healthModifiers) {
                if(names.add(am.getName())) healthOnItem += am.getAmount();
            }
        }
        meta.removeAttributeModifier(Attribute.GENERIC_MAX_HEALTH);
        meta.addAttributeModifier(Attribute.GENERIC_MAX_HEALTH, new AttributeModifier(UUID.randomUUID(), "generic.maxHealth", healthOnItem + this.healthPerLevel, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST));
        meta.addAttributeModifier(Attribute.GENERIC_MAX_HEALTH, new AttributeModifier(UUID.randomUUID(), "generic.maxHealth", healthOnItem + this.healthPerLevel, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS));

        if (Main.getPlugin().getConfig().getBoolean("HideAttributes")) {
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        } else {
            meta.removeItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        }
        tool.setItemMeta(meta);

        return true;
    }

    @Override
    public void removeMod(ItemStack tool) {
        ItemMeta meta = tool.getItemMeta();
        if (meta == null) return;

        meta.removeAttributeModifier(Attribute.GENERIC_MAX_HEALTH);
        tool.setItemMeta(meta);
    }

    @Override
    public void reload() {
        FileConfiguration config = getConfig();
        config.options().copyDefaults(true);

        String key = "Tanky";
        config.addDefault(key + ".allowed", true);
        config.addDefault(key + ".name", key);
        config.addDefault(key + ".name_modifier", "Bloodinfused Obsidian");
        config.addDefault(key + ".modifier_item", "OBSIDIAN"); //Needs to be a viable Material-Type
        config.addDefault(key + ".description", "Makes you extra tanky!");
        config.addDefault(key + ".description_modifier", "%WHITE%Modifier-Item for the Tanky-Modifier");
        config.addDefault(key + ".Color", "%DARK_GRAY%");
        config.addDefault(key + ".MaxLevel", 5);
        config.addDefault(key + ".HealthPerLevel", 3);

        config.addDefault(key + ".Recipe.Enabled", true);
        config.addDefault(key + ".Recipe.Top", "RBR");
        config.addDefault(key + ".Recipe.Middle", "BOB");
        config.addDefault(key + ".Recipe.Bottom", "RBR");

        Map<String, String> recipeMaterials = new HashMap<>();
        recipeMaterials.put("B", "BONE");
        recipeMaterials.put("O", "OBSIDIAN");
        recipeMaterials.put("R", "ROTTEN_FLESH");

        config.addDefault(key + ".Recipe.Materials", recipeMaterials);

        ConfigurationManager.saveConfig(config);
        ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

        this.healthPerLevel = config.getInt(key + ".HealthPerLevel");

        init(config.getString(key + ".name"),
                "[" + config.getString(key + ".name_modifier") + "] \u200B" + config.getString(key + ".description"),
                ChatWriter.getColor(config.getString(key + ".Color")),
                config.getInt(key + ".MaxLevel"),
                modManager.createModifierItem(Material.getMaterial(config.getString(key + ".modifier_item")), ChatWriter.getColor(config.getString(key + ".Color")) + config.getString(key + ".name_modifier"), ChatWriter.addColors(config.getString(key + ".description_modifier")), this));
    }

    @Override
    public List<Attribute> getAppliedAttributes() {
        List<Attribute> attributes = new ArrayList<>();
        attributes.add(Attribute.GENERIC_MAX_HEALTH);
        return attributes;
    }

    @Override
    public boolean isAllowed() {
        return getConfig().getBoolean("Tanky.allowed");
    }

    @Override
    public List<Enchantment> getAppliedEnchantments() {
        return new ArrayList<>();
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Tanky", "Modifier_Tanky");
    }
}

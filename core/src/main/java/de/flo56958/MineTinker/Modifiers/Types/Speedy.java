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

import java.util.*;

public class Speedy extends Modifier {

    private static Speedy instance;

    private double speedPerLevel;

    public static Speedy instance() {
        synchronized (Speedy.class) {
            if (instance == null) instance = new Speedy();
        }
        return instance;
    }

    private Speedy() {
        super("Speedy", "Speedy.yml",
                new ArrayList<>(Arrays.asList(ToolType.BOOTS, ToolType.LEGGINGS)),
                Main.getPlugin());
    }

    @Override
    public boolean applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (!Modifier.checkAndAdd(p, tool, this, "speedy", isCommand)) return false;

        ItemMeta meta = tool.getItemMeta();
        if (meta == null) return false;

        meta.removeAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED);
        meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, new AttributeModifier(UUID.randomUUID(), "generic.movementSpeed", this.speedPerLevel * modManager.getModLevel(tool, this), AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS));
        meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, new AttributeModifier(UUID.randomUUID(), "generic.movementSpeed", this.speedPerLevel * modManager.getModLevel(tool, this), AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET));

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

        meta.removeAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED);
        tool.setItemMeta(meta);
    }

    @Override
    public void reload() {
        FileConfiguration config = getConfig();
        config.options().copyDefaults(true);

        String key = "Speedy";
        config.addDefault(key + ".allowed", true);
        config.addDefault(key + ".name", key);
        config.addDefault(key + ".name_modifier", "Enhanced Rabbithide");
        config.addDefault(key + ".modifier_item", "RABBIT_HIDE"); //Needs to be a viable Material-Type
        config.addDefault(key + ".description", "Gotta go fast!");
        config.addDefault(key + ".description_modifier", "%WHITE%Modifier-Item for the Speedy-Modifier");
        config.addDefault(key + ".Color", "%BLUE%");
        config.addDefault(key + ".MaxLevel", 3);
        config.addDefault(key + ".SpeedPerLevel", 0.05);

        config.addDefault(key + ".Recipe.Enabled", true);
        config.addDefault(key + ".Recipe.Top", "R R");
        config.addDefault(key + ".Recipe.Middle", " H ");
        config.addDefault(key + ".Recipe.Bottom", "R R");

        Map<String, String> recipeMaterials = new HashMap<>();
        recipeMaterials.put("H", "RABBIT_HIDE");
        recipeMaterials.put("R", "RABBIT_FOOT");

        config.addDefault(key + ".Recipe.Materials", recipeMaterials);

        ConfigurationManager.saveConfig(config);

        this.speedPerLevel = config.getDouble(key + ".SpeedPerLevel");

        init(config.getString(key + ".name"),
                "[" + config.getString(key + ".name_modifier") + "] \u200B" + config.getString(key + ".description"),
                ChatWriter.getColor(config.getString(key + ".Color")),
                config.getInt(key + ".MaxLevel"),
                modManager.createModifierItem(Material.getMaterial(config.getString(key + ".modifier_item")), ChatWriter.getColor(config.getString(key + ".Color")) + config.getString(key + ".name_modifier"), ChatWriter.addColors(config.getString(key + ".description_modifier")), this));
    }

    @Override
    public boolean isAllowed() {
        return getConfig().getBoolean("Speedy.allowed");
    }

    @Override
    public List<Enchantment> getAppliedEnchantments() {
        return new ArrayList<>();
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Speedy", "Modifier_Speedy");
    }
}

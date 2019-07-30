package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Modifier;
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

        Collection<AttributeModifier> speedModifiers = meta.getAttributeModifiers(Attribute.GENERIC_MOVEMENT_SPEED);
        if (speedModifiers == null || speedModifiers.isEmpty()) modManager.addArmorAttributes(tool);
        double speedOnItem = 0.0D;
        if (!(speedModifiers == null || speedModifiers.isEmpty())) {
            HashSet<String> names = new HashSet<>();
            for(AttributeModifier am : speedModifiers) {
                if(names.add(am.getName())) speedOnItem += am.getAmount();
            }
        }

        meta.removeAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED);
        meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, new AttributeModifier(UUID.randomUUID(), "generic.movementSpeed", speedOnItem + this.speedPerLevel, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS));
        meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, new AttributeModifier(UUID.randomUUID(), "generic.movementSpeed", speedOnItem + this.speedPerLevel, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET));

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

        config.addDefault("Allowed", true);
        config.addDefault("Name", "Speedy");
        config.addDefault("ModifierItemName", "Enhanced Rabbithide");
        config.addDefault("Description", "Gotta go fast!");
        config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Speedy-Modifier");
        config.addDefault("Color", "%BLUE%");
        config.addDefault("MaxLevel", 5);
        config.addDefault("SpeedPerLevel", 0.01);

        config.addDefault("Recipe.Enabled", true);
        config.addDefault("Recipe.Top", "R R");
        config.addDefault("Recipe.Middle", " H ");
        config.addDefault("Recipe.Bottom", "R R");

        Map<String, String> recipeMaterials = new HashMap<>();
        recipeMaterials.put("H", "RABBIT_HIDE");
        recipeMaterials.put("R", "RABBIT_FOOT");

        config.addDefault("Recipe.Materials", recipeMaterials);

        ConfigurationManager.saveConfig(config);
        ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

        this.speedPerLevel = config.getDouble("SpeedPerLevel");

        init(Material.RABBIT_HIDE, true);
    }

    @Override
    public List<Attribute> getAppliedAttributes() {
        List<Attribute> attributes = new ArrayList<>();
        attributes.add(Attribute.GENERIC_MOVEMENT_SPEED);
        return attributes;
    }

    @Override
    public List<Enchantment> getAppliedEnchantments() {
        return new ArrayList<>();
    }
}

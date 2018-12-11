package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Modifiers.ModManager;

public enum ModifierType {

    AUTO_SMELT,
    BEHEADING,
    DIRECTING,
    ENDER,
    EXPERIENCED,
    EXTRA_MODIFIER,
    FIERY,
    GLOWING,
    HASTE,
    INFINITY,
    KNOCKBACK,
    LIGHT_WEIGHT,
    LUCK,
    MELTING,
    POISONOUS,
    POWER,
    PROTECTING,
    REINFORCED,
    SELF_REPAIR,
    SHARPNESS,
    SHULKING,
    SILK_TOUCH,
    SWEEPING,
    TIMBER,
    WEBBED;

    private final ModManager modManager = ModManager.instance();

    public String getName() {
        return modManager.get(this).getName();
    }
}

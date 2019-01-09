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
    SOULBOUND,
    SWEEPING,
    TIMBER,
    WEBBED;

    private final ModManager modManager = ModManager.instance();

    public String getName() {
        return modManager.get(this).getName();
    }

    public String getNBTKey() {
        switch (this) {
            case AUTO_SMELT:
                return "Auto-Smelt";
            case BEHEADING:
                return "Beheading";
            case DIRECTING:
                return "Directing";
            case ENDER:
                return "Ender";
            case EXPERIENCED:
                return "Experienced";
            case EXTRA_MODIFIER:
                return "Extra-Modifier";
            case FIERY:
                return "Fiery";
            case GLOWING:
                return "Glowing";
            case HASTE:
                return "Haste";
            case INFINITY:
                return "Infinity";
            case KNOCKBACK:
                return "Knockback";
            case LIGHT_WEIGHT:
                return "Light-Weight";
            case LUCK:
                return "Luck";
            case MELTING:
                return "Melting";
            case POISONOUS:
                return "Poisonous";
            case POWER:
                return "Power";
            case PROTECTING:
                return "Protecting";
            case REINFORCED:
                return "Reinforced";
            case SELF_REPAIR:
                return "Self-Repair";
            case SHARPNESS:
                return "Sharpness";
            case SHULKING:
                return "Shulking";
            case SILK_TOUCH:
                return "Silk-Touch";
            case SOULBOUND:
                return "Soulbound";
            case SWEEPING:
                return "Sweeping";
            case TIMBER:
                return "Timber";
            case WEBBED:
                return "Webbed";
        }
        return null;
    }
}

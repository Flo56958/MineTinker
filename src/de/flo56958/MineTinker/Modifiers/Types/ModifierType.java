package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Main;
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
    LUCK,
    MELTING,
    POISONOUS,
    POWER,
    REINFORCED,
    SELF_REPAIR,
    SHARPNESS,
    SHULKING,
    SILK_TOUCH,
    SWEEPING,
    TIMBER,
    WEBBED;

    private final ModManager modManager = Main.getModManager();

    public String getName() {
        return modManager.get(this).getName();
    }
}

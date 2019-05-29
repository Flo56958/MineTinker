package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Modifiers.ModManager;

public enum ModifierType {
    AQUAPHILIC("Aquaphilic"),
    AUTO_SMELT("Auto-Smelt"),
    BEHEADING("Beheading"),
    DIRECTING("Directing"),
    ENDER("Ender"),
    EXPERIENCED("Experienced"),
    EXTRA_MODIFIER("Extra-Modifier"),
    FIERY("Fiery"),
    FREEZING("Freezing"),
    GLOWING("Glowing"),
    HASTE("Haste"),
    INFINITY("Infinity"),
    KNOCKBACK("Knockback"),
    LIGHT_WEIGHT("Light-Weight"),
    LIFESTEAL("Lifesteal"),
    LUCK("Luck"),
    MELTING("Melting"),
    POISONOUS("Poisonous"),
    PORTALIZED("Portalized"),
    POWER("Power"),
    PROPELLING("Propelling"),
    PROTECTING("Protecting"),
    REINFORCED("Reinforced"),
    SELF_REPAIR("Self-Repair"),
    SHARPNESS("Sharpness"),
    SHULKING("Shulking"),
    SILK_TOUCH("Silk-Touch"),
    SOULBOUND("Soulbound"),
    SWEEPING("Sweeping"),
    TIMBER("Timber"),
    WEBBED("Webbed"),

    UNSPECIFIED("Unspecified"); //Should be used for third party modifiers

    private final String nbtTag;
    ModifierType(String nbtTag) { this.nbtTag = nbtTag; }

    private final ModManager modManager = ModManager.instance();

    public String getName() {
        return modManager.get(this).getName();
    }

    public String getNBTKey() {
        return nbtTag;
    }
}

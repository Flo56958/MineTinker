package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Modifiers.ModManager;

public enum ModifierType {
    ANTI_ARROW_PLATING("Anti-Arrow-Plating"),
    ANTI_BLAST_PLATING("Anti-Blast-Plating"),
    ANTI_FIRE_PLATING("Anti-Fire-Plating"),
    AQUAPHILIC("Aquaphilic"),
    AUTO_SMELT("Auto-Smelt"),
    BEHEADING("Beheading"),
    CHANNELING("Channeling"),
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
    MULTISHOT("Multishot"),
    MELTING("Melting"),
    PIERCING("Piercing"),
    POISONOUS("Poisonous"),
    POWER("Power"),
    PROPELLING("Propelling"),
    PROTECTING("Protecting"),
    QUICK_CHARGE("Quick-Charge"),
    REINFORCED("Reinforced"),
    SELF_REPAIR("Self-Repair"),
    SHARPNESS("Sharpness"),
    SHULKING("Shulking"),
    SILK_TOUCH("Silk-Touch"),
    SMITE("Smite"),
    SOULBOUND("Soulbound"),
    SPIDERSBANE("Spider's-Bane"),
    SWEEPING("Sweeping"),
    THORNED("Thorned"),
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

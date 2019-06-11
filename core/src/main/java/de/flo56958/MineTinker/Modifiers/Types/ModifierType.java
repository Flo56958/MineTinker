package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Modifiers.ModManager;

public enum ModifierType {

    ANTI_ARROW_PLATING("Anti-Arrow-Plating", "Anti-Arrow-Plating.yml"),
    ANTI_BLAST_PLATING("Anti-Blast-Plating", "Anti-Blast-Plating.yml"),
    AQUAPHILIC("Aquaphilic", "Aquaphilic.yml"),
    AUTO_SMELT("Auto-Smelt", "Auto-Smelt.yml"),
    BEHEADING("Beheading", "Beheading.yml"),
    CHANNELING("Channeling", "Channeling.yml"),
    DIRECTING("Directing", "Directing.yml"),
    ENDER("Ender", "Ender.yml"),
    EXPERIENCED("Experienced", "Experienced.yml"),
    EXTRA_MODIFIER("Extra-Modifier", "Extra-Modifier.yml"),
    FIERY("Fiery", "Fiery.yml"),
    FREEZING("Freezing", "Freezing.yml"),
    GLOWING("Glowing", "Glowing.yml"),
    HASTE("Haste", "Haste.yml"),
    INFINITY("Infinity", "Infinity.yml"),
    INSULATING("Insulating", "Insulating.yml"),
    KNOCKBACK("Knockback", "Knockback.yml"),
    LIGHT_WEIGHT("Light-Weight", "Lifesteal.yml"),
    LIFESTEAL("Lifesteal", "Light-Weight.yml"),
    LUCK("Luck", "Luck.yml"),
    MULTISHOT("Multishot", "Melting.yml"),
    MELTING("Melting", "Multishot.yml"),
    PIERCING("Piercing", "Piercing.yml"),
    POISONOUS("Poisonous", "Poisonous.yml"),
    POWER("Power", "Power.yml"),
    PROPELLING("Propelling", "Propelling.yml"),
    PROTECTING("Protecting", "Protecting.yml"),
    QUICK_CHARGE("Quick-Charge", "Quick-Charge.yml"),
    REINFORCED("Reinforced", "Reinforced.yml"),
    SELF_REPAIR("Self-Repair", "Self-Repair.yml"),
    SHARPNESS("Sharpness", "Sharpness.yml"),
    SHULKING("Shulking", "Shulking.yml"),
    SILK_TOUCH("Silk-Touch", "Silk-Touch.yml"),
    SMITE("Smite", "Smite.yml"),
    SOULBOUND("Soulbound", "Soulbound.yml"),
    SPIDERSBANE("Spider's-Bane", "Spiders-Bane.yml"),
    SWEEPING("Sweeping", "Sweeping.yml"),
    THORNED("Thorned", "Thorned.yml"),
    TIMBER("Timber", "Timber.yml"),
    WEBBED("Webbed", "Webbed.yml"),

    UNSPECIFIED("Unspecified", ""); //Should be used for third party modifiers

    private final String nbtTag;
    private final String fileName;

    ModifierType(String nbtTag, String fileName) {
        this.nbtTag = nbtTag;
        this.fileName = fileName;
    }

    private final ModManager modManager = ModManager.instance();

    public String getName() {
        return modManager.get(this).getName();
    }

    public String getNBTKey() {
        return nbtTag;
    }

    public String getFileName() {
        return fileName;
    }
}

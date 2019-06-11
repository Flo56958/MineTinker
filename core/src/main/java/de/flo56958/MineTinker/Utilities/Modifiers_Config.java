package de.flo56958.MineTinker.Utilities;

public enum Modifiers_Config {
	//TODO: Merge with Modifier-Type
	Anti_Arrow_Plating("Anti-Arrow-Plating.yml"),
	Anti_Blast_Plating("Anti-Blast-Plating.yml"),
	Anti_Fire_Plating("Anti-Fire-Plating.yml"),
	Aquaphilic("Aquaphilic.yml"),
	Auto_Smelt("Auto-Smelt.yml"),
	Beheading("Beheading.yml"),
	Channeling("Channeling.yml"),
	Directing("Directing.yml"),
	Ender("Ender.yml"),
	Experienced("Experienced.yml"),
	Extra_Modifier("Extra-Modifier.yml"),
	Fiery("Fiery.yml"),
	Freezing("Freezing.yml"),
	Glowing("Glowing.yml"),
	Haste("Haste.yml"),
	Infinity("Infinity.yml"),
	Knockback("Knockback.yml"),
	Lifesteal("Lifesteal.yml"),
	Light_Weight("Light-Weight.yml"),
	Luck("Luck.yml"),
	Melting("Melting.yml"),
	Multishot("Multishot.yml"),
	Piercing("Piercing.yml"),
	Poisonous("Poisonous.yml"),
	Power("Power.yml"),
	Propelling("Propelling.yml"),
	Protecting("Protecting.yml"),
	QuickCharge("QuickCharge.yml"),
	Reinforced("Reinforced.yml"),
	Self_Repair("Self-Repair.yml"),
	Sharpness("Sharpness.yml"),
	Shulking("Shulking.yml"),
	Silk_Touch("Silk-Touch.yml"),
	Smite("Smite.yml"),
	Soulbound("Soulbound.yml"),
	SpidersBane("Spiders-Bane.yml"),
	Sweeping("Sweeping.yml"),
	Thorned("Thorned.yml"),
	Timber("Timber.yml"),
	Webbed("Webbed.yml");

	private final String value;

	Modifiers_Config(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}
}

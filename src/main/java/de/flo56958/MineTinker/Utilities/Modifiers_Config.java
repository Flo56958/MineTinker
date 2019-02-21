package de.flo56958.MineTinker.Utilities;

public enum Modifiers_Config {
	Aquaphilic("Aquaphilic.yml"),
	Auto_Smelt("Auto-Smelt.yml"),
	Beheading("Beheading.yml"),
	Directing("Directing.yml"),
	Ender("Ender.yml"),
	Experienced("Experienced.yml"),
	Fiery("Fiery.yml"),
	Glowing("Glowing.yml"),
	Haste("Haste.yml"),
	Infinity("Infinity.yml"),
	Knockback("Knockback.yml"),
	Light_Weight("Light-Weight.yml"),
	Lifesteal("Lifesteal.yml"),
	Luck("Luck.yml"),
	Melting("Melting.yml"),
	Poisonous("Poisonous.yml"),
	Power("Power.yml"),
	Protecting("Protecting.yml"),
	Reinforced("Reinforced.yml"),
	Self_Repair("Self-Repair.yml"),
	Sharpness("Sharpness.yml"),
	Shulking("Shulking.yml"),
	Silk_Touch("Silk-Touch.yml"),
	Soulbound("Soulbound.yml"),
	Sweeping("Sweeping.yml"),
	Timber("Timber.yml"),
	Webbed("Webbed.yml"),
	Extra_Modifier("Extra-Modifier.yml");
	
	private final String value;
	Modifiers_Config(String value) {
		this.value = value;
	}
	
	@Override public String toString() {
		return value;
	}
}

package de.flo56958.minetinker.utils.playerconfig;

public record PlayerConfigurationOption(PlayerConfigurationInterface source, String key, Type type, String displayName, Object defaultValue) {

	public enum Type {
		BOOLEAN,
		INTEGER,
		DOUBLE,
		STRING
	}

	public String getKey() {
		return source.getPCIKey() + "." + key;
	}
}
package de.flo56958.minetinker.modifiers;

import de.flo56958.minetinker.utils.playerconfig.PlayerConfigurationInterface;
import de.flo56958.minetinker.utils.playerconfig.PlayerConfigurationOption;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import java.util.List;

public abstract class PlayerConfigurableModifier extends Modifier implements PlayerConfigurationInterface {

	protected PlayerConfigurableModifier(Plugin source) {
		super(source);
	}

	@Override
	public List<PlayerConfigurationOption> getPCIOptions() {
		return List.of();
	}

	@Override
	public ChatColor getPCIDisplayColor() {
		return this.getColor();
	}

	@Override
	public String getPCIDisplayName() {
		return this.getName();
	}

	@Override
	public String getPCIKey() {
		return this.getKey();
	}
}

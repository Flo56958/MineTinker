package de.flo56958.minetinker.modifiers;

import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.LanguageManager;
import de.flo56958.minetinker.utils.data.DataHandler;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CooldownModifier extends Modifier {
	protected double cooldownInSeconds;
	protected double cooldownReductionPerLevel = 0.0;

	/**
	 * Class constructor
	 *
	 * @param source The Plugin that registered the Modifier
	 */
	public CooldownModifier(Plugin source) {
		super(source);
	}

	/**
	 * checks if the modifier on the tool is still on cooldown
	 *
	 * @param player    the player that uses the tool
	 * @param tool      the tool that is used
	 * @param sendAlert if an alert should be sent to the player if it is still on cooldown
	 * @param event     the event that triggered the usage of the tool
	 * @return true if the modifier is still on cooldown on the tool
	 */
	protected final boolean onCooldown(@NotNull final Player player, @NotNull final ItemStack tool,
	                                   final boolean sendAlert, @Nullable final Event event) {
		final long time = System.currentTimeMillis();
		final int level = modManager.getModLevel(tool, this);
		final long cooldownTime = getCooldown(level);
		if (cooldownTime <= /* one tick in ms */ 1000 / 20) return false; // cooldown is disabled
		final Long cd = DataHandler.getTag(tool, this.getKey() + "cooldown", PersistentDataType.LONG);
		if (cd == null) return false; // was never on cooldown

		if (!(time - cd < Math.round(cooldownTime) && player.getGameMode() != GameMode.CREATIVE)) return false;

		ChatWriter.logModifier(player, event, this, tool, "Cooldown");
		if (sendAlert)
			ChatWriter.sendActionBar(player, this.getName() + ": "
					+ LanguageManager.getString("Alert.OnCooldown", player));
		return true; //still on cooldown
	}

	protected final void setCooldown(@NotNull final ItemStack tool) {
		if (this.cooldownInSeconds > 1 / 20.0)
			DataHandler.setTag(tool, this.getKey() + "cooldown", System.currentTimeMillis(), PersistentDataType.LONG);
	}

	/**
	 * sets the given tool on cooldown
	 *
	 * @param tool the tool that should be set on cooldown
	 * @param time how long the cooldown should be in ms
	 */
	protected final void setCooldown(@NotNull final ItemStack tool, long time) {
		if (this.cooldownInSeconds > 1 / 20.0)
			time = Math.round(this.cooldownInSeconds * 1000.0) - time;
		DataHandler.setTag(tool, this.getKey() + "cooldown",
				System.currentTimeMillis() - time, PersistentDataType.LONG);
	}

	/**
	 * calculates the cooldown for the given level in ms
	 *
	 * @param level the level of the modifier
	 * @return the cooldown in ms
	 */
	protected final long getCooldown(final int level) {
		double cooldownTime = this.cooldownInSeconds * 1000L;
		if (this.cooldownReductionPerLevel > 1e-7)
			cooldownTime *= Math.pow(1.0 - this.cooldownReductionPerLevel, level - 1);
		return Math.round(cooldownTime);
	}
}

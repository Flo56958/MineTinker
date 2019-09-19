package de.flo56958.MineTinker.Data;

import de.flo56958.MineTinker.Utilities.LanguageManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public enum ModifierFailCause {

	INCOMPATIBLE_MODIFIERS,
	INVALID_TOOLTYPE,
	MAXIMUM_SLOTS_REACHED,
	MOD_MAXLEVEL,
	NO_PERMISSION,
	NO_FREE_SLOTS;

	@Override
	public String toString() {
		return toString(null);
	}

	@NotNull
	public String toString(Player p) {
		switch (this) {
			case INCOMPATIBLE_MODIFIERS:
				return LanguageManager.getString("ModifierFailCause.IncompatibleModifiers", p);
			case INVALID_TOOLTYPE:
				return LanguageManager.getString("ModifierFailCause.InvalidToolType", p);
			case MAXIMUM_SLOTS_REACHED:
				return LanguageManager.getString("ModifierFailCause.MaximumSlotsReached", p);
			case MOD_MAXLEVEL:
				return LanguageManager.getString("ModifierFailCause.ModMaxLevel", p);
			case NO_FREE_SLOTS:
				return LanguageManager.getString("ModifierFailCause.NoFreeSlots", p);
			case NO_PERMISSION:
				return LanguageManager.getString("ModifierFailCause.NoPermission", p);
		}
		return "";
	}
}

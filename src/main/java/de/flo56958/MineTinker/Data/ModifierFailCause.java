package de.flo56958.MineTinker.Data;

public enum ModifierFailCause {

    INCOMPATIBLE_MODIFIERS("Incompatible Modifiers!"),
    INVALID_TOOLTYPE("Invalid Tool-Type!"),
    MAXIMUM_SLOTS_REACHED("Maximum Slots reached!"),
    MOD_MAXLEVEL("Modifier is already max Level!"),
    NO_PERMISSION("No Permission!"),
    NO_FREE_SLOTS("No free Slots!");

    private final String value;
    ModifierFailCause(String value) {
        this.value = value;
    }

    @Override
    public String toString() { //TODO: relocate to Lang-config
        return value;
    }
}

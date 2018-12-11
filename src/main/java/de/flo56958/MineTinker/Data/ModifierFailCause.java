package de.flo56958.MineTinker.Data;

public enum ModifierFailCause {

    INCOMPATIBLE_MODIFIERS,
    INVALID_TOOLTYPE,
    MAXIMUM_SLOTS_REACHED,
    MOD_MAXLEVEL,
    NO_PERMISSION,
    NO_FREE_SLOTS;

    @Override
    public String toString() { //TODO: relocate to Lang-config
        switch(this) {
            case INCOMPATIBLE_MODIFIERS: return "Incompatible Modifiers!";
            case INVALID_TOOLTYPE: return "Invalid Tool-Type!";
            case MAXIMUM_SLOTS_REACHED: return "Maximum Slots reached!";
            case MOD_MAXLEVEL: return "Modifier is already max Level!";
            case NO_PERMISSION: return "No Permission!";
            case NO_FREE_SLOTS: return "No free Slots!";
            default: return "";
        }
    }
}

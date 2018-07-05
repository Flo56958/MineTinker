package de.flo56958.MineTinker.Data;

import de.flo56958.MineTinker.Main;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Lists {

    public static final List<String> SWORDS = (List<String>) Main.getPlugin().getConfig().getList("AllowedTools.Swords");
    public static final List<String> AXES = (List<String>) Main.getPlugin().getConfig().getList("AllowedTools.Axes");
    public static final List<String> PICKAXES = (List<String>) Main.getPlugin().getConfig().getList("AllowedTools.Pickaxes");
    public static final List<String> SHOVELS = (List<String>) Main.getPlugin().getConfig().getList("AllowedTools.Shovels");

    public static final ItemStack[] MODIFIERS = new ItemStack[] {Modifiers.SELFREPAIR_MODIFIER};
}

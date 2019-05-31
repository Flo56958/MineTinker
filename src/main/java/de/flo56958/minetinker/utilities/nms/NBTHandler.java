package de.flo56958.minetinker.utilities.nms;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class NBTHandler {
    public abstract int getInt(ItemStack item, String key);
    public abstract long getLong(ItemStack item, String key);
    public abstract String getString(ItemStack item, String key);
    public abstract List<String> getStringList(ItemStack item, String key);

    public abstract void setInt(ItemStack item, String key, int value);
    public abstract void setLong(ItemStack item, String key, long value);
    public abstract void setString(ItemStack item, String key, String value);
    public abstract void setStringList(ItemStack item, String key, String ... value);

    public abstract boolean hasTag(ItemStack item, String key);
    public abstract void removeTag(ItemStack item, String key);
}

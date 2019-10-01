package de.flo56958.MineTinker.Utilities.nms;

import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.Iterator;
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

    public abstract void playerBreakBlock(Player p, Block block);

    public abstract Iterator<Recipe> getRecipeIterator();

    public abstract void removeArrowFromClient(Arrow arrow);
}

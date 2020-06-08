package de.flo56958.MineTinker.Utilities.nms;

import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public abstract class NBTHandler {

    public abstract Plugin getPlugin();

    private StringArrayItemTagType STRING_ARRAY = new StringArrayItemTagType(Charset.defaultCharset());

    public int getInt(ItemStack item, String key) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        Integer value = container.get(new NamespacedKey(getPlugin(), key), PersistentDataType.INTEGER);

        if (value != null) {
            return value;
        }

        return 0;
    }

    public long getLong(ItemStack item, String key) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        Long value = container.get(new NamespacedKey(getPlugin(), key), PersistentDataType.LONG);

        if (value != null) {
            return value;
        }

        return 0;
    }

    public String getString(ItemStack item, String key) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();

        return container.get(new NamespacedKey(getPlugin(), key), PersistentDataType.STRING);
    }

    public List<String> getStringList(ItemStack item, String key) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        String[] array = container.get(new NamespacedKey(getPlugin(), key), STRING_ARRAY);

        if (array == null) {
            return null;
        }

        return Arrays.asList(array);
    }

    public void setInt(ItemStack item, String key, int value) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();

        container.set(new NamespacedKey(getPlugin(), key), PersistentDataType.INTEGER, value);
    }

    public void setLong(ItemStack item, String key, long value) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();

        container.set(new NamespacedKey(getPlugin(), key), PersistentDataType.LONG, value);
    }

    public void setString(ItemStack item, String key, String value) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();

        container.set(new NamespacedKey(getPlugin(), key), PersistentDataType.STRING, value);
    }

    public void setStringList(ItemStack item, String key, String ... value) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();

        container.set(new NamespacedKey(getPlugin(), key), STRING_ARRAY, value);
    }

    public boolean hasTag(ItemStack item, String key, PersistentDataType dataType) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();

        return container.has(new NamespacedKey(getPlugin(), key), dataType);
    }

    public void removeTag(ItemStack item, String key) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();

        container.remove(new NamespacedKey(getPlugin(), key));
    }

    public abstract boolean playerBreakBlock(Player player, Block block);

    public abstract Iterator<Recipe> getRecipeIterator();

    public abstract void removeArrowFromClient(Arrow arrow);
}

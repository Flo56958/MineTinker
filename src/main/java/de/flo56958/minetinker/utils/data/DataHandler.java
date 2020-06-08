package de.flo56958.minetinker.utils.data;

import de.flo56958.minetinker.MineTinker;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class DataHandler {

    private static StringArrayItemTagType STRING_ARRAY = new StringArrayItemTagType(Charset.defaultCharset());

    public static int getInt(ItemStack item, String key) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        Integer value = container.get(new NamespacedKey(MineTinker.getPlugin(), key), PersistentDataType.INTEGER);

        if (value != null) {
            return value;
        }

        return 0;
    }

    public static long getLong(ItemStack item, String key) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        Long value = container.get(new NamespacedKey(MineTinker.getPlugin(), key), PersistentDataType.LONG);

        if (value != null) {
            return value;
        }

        return 0;
    }

    public static String getString(ItemStack item, String key) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();

        return container.get(new NamespacedKey(MineTinker.getPlugin(), key), PersistentDataType.STRING);
    }

    public static List<String> getStringList(ItemStack item, String key) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        String[] array = container.get(new NamespacedKey(MineTinker.getPlugin(), key), STRING_ARRAY);

        if (array == null) {
            return null;
        }

        return Arrays.asList(array);
    }

    public static void setInt(ItemStack item, String key, int value) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();

        container.set(new NamespacedKey(MineTinker.getPlugin(), key), PersistentDataType.INTEGER, value);
    }

    public static void setLong(ItemStack item, String key, long value) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();

        container.set(new NamespacedKey(MineTinker.getPlugin(), key), PersistentDataType.LONG, value);
    }

    public static void setString(ItemStack item, String key, String value) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();

        container.set(new NamespacedKey(MineTinker.getPlugin(), key), PersistentDataType.STRING, value);
    }

    public static void setStringList(ItemStack item, String key, String ... value) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();

        container.set(new NamespacedKey(MineTinker.getPlugin(), key), STRING_ARRAY, value);
    }

    public static boolean hasTag(ItemStack item, String key, PersistentDataType dataType) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();

        return container.has(new NamespacedKey(MineTinker.getPlugin(), key), dataType);
    }

    public static void removeTag(ItemStack item, String key) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();

        container.remove(new NamespacedKey(MineTinker.getPlugin(), key));
    }

    public static boolean playerBreakBlock(Player player, Block block) {
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        Block position = player.getLocation().getBlock();

        BlockBreakEvent breakEvent = new BlockBreakEvent(block, player);

        if (!breakEvent.isCancelled() && position.breakNaturally(itemStack)) {
            Collection<ItemStack> items = position.getDrops(itemStack);

            List<Item> itemEntities = items.stream().map(entry -> {
                return (Item)player.getWorld().spawnEntity(player.getLocation(), EntityType.DROPPED_ITEM);
            }).collect(Collectors.toList());

            BlockDropItemEvent event = new BlockDropItemEvent(position, position.getState(), player, itemEntities);

            if (!event.isCancelled()) {
                for (Item item : event.getItems()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), item.getItemStack());
                }
            }

            // TODO: drop experience

            return true;
        }

        return false;
    }
}

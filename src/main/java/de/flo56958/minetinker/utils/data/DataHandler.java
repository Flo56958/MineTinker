package de.flo56958.minetinker.utils.data;

import de.flo56958.minetinker.MineTinker;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class DataHandler {

    private static final StringArrayItemTagType STRING_ARRAY = new StringArrayItemTagType(Charset.defaultCharset());

    public static int getInt(@NotNull ItemStack item, @NotNull String key) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return 0;
        PersistentDataContainer container = meta.getPersistentDataContainer();
        Integer value = container.get(new NamespacedKey(MineTinker.getPlugin(), key), PersistentDataType.INTEGER);

        if (value != null) {
            return value;
        }

        return 0;
    }

    public static long getLong(@NotNull ItemStack item, @NotNull String key) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return 0L;
        PersistentDataContainer container = meta.getPersistentDataContainer();
        Long value = container.get(new NamespacedKey(MineTinker.getPlugin(), key), PersistentDataType.LONG);

        if (value != null) {
            return value;
        }

        return 0;
    }

    @Nullable
    public static String getString(@NotNull ItemStack item, @NotNull String key) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;
        PersistentDataContainer container = meta.getPersistentDataContainer();

        return container.get(new NamespacedKey(MineTinker.getPlugin(), key), PersistentDataType.STRING);
    }

    @Nullable
    public static List<String> getStringList(@NotNull ItemStack item, @NotNull String key) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;
        PersistentDataContainer container = meta.getPersistentDataContainer();
        String[] array = container.get(new NamespacedKey(MineTinker.getPlugin(), key), STRING_ARRAY);

        if (array == null) {
            return null;
        }

        return Arrays.asList(array);
    }

    public static void setInt(@NotNull ItemStack item, @NotNull String key, int value) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        PersistentDataContainer container = meta.getPersistentDataContainer();

        container.set(new NamespacedKey(MineTinker.getPlugin(), key), PersistentDataType.INTEGER, value);
        item.setItemMeta(meta);
    }

    public static void setLong(@NotNull ItemStack item, @NotNull String key, long value) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        PersistentDataContainer container = meta.getPersistentDataContainer();

        container.set(new NamespacedKey(MineTinker.getPlugin(), key), PersistentDataType.LONG, value);
        item.setItemMeta(meta);
    }

    public static void setString(@NotNull ItemStack item, @NotNull String key, String value) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        PersistentDataContainer container = meta.getPersistentDataContainer();

        container.set(new NamespacedKey(MineTinker.getPlugin(), key), PersistentDataType.STRING, value);
        item.setItemMeta(meta);
    }

    public static void setStringList(@NotNull ItemStack item, @NotNull String key, String ... value) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        PersistentDataContainer container = meta.getPersistentDataContainer();

        container.set(new NamespacedKey(MineTinker.getPlugin(), key), STRING_ARRAY, value);
        item.setItemMeta(meta);
    }

    public static <T, Z> boolean hasTag(ItemStack item, String key, PersistentDataType<T, Z> dataType) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        PersistentDataContainer container = meta.getPersistentDataContainer();

        return container.has(new NamespacedKey(MineTinker.getPlugin(), key), dataType);
    }

    public static void removeTag(ItemStack item, String key) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        PersistentDataContainer container = meta.getPersistentDataContainer();

        container.remove(new NamespacedKey(MineTinker.getPlugin(), key));
        item.setItemMeta(meta);
    }

    public static boolean playerBreakBlock(Player player, Block block) {
        ItemStack itemStack = player.getInventory().getItemInMainHand();

        BlockBreakEvent breakEvent = new BlockBreakEvent(block, player);
        Bukkit.getPluginManager().callEvent(breakEvent);

        if (!breakEvent.isCancelled() && block.breakNaturally(itemStack)) {
            Collection<ItemStack> items = block.getDrops(itemStack);

            List<Item> itemEntities = items.stream().map(entry ->
                    (Item)player.getWorld().spawnEntity(player.getLocation(), EntityType.DROPPED_ITEM)).collect(Collectors.toList());

            BlockDropItemEvent event = new BlockDropItemEvent(block, block.getState(), player, itemEntities);
            Bukkit.getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                for (Item item : event.getItems()) {
                    player.getWorld().dropItemNaturally(block.getLocation(), item.getItemStack());
                }
            }

            // TODO: drop experience

            return true;
        }

        return false;
    }
}

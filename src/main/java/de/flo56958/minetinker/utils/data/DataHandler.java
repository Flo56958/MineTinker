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

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class DataHandler {

    public static <T, Z> boolean hasTag(@NotNull ItemStack item, @NotNull String key, PersistentDataType<T, Z> dataType, boolean useMinecraft) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        PersistentDataContainer container = meta.getPersistentDataContainer();

        return container.has((useMinecraft ? NamespacedKey.minecraft(key) : new NamespacedKey(MineTinker.getPlugin(), key)), dataType);
    }

    public static <T, Z> void setTag(@NotNull ItemStack item, @NotNull String key, Z value, PersistentDataType<T, Z> dataType, boolean useMinecraft) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set((useMinecraft ? NamespacedKey.minecraft(key) : new NamespacedKey(MineTinker.getPlugin(), key)), dataType, value);
        item.setItemMeta(meta);
    }

    public static <T, Z> @Nullable Z getTag(@NotNull ItemStack item, @NotNull String key, PersistentDataType<T, Z> dataType, boolean useMinecraft) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        PersistentDataContainer container = meta.getPersistentDataContainer();
        return container.get((useMinecraft ? NamespacedKey.minecraft(key) : new NamespacedKey(MineTinker.getPlugin(), key)), dataType);
    }

    public static void removeTag(@NotNull ItemStack item, @NotNull String key, boolean useMinecraft) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        PersistentDataContainer container = meta.getPersistentDataContainer();

        container.remove((useMinecraft ? NamespacedKey.minecraft(key) : new NamespacedKey(MineTinker.getPlugin(), key)));
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

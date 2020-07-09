package de.flo56958.minetinker.utils.data;

import de.flo56958.minetinker.MineTinker;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
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

    public static boolean playerBreakBlock(@NotNull Player player, Block block, @NotNull ItemStack itemStack) {
        //
        //This skips all interactions and synergies with MT and other Plugins but is way less performance heavy
        //
        if (MineTinker.getPlugin().getConfig().getBoolean("LowSpecMode")) {
            block.breakNaturally(itemStack);

            //Spawn Experience Orb
            int exp = calculateExp(block.getType());
            if (exp > 0) {
                ExperienceOrb orb = (ExperienceOrb) player.getWorld().spawnEntity(block.getLocation(), EntityType.EXPERIENCE_ORB);
                orb.setExperience(exp);
            }
            return true;
        }

        //
        //Trigger BlockBreakEvent
        //For interactions with MT itself and other Plugins
        //
        BlockBreakEvent breakEvent = new BlockBreakEvent(block, player);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null && !meta.hasEnchant(Enchantment.SILK_TOUCH)) breakEvent.setExpToDrop(calculateExp(block.getType()));
        Bukkit.getPluginManager().callEvent(breakEvent);

        //Check if Event got cancelled and if not destroy the block and check if the player can successfully break the blocks (incl. drops)
        //Block#breakNaturally(ItemStack itemStack) can not be used as it drops Items itself (without Event and we don't want that)
        if (!breakEvent.isCancelled()) {
            //Get all drops to drop
            Collection<ItemStack> items = block.getDrops(itemStack);

            //Set Block to Material.AIR (effectively breaks the Block)
            block.setType(Material.AIR);
            //TODO: Play Sound?

            //Check if items need to be dropped
            if (breakEvent.isDropItems()) {
                List<Item> itemEntities = items.stream()
                        .map(entry -> player.getWorld().dropItemNaturally(block.getLocation(), entry)) //World#spawnEntity() does not work for Items
                        .collect(Collectors.toList());

                //Trigger BlockDropItemEvent (internally also used for Directing)
                BlockDropItemEvent event = new BlockDropItemEvent(block, block.getState(), player, new ArrayList<>(itemEntities));
                Bukkit.getPluginManager().callEvent(event);

                //check if Event got cancelled
                if (!event.isCancelled()) {
                    //Remove all drops that should be dropped
                    itemEntities.removeIf(element -> event.getItems().contains(element));
                }
                itemEntities.forEach(Item::remove);
            }

            //Check if Exp needs to be dropped
            if (breakEvent.getExpToDrop() > 0) {
                //Spawn Experience Orb
                ExperienceOrb orb = (ExperienceOrb) player.getWorld().spawnEntity(block.getLocation(), EntityType.EXPERIENCE_ORB);
                orb.setExperience(breakEvent.getExpToDrop());
            }

            return true;
        }

        return false;
    }

    private static int calculateExp(Material type) {
        //TODO: Find better method then hardcoded values
        switch(type) {
            case COAL_ORE: //0-2
                return new Random().nextInt(3);
            case DIAMOND_ORE: //3-7
            case EMERALD_ORE:
                return new Random().nextInt(5) + 3;
            case NETHER_QUARTZ_ORE: //2-5
            case LAPIS_ORE:
                return new Random().nextInt(4) + 2;
            case REDSTONE_ORE: //1-5
                return new Random().nextInt(4) + 1;
            case SPAWNER: //15-43
                return new Random().nextInt(29) + 15;
            default:
                return 0;
        }
    }
}

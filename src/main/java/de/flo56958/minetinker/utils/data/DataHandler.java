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
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
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

    public static boolean playerBreakBlock(@NotNull Player player, Block block, @NotNull ItemStack itemStack) throws IllegalArgumentException {
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

            //Calculate Damage for itemStack
            ItemMeta meta = itemStack.getItemMeta();
            if (!meta.isUnbreakable()) {
                if (meta instanceof Damageable) {
                    //Consider Unbreaking enchant
                    int lvl = meta.getEnchantLevel(Enchantment.DURABILITY);
                    int r = new Random().nextInt(100);
                    if (!(r > 100 / (lvl + 1))) {
                        ((Damageable) meta).setDamage(((Damageable) meta).getDamage() + 1);
                        itemStack.setItemMeta(meta);
                    }
                }
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

            //Calculate Damage for itemStack
            meta = itemStack.getItemMeta();
            if (!meta.isUnbreakable()) {
                if (meta instanceof Damageable) {
                    //Consider Unbreaking Enchant
                    int lvl = meta.getEnchantLevel(Enchantment.DURABILITY);
                    int damage = 1;
                    int r = new Random().nextInt(100);
                    if (r > 100 / (lvl + 1)) {
                        damage = 0;
                    }
                    PlayerItemDamageEvent damageEvent = new PlayerItemDamageEvent(player, itemStack, damage);
                    Bukkit.getPluginManager().callEvent(damageEvent);
                    if (!damageEvent.isCancelled()) {
                        meta = itemStack.getItemMeta();
                        ((Damageable) meta).setDamage(((Damageable) meta).getDamage() + damageEvent.getDamage());
                        itemStack.setItemMeta(meta);
                    }
                }
            }

            return true;
        }

        return false;
    }

    private static int calculateExp(Material type) {
        //TODO: Find better method then hardcoded values
        return switch (type) {
            //0-2
            case COAL_ORE -> new Random().nextInt(3);
            //3-7
            case DIAMOND_ORE, EMERALD_ORE -> new Random().nextInt(5) + 3;
            //2-5
            case NETHER_QUARTZ_ORE, LAPIS_ORE -> new Random().nextInt(4) + 2;
            //1-5
            case REDSTONE_ORE -> new Random().nextInt(4) + 1;
            //15-43
            case SPAWNER -> new Random().nextInt(29) + 15;
            default -> 0;
        };
    }
}

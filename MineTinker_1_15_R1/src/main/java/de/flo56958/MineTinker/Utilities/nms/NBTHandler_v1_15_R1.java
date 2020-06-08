package de.flo56958.MineTinker.Utilities.nms;

import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.stream.Collectors;

class NBTHandler_v1_15_R1 extends NBTHandler {

    private Plugin plugin;

    public NBTHandler_v1_15_R1(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

	@Override
	public boolean playerBreakBlock(Player player, Block block) {
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
		//return ((CraftPlayer) player).getHandle().playerInteractManager.breakBlock(new BlockPosition(block.getX(), block.getY(), block.getZ()));
	}

    @Override
    public Iterator<Recipe> getRecipeIterator() {
        return new RecipeIterator_v1_15_R1();
    }

    @Override
    public void removeArrowFromClient(Arrow arrow) {
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(arrow.getEntityId());
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }
    }
}

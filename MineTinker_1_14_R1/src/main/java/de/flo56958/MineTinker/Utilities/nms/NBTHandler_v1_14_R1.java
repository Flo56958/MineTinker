package de.flo56958.MineTinker.Utilities.nms;

import net.minecraft.server.v1_14_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

class NBTHandler_v1_14_R1 extends NBTHandler {

	private Plugin plugin;

	public NBTHandler_v1_14_R1(Plugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public Plugin getPlugin() {
		return plugin;
	}

	@Override
	public boolean playerBreakBlock(Player player, Block block) {
		return ((CraftPlayer) player).getHandle().playerInteractManager.breakBlock(new BlockPosition(block.getX(), block.getY(), block.getZ()));
	}

    @Override
    public Iterator<Recipe> getRecipeIterator() {
        return new RecipeIterator_v1_14_R1();
    }

    @Override
    public void removeArrowFromClient(Arrow arrow) {
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(arrow.getEntityId());
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }
    }
}

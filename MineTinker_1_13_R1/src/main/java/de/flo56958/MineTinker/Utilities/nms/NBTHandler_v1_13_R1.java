package de.flo56958.MineTinker.Utilities.nms;

import net.minecraft.server.v1_13_R1.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_13_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

class NBTHandler_v1_13_R1 extends NBTHandler {

    private NBTBase getBase(org.bukkit.inventory.ItemStack item, String key) {
        ItemStack nmsItem = CraftItemStack.asNMSCopy(item);

        NBTTagCompound comp = nmsItem.getTag();

        if (comp == null) {
            return null;
        }

        return comp.get(key);
    }

    private void setNumber(org.bukkit.inventory.ItemStack item, String key, NBTBase base) {
        ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound comp = nmsItem.getTag();

        if (comp == null) {
            comp = new NBTTagCompound();
        }

        comp.set(key, base);
        nmsItem.setTag(comp);

        ItemMeta meta = CraftItemStack.getItemMeta(nmsItem);
        item.setItemMeta(meta);
    }

    @Override
    public int getInt(org.bukkit.inventory.ItemStack item, String key) {
        NBTBase base = getBase(item, key);

        if (!(base instanceof NBTTagInt)) {
            return 0;
        }

        return ((NBTTagInt)base).e();
    }

    @Override
    public long getLong(org.bukkit.inventory.ItemStack item, String key) {
        NBTBase base = getBase(item, key);

        if (!(base instanceof NBTTagLong)) {
            return 0;
        }

        return ((NBTTagLong)base).d();
    }

    @Override
    public String getString(org.bukkit.inventory.ItemStack item, String key) {
        NBTBase base = getBase(item, key);

        if (!(base instanceof NBTTagString)) {
            return null;
        }

        return base.b_();
    }

    @Override
    public List<String> getStringList(org.bukkit.inventory.ItemStack item, String key) {
        NBTBase base = getBase(item, key);

        if (!(base instanceof NBTTagList)) {
            return null;
        }

        String[] values = (String[])((NBTTagList) base).toArray();

        return Arrays.asList(values);
    }

    @Override
    public void setInt(org.bukkit.inventory.ItemStack item, String key, int value) {
        setNumber(item, key, new NBTTagInt(value));
    }

    @Override
    public void setLong(org.bukkit.inventory.ItemStack item, String key, long value) {
        setNumber(item, key, new NBTTagLong(value));
    }

    @Override
    public void setString(org.bukkit.inventory.ItemStack item, String key, String value) {
        setNumber(item, key, new NBTTagString(value));
    }

    @Override
    public void setStringList(org.bukkit.inventory.ItemStack item, String key, String... value) {
        NBTTagList list = new NBTTagList();

        for (String val : value) {
            list.add(new NBTTagString(val));
        }

        ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound comp = nmsItem.getTag();

        if (comp == null) {
            comp = new NBTTagCompound();
        }

        comp.set(key, list);
        nmsItem.setTag(comp);

        ItemMeta meta = CraftItemStack.getItemMeta(nmsItem);
        item.setItemMeta(meta);
    }

    @Override
    public boolean hasTag(org.bukkit.inventory.ItemStack item, String key) {
        ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound comp = nmsItem.getTag();

        if (comp == null) {
            return false;
        }

        return comp.get(key) != null;
    }

    @Override
    public void removeTag(org.bukkit.inventory.ItemStack item, String key) {
        ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound comp = nmsItem.getTag();

        if (comp == null) {
            comp = new NBTTagCompound();
        }

        comp.remove(key);
        nmsItem.setTag(comp);

        ItemMeta meta = CraftItemStack.getItemMeta(nmsItem);
        item.setItemMeta(meta);
    }

    @Override
    public void playerBreakBlock(Player p, Block block) {
        ((CraftPlayer) p).getHandle().playerInteractManager.breakBlock(new BlockPosition(block.getX(), block.getY(), block.getZ()));
    }
}

package de.flo56958.minetinker.utilities.nms;

import net.minecraft.server.v1_14_R1.NBTBase;
import net.minecraft.server.v1_14_R1.NBTTagCompound;
import net.minecraft.server.v1_14_R1.NBTTagInt;
import net.minecraft.server.v1_14_R1.NBTTagList;
import net.minecraft.server.v1_14_R1.NBTTagLong;
import net.minecraft.server.v1_14_R1.NBTTagString;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

class NBTHandler_1_14_R0 extends NBTHandler {

    @Override
    public int getInt(ItemStack item, String key) {
        net.minecraft.server.v1_14_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);

        NBTTagCompound comp = nmsItem.getTag();
        if (comp == null) { return 0; }

        NBTBase base = comp.get(key);
        if (!(base instanceof NBTTagInt)) return 0;

        return ((NBTTagInt)base).asInt();
    }

    @Override
    public long getLong(ItemStack item, String key) {
        net.minecraft.server.v1_14_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);

        NBTTagCompound comp = nmsItem.getTag();
        if (comp == null) { return 0; }

        NBTBase base = comp.get(key);
        if (!(base instanceof NBTTagLong)) return 0;

        return ((NBTTagLong)base).asLong();
    }

    @Override
    public String getString(ItemStack item, String key) {
        net.minecraft.server.v1_14_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);

        NBTTagCompound comp = nmsItem.getTag();
        if (comp == null) { return null; }

        NBTBase base = comp.get(key);
        if (!(base instanceof NBTTagString)) return null;

        return base.asString();
    }

    @Override
    public List<String> getStringList(ItemStack item, String key) {
        net.minecraft.server.v1_14_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);

        NBTTagCompound comp = nmsItem.getTag();
        if (comp == null) { return null; }

        NBTBase base = comp.get(key);
        if (!(base instanceof NBTTagList)) return null;

        String[] values = (String[])((NBTTagList) base).toArray();

        return Arrays.asList(values);
    }

    @Override
    public void setInt(ItemStack item, String key, int value) {
        net.minecraft.server.v1_14_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound comp = nmsItem.getTag();

        if (comp == null) { comp = new NBTTagCompound(); }

        comp.setInt(key, value);
        nmsItem.setTag(comp);

        ItemMeta meta = CraftItemStack.getItemMeta(nmsItem);
        item.setItemMeta(meta);
    }

    @Override
    public void setLong(ItemStack item, String key, long value) {
        net.minecraft.server.v1_14_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound comp = nmsItem.getTag();

        if (comp == null) { comp = new NBTTagCompound(); }

        comp.setLong(key, value);
        nmsItem.setTag(comp);

        ItemMeta meta = CraftItemStack.getItemMeta(nmsItem);
        item.setItemMeta(meta);
    }

    @Override
    public void setString(ItemStack item, String key, String value) {
        net.minecraft.server.v1_14_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound comp = nmsItem.getTag();

        if (comp == null) { comp = new NBTTagCompound(); }

        comp.setString(key, value);
        nmsItem.setTag(comp);

        ItemMeta meta = CraftItemStack.getItemMeta(nmsItem);
        item.setItemMeta(meta);
    }

    @Override
    public void setStringList(ItemStack item, String key, String... value) {
        NBTTagList list = new NBTTagList();

        for (String val : value) list.add(new NBTTagString(val));

        net.minecraft.server.v1_14_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound comp = nmsItem.getTag();

        if (comp == null) { comp = new NBTTagCompound(); }

        comp.set(key, list);
        nmsItem.setTag(comp);

        ItemMeta meta = CraftItemStack.getItemMeta(nmsItem);
        item.setItemMeta(meta);
    }

    @Override
    public boolean hasTag(ItemStack item, String key) {
        return CraftItemStack.asNMSCopy(item).getTag() != null;
    }

    @Override
    public void removeTag(ItemStack item, String key) {
        net.minecraft.server.v1_14_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound comp = nmsItem.getTag();

        if (comp == null) comp = new NBTTagCompound();

        comp.remove(key);
        nmsItem.setTag(comp);

        ItemMeta meta = CraftItemStack.getItemMeta(nmsItem);
        item.setItemMeta(meta);
    }
}

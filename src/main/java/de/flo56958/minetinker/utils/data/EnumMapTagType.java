package de.flo56958.minetinker.utils.data;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.EnumMap;

public class EnumMapTagType<K extends Enum<K>, V> implements PersistentDataType<byte[], EnumMap<K, V>> {

    private final EnumMap<K, V> reference;

    public EnumMapTagType(EnumMap<K, V> ref) {
        this.reference = ref;
    }

    @Override
    public @NotNull Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public @NotNull Class<EnumMap<K, V>> getComplexType() {
        return (Class<EnumMap<K, V>>) reference.getClass();
    }

    @Override
    public byte @NotNull [] toPrimitive(@NotNull EnumMap<K, V> map, @NotNull PersistentDataAdapterContext itemTagAdapterContext) {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        try {
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(map);
            return byteOut.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    @Override
    public @NotNull EnumMap<K, V> fromPrimitive(byte @NotNull [] bytes, @NotNull PersistentDataAdapterContext itemTagAdapterContext) {
        ByteArrayInputStream byteIn = new ByteArrayInputStream(bytes);
        try {
            ObjectInputStream in = new ObjectInputStream(byteIn);
            return (EnumMap<K, V>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return this.reference.clone();
    }
}

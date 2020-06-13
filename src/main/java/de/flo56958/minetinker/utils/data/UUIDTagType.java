package de.flo56958.minetinker.utils.data;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * from API-comment of
 * @see PersistentDataType
 */
public class UUIDTagType implements PersistentDataType<byte[], UUID> {

    public static final UUIDTagType instance = new UUIDTagType();

    private UUIDTagType() {}

    @Override
	public @NotNull Class<byte[]> getPrimitiveType() {
		return byte[].class;
	}

	@Override
	public @NotNull Class<UUID> getComplexType() {
		return UUID.class;
	}

	@Override
	public byte @NotNull [] toPrimitive(UUID complex, @NotNull PersistentDataAdapterContext context) {
		ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
		bb.putLong(complex.getMostSignificantBits());
		bb.putLong(complex.getLeastSignificantBits());
		return bb.array();
	}

	@Override
	public @NotNull UUID fromPrimitive(byte @NotNull [] primitive, @NotNull PersistentDataAdapterContext context) {
		ByteBuffer bb = ByteBuffer.wrap(primitive);
		long firstLong = bb.getLong();
		long secondLong = bb.getLong();
		return new UUID(firstLong, secondLong);
	}
}
package dristmine.dristregions.creation;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.UUID;

public class UUIDDataType implements PersistentDataType<byte[], UUID> {
	@Override
	public @NotNull Class<byte[]> getPrimitiveType() {
		return byte[].class;
	}

	@Override
	public @NotNull Class<UUID> getComplexType() {
		return UUID.class;
	}

	@Override
	public byte @NotNull [] toPrimitive(@NotNull UUID complex, @NotNull PersistentDataAdapterContext context) {
		ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);

		byteBuffer.putLong(complex.getMostSignificantBits());
		byteBuffer.putLong(complex.getLeastSignificantBits());

		return byteBuffer.array();
	}

	@Override
	public @NotNull UUID fromPrimitive(byte @NotNull [] primitive, @NotNull PersistentDataAdapterContext context) {
		ByteBuffer byteBuffer = ByteBuffer.wrap(primitive);

		long mostSignificantBits = byteBuffer.getLong();
		long leastSignificantBits = byteBuffer.getLong();

		return new UUID(mostSignificantBits, leastSignificantBits);
	}
}

package dristmine.dristregions.creation;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class RegionOriginDataType implements PersistentDataType<int[], Vector> {

	@Override
	public @NotNull Class<int[]> getPrimitiveType() {
		return int[].class;
	}

	@Override
	public @NotNull Class<Vector> getComplexType() {
		return Vector.class;
	}

	@Override
	public int @NotNull [] toPrimitive(@NotNull Vector complex, @NotNull PersistentDataAdapterContext context) {
		return new int[] { complex.getBlockX(), complex.getBlockY(), complex.getBlockZ() };
	}

	@Override
	public @NotNull Vector fromPrimitive(int @NotNull [] primitive, @NotNull PersistentDataAdapterContext context) {
		return new Vector(primitive[0], primitive[1], primitive[2]);
	}
}

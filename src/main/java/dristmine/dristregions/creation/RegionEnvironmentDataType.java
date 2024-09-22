package dristmine.dristregions.creation;

import org.bukkit.World;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class RegionEnvironmentDataType implements PersistentDataType<Integer, World.Environment> {
	@Override
	public @NotNull Class<Integer> getPrimitiveType() {
		return Integer.class;
	}

	@Override
	public @NotNull Class<World.Environment> getComplexType() {
		return World.Environment.class;
	}

	@Override
	public @NotNull Integer toPrimitive(World.@NotNull Environment complex, @NotNull PersistentDataAdapterContext context) {
		int primitive;

		switch (complex) {
			case NORMAL -> primitive = 0;
			case NETHER -> primitive = 1;
			case THE_END -> primitive = 2;
			default -> primitive = -1;
		}

		return primitive;
	}

	@Override
	public @NotNull World.Environment fromPrimitive(@NotNull Integer primitive, @NotNull PersistentDataAdapterContext context) {
		World.Environment complex;

		switch (primitive) {
			case 0 -> complex = World.Environment.NORMAL;
			case 1 -> complex = World.Environment.NETHER;
			case 2 -> complex = World.Environment.THE_END;
			default -> complex = World.Environment.CUSTOM;
		}

		return complex;
	}
}

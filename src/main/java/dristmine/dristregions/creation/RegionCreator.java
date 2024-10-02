package dristmine.dristregions.creation;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import net.kyori.adventure.text.Component;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.IntStream;

public class RegionCreator {
	private static class CompassData {
		public ItemStack compassItem;
		public CompassMeta compassMetaData;
		public PersistentDataContainer compassContainer;

		public CompassData(ItemStack compassItem, CompassMeta compassMetaData, PersistentDataContainer compassContainer) {
			this.compassItem = compassItem;
			this.compassMetaData = compassMetaData;
			this.compassContainer = compassContainer;
		}
	}

	private static class RegionManagerData {
		public RegionContainer regionContainer;
		public RegionManager regionManager;

		public RegionManagerData(RegionContainer regionContainer, RegionManager regionManager) {
			this.regionContainer = regionContainer;
			this.regionManager = regionManager;
		}
	}

	private final NamespacedKey containerKey;

	private final Component compassDisplayName;

	private final List<? extends Component> compassLore;

	private final int regionRadius;

	public RegionCreator(
			NamespacedKey containerKey,
			Component compassDisplayName,
			List<? extends Component> compassLore, int regionRadius
	) {
		this.containerKey = containerKey;
		this.compassDisplayName = compassDisplayName;
		this.compassLore = compassLore;
		this.regionRadius = regionRadius;
	}

	public boolean create(PlayerInteractEvent event, UUID regionUUID) throws NullPointerException, StorageException {
		if (isInvalid(event))
			return false;

		Location regionOrigin = getRegionOriginFrom(event);
		BlockVector3 regionOriginVector3 = vector3From(regionOrigin);

		RegionManagerData regionManagerData = getRegionDataFrom(regionOrigin);
		Map<String, ProtectedRegion> idRegionMap = regionManagerData.regionManager.getRegions();
		List<ProtectedRegion> allProtectedRegions = idRegionMap.values().stream().toList();

		if (isAlreadyOccupied(regionOriginVector3, allProtectedRegions))
			return false;

		event.setCancelled(true);

		CompassData compassData = getCompassDataFrom(event);

		if (isInvalid(compassData, containerKey))
			return false;

		assignContainerTo(compassData, containerKey, regionUUID);
		assignTextTo(compassData, compassDisplayName, compassLore);
		assignLodestoneTo(compassData, regionOrigin);

		BlockVector3 point1 = calculatePointFrom(regionOriginVector3, regionRadius);
		BlockVector3 point2 = calculatePointFrom(regionOriginVector3, -regionRadius);
		ProtectedCuboidRegion region = createRegion(regionUUID, point1, point2);
		applyFlagsTo(region, new AbstractMap.SimpleEntry<>(Flags.BUILD, StateFlag.State.DENY));

		if (isInvalid(region, allProtectedRegions))
			return false;

		addRegionTo(regionManagerData, region);

		applyCompassMetadataTo(compassData);

		return true;
	}

	private static boolean isInvalid(PlayerInteractEvent event) {


		if (event.getClickedBlock() == null || event.getItem() == null)
			return true;

		return !(
			event.hasBlock() &&
			event.getItem().getType() == Material.COMPASS &&
			event.getClickedBlock().getType() == Material.LODESTONE
		);
	}

	private static boolean isAlreadyOccupied(BlockVector3 regionOrigin, List<ProtectedRegion> allRegions) {
		List<BlockVector3> minPoints = allRegions.stream()
				.map(ProtectedRegion::getMinimumPoint)
				.toList();
		List<BlockVector3> maxPoints = allRegions.stream()
				.map(ProtectedRegion::getMaximumPoint)
				.toList();

		if (minPoints.size() != maxPoints.size())
			throw new IllegalArgumentException("Points lists must be the same length: " + minPoints + " and " + maxPoints);

		List<BlockVector3> allOrigins = IntStream.range(0, minPoints.size())
				.mapToObj(i -> calculateVectorMean(minPoints.get(i), maxPoints.get(i)))
				.toList();

		for (BlockVector3 vector : allOrigins) {
			if (regionOrigin.divide(vector) == BlockVector3.ZERO) {
				return true;
			}
		}

		return false;
	}

	private static CompassData getCompassDataFrom(PlayerInteractEvent event) {
		ItemStack compassItem = event.getItem();
		CompassMeta compassMetaData = (CompassMeta) Objects.requireNonNull(compassItem).getItemMeta();
		PersistentDataContainer compassContainer = compassMetaData.getPersistentDataContainer();

		return new CompassData(compassItem, compassMetaData, compassContainer);
	}

	private static boolean isInvalid(CompassData compassData, NamespacedKey containerKey) {
		return !compassData.compassContainer.has(containerKey);
	}

	private static Location getRegionOriginFrom(PlayerInteractEvent event) {
		return Objects.requireNonNull(event.getClickedBlock()).getLocation();
	}

	private static void assignContainerTo(CompassData compassData, NamespacedKey containerKey, UUID regionKeyUUID) {
		compassData.compassContainer.set(containerKey, new UUIDDataType(), regionKeyUUID);
	}

	private static void assignTextTo(CompassData compassData, Component displayName, List<? extends  Component> lore) {
		compassData.compassMetaData.displayName(displayName);
		compassData.compassMetaData.lore(lore);
	}

	private static void assignLodestoneTo(CompassData compassData, Location regionOrigin) {
		compassData.compassMetaData.setLodestoneTracked(false);
		compassData.compassMetaData.setLodestone(regionOrigin);
	}

	private static void applyCompassMetadataTo(CompassData compassData) {
		compassData.compassItem.setItemMeta(compassData.compassMetaData);
	}

	private static RegionManagerData getRegionDataFrom(Location regionOrigin) {
		World adaptedWorld = BukkitAdapter.adapt(regionOrigin.getWorld());
		RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager regionManager = regionContainer.get(adaptedWorld);

		return new RegionManagerData(regionContainer, regionManager);
	}

	private static BlockVector3 vector3From(Location regionOrigin) {
		int x = regionOrigin.getBlockX();
		int y = regionOrigin.getBlockY();
		int z = regionOrigin.getBlockZ();

		return BlockVector3.at(x, y, z);
	}

	private static BlockVector3 calculatePointFrom(BlockVector3 regionOrigin, int radius) {
		return regionOrigin.add(radius, radius, radius);
	}

	private static ProtectedCuboidRegion createRegion(UUID regionUUID, BlockVector3 point1, BlockVector3 point2) {
		return new ProtectedCuboidRegion(regionUUID.toString(), point1, point2);
	}

	@SafeVarargs
	private static <T extends Flag<V>, V> void applyFlagsTo(ProtectedCuboidRegion region, AbstractMap.SimpleEntry<T, V>... flags) {
		for (AbstractMap.SimpleEntry<T, V> flag : flags) {
			region.setFlag(flag.getKey(), flag.getValue());
		}
	}

	private static boolean isInvalid(ProtectedCuboidRegion region, List<ProtectedRegion> allRegions) {
		List<ProtectedRegion> overlapping = region.getIntersectingRegions(allRegions);

		return overlapping.size() > 0;
	}

	private static void addRegionTo(RegionManagerData regionManagerData, ProtectedCuboidRegion region) throws StorageException {
		regionManagerData.regionManager.addRegion(region);
		regionManagerData.regionManager.save();
	}

	private static BlockVector3 calculateVectorMean(BlockVector3 point1, BlockVector3 point2) {
		BlockVector3 sum = point1.add(point2);

		return sum.divide(2);
	}
}

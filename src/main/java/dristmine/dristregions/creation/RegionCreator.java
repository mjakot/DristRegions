package dristmine.dristregions.creation;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
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
import java.util.Objects;
import java.util.UUID;

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

	private static class RegionData {
		public RegionContainer regionContainer;
		public RegionManager regionManager;

		public RegionData(RegionContainer regionContainer, RegionManager regionManager) {
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
		if (!verifyEvent(event))
			return false;

		event.setCancelled(true);

		CompassData compassData = getCompassDataFrom(event);

		if (!verifyCompass(compassData.compassContainer, containerKey))
			return false;

		Location regionOrigin = getRegionOriginFrom(event);

		assignContainerTo(compassData, containerKey, regionUUID);
		assignTextTo(compassData, compassDisplayName, compassLore);
		assignLodestoneTo(compassData, regionOrigin);
		applyCompassMetadataTo(compassData);

		BlockVector3 point1 = calculatePointFrom(regionOrigin, regionRadius);
		BlockVector3 point2 = calculatePointFrom(regionOrigin, -regionRadius);
		ProtectedCuboidRegion region = createRegion(regionUUID, point1, point2);
		applyFlagsTo(region, new AbstractMap.SimpleEntry<>(Flags.BUILD, StateFlag.State.DENY));

		RegionData regionData = getRegionDataFrom(regionOrigin);
		addRegionTo(regionData, region);

		return true;
	}

	private static boolean verifyEvent(PlayerInteractEvent event) {
		if (event.getClickedBlock() == null || event.getItem() == null)
			return false;

		return
			event.hasBlock() &&
			event.getItem().getType() == Material.COMPASS &&
			event.getClickedBlock().getType() == Material.LODESTONE;
	}

	private static CompassData getCompassDataFrom(PlayerInteractEvent event) {
		ItemStack compassItem = event.getItem();
		CompassMeta compassMetaData = (CompassMeta) Objects.requireNonNull(compassItem).getItemMeta();
		PersistentDataContainer compassContainer = compassMetaData.getPersistentDataContainer();

		return new CompassData(compassItem, compassMetaData, compassContainer);
	}

	private static boolean verifyCompass(PersistentDataContainer compassContainer, NamespacedKey containerKey) {
		return compassContainer.has(containerKey);
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

	private static BlockVector3 calculatePointFrom(Location regionOrigin, int radius) {
		int x = regionOrigin.getBlockX() + radius;
		int y = regionOrigin.getBlockY() + radius;
		int z = regionOrigin.getBlockZ() + radius;

		return BlockVector3.at(x, y, z);
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

	private static RegionData getRegionDataFrom(Location regionOrigin) {
		World adaptedWorld = BukkitAdapter.adapt(regionOrigin.getWorld());
		RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager regionManager = regionContainer.get(adaptedWorld);

		return new RegionData(regionContainer, regionManager);
	}

	private static void addRegionTo(RegionData regionData, ProtectedCuboidRegion region) throws StorageException {
		regionData.regionManager.addRegion(region);
		regionData.regionManager.save();
	}
}

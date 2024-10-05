package dristmine.dristregions.interaction;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import dev.triumphteam.gui.guis.Gui;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RegionUIProvider {
	private static class RegionManagerData {
		public RegionContainer regionContainer;
		public RegionManager regionManager;

		public RegionManagerData(RegionContainer regionContainer, RegionManager regionManager) {
			this.regionContainer = regionContainer;
			this.regionManager = regionManager;
		}
	}

	private final Gui gui;

	public RegionUIProvider(Gui gui) {
		this.gui = gui;
	}

	public boolean provide(PlayerInteractEvent event) {
		if (isInvalid(event))
			return false;

		Player player = event.getPlayer();
		LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
		Location playerLocation = player.getLocation();
		BlockVector3 playerVector = BukkitAdapter.asBlockVector(playerLocation);
		RegionManagerData regionManagerData = getRegionDataFrom(playerLocation);
		Map<String, ProtectedRegion> idRegionMap = regionManagerData.regionManager.getRegions();
		List<ProtectedRegion> allProtectedRegions = idRegionMap.values().stream().toList();
		Optional<ProtectedRegion> currentRegion = regionExistAt(playerVector, allProtectedRegions);

		if (currentRegion.isEmpty())
			return false;

		if (!isOwnerOf(currentRegion.get(), localPlayer))
			return false;

		gui.open(player);

		return true;
	}

	private static boolean isInvalid(PlayerInteractEvent event) {
		if (event.getItem() == null)
			return true;

		return event.getItem().getType() != Material.COMPASS;
	}

	private static RegionManagerData getRegionDataFrom(Location regionOrigin) {
		World adaptedWorld = BukkitAdapter.adapt(regionOrigin.getWorld());
		RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager regionManager = regionContainer.get(adaptedWorld);

		return new RegionManagerData(regionContainer, regionManager);
	}

	private Optional<ProtectedRegion> regionExistAt(BlockVector3 blockVector, List<ProtectedRegion> allRegions) {
		for (ProtectedRegion region : allRegions) {
			if (region.contains(blockVector))
				return Optional.of(region);
		}

		return Optional.empty();
	}

	private boolean isOwnerOf(ProtectedRegion region, LocalPlayer player) {
		return region.isOwner(player);
	}
}

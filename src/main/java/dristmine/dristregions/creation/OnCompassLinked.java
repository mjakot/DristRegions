package dristmine.dristregions.creation;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import dristmine.dristregions.DristRegions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.UUID;

public class OnCompassLinked implements Listener {
	private final JavaPlugin instance;

	public OnCompassLinked(DristRegions instance) {
		this.instance = instance;
	}

	@SuppressWarnings("ConstantConditions")
	@EventHandler
	public void onPlayerInteraction(PlayerInteractEvent event) throws ProtectedRegion.CircularInheritanceException {
		if (!event.hasItem())
			return;

		if (!event.hasBlock())
			return;

		if (
			event.getAction() != Action.RIGHT_CLICK_BLOCK ||
			event.getItem().getType() != Material.COMPASS ||
			event.getClickedBlock().getType() != Material.LODESTONE
		) {
			return;
		}

		event.setCancelled(true);

		ItemStack compassInHand = event.getItem();
		CompassMeta compassMeta = (CompassMeta) compassInHand.getItemMeta();
		PersistentDataContainer compassContainer = compassMeta.getPersistentDataContainer();
		NamespacedKey key = new NamespacedKey(instance, "drist-regions");      //TODO: config property

		if (compassContainer.has(key))
			return;

		Location regionOrigin = event.getClickedBlock().getLocation();

		UUID id = new UUID(0L, 0L);
		compassContainer.set(key, new UUIDDataType(), id);   //TODO: Add uuid system

		compassMeta.displayName(Component.text("Region access key"));             //TODO: config property
		compassMeta.lore(List.of(new TextComponent[] { Component.text("TEST"), Component.text("TEST222") })); //TODO: config property

		compassMeta.setLodestoneTracked(false);
		compassMeta.setLodestone(regionOrigin);


		int point1X = regionOrigin.getBlockX() - 5;
		int point1Y = regionOrigin.getBlockY() - 5;
		int point1Z = regionOrigin.getBlockZ() - 5;
		BlockVector3 point1 = BlockVector3.at(point1X, point1Y, point1Z);
		int point2X = regionOrigin.getBlockX() + 5;
		int point2Y = regionOrigin.getBlockY() + 5;
		int point2Z = regionOrigin.getBlockZ() + 5;
		BlockVector3 point2 = BlockVector3.at(point2X, point2Y, point2Z);
		ProtectedCuboidRegion region = new ProtectedCuboidRegion(id.toString(), point1, point2);
		DefaultDomain owner = region.getOwners();
		owner.addPlayer(event.getPlayer().getUniqueId());
		region.setParent(null);
		RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager regionManager = regionContainer.get(BukkitAdapter.adapt(regionOrigin.getWorld()));
		regionManager.addRegion(region);

		compassInHand.setItemMeta(compassMeta);
	}
}

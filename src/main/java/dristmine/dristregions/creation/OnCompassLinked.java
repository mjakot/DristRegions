package dristmine.dristregions.creation;

import dristmine.dristregions.DristRegions;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class OnCompassLinked implements Listener {
	private final JavaPlugin instance;

	public OnCompassLinked(DristRegions instance) {
		this.instance = instance;
	}

	@SuppressWarnings("ConstantConditions")
	@EventHandler
	public void onPlayerInteraction(PlayerInteractEvent event) {
		if (!event.hasItem())
			return;

		if (                                                            // TODO: this might need some optimization in the future
				event.getAction() != Action.RIGHT_CLICK_BLOCK &&
				event.getItem().getType() != Material.COMPASS &&
				event.getClickedBlock().getType() != Material.LODESTONE
		)
			return;

		event.setCancelled(true);

		NamespacedKey regionCompassKey = new NamespacedKey(instance, "drist-regions"); // FIXME: define this key in the plugin config file

		ItemStack linkedCompass = event.getItem();
		ItemMeta compassMeta = linkedCompass.getItemMeta();
		PersistentDataContainer container = compassMeta.getPersistentDataContainer();

		Location clickedBlockLocation = event.getClickedBlock().getLocation();

		if (!container.has(regionCompassKey, new RegionOriginDataType())) {
			Vector clickedBlockVector = new Vector(clickedBlockLocation.getBlockX(), clickedBlockLocation.getBlockY(), clickedBlockLocation.getBlockY());

			container.set(regionCompassKey, new RegionOriginDataType(), clickedBlockVector); // FIXME: implement some kind of unique id system for region compasses
		}

		if (!container.has(regionCompassKey, new RegionEnvironmentDataType())) {
			World.Environment clickedBlockEnvironment = clickedBlockLocation.getWorld().getEnvironment();

			container.set(regionCompassKey, new RegionEnvironmentDataType(), clickedBlockEnvironment);
		}

		linkedCompass.setItemMeta(compassMeta);
	}
}

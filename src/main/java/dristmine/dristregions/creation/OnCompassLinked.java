package dristmine.dristregions.creation;

import dristmine.dristregions.DristRegions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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
	public void onPlayerInteraction(PlayerInteractEvent event) {
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

		compassContainer.set(key, new UUIDDataType(), new UUID(0L, 0L));   //TODO: Add uuid system

		compassMeta.displayName(Component.text("Region access key"));             //TODO: config property
		compassMeta.lore(List.of(new TextComponent[] { Component.text("TEST"), Component.text("TEST222") })); //TODO: config property

		compassMeta.setLodestoneTracked(false);
		compassMeta.setLodestone(regionOrigin);

		compassInHand.setItemMeta(compassMeta);
	}
}

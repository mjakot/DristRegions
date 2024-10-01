package dristmine.dristregions.creation;

import com.sk89q.worldguard.protection.managers.storage.StorageException;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.UUID;

public class OnPlayerInteract implements Listener {
	private final RegionCreator regionCreator;

	public OnPlayerInteract(RegionCreator regionCreator) {
		this.regionCreator = regionCreator;
	}

	@EventHandler
	public void onPlayerInteraction(PlayerInteractEvent event) throws NullPointerException, StorageException {
		if (!event.hasItem())
			return;

		switch (event.getAction()) {
			case RIGHT_CLICK_BLOCK -> {
				regionCreator.create(event, new UUID(0, 0)); //TODO: Add uuid system
			}
		}
	}
}

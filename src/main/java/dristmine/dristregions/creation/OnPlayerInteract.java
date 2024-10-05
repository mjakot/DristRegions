package dristmine.dristregions.creation;

import com.sk89q.worldguard.protection.managers.storage.StorageException;

import dristmine.dristregions.UUIDGenerator;
import dristmine.dristregions.interaction.RegionUIProvider;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class OnPlayerInteract implements Listener {
	private final RegionCreator regionCreator;
	private final RegionUIProvider regionUIProvider;
	private final UUIDGenerator uuidGenerator;

	public OnPlayerInteract(RegionCreator regionCreator, RegionUIProvider regionUIProvider, UUIDGenerator uuidGenerator) {
		this.regionCreator = regionCreator;
		this.regionUIProvider = regionUIProvider;
		this.uuidGenerator = uuidGenerator;
	}

	@EventHandler
	public void onPlayerInteraction(PlayerInteractEvent event) throws NullPointerException, StorageException, UUIDGenerator.UUIDOverflowException {
		if (!event.hasItem())
			return;

		switch (event.getAction()) {
			case RIGHT_CLICK_BLOCK:
				if (!regionCreator.create(event, uuidGenerator.next())) {
					uuidGenerator.previous();

					break;
				}
			case RIGHT_CLICK_AIR:
				regionUIProvider.provide(event);
				
				break;
		}
	}
}

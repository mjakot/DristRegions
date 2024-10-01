package dristmine.dristregions;

import dristmine.dristregions.config.ConfigManager;
import dristmine.dristregions.creation.OnPlayerInteract;
import dristmine.dristregions.creation.RegionCreator;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public final class DristRegions extends JavaPlugin {

	@Override
	public void onEnable() {
		getLogger().info("        ");
		getLogger().info("       ^");
		getLogger().info("       |");
		getLogger().info("  <----+---->");
		getLogger().info("       |");
		getLogger().info("       v");
		getLogger().info("        ");

		ConfigManager configManager = new ConfigManager(this);

		UUIDGenerator uuidGenerator = null;
		try {
			uuidGenerator = new UUIDGenerator(configManager.getString("last_uuid"));
		}
		catch (UUIDGenerator.UUIDOverflowException e) {
			getLogger().severe(e.getMessage());
			getLogger().severe("Disabling DristRegions...");
			getServer().getPluginManager().disablePlugin(this);
		}

		NamespacedKey compassContainerKey = new NamespacedKey(this, configManager.getString("compass_containers_namespace"));

		Component compassDisplayName = Component.text(configManager.getString("compass_display_name"));
		List<Component> compassLore = configManager.getStringList("compass_lore")
			.stream()
			.map(Component::text)
			.collect(Collectors.toList());

		int regionRadius = configManager.getInteger("region_radius");

		RegionCreator regionCreator = new RegionCreator(compassContainerKey, compassDisplayName, compassLore, regionRadius);


		register(new OnPlayerInteract(regionCreator, uuidGenerator));
	}

	@Override
	public void onDisable() {
		getLogger().info("        ");
		getLogger().info("       ^");
		getLogger().info("       |");
		getLogger().info("  <----+---->");
		getLogger().info("       |");
		getLogger().info("       v");
		getLogger().info("        ");
	}

	private void register(Listener... events) {
		for (Listener event : events) {
			getServer().getPluginManager().registerEvents(event, this);
		}
	}
}

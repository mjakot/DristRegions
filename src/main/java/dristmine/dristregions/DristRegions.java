package dristmine.dristregions;

import dristmine.dristregions.creation.OnCompassLinked;
import dristmine.dristregions.creation.RegionCreator;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

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

		NamespacedKey compassContainerKey = new NamespacedKey(this, "drist-regions"); //TODO: from config

		Component compassDisplayName = Component.text("Hii"); //TODO: from config
		List<Component> compassLore = List.of(Component.text("HII")); //TODO: from config

		RegionCreator regionCreator = new RegionCreator(compassContainerKey, compassDisplayName, compassLore, 5); //TODO: from config


		register(new OnCompassLinked(regionCreator));
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

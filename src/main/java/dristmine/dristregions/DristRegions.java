package dristmine.dristregions;

import dristmine.dristregions.creation.OnCompassLinked;
import org.bukkit.plugin.java.JavaPlugin;

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

		getServer().getPluginManager().registerEvents(new OnCompassLinked(this), this);
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
}

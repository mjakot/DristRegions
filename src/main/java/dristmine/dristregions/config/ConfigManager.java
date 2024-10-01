package dristmine.dristregions.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class ConfigManager {
	private final FileConfiguration configuration;

	public ConfigManager(JavaPlugin instance) {
		this.configuration = instance.getConfig();

		instance.saveDefaultConfig();
	}

	public String getString(String key) throws NullPointerException {
		if (isInvalid(key))
			return "";

		return configuration.getString(key);
	}

	public List<String> getStringList(String key) throws NullPointerException {
		if (isInvalid(key))
			return List.of();

		return configuration.getStringList(key);
	}

	public int getInteger(String key) throws NullPointerException {
		if (isInvalid(key))
			return 0;

		return configuration.getInt(key);
	}

	private static boolean isInvalid(String key) throws NullPointerException {
		if (key == null)
			throw new NullPointerException();
		return key.isEmpty();
	}
}

package me.libraryaddictfan.Utilities;

import me.libraryaddictfan.ClansLegendaries;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigUtils {
	
	private static FileConfiguration config;
	private static ClansLegendaries main;
	
	public static void register(ClansLegendaries mainn) {
		config = mainn.getConfig();
		main = mainn;
	}
	
	public static void reload() {
		config = main.getConfig();
		
	}
	
	/*
	 * I know this is cringy, but I want to do it without casting and like a normal config call.
	 */
	
	public static <T> void setSection(ConfigSections section, T obj) {
		config.set(section.getConfigSection(), obj);
	}
	
	@Deprecated
	public static void setBooleanSection(ConfigSections section, boolean obj) {
		config.set(section.getConfigSection(), obj);
	}
	
	@Deprecated
	public static void setIntegerSection(ConfigSections section, int obj) {
		config.set(section.getConfigSection(), obj);
	}
	
	@Deprecated
	public static void setDoubleSection(ConfigSections section, double obj) {
		config.set(section.getConfigSection(), obj);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getSection(ConfigSections section) {
		return (T) config.get(section.getConfigSection(), (T) section.getDefaultValue());
	}
	
	@Deprecated
	public static boolean getBooleanSection(ConfigSections section) {
		return config.getBoolean(section.getConfigSection(), (boolean) section.getDefaultValue());
	}
	
	@Deprecated
	public static double getDoubleSection(ConfigSections section) {
		return config.getDouble(section.getConfigSection(), (double) section.getDefaultValue());
	}
	
	@Deprecated
	public static int getIntegerSection(ConfigSections section) {
		return config.getInt(section.getConfigSection(), (int) section.getDefaultValue());
	}
}

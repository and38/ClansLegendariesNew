package me.libraryaddictfan.Utilities;

public enum ConfigSections {
	WINDBLADE_INFINITE("windblade-infinite-charge", false),
	WINDBLADE_VELOCITY("windblade-velocity", 0.66),
	SCEPTER_TARGET_ENT("scepter-nonplayer-target", true),
	HYPERAXE_DAMAGE_DELAY("hyper-damage-delay", 7),
	HYPERAXE_DAMAGE("hyper-damage", 4),
	SCEPTER_DAMAGE("scepter-damage", 11);
	
	private String configSection;
	private Object defaultValue;
	
	private ConfigSections(String configSectionn, Object defaultValuee) {
		configSection = configSectionn;
		defaultValue = defaultValuee;
	}
	
	public String getConfigSection() {
		return configSection;
	}
	
	public Object getDefaultValue() {
		return defaultValue;
	}
	
}

package me.fallenbreath.tweakermore.config;

import fi.dy.masa.malilib.config.options.ConfigBooleanHotkeyed;
import fi.dy.masa.malilib.config.options.ConfigDouble;

public class TweakerMoreConfigs
{
	// Generic
	public static final ConfigDouble NETHER_PORTAL_SOUND_CHANCE = new ConfigDouble("netherPortalSoundChance", 0.01D, 0.0D, 0.01D, "The chance for a nether portal block to play sound\nSet it to 0.001 or 0.0001 for less noisy portal");

	// Disable
	public static final ConfigBooleanHotkeyed DISABLE_LIGHT_UPDATES = new ConfigBooleanHotkeyed("disableLightUpdates", false, "", "Yeets client-side light updates");
}

package me.fallenbreath.tweakermore.config.early_risers;

import com.chocohead.mm.api.ClassTinkerers;

public class TogglesEarlyRiser implements Runnable
{
	private static final String FeatureToggleClassPath = "fi.dy.masa.tweakeroo.config.FeatureToggle";

	@Override
	public void run()
	{
		// FeatureToggleClass(String name, boolean defaultValue, String defaultHotkey, String comment)
		ClassTinkerers.enumBuilder(FeatureToggleClassPath, String.class, boolean.class, String.class, String.class).
				addEnum("TWEAKM_AUTO_CLEAN_CONTAINER", "tweakmAutoCleanContainer", false, "", "Automatically drops everything in the opened container\nand then close the container if any item is dropped").
				build();
	}
}

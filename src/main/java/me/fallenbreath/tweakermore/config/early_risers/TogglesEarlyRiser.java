package me.fallenbreath.tweakermore.config.early_risers;

import com.chocohead.mm.api.ClassTinkerers;

public class TogglesEarlyRiser implements Runnable
{
	private static final String FeatureToggleClassPath = "fi.dy.masa.tweakeroo.config.FeatureToggle";

	@Override
	public void run()
	{
		// private FeatureToggle(String name, boolean defaultValue, String defaultHotkey, String comment, String prettyName)
		ClassTinkerers.enumBuilder(FeatureToggleClassPath, String.class, boolean.class, String.class, String.class, String.class).
				addEnum("TWEAKM_AUTO_CLEAN_CONTAINER", "tweakmAutoCleanContainer", false, "", "tweakmAutoCleanContainer.comment", "Auto Clean Container").
				addEnum("TWEAKM_AUTO_FILL_CONTAINER", "tweakmAutoFillContainer", false, "", "tweakmAutoFillContainer.comment", "Auto Fill Container").
				addEnum("TWEAKM_AUTO_PICK_SCHEMATIC_BLOCK", "tweakmAutoPickSchematicBlock", false, "", "tweakmAutoPickSchematicBlock.comment", "Auto Pick Schematic Block").
				build();
	}
}

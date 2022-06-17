package me.fallenbreath.tweakermore.impl.features.tweakmSchematicProPlace;

import com.google.common.collect.Lists;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.config.options.TweakerMoreConfigBooleanHotkeyed;
import me.fallenbreath.tweakermore.gui.TweakerMoreConfigGui;
import me.fallenbreath.tweakermore.util.StringUtil;

import java.util.List;

public class ProPlaceImpl
{
	private static final List<TweakerMoreConfigBooleanHotkeyed> PRO_PLACE_CONFIGS = Lists.newArrayList(
			TweakerMoreConfigs.TWEAKM_AUTO_PICK_SCHEMATIC_BLOCK,
			TweakerMoreConfigs.TWEAKM_SCHEMATIC_BLOCK_PLACEMENT_RESTRICTION
	);

	public static void onConfigChanged(ConfigBoolean config)
	{
		final String value = String.valueOf(config.getBooleanValue());

		// setValueFromString doesn't trigger statistic calculation logic
		PRO_PLACE_CONFIGS.forEach(c -> c.setValueFromString(value));

		// redraw config gui to refresh config value change
		TweakerMoreConfigGui.getCurrentInstance().ifPresent(TweakerMoreConfigGui::reDraw);
	}

	public static String modifyComment(String comment)
	{
		String lines = StringUtil.configsToListLines(PRO_PLACE_CONFIGS);
		return comment.replaceFirst("##CONFIGS##", lines);
	}
}

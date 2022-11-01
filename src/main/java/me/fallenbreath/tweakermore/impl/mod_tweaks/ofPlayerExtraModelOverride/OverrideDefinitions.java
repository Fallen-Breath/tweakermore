package me.fallenbreath.tweakermore.impl.mod_tweaks.ofPlayerExtraModelOverride;

import com.google.common.collect.Lists;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;

import java.util.List;

public class OverrideDefinitions
{
	// TODO: when there are more than 1 overriders, try to make all of them work together -> try to merge those activated configs
	public static final List<OverrideDefinition> OVERRIDE_DEFS = Lists.newArrayList(
			OverrideDefinition.create(TweakerMoreConfigs.OF_WITCH_HAT).
					cfg("assets/tweakermore/tweak/ofPlayerExtraModel/hat_santa/config.json").
					model("assets/tweakermore/tweak/ofPlayerExtraModel/hat_santa/model.json").
					texture("assets/tweakermore/tweak/ofPlayerExtraModel/hat_santa/texture.png")
	);
}

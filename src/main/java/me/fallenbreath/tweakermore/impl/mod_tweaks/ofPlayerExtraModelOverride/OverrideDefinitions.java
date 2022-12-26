package me.fallenbreath.tweakermore.impl.mod_tweaks.ofPlayerExtraModelOverride;

import com.google.common.collect.Lists;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;

import java.util.List;

public class OverrideDefinitions
{
	private static final String RESOURCES_ROOT = "assets/tweakermore/tweak/ofPlayerExtraModel/";

	// TODO: when there are more than 1 overriders, try to make all of them work together -> try to merge those activated configs
	public static final List<OverrideDefinition> OVERRIDE_DEFS = Lists.newArrayList(
			OverrideDefinition.create(TweakerMoreConfigs.OF_WITCH_HAT).
					cfg(RESOURCES_ROOT + "hat_jingy/config.json").
					model(RESOURCES_ROOT + "hat_jingy/model.json").
					texture(RESOURCES_ROOT + "hat_jingy/texture.png"),
			OverrideDefinition.create(TweakerMoreConfigs.OF_SANTA_HAT).
					cfg(RESOURCES_ROOT + "hat_santa/config.json").
					model(RESOURCES_ROOT + "hat_santa/model.json").
					texture(RESOURCES_ROOT + "hat_santa/texture.png")
	);
}

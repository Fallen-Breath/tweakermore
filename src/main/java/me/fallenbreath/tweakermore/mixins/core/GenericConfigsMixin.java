package me.fallenbreath.tweakermore.mixins.core;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.tweakeroo.config.Configs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(Configs.Generic.class)
public abstract class GenericConfigsMixin
{
	@Mutable
	@Shadow(remap = false) @Final public static ImmutableList<IConfigBase> OPTIONS;

	static
	{
		List<IConfigBase> optionList = Lists.newArrayList(OPTIONS);
		optionList.add(me.fallenbreath.tweakermore.config.TweakerMoreConfigs.NETHER_PORTAL_SOUND_CHANCE);
		OPTIONS = ImmutableList.copyOf(optionList);
	}
}

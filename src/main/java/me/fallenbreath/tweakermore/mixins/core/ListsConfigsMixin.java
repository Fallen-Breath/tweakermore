package me.fallenbreath.tweakermore.mixins.core;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.tweakeroo.config.Configs;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.config.annotations.Config;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Configs.Lists.class)
public abstract class ListsConfigsMixin
{
	@Mutable
	@Shadow(remap = false) @Final public static ImmutableList<IConfigBase> OPTIONS;

	static
	{
		OPTIONS = TweakerMoreConfigs.updateOptionList(OPTIONS, Config.Type.LIST);
	}
}

package me.fallenbreath.tweakermore.mixins.core;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import fi.dy.masa.malilib.config.IHotkeyTogglable;
import fi.dy.masa.tweakeroo.config.Configs;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(Configs.Disable.class)
public abstract class DisableConfigsMixin
{
	@Mutable
	@Shadow(remap = false) @Final public static ImmutableList<IHotkeyTogglable> OPTIONS;

	static
	{
		List<IHotkeyTogglable> optionList = Lists.newArrayList(OPTIONS);
		optionList.add(TweakerMoreConfigs.DISABLE_LIGHT_UPDATES);
		OPTIONS = ImmutableList.copyOf(optionList);
	}
}

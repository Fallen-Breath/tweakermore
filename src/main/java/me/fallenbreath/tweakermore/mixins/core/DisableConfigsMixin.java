package me.fallenbreath.tweakermore.mixins.core;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.IHotkeyTogglable;
import fi.dy.masa.tweakeroo.config.Configs;
import me.fallenbreath.tweakermore.config.Config;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Configs.Disable.class)
public abstract class DisableConfigsMixin
{
	@Mutable
	@Shadow(remap = false) @Final public static ImmutableList<IHotkeyTogglable> OPTIONS;

	static
	{
		OPTIONS = TweakerMoreConfigs.updateOptionList(OPTIONS, Config.Type.DISABLE);
	}
}

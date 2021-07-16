package me.fallenbreath.tweakermore.mixins.core;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.tweakeroo.config.Configs;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(Configs.Lists.class)
public abstract class ListsConfigsMixin
{
	@Mutable
	@Shadow(remap = false) @Final public static ImmutableList<IConfigBase> OPTIONS;

	static
	{
		List<IConfigBase> optionList = Lists.newArrayList(OPTIONS);
		optionList.add(TweakerMoreConfigs.HAND_RESTORE_LIST_TYPE);
		optionList.add(TweakerMoreConfigs.HAND_RESTORE_BLACKLIST);
		optionList.add(TweakerMoreConfigs.HAND_RESTORE_WHITELIST);
		OPTIONS = ImmutableList.copyOf(optionList);
	}
}

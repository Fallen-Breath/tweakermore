package me.fallenbreath.tweakermore.mixins.core;

import fi.dy.masa.malilib.util.restrictions.UsageRestriction;
import fi.dy.masa.tweakeroo.config.Configs;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Configs.class)
public class ConfigsMixin
{
	@Inject(method = "loadFromFile", at = @At("TAIL"), remap = false)
	private static void buildRestrictionLists(CallbackInfo ci)
	{

		TweakerMoreConfigs.HAND_RESTORE_RESTRICTION.setListType((UsageRestriction.ListType)TweakerMoreConfigs.HAND_RESTORE_LIST_TYPE.getOptionListValue());
		TweakerMoreConfigs.HAND_RESTORE_RESTRICTION.setListContents(TweakerMoreConfigs.HAND_RESTORE_BLACKLIST.getStrings(), TweakerMoreConfigs.HAND_RESTORE_WHITELIST.getStrings());
	}
}

package me.fallenbreath.tweakermore.mixins.core.gui;

import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.options.ConfigBase;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//#if MC >= 11800
//$$ import org.spongepowered.asm.mixin.Shadow;
//$$ import org.spongepowered.asm.mixin.injection.ModifyArg;
//#endif

import java.util.List;

@Mixin(ConfigBase.class)
public abstract class ConfigBaseMixin
{
	//#if MC >= 11800
	//$$ @Shadow(remap = false) private String comment;
 //$$
	//$$ @ModifyArg(
	//$$ 		method = "getComment",
	//$$ 		at = @At(
	//$$ 				value = "INVOKE",
	//$$ 				target = "Lfi/dy/masa/malilib/util/StringUtils;getTranslatedOrFallback(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"
	//$$ 		),
	//$$ 		index = 0,
	//$$ 		remap = false
	//$$ )
	//$$ private String tweakerMoreConfigCommentIsTheTranslationKey(String key)
	//$$ {
	//$$ 	if (TweakerMoreConfigs.hasConfig((IConfigBase)this))
	//$$ 	{
	//$$ 		key = this.comment;
	//$$ 	}
	//$$ 	return key;
	//$$ }
	//#endif

	@Inject(method = "getComment", at = @At("TAIL"), cancellable = true, remap = false)
	private void appendModRequirementHeader(CallbackInfoReturnable<String> cir)
	{
		TweakerMoreConfigs.getOptionFromConfig((IConfigBase)this).ifPresent(tweakerMoreOption -> {
			cir.setReturnValue(tweakerMoreOption.modifyComment(cir.getReturnValue()));
		});
	}
}

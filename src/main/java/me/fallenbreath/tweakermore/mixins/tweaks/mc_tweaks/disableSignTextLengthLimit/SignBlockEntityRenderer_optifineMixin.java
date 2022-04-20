package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableSignTextLengthLimit;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

//#if MC < 11500
//$$ import me.fallenbreath.tweakermore.impl.disableSignTextLengthLimit.SignOverflowHintDrawer;
//$$ import net.minecraft.block.BlockState;
//$$ import net.minecraft.block.entity.SignBlockEntity;
//$$ import net.minecraft.client.font.TextRenderer;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//$$ import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
//#endif

/**
 * Only used in mc1.14.4
 */
@Restriction(require = {
		@Condition(ModIds.optifine),
		@Condition(value = ModIds.minecraft, versionPredicates = "<1.15")
})
@Mixin(SignBlockEntityRenderer.class)
public abstract class SignBlockEntityRenderer_optifineMixin
{
	@SuppressWarnings({"UnresolvedMixinReference", "MixinAnnotationTarget"})
	@ModifyArg(
			method = "lambda$render$0",  // lambda method in method render after being polluted by optifine
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/util/Texts;wrapLines(Lnet/minecraft/text/Text;ILnet/minecraft/client/font/TextRenderer;ZZ)Ljava/util/List;",
					remap = true
			),
			remap = false
	)
	private static int disableSignTextLengthLimit(int maxLength)
	{
		if (TweakerMoreConfigs.DISABLE_SIGN_TEXT_LENGTH_LIMIT.getBooleanValue())
		{
			maxLength = Integer.MAX_VALUE;
		}
		return maxLength;
	}

	//#if MC < 11500
	//$$ @Inject(
	//$$ 		method = "render(Lnet/minecraft/block/entity/SignBlockEntity;DDDFI)V",
	//$$ 		at = @At(
	//$$ 				value = "INVOKE",
	//$$ 				target = "Lnet/minecraft/client/font/TextRenderer;draw(Ljava/lang/String;FFI)I",
	//$$ 				ordinal = 0
	//$$ 		),
	//$$ 		locals = LocalCapture.CAPTURE_FAILSOFT
	//$$ )
	//$$ private void drawLineOverflowHint(SignBlockEntity signBlockEntity, double xOffset, double yOffset, double zOffset, float tickDelta, int blockBreakStage, CallbackInfo ci, BlockState blockstate, float f, TextRenderer textRenderer, float f1, int i, int lineIdx, String lineContent)
	//$$ {
	//$$ 	SignOverflowHintDrawer.drawLineOverflowHint(signBlockEntity, textRenderer, lineIdx, lineContent);
	//$$ }
	//#endif
}
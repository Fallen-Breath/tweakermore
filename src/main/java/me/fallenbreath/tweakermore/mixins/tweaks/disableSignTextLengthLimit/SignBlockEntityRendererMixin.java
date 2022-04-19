package me.fallenbreath.tweakermore.mixins.tweaks.disableSignTextLengthLimit;

//#if MC < 11500
//$$ import me.fallenbreath.conditionalmixin.api.annotation.Condition;
//$$ import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
//$$ import me.fallenbreath.tweakermore.impl.disableSignTextLengthLimit.SignOverflowHintDrawer;
//$$ import me.fallenbreath.tweakermore.util.ModIds;
//$$ import net.minecraft.block.entity.SignBlockEntity;
//$$ import net.minecraft.client.font.TextRenderer;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//$$ import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
//#endif
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.ModifyArg;

//#if MC < 11500
//$$ @Restriction(conflict = @Condition(ModIds.optifine))
//#endif
@Mixin(SignBlockEntityRenderer.class)
public abstract class SignBlockEntityRendererMixin
{
	@SuppressWarnings("UnresolvedMixinReference")
	@Group(min = 1, max = 1)
	@ModifyArg(
			method = {  // lambda method in method render
					"method_3583",  // vanilla
					"lambda$render$0"  // after being polluted by optifine
			},
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
	//$$ 		locals = LocalCapture.CAPTURE_FAILHARD
	//$$ )
	//$$ private void drawLineOverflowHint(SignBlockEntity signBlockEntity, double xOffset, double yOffset, double zOffset, float tickDelta, int blockBreakStage, CallbackInfo ci, TextRenderer textRenderer, float j, int signColor, int lineIdx, String lineContent)
	//$$ {
	//$$ 	SignOverflowHintDrawer.drawLineOverflowHint(signBlockEntity, textRenderer, lineIdx, lineContent);
	//$$ }
	//#endif
}

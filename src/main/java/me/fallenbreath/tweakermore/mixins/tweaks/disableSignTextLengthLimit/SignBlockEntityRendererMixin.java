package me.fallenbreath.tweakermore.mixins.tweaks.disableSignTextLengthLimit;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.util.Texts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

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

	@Inject(
			method = "render(Lnet/minecraft/block/entity/SignBlockEntity;DDDFI)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/font/TextRenderer;draw(Ljava/lang/String;FFI)I",
					ordinal = 0
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void drawLineOverflowHint(SignBlockEntity signBlockEntity, double xOffset, double yOffset, double zOffset, float tickDelta, int blockBreakStage, CallbackInfo ci, TextRenderer textRenderer, float j, int signColor, int lineIdx, String string)
	{
		if (TweakerMoreConfigs.DISABLE_SIGN_TEXT_LENGTH_LIMIT.getBooleanValue())
		{
			// make sure it's rendered during SignEditScreen rendering
			if (signBlockEntity.getCurrentRow() == -1)
			{
				return;
			}

			Text[] texts = signBlockEntity.text;
			if (0 <= lineIdx && lineIdx < texts.length)
			{
				boolean overflowed = Texts.wrapLines(texts[lineIdx], 90, textRenderer, false, true).size() > 1;
				if (overflowed)
				{
					assert Formatting.RED.getColorValue() != null;
					float xStart = (float)(-textRenderer.getStringWidth(string) / 2);
					textRenderer.draw("!", xStart - 10, lineIdx * 10 - texts.length * 5, Formatting.RED.getColorValue());
				}
			}
		}
	}
}

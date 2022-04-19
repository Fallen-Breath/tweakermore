package me.fallenbreath.tweakermore.mixins.tweaks.disableSignTextLengthLimit;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.util.SelectionManager;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

//#if MC >= 11500
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.Texts;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
//#endif

@Mixin(SignEditScreen.class)
public abstract class SignEditScreenMixin extends Screen
{
	@Shadow private SelectionManager selectionManager;

	@Shadow @Final private SignBlockEntity sign;

	protected SignEditScreenMixin(Text title)
	{
		super(title);
	}

	@ModifyArg(
			method = "init",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/util/SelectionManager;<init>(Lnet/minecraft/client/MinecraftClient;Ljava/util/function/Supplier;Ljava/util/function/Consumer;I)V"
			)
	)
	private int disableSignTextLengthLimitInSignEditor(int maxLength)
	{
		if (TweakerMoreConfigs.DISABLE_SIGN_TEXT_LENGTH_LIMIT.getBooleanValue())
		{
			maxLength = Integer.MAX_VALUE;
		}
		return maxLength;
	}

	//#if MC >= 11500
	@ModifyArg(
			method = "method_23773",  // lambda method in method render
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/util/Texts;wrapLines(Lnet/minecraft/text/Text;ILnet/minecraft/client/font/TextRenderer;ZZ)Ljava/util/List;",
					remap = true
			),
			remap = false
	)
	private int disableSignTextLengthLimitInSignEditScreenRendering(int maxLength)
	{
		if (TweakerMoreConfigs.DISABLE_SIGN_TEXT_LENGTH_LIMIT.getBooleanValue())
		{
			// should be modified into Integer.MAX_VALUE too in the @ModifyArg above
			maxLength = ((SelectionManagerAccessor)this.selectionManager).getMaxLength();
		}
		return maxLength;
	}

	@Inject(
			method = "render",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/font/TextRenderer;draw(Ljava/lang/String;FFIZLnet/minecraft/client/util/math/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZII)I",
					ordinal = 0
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void drawLineOverflowHint(int mouseX, int mouseY, float delta, CallbackInfo ci, MatrixStack matrixStack, float f, BlockState blockState, boolean bl, boolean bl2, float g, VertexConsumerProvider.Immediate immediate, float h, int i, String strings[], Matrix4f matrix4f, int k, int l, int m, int n, int lineIdx, String string, float xStart)
	{
		if (TweakerMoreConfigs.DISABLE_SIGN_TEXT_LENGTH_LIMIT.getBooleanValue())
		{
			Text[] texts = this.sign.text;
			if (0 <= lineIdx && lineIdx < texts.length)
			{
				assert this.minecraft != null;
				boolean overflowed = Texts.wrapLines(texts[lineIdx], 90, this.minecraft.textRenderer, false, true).size() > 1;
				if (overflowed)
				{
					assert Formatting.RED.getColorValue() != null;
					this.minecraft.textRenderer.draw("!", xStart - 10, lineIdx * 10 - texts.length * 5, Formatting.RED.getColorValue(), false, matrix4f, immediate, false, 0, 15728880);
				}
			}
		}
	}
	//#endif
}
